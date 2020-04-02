package com.PLATO.Threads;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.PLATO.constants.GlobalConstants;


public class MultiplePlatformToolThread implements Callable<String>
{
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	private int buildHistoryId;
	private static final Logger logger=Logger.getLogger(MultiplePlatformToolThread.class);
	public MultiplePlatformToolThread(String toolName,String jobName,int nextBuildNumber,int buildHistoryId)
	{
		logger.info("Inside FAST thread constructor");
		logger.debug("Received parameters toolName :"+toolName+" jobName :"+jobName+" nextBuildNumber : "+nextBuildNumber+" buildHistoryId :"+buildHistoryId);
		//assign job name to thread name
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
		this.buildHistoryId=buildHistoryId;
	}

	@Override
	public String call()
	{
		HttpURLConnection con=null;
		InputStreamReader reader=null;

		logger.info("Inside call method of FAST thread");
		try
		{
			logger.debug("FASTToolThread :"+threadName);

			logger.info("Sending get request");
			String read_fast_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/multiplePlatformReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc";
			//String read_fast_console="http://localhost:8080/PlatoBuildMongoDBService/PlatoMongoTemplate/multiplePlatformReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc";

			logger.debug("URL is :"+read_fast_console);
			URL url = new URL(read_fast_console);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("GET");
			
			// reading the response
			logger.info("Reading response returned by fastReportService");
			reader = new InputStreamReader( con.getInputStream() );
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[ 2048 ];
			int num;
			while ( -1 != (num=reader.read( cbuf )))
			{
				buf.append( cbuf, 0, num );
			}
			String result = buf.toString();
			//if result is empty
			if(result.trim().isEmpty())
			{
				logger.info("Empty response received from service to read FAST Jenkins console. So returning response : failed");
				logger.info("Returning Failed");
				return "Failed :"+threadName;
			}
			//return the response from console reading service to module thread. We are appending threadname to response.
			logger.debug( "\nResponse from server after POST:\n" + result );
			logger.info("Returning Result");
			result=result+" :"+threadName;
			return result;
			//return "Success :"+threadName;
		}
		catch(Exception e)
		{
			//return failed in case of exception
			logger.error("Exception in FASTToolThread for "+threadName+" : "+e);
			e.printStackTrace();
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
