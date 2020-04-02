package com.reportingServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.adapters.XcheckAdapter;
import com.mongo.constants.GlobalConstants;
import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;
import com.plato.errorHandler.ErrorHandler;

/**
 * @author 10643331(Sueanne Alphonso)
 **/
public class FASTReportService {

	private static final Logger logger = Logger
			.getLogger(FASTReportService.class);

	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status = "Passed";
	String full_tool_status="";
	private boolean lastCheckpoint=false;
	private String jenkinsUrl;
	private String toolName;

	String jenkinsConsoleOutput = "";

	public FASTReportService(int buildHistoryId, int buildNumber,
			String jobName, String jenkinsUrl,String toolName) {
		logger.info("inside FastReportService Constructor");
		this.buildNumber = buildNumber;
		this.jobName = jobName;
		this.buildHistoryId = buildHistoryId;
		this.jenkinsUrl = jenkinsUrl;
		this.toolName=toolName;
	}

	public String readFASTJenkinsTotalResponse() throws Exception {

		logger.debug("inside readFASTJenkinsTotalResponse");
		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();

		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		logger.info("Reading FAST Execution from Jenkins Console ");

		for (;;) {
			Thread.sleep(5000);
			jenkinsConsoleOutput = readJenkinsConsole.readJenkinsConsole(
					buildNumber, jobName, jenkinsUrl);
			finalPlatoJsonObj = readFASTJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
			if (finalPlatoJsonObj == null) {
				logger.error("readFASTJenkinsTotalResponse: Template not found with Build History Id : "
						+ buildHistoryId);
				logger.error("Throwing Exception ");
				throw new Exception(
						"Plato Template Doesnt Exists for build History id :"
								+ buildHistoryId);
			}
			logger.info(jenkinsConsoleOutput + "text above is fast console");

			if (jenkinsConsoleOutput.contains("Finished: SUCCESS")
					|| jenkinsConsoleOutput.contains("Finished: FAILURE")
					|| jenkinsConsoleOutput.contains("Finished: ABORTED")
					|| jenkinsConsoleOutput.contains("Finished: UNSTABLE")
					|| jenkinsConsoleOutput.contains("FATAL: null")) {
				logger.info("jenkins console output contains******** Fast Execution Completed ");
				System.out.println("FAST EXECUTION COMPLETED!!!");
				break;
			}
		}

		logger.info("COMPLETED reading FAST Execution from Jenkins Console ");

		if (status.equalsIgnoreCase("Failed")) {
			response = "Failed";
		} else if (status.equalsIgnoreCase("Aborted")) {
			response = "Aborted";
		} else {
			response = "Passed";
		}
		return response;
	}

	private JSONObject readFASTJenkinsConsoleAndUpdateStatus(
			
			String jenkinsConsoleOutput) throws Exception {
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
								.println("Updating FAST Status in Mongo ---------------------------------"
										+ jName);
						obj4.replace("tool_status", "In_Progress", "Passed");
						
						if(status.equalsIgnoreCase("failed")) {
						obj4.replace("tool_status", "In_Progress", "Failed");
						status="Failed";
						}
						
						
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

			return finalPlatoJsonObj;
			
			
			
			
		}

	public static int countSubstring(String subStr, String str){
		return (str.length() - str.replace(subStr, "").length()) / subStr.length();
	}
	
	public String createFASTStepWiseReport(int buildHistoryId) throws Exception {
		logger.debug("in createFASTStepWiseReport ******");
		String jenkinsOutput;
		//String  reportPath="D:\\ExecutionReports\\ExcelReports";
		String  reportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+jobName+"/"+buildNumber+"/ExcelReports/";
		System.out.println("FAST Report Path is " + reportPath);
		
		
		logger.debug("FAST Report Path is " + reportPath);
		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();
		String response;
		JSONArray newJsonToolReportArray=new JSONArray();
		logger.debug("Finished Reading Jenkins Console");
		File f = new File(reportPath);
		Thread.sleep(5000);
		logger.info("Getting started to Update in Mongo DB");
		File[] fList = f.listFiles();
		JSONObject newJsonToolReportObject=new JSONObject();
		JSONArray toolReportArray = new JSONArray();
		JSONArray tabularArray=new JSONArray();
		JSONObject tabluarObj=new JSONObject();
		
		JSONArray chart_labels=new JSONArray();
		JSONArray chart_values=new JSONArray();
		int passCount = 0;
		int failCount = 0;
		int noRunCount = 0;
		int testCaseCount=0;
		int testcasePassCount=0;
		int testcaseFailCount=0;
		int testcaseNoRunCount=0;
		try {

	/*
			for (;;) {
				logger.debug("Reading Jenkins Console");
				// System.out.println("in createFASTStepWiseReport for loop ******");
				jenkinsOutput = readJenkinsConsole.readJenkinsConsole(buildNumber,
						jobName, jenkinsUrl);
				Thread.sleep(8000);
				if (jenkinsOutput.contains("Finished: FAILURE")
						|| jenkinsOutput.contains("Finished: SUCCESS")) {

					break;
				}
			}*/
			
			
			for (File file : fList) {
				
				String overall_status="pass";

				JSONObject tabluarObject=new JSONObject();
				JSONArray component=new JSONArray();
				JSONObject testcasesObj=new JSONObject();
				
				
				JSONObject toolReportObject = new JSONObject();
				JSONObject chartDataObject = new JSONObject();
				
				JSONObject kdtObj=new JSONObject();
		
				
				JSONObject componentsObj=new JSONObject();
				JSONArray componentsArray=new JSONArray();
				
				
				
				
				
				
				String kdtName = null;
				if ((file.toString().contains("_RPT_") && file.toString().contains(
						".xls"))
						|| (file.toString().contains("_RPT_") && file.toString()
								.contains(".xlsx"))) {

					int rptCount=countSubstring("_RPT_", file.toString());
					
					if(rptCount==1){
						
						
						String reportName[]=file.toString().split("\\\\");
						String splitRPT[]=reportName[reportName.length - 1].split("RPT_");
						logger.info("Reading report "+splitRPT[0]);
						kdtName=splitRPT[0];
										
						FileInputStream inp = new FileInputStream(file.toString());

						Workbook workbook = WorkbookFactory.create(inp);
						Sheet sheet = workbook.getSheetAt(0);

						int iNoOfRows = sheet.getLastRowNum() + 1;
						int iNoOfCols = sheet.getRow(0).getLastCellNum();
						Row row = null;
						Cell cell = null;
						String cellContents = null;

					
						String overallStatus="pass";

						JSONArray checkpointArray = new JSONArray();
						for (int iRowCntr = 0; iRowCntr < iNoOfRows; iRowCntr++) {
							
							
							
							
							if (iRowCntr >= 2) {
								row = sheet.getRow(iRowCntr);
								if (row == null) {
									System.out.println("row == null, hencing breaking");
									break;
								}

								
								
								String keyword=readCellValue(row.getCell(3));
							

							
								String executeKDTStr=readCellValue(row.getCell(3));
								String subKDTname = readCellValue(row.getCell(4));
								
								if(subKDTname.contains(".xlsx")){
									 subKDTname=subKDTname.substring(0, subKDTname.length() - 5);
								 }else if(subKDTname.contains(".xls")){
									 subKDTname=subKDTname.substring(0, subKDTname.length() - 4);						
								 }
								
								//Keyword level ************************************************************************************
								if(executeKDTStr.equals("ExecuteKDT")){
									
									cell = row.getCell(0);

									cellContents = readCellValue(cell);

									if (cellContents == null || cellContents.isEmpty() ||cellContents.equals("")) {
										break;
									}
									
									if(cellContents==""){
										break;								
									}
									
									JSONArray arr=new JSONArray();
									JSONObject stepObj=new JSONObject();
													
									stepObj.put("Step_No", cellContents);
									arr.add(stepObj);
									
									cell = row.getCell(2);
									cellContents = readCellValue(cell);

									if (cellContents != null) {
										JSONObject descObj=new JSONObject();
										descObj.put("Step_Description", cellContents);
										arr.add(descObj);
									} else {
										JSONObject descObj=new JSONObject();
										descObj.put("Step_Description", "");
										arr.add(descObj);
									}

									cell = row.getCell(16);
									cellContents = readCellValue(cell);
									
									JSONObject statusObj=new JSONObject();
									statusObj.put("status", cellContents);
									arr.add(statusObj);
									
									if (cellContents.trim().equalsIgnoreCase("pass")) {
										passCount++;
										JSONObject screenshotObj=new JSONObject();
										screenshotObj.put("Screenshot", "");
										arr.add(screenshotObj);
									} else if (cellContents.trim().equalsIgnoreCase("fail")) {
										overallStatus="fail";
										overall_status="fail";
										full_tool_status="fail";
										failCount++;
										cell = row.getCell(15);
										cellContents = readCellValue(cell);
										System.out.println("Fail-Image Name is:  " + cellContents);
										String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/ExcelReports/Images/"+cellContents;
										JSONObject screenshotObj=new JSONObject();
										screenshotObj.put("Screenshot", screenshotUrl);
										arr.add(screenshotObj);
									} else if (cellContents.trim().equalsIgnoreCase(
											"no run")) {
										noRunCount++;
										JSONObject screenshotObj=new JSONObject();
										screenshotObj.put("Screenshot", "");
										arr.add(screenshotObj);
									}


									
									
									
									
									
									
									cell = row.getCell(17);
									cellContents = readCellValue(cell);
									
									JSONObject timeObj=new JSONObject();
									timeObj.put("Exeuction_Time", cellContents);
									arr.add(timeObj);
									
									checkpointArray.add(arr);
									System.out.println(checkpointArray);
									
									System.out.println("cell contains ExecuteKDT");
									for (File reportFile : fList) {
										System.out.println(reportFile.toString());
										//cell=
									
										
										if ((reportFile.toString().contains("_RPT_") && reportFile.toString().contains(".xls"))
												|| (reportFile.toString().contains("_RPT_") && reportFile.toString()
														.contains(".xlsx"))) {
											
											int subRptCount=countSubstring("_RPT_", reportFile.toString());
											
											if(subRptCount>1){
												
												
												if(reportFile.toString().contains(subKDTname) && reportFile.toString().contains(kdtName)){
													
													
													FileInputStream inpOfSubKDT = new FileInputStream(reportFile.toString());

													Workbook subKDTworkbook = WorkbookFactory.create(inpOfSubKDT);
													Sheet subKDTsheet = subKDTworkbook.getSheetAt(0);

													int iNoOfRowsInSubKDT = subKDTsheet.getLastRowNum() + 1;
													//int iNoOfColsInSubKDT = subKDTsheet.getRow(0).getLastCellNum();
													Row subKDTrow = null;
													Cell subKDTcell = null;
													String subKDTcellContents = null;
													
													
													for (int iRowCntrsubKDT = 0; iRowCntrsubKDT < iNoOfRowsInSubKDT; iRowCntrsubKDT++) {

														if (iRowCntrsubKDT >= 2) {
															subKDTrow = subKDTsheet.getRow(iRowCntrsubKDT);
															if (subKDTrow == null) {
																break;
															}
													

															
															//__________________________________________________________________________________________________
															subKDTcell = subKDTrow.getCell(0);

															subKDTcellContents = readCellValue(subKDTcell);

															if (subKDTcellContents == null || subKDTcellContents.isEmpty() ||subKDTcellContents.equals("")) {
																break;
															}
															
															if(subKDTcellContents==""){
																break;								
															}
															
															JSONArray arry=new JSONArray();
															JSONObject subKDTstepObj=new JSONObject();
																			
															subKDTstepObj.put("Step_No", subKDTcellContents);
															arry.add(subKDTstepObj);
															
															subKDTcell = subKDTrow.getCell(2);
															subKDTcellContents = readCellValue(subKDTcell);

															if (subKDTcellContents != null) {
																JSONObject subKDTdescObj=new JSONObject();
																subKDTdescObj.put("Step_Description", subKDTcellContents);
																arry.add(subKDTdescObj);
															} else {
																JSONObject subKDTdescObj=new JSONObject();
																subKDTdescObj.put("Step_Description", "");
																arry.add(subKDTdescObj);
															}

															subKDTcell = subKDTrow.getCell(16);
															subKDTcellContents = readCellValue(subKDTcell);
															
															JSONObject subKDTstatusObj=new JSONObject();
															subKDTstatusObj.put("status", subKDTcellContents);
															arry.add(subKDTstatusObj);
															
															if (subKDTcellContents.trim().equalsIgnoreCase("pass")) {
																passCount++;
																JSONObject screenshotObj=new JSONObject();
																screenshotObj.put("Screenshot", "");
																arry.add(screenshotObj);
															} else if (subKDTcellContents.trim().equalsIgnoreCase("fail")) {
																overallStatus="fail";
																overall_status="fail";
																full_tool_status="fail";
																failCount++;
																subKDTcell = subKDTrow.getCell(15);
																subKDTcellContents = readCellValue(subKDTcell);
																
																String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/ExcelReports/Images/"+subKDTcellContents;
																JSONObject screenshotObj=new JSONObject();
																screenshotObj.put("Screenshot", screenshotUrl);
																arry.add(screenshotObj);
															} else if (subKDTcellContents.trim().equalsIgnoreCase(
																	"no run")) {
																noRunCount++;
																JSONObject screenshotObj=new JSONObject();
																screenshotObj.put("Screenshot", "");
																arry.add(screenshotObj);
															}


															
															
															
															
															
															
															cell = row.getCell(17);
															cellContents = readCellValue(cell);
															
															JSONObject subKDTtimeObj=new JSONObject();
															subKDTtimeObj.put("Exeuction_Time", cellContents);
															arry.add(subKDTtimeObj);
															
															checkpointArray.add(arry);
															System.out.println(checkpointArray);
															
														}//end of if loop that checks the RPT count
														
														
													}//end of for-loop of subkdt
													
													break;
												}//checking both the kdt names in the file name
												
												
											}//checking count of RPT>1
											
											
											
										}//checks if xls or xlsx
											
									}//goes thourgh al reports
									
									
								
									
									
								}else{

									
									cell = row.getCell(0);

									cellContents = readCellValue(cell);

									if (cellContents == null || cellContents.isEmpty() ||cellContents.equals("")) {
										break;
									}
									
									if(cellContents==""){
										break;								
									}
									
									JSONArray arr=new JSONArray();
									JSONObject stepObj=new JSONObject();
													
									stepObj.put("Step_No", cellContents);
									arr.add(stepObj);
									
									cell = row.getCell(2);
									cellContents = readCellValue(cell);

									if (cellContents != null) {
										JSONObject descObj=new JSONObject();
										descObj.put("Step_Description", cellContents);
										arr.add(descObj);
									} else {
										JSONObject descObj=new JSONObject();
										descObj.put("Step_Description", "");
										arr.add(descObj);
									}

									cell = row.getCell(16);
									cellContents = readCellValue(cell);
									
									JSONObject statusObj=new JSONObject();
									statusObj.put("status", cellContents);
									arr.add(statusObj);
									
									if (cellContents.trim().equalsIgnoreCase("pass")) {
										passCount++;
										JSONObject screenshotObj=new JSONObject();
										screenshotObj.put("Screenshot", "");
										arr.add(screenshotObj);
									} else if (cellContents.trim().equalsIgnoreCase("fail")) {
										overallStatus="fail";
										overall_status="fail";
										full_tool_status="fail";
										failCount++;
										cell = row.getCell(15);
										cellContents = readCellValue(cell);
										
										String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/ExcelReports/Images/"+cellContents;
										JSONObject screenshotObj=new JSONObject();
										screenshotObj.put("Screenshot", screenshotUrl);
										arr.add(screenshotObj);
									} else if (cellContents.trim().equalsIgnoreCase(
											"no run")) {
										noRunCount++;
										JSONObject screenshotObj=new JSONObject();
										screenshotObj.put("Screenshot", "");
										arr.add(screenshotObj);
									}


									
									
									
									
									
									
									cell = row.getCell(17);
									cellContents = readCellValue(cell);
									
									JSONObject timeObj=new JSONObject();
									timeObj.put("Exeuction_Time", cellContents);
									arr.add(timeObj);
									
									checkpointArray.add(arr);
									System.out.println(checkpointArray);
									
									if(keyword.equalsIgnoreCase("settestcase")) {
										
										cell=row.getCell(5);
										//overallStatus="pass";
										cellContents=readCellValue(cell);
										System.out.println("----------------------------->"+cellContents);
										if(cellContents.equalsIgnoreCase("")) {
											cellContents="";
										}
										componentsObj.put("componentid", cellContents);
										componentsObj.put("componentsArray", checkpointArray);
										componentsObj.put("overallStatus", overallStatus);
									
										
									}
									
									
									componentsObj.replace("overallStatus", overallStatus);
							
									if(keyword.equalsIgnoreCase("reportresult")) {
										
										componentsArray.add(componentsObj);
										componentsObj=new JSONObject();	
										checkpointArray=new JSONArray();
										testCaseCount++;
										if(overallStatus.equalsIgnoreCase("pass")) {
											testcasePassCount++;
										}else {
											testcaseFailCount++;
										}
										
										overallStatus="pass";
								
										
									}
								
									
									
									
								}//else part of execute kdt ie if its not execute kdt keyword

								
								//*******************************************************************************
								
								
								
							
								
						
							
								
								
								
								
								
							}//to start from 2nd row of kdt
							
							
							
							
						}//for loop to go through all the rows in excel
						
						
						
						
						kdtObj.put("components", componentsArray);
						kdtObj.put("pageTitle", kdtName);
//						kdtObj.put("failed", 0);
//						kdtObj.put("passed", 0);
						kdtObj.put("OverAll_Status", overall_status);
						kdtObj.put("total_test_cases", testCaseCount);
						testCaseCount=0;
						
						
						tabularArray.add(kdtObj);
						
						
						
						
					}//check if RPT=1
				
				}//check for RPT and xls
				
				
			
			}//for loop for all the files

		
		
			
			chart_labels.add("pass");
			chart_labels.add("fail");
			chart_labels.add("no_run");
			
			chart_values.add( String.valueOf(testcasePassCount));
			chart_values.add( String.valueOf(testcaseFailCount));					
			chart_values.add( String.valueOf(testcaseNoRunCount));
			
			newJsonToolReportObject.put("chart_name", "Transaction Summary");
			newJsonToolReportObject.put("chart_labels", chart_labels);
			newJsonToolReportObject.put("chart_values", chart_values);
			newJsonToolReportObject.put("total_execution_time", "total_execution_time");
			newJsonToolReportObject.put("total_scenario_executed", "total_scenario_executed");
			
			System.out.println("~~~~~~~~~~~~~~~~~~Tool Master tool namee"+toolName);
			if(toolName.equalsIgnoreCase("FAST") || toolName.equalsIgnoreCase("BROWSERSTACK")){	
			
			newJsonToolReportObject.put("tabular_data", tabularArray);
			
			}else if(toolName.equalsIgnoreCase("x_check")){
				
				System.out.println("==Tool Master tool namee"+toolName);
				XcheckAdapter xcheckAdapter=new XcheckAdapter();
				//String xcheckReportPath =GlobalConstants.JENKINS_HOME+ "/jobs/" + jobName
					//	+ "/htmlreports/HTML_Report/MostRecent/";
				String xcheckReportPath =GlobalConstants.JENKINS_HOME+"/workspace/"+jobName+"/"+buildNumber+"/";
				
				//String reportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+subJobs.getSubjob_name()+"/";
				
				//String  xcheckReportPath="D:\\fast_latest_dipika\\ExecutionReports\\Old\\RSA_24052018_03_45_55\\";
				
				JSONArray tabularData=xcheckAdapter.getXcheckReportsForFast(xcheckReportPath, jobName, buildNumber);
				newJsonToolReportObject.put("tabular_data", tabularData);
			}
		}catch(Exception e) {
			newJsonToolReportArray.add(newJsonToolReportObject);
			fetchAndUpdateE2EPart(newJsonToolReportArray, buildHistoryId,e);			
		}
		
		newJsonToolReportArray.add(newJsonToolReportObject);
		fetchAndUpdateE2EPart(newJsonToolReportArray, buildHistoryId);
	
		if (full_tool_status.equalsIgnoreCase("fail")) {
			response = "Failed";
		} else {
			response = "Passed";
		}
		return response;
	}	
	
	
	
	
/*	
	
	public void createFASTStepWiseReportOld(int buildHistoryId) throws Exception {

		logger.debug("in createFASTStepWiseReport ******");
		String jenkinsOutput;

//		String  reportPath="D:\\ExcelReports\\ExcelReports";
//		
		
		String reportPath =GlobalConstants.JENKINS_HOME+ "/jobs/" + jobName
				+ "/htmlreports/HTML_Report/MostRecent/ExcelReports/";
		
		
		logger.debug("FAST Report Path is " + reportPath);
		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();

		for (;;) {
			logger.debug("Reading Jenkins Console");
			// System.out.println("in createFASTStepWiseReport for loop ******");
			jenkinsOutput = readJenkinsConsole.readJenkinsConsole(buildNumber,
					jobName, jenkinsUrl);
			Thread.sleep(8000);
			if (jenkinsOutput.contains("Finished: FAILURE")
					|| jenkinsOutput.contains("Finished: SUCCESS")) {

				break;
			}
		}

		logger.debug("Finished Reading Jenkins Console");
		File f = new File(reportPath);
		Thread.sleep(5000);
		logger.info("Getting started to Update in Mongo DB");
		File[] fList = f.listFiles();

		JSONArray toolReportArray = new JSONArray();
		for (File file : fList) {

			JSONObject toolReportObject = new JSONObject();
			JSONObject chartDataObject = new JSONObject();
			
			JSONArray chart_labels=new JSONArray();
			JSONArray chart_values=new JSONArray();
			
			
			
			JSONArray checkpointArray = new JSONArray();
			String kdtName = null;
			if ((file.toString().contains("_RPT_") && file.toString().contains(
					".xls"))
					|| (file.toString().contains("_RPT_") && file.toString()
							.contains(".xlsx"))) {
				
			
				
				int rptCount=countSubstring("_RPT_", file.toString());
				
				if(rptCount==1){
					
					
					String reportName[]=file.toString().split("\\\\");
					String splitRPT[]=reportName[reportName.length - 1].split("RPT_");
					
					kdtName=splitRPT[0];
									
					FileInputStream inp = new FileInputStream(file.toString());

					Workbook workbook = WorkbookFactory.create(inp);
					Sheet sheet = workbook.getSheetAt(0);

					int iNoOfRows = sheet.getLastRowNum() + 1;
					int iNoOfCols = sheet.getRow(0).getLastCellNum();
					Row row = null;
					Cell cell = null;
					String cellContents = null;

					int passCount = 0;
					int failCount = 0;
					int noRunCount = 0;

					for (int iRowCntr = 0; iRowCntr < iNoOfRows; iRowCntr++) {
						
						System.out.println("Reading row no"+iRowCntr);

						if (iRowCntr >= 2) {
							row = sheet.getRow(iRowCntr);
							if (row == null) {
								System.out.println("row == null, hencing breaking");
								break;
							}

							String executeKDTStr=readCellValue(row.getCell(3));
							String subKDTname = readCellValue(row.getCell(4));
							
							
							 if(subKDTname.contains(".xls")){
								 subKDTname=subKDTname.substring(0, subKDTname.length() - 4);
						
							 }else if(subKDTname.contains(".xlsx")){
								 subKDTname=subKDTname.substring(0, subKDTname.length() - 5);
							 }
							
							if(executeKDTStr.equals("ExecuteKDT")){
								
								cell = row.getCell(0);
								
								cellContents = readCellValue(cell);

								if (cellContents == null || cellContents.equals("") || cellContents.isEmpty()) {
									break;
								}

								JSONArray arr=new JSONArray();
								JSONObject stepObj=new JSONObject();
								//LinkedHashMap<String, String> stepObj =new LinkedHashMap<String, String>();
								stepObj.put("Step_No", cellContents);
								arr.add(stepObj);
								cell = row.getCell(2);
								cellContents = readCellValue(cell);

								if (cellContents != null) {
									JSONObject descObj=new JSONObject();
									descObj.put("Step_Description", cellContents);
									arr.add(descObj);
								} else {
									JSONObject descObj=new JSONObject();
									descObj.put("Step_Description", "");
									arr.add(descObj);
								}

								cell = row.getCell(16);
								cellContents = readCellValue(cell);

								if (cellContents.trim().equalsIgnoreCase("pass")) {
									passCount++;
									JSONObject screenshotObj=new JSONObject();
									screenshotObj.put("Screenshot", "");
									arr.add(screenshotObj);
								} else if (cellContents.trim().equalsIgnoreCase("fail")) {
								failCount++;
								cell = row.getCell(15);
								cellContents = readCellValue(cell);
								
								//http://localhost:8087/jenkins/job/genericsubjobFAST31/ws/3/ExcelReports/Images/
								
								
								String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/ExcelReports/Images/"+cellContents;

								//String screenshotUrl=GlobalConstants.JENKINS_HOME+"/workspace/"+jobName+"/"+buildNumber+"/Images/"+cellContents;
								JSONObject screenshotObj=new JSONObject();
								screenshotObj.put("Screenshot", screenshotUrl);
								arr.add(screenshotObj);} else if (cellContents.trim().equalsIgnoreCase(
										"no run")) {
									noRunCount++;
									JSONObject screenshotObj=new JSONObject();
									screenshotObj.put("Screenshot", "");
									arr.add(screenshotObj);
								}

							
								JSONObject statusObj=new JSONObject();
								statusObj.put("Execution_Status", cellContents);
								arr.add(statusObj);

								
								cell = row.getCell(17);
								cellContents = readCellValue(cell);

								JSONObject timeObj=new JSONObject();
								timeObj.put("Exeuction_Time", cellContents);
								arr.add(timeObj);
						
								//stepObj.put("Exeuction_Time", cellContents);

								checkpointArray.add(arr);
								System.out.println(checkpointArray);
								
								System.out.println("cell contains ExecuteKDT");
								for (File reportFile : fList) {
									System.out.println(reportFile.toString());
									//cell=
								
									
									if ((reportFile.toString().contains("_RPT_") && reportFile.toString().contains(".xls"))
											|| (reportFile.toString().contains("_RPT_") && reportFile.toString()
													.contains(".xlsx"))) {
										
										int subRptCount=countSubstring("_RPT_", reportFile.toString());
										
										if(subRptCount>1){
											
											if(reportFile.toString().contains(subKDTname) && reportFile.toString().contains(kdtName)){
												
												
												FileInputStream inpOfSubKDT = new FileInputStream(reportFile.toString());

												Workbook subKDTworkbook = WorkbookFactory.create(inpOfSubKDT);
												Sheet subKDTsheet = subKDTworkbook.getSheetAt(0);

												int iNoOfRowsInSubKDT = subKDTsheet.getLastRowNum() + 1;
												//int iNoOfColsInSubKDT = subKDTsheet.getRow(0).getLastCellNum();
												Row subKDTrow = null;
												Cell subKDTcell = null;
												String subKDTcellContents = null;
												
												
												for (int iRowCntrsubKDT = 0; iRowCntrsubKDT < iNoOfRowsInSubKDT; iRowCntrsubKDT++) {

													if (iRowCntrsubKDT >= 2) {
														subKDTrow = subKDTsheet.getRow(iRowCntrsubKDT);
														if (subKDTrow == null) {
															break;
														}
												
														subKDTcell = subKDTrow.getCell(0);

														subKDTcellContents = readCellValue(subKDTcell);

														if (subKDTcellContents == null || subKDTcellContents.equals("") ||subKDTcellContents.isEmpty()) {
															break;
														}
														JSONArray arry=new JSONArray();
														JSONObject subStepObj=new JSONObject();
														//arry.add(subStepObj);
													//	LinkedHashMap<String, String> subStepObj =new LinkedHashMap<String, String>();
														
														//stepObj.replace(arg0, arg1)
														subStepObj.put("Step_No", subKDTcellContents);
														arry.add(subStepObj);
														
														subKDTcell = subKDTrow.getCell(2);
														subKDTcellContents = readCellValue(subKDTcell);

														if (subKDTcellContents != null) {
															JSONObject subDescObj=new JSONObject();
															subDescObj.put("Step_Description", subKDTcellContents);
															arry.add(subDescObj);
														} else {
															JSONObject subDescObj=new JSONObject();
															subDescObj.put("Step_Description", "");
															arry.add(subDescObj);
														}

														subKDTcell = row.getCell(16);
														subKDTcellContents = readCellValue(subKDTcell);

														if (subKDTcellContents.trim().equalsIgnoreCase("pass")) {
															passCount++;
															JSONObject screenshotObj=new JSONObject();
															screenshotObj.put("Screenshot", "");
															arr.add(screenshotObj);
														} else if (subKDTcellContents.trim().equalsIgnoreCase("fail")) {
														
														failCount++;
														cell = row.getCell(15);
														cellContents = readCellValue(cell);
														
														String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/ExcelReports/Images/"+cellContents;
														JSONObject screenshotObj=new JSONObject();
														screenshotObj.put("Screenshot", screenshotUrl);
														arr.add(screenshotObj);} else if (subKDTcellContents.trim().equalsIgnoreCase(
																"no run")) {
															noRunCount++;
															JSONObject screenshotObj=new JSONObject();
															screenshotObj.put("Screenshot", "");
															arr.add(screenshotObj);
														}

														JSONObject subStatusObj=new JSONObject();
														subStatusObj.put("status", subKDTcellContents);
														arry.add(subStatusObj);

														subKDTcell = row.getCell(17);
														subKDTcellContents = readCellValue(subKDTcell);

														JSONObject subTimeObj=new JSONObject();
														subTimeObj.put("Exeuction_Time", subKDTcellContents);
														arry.add(subTimeObj);

														checkpointArray.add(arry);
														System.out.println(checkpointArray);
														
													}//end of if loop that checks the RPT count
													
													
												}//end of for-loop of subkdt
												
												break;
											}//checking both the kdt names in the file name
											
											
										}//checking count of RPT>1
										
										
										
									}//checks if xls or xlsx
										
								}//goes thourgh al reports
								
								
							}else{
							
							cell = row.getCell(0);

							cellContents = readCellValue(cell);

							if (cellContents == null || cellContents.isEmpty() ||cellContents.equals("")) {
								break;
							}
							
							if(cellContents==""){
								break;								
							}
							
							JSONArray arr=new JSONArray();
							JSONObject stepObj=new JSONObject();
							//LinkedHashMap<String, String> stepObj =new LinkedHashMap<String, String>();
							
							stepObj.put("Step_No", cellContents);
							arr.add(stepObj);
							
							cell = row.getCell(2);
							cellContents = readCellValue(cell);

							if (cellContents != null) {
								JSONObject descObj=new JSONObject();
								descObj.put("Step_Description", cellContents);
								arr.add(descObj);
							} else {
								JSONObject descObj=new JSONObject();
								descObj.put("Step_Description", "");
								arr.add(descObj);
							}

							cell = row.getCell(16);
							cellContents = readCellValue(cell);
							
							JSONObject statusObj=new JSONObject();
							statusObj.put("status", cellContents);
							arr.add(statusObj);
							
							if (cellContents.trim().equalsIgnoreCase("pass")) {
								passCount++;
								JSONObject screenshotObj=new JSONObject();
								screenshotObj.put("Screenshot", "");
								arr.add(screenshotObj);
							} else if (cellContents.trim().equalsIgnoreCase("fail")) {
							
							failCount++;
							cell = row.getCell(15);
							cellContents = readCellValue(cell);
							
							String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/ExcelReports/Images/"+cellContents;
							JSONObject screenshotObj=new JSONObject();
							screenshotObj.put("Screenshot", screenshotUrl);
							arr.add(screenshotObj);} else if (cellContents.trim().equalsIgnoreCase(
									"no run")) {
								noRunCount++;
								JSONObject screenshotObj=new JSONObject();
								screenshotObj.put("Screenshot", "");
								arr.add(screenshotObj);
							}

							

							cell = row.getCell(17);
							cellContents = readCellValue(cell);
							
							JSONObject timeObj=new JSONObject();
							timeObj.put("Exeuction_Time", cellContents);
							arr.add(timeObj);
							
						//	JSONObject orderedStepDet = new JSONObject();

							
							checkpointArray.add(arr);
							System.out.println(checkpointArray);
							
						}

						}

					}
					
					
				
					chart_labels.add("pass");
					chart_labels.add("fail");
					chart_labels.add("no_run");
					
					chart_values.add( String.valueOf(passCount));
					chart_values.add( String.valueOf(failCount));					
					chart_values.add( String.valueOf(noRunCount));
					
					
					
					chartDataObject.put("fail",String.valueOf(failCount));
					chartDataObject.put("pass", String.valueOf(passCount));
					chartDataObject.put("no_run", String.valueOf(noRunCount));
					
					toolReportObject.put("chart_name", kdtName);
					toolReportObject.put("tabular_data", checkpointArray);
					//toolReportObject.put("chart_data", chartDataObject);
					toolReportObject.put("chart_labels", chart_labels);
					toolReportObject.put("chart_values", chart_values);

					toolReportArray.add(toolReportObject);
					System.out.println(toolReportArray);
					
				}
			
			}

		}

		fetchAndUpdateE2EPart(toolReportArray, buildHistoryId);

	}*/

	private void fetchAndUpdateE2EPart(JSONArray toolReportArray,
			int buildHistoryId,Exception e) throws Exception {

		logger.info("Updating Automation Testing (Tool Report) in MongoDB");

		// System.out.println("IN E2E PART");

		JSONParser parser = new JSONParser();
		// for readCount----------------
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
		// end-------------
		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2 = (JSONObject) obj1.get("AutomationTesting");
		
		//JSONObject obj3 = (JSONObject) obj2.get("Tools");
		
		//####################$$$$$$$$$$$$$$$$$$$$$$$$$#################$$$$$$$$$$$$$$$$$$$########################
		
		JSONArray toolsArray = (JSONArray) obj2.get("Tools");
		for (int i = 0; i < toolsArray.size(); i++) {

			JSONObject toolsObject = (JSONObject) toolsArray.get(i);
			
			String tool_name = (String) toolsObject.get("tool_name");

			//String temp=(String)toolsObject.get("tool_status");
			//toolsObject.replace(toolsObject.get("tool_status").toString(), tool_status);
			if (tool_name.equals(jobName)) {
				
				JSONArray newToolReportArray = (JSONArray) toolsObject.get("ToolReport");
				if(toolReportArray.toString()=="null") {
					toolReportArray=new JSONArray();
				}
				toolsObject.replace("ToolReport", newToolReportArray,toolReportArray);
				if(e.getClass().getSimpleName().equals("NullPointerException")) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNullPointerException(e,toolsObject);
				}else if(e.getClass().getSimpleName().equals("SecurityException") || e.getClass().getSimpleName().equals("IOException")) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleSecurityException(e,toolsObject);					
				}else if(e.getClass().getSimpleName().equals("NumberFormatException") || e.getClass().getSimpleName().equals("StringIndexOutOfBoundsException") ||  e.getClass().getSimpleName().equals("ArrayIndexOutOfBoundsException") || e.getClass().getSimpleName().equals("InvalidFormatException")) {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleNumberFormatException(e,toolsObject);					
				}else {
					ErrorHandler errorHandler=new ErrorHandler();
					errorHandler.handleUnanticipatedException(e,toolsObject);					
				}

				//newToolReportArray=toolReportArray;
				
				
				
			}
		}
		
		if(full_tool_status.equalsIgnoreCase("fail")) {
			
			
		
		JSONObject lbc = (JSONObject) obj1.get("LiveBuildConsole");
		JSONArray ta = (JSONArray) lbc.get("tools");
		
		for (int i = 0; i < ta.size(); i++) {

			JSONObject toolsObject = (JSONObject) ta.get(i);
			
			String tool_name = (String) toolsObject.get("tool_name");

			//String temp=(String)toolsObject.get("tool_status");
		
			
			
			if (tool_name.equals(jobName)) {

				toolsObject.replace("tool_status", toolsObject.get("tool_status"), "Failed");
				//toolsObject.replace(toolsObject.get("tool_status"), tool_status);
				
				
			}
		}
		
		}
		
		MongoDBOperations
				.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistoryId);
		System.out.println(finalPlatoJsonObj);
		logger.info("Finsihed Updating in MongoDB for FAST");

	}
	
	private void fetchAndUpdateE2EPart(JSONArray toolReportArray,
			int buildHistoryId) throws Exception {

		logger.info("Updating Automation Testing (Tool Report) in MongoDB");

		// System.out.println("IN E2E PART");

		JSONParser parser = new JSONParser();
		// for readCount----------------
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
		// end-------------
		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2 = (JSONObject) obj1.get("AutomationTesting");
		
		//JSONObject obj3 = (JSONObject) obj2.get("Tools");
		
		//####################$$$$$$$$$$$$$$$$$$$$$$$$$#################$$$$$$$$$$$$$$$$$$$########################
		
		JSONArray toolsArray = (JSONArray) obj2.get("Tools");
		for (int i = 0; i < toolsArray.size(); i++) {

			JSONObject toolsObject = (JSONObject) toolsArray.get(i);
			
			String tool_name = (String) toolsObject.get("tool_name");

			//String temp=(String)toolsObject.get("tool_status");
			//toolsObject.replace(toolsObject.get("tool_status").toString(), tool_status);
			if (tool_name.equals(jobName)) {

				
				JSONArray newToolReportArray = (JSONArray) toolsObject.get("ToolReport");
				if(toolReportArray.toString()=="null") {
					toolReportArray=new JSONArray();
				}
				toolsObject.replace("ToolReport", newToolReportArray,toolReportArray);

				//newToolReportArray=toolReportArray;
				
				
				
			}
		}
		
		if(full_tool_status.equalsIgnoreCase("fail")) {
			
			
		
		JSONObject lbc = (JSONObject) obj1.get("LiveBuildConsole");
		JSONArray ta = (JSONArray) lbc.get("tools");
		
		for (int i = 0; i < ta.size(); i++) {

			JSONObject toolsObject = (JSONObject) ta.get(i);
			
			String tool_name = (String) toolsObject.get("tool_name");

			//String temp=(String)toolsObject.get("tool_status");
		
			
			
			if (tool_name.equals(jobName)) {

				System.out.println(">>>>>>>>>>>>>>>>>>>>>");
				toolsObject.replace("tool_status", toolsObject.get("tool_status"), "Failed");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>");
				//toolsObject.replace(toolsObject.get("tool_status"), tool_status);
				
				
			}
		}
		
		}
		
		MongoDBOperations
				.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistoryId);
		System.out.println(finalPlatoJsonObj);
		logger.info("Finsihed Updating in MongoDB for FAST");

	}

	public String readCellValue(Cell cell) {
		String cellValue = null;

		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cellValue = cell.getStringCellValue();

			cellValue = cellValue.trim();
		}
		if (cellValue == null) {
			cellValue = "";
		}
		return cellValue;
	}

}
