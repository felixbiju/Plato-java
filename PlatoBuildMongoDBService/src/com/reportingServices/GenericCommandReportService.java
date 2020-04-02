package com.reportingServices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongo.constants.ConstantQueries;
import com.mongo.dao.GenericDao;
import com.mongo.entities.ModuleJobsJenkins;
import com.mongo.entities.ModuleSubJobsJenkins;
import com.mongo.singletons.GenericDaoSingleton;
import com.adapters.AppiumAdapter;
import com.adapters.BURPAdapter;
import com.adapters.CucumberAdapter;
import com.adapters.DTFAdapter;
import com.adapters.ETDMAdapter;
import com.adapters.GDPRAdapter;
import com.adapters.GenericURLApdapter;
import com.adapters.IbmAppScanAdapter;
import com.adapters.JMeterAdapter;
import com.adapters.JUnitAdapter;
import com.adapters.JiraAdapterNew;
import com.adapters.KatalonAdapter;
import com.adapters.OwaspZapAdapter;
import com.adapters.PdfDiffAdapter;
import com.adapters.PerformanceCenterAdapter;
import com.adapters.QtpAdapter;
import com.adapters.QtpAdapterTMNAS;
import com.adapters.ScaniaToolAdapter;
import com.adapters.SeleniumAdapter;
import com.adapters.SeleniumFramework;
import com.adapters.TacAdapter;
import com.adapters.WebServiceAdapter;
import com.adapters.XcheckAdapter;
import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;
import com.plato.errorHandler.ErrorHandler;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class GenericCommandReportService {
	GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	private static final Logger logger=Logger.getLogger(GenericCommandReportService.class);
	
	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status="Passed";
	private String jenkinsUrl;
	private String toolName;
	private String reportPath;
	private boolean lastCheckpoint=false;
	
	String jenkinsConsoleOutput="";
	
	public GenericCommandReportService(int buildHistoryId, int buildNumber, String jobName,String jenkinsUrl,String toolName,String reportPath) {
		// TODO Auto-generated constructor stub
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
		this.jenkinsUrl=jenkinsUrl;
		this.toolName=toolName;
		try {
			reportPath=URLDecoder.decode(reportPath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ErrorHandler errorHandler=new ErrorHandler();
			errorHandler.handleUnanticipatedException(e, new JSONObject());
		}
		this.reportPath=reportPath;
	}
	
	public synchronized String readGenericCommandJenkinsTotalResponse() throws ParseException, InterruptedException {
		
		ReadJenkinsConsole readJenkinsConsole=new ReadJenkinsConsole();

		//int count =0;s
		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		System.out.println("Reading generic command Execution from Jenkins Console ");
		System.out.println("report path is "+reportPath);
		for(;;){
			//count++;
			Thread.sleep(10000);
			jenkinsConsoleOutput=readJenkinsConsole.readJenkinsConsole(buildNumber,jobName,jenkinsUrl);
			try {
				updateCheckpoints(jenkinsConsoleOutput);	
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("after updateStatus jenkinsConsoleOutput is "+jenkinsConsoleOutput);
			//if(count==5){
			if(jenkinsConsoleOutput.contains("Finished: SUCCESS")|| jenkinsConsoleOutput.contains("Finished: FAILURE") || jenkinsConsoleOutput.contains("Finished: ABORTED") || jenkinsConsoleOutput.contains("Finished: UNSTABLE") || jenkinsConsoleOutput.contains("FATAL: null")){
				System.out.println("jenkins console output contains********  Execution Completed ");
				finalPlatoJsonObj=readGenericCommandJenkinsConsoleAndUpdateStatus2(jenkinsConsoleOutput);
				if(!jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
					status="false";
				}
				break;
			}else if(jenkinsConsoleOutput==null || jenkinsConsoleOutput.equals("")) {
					HashMap keyvalueMap=new HashMap();
					keyvalueMap.put("subJobName",jobName );
					try {
						ModuleSubJobsJenkins subjob=(ModuleSubJobsJenkins)genericDao.findUniqueByQuery(ConstantQueries.GETSUBMODULEBYNAME, keyvalueMap);
						Set<ModuleSubJobsJenkins> subjobList=subjob.getModuleJobsJenkins().getModuleSubJobsJenkins();
						for(ModuleSubJobsJenkins subjob1 : subjobList)
						{
							String postBuildArray[]=subjob1.getPostbuild_subjob().split(",");
							for(String s:postBuildArray)
							{
								if((s.equals(jobName))&&(subjob1.getPostBuild_trigger_option().equalsIgnoreCase("trigger only if build is stable")))
										{
											if(checkPredecessorStatusInMongo(subjob1.getSubjob_name()))
											{
												jenkinsConsoleOutput="predecessor fail hua"
														+ "FINISHED: FAILED";
												finalPlatoJsonObj=readGenericCommandJenkinsConsoleAndUpdateStatus2(jenkinsConsoleOutput);
												if(!jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
													status="false";
												}
											}
											break;
										}
							}
							if(jenkinsConsoleOutput.contains("predecessor fail hua"))
								break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					else{
						updateStatusInMongo(jenkinsConsoleOutput);
					}
				if(jenkinsConsoleOutput.contains("FINISHED: FAILED"))
					break;
				}
			
			//return finalPlatoJsonObj;
		
		
		logger.info("COMPLETED reading Execution from Jenkins Console ");
		//return finalPlatoJsonObj;
		
		if(status.equals("false")){
			response="Failed";
		}else{
			response="Passed";
		}
		
		return response;
		//return jenkinsConsoleOutput;
	
	}
	
	
//	public JSONObject readGenericCommandJenkinsConsoleAndUpdateStatus(String jenkinsConsoleOutput)  {
//		String consoleOutput=jenkinsConsoleOutput;
//		if(!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))){
//			//String jenkinsOutput[]=jenkinsConsoleOutput.split(this.jenkinsConsoleOutput);
//			if(jenkinsConsoleOutput.contains(this.jenkinsConsoleOutput.trim())){
//				logger.info("Removing previous console");
//				jenkinsConsoleOutput=jenkinsConsoleOutput.replace(this.jenkinsConsoleOutput.trim(), "");
//			}
//			/*jenkinsConsoleOutput=jenkinsOutput[1];
//		System.out.println("jenkins output 0 ***"+jenkinsOutput[0]);
//		System.out.println("jenkins output 1 ***"+jenkinsOutput[1]);*/
//		}else{
//			logger.info("this.jenkinsConsoleOutput is blank");
//		}
//		
//		JSONObject jsonGenericCommandReport=new JSONObject();
//		JSONObject finalPlatoJsonObj=new JSONObject();
//		JSONParser parser = new JSONParser();
//		
//		//JSONObject finalPlatoJsonObj = (JSONObject) parser.parse(jenkinsConsoleOutput);
//		
//		try {
//			//finalPlatoJsonObj = (JSONObject) parser.parse(mongoDBOperations.fetchJsonFromMongoDB(buildHistoryId));
//			
//			//------------------------------changed for mongo read count
//			finalPlatoJsonObj=null;
//			String finalPlatoJsonString=null;
//			while(finalPlatoJsonString==null)
//			{
//				logger.debug("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
//				finalPlatoJsonString=MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
//				
//			
//			}
//			logger.debug("Retrived data : " +finalPlatoJsonString);
//			finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
//			//------------------------------------------end
//			finalPlatoJsonObj.replace("readCount","1","0");
//			
//			System.out.println("jsonGenericCommandReport is "+jsonGenericCommandReport);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		JSONObject obj1= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
//		JSONObject obj2= (JSONObject) obj1.get("LiveBuildConsole");
//		logger.info("obj1 before is "+obj1);
//		System.out.println("obj1 before is "+obj1);
//		JSONObject obj3= (JSONObject) obj2.get("application_under_test");
//		JSONArray obj4= (JSONArray) obj3.get("generic_tools");
//		logger.info("obj1 before is "+obj1);
//		System.out.println("obj1 before is "+obj1);
//		/*String diatStatus=(String)obj2.get("status");
//		if(diatStatus==null) {
//			diatStatus=" ";
//		}
//		if(diatStatus.equalsIgnoreCase("Finished: SUCCESS")){
//			
//			obj2.replace("Build", diatStatus, "Success");
//		}else{
//			
//			if(diatStatus.equalsIgnoreCase("Finished: FAILURE")){
//				obj2.replace("Build", diatStatus, "Failed");		
//			}
//			
//			if(status){
//				status=false;
//				}	
//			
//		}*/
//		String status="";
//		this.jenkinsConsoleOutput=consoleOutput;
//		if(jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
//			status="SUCCESS";
//			
//		}else {
//			status="FAILED";
//		}
//		System.out.println("obj4 size is "+obj4.size());
//		for(int i=0;i<obj4.size();i++) {
//			JSONObject temp=(JSONObject)obj4.get(i);
//			if(temp.get("tool_name").equals(jobName)) {
//				if(status=="SUCCESS") {
//					temp.put("tool_status",status);
//					temp.put("build_number",buildNumber);
//					temp.put("module_kdt",jsonGenericCommandReport);
//					
//					if(toolName.equalsIgnoreCase("dtf")) {
//						DTFAdapter dtfAdapter=new DTFAdapter();
//						org.json.JSONArray arr=dtfAdapter.GetJsonArray(reportPath);
//						temp.put("report",arr);
//					}else if(toolName.equalsIgnoreCase("uipath")) {
//						
//					}else if(toolName.equalsIgnoreCase("owasp_zap")) {
//						OwaspZapAdapter owaspZapAdapter=new OwaspZapAdapter();
//						JSONArray toolReport=owaspZapAdapter.getToolReportJsonArray(reportPath);
//					}else if(toolName.equalsIgnoreCase("junit")) {
//						
//					}else if(toolName.equalsIgnoreCase("testng")) {
//						
//					}					
//				}else {
//					temp.put("tool_status",status);
//				}
//				break;
//			}
//		}
//
//		//obj5.put(jsonGenericCommandReport);
//		System.out.println("obj1 is "+obj1);
//		
//		logger.info("Updating the MongoDB template");
//		logger.debug(finalPlatoJsonObj);
//		logger.debug("Updating template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
//		MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
//		logger.debug("Updated Status of DIAT in MongoDB for build history id :"+buildHistoryId+ " and job name :  "+jobName);
//
//		
//		return finalPlatoJsonObj;
//		}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public JSONObject readGenericCommandJenkinsConsoleAndUpdateStatus2(String jenkinsConsoleOutput) {
		System.out.println("inside readGenericCommandJenkinsConsoleAndUpdateStatus2");
		logger.info("inside readGenericCommandJenkinsConsoleAndUpdateStatus2");
		String consoleOutput=jenkinsConsoleOutput;
		if(!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))){
			//String jenkinsOutput[]=jenkinsConsoleOutput.split(this.jenkinsConsoleOutput);
			if(jenkinsConsoleOutput.contains(this.jenkinsConsoleOutput.trim())){
				System.out.println("Removing previous console");
				jenkinsConsoleOutput=jenkinsConsoleOutput.replace(this.jenkinsConsoleOutput.trim(), "");
			}
			/*jenkinsConsoleOutput=jenkinsOutput[1];
		System.out.println("jenkins output 0 ***"+jenkinsOutput[0]);
		System.out.println("jenkins output 1 ***"+jenkinsOutput[1]);*/
		}else{
			logger.info("this.jenkinsConsoleOutput is blank");
			System.out.println("this.jenkinsConsoleOutput is blank");
		}
		System.out.println("");
		
		JSONObject jsonGenericCommandReport=new JSONObject();
		JSONObject finalPlatoJsonObj=new JSONObject();
		JSONParser parser = new JSONParser();
		
		try {
			finalPlatoJsonObj=null;
			String finalPlatoJsonString=null;
			while(finalPlatoJsonString==null)
			{	
				logger.info("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				System.out.println("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				finalPlatoJsonString=MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
				
			
			}
			logger.info("Retrived data : " +finalPlatoJsonString);
			System.out.println("Retrived data : " +finalPlatoJsonString);
			finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
			//------------------------------------------end
			finalPlatoJsonObj.replace("readCount","1","0");
			JSONObject buildHistory= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
			System.out.println("buildHistoryId "+buildHistoryId+" buildHistory "+buildHistory.toJSONString());
			JSONObject liveBuildConsole=(JSONObject) buildHistory.get("LiveBuildConsole");
			JSONArray liveBuildTools=(JSONArray)liveBuildConsole.get("tools");
			String status="";
			this.jenkinsConsoleOutput=consoleOutput;
			logger.info("readGenericCommandJenkinsConsoleAndUpdateStatus2 : jenkinsConsoleOutput is " +jenkinsConsoleOutput);
			System.out.println("readGenericCommandJenkinsConsoleAndUpdateStatus2 : jenkinsConsoleOutput is " +jenkinsConsoleOutput);
			logger.info("readGenericCommandJenkinsConsoleAndUpdateStatus2 : consoleOutput is " +consoleOutput);
			System.out.println("readGenericCommandJenkinsConsoleAndUpdateStatus2 : consoleOutput is " +consoleOutput);
			if(consoleOutput.contains("Finished: SUCCESS")) {
				status="PASSED";
			}else {
				status="FAILED";
			}
			System.out.println("1.status is "+status);
			//updating live build console
			for(int i=0;i<liveBuildTools.size();i++) {
				JSONObject temp=(JSONObject)liveBuildTools.get(i);
				if(temp.get("tool_name").equals(jobName)) {
					temp.put("tool_status",status);
					boolean isLadyBugChecked=(boolean)temp.get("isLadybugChecked");
					if(isLadyBugChecked==true)
						temp.put("ladyBugNotification","success");
				}
			}
			logger.info("status is "+status);
			logger.info("toolname is "+toolName);
			System.out.println("status is "+status);
			System.out.println("toolname is "+toolName);
			if(status.equalsIgnoreCase("PASSED")) {
				System.out.println("it has succeeded");
				if(toolName.equalsIgnoreCase("junit")||toolName.equalsIgnoreCase("testng")) {
					System.out.println("junit or testng");
					logger.info("junit or testng");
					//if tool comes under build
					buildReport(buildHistory,status);
					
				}else if(toolName.equalsIgnoreCase("uipath") || toolName.equalsIgnoreCase("hp_qtp")||toolName.equalsIgnoreCase("selenium")||toolName.equalsIgnoreCase("x_check")||toolName.equalsIgnoreCase("selenium_framework")||toolName.equalsIgnoreCase(""
						+ "_FRAMEWORK")||toolName.equalsIgnoreCase("katalon")||
						toolName.equalsIgnoreCase("tac")||toolName.equalsIgnoreCase("jpack") ||toolName.equalsIgnoreCase("Worksoft_Certify")||toolName.equalsIgnoreCase("Jsure")||toolName.equalsIgnoreCase("WebService")||toolName.equalsIgnoreCase("jira")||toolName.equalsIgnoreCase("Appium")||toolName.equalsIgnoreCase("scania_tool")||toolName.equalsIgnoreCase("cucumber")||toolName.equalsIgnoreCase("openscript")) { 
					//if tool comes under automation
					logger.info("automation tool");
					automationReport(buildHistory,status);
				}else if(toolName.equalsIgnoreCase("dtf")||toolName.equalsIgnoreCase("radar") || toolName.equalsIgnoreCase("pdf") || toolName.equalsIgnoreCase("pdf_diff") ||toolName.equalsIgnoreCase("idiscover") || toolName.equalsIgnoreCase("ETDM")) {
					System.out.println("dtf job :D");
					//if tool comes under data testing
					dataTestingReport(buildHistory,status);
				}else if(toolName.equalsIgnoreCase("jmeter") ||toolName.equalsIgnoreCase("loadrunner")||toolName.equalsIgnoreCase("vykon")||toolName.equalsIgnoreCase("nyx")|| toolName.equalsIgnoreCase("performance_center")) {
					//if tool comes under performance
					performanceReport(buildHistory,status);
				}else if(toolName.equalsIgnoreCase("owasp_zap")|| toolName.equalsIgnoreCase("ibm_app_scan") || toolName.equalsIgnoreCase("burp")) {
					//if tool comes under security
					securityReport(buildHistory,status);
				}
				/*else if(toolName.equalsIgnoreCase("jpack")) {
					//if tool comes under security
					buildCategoryReport(buildHistory,status);
				}*/
				/*else if(toolName.equalsIgnoreCase("idiscover")){
					gdprReport(buildHistory,status);
				}*/
					/*else if(toolName.equalsIgnoreCase("")) {
				}
					//if tool comes under notification
					notificationReport(buildHistory,status);
				}*/
				//Imortant this is just temporary
			}else {
				if(toolName.equalsIgnoreCase("junit")||toolName.equalsIgnoreCase("testng")) {
					System.out.println("junit or testng");
					//if tool comes under build
					buildReport(buildHistory,status);
					
				}
			}
			
			System.out.println("readGenericCommandJenkinsConsoleAndUpdateStatus2:buildHistory "+buildHistory.toJSONString());
			System.out.println("readGenericCommandJenkinsConsoleAndUpdateStatus2:finalPlatoJsonObj "+finalPlatoJsonObj.toJSONString());
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
					

		}catch(Exception e) {
			e.printStackTrace();
			logger.debug("readcount changed to 1 and updated in mongo");
			finalPlatoJsonObj.replace("readCount","1","0");
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
		}
		return finalPlatoJsonObj;
	}
	
	//helper functions
	private void buildReport(JSONObject buildHistory,String status) throws Exception {
		JSONObject build=(JSONObject)buildHistory.get("Build");
		JSONObject unitTesting=new JSONObject();
		if(toolName.equalsIgnoreCase("junit")) {
			System.out.println(" buildReport: got junit ");
			JUnitAdapter jUnitAdapter=new JUnitAdapter();
			unitTesting.put("j_unit",jUnitAdapter.getJsonObject(reportPath) );
			build.put("unit_testing",unitTesting);
			}else if(toolName.equalsIgnoreCase("testng")) {
		
		}	
	}

	
	/*@SuppressWarnings("unchecked")
	private void buildCategoryReport(JSONObject buildHistory,String status) {
		JSONObject buildCategoryTesting=(JSONObject)buildHistory.get("Build");
		JSONArray tools=(JSONArray)buildCategoryTesting.get("Tools");
		System.out.println("buildCategory testing");
		System.out.println("toolName is "+toolName);
		System.out.println("jobName is "+jobName);
		
			for(int i=0;i<tools.size();i++) {
				JSONObject temp=(JSONObject)tools.get(i);
				try {
					reportPath=URLDecoder.decode(reportPath,"UTF-8");
					System.out.println(">>>>> Report path is"+reportPath);
					if(temp.get("tool_name").equals(jobName)) {
						if(toolName.equalsIgnoreCase("jpack")) {
							logger.info("jpack");
							
//							QtpAdapterTMNAS qtpAdapter = new QtpAdapterTMNAS();
//							JSONArray toolReport=qtpAdapter.getReport_format_3(reportPath,jobName,buildNumber);
//							temp.put("ToolReport",toolReport);
							GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
							logger.info(reportPath+""+jobName);
							System.out.println(reportPath+""+jobName);
							JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
							temp.put("ToolReport",toolReport);
						}
					}
				}catch(NullPointerException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNullPointerException(e,temp);
					break;
				}catch(SecurityException  | IOException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleSecurityException(e,temp);
					break;
				}catch(NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException |InvalidFormatException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNumberFormatException(e,temp);
					break;
				}catch(Exception e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleUnanticipatedException(e,temp);
					break;
				}				
			
		}
			System.out.println("buildCategoryReport:buildHistory "+buildHistory.toJSONString());
		
	}
			*/
	@SuppressWarnings("unchecked")
	private void automationReport(JSONObject buildHistory,String status) {
		JSONObject automationTesting=(JSONObject)buildHistory.get("AutomationTesting");
		JSONArray tools=(JSONArray)automationTesting.get("Tools");
		System.out.println("automation testing");
		System.out.println("toolName is "+toolName);
		System.out.println("jobName is "+jobName);
		
			for(int i=0;i<tools.size();i++) {
				JSONObject temp=(JSONObject)tools.get(i);
				try {
					reportPath=URLDecoder.decode(reportPath,"UTF-8");
					System.out.println(">>>>> Report path is"+reportPath);
					if(temp.get("tool_name").equals(jobName)) {
						if(toolName.equalsIgnoreCase("uipath")) {
							
						}else if(toolName.equalsIgnoreCase("hp_qtp")) {
							logger.info("automation job is hp_qtp");
							logger.info("automation job is hp_qtp");
//							QtpAdapterTMNAS qtpAdapter = new QtpAdapterTMNAS();
//							JSONArray toolReport=qtpAdapter.getReport_format_3(reportPath,jobName,buildNumber);
//							temp.put("ToolReport",toolReport);
							QtpAdapter qtpAdapter=new QtpAdapter();
							JSONArray toolReport=qtpAdapter.getQtpReport(reportPath,jobName,buildNumber);
							temp.put("ToolReport",toolReport);
						}else if(toolName.equalsIgnoreCase("selenium")) {
							System.out.println("its selenium");
							SeleniumAdapter selAdapter=new SeleniumAdapter();
							JSONArray toolReport = selAdapter.getSeleniumReport(reportPath,jobName,buildNumber);
							temp.put("ToolReport",toolReport);
							
							//for Broadridge
							//GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
							//logger.info(reportPath+""+jobName);
							//System.out.println(reportPath+""+jobName);
							//JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
							//temp.put("ToolReport",toolReport);
						}else if(toolName.equalsIgnoreCase("Appium")){
							System.out.println("its Appium");
							AppiumAdapter appiumAdapter = new AppiumAdapter();
							JSONArray toolReport=appiumAdapter.getReport(reportPath,jobName,buildNumber);
							temp.put("ToolReport",toolReport);
						}else if (toolName.equalsIgnoreCase("cucumber")) {
							System.out.println("its Cucumber");
							logger.info("Cucumber tool");
							CucumberAdapter cucumberAdapter = new CucumberAdapter();
							//for adapter
							//JSONArray toolReport = cucumberAdapter.getReport(reportPath,jobName,buildNumber);

							//for showing extend report
							JSONArray toolReport = cucumberAdapter.getExtendReport(reportPath,jobName,buildNumber);
							
							//for Broadridge
							/*GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
							logger.info(reportPath+""+jobName);
							System.out.println(reportPath+""+jobName);
							JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);*/
							temp.put("ToolReport",toolReport);
							
							
							temp.put("ToolReport",toolReport);
						}else if(toolName.equalsIgnoreCase("x_check")) {
							System.out.println("its x-check");
							XcheckAdapter xCheckAdapter=new XcheckAdapter();
							JSONObject toolReportObject=xCheckAdapter.getXcheckReports(reportPath, jobName, buildNumber);
							JSONArray toolReport=new JSONArray();
							toolReport.add(toolReportObject);
							temp.put("ToolReport", toolReport);
						}else if(toolName.equalsIgnoreCase("selenium_framework")||toolName.equalsIgnoreCase("SELENIUM_FRAMEWORK")) {
							System.out.println("its selenium framework");
							SeleniumFramework seleniumFramework=new SeleniumFramework();
							JSONArray toolReport=seleniumFramework.getReports(reportPath, jobName, buildNumber,jenkinsConsoleOutput);
							temp.put("ToolReport", toolReport);
						}else if(toolName.equalsIgnoreCase("katalon")) {

							System.out.println("its katalon");
							KatalonAdapter katalonAdapter=new KatalonAdapter();
							JSONArray toolReportObject=katalonAdapter.getKatalonReports(reportPath, jobName, buildNumber);
							//JSONArray toolReport=new JSONArray();
							temp.put("ToolReport", toolReportObject);
						
							
						}else if(toolName.equalsIgnoreCase("tac")) {
							System.out.println("its TAC");
							TacAdapter tacAdapter=new TacAdapter();
							JSONArray toolReportObject=tacAdapter.getReport(reportPath,jobName,buildNumber);
							temp.put("ToolReport", toolReportObject);
						}else if(toolName.equalsIgnoreCase("WebService")) {
							System.out.println("its WebService Testing");
							WebServiceAdapter webServiceAdapter=new WebServiceAdapter();
							JSONArray toolReportObject=webServiceAdapter.getReport(reportPath,jobName,buildNumber);
							temp.put("ToolReport", toolReportObject);
							JSONObject toolReportObj=(JSONObject) toolReportObject.get(0);
							JSONArray chart_values=(JSONArray) toolReportObj.get("chart_values");
							System.out.println("Chart values:  "+chart_values);
							int fail_count=(int) chart_values.get(1);
							if(fail_count>0) {	
								System.out.println("Updating final status as failed");
							this.status="false";
							
							JSONObject liveBuildCon=(JSONObject)buildHistory.get("LiveBuildConsole");
							JSONArray liveBuild_Tools=(JSONArray) liveBuildCon.get("tools");
							for (int j = 0; j < liveBuild_Tools.size(); j++) {
								JSONObject tool_j = (JSONObject) liveBuild_Tools.get(j);
								System.out.println("sueanne---->>");
								String tool_name = (String) tool_j.get("tool_name");
								if(tool_name.equalsIgnoreCase(jobName)) {
									String tool_status=(String) tool_j.get("tool_status");
									System.out.println("upadting fail status in live build console");
									tool_j.replace("tool_status", tool_status, "FAILED");
									break;
									}
								}
							}
							}else if(toolName.equalsIgnoreCase("jira")) {
								System.out.println("its jira");
								JiraAdapterNew jiraAdapter=new JiraAdapterNew();
								String[] reportPathSplit=reportPath.split(",");
								jiraAdapter.test(reportPathSplit[0],reportPathSplit[1],reportPathSplit[3], reportPathSplit[4], reportPathSplit[5]);
							}else if(toolName.equalsIgnoreCase("scania_tool")) {
								System.out.println("its scania tool");
								ScaniaToolAdapter scaniaToolAdapter=new ScaniaToolAdapter();
								JSONArray toolReport=scaniaToolAdapter.getToolReportJsonArray(reportPath);
								temp.put("ToolReport", toolReport);
							}else if(toolName.equalsIgnoreCase("openscript")){
								
								logger.info("openscript adapter is used");
								GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
								logger.info(reportPath+""+jobName);
								System.out.println(reportPath+""+jobName);
								JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
								temp.put("ToolReport",toolReport);
							}else if(toolName.equalsIgnoreCase("Worksoft_Certify")){
								logger.info("generic adapter is used");
								GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
								logger.info(reportPath+""+jobName);
								System.out.println(reportPath+""+jobName);
								JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
								temp.put("ToolReport",toolReport);
							}else if(toolName.equalsIgnoreCase("jpack")){
								logger.info("generic adapter is used");
								GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
								logger.info(reportPath+""+jobName);
								System.out.println(reportPath+""+jobName);
								JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
								temp.put("ToolReport",toolReport);
							}else if (toolName.equalsIgnoreCase("Jsure")){
							System.out.println("hiiiiiiiiii");
							logger.info("generic adapter is used");
							GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
							logger.info(reportPath+""+jobName);
							System.out.println(reportPath+""+jobName);
							JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
							temp.put("ToolReport",toolReport);
							}
						}					
				}catch(NullPointerException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNullPointerException(e,temp);
					break;
				}catch(SecurityException  | IOException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleSecurityException(e,temp);
					break;
				}catch(NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException |InvalidFormatException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNumberFormatException(e,temp);
					break;
				}catch(Exception e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleUnanticipatedException(e,temp);
					break;
				}				
			
		}
			System.out.println("automationReport:buildHistory "+buildHistory.toJSONString());
		
	}
	
	private void dataTestingReport(JSONObject buildHistory,String status) {
		JSONObject dataTesting=(JSONObject)buildHistory.get("DataTesting");
		System.out.println("buildHistory "+buildHistory.toJSONString());
		System.out.println("dataTesting "+dataTesting);
		System.out.println("dataTesting string "+dataTesting.toJSONString());
		JSONArray tools=(JSONArray)dataTesting.get("Tools");
		System.out.println("tools "+tools.toJSONString());
		for(int i=0;i<tools.size();i++) {
			JSONObject temp=(JSONObject)tools.get(i);
			if(temp.get("tool_name").equals(jobName)) {
				try {
					if(toolName.equalsIgnoreCase("dtf")) {
						DTFAdapter dtfAdapter=new DTFAdapter();
						org.json.JSONArray arr=dtfAdapter.GetJsonArray(reportPath);
						JSONArray chartData=dtfAdapter.getChartDataAsJSONArray(reportPath,arr);
						temp.put("ToolReport",chartData);
						System.out.println("dataTestingReport: temp is "+temp);
					}else if(toolName.equalsIgnoreCase("pdf") || toolName.equalsIgnoreCase("pdf_diff")) {
						PdfDiffAdapter pdfDiffAdapter=new PdfDiffAdapter();
						//JSONArray toolReport=pdfDiffAdapter.getPdfDiffReport(reportPath,jobName,buildNumber);
						JSONArray toolReport=pdfDiffAdapter.getPdfDiffReportRSA(reportPath,jobName,buildNumber);
						temp.put("ToolReport",toolReport);
					}else if(toolName.equalsIgnoreCase("idiscover")) {
						GDPRAdapter gdprAdapter=new GDPRAdapter();
						JSONArray toolReport=gdprAdapter.getGDPRReport(reportPath,jobName,buildNumber);
						temp.put("ToolReport",toolReport);
					}else if(toolName.equalsIgnoreCase("etdm")) {
						ETDMAdapter etdmAdapter=new ETDMAdapter();
						JSONArray reportData=etdmAdapter.GetJsonArray(reportPath,jobName);
						temp.put("ToolReport", reportData);
						System.out.println("datatesting: etdm-temp is "+temp);
					}
					else if(toolName.equalsIgnoreCase("radar")) {
						GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
						logger.info(reportPath+""+jobName);
						System.out.println(reportPath+""+jobName);
						JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
						temp.put("ToolReport",toolReport);
					}
					break;					
				}catch(NullPointerException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNullPointerException(e,temp);
					break;
				}catch(SecurityException  | IOException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleSecurityException(e,temp);
					break;
				}catch(NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException |InvalidFormatException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNumberFormatException(e,temp);
					break;
				}catch(Exception e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleUnanticipatedException(e,temp);
					break;
				}
			}
		}
	}
	
	private void performanceReport(JSONObject buildHistory,String status) {
		JSONObject dataTesting=(JSONObject)buildHistory.get("PerformanceTesting");
		JSONArray tools=(JSONArray)dataTesting.get("Tools");
		for(int i=0;i<tools.size();i++) {
			JSONObject temp=(JSONObject)tools.get(i);
			if(temp.get("tool_name").equals(jobName)) {
				try {
					if(toolName.equalsIgnoreCase("jmeter")) {
						JMeterAdapter jMeterAdapter=new JMeterAdapter();
						JSONArray reportData=jMeterAdapter.getReport(reportPath, jobName, buildNumber);
						temp.put("ToolReport", reportData);
						System.out.println("performanceReport: temp is "+temp);
					}
					else if(toolName.equalsIgnoreCase("loadrunner")) {
							JMeterAdapter jMeterAdapter=new JMeterAdapter();
							JSONArray reportData=jMeterAdapter.getReport(reportPath, jobName, buildNumber);
							temp.put("ToolReport", reportData);
					
					}else if(toolName.equalsIgnoreCase("performance_center")) {
						if(temp.containsKey("error_body")) {
							temp.remove("error_body");
						}
						if(temp.containsKey("stackTrace")){
							temp.remove("stackTrace");
						}
						PerformanceCenterAdapter performanceCenterAdapter=new PerformanceCenterAdapter();
						JSONArray reportData=performanceCenterAdapter.getPerformanceCenterReport(reportPath, jobName, buildNumber);
						temp.put("ToolReport", reportData);
						
					}else if(toolName.equalsIgnoreCase("vykon")) {
						GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
						logger.info(reportPath+""+jobName);
						System.out.println(reportPath+""+jobName);
						JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
						temp.put("ToolReport",toolReport);
					}else if(toolName.equalsIgnoreCase("nyx")) {
						GenericURLApdapter genericURLAdapter = new GenericURLApdapter();
						logger.info(reportPath+""+jobName);
						System.out.println(reportPath+""+jobName);
						JSONArray toolReport=genericURLAdapter.URLAdapter(reportPath,jobName,buildNumber);
						temp.put("ToolReport",toolReport);
					}
					break;					
				}catch(NullPointerException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNullPointerException(e,temp);
					break;
				}catch(SecurityException  | IOException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleSecurityException(e,temp);
					break;
				}catch(NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException |InvalidFormatException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNumberFormatException(e,temp);
					break;
				}catch(Exception e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleUnanticipatedException(e,temp);
					break;
				}

			}
			
		}
		
	}
	private void securityReport(JSONObject buildHistory,String status) {
		JSONObject dataTesting=(JSONObject)buildHistory.get("SecurityTesting");
		JSONArray tools=(JSONArray)dataTesting.get("Tools");
		for(int i=0;i<tools.size();i++) {
			JSONObject temp=(JSONObject)tools.get(i);
			if(temp.get("tool_name").equals(jobName)) {
				try {
					if(toolName.equalsIgnoreCase("owasp_zap")) {
						OwaspZapAdapter owaspZapAdapter=new OwaspZapAdapter();
						JSONArray toolReport=owaspZapAdapter.getToolReportJsonArray(reportPath);
						temp.put("ToolReport", toolReport);
						System.out.println("SecurityTesting: temp is "+temp);
					}else if(toolName.equalsIgnoreCase("ibm_app_scan")) {
						System.out.println("ibmAppScan:");
						IbmAppScanAdapter ibmAppScanAdapter=new IbmAppScanAdapter();
						JSONArray toolReport=ibmAppScanAdapter.getToolReportJsonArray(reportPath);
						temp.put("ToolReport",toolReport);
						System.out.println("SecurityTesting ibm_app_scan : temp is "+temp);
					}else if(toolName.equalsIgnoreCase("burp")) {
						if(temp.containsKey("error_body")) {
							temp.remove("error_body");
						}
						if(temp.containsKey("stackTrace")){
							temp.remove("stackTrace");
						}
						System.out.println("burp:");
						BURPAdapter burpAdapter = new BURPAdapter();
						System.out.println("<><><><><><><> "+reportPath);
						JSONArray toolReport=burpAdapter.getReport(reportPath,jobName,buildNumber);
						temp.put("ToolReport",toolReport);
						System.out.println("SecurityTesting burp : temp is "+temp);
					}

					break;					
				}catch(NullPointerException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNullPointerException(e,temp);
					break;
				}catch(SecurityException  | IOException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleSecurityException(e,temp);
					break;
				}catch(NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException |InvalidFormatException e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNumberFormatException(e,temp);
					break;
				}catch(Exception e) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleUnanticipatedException(e,temp);
					break;
				}

			}
		}
	}
/*	private void gdprReport(JSONObject buildHistory,String status) {
	
				if(toolName.equalsIgnoreCase("idiscover")) {
					GDPRAdapter gdprAdapter=new GDPRAdapter();
					JSONArray toolReport=gdprAdapter.getGDPRReport(reportPath,jobName,buildNumber);
					buildHistory.put("GDPR", toolReport);
					System.out.println("GDPR Report Added");
				}
				
			}*/
	
	
	/*private void gdprReport(JSONObject buildHistory,String status) {
		JSONObject dataTesting=(JSONObject)buildHistory.get("DataTesting");
		JSONArray tools=(JSONArray)dataTesting.get("Tools");
		for(int i=0;i<tools.size();i++) {
			JSONObject temp=(JSONObject)tools.get(i);
			if(temp.get("tool_name").equals(jobName)) {
				if(toolName.equalsIgnoreCase("idiscover")) {
					GDPRAdapter gdprAdapter=new GDPRAdapter();
					JSONArray toolReport=gdprAdapter.getGDPRReport(reportPath,jobName,buildNumber);
					
					
					temp.put("ToolReport", toolReport);
					System.out.println("gdpr Report: temp is "+temp);
				}
				break;
			}
			
		}
		
	}
	*/
	
	
	
	
	private void notificationReport(JSONObject buildHistory,String status) {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateStatusInMongo(String jenkinsConsoleOutput){
		JSONObject finalPlatoJsonObj=new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			finalPlatoJsonObj=null;
			String finalPlatoJsonString=null;
			while(finalPlatoJsonString==null)
			{
				logger.debug("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				finalPlatoJsonString=MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
				logger.debug("finalPlatoJsonString is "+finalPlatoJsonString);
			}		
			finalPlatoJsonObj=(JSONObject)parser.parse(finalPlatoJsonString);
			logger.debug("finalPlatoJsonObj is "+finalPlatoJsonObj);
			finalPlatoJsonObj.replace("readCount","1","0");
			
			JSONObject obj1= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
			JSONObject obj2= (JSONObject) obj1.get("LiveBuildConsole");
			System.out.println("obj1 before is "+obj1);
			JSONArray obj3= (JSONArray) obj2.get("tools");
			//JSONArray obj4= (JSONArray) obj3.get("generic_tools");
			for(int i=0;i<obj3.size();i++) {
				JSONObject temp=(JSONObject)obj3.get(i);
				if(temp.get("tool_name").equals(jobName)) {
					temp.put("tool_status", "In_Progress");
					break;
				}
			}
			logger.info("Updating the MongoDB template");
			logger.debug(finalPlatoJsonObj);
			logger.debug("Updating template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
			logger.debug("Updated Status of DIAT in MongoDB for build history id :"+buildHistoryId+ " and job name :  "+jobName);
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.debug("readcount changed to 1 and updated in mongo");
			finalPlatoJsonObj.replace("readCount","1","0");
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
		}
	}
	
	
	public boolean checkPredecessorStatusInMongo(String predecessor){
		JSONObject finalPlatoJsonObj=new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			finalPlatoJsonObj=null;
			String finalPlatoJsonString=null;
			while(finalPlatoJsonString==null)
			{
				logger.debug("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				finalPlatoJsonString=MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
				logger.debug("finalPlatoJsonString is "+finalPlatoJsonString);
			}		
			finalPlatoJsonObj=(JSONObject)parser.parse(finalPlatoJsonString);
			logger.debug("finalPlatoJsonObj is "+finalPlatoJsonObj);
			finalPlatoJsonObj.replace("readCount","1","0");
			
			JSONObject obj1= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
			JSONObject obj2= (JSONObject) obj1.get("LiveBuildConsole");
			System.out.println("obj1 before is "+obj1);
			JSONArray obj3= (JSONArray) obj2.get("tools");
			//JSONArray obj4= (JSONArray) obj3.get("generic_tools");
			for(int i=0;i<obj3.size();i++) {
				JSONObject temp=(JSONObject)obj3.get(i);
				if(temp.get("tool_name").equals(predecessor)) {
					try{
					if(((String) temp.get("tool_status")).equalsIgnoreCase("failed")){
						finalPlatoJsonObj.replace("readCount","1","0");
						MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
						return true;
					}
					}catch(NullPointerException e){
						System.out.println("predecesor not yet completed");
					}
					
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			finalPlatoJsonObj.replace("readCount","1","0");
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
			
		}
		MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
		return false;
	}
	
	public void updateCheckpoints(String jenkinsConsoleOutput) throws Exception {
		String consoleOutput;
		consoleOutput = jenkinsConsoleOutput;

		if (!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))) {
			if (jenkinsConsoleOutput.contains(this.jenkinsConsoleOutput.trim())) {
				logger.info("Removing previous console");
				jenkinsConsoleOutput = jenkinsConsoleOutput.replace(
						this.jenkinsConsoleOutput.trim(), "");
			}
		} else {
			logger.info("this.jenkinsConsoleOutput is blank");
		}

		logger.info("Jenkins console after trimming is below: ");
		logger.info(jenkinsConsoleOutput);
		ArrayList<Integer> moduleOrderList = new ArrayList<Integer>();
		Long module_order_num;
		Long checkpoint_order_num;

		JSONParser parser = new JSONParser();
		JSONObject finalPlatoJsonObj = null;
		String finalPlatoJsonString = null;
		while (finalPlatoJsonString == null) {
			logger.debug("Inside while loop. Fetching template for build history id :"
					+ buildHistoryId + " and job name :  " + jobName);
			finalPlatoJsonString = MongoDBOperations
					.fetchJsonFromMongoDB(buildHistoryId);
		}
		logger.debug("Retrived data : " + finalPlatoJsonString);
		finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);

		// added for read count
		finalPlatoJsonObj.replace("readCount", "1", "0");
		// -----
		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2 = (JSONObject) obj1.get("LiveBuildConsole");

		JSONArray subJobList = (JSONArray) obj2.get("tools");

		//JSONArray subJobList = (JSONArray) obj3.get("tools");

		int len = subJobList.size();

		moduleOrderList.clear();
		
		for (int i = 0; i < len; i++) {
			JSONObject obj4 = (JSONObject) subJobList.get(i);

			
			String jName = (String) obj4.get("tool_name");
			if (jName.equalsIgnoreCase(jobName)) {
				obj4.replace("build_number", "", buildNumber);
				JSONArray modules = (JSONArray) obj4.get("modules");
				
				if(modules!=null){

				for (int j = 0; j < modules.size(); j++) {
					JSONObject obj5 = (JSONObject) modules.get(j);

					// String module_name = (String) obj5.get("module_name");
					module_order_num = (Long) obj5.get("module_order");
					moduleOrderList.add(Integer.valueOf(module_order_num.intValue()));

					Collections.sort(moduleOrderList);
					System.out.println(moduleOrderList);
					logger.info(moduleOrderList);
				}
				
				}
			}

		}

		
		
		//len is the size of the subJob list
		for (int i = 0; i < len; i++) {
			int flag=0;
			
			//get the first subjob and check if its the current threads job, because every job is running on a different thread
			JSONObject obj4 = (JSONObject) subJobList.get(i);

			String jName = (String) obj4.get("tool_name");
			
			//checking job name with thread job
			if (jName.equalsIgnoreCase(jobName)) {
				
				JSONArray modules = (JSONArray) obj4.get("modules");
				
				if (jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
					System.out
							.println("Updating Status in Mongo ---------------------------------"
									+ jName);
					obj4.replace("tool_status", "In_Progress", "Passed");
				} else if (jenkinsConsoleOutput.contains("Finished: FAILURE")) {
					System.out
							.println("Updating FAST Status in Mongo ---------------------------------"
									+ jName);
					obj4.replace("tool_status", "In_Progress", "Failed");
					status = "Failed";

				} else if (jenkinsConsoleOutput.contains("Finished: ABORTED")) {
					obj4.replace("tool_status", "In_Progress", "Aborted");
					status = "Aborted";
				} else if (jenkinsConsoleOutput != null
						|| !(jenkinsConsoleOutput.contains("Finished: FAILURE"))
						|| !(jenkinsConsoleOutput.contains("Finished: SUCCESS"))
						|| !(jenkinsConsoleOutput.contains("Finished: ABORTED"))) {
					obj4.replace("tool_status", "", "In_Progress");
					System.out
							.println("In Progress Updated in Mongo DB----------------------------------------"
									+ jName);

				}

				if(!moduleOrderList.isEmpty()){
				int firstModule=moduleOrderList.get(0);
				
				for (int f = 0; f < modules.size(); f++) {

					JSONObject obj5 = (JSONObject) modules.get(f);

					Long module_order = (Long) obj5.get("module_order");

					if (firstModule == module_order) {

						// arrCheckPointst
						JSONArray checkpoints_arr = (JSONArray) obj5
								.get("checkpoints");
						for (int fcp = 0; fcp < checkpoints_arr.size(); fcp++) {

							JSONObject fcpobj6 = (JSONObject) checkpoints_arr
									.get(fcp);
							Long fcp_checkpoint_order = (Long) fcpobj6
									.get("checkpoint_order");

							if (fcp_checkpoint_order == 1) {

								fcpobj6.replace("status", "", "In_Progress");
							}
						}
					}
				}
				
							
				ArrayList<Integer> checkpointsOrderList = new ArrayList<Integer>();
				int noOfModules = moduleOrderList.size();
				
				//Module Loop- for each module-fetch checkpoints status and update in mongoDB
				for (int m = 0; m < noOfModules; m++) {

					
					int currentModule = moduleOrderList.get(m);
					logger.debug("Current module is "+currentModule);
					
					//loop to check order of the module,
					//goes according to moduleOrderList
					for (int j = 0; j < modules.size(); j++) {

						JSONObject obj5 = (JSONObject) modules.get(j);

						Long module_order = (Long) obj5.get("module_order");

						if (currentModule == module_order) {
							
									//arrCheckPointst
							JSONArray checkpoints_arr = (JSONArray) obj5.get("checkpoints");

							checkpointsOrderList.clear();
							for (int k = 0; k < checkpoints_arr.size(); k++) {
								JSONObject obj6 = (JSONObject) checkpoints_arr.get(k);

								checkpoint_order_num=(Long) obj6.get("checkpoint_order");
								
								//String test_case_name = (String) obj6.get("test_case_name");

								checkpointsOrderList.add(Integer.valueOf(checkpoint_order_num.intValue()));

								Collections.sort(checkpointsOrderList);
								
								

							}

							logger.debug(checkpointsOrderList);
							int noOfCheckPointsInModule = checkpointsOrderList.size();
							logger.info("Number of checkpoints in module are"+noOfCheckPointsInModule);
							//Checkpoints Loop- for each check point in order, check jenkins console and update status
							//if checkpoint criteria not found break loop
							for (int k = 0; k < noOfCheckPointsInModule; k++) {
								
								int currentCheckpointOrder = checkpointsOrderList.get(k);
								logger.info("Current checkpoint  "+currentCheckpointOrder);
								System.out.println("Current checkpoint  "+currentCheckpointOrder);
								for(int c=0;c<checkpoints_arr.size();c++){
								
									JSONObject obj6 = (JSONObject) checkpoints_arr.get(c);
									
									String checkpointName = (String) obj6.get("checkpoint_name");
									Long checkpoint_order=(Long) obj6.get("checkpoint_order");
									
									if(currentCheckpointOrder==checkpoint_order){
										
										
										String checkpointStatus = (String) obj6.get("status");
										logger.info("Status of current checkpoint i.e.  "+checkpointName+ " is "+ checkpointStatus);
										System.out.println("Status of current checkpoint i.e.  "+checkpointName+ " is "+ checkpointStatus);
									
										
										//check this if loop because blank condition is not required
										if(checkpointStatus.equals("In_Progress")){
											
											
											
											String testPassCriteria = (String) obj6.get("pass_criteria");
											String testFailCriteria = (String) obj6.get("fail_criteria");

											System.out.println("checkpoint name : passCriteria : failCriteria "
															+ checkpointName
															+ " "
															+ " "
															+ testPassCriteria
															+ " "
															+ testFailCriteria);

											logger.info("checkpoint name : passCriteria : failCriteria "
															+ checkpointName
															+ " "
															+ " "
															+ testPassCriteria
															+ " "
															+ testFailCriteria);
											
											
											
											//check jenkins console for this checkpoint
											
											// pass criteria
											if (testPassCriteria.contains(",")) {
												System.out.println("testPassCriteria contains ,");
												String testPassCriteriaArray[] = testPassCriteria
														.split(",");
												for (String passCriteria : testPassCriteriaArray) {
													if (jenkinsConsoleOutput.contains(passCriteria)) {

														System.out
																.println("checking fo pass criteria ");
														// added later
														if (((String) (obj6.get("status")))
																.equalsIgnoreCase("In_Progress")) {
															obj6.replace("status", "In_Progress", "pass");
															System.out.println("status of checkpoint "
																			+ checkpointName + " : "
																			+ obj6.get("status"));
															logger.info("status of checkpoint "
																	+ checkpointName + " : "
																	+ obj6.get("status"));
															//code to change the next checkpoint status to In progress
															if(currentCheckpointOrder==checkpointsOrderList.size()){
																//if checkpoint is the last check point in the module
																//then go to the next module and update the first chekpoints status to in progress
																
																if(m+1>=moduleOrderList.size()){
																	flag=1;
																	lastCheckpoint=true;
																	System.out
																			.println("This was the last module, status updated for all checkpoints");
																	break;
																}else{
																int nextModule=moduleOrderList.get(m+1);
																for (int nm = 0; nm < modules.size(); nm++) {

																	JSONObject nmobj5 = (JSONObject) modules.get(nm);

																	Long nm_module_order = (Long) nmobj5.get("module_order");

																	if (nextModule == nm_module_order) {
																		
																				//arrCheckPointst
																		JSONArray nm_checkpoint_arr = (JSONArray) nmobj5.get("checkpoints");

																		for(int nxtmodcp=0;nxtmodcp<nm_checkpoint_arr.size();nxtmodcp++){
																			
																			JSONObject nxtmodcpobj6 = (JSONObject) nm_checkpoint_arr.get(nxtmodcp);
																			Long nxtmodcp_checkpoint_order=(Long) nxtmodcpobj6.get("checkpoint_order");
																			
																			if(nxtmodcp_checkpoint_order==1){
																				logger.info("Updating first checkpoint status to in_progress");

																				nxtmodcpobj6.replace("status", "","In_Progress");
																				
																				
																			}
																				
																		}
																		
																	}
																}
																
															
																}
																
															}else{
																
																//get the next checkpoint
																if(checkpointsOrderList.get(k+1)!=null){
																 int nextCheckPoint= checkpointsOrderList.get(k+1);
																 
																	for(int nxtcp=0;nxtcp<checkpoints_arr.size();nxtcp++){
																		
																		JSONObject nxtcpobj6 = (JSONObject) checkpoints_arr.get(nxtcp);
																		Long nxtcp_checkpoint_order=(Long) nxtcpobj6.get("checkpoint_order");
																		
																		if(nextCheckPoint==nxtcp_checkpoint_order){
																			logger.info("Updating next checkpoint status to in_progress");

																			nxtcpobj6.replace("status", "","In_Progress");
																			
																			
																		}
																			
																	}
																}
																}
														
														}
													}
												}
											} else if (jenkinsConsoleOutput
														.contains(testPassCriteria)) {

												logger.info("Console contains pass creiteria "+testPassCriteria);
													System.out
													.println("testPassCriteria dosen't contains ,");
													
													// added later
													if (((String) (obj6.get("status")))
															.equalsIgnoreCase("In_Progress")) {
														obj6.replace("status", "In_Progress",
																"pass");
														System.out
																.println("status of checkpoint "
																		+ checkpointName
																		+ " : "
																		+ obj6.get("status"));
														
														//code to change the next checkpoint status to In progress
														if(currentCheckpointOrder==checkpointsOrderList.size()){
															//if checkpoint is the last check point in the module
															//then go to the next module and update the first chekpoints status to in progress
															
															if(m+1>=moduleOrderList.size()){
																flag=1;
																lastCheckpoint=true;
																System.out
																		.println("This was the last module, status updated for all checkpoints");
																break;
															}else{
															int nextModule=moduleOrderList.get(m+1);
															for (int nm = 0; nm < modules.size(); nm++) {

																JSONObject nmobj5 = (JSONObject) modules.get(nm);

																Long nm_module_order = (Long) nmobj5.get("module_order");

																if (nextModule == nm_module_order) {
																	
																			//arrCheckPointst
																	JSONArray nm_checkpoints_arr = (JSONArray) nmobj5.get("checkpoints");

																	for(int nxtmodcp=0;nxtmodcp<nm_checkpoints_arr.size();nxtmodcp++){
																		
																		JSONObject nxtmodcpobj6 = (JSONObject) nm_checkpoints_arr.get(nxtmodcp);
																		Long nxtmodcp_checkpoint_order=(Long) nxtmodcpobj6.get("checkpoint_order");
																		
																		if(nxtmodcp_checkpoint_order==1){
																			logger.info("Updating next checkpoint status to in_progress");

																			nxtmodcpobj6.replace("status", "","In_Progress");
																			
																			
																		}
																			
																	}
																	
																}
															}
															
														
															}
															
															
															
															
														}else{
															
															//get the next checkpoint
															if(checkpointsOrderList.get(k+1)!=null){
															 int nextCheckPoint= checkpointsOrderList.get(k+1);
															 
															 logger.info("Next checkpoint is "+ nextCheckPoint);
																for(int nxtcp=0;nxtcp<checkpoints_arr.size();nxtcp++){
																	
																	JSONObject nxtcpobj6 = (JSONObject) checkpoints_arr.get(nxtcp);
																	Long nxtcp_checkpoint_order=(Long) nxtcpobj6.get("checkpoint_order");
																	
																	if(nextCheckPoint==nxtcp_checkpoint_order){
																		logger.info("Updating next checkpoint status to in_progress");

																		nxtcpobj6.replace("status", "","In_Progress");
																		
																		
																	}
																		
																}
															}
															}

													}
												}else if (testFailCriteria.contains(",")) {
												// fail criteria
												System.out
														.println("testFailCriteria contains ,");
												String testFailCriteriaArray[] = testFailCriteria
														.split(",");
												for (String failCriteria : testFailCriteriaArray) {
													if (jenkinsConsoleOutput
															.contains(failCriteria)) {

														// added later
														if (((String) (obj6
																.get("status")))
																.equalsIgnoreCase("In_Progress")) {
															obj6.replace("status", "In_Progress",
																	"fail");
															System.out
																	.println("status of checkpoint "
																			+ checkpointName
																			+ " : "
																			+ obj6.get("status"));
															
															//code to change the next checkpoint status to In progress
															if(currentCheckpointOrder==checkpointsOrderList.size()){
																//if checkpoint is the last check point in the module
																//then go to the next module and update the first chekpoints status to in progress
																
																if(m+1>=moduleOrderList.size()){
																	flag=1;
																	lastCheckpoint=true;
																	System.out
																			.println("This was the last module, status updated for all checkpoints");
																	break;
																}else{
																int nextModule=moduleOrderList.get(m+1);
																for (int nm = 0; nm < modules.size(); nm++) {

																	JSONObject nmobj5 = (JSONObject) modules.get(nm);

																	Long nm_module_order = (Long) nmobj5.get("module_order");

																	if (nextModule == nm_module_order) {
																		
																				//arrCheckPointst
																		JSONArray nm_checkpoints_arr = (JSONArray) nmobj5.get("checkpoints");

																		for(int nxtmodcp=0;nxtmodcp<nm_checkpoints_arr.size();nxtmodcp++){
																			
																			JSONObject nxtmodcpobj6 = (JSONObject) nm_checkpoints_arr.get(nxtmodcp);
																			Long nxtmodcp_checkpoint_order=(Long) nxtmodcpobj6.get("checkpoint_order");
																			
																			if(nxtmodcp_checkpoint_order==1){
																				logger.info("Updating next checkpoint status to in_progress");

																				nxtmodcpobj6.replace("status", "","In_Progress");
																				
																				
																			}
																				
																		}
																		
																	}
																}
																
															
																}
																
																
																
																
															}else{
																
																//get the next checkpoint
																if(checkpointsOrderList.get(k+1)!=null){
																 int nextCheckPoint= checkpointsOrderList.get(k+1);
																 
																	for(int nxtcp=0;nxtcp<checkpoints_arr.size();nxtcp++){
																		
																		JSONObject nxtcpobj6 = (JSONObject) checkpoints_arr.get(nxtcp);
																		Long nxtcp_checkpoint_order=(Long) nxtcpobj6.get("checkpoint_order");
																		
																		if(nextCheckPoint==nxtcp_checkpoint_order){
																			logger.info("Updating next checkpoint status to in_progress");

																			nxtcpobj6.replace("status", "","In_Progress");
																			
																			
																		}
																			
																	}
																}
																}
															

														}
														if (status
																.equalsIgnoreCase("Passed")) {
															status = "Failed";
														}
													}
												}

											} else if (jenkinsConsoleOutput
													.contains(testFailCriteria)){
												System.out
														.println("testFailCriteria dosen't contains ,");
												
												logger.info("Console contains fail criteria  "+testFailCriteria);
													// added later
													if (((String) (obj6.get("status")))
															.equalsIgnoreCase("In_Progress")) {
														obj6.replace("status", "In_Progress",
																"fail");
														System.out
																.println("status of checkpoint "
																		+ checkpointName
																		+ " : "
																		+ obj6.get("status"));
														
														//code to change the next checkpoint status to In progress
														if(currentCheckpointOrder==checkpointsOrderList.size()){
															//if checkpoint is the last check point in the module
															//then go to the next module and update the first chekpoints status to in progress
															
															if(m+1>=moduleOrderList.size()){
																flag=1;
																lastCheckpoint=true;
																System.out
																		.println("This was the last module, status updated for all checkpoints");
																break;
															}else{
															int nextModule=moduleOrderList.get(m+1);
															for (int nm = 0; nm < modules.size(); nm++) {

																JSONObject nmobj5 = (JSONObject) modules.get(nm);

																Long nm_module_order = (Long) nmobj5.get("module_order");

																if (nextModule == nm_module_order) {
																	
																			//arrCheckPointst
																	JSONArray nm_checkpoints_arr = (JSONArray) nmobj5.get("checkpoints");

																	for(int nxtmodcp=0;nxtmodcp<nm_checkpoints_arr.size();nxtmodcp++){
																		
																		JSONObject nxtmodcpobj6 = (JSONObject) nm_checkpoints_arr.get(nxtmodcp);
																		Long nxtmodcp_checkpoint_order=(Long) nxtmodcpobj6.get("checkpoint_order");
																		
																		if(nxtmodcp_checkpoint_order==1){
																			logger.info("Updating next checkpoint status to in_progress");

																			nxtmodcpobj6.replace("status", "","In_Progress");
																			
																			
																		}
																			
																	}
																	
																}
															}
															
														
															}
															
														}else{
															
															//get the next checkpoint
															if(checkpointsOrderList.get(k+1)!=null){
															 int nextCheckPoint= checkpointsOrderList.get(k+1);
															 
																for(int nxtcp=0;nxtcp<checkpoints_arr.size();nxtcp++){
																	
																	JSONObject nxtcpobj6 = (JSONObject) checkpoints_arr.get(nxtcp);
																	Long nxtcp_checkpoint_order=(Long) nxtcpobj6.get("checkpoint_order");
																	
																	if(nextCheckPoint==nxtcp_checkpoint_order){
																		logger.info("Updating next checkpoint status to in_progress");
																		nxtcpobj6.replace("status", "","In_Progress");
																		
																		
																	}
																		
																}
															}
															}

													} 

													if (status.equalsIgnoreCase("Passed")) {
														status = "Failed";
													}
												
											}else {
												flag=1;
												
												logger.info("Checkpoint not present in console");
												System.out.println("Checkpoint not present in console");
												break;
											}
											
											//*********finished checking console***********
											
											
										}
										
										
										/*else{
											flag=1;
											break;
										}*/
																				
									}//no else because it will find one checkpoint in the list that matches hopefully
									
									
								}//json for-loop to get object of current checkpoint
								
									if(flag==1 || lastCheckpoint==true){
										break;//if particular checkpoint isnt found then no point going to the next checkpoint
									}
								
							}//for-loop to change value of current checkpoint(to go to next checkpoint in particular module)
								
					}//no else because it will to go to the next module
						
						
						if(flag==1 || lastCheckpoint==true){
						
							break;// no point checking next module if a previous chekcpoint is not found
						}

						}//json for-loop  to get object of current module

						// end of if loop, checked current module
					
					if(flag==1 || lastCheckpoint==true){
						
						break;// no point checking next module if a previous chekcpoint is not found
					}
					
				}
				
			}//for-loop for all modules to change value of current module(to go to next module)
		} //doesnt need an else condition
				
		}//end of for-loop which gets us the particular job of thread	
				
			

		this.jenkinsConsoleOutput = consoleOutput;

		// update the json in mongoDb
		logger.info("Updating the MongoDB template");
		logger.debug(finalPlatoJsonObj);
		logger.debug("Updating template for build history id :"
				+ buildHistoryId + " and job name :  " + jobName);
		MongoDBOperations
				.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistoryId);

		logger.debug("Updated Status of FAST in MongoDB for build history id :"
				+ buildHistoryId + " and job name :  " + jobName);

	
	}
	
}
