package com.PLATO.services;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.GlobalConstants;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ProjectToolMapping;
import com.PLATO.utilities.XMLUtilities;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Path("diatCompareService")
public class DiatCompareService {
	private static final Logger logger=Logger.getLogger(DiatCompareService.class);
	@GET
	@Path(value="getFilesComparison/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getFilesComparison(@QueryParam("xmlFilePath1") String xmlFilePath1,@QueryParam("xmlFilePath2") String xmlFilePath2,@QueryParam("mappingPath") String mappingPath,@PathParam("projectId") int projectId) throws Exception {
		ModuleSubJobsJenkins diatSubJob=new ModuleSubJobsJenkins();
		
		
		ProjectToolMapping projectToolMap=new ProjectToolMapping();
		String queryString="from ProjectToolMapping ptm where ptm.projectMaster.project_id=:projectId AND ptm.toolMaster.tool_id=11";
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		keyvalueMap.put("projectId",projectId );
		projectToolMap=(ProjectToolMapping)GenericDaoSingleton.getGenericDao().findUniqueByQuery(queryString, keyvalueMap);
		
		
		diatSubJob.setSubjob_name(("diatComparison"+projectId).replace(" ", "%20"));
		String diatBat="D: \n cd D:\\DIAT \n java -Xmx1024m -Xss20m -jar DIAT.jar \n"+"java -Xmx1024m -Xss20m -jar DIAT.jar"+" \""+xmlFilePath1+"\" "+" \""+xmlFilePath2+"\""+" \""+mappingPath+"\""+"\npause";
		//String commandToExecute=diatBat+"\npause \nexit";
		String commandToExecute="D:\r\n" + 
				"cd D:\\Software\\DIAT\r\n" + 
				"DIAT.bat\r\n" + 
				"pause\r\n" + 
				"exit ";
		diatSubJob.setCommand_to_execute(commandToExecute);
		diatSubJob.setReport_path(projectToolMap.getReport_path());
		diatSubJob.setNodeMaster(projectToolMap.getNodeMaster());
		diatSubJob.setTool_name("DIAT");
		JenkinsServices jenkinsService=new JenkinsServices();
		
		
		if(jenkinsService.checkJenkinsJobExist(diatSubJob.getSubjob_name())==null) {
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource("xmlFiles/jobDiatConfig.xml").getFile());
			Document doc = docBuilder.parse(file);
			Element command=(Element)doc.getElementsByTagName("command").item(0);
			command.setTextContent(diatSubJob.getCommand_to_execute());
			Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
			assignedNode.setTextContent(diatSubJob.getNodeMaster().getNode_name());
			Element sourceFolderPath=(Element)doc.getElementsByTagName("sourceFolderPath").item(0);
			sourceFolderPath.setTextContent(diatSubJob.getReport_path());
			diatSubJob.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			//diatSubJob.setReport_path("C:/Users/"+System.getProperty("user.name")+"/.jenkins/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);
			
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(diatSubJob.getSubjob_name(),"UTF-8"));

			logger.info("Jenkins URL: "+url);


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

			logger.info("Diat job created successfully ");
			//	reloadJenkins();

		}else {
			System.out.println("updating the job.....");
			Document doc=XMLUtilities.convertStringToDocument(jenkinsService.checkJenkinsJobExist(diatSubJob.getSubjob_name()));
			doc=XMLUtilities.setTagText(doc, "command", diatSubJob.getCommand_to_execute());
			doc=XMLUtilities.setTagText(doc,"assignedNode",diatSubJob.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"sourceFolderPath",diatSubJob.getReport_path());
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println("st is "+st);
			
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+diatSubJob.getSubjob_name()+"/config.xml");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");
			diatSubJob.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			//diatSubJob.setReport_path("C:/Users/"+System.getProperty("user.name")+"/.jenkins/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			
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
		}
		int nextBuildNumber=jenkinsService.fetchNextBuildNumber(diatSubJob.getSubjob_name());
		jenkinsService.buildJob(diatSubJob.getSubjob_name());
		//String readDiatJsonPath="http://localhost:7080/PlatoBuildMongoDBService/DiatCompareConsoleService/returnDiatJson/"+diatSubJob.getSubjob_name()+"/"+nextBuildNumber+"/?reportPath="+URLEncoder.encode(diatSubJob.getReport_path(),"UTF-8");

		String readDiatJsonPath=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/DiatCompareConsoleService/returnDiatJson/"+diatSubJob.getSubjob_name()+"/"+nextBuildNumber+"/?reportPath="+URLEncoder.encode(diatSubJob.getReport_path(),"UTF-8");
		
		
		//String readDiatJsonPath="http://localhost:7080/PlatoBuildMongoDBService/DiatCompareConsoleService/hello";
		System.out.println("json path "+readDiatJsonPath);
		URL url = new URL(readDiatJsonPath);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("GET");
		
	/*	OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.flush();
		wr.close();*/
		
		/*InputStreamReader reader = new InputStreamReader( con.getInputStream() );
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[ 2048 ];
		int num;
		while ( -1 != (num=reader.read( cbuf )))
		{
			buf.append( cbuf, 0, num );
		}
		String result = buf.toString();
		logger.debug( "\nResponse from server after GET:\n" + result );
		logger.info("Returning Result");
		System.out.println(result);*/
		StringBuilder sb = new StringBuilder();  
		int HttpResult = con.getResponseCode(); 
		System.out.println("HttpResult is "+HttpResult);
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
		JSONParser parser = new JSONParser();
		JSONObject obj=new JSONObject();
		obj=(JSONObject)parser.parse(sb.toString());
		return Response.status(Response.Status.OK).entity(obj).build();
		//ModuleSubJobsJenkins subJob=new ModuleSubJobsJenkins();
	}
	
	@GET
	@Path(value="getComparison/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getComparison(@QueryParam("commandToExecute") String commandToExecute,@PathParam("projectId") int projectId) throws Exception {
		ModuleSubJobsJenkins diatSubJob=new ModuleSubJobsJenkins();
		
		
		ProjectToolMapping projectToolMap=new ProjectToolMapping();
		String queryString="from ProjectToolMapping ptm where ptm.projectMaster.project_id=:projectId AND ptm.toolMaster.tool_id=11";
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		keyvalueMap.put("projectId",projectId );
		projectToolMap=(ProjectToolMapping)GenericDaoSingleton.getGenericDao().findUniqueByQuery(queryString, keyvalueMap);
		
		
		diatSubJob.setSubjob_name(("diatComparison"+projectId).replace(" ", "%20"));
//		String diatBat="D: \n cd D:\\DIAT \n java -Xmx1024m -Xss20m -jar DIAT.jar \n"+"java -Xmx1024m -Xss20m -jar DIAT.jar"+" \""+xmlFilePath1+"\" "+" \""+xmlFilePath2+"\""+" \""+mappingPath+"\""+"\npause";
//		//String commandToExecute=diatBat+"\npause \nexit";
//		String commandToExecute="D:\r\n" + 
//				"cd D:\\Software\\DIAT\r\n" + 
//				"DIAT.bat\r\n" + 
//				"pause\r\n" + 
//				"exit ";
		diatSubJob.setCommand_to_execute(commandToExecute);
		diatSubJob.setReport_path(projectToolMap.getReport_path());
		diatSubJob.setNodeMaster(projectToolMap.getNodeMaster());
		diatSubJob.setTool_name("DIAT");
		JenkinsServices jenkinsService=new JenkinsServices();
		
		
		if(jenkinsService.checkJenkinsJobExist(diatSubJob.getSubjob_name())==null) {
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource("xmlFiles/jobDiatConfig.xml").getFile());
			Document doc = docBuilder.parse(file);
			Element command=(Element)doc.getElementsByTagName("command").item(0);
			command.setTextContent(diatSubJob.getCommand_to_execute());
			Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
			assignedNode.setTextContent(diatSubJob.getNodeMaster().getNode_name());
			Element sourceFolderPath=(Element)doc.getElementsByTagName("sourceFolderPath").item(0);
			sourceFolderPath.setTextContent(diatSubJob.getReport_path());
			diatSubJob.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			//diatSubJob.setReport_path("C:/Users/"+System.getProperty("user.name")+"/.jenkins/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);
			
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(diatSubJob.getSubjob_name(),"UTF-8"));

			logger.info("Jenkins URL: "+url);


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

			logger.info("Diat job created successfully ");
			//	reloadJenkins();

		}else {
			System.out.println("updating the job.....");
			logger.debug("jenkinsService.checkJenkinsJobExist(diatSubJob.getSubjob_name() "+jenkinsService.checkJenkinsJobExist(diatSubJob.getSubjob_name()));
			Document doc=XMLUtilities.convertStringToDocument(jenkinsService.checkJenkinsJobExist(diatSubJob.getSubjob_name()));
			logger.debug("command is "+doc.getElementsByTagName("command").item(0).getTextContent());
			logger.debug("doc is "+doc);
			doc=XMLUtilities.setTagText(doc, "command", diatSubJob.getCommand_to_execute());
			doc=XMLUtilities.setTagText(doc,"assignedNode",diatSubJob.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"sourceFolderPath",diatSubJob.getReport_path());
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			logger.debug("doc is "+doc);
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println("st is "+st);
			
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+diatSubJob.getSubjob_name()+"/config.xml");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");
			diatSubJob.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			//diatSubJob.setReport_path("C:/Users/"+System.getProperty("user.name")+"/.jenkins/workspace/"+diatSubJob.getSubjob_name()+"/RecentReport.xlsx");
			
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
		}
		int nextBuildNumber=jenkinsService.fetchNextBuildNumber(diatSubJob.getSubjob_name());
		jenkinsService.buildJob(diatSubJob.getSubjob_name());
		//String readDiatJsonPath="http://localhost:7080/PlatoBuildMongoDBService/DiatCompareConsoleService/returnDiatJson/"+diatSubJob.getSubjob_name()+"/"+nextBuildNumber+"/?reportPath="+URLEncoder.encode(diatSubJob.getReport_path(),"UTF-8");

		String readDiatJsonPath=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/DiatCompareConsoleService/returnDiatJson/"+diatSubJob.getSubjob_name()+"/"+nextBuildNumber+"/?reportPath="+URLEncoder.encode(diatSubJob.getReport_path(),"UTF-8");
		
		
		//String readDiatJsonPath="http://localhost:7080/PlatoBuildMongoDBService/DiatCompareConsoleService/hello";
		System.out.println("json path "+readDiatJsonPath);
		URL url = new URL(readDiatJsonPath);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("GET");
		
	/*	OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.flush();
		wr.close();*/
		
		/*InputStreamReader reader = new InputStreamReader( con.getInputStream() );
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[ 2048 ];
		int num;
		while ( -1 != (num=reader.read( cbuf )))
		{
			buf.append( cbuf, 0, num );
		}
		String result = buf.toString();
		logger.debug( "\nResponse from server after GET:\n" + result );
		logger.info("Returning Result");
		System.out.println(result);*/
		StringBuilder sb = new StringBuilder();  
		int HttpResult = con.getResponseCode(); 
		System.out.println("HttpResult is "+HttpResult);
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
		JSONParser parser = new JSONParser();
		JSONObject obj=new JSONObject();
		obj=(JSONObject)parser.parse(sb.toString());
		return Response.status(Response.Status.OK).entity(obj).build();
		//ModuleSubJobsJenkins subJob=new ModuleSubJobsJenkins();
	}
}
