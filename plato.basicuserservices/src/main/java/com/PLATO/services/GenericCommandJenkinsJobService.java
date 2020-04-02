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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.PLATO.constants.GlobalConstants;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkinsParameters;
import com.PLATO.utilities.XMLUtilities;
/*
 *@author 10643380 (Rahul Bhardwaj)
 * 
 * **/
//this class is being created to enhance Reusability 
public class GenericCommandJenkinsJobService  {
	private static final Logger logger=Logger.getLogger(GenericCommandJenkinsJobService.class);
	//create generic doc that will be common to all subjobs with simple command structure, like diat,uft etc
	
	public Document createGenericDoc(ModuleSubJobsJenkins subJob,String reportName) throws Exception{
		logger.debug("in createGenericDoc");
		String encodedJobName=subJob.getSubjob_name().replaceAll(" ", "%20");
		JenkinsServices jenkinsService=new JenkinsServices();
		try {
			if(jenkinsService.checkJenkinsJobExist(encodedJobName)==null) {
				
				HashMap<String,String> parameters=new HashMap<String,String>();
				if(subJob.getModuleSubJobsJenkinsParametersList()!=null) {
					for(ModuleSubJobsJenkinsParameters parameter:subJob.getModuleSubJobsJenkinsParametersList()) {
						parameters.put(parameter.getParameter_key(), parameter.getValue());
					}				
				}
				
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				ClassLoader classLoader=this.getClass().getClassLoader();
				File file=new File(classLoader.getResource("xmlFiles/GenericCommandJenkinsSubJob.xml").getFile());
				Document doc = docBuilder.parse(file);
				/*switch(subJob.getTool_name().toLowerCase()) {
				case "diat":
					file=new File(classLoader.getResource("xmlFiles/jobDiatConfig.xml").getFile());
					doc = docBuilder.parse(file);
					break;
				case "uft":
				case "hp qtp":
					file=new File(classLoader.getResource("xmlFiles/jobUftConfig.xml").getFile());
					doc = docBuilder.parse(file);
					doc=XMLUtilities.setTagText(doc,"artifacts" ,subJob.getReport_path());
					break;
				
				}*/
				doc=XMLUtilities.setTagText(doc,"command" ,subJob.getCommand_to_execute());
				doc=XMLUtilities.setTagText(doc,"description" ,subJob.getSubjob_description());
				doc=XMLUtilities.setTagText(doc,"assignedNode" ,subJob.getNodeMaster().getNode_name());
				doc=XMLUtilities.setTagText(doc,"sourceFolderPath" ,subJob.getReport_path());
				
				if(parameters.containsKey("repo_manager") && parameters.get("repo_manager")!=null) {
					if(parameters.get("repo_manager").equalsIgnoreCase("svn")) {
						addSvnOptions(doc,parameters);
					}else if(parameters.get("repo_manager").equalsIgnoreCase("git")) {
						addGitOptions(doc,parameters);
					}
				}
				
				if(subJob.getPostbuild_subjob()!=null && !subJob.getPostbuild_subjob().isEmpty() && subJob.getPostbuild_subjob()!="") {
					//doc=XMLUtilities.setTagText(doc, "hudson.tasks.BuildTrigger", subJob.getPostbuild_subjob());
					Element hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element childProjects=(Element)doc.getElementsByTagName("childProjects").item(0);
					childProjects.setTextContent(subJob.getPostbuild_subjob());
				}else {
					Element hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element childProjects=(Element)doc.getElementsByTagName("childProjects").item(0);
					childProjects.setTextContent("");
				}
				if(subJob.getPostBuild_trigger_option()==null) {
					
				}else if(subJob.getPostBuild_trigger_option().trim().equals("trigger even if build is unstable")) {
					Element  hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element threshold=(Element)hudsonTB.getElementsByTagName("threshold").item(0);
					
					Element name=(Element)threshold.getElementsByTagName("name").item(0);
					name.setTextContent("UNSTABLE");
					Element ordinal=(Element)threshold.getElementsByTagName("ordinal").item(0);
					ordinal.setTextContent("1");
					Element color=(Element)threshold.getElementsByTagName("color").item(0);
					color.setTextContent("YELLOW");
				}else if(subJob.getPostBuild_trigger_option().trim().equals("trigger even if build fails")) {
					Element  hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element threshold=(Element)hudsonTB.getElementsByTagName("threshold").item(0);
					
					Element name=(Element)threshold.getElementsByTagName("name").item(0);
					name.setTextContent("FAILURE");
					Element ordinal=(Element)threshold.getElementsByTagName("ordinal").item(0);
					ordinal.setTextContent("2");
					Element color=(Element)threshold.getElementsByTagName("color").item(0);
					color.setTextContent("RED");					
				}
//				subJob.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+subJob.getSubjob_name()+"/"+reportName);
				
				//subJob.setReport_path("C:\\Users\\"+System.getProperty("user.name")+"\\.jenkins\\workspace\\"+subJob.getSubjob_name()+"\\"+reportName);
				//writeSubJobInJenkins(doc,subJob);
				
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
				
				System.out.println("doc is "+ writer.toString());
				return doc;

			}else {
				throw new Exception("Duplicate Module");
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Exception while creating DOC");
		}

	}
	
	//it writes the subJob in jenkins, takes Document and ModuleSubJobsJenkins as parameter
	public void writeSubJobInJenkins(Document doc,ModuleSubJobsJenkins subJob) throws Exception{
		logger.debug("in writeSubJobJenkins");
		try {
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String st=sw.toString(); 
			System.out.println(st);
			
			URL url = new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/createItem?name="+URLEncoder.encode(subJob.getSubjob_name(),"UTF-8"));
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
			
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Exception while creating in Jenkins");
		}
	}
	
	//used for creating updated GenericDoc
	public Document createUpdatedGenericDoc(ModuleSubJobsJenkins subJob,String reportName) throws Exception {
		logger.debug("in createUpdatedGenericDoc");
		String encodedJobName=subJob.getSubjob_name().replaceAll(" ", "%20");
		JenkinsServices jenkinsService=new JenkinsServices();
		try {
			HashMap<String,String> parameters=new HashMap<String,String>();
			if(subJob.getModuleSubJobsJenkinsParametersList()!=null) {
				for(ModuleSubJobsJenkinsParameters parameter:subJob.getModuleSubJobsJenkinsParametersList()) {
					parameters.put(parameter.getParameter_key(), parameter.getValue());
				}				
			}

			String jobConfig=jenkinsService.checkJenkinsJobExist(encodedJobName);
			if(jobConfig!=null) {
				Document doc=XMLUtilities.convertStringToDocument(jobConfig);
				doc=XMLUtilities.setTagText(doc,"command" ,subJob.getCommand_to_execute());
				doc=XMLUtilities.setTagText(doc,"description" ,subJob.getSubjob_description());
				doc=XMLUtilities.setTagText(doc,"assignedNode" ,subJob.getNodeMaster().getNode_name());
				doc=XMLUtilities.setTagText(doc,"sourceFolderPath" ,subJob.getReport_path());
				
				if(parameters.containsKey("repo_manager")&& parameters.get("repo_manager")!=null) {
					if(parameters.get("repo_manager").equalsIgnoreCase("svn")) {
						addSvnOptions(doc,parameters);
					}else if(parameters.get("repo_manager").equalsIgnoreCase("git")) {
						addGitOptions(doc,parameters);
					}
				}
				if(subJob.getPostbuild_subjob()!=null && !subJob.getPostbuild_subjob().isEmpty() && subJob.getPostbuild_subjob()!="") {
					//doc=XMLUtilities.setTagText(doc, "hudson.tasks.BuildTrigger", subJob.getPostbuild_subjob());
					Element hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element childProjects=(Element)doc.getElementsByTagName("childProjects").item(0);
					childProjects.setTextContent(subJob.getPostbuild_subjob());
				}else {
					Element hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element childProjects=(Element)doc.getElementsByTagName("childProjects").item(0);
					childProjects.setTextContent("");
				}
				if(subJob.getPostBuild_trigger_option()==null) {
					
				}else if(subJob.getPostBuild_trigger_option().trim().equals("trigger even if build is unstable")) {
					Element  hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element threshold=(Element)hudsonTB.getElementsByTagName("threshold").item(0);
					
					Element name=(Element)threshold.getElementsByTagName("name").item(0);
					name.setTextContent("UNSTABLE");
					Element ordinal=(Element)threshold.getElementsByTagName("ordinal").item(0);
					ordinal.setTextContent("1");
					Element color=(Element)threshold.getElementsByTagName("color").item(0);
					color.setTextContent("YELLOW");
				}else if(subJob.getPostBuild_trigger_option().trim().equals("trigger even if build fails")) {
					Element  hudsonTB=(Element)doc.getElementsByTagName("hudson.tasks.BuildTrigger").item(0);
					Element threshold=(Element)hudsonTB.getElementsByTagName("threshold").item(0);
					
					Element name=(Element)threshold.getElementsByTagName("name").item(0);
					name.setTextContent("FAILURE");
					Element ordinal=(Element)threshold.getElementsByTagName("ordinal").item(0);
					ordinal.setTextContent("2");
					Element color=(Element)threshold.getElementsByTagName("color").item(0);
					color.setTextContent("RED");					
				}
				
				//subJob.setReport_path(GlobalConstants.JENKINS_HOME+"/workspace/"+subJob.getSubjob_name()+"/"+reportName);
				
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
				
				System.out.println("doc is "+ writer.toString());
				
				return doc;
			}else {
				throw new Exception("No  Module");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Exception while creating updated DOC");			
		}
	}
	public void writeUpdatedSubJobInJenkins(Document doc,ModuleSubJobsJenkins subJob) throws Exception{
		try {
			String encodedJobName=subJob.getSubjob_name().replaceAll(" ", "%20");
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
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Exception while updating in Jenkins");
		}

	}
	
	private void addSvnOptions(Document doc,HashMap<String,String> parameters) {
		Element project=(Element)doc.getElementsByTagName("project").item(0);
		Element scm=doc.createElement("scm");
		project.appendChild(scm);
		scm.setAttribute("class", "hudson.scm.SubversionSCM");
		Element locations=doc.createElement("locations");
		scm.appendChild(locations);
		Element subversionSCM_ModuleLocation=doc.createElement("hudson.scm.SubversionSCM_-ModuleLocation");
		locations.appendChild(subversionSCM_ModuleLocation);
		
		Element remote=doc.createElement("remote");
		String remoteUrl=parameters.get("repo_path");
		remote.setTextContent(remoteUrl);
		subversionSCM_ModuleLocation.appendChild(remote);
		
		Element credentialsId=doc.createElement("credentialsId");
		String credentialsIdString=parameters.get("repo_credentials");
		credentialsId.setTextContent(credentialsIdString);
		subversionSCM_ModuleLocation.appendChild(credentialsId);
		
		Element local=doc.createElement("local");
		local.setTextContent(".");
		subversionSCM_ModuleLocation.appendChild(local);
		
		Element depthOption=doc.createElement("depthOption");
		depthOption.setTextContent("infinity");
		subversionSCM_ModuleLocation.appendChild(depthOption);
		
		Element ignoreExternalsOption=doc.createElement("ignoreExternalsOption");
		ignoreExternalsOption.setTextContent("true");
		subversionSCM_ModuleLocation.appendChild(ignoreExternalsOption);
		
		Element excludedRegions=doc.createElement("excludedRegions");
		scm.appendChild(excludedRegions);
		
		Element includedRegions=doc.createElement("includedRegions");
		scm.appendChild(includedRegions);
		
		Element excludedUsers=doc.createElement("excludedUsers");
		scm.appendChild(excludedUsers);
		
		Element excludedRevprop=doc.createElement("excludedRevprop");
		scm.appendChild(excludedRevprop);
		
		Element excludedCommitMessages=doc.createElement("excludedCommitMessages");
		scm.appendChild(excludedCommitMessages);
		
		Element workspaceUpdater=doc.createElement("workspaceUpdater");
		scm.appendChild(workspaceUpdater);
		workspaceUpdater.setAttribute("class", "hudson.scm.subversion.UpdateUpdater");
		
		Element ignoreDirPropChanges=doc.createElement("ignoreDirPropChanges");
		scm.appendChild(ignoreDirPropChanges);
		ignoreDirPropChanges.setTextContent("false");
		
		Element filterChangelog=doc.createElement("filterChangelog");
		scm.appendChild(filterChangelog);
		filterChangelog.setTextContent("false");
		
		Element quietOperation=doc.createElement("quietOperation");
		scm.appendChild(quietOperation);
		quietOperation.setTextContent("true");
	}
	
	private void addGitOptions(Document doc,HashMap<String,String> parameters) {
		Element project=(Element)doc.getElementsByTagName("project").item(0);
		System.out.println(">json >> "+parameters.toString());
//		Element triggers=doc.createElement("triggers");
//		project.appendChild(triggers);
//		
//		Element timerTrigger=doc.createElement("hudson.triggers.TimerTrigger");
//		triggers.appendChild(timerTrigger);
//		Element specTimerTrigger=doc.createElement("spec");
//		timerTrigger.appendChild(specTimerTrigger);
//		specTimerTrigger.setTextContent(parameters.get("timer_trigger"));
//		
//		Element scmTrigger=doc.createElement("hudson.triggers.SCMTrigger");
//		triggers.appendChild(scmTrigger);
//		Element specScmTrigger=doc.createElement("spec");
//		scmTrigger.appendChild(specScmTrigger);
//		specScmTrigger.setTextContent(parameters.get("scm_timer"));
		
		
		Element scm=doc.createElement("scm");
		project.appendChild(scm);
		scm.setAttribute("class", "hudson.plugins.git.GitSCM");
		scm.setAttribute("plugin", "git@3.7.0");
		
		Element configVersion=doc.createElement("configVersion");
		configVersion.setTextContent("2");
		scm.appendChild(configVersion);
		
		Element userRemoteConfigs=doc.createElement("userRemoteConfigs");
		scm.appendChild(userRemoteConfigs);
		
		Element UserRemoteConfig=doc.createElement("hudson.plugins.git.UserRemoteConfig");
		userRemoteConfigs.appendChild(UserRemoteConfig);
		
		Element name=doc.createElement("name");
//		String nameValue=parameters.get("");
//		name.setTextContent(nameValue);
		UserRemoteConfig.appendChild(name);
		
		Element refspec=doc.createElement("refspec");
//		String refspecValue=parameters.get("");
//		name.setTextContent(refspecValue);
		UserRemoteConfig.appendChild(refspec);
		
		Element url=doc.createElement("url");
		String urlValue=parameters.get("repo_path");
		url.setTextContent(urlValue);
		UserRemoteConfig.appendChild(url);
		
		Element credentialsId=doc.createElement("credentialsId");
		String credentialsIdValue=parameters.get("repo_credentials");
		credentialsId.setTextContent(credentialsIdValue);
		UserRemoteConfig.appendChild(credentialsId);
		
		Element branches=doc.createElement("branches");
		scm.appendChild(branches);
		
		Element BranchSpec=doc.createElement("hudson.plugins.git.BranchSpec");
		branches.appendChild(BranchSpec);
		
		Element branchName=doc.createElement("name");
		String branchNameValue=parameters.get("branch_name");
		branchName.setTextContent(branchNameValue);
		BranchSpec.appendChild(branchName);
		
		Element doGenerateSubmoduleConfigurations=doc.createElement("doGenerateSubmoduleConfigurations");
		doGenerateSubmoduleConfigurations.setTextContent("false");
		scm.appendChild(doGenerateSubmoduleConfigurations);
		
		Element browser=doc.createElement("browser");
		
		browser.setAttribute("class", "hudson.plugins.git.browser."+parameters.get("git_browser_type"));
		scm.appendChild(browser);
		if(parameters.get("git_browser_type").equalsIgnoreCase("GitLab")) {
			Element browserUrl=doc.createElement("url");
			browserUrl.setTextContent(parameters.get("browser_url"));
			browser.appendChild(browserUrl);
			
			Element version=doc.createElement("version");
			version.setTextContent(parameters.get("browser_version"));
			browser.appendChild(version);
		}
		
		Element submoduleCfg=doc.createElement("submoduleCfg");
		submoduleCfg.setAttribute("class","list");
		scm.appendChild(submoduleCfg);
	}
	
}
