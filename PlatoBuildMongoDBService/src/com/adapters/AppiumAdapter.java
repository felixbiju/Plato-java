package com.adapters;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.mongo.constants.GlobalConstants;

public class AppiumAdapter {

	@SuppressWarnings("unchecked")
	public JSONArray getReport(String reportPath, String jobName, int buildNumber) {
		File htmlReport = getHtmlReport(reportPath);
		
		JSONArray reportJA = new JSONArray();
		JSONObject report = new JSONObject();
		
		String link=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/";
		String htmlLink=link+htmlReport.getName();
		try {
			report.put("HTML_", htmlLink);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		reportJA.add(report);
		return reportJA;
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
}
