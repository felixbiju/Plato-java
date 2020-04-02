package com.PLATO.services;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.PLATO.constants.GlobalConstants;

public class BuildJobService {
	private static final Logger logger=Logger.getLogger(BuildJobService.class);
	public static boolean shouldPostBuildBuildJobsContinue(int buildNumber,String moduleName,int buildHistoryId) {
		String jenkinsURL=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT;
		
		String encodedmoduleName=moduleName.replaceAll(" ", "%20");
		String read_build_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/shouldPostBuildJobsContinue/"+Integer.toString(buildHistoryId)+"/"+encodedmoduleName+"/"+Integer.toString(buildNumber);
		HttpURLConnection con=null;
		InputStreamReader reader=null;
		try {
			logger.debug("URL is :"+read_build_console);
			URL url = new URL(read_build_console);
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
			boolean resultBoolean=Boolean.parseBoolean(result);
			return resultBoolean;
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Exception ");
			logger.error("Returning failure");
			return false;
		}finally {
			
		}

	} 
}
