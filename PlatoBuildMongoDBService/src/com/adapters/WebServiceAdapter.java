package com.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongo.constants.GlobalConstants;

public class WebServiceAdapter {

	private static final Logger logger=Logger.getLogger(WebServiceAdapter.class);

	public JSONArray getReport(String reportPath, String jobName, int buildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception {

		JSONArray toolReportArray = new JSONArray();
		JSONObject toolReportObj = new JSONObject();

		JSONArray tabularDataArray = new JSONArray();
		JSONObject tabularDataObj;
		//to skip the 2nd line of the csv
		int count = 0;
		int totalPassCount=0;
		int totatFailCount=0;
		//int totalScenariosCount=0;
		
		logger.info("WBS_SUE"+reportPath+"result.csv");
		try{
			System.out.println(reportPath+"result.csv");

			Reader reader = Files.newBufferedReader(Paths.get(reportPath+"result.csv"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : csvParser) {
				// Accessing Values by Column Index
				logger.info("reading result.csv");
				count++;

				String scenario_name = csvRecord.get(0);
				String test_case_name = csvRecord.get(1);
				String status = csvRecord.get(2);
				//String error_count = csvRecord.get(3);
				//String responseTime = csvRecord.get(3);
				String response_message = csvRecord.get(3);
				String output_id = csvRecord.get(4);
				// String country = csvRecord.get(6);

				logger.info("Record No - " + csvRecord.getRecordNumber());
				logger.info("---------------");
				logger.info("scenario_name : " + scenario_name);
				logger.info("status : " + status);
//				logger.info("error_count : " + error_count);
//				logger.info("responseTime : " + responseTime);
				logger.info("response_message : " + response_message);
				logger.info("output_id : " + output_id);
				logger.info("---------------\n\n");

				if (count > 1) {
					if (tabularDataArray.size() > 0) {
						boolean scenarioCaseExists = checkScenarioExists(tabularDataArray, scenario_name);

						if (scenarioCaseExists) {
							for (int j = 0; j < tabularDataArray.size(); j++) {
								JSONObject tabular_i = (JSONObject) tabularDataArray.get(j);
								String scenarioName = (String) tabular_i.get("pageTitle");
								if (scenarioName.contains(scenario_name)) {
									JSONObject tabular_Data_Obj = tabular_i;
									
									if(status.equalsIgnoreCase("fail")) {
										String OverAll_Status=(String) tabular_i.get("OverAll_Status");
										tabular_i.replace("OverAll_Status", OverAll_Status, "fail");
									}
									JSONArray components_Array = (JSONArray) tabular_Data_Obj.get("components");
									JSONObject components_Obj = new JSONObject();
									JSONArray componentsArray_A = new JSONArray();
									
									
									boolean testCaseExists=checkTestCaseExists(components_Array, test_case_name);
									
									if(!testCaseExists) {
										
										
									
									components_Obj.put("componentid", test_case_name);
									//components_Obj.put("Response_Time", responseTime);
									components_Obj.put("Response_Message", response_message);
									components_Obj.put("OutputID", output_id);
									components_Obj.put("componentsArray", componentsArray_A);
									if (status.equalsIgnoreCase("pass")) {
										components_Obj.put("overallStatus", "pass");
									} else {
										components_Obj.put("overallStatus", "fail");
									}

									components_Array.add(components_Obj);

									break;
									}else {
										break;
									}
								}

							}

						} else {
							tabularDataObj = new JSONObject();

							JSONArray components_Array = new JSONArray();
							JSONObject components_Obj = new JSONObject();

							JSONArray componentsArray_A = new JSONArray();

							components_Obj.put("componentid", test_case_name);
							//components_Obj.put("Response_Time", responseTime);
							components_Obj.put("Response_Message", response_message);
							components_Obj.put("OutputID", output_id);
							components_Obj.put("componentsArray", componentsArray_A);
							if (status.equalsIgnoreCase("pass")) {								
								components_Obj.put("overallStatus", "pass");
							} else {								
								components_Obj.put("overallStatus", "fail");
							}

							components_Array.add(components_Obj);
							//totalScenariosCount++;
							tabularDataObj.put("components", components_Array);
							tabularDataObj.put("pageTitle", scenario_name);
							tabularDataObj.put("OverAll_Status", status);
							//tabularDataObj.put("total_test_cases", 2);

							tabularDataArray.add(tabularDataObj);

						}

					} else {
						tabularDataObj = new JSONObject();
						JSONArray components_Array = new JSONArray();
						JSONObject components_Obj = new JSONObject();

						JSONArray componentsArray_A = new JSONArray();

						components_Obj.put("componentid", test_case_name);
						//components_Obj.put("Response_Time", responseTime);
						components_Obj.put("Response_Message", response_message);
						components_Obj.put("OutputID", output_id);
						components_Obj.put("componentsArray", componentsArray_A);
						if (status.equalsIgnoreCase("pass")) {
							components_Obj.put("overallStatus", "pass");							
						} else {
							components_Obj.put("overallStatus", "fail");							
						}

						components_Array.add(components_Obj);
						//totalScenariosCount++;
						tabularDataObj.put("components", components_Array);
						tabularDataObj.put("pageTitle", scenario_name);
						tabularDataObj.put("OverAll_Status", status);
						//tabularDataObj.put("total_test_cases", 2);

						tabularDataArray.add(tabularDataObj);
					}
				}
			}

			for (int j = 0; j < tabularDataArray.size(); j++) {
				JSONObject tabular_i = (JSONObject) tabularDataArray.get(j);
				String final_status = (String) tabular_i.get("OverAll_Status");
				
				if(final_status.equalsIgnoreCase("pass")) {
					totalPassCount++;
				}else {
					totatFailCount++;
				}
			
				
			}
			
			
			
			
			JSONArray chart_labels = new JSONArray();
			JSONArray chart_values = new JSONArray();

			//chart_labels.add("Total Scenarios");
			chart_labels.add("Passed Scenarios");
			chart_labels.add("Failed Scenarios");
			//chart_values.add(totalScenariosCount);
			chart_values.add(totalPassCount);
			chart_values.add(totatFailCount);

			toolReportObj.put("chart_name", "Summary");
			toolReportObj.put("chart_labels", chart_labels);
			toolReportObj.put("chart_values", chart_values);
			toolReportObj.put("tabular_data", tabularDataArray);
			csvParser.close();
			toolReportArray.add(toolReportObj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception reading result.csv");
			e.printStackTrace();
		}
		logger.info("WBS_SUE"+reportPath+"assertion_result.csv");
		System.out.println(reportPath+"assertion_result.csv");
		try{
			Reader reader = Files.newBufferedReader(Paths.get(reportPath+"assertion_result.csv"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : csvParser) {
				// Accessing Values by Column Index				

				logger.info("reading assertion_result.csv");
				String test_suite = csvRecord.get(0);
				String test_case_name = csvRecord.get(1);
				String assertion_name = csvRecord.get(2);
				String expected_value = csvRecord.get(3);
				String actual_value = csvRecord.get(4);
				String status = csvRecord.get(5);

				logger.info("Record No - " + csvRecord.getRecordNumber());
				logger.info("Record No - " + csvRecord.getRecordNumber());
				logger.info("---------------");
				logger.info("test_suite : " + test_suite);
				logger.info("test_case_name : " + test_case_name);
				logger.info("assertion_name : " + assertion_name);
				logger.info("expected_value : " + expected_value);
				logger.info("actual_value : " + actual_value);
				logger.info("status : " + status);

				System.out.println("---------------\n\n");

				if (count > 1) {
					if (tabularDataArray.size() > 0) {
						boolean scenarioCaseExists = checkScenarioExists(tabularDataArray, test_suite);

						if (scenarioCaseExists) {
							for (int j = 0; j < tabularDataArray.size(); j++) {
								JSONObject tabular_i = (JSONObject) tabularDataArray.get(j);
								String scenarioName = (String) tabular_i.get("pageTitle");
								if (scenarioName.contains(test_suite)) {
									JSONObject tabular_Data_Obj = tabular_i;
									JSONArray components_Array = (JSONArray) tabular_Data_Obj.get("components");

									JSONArray componentsArray_A = new JSONArray();

									for (int k = 0; k < components_Array.size(); k++) {
										JSONObject compValObj = (JSONObject) components_Array.get(k);
										String test_case = (String) compValObj.get("componentid");
										if (test_case_name.equalsIgnoreCase(test_case)) {
											componentsArray_A = (JSONArray) compValObj.get("componentsArray");

											break;

										}

									}

									JSONObject assertion_nameObj = new JSONObject();
									JSONObject expected_valueObj = new JSONObject();
									JSONObject actual_valueObj = new JSONObject();
									JSONObject statusObj = new JSONObject();

									assertion_nameObj.put("Assertion_Name", assertion_name);
									expected_valueObj.put("Expected_Value", expected_value);
									actual_valueObj.put("Actual_Value", actual_value);
									if(status.equalsIgnoreCase("fail")) {
										statusObj.put("Status", "fail");
									}else if(status.equalsIgnoreCase("pass")) {
										statusObj.put("Status", "pass");
									}
									

									JSONArray componentsArray_Array = new JSONArray();
									componentsArray_Array.add(assertion_nameObj);
									componentsArray_Array.add(expected_valueObj);
									componentsArray_Array.add(actual_valueObj);
									componentsArray_Array.add(statusObj);

									componentsArray_A.add(componentsArray_Array);

									break;
								}

							}

						}

					}

				}
			}

			csvParser.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception reading assertion_result.csv");
			e.printStackTrace();
		}
		logger.debug("toolReport array is:  "+toolReportArray);
		return toolReportArray;

	}

	boolean checkScenarioExists(JSONArray tabularDataArray, String scenario_name) {
		// TODO Auto-generated method stub

		for (int i = 0; i <= tabularDataArray.size() - 1; i++) {
			JSONObject testObj = (JSONObject) tabularDataArray.get(i);
			String scenarioName = (String) testObj.get("pageTitle");
			if (scenarioName.contains(scenario_name)) {
				System.out.println("Returning TRUE");
				return true;

			}

		}
		System.out.println("Returning FALSE");
		return false;
	}

	boolean checkTestCaseExists(JSONArray componentvalueArr, String testCaseName) {
		for (int j = 0; j <= componentvalueArr.size() - 1; j++) {
			JSONObject compValObj = (JSONObject) componentvalueArr.get(j);
			String test_case_name = (String) compValObj.get("componentid");
			if (test_case_name.equalsIgnoreCase(testCaseName)) {

				return true;

			}

		}

		return false;
	}
}