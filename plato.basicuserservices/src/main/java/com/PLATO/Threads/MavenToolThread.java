package com.PLATO.Threads;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.PLATO.constants.GlobalConstants;

public class MavenToolThread implements Callable<String>
{
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	private int buildHistoryId;
	private static final Logger logger=Logger.getLogger(MavenToolThread.class);
	public MavenToolThread(String toolName,String jobName,int nextBuildNumber,int buildHistoryId)
	{
		logger.info("Inside Maven thread constructor");
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
		this.buildHistoryId=buildHistoryId;
	}

	@Override
	public String call()
	{
		logger.info("Inside Maven thread");
		try
		{
			logger.debug("MavenToolThread :"+threadName);
			logger.info("Sending get request for maven data ");
			//String jenkinsURL=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;
			String read_maven_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/mavenReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc";
			//String read_maven_console="http://localhost:7080/PlatoBuildMongoDBService/PlatoMongoTemplate/mavenReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc";
			
			
			//String read_fast_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/fastReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc";
			System.out.println("URL for maven thread is :"+read_maven_console);
			logger.debug("URL is :"+read_maven_console);
			URL url = new URL(read_maven_console);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("GET");
			//System.out.println("Connect timeout :"+con.getConnectTimeout());
			//con.setConnectTimeout(200000);
			//con.setReadTimeout(2000000);

		/*	OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write("Read_Console");
			wr.flush();
			wr.close();*/

			// reading the response
			InputStreamReader reader = new InputStreamReader( con.getInputStream() );
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[ 2048 ];
			int num;
			while ( -1 != (num=reader.read( cbuf )))
			{
				buf.append( cbuf, 0, num );
			}
			String result = buf.toString();
			logger.debug( "\nResponse from server after POST:\n" + result );
			logger.info("Returning Result");
			result=result+" :"+threadName;
			return result;
			//return "Success :"+threadName;
		}
		catch(Exception e)
		{
			logger.error("Exception in MavenToolThread for "+threadName+" : "+e);
			logger.error("Returning failure");
			return "Failed :"+threadName;
		}
	}

}
