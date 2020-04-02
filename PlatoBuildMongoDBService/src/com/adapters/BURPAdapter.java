package com.adapters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mongo.constants.GlobalConstants;

/**
 * 
 * @author 10650463 (Harsh Mathur)
 * @version 2.0
 * 
 */

public class BURPAdapter {
	
	@SuppressWarnings("unchecked")
	public JSONArray getReport(String reportPath, String jobName, int buildNumber)throws Exception{

		String chart_low = "0";
		String chart_medium = "0";
		String chart_high = "0";
		String chart_informational = "0";
		
//		reportPath = reportPath + "test.html";
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + reportPath);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		
		File file1 = new File(reportPath);
		
		JSONArray ToolReport = new JSONArray();
		JSONObject report = new JSONObject();
		JSONArray chart_labels_array = new JSONArray();
		JSONArray chart_values_array = new JSONArray();		
		
		for(File file:file1.listFiles()) {
			if (!file.isDirectory() && (file.getName().contains(".html"))) {
				String htmlPath=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/";
				htmlPath=htmlPath+file.getName();
				report.put("chart_name", file.getName());
				report.put("chart_labels", chart_labels_array);
				
				report.put("HTML_Report", htmlPath);
				
				try {
					Document document = Jsoup.parse( file , "utf-8" );
					chart_high = document.select("span").get(4).text();
					chart_medium = document.select("span").get(8).text();
					chart_low = document.select("span").get(12).text();
					chart_informational = document.select("span").get(16).text();
					
					if(!chart_high.matches("-?\\d+(\\.\\d+)?")){
						chart_high = "0";
					}
					if(!chart_medium.matches("-?\\d+(\\.\\d+)?")){
						chart_medium = "0";
					}
					if(!chart_low.matches("-?\\d+(\\.\\d+)?")){
						chart_low = "0";
					}
					if(!chart_informational.matches("-?\\d+(\\.\\d+)?")){
						chart_informational = "0";
					}
					
					
					System.out.println(chart_high+" --- "+chart_medium+" ---  "+chart_low+" --- "+chart_informational);
										
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				chart_labels_array.add("low");
				chart_labels_array.add("medium");
				chart_labels_array.add("high");
				chart_labels_array.add("informational");
				
				chart_values_array.add(Integer.parseInt(chart_low));
				chart_values_array.add(Integer.parseInt(chart_medium));
				chart_values_array.add(Integer.parseInt(chart_high));
				chart_values_array.add(Integer.parseInt(chart_informational));
				
				report.put("chart_labels", chart_labels_array);
				report.put("chart_values",chart_values_array);
			}			
		}

		
		ToolReport.add(report);		
		return ToolReport;
	}
	

}
