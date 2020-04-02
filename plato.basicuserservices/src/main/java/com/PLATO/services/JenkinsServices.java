package com.PLATO.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.PLATO.constants.GlobalConstants;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleJobsJenkinsParameters;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.utilities.XMLUtilities;


//this class has functions to perform operations on jenkins
public class JenkinsServices 
{

	private static final Logger logger=Logger.getLogger(JenkinsServices.class);


	//function to create a job in jenkins
	public String createJob(String description, String jobName,int jobId,List<ModuleJobsJenkinsParameters> moduleJobsJenkinsParametersList) throws Exception
	{
		logger.info("Inside function createJob");
		logger.debug("Received parameters description :"+description+" jobName :"+jobName);
		String encodedJobName=jobName.replaceAll(" ", "%20");
		
		HashMap<String,String> parameters=new HashMap<String,String>();
		for(ModuleJobsJenkinsParameters moduleJobsJenkinsParameters:moduleJobsJenkinsParametersList) {
			parameters.put(moduleJobsJenkinsParameters.getParameter_key(), moduleJobsJenkinsParameters.getValue());
		}
		if(checkJenkinsJobExist(encodedJobName)==null)
		{
			//read xml file, add our description to it and save it
			//read jobConfig.xml

			//read xml from resources folder
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource(GlobalConstants.JENKINS_JOBCONFIG_XML).getFile());
			Document doc = docBuilder.parse(file);	

			//		Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JOBCONFIG_XML);

			//set text of description tag to given description
			doc=XMLUtilities.setTagText(doc,"description",description);
			if(parameters.containsKey("timer_trigger") || parameters.containsKey("scm_timer")) {
				addBuildTriggerOptions(doc,parameters,jobId);
			}
			
			//write the updated document to file
			/*doc.getDocumentElement().normalize();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource xmlSource = new DOMSource(doc);
			StreamResult xmlResult = new StreamResult(GlobalConstants.JOBCONFIG_XML); 
			transformer.transform(xmlSource, xmlResult);*/


			//read changed document
			//doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JOBCONFIG_XML);

			//convert document to String
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the jobconfig.xml to string :"+st);

			//send post request for creating Jenkins job
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(jobName,"UTF-8"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");

			/* String authStr =  "admin:6482c343206446e4ad0553b9b2047bc1";
	     String encoding = DatatypeConverter.printBase64Binary(authStr.getBytes("utf-8"));
	     con.setRequestProperty("Authorization", "Basic " + encoding);*/

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
			logger.debug( "\nResponse from server after POST:\n" + result );
			logger.info("Returning response after creating job in jenkins");
			return result;
		}
		else
		{
			logger.error("Duplicate job :"+jobName);
			throw new Exception("Duplicate Module");
		}
	}
	
	
	
	
	

	//function to update a job in jenkins
	//function to update a job in jenkins
		public String updateJob(String description, String jobName,int jobId,List<ModuleJobsJenkinsParameters> moduleJobsJenkinsParametersList) throws Exception
		{
			logger.info("Inside function updateJob");
			logger.debug("Received parameters description :"+description+" jobName :"+jobName);
			String encodedJobName=jobName.replaceAll(" ", "%20");
			
			HashMap<String,String> parameters=new HashMap<String,String>();
			for(ModuleJobsJenkinsParameters moduleJobsJenkinsParameters:moduleJobsJenkinsParametersList) {
				parameters.put(moduleJobsJenkinsParameters.getParameter_key(), moduleJobsJenkinsParameters.getValue());
			}
		
			String jobConfig=checkJenkinsJobExist(encodedJobName);
			if(jobConfig!=null)
			{

				//convert string to xml
				Document doc=XMLUtilities.convertStringToDocument(jobConfig);
				//Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+fastModule.getSubjob_name()+"/config.xml");
				//convert document to string
				 doc=XMLUtilities.setTagText(doc,"description",description);
					if(parameters.containsKey("timer_trigger") || parameters.containsKey("scm_timer")) {
						logger.info("scheduling");
						addBuildTriggerOptions(doc,parameters,jobId);
					}
				StringWriter sw = new StringWriter(); 
				Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
				serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
				String st=sw.toString(); 
				logger.debug("Converted the xml to string :"+st);

				/*doc.getDocumentElement().normalize();
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				DOMSource xmlSource = new DOMSource(doc);
				StreamResult xmlResult = new StreamResult(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+fastModule.getSubjob_name()+"/config.xml"); 
				transformer.transform(xmlSource, xmlResult);

				st=sw.toString(); 
				System.out.println(st);
				 */

				//send request to update jenkins job
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

				//read response
				
				StringBuilder sb = new StringBuilder(); 
				logger.debug("Response code :"+con.getResponseCode());
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
					logger.info("Received Response :" + sb.toString());  
					//logger.info("reloading jenkins");
					//commenting reload 
					//reloadJenkins();			
					// reading the response
				
					
				}
				return sb.toString();
			}
			else
			{
				logger.error("Job does not exist in jenkins :"+jobName);
				throw new Exception("Job does not exist in jenkins");
			}
		}
		/**
		 * @author 10643380(Rahul Bhardwaj)
		 * */
		private Document addBuildTriggerOptions(Document doc,HashMap<String,String> parameters,int jobId) {
			Element project=(Element)doc.getElementsByTagName("project").item(0);
			NodeList nl = doc.getElementsByTagName("hudson.triggers.TimerTrigger");
			NodeList nl2 = doc.getElementsByTagName("hudson.triggers.SCMTrigger");
			Element triggers=(Element)doc.getElementsByTagName("triggers").item(0);	
			Element builders=(Element)project.getElementsByTagName("builders").item(0);
			if ((nl.getLength() == 0) && (nl2.getLength() == 0)) {	
				Element batchFile=doc.createElement("hudson.tasks.BatchFile");
				builders.appendChild(batchFile);
				Element command=doc.createElement("command");
				batchFile.appendChild(command);
				command.setTextContent(GlobalConstants.CURL_HOME.split(":")[0]+":"+"\r\n"+"cd " +GlobalConstants.CURL_HOME+"\r\n" + 
						"curl "+GlobalConstants.TOMCAT_URL+":"+GlobalConstants.TOMCAT_PORT+"/plato.basicuserservices/buildModule/getJenkinsBuildNumbers/"+jobId+"\r\n" + 
						"curl "+GlobalConstants.TOMCAT_URL+":"+GlobalConstants.TOMCAT_PORT+"/plato.basicuserservices/buildModule/executeJobNoJenkinsTrigger/"+jobId+"/%BUILD_NUMBER%"+" --max-time 20 || exit 0");
			}
			if ((nl.getLength() > 0) && (parameters.containsKey("timer_trigger"))) {
			    nl.item(0).getFirstChild().setTextContent(parameters.get("timer_trigger"));
			    logger.info("found element timertrigger and edited it");
			}
			else{
				logger.info("first time setting timer trigger");
				NodeList triggersNodeList = doc.getElementsByTagName("triggers");
				if(parameters.containsKey("timer_trigger") ) {
					Element timerTrigger=doc.createElement("hudson.triggers.TimerTrigger");
					triggersNodeList.item(0).appendChild(timerTrigger);
					Element specTimerTrigger=doc.createElement("spec");
					timerTrigger.appendChild(specTimerTrigger);
					specTimerTrigger.setTextContent(parameters.get("timer_trigger"));	
					
					
				}
			}
			
			
			if ((nl2.getLength() > 0) && (parameters.containsKey("scm_timer"))) {
			    nl2.item(0).getFirstChild().setTextContent(parameters.get("scm_timer"));
			    logger.info("found element scmtrigger and edited it");
			}
			else{
				logger.info("first time setting scm trigger");
				NodeList triggersNodeList = doc.getElementsByTagName("triggers");
				if(parameters.containsKey("scm_timer") ) {
					Element scmTrigger=doc.createElement("hudson.triggers.SCMTrigger");
					triggersNodeList.item(0).appendChild(scmTrigger);
					Element specScmTrigger=doc.createElement("spec");
					scmTrigger.appendChild(specScmTrigger);
					specScmTrigger.setTextContent(parameters.get("scm_timer"));	
					
					
				}
			}
			

		
			Element batchFile=(Element)doc.getElementsByTagName("hudson.tasks.BatchFile").item(0);
			batchFile.getFirstChild().setTextContent(GlobalConstants.CURL_HOME.split(":")[0]+":"+"\r\n"+"cd " +GlobalConstants.CURL_HOME+"\r\n" + 
					"curl "+GlobalConstants.TOMCAT_URL+":"+GlobalConstants.TOMCAT_PORT+"/plato.basicuserservices/buildModule/getJenkinsBuildNumbers/"+jobId+"\r\n" + 
					"curl "+GlobalConstants.TOMCAT_URL+":"+GlobalConstants.TOMCAT_PORT+"/plato.basicuserservices/buildModule/executeJobNoJenkinsTrigger/"+jobId+"/%BUILD_NUMBER%"+" --max-time 20 || exit 0");
			
			
			return doc;
		}
	public String createBuildMainJob(ModuleJobsJenkins module) throws Exception
	{
		String jobName=module.getJenkins_job_name();
		String description=module.getDescription();
		logger.info("Inside function createJob");
		logger.debug("Received parameters description :"+description+" jobName :"+jobName);
		String encodedJobName=jobName.replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null)
		{
			//read xml file, add our description to it and save it
			//read jobConfig.xml

			//read xml from resources folder
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource(GlobalConstants.JENKINS_JOBCONFIG_XML).getFile());
			Document doc = docBuilder.parse(file);	

			//		Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JOBCONFIG_XML);

			//set text of description tag to given description
			doc=XMLUtilities.setTagText(doc,"description",description);
			//write the updated document to file
			/*doc.getDocumentElement().normalize();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource xmlSource = new DOMSource(doc);
			StreamResult xmlResult = new StreamResult(GlobalConstants.JOBCONFIG_XML); 
			transformer.transform(xmlSource, xmlResult);*/


			//read changed document
			//doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JOBCONFIG_XML);

			//convert document to String
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the jobconfig.xml to string :"+st);

			//send post request for creating Jenkins job
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(jobName,"UTF-8"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");

			/* String authStr =  "admin:6482c343206446e4ad0553b9b2047bc1";
	     String encoding = DatatypeConverter.printBase64Binary(authStr.getBytes("utf-8"));
	     con.setRequestProperty("Authorization", "Basic " + encoding);*/

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
			logger.debug( "\nResponse from server after POST:\n" + result );
			logger.info("Returning response after creating job in jenkins");
			return result;
		}
		else
		{
			logger.error("Duplicate job :"+jobName);
			throw new Exception("Duplicate Module");
		}
	}


	//function to delete job from jenkins
	public String deleteJob(String jobName) throws Exception
	{
		logger.info("Inside function deleteJob");
		logger.debug("Received parameters jobName :"+jobName);
		String encodedJobName=jobName.replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)!=null)
		{	
			//send post request for creating Jenkins job
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedJobName+"/doDelete");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");

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
			logger.info("Sucessfully deleted job :"+jobName);
			return "Successfully Deleted Job";
		}
		else
		{
			logger.error("Error Job does not exist");
			return "Job does not exist";
		}	
	}
	
	
	
	//function to abort job on jenkins
		public String abortJob(String sub_job_name, int buildNumber) throws Exception
		{
			logger.info("Inside function abortJob");
			logger.debug("Received parameters sub_job_name :"+sub_job_name+" and Build Number :"+buildNumber);
			//String encodedJobName=job_name.replaceAll(" ", "%20");
			String encodedSubJobName=sub_job_name.replaceAll(" ", "%20");
			
			if(checkJenkinsJobExist(encodedSubJobName)!=null)
			{	
				try {
				//send post request for creating Jenkins job
				URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedSubJobName+"/"+buildNumber+"/stop");
				logger.info("ABORTING URL IS:  "+url);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestMethod("POST");

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
				logger.info("Sucessfully Aborted Job :"+sub_job_name);
				
				
				
				
				return "Successfully Aborted Job";
				
			}catch(Exception e) {
				logger.error("Latest build of subjob: "+encodedSubJobName+" is not in progress!!!!!!!!");
				
				e.printStackTrace();
				return "Successfully Aborted Job";
			}
			}
			else
			{
				logger.error("Error Job does not exist");
				return "Job does not exist";
			}	
		}


	public void createSeleniumSubJob(ModuleSubJobsJenkins seleniumModule) throws Exception {
		logger.info("Inside function createSeleniumSubJob");
		logger.info("Creating selenium SubJob. inside jenkins servies->create SeleniumSubJob");


		String encodedJobName=seleniumModule.getSubjob_name().replace(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {

			//read xml from resources folder
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			//File file=new File(classLoader.getResource("xmlFiles/jobSVNConfig.xml").getFile());
			File file=new File(classLoader.getResource(GlobalConstants.JENKINS_JOBCONFIG_XML).getFile());
			Document doc=docBuilder.parse(file);

			doc=XMLUtilities.setTagText(doc,"description" ,seleniumModule.getSubjob_description());
			doc=XMLUtilities.setTagText(doc, "canRoam", "true");

			//add Node to config file
			Node project = doc.getElementsByTagName("project").item(0);
			Element actions=doc.createElement("actions");
			project.appendChild(actions);

			Element scm=(Element)doc.getElementsByTagName("scm").item(0);
			scm.setAttribute("class", "hudson.scm.NullSCM");

			Element builders=(Element)doc.getElementsByTagName("builders").item(0);

			Element hudsonTask=doc.createElement("hudson.tasks.BatchFile");

			Element command=doc.createElement("command");
			command.appendChild(doc.createTextNode(seleniumModule.getCommand_to_execute()));  //

			hudsonTask.appendChild(command);
			builders.appendChild(hudsonTask);

			Element publishers=(Element)doc.getElementsByTagName("publishers").item(0);

			Element hudsonPlugins=doc.createElement("hudson.plugins.testng.Publisher");

			hudsonPlugins.setAttribute("plugin","testng-plugin@1.14");


			Element reportFnamePattern=doc.createElement("reportFilenamePattern");
			reportFnamePattern.appendChild(doc.createTextNode("**/testng-results.xml"));

			Element escapeTestDescp=doc.createElement("escapeTestDescp");
			escapeTestDescp.appendChild(doc.createTextNode("true"));

			Element escapeExceptionMsg=doc.createElement("escapeExceptionMsg");
			escapeExceptionMsg.appendChild(doc.createTextNode("true"));

			Element failureOnFailedTestConfig=doc.createElement("failureOnFailedTestConfig");
			failureOnFailedTestConfig.appendChild(doc.createTextNode("false"));

			Element showFailedBuilds=doc.createElement("showFailedBuilds");
			showFailedBuilds.appendChild(doc.createTextNode("false"));

			Element unstableSkips=doc.createElement("unstableSkips");
			unstableSkips.appendChild(doc.createTextNode("100"));

			Element unstableFails=doc.createElement("unstableFails");
			unstableFails.appendChild(doc.createTextNode("0"));

			Element failedSkips=doc.createElement("failedSkips");
			failedSkips.appendChild(doc.createTextNode("100"));

			Element failedFails=doc.createElement("failedFails");
			failedFails.appendChild(doc.createTextNode("100"));

			Element thresholdMode=doc.createElement("thresholdMode");
			thresholdMode.appendChild(doc.createTextNode("2"));

			hudsonPlugins.appendChild(reportFnamePattern);
			hudsonPlugins.appendChild(escapeTestDescp);
			hudsonPlugins.appendChild(escapeExceptionMsg);
			hudsonPlugins.appendChild(failureOnFailedTestConfig);
			hudsonPlugins.appendChild(showFailedBuilds);
			hudsonPlugins.appendChild(unstableSkips);
			hudsonPlugins.appendChild(unstableFails);
			hudsonPlugins.appendChild(failedSkips);
			hudsonPlugins.appendChild(failedFails);
			hudsonPlugins.appendChild(thresholdMode);

			publishers.appendChild(hudsonPlugins);

			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the jobconfig.xml to string :"+st);



			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(seleniumModule.getSubjob_name(),"UTF-8"));

			logger.info("Jenkins URL : "+url); 

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
			logger.debug( "\nResponse from server after POST:\n" + result );
			//reloadJenkins();

		}else {
			logger.error("Exception in Creating selenium subjob");
			throw new Exception("Duplicate Module");
		}
	}



	/*
	 * Important: 	In case of createMavenSubJob we are using 
	 * 				ModuleSubJobsJenkins.getCommand_to_execute to send
	 * 				rootPOM path
	 * 				This has been done because ModuleSubJobsJenkins does not have any field called as rootPOM.
	 * 				rootPOM is the input under build section of MavenJob in Jenkins, therefore the above mentioned field is being used.
	 * 				In case of SVN repository and credentials we will be using report path,space separated
	 * */
	public void createMavenSubJob(ModuleSubJobsJenkins mavenSvnModule) throws Exception{
		String encodedJobName=mavenSvnModule.getSubjob_name().replace(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
			ClassLoader classLoader=this.getClass().getClassLoader();
			//File file=new File(classLoader.getResource("xmlFiles/jobSVNConfig.xml").getFile());
			File file=new File(classLoader.getResource("xmlFiles/jobMavenSvnConfig.xml").getFile());
			Document doc=docBuilder.parse(file);
			String[] credentialsAndRemote=mavenSvnModule.getReport_path().split(",");
			if(credentialsAndRemote[0].equalsIgnoreCase("SVN")) {

				doc=XMLUtilities.setTagText(doc,"description" ,mavenSvnModule.getSubjob_description());
				Element rootPOM=(Element)doc.getElementsByTagName("rootPOM").item(0);
				rootPOM.setTextContent(mavenSvnModule.getCommand_to_execute());
				Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
				assignedNode.setTextContent(mavenSvnModule.getNodeMaster().getNode_name());
				
				Element remote=(Element)doc.getElementsByTagName("remote").item(0);
				Element credentialsId=(Element)doc.getElementsByTagName("credentialsId").item(0);
				remote.setTextContent(credentialsAndRemote[1]);
				credentialsId.setTextContent(credentialsAndRemote[2]);
				createCredential("","");
			}else if(credentialsAndRemote[0].equalsIgnoreCase("GIT")) {

			}


			/*for(Node n:rootPOM.getChildNodes()) {
				rootPOM.removeChild(n);
			}
			rootPOM.appendChild(doc.createTextNode(mavenModule.getCommand_to_execute()));*/

			//writing the document as a string
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the jobconfig.xml to string :"+st);


			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(mavenSvnModule.getSubjob_name(),"UTF-8"));
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
			logger.debug( "\nResponse from server after POST:\n" + result );
			//reloadJenkins();


		}else {
			throw new Exception("Duplicate Module");
		}	

	}

	public void updateMavenSubJob(ModuleSubJobsJenkins mavenSvnModule) throws Exception{
		String encodedJobName=mavenSvnModule.getSubjob_name().replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null) {
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			doc=XMLUtilities.setTagText(doc,"description",mavenSvnModule.getSubjob_description());	
			doc=XMLUtilities.setTagText(doc,"assignedNode",mavenSvnModule.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"rootPOM",mavenSvnModule.getCommand_to_execute());
			String[] credentialsAndRemote=mavenSvnModule.getReport_path().split(",");
			if(credentialsAndRemote[0].equalsIgnoreCase("SVN")) {
				doc=XMLUtilities.setTagText(doc,"remote",credentialsAndRemote[1]);
				doc=XMLUtilities.setTagText(doc,"credentialsId",credentialsAndRemote[2]);
			}else if(credentialsAndRemote[0].equalsIgnoreCase("GIT")) {

			}
			//doc=XMLUtilities.setTagText(doc,"reportDir",mavenModule.getReport_path());

			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the xml to string :"+st);

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
			logger.debug("response is :"+con.getResponseCode());
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
				logger.debug("Received Response :" + sb.toString());  


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

	/*	public void createMavenSubJob(ModuleSubJobsJenkins mavenModule) throws Exception{
		String encodedJobName=mavenModule.getSubjob_name().replace(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
			ClassLoader classLoader=this.getClass().getClassLoader();
			//File file=new File(classLoader.getResource("xmlFiles/jobSVNConfig.xml").getFile());
			File file=new File(classLoader.getResource("xmlFiles/jobMavenConfig.xml").getFile());
			Document doc=docBuilder.parse(file);

			doc=XMLUtilities.setTagText(doc,"description" ,mavenModule.getSubjob_description());

			Element rootPOM=(Element)doc.getElementsByTagName("rootPOM").item(0);
			rootPOM.setTextContent(mavenModule.getCommand_to_execute());
			Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
			assignedNode.setTextContent(mavenModule.getNodeMaster().getNode_name());


			//for(Node n:rootPOM.getChildNodes()) {
			//	rootPOM.removeChild(n);
			//}
			//rootPOM.appendChild(doc.createTextNode(mavenModule.getCommand_to_execute()));

			//writing the document as a string
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);


			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(mavenModule.getSubjob_name(),"UTF-8"));
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
			//reloadJenkins();


		}else {
			throw new Exception("Duplicate Module");
		}

	}

	 // Here Instead of updating command, we will be updating rootPOM


	public void updateMavenSubJob(ModuleSubJobsJenkins mavenModule) throws Exception {
		String encodedJobName=mavenModule.getSubjob_name().replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null) {
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			doc=XMLUtilities.setTagText(doc,"description",mavenModule.getSubjob_description());	
			doc=XMLUtilities.setTagText(doc,"assignedNode",mavenModule.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"rootPOM",mavenModule.getCommand_to_execute());
			//doc=XMLUtilities.setTagText(doc,"reportDir",mavenModule.getReport_path());

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

	}*/

	public void createSVNSubJob(ModuleSubJobsJenkins svnModule) throws Exception {
		String encodedJobName=svnModule.getSubjob_name().replace(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {

			//read xml from resources folder
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			//File file=new File(classLoader.getResource("xmlFiles/jobSVNConfig.xml").getFile());
			File file=new File(classLoader.getResource(GlobalConstants.JENKINS_JOBCONFIG_XML).getFile());
			Document doc=docBuilder.parse(file);

			//read jobConfig.xml file
			//	Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JOBCONFIG_XML);

			doc=XMLUtilities.setTagText(doc,"description" ,svnModule.getSubjob_description());
			doc=XMLUtilities.setTagText(doc, "canRoam", "false");

			//add Node to config file
			Node project = doc.getElementsByTagName("project").item(0);
			Element actions=doc.createElement("actions");
			Element assignedNode=doc.createElement("assignedNode");
			assignedNode.appendChild(doc.createTextNode(svnModule.getNodeMaster().getNode_name()));
			project.insertBefore(assignedNode, project.getFirstChild());
			project.insertBefore(actions, project.getFirstChild());
			//project.appendChild(actions);
			//project.appendChild(assignedNode);

			Element scm=(Element)doc.getElementsByTagName("scm").item(0);
			scm.setAttribute("class", "hudson.scm.SubversionSCM");
			scm.setAttribute("plugin", "subversion@2.10.2");

			Element locations=doc.createElement("locations");

			Element hudsonScm=doc.createElement("hudson.scm.SubversionSCM_-ModuleLocation");

			Element remote=doc.createElement("remote");
			remote.appendChild(doc.createTextNode("https://www.google.co.in/?gfe_rd=cr&amp;dcr=0&amp;ei=BMpEWsDtO-jI8AewvI3ADA"));
			Element credentialsId=doc.createElement("credentialsId");
			Element local=doc.createElement("local");
			local.appendChild(doc.createTextNode("."));
			Element depthOption=doc.createElement("depthOption");
			depthOption.appendChild(doc.createTextNode("infinity"));
			Element ignoreExternalsOption=doc.createElement("ignoreExternalsOption");
			ignoreExternalsOption.appendChild(doc.createTextNode("true"));

			hudsonScm.appendChild(remote);
			hudsonScm.appendChild(credentialsId);
			hudsonScm.appendChild(local);
			hudsonScm.appendChild(depthOption);
			hudsonScm.appendChild(ignoreExternalsOption);

			locations.appendChild(hudsonScm);

			Element excludedRegions=doc.createElement("excludedRegions");
			Element includedRegions=doc.createElement("includedRegions");
			Element excludedUsers=doc.createElement("excludedUsers");
			Element excludedRevprop=doc.createElement("excludedRevprop");
			Element excludedCommitMessages=doc.createElement("excludedCommitMessages");
			Element workspaceUpdater=doc.createElement("workspaceUpdater");
			workspaceUpdater.setAttribute("class", "hudson.scm.subversion.UpdateUpdater");
			Element ignoreDirPropChanges=doc.createElement("ignoreDirPropChanges");
			ignoreDirPropChanges.appendChild(doc.createTextNode("false"));
			Element filterChangelog=doc.createElement("filterChangelog");
			filterChangelog.appendChild(doc.createTextNode("false"));
			Element quietOperation=doc.createElement("quietOperation");
			quietOperation.appendChild(doc.createTextNode("true"));

			scm.appendChild(locations);
			scm.appendChild(excludedRegions);
			scm.appendChild(includedRegions);
			scm.appendChild(excludedUsers);
			scm.appendChild(excludedRevprop);
			scm.appendChild(excludedCommitMessages);
			scm.appendChild(workspaceUpdater);
			scm.appendChild(ignoreDirPropChanges);
			scm.appendChild(filterChangelog);
			scm.appendChild(quietOperation);

			//add executionCommand for SVN
			Node builder = doc.getElementsByTagName("builders").item(0);
			Element batchFileTag = doc.createElement("hudson.tasks.BatchFile");

			Element command = doc.createElement("command");
			command.appendChild(doc.createTextNode(svnModule.getCommand_to_execute()));
			batchFileTag.appendChild(command);	
			builder.appendChild(batchFileTag);
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the xml to string :"+st);


			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(svnModule.getSubjob_name(),"UTF-8"));
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
			logger.debug( "\nResponse from server after POST:\n" + result );
			//reloadJenkins();

		}else {

			throw new Exception("Duplicate Module");
		}
	}

	public void updateSvnSubJob(ModuleSubJobsJenkins svnModule) throws Exception {
		String encodedJobName=svnModule.getSubjob_name().replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null) {
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			doc=XMLUtilities.setTagText(doc,"description",svnModule.getSubjob_description());	
			doc=XMLUtilities.setTagText(doc,"assignedNode",svnModule.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"command",svnModule.getCommand_to_execute());
			//doc=XMLUtilities.setTagText(doc,"reportDir",svnModule.getReport_path());

			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the xml to string :"+st);

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
			logger.debug("Response is :"+con.getResponseCode());
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

	public void createSonarJob(ModuleSubJobsJenkins sonarModule) throws Exception{
		String encodedJobName=sonarModule.getSubjob_name().replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource("xmlFiles/jobSonarConfig.xml").getFile());
			Document doc = docBuilder.parse(file);
			String[] credentialsAndRemote=sonarModule.getReport_path().split(",");
			if(credentialsAndRemote[0].equalsIgnoreCase("SVN")) {
				Element remote=(Element)doc.getElementsByTagName("remote").item(0);
				Element credentialsId=(Element)doc.getElementsByTagName("credentialsId").item(0);
				remote.setTextContent(credentialsAndRemote[1]);
				credentialsId.setTextContent(credentialsAndRemote[2]);
				createCredential("","");
			}else if(credentialsAndRemote[0].equalsIgnoreCase("GIT")) {

			}
			sonarModule.setReport_path(credentialsAndRemote[3]);
			doc=XMLUtilities.setTagText(doc,"description" ,sonarModule.getSubjob_description());
			Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
			assignedNode.setTextContent(sonarModule.getNodeMaster().getNode_name());
			Element properties=(Element)doc.getElementsByTagName("properties").item(1);
			properties.setTextContent(sonarModule.getCommand_to_execute());
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);

			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(sonarModule.getSubjob_name(),"UTF-8"));
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

	public void updateSonarSubJob(ModuleSubJobsJenkins sonarModule) throws Exception {
		String encodedJobName=sonarModule.getSubjob_name().replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null) {
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			doc=XMLUtilities.setTagText(doc,"description",sonarModule.getSubjob_description());	
			doc=XMLUtilities.setTagText(doc,"assignedNode",sonarModule.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"properties",sonarModule.getCommand_to_execute());
			//doc=XMLUtilities.setTagText(doc,"reportDir",svnModule.getReport_path());
			String[] credentialsAndRemote=sonarModule.getReport_path().split(",");
			if(credentialsAndRemote[0].equalsIgnoreCase("SVN")) {
				Element remote=(Element)doc.getElementsByTagName("remote").item(0);
				Element credentialsId=(Element)doc.getElementsByTagName("credentialsId").item(0);
				remote.setTextContent(credentialsAndRemote[1]);
				credentialsId.setTextContent(credentialsAndRemote[2]);
				createCredential("","");
			}else if(credentialsAndRemote[0].equalsIgnoreCase("GIT")) {

			}
			sonarModule.setReport_path(credentialsAndRemote[3]);
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

	public void createDiatSubJob(ModuleSubJobsJenkins diatModule) throws Exception{

		logger.info("Creating Diat Job on jenkins : "+diatModule.getSubjob_name());

		String encodedJobName=diatModule.getSubjob_name().replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null) {
			//read xml from resources folder
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource("xmlFiles/jobDiatConfig.xml").getFile());
			Document doc = docBuilder.parse(file);

			doc=XMLUtilities.setTagText(doc,"description" ,diatModule.getSubjob_description());
			//doc=XMLUtilities.setTagText(doc,"" ,diatModule.getReport_path());

			Element command=(Element)doc.getElementsByTagName("command").item(0);
			command.setTextContent(diatModule.getCommand_to_execute());
			Element assignedNode=(Element)doc.getElementsByTagName("assignedNode").item(0);
			assignedNode.setTextContent(diatModule.getNodeMaster().getNode_name());
			Element sourceFolderPath=(Element)doc.getElementsByTagName("sourceFolderPath").item(0);
			sourceFolderPath.setTextContent(diatModule.getReport_path());
			//diatModule.setReport_path("C:\\Users\\"+System.getProperty("user.name")+"\\.jenkins\\workspace\\"+diatModule.getSubjob_name()+"\\RecentReport.xlsx");
			diatModule.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+diatModule.getSubjob_name()+"/RecentReport.xlsx");
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);


			//send post request for creating job

			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(diatModule.getSubjob_name(),"UTF-8"));

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

			logger.info("Duplicate Module");
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

	//function to create job in jenkins with configuration of FAST tool
	public void createFASTSubJob(ModuleSubJobsJenkins fastModule) throws Exception
	{
		logger.info("Inside function createFASTSubJob");
		logger.debug("Received object fastModule as parameter with name :"+fastModule.getSubjob_name());
		String encodedJobName=fastModule.getSubjob_name().replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)==null)
		{

			//read xml from resources folder
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ClassLoader classLoader=this.getClass().getClassLoader();
			File file = new File(classLoader.getResource(GlobalConstants.JENKINS_JOBCONFIG_XML).getFile());
			Document doc = docBuilder.parse(file);	

			//read jobConfig.xml file
			//	Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JOBCONFIG_XML);

			doc=XMLUtilities.setTagText(doc,"description",fastModule.getSubjob_description());
			doc=XMLUtilities.setTagText(doc,"canRoam","false");

			//add Node to config file
			Node project = doc.getElementsByTagName("project").item(0);
			Element node = doc.createElement("assignedNode");
			node.appendChild(doc.createTextNode(fastModule.getNodeMaster().getNode_name()));
			project.appendChild(node);

			//add executionCommand for FAST
			Node builder = doc.getElementsByTagName("builders").item(0);
			Element batchFileTag = doc.createElement("hudson.tasks.BatchFile");

			Element command = doc.createElement("command");
			command.appendChild(doc.createTextNode(fastModule.getCommand_to_execute()));
			batchFileTag.appendChild(command);	
			builder.appendChild(batchFileTag);

			//add HTML report upload post Build action for FAST
			Node publisher = doc.getElementsByTagName("publishers").item(0);

			Element reportTargets = doc.createElement("reportTargets");

			Element htmlPublisher = doc.createElement("htmlpublisher.HtmlPublisher");
			htmlPublisher.setAttribute("plugin", "htmlpublisher@1.14");

			Element htmlPublisherTarget = doc.createElement("htmlpublisher.HtmlPublisherTarget");

			Element reportName = doc.createElement("reportName");
			reportName.appendChild(doc.createTextNode("HTML Report"));

			Element reportDir = doc.createElement("reportDir");
			reportDir.appendChild(doc.createTextNode(fastModule.getReport_path()));

			Element reportFiles = doc.createElement("reportFiles");
			reportFiles.appendChild(doc.createTextNode("index.html"));

			Element alwaysLinkToLastBuild = doc.createElement("alwaysLinkToLastBuild");
			alwaysLinkToLastBuild.appendChild(doc.createTextNode("false"));

			Element reportTitles = doc.createElement("reportTitles");

			Element allowMissing = doc.createElement("allowMissing");
			allowMissing.appendChild(doc.createTextNode("false"));

			Element includes = doc.createElement("includes");

			htmlPublisherTarget.appendChild(reportName);
			htmlPublisherTarget.appendChild(reportDir);
			htmlPublisherTarget.appendChild(reportFiles);
			htmlPublisherTarget.appendChild(alwaysLinkToLastBuild);
			htmlPublisherTarget.appendChild(reportTitles);
			htmlPublisherTarget.appendChild(allowMissing);
			htmlPublisherTarget.appendChild(includes);

			reportTargets.appendChild(htmlPublisherTarget);
			htmlPublisher.appendChild(reportTargets);
			publisher.appendChild(htmlPublisher);

			//convert xml document to string
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the xml to string :"+st);


			//send post request for creating job
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(fastModule.getSubjob_name(),"UTF-8"));
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
			logger.info( "\nResponse from server after POST:\n" + result );
			logger.info("Returning from create FASTSubjob");
			//	reloadJenkins();
		}

		else
		{
			logger.error("Error in creating FASTSubjob - Duplicate job");
			throw new Exception("Duplicate Module");
		}

	}

	//function to update fast subjob
	public void updateFASTSubJob(ModuleSubJobsJenkins fastModule) throws Exception
	{
		logger.info("Inside function updateFASTSubJob");
		logger.debug("Received object fastModule as parameter with name :"+fastModule.getSubjob_name());
		String encodedJobName=fastModule.getSubjob_name().replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null)
		{

			//convert string to xml
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			//Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+fastModule.getSubjob_name()+"/config.xml");

			doc=XMLUtilities.setTagText(doc,"description",fastModule.getSubjob_description());	
			doc=XMLUtilities.setTagText(doc,"assignedNode",fastModule.getNodeMaster().getNode_name());
			doc=XMLUtilities.setTagText(doc,"command",fastModule.getCommand_to_execute());
			doc=XMLUtilities.setTagText(doc,"reportDir",fastModule.getReport_path());


			//convert document to string
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the xml to string :"+st);

			/*doc.getDocumentElement().normalize();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource xmlSource = new DOMSource(doc);
			StreamResult xmlResult = new StreamResult(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+fastModule.getSubjob_name()+"/config.xml"); 
			transformer.transform(xmlSource, xmlResult);

			st=sw.toString(); 
			System.out.println(st);
			 */

			//send request to update jenkins job
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

			//read response
			StringBuilder sb = new StringBuilder(); 
			logger.debug("Response code :"+con.getResponseCode());
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
				logger.info("Received Response :" + sb.toString());  
				logger.info("reloading jenkins");
				//commenting reload 
				//reloadJenkins();
			}
			else
			{
				logger.error("Error while updating fast subjob");
				throw new Exception("Error while updating Job");
			}
		}

		else
		{
			logger.error("Error while updating fast subjob - Module does not exist");
			throw new Exception("Module Does Not Exist");
		}
		logger.info("Returning from update fast subjob");

	}

	//function to set created job in downstream projects of main job in jenkins
	public void setChildProjectsInMainJob(Set<ModuleSubJobsJenkins> subModuleSet,String moduleName) throws Exception
	{

		logger.info("Inside function setChildProjectsInMainJob");
		logger.debug("Received object parameter moduleName :"+moduleName+" and submoduleSet");

		String encodedJobName=moduleName.replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null)
		{	
			String childProjectString="";
			//change config of main module
			Iterator<ModuleSubJobsJenkins> it=subModuleSet.iterator();
			logger.info("subModuleSet.size() "+subModuleSet.size());
			//author:Rahul
			//I am adding this code,basically the subjobs which are in postBuildJobs shouldn't be present in the main jobs' postBuild
			Set<String> postBuildJobs=new HashSet<String>();
			while(it.hasNext()) {
				ModuleSubJobsJenkins subModule=it.next();
				if(subModule.getPostbuild_subjob()!=null && !subModule.getPostbuild_subjob().isEmpty() && subModule.getPostbuild_subjob()!="") {
					String[] postBuildJobsSplit= subModule.getPostbuild_subjob().split(",");
					for(int i=0;i<postBuildJobsSplit.length;i++) {
						postBuildJobs.add(postBuildJobsSplit[i]);
						}
					}
				}
			it=subModuleSet.iterator();
			
			//
			logger.info("childProjectString="+childProjectString);
			while(it.hasNext())
			{
				ModuleSubJobsJenkins subModule=it.next();
				if((postBuildJobs.contains(subModule.getSubjob_name()))||(subModule.getSubjob_name().equals(moduleName))) {
					continue;
				}
				childProjectString+=subModule.getSubjob_name()+",";
			}
			
			
			
			if(childProjectString!=null  && childProjectString!="")
			{
				childProjectString=childProjectString.substring(0, childProjectString.lastIndexOf(","));
				
			
				logger.info("childProjectString="+childProjectString);
				Document doc=XMLUtilities.convertStringToDocument(jobConfig);
				//Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+moduleName+"/config.xml");
				Node publisher = doc.getElementsByTagName("publishers").item(0);
	
				if(!publisher.hasChildNodes())
				{
					Element buildTrigger = doc.createElement("hudson.tasks.BuildTrigger");
					Element childProjects = doc.createElement("childProjects");				
					childProjects.appendChild(doc.createTextNode(childProjectString));
					Element threshold = doc.createElement("threshold");
					Element name = doc.createElement("name");
					name.appendChild(doc.createTextNode("SUCCESS"));
	
					Element ordinal = doc.createElement("ordinal");
					ordinal.appendChild(doc.createTextNode("0"));
	
					Element color = doc.createElement("color");
					color.appendChild(doc.createTextNode("BLUE"));
	
					Element completeBuild = doc.createElement("completeBuild");
					completeBuild.appendChild(doc.createTextNode("true"));
	
					threshold.appendChild(name);
					threshold.appendChild(ordinal);
					threshold.appendChild(color);
					threshold.appendChild(completeBuild);
	
					buildTrigger.appendChild(childProjects);
					buildTrigger.appendChild(threshold);
	
					publisher.appendChild(buildTrigger);
				}
				else
				{
					doc=XMLUtilities.setTagText(doc,"childProjects",childProjectString);
				}
	
				StringWriter sw = new StringWriter(); 
				Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
				serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
				String st=sw.toString(); 
				logger.debug("Converted the xml to string :"+st);
	
				/*	doc.getDocumentElement().normalize();
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				DOMSource xmlSource = new DOMSource(doc);
				StreamResult xmlResult = new StreamResult(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+moduleName+"/config.xml"); 
				transformer.transform(xmlSource, xmlResult);*/
	
				//send request to update jenkins job
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
	
				//read response
				StringBuilder sb = new StringBuilder(); 
				logger.debug("Response code is :"+con.getResponseCode());
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
					logger.debug("Received Response :" + sb.toString());  
	
	                //commenting reload
					//reloadJenkins();
				}
				else
				{
					logger.error("Error while setting child project in module");
					throw new Exception("Error while updating Job");
				}
	
				//	reloadJenkins();
			}
		}
		else
		{
			logger.error("Error while setting child project in module : Module does not exist");
			throw new Exception("Module does not Exist");
		}

	}

	//function to set created job in downstream projects of main job in jenkins
	public void setChildProjectsInMainJob(String moduleSubJobOrder ,String moduleName) throws Exception
	{
		logger.info("Inside function setChildProjectsInMainJob");
		logger.debug("Received object parameter moduleName :"+moduleName+" and submoduleSet");

		String encodedJobName=moduleName.replaceAll(" ", "%20");
		String jobConfig=checkJenkinsJobExist(encodedJobName);
		if(jobConfig!=null)
		{	
			Document doc=XMLUtilities.convertStringToDocument(jobConfig);
			//Document doc = XMLUtilities.getDocumentFromXML(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+moduleName+"/config.xml");
			Node publisher = doc.getElementsByTagName("publishers").item(0);

			if(!publisher.hasChildNodes())
			{
				Element buildTrigger = doc.createElement("hudson.tasks.BuildTrigger");
				Element childProjects = doc.createElement("childProjects");				
				childProjects.appendChild(doc.createTextNode(moduleSubJobOrder));
				Element threshold = doc.createElement("threshold");
				Element name = doc.createElement("name");
				name.appendChild(doc.createTextNode("SUCCESS"));

				Element ordinal = doc.createElement("ordinal");
				ordinal.appendChild(doc.createTextNode("0"));

				Element color = doc.createElement("color");
				color.appendChild(doc.createTextNode("BLUE"));

				Element completeBuild = doc.createElement("completeBuild");
				completeBuild.appendChild(doc.createTextNode("true"));

				threshold.appendChild(name);
				threshold.appendChild(ordinal);
				threshold.appendChild(color);
				threshold.appendChild(completeBuild);

				buildTrigger.appendChild(childProjects);
				buildTrigger.appendChild(threshold);

				publisher.appendChild(buildTrigger);
			}
			else
			{
				doc=XMLUtilities.setTagText(doc,"childProjects",moduleSubJobOrder);
			}

			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			logger.debug("Converted the xml to string :"+st);

			/*doc.getDocumentElement().normalize();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource xmlSource = new DOMSource(doc);
			StreamResult xmlResult = new StreamResult(GlobalConstants.JENKINS_WORKSPACE+"/jobs/"+moduleName+"/config.xml"); 
			transformer.transform(xmlSource, xmlResult);
			 */
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
			logger.debug(con.getResponseCode());
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
				logger.debug("Received Response :" + sb.toString());  

				//commenting reload
				//reloadJenkins();
			}
			else
			{
				logger.error("Error while setting child project in module");
				throw new Exception("Error while updating Job");
			}


			//reloadJenkins();
		}
		else
		{
			logger.error("Error while setting child project in module : Module does not exist");
			throw new Exception("Module does not Exist");
		}

	}

	//function to trigger build in jenkins
	public String buildJob(String jobName) throws Exception
	{
		logger.info("Inside function buildJob");
		logger.debug("Received object parameter jobName :"+jobName);
		String encodedJobName=jobName.replaceAll(" ", "%20");
		if(checkJenkinsJobExist(encodedJobName)!=null)
		{
			//send post request for creating Jenkins job
			try
			{
				URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedJobName+"/build");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestProperty("Accept", "text/xml");
				con.setRequestMethod("POST");

				// reading the response


				logger.info("Response Code : " + con.getResponseCode());
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				logger.info("Response is "+response.toString());
				logger.info("Returning result from buildJob");
				return response.toString();
			}
			catch(Exception e)
			{
				logger.error("Error while building job :"+jobName);
				throw new Exception("Jenkins build not started");
			}
		}
		else
		{
			logger.error("Error in buildJob : Job does not exist");
			throw new Exception("Job does not exist");
		}
	}



	//function to reload config files from jenkins workspace
	public void reloadJenkins() throws Exception
	{
		logger.info("Inside function reloadJenkins");

		try
		{

			URL reloadUrl = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/reload");
			logger.debug("URL for reload :"+reloadUrl);
			HttpURLConnection con = (HttpURLConnection) reloadUrl.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write("Reload");
			wr.flush();
			wr.close();

			// reading the response
			/*InputStreamReader reader = new InputStreamReader( con.getInputStream() );
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[ 2048 ];
			int num;
			while ( -1 != (num=reader.read( cbuf )))
			{
				buf.append( cbuf, 0, num );
			}
			String result = buf.toString();*/

			StringBuilder sb = new StringBuilder(); 
			logger.debug(con.getResponseCode());
			int HttpResult = con.getResponseCode(); 
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;  
				while ((line = br.readLine()) != null) {  
					sb.append(line + "\n");  
				}
				br.close();
				logger.debug("Received Response :" + sb.toString());  
			} 
			Thread.sleep(2);
		}
		catch(Exception e)
		{
			logger.error("Reload exception");
			Thread.sleep(2);
		}
	}


	//function to check if job exists in jenkins. If exists return config file else return null 
	String checkJenkinsJobExist(String jobName) throws Exception
	{
		logger.info("Inside function checkJenkinsJobExist");
		logger.debug("Received object parameter jobName :"+jobName);
		String inputLine;
		String encodedJobName=jobName.replaceAll(" ", "%20");
		logger.info("Encoded Job is "+encodedJobName);
		URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedJobName+"/config.xml");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept", "text/xml");
		con.setRequestMethod("GET");

		logger.debug("Response Code : " + con.getResponseCode());
		if(con.getResponseCode()!=200)
		{
			logger.info("Job does not exist");
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		logger.debug("Response is :"+response.toString());
		String output=response.toString();
		logger.debug("Returning config.xml as string for job :"+jobName);
		return output;
	}

	//function to find the next build number for a jenkins job
	public int fetchNextBuildNumber(String jobName) throws Exception
	{
		logger.info("Inside function fetchNextBuildNumber");
		logger.debug("Received object parameter jobName :"+jobName);
		String inputLine;
		int lastBuildNumber=-1;
		int nextBuildNumber=0;
		String encodedJobName=jobName.replaceAll(" ", "%20");
		URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedJobName+"/lastBuild/api/json");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept", "text/json");
		con.setRequestMethod("GET");

		if(con.getResponseCode()==404)
		{
			return 1;
		}
		if(con.getResponseCode()!=200)
		{
			return 0;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		logger.debug("Response is :"+response.toString());
		String output=response.toString();

		JSONParser parser = new JSONParser();
		JSONObject nextBuildNumberJson=(JSONObject) parser.parse(output);
		lastBuildNumber=Integer.parseInt(nextBuildNumberJson.get("number").toString());		
		logger.debug("Returning the last build number :"+lastBuildNumber);
		nextBuildNumber=lastBuildNumber+1;
		logger.debug("Returning the next build number :"+nextBuildNumber);
		return nextBuildNumber;
	}

	public int fetchLastBuildNumber(String jobName) throws Exception
	{
		logger.info("Inside function fetchNextBuildNumber");
		logger.debug("Received object parameter jobName :"+jobName);
		String inputLine;
		int lastBuildNumber=-1;
		int nextBuildNumber=0;
		String encodedJobName=jobName.replaceAll(" ", "%20");
		URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+encodedJobName+"/lastBuild/api/json");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept", "text/json");
		con.setRequestMethod("GET");

		if(con.getResponseCode()==404)
		{
			return 1;
		}
		if(con.getResponseCode()!=200)
		{
			return 0;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		logger.debug("Response is :"+response.toString());
		String output=response.toString();

		JSONParser parser = new JSONParser();
		JSONObject nextBuildNumberJson=(JSONObject) parser.parse(output);
		lastBuildNumber=Integer.parseInt(nextBuildNumberJson.get("number").toString());		
		logger.debug("Returning the last build number :"+lastBuildNumber);
		nextBuildNumber=lastBuildNumber;
		logger.debug("Returning the next build number :"+nextBuildNumber);
		return nextBuildNumber;
	}
	public boolean isJenkinsUp() throws Exception
	{

		URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept", "text/json");
		con.setRequestMethod("GET");

		if(con.getResponseCode()==200)
		{
			return true;
		}

		return false;
	}

	public void createCredential(String username,String password) throws Exception{
		String inputLine;
		URL url=new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/credentials/store/system/domain/_/api/json?depth=1");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "text/json");
		con.setRequestProperty("Accept", "text/json");
		con.setRequestMethod("GET");
		System.out.println("Hello is anybody out there");
		System.out.println("Response Code : " + con.getResponseCode());
		if(con.getResponseCode()!=200)
		{
			return;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		String output=response.toString();
		System.out.println("CREATE CREDENTIALS"+output);

	}

}
