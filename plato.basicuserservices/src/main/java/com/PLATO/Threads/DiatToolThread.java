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
public class DiatToolThread implements Callable<String>
{   //threadName is actually the subJobName
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	private int buildHistoryId;
	private String reportPath;
	private static final Logger logger=Logger.getLogger(DiatToolThread.class);
	public DiatToolThread(String toolName,String jobName,int nextBuildNumber,int buildHistoryId,String reportPath)
	{
		logger.info("Inside DiatTool thread constructor");
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
		this.buildHistoryId=buildHistoryId;
		this.reportPath=reportPath+"/"+nextBuildNumber+"/RecentReport.xlsx";
	}

	@Override
	public String call()
	{	
		HttpURLConnection con=null;
		InputStreamReader reader=null;
		logger.info("Inside Diat thread");
		try
		{
			logger.debug("DiatToolThread :"+threadName);
			logger.info("Sending get request");
			reportPath=URLEncoder.encode(reportPath,"UTF-8");
			String jenkinsURL=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;
			String read_diat_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/diatReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc"+"/?reportPath="+reportPath;
			//String read_diat_console="http://localhost:7080/PlatoBuildMongoDBService/PlatoMongoTemplate/diatReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc"+"/?reportPath="+reportPath;
			//String read_diat_console="http://localhost:7080/PlatoBuildMongoDBService/PlatoMongoTemplate/diatReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc"+"/?reportPath="+reportPath;
			logger.debug("URL is :"+read_diat_console);
			URL url = new URL(read_diat_console);
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
			logger.error("Exception in DiatToolThread for "+threadName+" : "+e);
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