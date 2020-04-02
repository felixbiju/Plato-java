package com.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 
 * @author 10643380(Rahul Bhardwaj)
 * @version 1.1
 * 
 */
import com.mongo.constants.GlobalConstants;

public class PerformanceCenterAdapter {

	private static final Logger logger=Logger.getLogger( PerformanceCenterAdapter.class);
	
	public JSONArray getPerformanceCenterReport(String reportPath,String jobName,int nextBuildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception{
		System.out.println("reportPath "+reportPath);
		File f=new File(reportPath);
		File htmlReport=getHtmlReport(reportPath);
		if(htmlReport==null) {
			throw new IOException();
		}
		File pdfReport=getPdfReport(reportPath);
		return readHtmlAndReturnJSONArray(htmlReport,pdfReport,reportPath,jobName,nextBuildNumber);
	}
	
	@SuppressWarnings({ "unchecked" })
	private JSONArray readHtmlAndReturnJSONArray(File htmlReport,File pdfReport,String reportPath,String jobName,int nextBuildNumber)throws Exception {
		JSONArray jsonArray=new JSONArray();
		JSONObject chartReport=new JSONObject();
		int totalPassedTransactions=0;
		int totalFailedTransactions=0;
		
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(htmlReport.toString()));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String content = contentBuilder.toString();
		Document html = Jsoup.parse(content);
		Element outerTable=html.body().getElementsByTag("table").get(0);
		Element outerTableBody=outerTable.getElementsByTag("tbody").get(0);
		Element innerTable=outerTableBody.getElementsByTag("table").get(0);
		Element innerTableBody=innerTable.getElementsByTag("tbody").get(0);
		for(int i=0;i<innerTableBody.getElementsByTag("tr").size();i++) {
			Element tr=innerTableBody.getElementsByTag("tr").get(i);
			for(int j=0;j<tr.getElementsByTag("td").size();j++) {
				Element td=tr.getElementsByTag("td").get(j);
				if(td.text().equals("Total Passed Transactions")) {
					String countString=tr.getElementsByTag("td").get(j+1).text();
					if(countString.equals("") || countString.isEmpty() || countString==null || countString.length()==0) {
						totalPassedTransactions=0;
					}else {
						totalPassedTransactions=Integer.parseInt(countString);
					}
					
				}else if(td.text().equals("Total Failed Transactions")) {
					String countString=tr.getElementsByTag("td").get(j+1).text();
					if(countString.equals("") || countString.isEmpty() || countString==null || countString.length()==0) {
						totalFailedTransactions=0;
					}else {
						totalFailedTransactions=Integer.parseInt(countString);
					}
				}				
			}
		}
		
		chartReport.put("chart_name", jobName);
		ArrayList<String> chartLabels=new ArrayList<String>();
		chartLabels.add("TotalPassedTransactions");
		chartLabels.add("TotalFailedTransactions");
		chartReport.put("chart_labels",chartLabels);
		ArrayList<Long> chartValues=new ArrayList<Long>();
		chartValues.add((long)totalPassedTransactions);
		chartValues.add((long)totalFailedTransactions);
		chartReport.put("chart_values",chartValues);
		JSONArray tabularDataRow=new JSONArray();
		JSONObject tabularDataRowField=new JSONObject();
		String link=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
		String pdfLink=link+pdfReport.getName();
		String htmlLink=link+htmlReport.getName();
//		tabularDataRowField.put("link",pdfLink);
//		tabularDataRow.add(tabularDataRowField);
//		tabularData.add(tabularDataRow);
		chartReport.put("HTML_Report",htmlLink);
		chartReport.put("PDF_Report", pdfLink);
//		chartReport.put("tabular_data",tabularData);
		jsonArray.add(chartReport);
		System.out.println("jsonArray  "+jsonArray);
		return jsonArray;
		
	}
	
	private File getHtmlReport(String reportPath) {
		File f=new File(reportPath);
		for(File file:f.listFiles()) {
			System.out.println("fileName "+file.getName());
			if(file.getName().contains(".html")) {
				return file;
			}
		}
		return null;
	}
	
	private File getPdfReport(String reportPath) {
		File f=new File(reportPath);
		for(File file:f.listFiles()) {
			if(file.getName().contains(".pdf")) {
				return file;
			}
		}
		return null;	
	}
}
