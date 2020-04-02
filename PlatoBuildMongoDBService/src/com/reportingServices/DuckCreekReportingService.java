package com.reportingServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mongo.utilities.MongoDBOperations;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class DuckCreekReportingService {
	private static final Logger logger=Logger.getLogger(GenericCommandReportService.class);
	
	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status="Passed";
	private String duckCreekUrl;
	private String reportPath;
	private boolean lastCheckpoint=false;
	
	String jenkinsConsoleOutput="";
	public DuckCreekReportingService(int buildHistoryId, int buildNumber, String jobName,String duckCreekUrl,String reportPath) {
		// TODO Auto-generated constructor stub
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
		this.duckCreekUrl=duckCreekUrl;
		this.reportPath=reportPath;
	}
	public String readDuckCreekTotalResponse() {
		JSONParser parser=new JSONParser();
		String response="Failed";
		try {
			int warningCount=0;
			JSONObject finalPlatoJsonObj=new JSONObject();
			finalPlatoJsonObj=null;
			String finalPlatoJsonString=null;
			while(finalPlatoJsonString==null)
			{
				System.out.println("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				finalPlatoJsonString=MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
				
			
			}
			System.out.println("Retrived data : " +finalPlatoJsonString);
			finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
			//------------------------------------------end
			finalPlatoJsonObj.replace("readCount","1","0");
			JSONObject buildHistory= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
			JSONObject liveBuildConsole=(JSONObject) buildHistory.get("LiveBuildConsole");
			JSONArray liveBuildTools=(JSONArray)liveBuildConsole.get("tools");
			String status="";
			
			////////////here code for reading the duckcreek url will be written in the future
			
			String inputLine;
			URL url = new URL(duckCreekUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept", "text/simple");
			con.setRequestMethod("GET");
			logger.debug("Response Code : " + con.getResponseCode());
			if(con.getResponseCode()!=200)
			{
				logger.info("error in job");
				status="Failed";
				return response;
			}
			status="PASSED";
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuffer responseFromDuckCreekUrl = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				responseFromDuckCreekUrl.append(inputLine);
			}
			in.close();
			logger.debug("Response is :"+response.toString());
			String output=responseFromDuckCreekUrl.toString();
			JSONArray reportData=(JSONArray)parser.parse(output);
			for(int i=0;i<reportData.size();i++) {
				JSONObject obj=(JSONObject)reportData.get(i);
				String statusReport=(String)obj.get("Status");
				if(statusReport.equalsIgnoreCase("warning")) {
					warningCount++;
				}
			}
			////////////////////////
			
			for(int i=0;i<liveBuildTools.size();i++) {
				JSONObject temp=(JSONObject)liveBuildTools.get(i);
				if(temp.get("tool_name").equals(jobName)) {
					temp.put("tool_status",status);
				}
			}
			System.out.println("status is "+status);
			if(status.equalsIgnoreCase("PASSED")) {
				JSONObject build=(JSONObject)buildHistory.get("Build");
				JSONObject duckCreek=new JSONObject();
				duckCreek.put("warnings",warningCount);
				duckCreek.put("tabular_data", output);
				build.put("duckCreek_manuscript_analyzer",duckCreek);
			}
				
			response="Passed";
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
			
		}catch(Exception e) {
				e.printStackTrace();		
		}

		return response;
	}
}
