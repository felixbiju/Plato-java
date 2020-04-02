package com.PLATO.Threads;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.PLATO.constants.GlobalConstants;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class BuildToolThread implements Callable<String> {
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	private int buildHistoryId;
	private String reportPath;
	private String commandToExecute;
	private static final Logger logger=Logger.getLogger(BuildToolThread.class);
	public BuildToolThread(	String toolName,String threadName,int nextBuildNumber,int buildHistoryId,
	String reportPath,String commandToExecute) {
		this.threadName=threadName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
		this.buildHistoryId=buildHistoryId;
		this.reportPath=reportPath;
		this.commandToExecute=commandToExecute;
	}
	
	@Override
	public String call() {
		HttpURLConnection con=null;
		InputStreamReader reader=null;
		logger.info("Inside Build thread");
		String[] reportPathArray=reportPath.split(",");
		System.out.println("reportPathArray size  is "+reportPathArray.length);
		for(int i=0;i<reportPathArray.length;i++) {
			System.out.println("reportPathArray["+i+"]="+reportPathArray[i]);
		}
		String[] commandToExecuteArray=commandToExecute.split(",");
		String result=new String();
		if(reportPathArray[1].equalsIgnoreCase("true")) {
			logger.debug("sonar is true");
			String sonarReportPath=reportPathArray[3];
			String sonarKey=reportPathArray[5];
			try {
				sonarReportPath=URLEncoder.encode(sonarReportPath,"UTF-8");
				String jenkinsURL=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;
				
				String encodedthreadName=threadName.replaceAll(" ", "%20");
				String read_sonar_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/sonarReportService/"+buildHistoryId+"/"+encodedthreadName+"/"+nextBuildNumber+"/avc"+"/"+sonarKey+"/?reportPath="+sonarReportPath;
				
				logger.debug("URL is :"+read_sonar_console);
				URL url = new URL(read_sonar_console);
				con = (HttpURLConnection) url.openConnection();
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "text/xml");
				con.setRequestProperty("Accept", "text/xml");
				con.setRequestMethod("GET");
				
				reader = new InputStreamReader( con.getInputStream() );
				StringBuilder buf = new StringBuilder();
				char[] cbuf = new char[ 2048 ];
				int num;
				while ( -1 != (num=reader.read( cbuf )))
				{
					buf.append( cbuf, 0, num );
				}
				result = buf.toString();
				logger.debug( "\nResponse from server after POST:\n" + result );
				logger.info("Returning Result");
				result=result+" :"+threadName+" ";
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(reportPathArray[7].equalsIgnoreCase("true")) {
			logger.debug("duckreek is true");
			String duckCreekUrl=reportPathArray[9];
			String reportPath=reportPathArray[11];
			String inputParameters=reportPathArray[13]; 
			try {
				threadName=URLEncoder.encode(threadName,"UTF-8");
				duckCreekUrl=URLEncoder.encode(duckCreekUrl,"UTF-8");
				reportPath=URLEncoder.encode(reportPath,"UTF-8");
				String readDuckCreekConsole=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/duckCreekReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc/?reportPath="+reportPath+"&duckCreekUrl="+duckCreekUrl;
				logger.debug("readDuckCreekConsole is "+readDuckCreekConsole);
				URL url = new URL(readDuckCreekConsole);
				con = (HttpURLConnection) url.openConnection();
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "text/xml");
				con.setRequestProperty("Accept", "text/xml");
				con.setRequestMethod("GET");
				
				reader = new InputStreamReader( con.getInputStream() );
				StringBuilder buf = new StringBuilder();
				char[] cbuf = new char[ 2048 ];
				int num;
				while ( -1 != (num=reader.read( cbuf )))
				{
					buf.append( cbuf, 0, num );
				}
				result = result+buf.toString();
				result=result+" :"+threadName+" ";
				logger.debug( "\nResponse from server after POST:\n" + result );
				logger.info("Returning Result");				
			}catch(Exception e) {
				e.printStackTrace();
			}

		}
		
		String unitTestingTool=commandToExecuteArray[1];
		
		if(unitTestingTool.equalsIgnoreCase("testng")) {
			logger.debug("testNg ");
			
		}else if(unitTestingTool.equalsIgnoreCase("junit")) {
			logger.debug("junit ");
			String reportPathJUnit=GlobalConstants.JENKINS_HOME+"/workspace/"+threadName+"/"+nextBuildNumber;
			//String reportPathJUnit="C:/Users/"+System.getProperty("user.name")+"/.jenkins/workspace/"+threadName;
			String readJUnitConsole=new String();
			try {
				threadName=URLEncoder.encode(threadName,"UTF-8");
				reportPathJUnit=URLEncoder.encode(reportPathJUnit,"UTF-8");
				readJUnitConsole=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/genericCommandReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc/"+unitTestingTool+"/?reportPath="+reportPathJUnit;
				logger.debug("readJUnitConsole is "+readJUnitConsole);
				URL url = new URL(readJUnitConsole);
				con = (HttpURLConnection) url.openConnection();
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "text/xml");
				con.setRequestProperty("Accept", "text/xml");
				con.setRequestMethod("GET");
				
				reader = new InputStreamReader( con.getInputStream() );
				StringBuilder buf = new StringBuilder();
				char[] cbuf = new char[ 2048 ];
				int num;
				while ( -1 != (num=reader.read( cbuf )))
				{
					buf.append( cbuf, 0, num );
				}
				result = result+buf.toString();
				result=result+" :"+threadName+" ";
				logger.debug( "\nResponse from server after POST:\n" + result );
				logger.info("Returning Result");
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
}
