package com.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.mongo.constants.GlobalConstants;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
//this class will be used for cucumber framework of T24
public class SeleniumFramework {
	public JSONArray getReports(String reportPath,String jobName,int nextBuildNumber,String jenkinsConsoleOutput) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception {
		System.out.println("inside selenium framework adapter");
		System.out.println("jenkinsConsoleOutput is "+jenkinsConsoleOutput);
		File htmlFile=new File(reportPath+"/Execution_report.html");
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(htmlFile.toString()));
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
		try {
			html=Jsoup.connect(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/"+htmlFile.getName()).get();
		}catch(Exception e) {
			e.printStackTrace();
		}
		int passCount=0;
		int failCount=0;
		int otherCount=0;
		String[] jenkinsConsoleArr= jenkinsConsoleOutput.split("\n");
		for(int i=0;i<jenkinsConsoleArr.length;i++) {
			String line=jenkinsConsoleArr[i];
			if(line.contains("Total Executed Test Cases")) {
				String[] lineSplit=line.split("=");
				String countString=lineSplit[1].trim();
			}else if(line.contains("Total Passed Test Cases")) {
				String[] lineSplit=line.split("=");
				String countString=lineSplit[1].trim();
				passCount=Integer.parseInt(countString);
			}else if(line.contains("Total Failed Test Cases")) {
				String[] lineSplit=line.split("=");
				String countString=lineSplit[1].trim();
				failCount=Integer.parseInt(countString);
			}
		}
//		System.out.println("content is\n "+content);
//		Element passCountElement=html.body().getElementsByAttributeValue("class","t-pass-count weight-normal").get(0);
//		Element failCountElement=html.body().getElementsByAttributeValue("class","t-fail-count weight-normal").get(0);
//		Element othersCountElement=html.getElementsByAttributeValue("class","t-others-count weight-normal").get(0);
//		//Element passCountElement=html.body().getElementsByClass("t-pass-count weight-normal").get(0);
////		Element failCountElement=html.body().getElementsByClass("t-fail-count weight-normal").get(0);
////		Element othersCountElement=html.getElementsByClass("t-others-count weight-normal").get(0);
//		try {
//			passCount=Integer.parseInt(passCountElement.text());
//			failCount=Integer.parseInt(failCountElement.text());
//			otherCount=Integer.parseInt(othersCountElement.text());
//		}catch(NumberFormatException e) {
//			e.printStackTrace();
//		}
		JSONArray chartLabels=new JSONArray();
		JSONArray chartValues=new JSONArray();
		JSONArray tabularData=new JSONArray();
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
		String htmlReportPath=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
		htmlReportPath=htmlReportPath+htmlFile.getName();
		toolReportObject.put("documentName",htmlFile.getName());
		toolReportObject.put("documentUrl",htmlReportPath);
		toolReportObject.put("chart_name","seleniumAdapter");
		chartLabels.add("pass");
		chartLabels.add("fail");
		//chartLabels.add("no_run");
		chartValues.add(passCount);
		chartValues.add(failCount);
		//chartValues.add(otherCount);
		toolReportObject.put("chart_labels", chartLabels);
		toolReportObject.put("chart_values",chartValues);
		toolReportObject.put("tabular_data",tabularData );
		toolReport.add(toolReportObject);
		return toolReport;
	}

}
