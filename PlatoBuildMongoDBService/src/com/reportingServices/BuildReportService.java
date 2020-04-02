package com.reportingServices;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */

public class BuildReportService {
	
	private static final Logger logger = Logger
			.getLogger(BuildReportService.class);
	
	private String status = "Passed";
	private boolean lastCheckpoint=false;
	
	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String jenkinsUrl;
	String jenkinsConsoleOutput = "";
	public BuildReportService(int buildHistoryId,String jobName,int buildNumber){
		this.buildHistoryId=buildHistoryId;
		this.jobName=jobName;
		System.out.println(jobName); 
		this.buildNumber=buildNumber;
		jenkinsUrl="";
	}
	
	public boolean shouldPostBuildJobsContinue() throws Exception{
		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();
		
		logger.debug("inside read BUILD JenkinsTotalResponse");
	

		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		logger.info("Reading Build Execution from Jenkins Console ");
		
		for(;;) 
		{
			Thread.sleep(5000);
			jenkinsConsoleOutput = readJenkinsConsole.readJenkinsConsole(
					buildNumber, jobName, jenkinsUrl);
			String consoleOutput=jenkinsConsoleOutput;
//			finalPlatoJsonObj = readFASTJenkinsConsoleAndUpdateStatus(consoleOutput);
//			if (finalPlatoJsonObj == null) {
//				logger.error("readFASTJenkinsTotalResponse: Template not found with Build History Id : "
//						+ buildHistoryId);
//				logger.error("Throwing Exception ");
//				throw new Exception(
//						"Plato Template Doesnt Exists for build History id :"
//								+ buildHistoryId);
//			}
			logger.info(jenkinsConsoleOutput + "text above is build console");

			if (jenkinsConsoleOutput.contains("Finished: SUCCESS")
					|| jenkinsConsoleOutput.contains("Finished: FAILURE")
					|| jenkinsConsoleOutput.contains("Finished: ABORTED")
					|| jenkinsConsoleOutput.contains("Finished: UNSTABLE")
					|| jenkinsConsoleOutput.contains("FATAL: null")) {
				logger.info("Build Execution Completed ");
				
				System.out.println("BUILD EXECUTION COMPLETED!!!");
				return true;
				
			}
		}
		
		//return true;
			
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

				
				String moduleName = (String) obj4.get("tool_name");
				
				if (moduleName.equalsIgnoreCase(jobName)) {
					obj4.replace("build_number", " ", buildNumber);
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

				String moduleName = (String) obj4.get("tool_name");
				
				//checking job name with thread job
				if (moduleName.equalsIgnoreCase(jobName)) {
					
					JSONArray modules = (JSONArray) obj4.get("modules");
					
					String tool_status = (String) obj4.get("tool_status");
					
					if (jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
						System.out
								.println("Updating FAST Status in Mongo ---------------------------------"
										+ moduleName);
						obj4.replace("tool_status", tool_status, "Passed");
					} else if (jenkinsConsoleOutput.contains("Finished: FAILURE")) {
						System.out
								.println("Updating FAST Status in Mongo ---------------------------------"
										+ moduleName);
						obj4.replace("tool_status", tool_status, "Failed");
						status = "Failed";

					} else if (jenkinsConsoleOutput.contains("Finished: ABORTED")) {
						obj4.replace("tool_status", tool_status, "Aborted");
						status = "Aborted";
					} else if (jenkinsConsoleOutput != null
							|| !(jenkinsConsoleOutput.contains("Finished: FAILURE"))
							|| !(jenkinsConsoleOutput.contains("Finished: SUCCESS"))
							|| !(jenkinsConsoleOutput.contains("Finished: ABORTED"))) {
						obj4.replace("tool_status", tool_status, "In_Progress");
						System.out
								.println("In Progress Updated in Mongo DB----------------------------------------"
										+ moduleName);

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
	
	
	
	

}
