package com.mongo.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
/**
 * @author 10643331(Sueanne Alphonso)
 **/
public class ReadSonarDashboard {
	private static final Logger logger=Logger.getLogger(ReadSonarDashboard.class);
	public String readSonarDashboard(String url) throws IOException {
		
		logger.info("Reading Sonar Dashboard from "+url);
		URL object=null;
		
		try {
			object = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String inputLine;
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setRequestProperty("Accept", "text/xml");
		con.setRequestMethod("GET");

		System.out.println("Response Code : " + con.getResponseCode());
		if (con.getResponseCode() != 200) {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));

		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// System.out.println(response.toString());
		String output = response.toString();
		logger.info("Successfully Read Sonar Dashboard");
		return output;
	}	
}
