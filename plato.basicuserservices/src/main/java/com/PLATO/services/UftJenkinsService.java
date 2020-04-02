package com.PLATO.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.PLATO.constants.GlobalConstants;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.utilities.XMLUtilities;

public class UftJenkinsService {
	public void createUftSubJob(ModuleSubJobsJenkins uftModule) throws Exception{
		String encodedJobName=uftModule.getSubjob_name().replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {
			//read xml from resources folder
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource("xmlFiles/jobUftConfig.xml").getFile());
			Document doc = docBuilder.parse(file);
			
			doc=XMLUtilities.setTagText(doc,"description" ,uftModule.getSubjob_description());
			doc=XMLUtilities.setTagText(doc,"artifacts" ,uftModule.getReport_path());
			
			Element command=(Element)doc.getElementsByTagName("command").item(0);
			command.setTextContent(uftModule.getCommand_to_execute());
			Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
			assignedNode.setTextContent(uftModule.getNodeMaster().getNode_name());
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);


			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(uftModule.getSubjob_name(),"UTF-8"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(st);
			wr.flush();
			wr.close();

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
			System.err.println( "\nResponse from server after POST:\n" + result );
		//	reloadJenkins();
			
			
		}else {
			throw new Exception("Duplicate Module");			
		}
		
	}
	
	public void updateUftSubJob(ModuleSubJobsJenkins uftModule) throws Exception {
		String encodedJobName=uftModule.getSubjob_name().replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null) {
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			doc=XMLUtilities.setTagText(doc,"description",uftModule.getSubjob_description());	
			doc=XMLUtilities.setTagText(doc,"assignedNode",uftModule.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"command",uftModule.getCommand_to_execute());
			doc=XMLUtilities.setTagText(doc,"artifacts",uftModule.getReport_path());
			
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);
			
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedJobName+"/config.xml");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(st);
			wr.flush();
			wr.close();
			
			StringBuilder sb = new StringBuilder(); 
			System.out.println(con.getResponseCode());
			int HttpResult = con.getResponseCode(); 
			if (HttpResult == HttpURLConnection.HTTP_OK)
			{
				BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;  
				while ((line = br.readLine()) != null)
				{  
					sb.append(line + "\n");  
				}
				br.close();
				System.out.println("Received Response :" + sb.toString());  


				//reloadJenkins();
			}
			else
			{
				throw new Exception("Error while updating Job");
			}
			
			
		}else {
			throw new Exception("Module Does Not Exist");
		}
		
	}
	
	String checkJenkinsJobExist(String jobName) throws Exception
	{
		String inputLine;
		URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/config.xml");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept", "text/xml");
		con.setRequestMethod("GET");

		System.out.println("Response Code : " + con.getResponseCode());
		if(con.getResponseCode()!=200)
		{
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		String output=response.toString();
		return output;
	}
}
