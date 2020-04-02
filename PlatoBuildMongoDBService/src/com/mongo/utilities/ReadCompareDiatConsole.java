package com.mongo.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.mongo.constants.GlobalConstants;

public class ReadCompareDiatConsole {
	private static final Logger logger=Logger.getLogger( ReadCompareDiatConsole.class);

	private String toolName;
	public String readJenkinsConsole(int buildNumber, String jobName,String jenkinsUrl) throws InterruptedException{
		logger.debug("in readJenkinsConsole");
		String jenkinsConsoleOutput = null;
		jenkinsUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;//"http://172.25.14.111:8080";
		String resp="";		
		try {			
			//url=url+"/job/"+platoJsonTemplate.jobName+"/"+platoJsonTemplate.buildNumber+"/consoleText";			
			System.out.println("jenkis url is "+jenkinsUrl);
			String url=jenkinsUrl+"/"+"jenkins/job/"+jobName+"/"+buildNumber+"/consoleText";
			System.out.println("jenkins url"+url);
			
			logger.debug("Reading Jenkins Console for Build Number: "+buildNumber+" and Job Name: "+jobName+" on Jenkins Url:  "+jenkinsUrl);

			resp=readJenkinsResponse(url);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		jenkinsConsoleOutput=resp;
		//readJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
		//Thread.sleep(20000);
		return jenkinsConsoleOutput;

	}
	
	//common method
	private String readJenkinsResponse(String url) throws IOException {
		logger.debug("in readJenkinsResponse");
		URL object=null;
		//JSONObject msgobj=new JSONObject(jsonData);	
		try {
			object = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("GET");
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		
		//used for create account POST request
		//wr.write(jsonData);
		logger.info("Connected to jenkins");
		//System.out.println("connected to jenkins");
		wr.flush();
		
		//display what returns the POST request
		StringBuilder sb = new StringBuilder();  
		int HttpResult = con.getResponseCode(); 
		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;  
			while ((line = br.readLine()) != null) {  
				sb.append(line + "\n");  
			}
			br.close();
			System.out.println("Received Response :" + sb.toString());  
		} else {
			System.out.println("Response is :"+con.getResponseMessage()+" : "+con.getRequestMethod()+ ": "+con.getHeaderFields());  
		}  
		
		return sb.toString();
	}
}
