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
public class GenericCommandToolThread implements Callable<String> {
	//threadName is the subJobName actually
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	private int buildHistoryId;
	private String reportPath;
	private static final Logger logger=Logger.getLogger(GenericCommandToolThread.class);
	public GenericCommandToolThread(String toolName,String jobName,int nextBuildNumber,int buildHistoryId,String reportPath) {
		logger.info("Inside GenericCommandTool thread constructor");
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
		this.buildHistoryId=buildHistoryId;
		if(!toolName.equalsIgnoreCase("iDiscover")) {
		reportPath=reportPath+nextBuildNumber+"/";
		}
		this.reportPath=reportPath;
		//now the reports are going to be stored in workspace+buldNo of the job,therefore the extra build number
	}
	public String call(){
		HttpURLConnection con=null;
		InputStreamReader reader=null;
		logger.info("Inside GenericCommandTool thread");
		try {
			logger.debug("GenericCommandToolThread :"+threadName);
			logger.info("Sending get request");
			reportPath=URLEncoder.encode(reportPath,"UTF-8");
			logger.info("threadName "+threadName);
			reportPath=reportPath.replace(" ","%20");
			reportPath=URLEncoder.encode(reportPath,"UTF-8");
			threadName=threadName.replaceAll(" ", "%20");
			threadName=URLEncoder.encode(threadName,"UTF-8");
			toolName=toolName.replaceAll(" ", "_");
			logger.info("threadName "+threadName);
			String jenkinsURL=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;
			String read_GenericCommandJob_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/genericCommandReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc/"+toolName+"/?reportPath="+reportPath;
			logger.debug("URL is :"+read_GenericCommandJob_console);
			URL url = new URL(read_GenericCommandJob_console);
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
			String result = buf.toString();
			logger.debug( "\nResponse from server after POST:\n" + result );
			logger.info("Returning Result");
			result=result+" :"+threadName;
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Exception in GenericCommandToolThread for "+threadName+" : "+e);
			logger.error("Returning failure");
			return "Failed :"+threadName;
		}finally{
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
