package com.PLATO.Threads;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.PLATO.constants.GlobalConstants;

public class SonarToolThread implements Callable<String>
{
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	private int buildHistoryId;
	private String sonarKey;
	private String reportPath;
	private static final Logger logger=Logger.getLogger(SonarToolThread.class);
	public SonarToolThread(String toolName,String jobName,int nextBuildNumber,int buildHistoryId,String sonarKey,String reportPath)
	{
		logger.info("Inside FAST thread constructor");
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
		this.buildHistoryId=buildHistoryId;
		this.sonarKey=sonarKey;
		this.reportPath=reportPath;
	}

	@Override
	public String call()
	{
		HttpURLConnection con=null;
		InputStreamReader reader=null;
		logger.info("Inside Sonar thread");
		try
		{
			logger.debug("SonarToolThread :"+threadName);
			logger.info("Sending get request");
			reportPath=URLEncoder.encode(reportPath,"UTF-8");
			String jenkinsURL=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;
			String read_sonar_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/sonarReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc"+"/"+sonarKey+"/?reportPath="+reportPath;
			//String read_sonar_console="http://localhost:7080/PlatoBuildMongoDBService/PlatoMongoTemplate/sonarReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc"+"/"+sonarKey+"/?reportPath="+reportPath;
			logger.debug("URL is :"+read_sonar_console);
			URL url = new URL(read_sonar_console);
			con = (HttpURLConnection) url.openConnection();
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
			reader = new InputStreamReader( con.getInputStream() );
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
			e.printStackTrace();
			logger.error("Exception in SonarToolThread for "+threadName+" : "+e);
			logger.error("Returning failure");
			return "Failed :"+threadName;
		}
		finally
		{	
			//close the open connections and streams in finally
			try
			{
				logger.info("Inside finally... Closing the connections");
				if(con!=null)
				{
					logger.info("Closing http connection");
					con.disconnect();
				}
				if(reader!=null)
				{
					logger.info("Closing input stream reader");
					reader.close();
				}
			}
			catch(Exception e)
			{
				logger.error("Error while closing connections");
				e.printStackTrace();
			}
		}
	}

}
