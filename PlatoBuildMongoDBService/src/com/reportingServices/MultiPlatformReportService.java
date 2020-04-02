package com.reportingServices;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;

public class MultiPlatformReportService {

	private static final Logger logger = Logger
			.getLogger(MultiPlatformReportService.class);

	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status = "Passed";
	private String jenkinsUrl;

	String jenkinsConsoleOutput = "";

	public MultiPlatformReportService(int buildHistoryId, int buildNumber,
			String jobName, String jenkinsUrl) {
		logger.info("inside FastReportService Constructor");
		this.buildNumber = buildNumber;
		this.jobName = jobName;
		this.buildHistoryId = buildHistoryId;
		this.jenkinsUrl = jenkinsUrl;
	}

	public String readFASTJenkinsTotalResponse() throws Exception {
		
		ArrayList<String> checkedPoints=new ArrayList<String>();
		
		
		//fetch modules
		ArrayList<Integer> moduleList=new ArrayList<Integer>();
		for(int i=0;i<moduleList.size();i++){
		
			int currentModule=moduleList.get(i);
			//fetch checkpoint for particular module
			
			ArrayList<Integer> checkpointsList=new ArrayList<Integer>();
			for(int j=0;j<checkpointsList.size();j++){
				
				int currentCheckPoint=checkpointsList.get(j);
				
				
				//if jenkins console contains pass or fail creiteria for current check point
					//update in mongodb and add in checked points list
				
				//else read the console again
				
				
				
			
			}
		}

		logger.debug("inside readFASTJenkinsTotalResponse");
		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();

		// int count =0;s
		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		logger.info("Reading FAST Execution from Jenkins Console ");

		for (;;) {
			// count++;
			Thread.sleep(10000);
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
			System.out.println(jenkinsConsoleOutput + "fast console");
			// if(count==5){
			/*
			 * if(jenkinsConsoleOutput.contains("FAST Execution Completed")){
			 * System.out.println(
			 * "jenkins console output contains******** Fast Execution Completed "
			 * ); break; }
			 */

			if (jenkinsConsoleOutput.contains("Finished: SUCCESS")
					|| jenkinsConsoleOutput.contains("Finished: FAILURE")
					|| jenkinsConsoleOutput.contains("Finished: ABORTED")
					|| jenkinsConsoleOutput.contains("Finished: UNSTABLE")
					|| jenkinsConsoleOutput.contains("FATAL: null")) {
				System.out
						.println("jenkins console output contains******** Fast Execution Completed ");
				break;
			}
			// return finalPlatoJsonObj;
		}

		logger.info("COMPLETED reading FAST Execution from Jenkins Console ");
		// return finalPlatoJsonObj;

		if (status.equalsIgnoreCase("Failed")) {
			response = "Failed";
		} else if (status.equalsIgnoreCase("Aborted")) {
			response = "Aborted";
		} else {
			response = "Passed";
		}
		return "Failed";
		// return jenkinsConsoleOutput;

	}

	private JSONObject readFASTJenkinsConsoleAndUpdateStatus(
			String jenkinsConsoleOutput) throws Exception {
		// TODO Auto-generated method stub
		String consoleOutput;
		consoleOutput = jenkinsConsoleOutput;
		// String jenkinsConsoleOutput=readJenkinsConsole();

		// to avoid traversing whole console output again and again
		/*
		 * if(jenkinsConsoleOutputForParsing.equalsIgnoreCase("")){
		 * jenkinsConsoleOutputForParsing=jenkinsConsoleOutput; }else{
		 * consoleOutput
		 * =jenkinsConsoleOutput.split(jenkinsConsoleOutputForParsing);
		 * jenkinsConsoleOutputForParsing=jenkinsConsoleOutput;
		 * jenkinsConsoleOutput=consoleOutput[1]; }
		 */

		if (!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))) {
			// String
			// jenkinsOutput[]=jenkinsConsoleOutput.split(this.jenkinsConsoleOutput);
			if (jenkinsConsoleOutput.contains(this.jenkinsConsoleOutput.trim())) {
				logger.info("Removing previous console");
				jenkinsConsoleOutput = jenkinsConsoleOutput.replace(
						this.jenkinsConsoleOutput.trim(), "");
			}
			/*
			 * jenkinsConsoleOutput=jenkinsOutput[1];
			 * System.out.println("jenkins output 0 ***"+jenkinsOutput[0]);
			 * System.out.println("jenkins output 1 ***"+jenkinsOutput[1]);
			 */
		} else {
			logger.info("this.jenkinsConsoleOutput is blank");
		}

		ArrayList<Integer> moduleOrderList = new ArrayList<Integer>();
		Long module_order_num;
		Long test_case_order_num;
		// HashMap<Integer,String> moduleList = new HashMap<Integer,String>();

		JSONParser parser = new JSONParser();
		JSONObject finalPlatoJsonObj;
		finalPlatoJsonObj = null;
		String finalPlatoJsonString = null;
		while (finalPlatoJsonString == null) {
			logger.debug("Inside while loop. Fetching template for build history id :"
					+ buildHistoryId + " and job name :  " + jobName);
			finalPlatoJsonString = MongoDBOperations
					.fetchJsonFromMongoDB(buildHistoryId);

		}
		logger.debug("Retrived data : " + finalPlatoJsonString);
		finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);

		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2 = (JSONObject) obj1.get("LiveBuildConsole");

		JSONObject obj3 = (JSONObject) obj2.get("application_under_test");

		JSONArray subJobList = (JSONArray) obj3.get("tools");

		int len = subJobList.size();
		for (int i = 0; i < len; i++) {
			JSONObject obj4 = (JSONObject) subJobList.get(i);

			obj4.replace("build_number", "", buildNumber);
			String jName = (String) obj4.get("tool_name");
			if (jName.equalsIgnoreCase(jobName)) {
				JSONArray arrModuleKdt = (JSONArray) obj4.get("module_kdt");

				for (int j = 0; j < arrModuleKdt.size(); j++) {
					JSONObject obj5 = (JSONObject) arrModuleKdt.get(j);

					//String module_name = (String) obj5.get("module_name");
					module_order_num = (Long) obj5.get("module_order");
					// num=Integer.valueOf(order_num.intValue());
					moduleOrderList.add(Integer.valueOf(module_order_num.intValue()));

					Collections.sort(moduleOrderList);
					System.out.println(moduleOrderList);
				}
			}

		}

		logger.info("Reading FAST Execution from Jenkins Console ");

		// JSONArray arrCheckPoints= (JSONArray) obj5.get("test_cases");
		for (int m = 0; m < len; m++) {
			JSONObject obj4 = (JSONObject) subJobList.get(m);

			obj4.replace("build_number", "", buildNumber);
			String jName = (String) obj4.get("tool_name");
			if (jName.equalsIgnoreCase(jobName)) {

				JSONArray arrModuleKdt = (JSONArray) obj4.get("module_kdt");

				if (jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
					System.out
							.println("Updating FAST Status in Mongo ---------------------------------"
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

				ArrayList<Integer> checkpointsOrderList = new ArrayList<Integer>();
				int noOfModules = moduleOrderList.size();
				for (int i = 0; i < noOfModules; i++) {

					int currentModule = moduleOrderList.get(i);

					for (int j = 0; j < arrModuleKdt.size(); j++) {

						JSONObject obj5 = (JSONObject) arrModuleKdt.get(j);

						Long module_order = (Long) obj5.get("module_order");

						if (currentModule == module_order) {
							JSONArray arrCheckPointst = (JSONArray) obj5
									.get("test_cases");

							for (int k = 0; k < arrCheckPointst.size(); k++) {
								JSONObject obj6 = (JSONObject) arrCheckPointst
										.get(k);

								test_case_order_num=(Long) obj6.get("test_case_order");
								
								String test_case_name = (String) obj6
										.get("test_case_name");

								checkpointsOrderList.add(Integer.valueOf(test_case_order_num.intValue()));

								Collections.sort(checkpointsOrderList);
								
								
								
								System.out.println(checkpointsOrderList);

							}

							for (int k = 0; k < arrCheckPointst.size(); k++) {
								JSONObject obj6 = (JSONObject) arrCheckPointst
										.get(k);
								String testCaseName = (String) obj6
										.get("test_case_name");
								Long test_case_order=(Long) obj6.get("test_case_order");

								String testPassCriteria = (String) obj6
										.get("pass_criteria");
								String testFailCriteria = (String) obj6
										.get("fail_criteria");

								System.out
										.println("test case name : passCriteria : failCriteria "
												+ testCaseName
												+ " "
												+ " "
												+ testPassCriteria
												+ " "
												+ testFailCriteria);

								if (checkpointsOrderList.contains(test_case_order.intValue())) {
									int position = checkpointsOrderList
											.indexOf(test_case_order.intValue());
									// pass criteria
									if (testPassCriteria.contains(",")) {
										System.out
												.println("testPassCriteria contains ,");
										String testPassCriteriaArray[] = testPassCriteria
												.split(",");
										for (String passCriteria : testPassCriteriaArray) {
											if (jenkinsConsoleOutput
													.contains(passCriteria)) {

												// added later
												if (((String) (obj6
														.get("status")))
														.equalsIgnoreCase("")) {
													obj6.replace("status", "",
															"pass");
													for (int z = 0; z <= position; z++) {
														checkpointsOrderList
																.remove(z);
													}
													System.out
															.println("status of test case "
																	+ testCaseName
																	+ " : "
																	+ obj6.get("status"));

												} else {
													continue;
												}

											}
										}
									} else {
										System.out
												.println("testPassCriteria dosen't contains ,");
										if (jenkinsConsoleOutput
												.contains(testPassCriteria)) {

											// added later
											if (((String) (obj6.get("status")))
													.equalsIgnoreCase("")) {
												obj6.replace("status", "",
														"pass");
												for (int z = 0; z <= position; z++) {
													checkpointsOrderList.remove(z);
												}
												System.out
														.println("status of test case "
																+ testCaseName
																+ " : "
																+ obj6.get("status"));

											} else {
												continue;
											}
										}
									}

									// fail criteria
									if (testFailCriteria.contains(",")) {
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
														.equalsIgnoreCase("")) {
													obj6.replace("status", "",
															"fail");
													for (int z = 0; z <= position; z++) {
														checkpointsOrderList
																.remove(z);
													}
													System.out
															.println("status of test case "
																	+ testCaseName
																	+ " : "
																	+ obj6.get("status"));

												} else {
													continue;
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
													.equalsIgnoreCase("")) {
												obj6.replace("status", "",
														"fail");
												for (int z = 0; z <= position; z++) {
													checkpointsOrderList.remove(z);
												}
												System.out
														.println("status of test case "
																+ testCaseName
																+ " : "
																+ obj6.get("status"));

											} else {
												continue;
											}

											if (status
													.equalsIgnoreCase("Passed")) {
												status = "Failed";
											}
										

									}else {
										System.out.println("checking the next check point");;
									}

								} else {
									System.out.println("check point not present in check point list");
									break;
								}// end of if loop if checkpoint List contains
									// testcase name

							}

							/*if (checkpointsOrderList.isEmpty()) {
								System.out.println("check point list empty, going for next module");
								continue;
							} else {
								System.out.println("check point list not empty, thus breaking and reading console again");
								break;
							}*/

						}

						// end of if loop, checked current module
					}

					if (checkpointsOrderList.isEmpty()) {
						System.out.println("check point list empty, going for next module");
						continue;
					} else {
						System.out.println("check point list not empty, thus breaking and reading console again");
						break;
					}

				} // end of forloop for modules(noOfModules)
			}// end if (checks jobName)

		}// end of subjob List forloop

		this.jenkinsConsoleOutput = consoleOutput;
		//
		// update the json in mongodefDb

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

	public void createFASTStepWiseReport(int buildHistoryId) throws Exception {

		logger.debug("in createFASTStepWiseReport ******");
		// System.out.println("in createFASTStepWiseReport ******");

		// JSONObject E2EObject=new JSONObject();
		// C:/Users/Appladmin/.jenkins/jobs
		// String
		// reportPath="C:/"+jobName+"/htmlreports/HTML_Report/MostRecent/ExcelReports";
		String jenkinsOutput;

		String reportPath = "C:/Users/Appladmin/.jenkins/jobs/" + jobName
				+ "/htmlreports/HTML_Report/MostRecent/ExcelReports/";

		// String
		// reportPath="D:\\FAST\\FAST-Setup\\FAST\\00baseline\\ExecutionReports\\MostRecent\\ExcelReports";

		logger.debug("FAST Report Path is " + reportPath);
		// String reportPath="H:/platowar/ExcelReports";

		// System.out.println("report path is*** "+reportPath);
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
		// logger.info("Getting started to Update in Mongo DB");
		File[] fList = f.listFiles();

		JSONArray businessProcessArray = new JSONArray();
		for (File file : fList) {

			JSONObject kdtObject = new JSONObject();
			JSONArray testCaseArray = new JSONArray();
			String kdtName = null;
			if ((file.toString().contains("_RPT_") && file.toString().contains(
					".xls"))
					|| (file.toString().contains("_RPT_") && file.toString()
							.contains(".xlsx"))) {

				String extractKdtName[] = file.toString().split("_RPT");

				String arr[] = extractKdtName[0].split("\\\\");

				kdtName = arr[arr.length - 1];

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

					if (iRowCntr >= 2) {
						row = sheet.getRow(iRowCntr);
						if (row == null) {
							break;
						}
						cell = row.getCell(0);

						cellContents = readCellValue(cell);

						if (cellContents == null) {
							break;
						}

						JSONObject stepObj = new JSONObject();
						stepObj.put("Step_No", cellContents);

						cell = row.getCell(1);
						cellContents = readCellValue(cell);

						// JSONObject stepDescriptionObj=new JSONObject();
						if (cellContents != null) {
							stepObj.put("Step_Description", cellContents);
						} else {
							stepObj.put("Step_Description", "");
						}

						cell = row.getCell(16);
						cellContents = readCellValue(cell);

						if (cellContents.trim().equalsIgnoreCase("pass")) {
							passCount++;
						} else if (cellContents.trim().equalsIgnoreCase("fail")) {
							failCount++;
						} else if (cellContents.trim().equalsIgnoreCase(
								"no run")) {
							noRunCount++;
						}

						// JSONObject stepStatusObj=new JSONObject();
						stepObj.put("Execution_Status", cellContents);

						cell = row.getCell(17);
						cellContents = readCellValue(cell);

						// JSONObject stepTimeObj=new JSONObject();
						stepObj.put("Exeuction_Time", cellContents);

						testCaseArray.add(stepObj);
						System.out.println(testCaseArray);

					}

				}

				kdtObject.put("bp_name", kdtName);
				kdtObject.put("fail", String.valueOf(failCount));
				kdtObject.put("pass", String.valueOf(passCount));
				kdtObject.put("no-run", String.valueOf(noRunCount));
				kdtObject.put("TestSteps", testCaseArray);

				businessProcessArray.add(kdtObject);
			}

		}

		fetchAndUpdateE2EPart(businessProcessArray, buildHistoryId);

		// return businessProcessArray;

	}

	private void fetchAndUpdateE2EPart(JSONArray businessProcessArray,
			int buildHistoryId) throws Exception {
		// TODO Auto-generated method stub

		logger.info("Updating E2E in MongoDB");

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
		// JSONObject finalPlatoJsonObj = (JSONObject)
		// parser.parse(jenkinsConsoleOutput);

		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2 = (JSONObject) obj1.get("LiveBuildConsole");

		JSONObject e2eObject = (JSONObject) obj2.get("E2E");

		System.out.println("Before fetching E2E Array");
		JSONArray bArray = (JSONArray) e2eObject.get("BusinessProcess");
		System.out.println("After fetching E2E Array");

		for (int i = 0; i < businessProcessArray.size(); i++) {

			JSONObject kdtObject = (JSONObject) businessProcessArray.get(i);
			// bArray.add(kdtObject);
			((JSONArray) e2eObject.get("BusinessProcess")).add(kdtObject);
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
		// Rohit
		if (cellValue == null) {
			cellValue = "";
			// System.out.println("cellout>"+cellValue);
		}
		return cellValue;
	}

}
