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

public class ReadJenkinsConsole {
	
	private static final Logger logger=Logger.getLogger(ReadJenkinsConsole.class);

	private String toolName;
	public synchronized String readJenkinsConsole(int buildNumber, String jobName,String jenkinsUrl) throws InterruptedException{
		logger.debug("in readJenkinsConsole");
		System.out.println("in readJenkinsConsole");
		String jenkinsConsoleOutput = null;
		jenkinsUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;//"http://172.25.14.111:8080";
		System.out.println("jenkinsUrl "+jenkinsUrl);
		String resp="";	
		

		
		try {			
			//url=url+"/job/"+platoJsonTemplate.jobName+"/"+platoJsonTemplate.buildNumber+"/consoleText";			
			System.out.println("jobName before "+jobName);
			jobName=jobName.replaceAll(" ", "%20");
//			jobName=jobName.replaceAll("\\+", "%20");
			System.out.println("jobName "+jobName);
			String url=jenkinsUrl+"/"+"jenkins/job/"+jobName+"/"+buildNumber+"/consoleText";
			System.out.println("jenkins url"+url);
			
			logger.debug("Reading Jenkins Console for Build Number: "+buildNumber+" and Job Name: "+jobName+" on Jenkins Url:  "+url);
			
			resp=readJenkinsResponse(url);
			System.out.println("resp is "+resp);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		jenkinsConsoleOutput=resp;
		System.out.println("jenkinsConsoleOutput "+jenkinsConsoleOutput);
		//readJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
		Thread.sleep(2000);
		System.out.println("jenkinsConsoleOutput "+jenkinsConsoleOutput);
		return jenkinsConsoleOutput;

	}
	
	//common method
	private synchronized String readJenkinsResponse(String url) throws IOException {
		logger.debug("in readJenkinsResponse");
		System.out.println("in readJenkinsResponse");
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
		System.out.println("Connected to jenkins");
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
