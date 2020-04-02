package com.adapters;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author 10650463(Harsh Mathur)
 */
public class TOSCAAdapter {

/*	public static void main(String[] args) {

		String reportPath = "D:/PLATO_COMPLETE/extras/TOSCA";
		String jobName = "Test";
		int buildNumber = 10;
		TOSCAAdapter tosca = new TOSCAAdapter();
		JSONArray toolReportObject = tosca.getReport(reportPath, jobName, buildNumber);
		System.out.println(toolReportObject);
	}
*/
	@SuppressWarnings("unchecked")
	public JSONArray getReport(String reportPath, String jobName, int buildNumber)throws Exception {

		File f = new File(reportPath + "/result.xml");
		
//		File f = new File("D:\\PLATO_COMPLET\\extras\\result.xml");
		
		System.out.println("TOSCA File name:"+f.getAbsolutePath());
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;
		
		boolean casePassStatus = true;
		boolean scenarioPassStatus = true;
		
		int passCount = 0;	//counts scenarios passed
		int failCount = 0;  //counts scenarios failed
		int noRunCount = 0; //counts scenarios no-run
		 
		int casesPassCount = 0;    //counts cases in each scenario passed
		int casesFailCount = 0;	   //counts cases in each scenario failed
		int casesNoRunCount = 0;   //counts cases in each scenario no-run
		
		JSONObject bar_chart_data = new JSONObject();
		JSONArray bar_chart_labels = new JSONArray();
		JSONArray bar_pass_data = new JSONArray();
		JSONArray bar_fail_data = new JSONArray();
		
		JSONObject report = new JSONObject();
		JSONArray reportJA = new JSONArray();
		
		JSONObject testSuitJO = null;
		JSONArray testSuitsJA = null;
		
		JSONObject testCaseJO = null;
		JSONArray testCasesJA = null;
		
		JSONArray testStepJA = null;
		JSONArray testStepsJA = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(f);
			doc.getDocumentElement().normalize();

			NodeList testSuiteList = doc.getElementsByTagName("testsuite");
			
			testSuitsJA = new JSONArray();
			for (int i = 0; i < testSuiteList.getLength(); i++) {
				casePassStatus = true;
				scenarioPassStatus = true;
				
				casesPassCount = 0;
				casesFailCount = 0;
				casesNoRunCount = 0;
				
				Node testSuite = testSuiteList.item(i);
				if (testSuite.getNodeType() == Node.ELEMENT_NODE) {
					testSuitJO = new JSONObject();
					
					Element testsuitElement = (Element) testSuite;
					testSuitJO.put("pageTitle" , testsuitElement.getAttribute("name"));
					bar_chart_labels.add(testsuitElement.getAttribute("name"));
					
					NodeList testCaseList = testsuitElement.getElementsByTagName("testcase");

					testCasesJA = new JSONArray();
					for (int j = 0; j < testCaseList.getLength(); j++) {
						Node testCase = testCaseList.item(j);
						casePassStatus = true;
						
						if (testCase.getNodeType() == Node.ELEMENT_NODE) {
							testCaseJO = new JSONObject();
							
							Element testcaseElement = (Element) testCase;
							testCaseJO.put("componentid" , testcaseElement.getAttribute("name"));
							
							String log = testcaseElement.getAttribute("log");
							String[] loglist = log.split(" Passed");
							
							testStepsJA = new JSONArray();
							int step_count = 1;
							for (int step_num = 0; step_num < loglist.length; step_num++) {
								
		
								JSONObject Step_No = null;
								JSONObject Step_Description = null;
								JSONObject status = null;
								
								
								loglist[step_num] = loglist[step_num].replace('+', ' ').trim();
								loglist[step_num] = loglist[step_num].replace("{", "___").trim();
								loglist[step_num] = loglist[step_num].replace("}","").trim();
								loglist[step_num] = loglist[step_num].replaceAll("[\r\n]+", " ").trim();
								
								
								if (loglist[step_num].contains("- Failed")) {
									casePassStatus = false;
									scenarioPassStatus = false;
									
									String[] parts = loglist[step_num].split("- Failed");
									
									if(parts[0].contains("___")){
										parts[0] = parts[0].substring(0, parts[0].indexOf("___"));  //parts[1] will be containing the logInfo data
									}
						
									Step_No = new JSONObject();
									Step_Description = new JSONObject();
									status = new JSONObject();
									Step_No.put("Step_No", step_count++);
									Step_Description.put("Step_Description", parts[0].trim());
									status.put("status", "Pass");
									
									testStepJA = new JSONArray();
									testStepJA.add(Step_No);
									testStepJA.add(Step_Description);
									testStepJA.add(status);
									testStepsJA.add(testStepJA);
									
									if(parts[1].contains("___")){
										parts[1] = parts[1].substring(0, parts[1].indexOf("___"));
									}
									
									Step_No = new JSONObject();
									Step_Description = new JSONObject();
									status = new JSONObject();
									Step_No.put("Step_No", step_count++);
									Step_Description.put("Step_Description", parts[1].trim());
									status.put("status", "Fail");
												
									testStepJA = new JSONArray();
									testStepJA.add(Step_No);
									testStepJA.add(Step_Description);
									testStepJA.add(status);
									testStepsJA.add(testStepJA);
									
								} else {
									if(loglist[step_num].contains("___")){
										loglist[step_num] = loglist[step_num].substring(0, loglist[step_num].indexOf("___"));
									}
									Step_No = new JSONObject();
									Step_Description = new JSONObject();
									status = new JSONObject();
									Step_No.put("Step_No", step_count++);
									Step_Description.put("Step_Description", loglist[step_num].trim());
									status.put("status", "Pass");
									
									testStepJA = new JSONArray();
									testStepJA.add(Step_No);
									testStepJA.add(Step_Description);
									testStepJA.add(status);
									testStepsJA.add(testStepJA);
								}
								
										
								
							}//steps
							testCaseJO.put("componentsArray", testStepsJA);
							if(casePassStatus){
								casesPassCount++;
								testCaseJO.put("overallStatus", "Pass");
							}else{
								casesFailCount++;
								testCaseJO.put("overallStatus", "Fail");
							}
							
							testCasesJA.add(testCaseJO);
						}
						
					}//cases
					testSuitJO.put("components", testCasesJA);
					
					if(scenarioPassStatus){
						passCount++;
						testSuitJO.put("OverAll_Status", "Pass");
					}else{
						failCount++;
						testSuitJO.put("OverAll_Status", "Fail");
					}
					
					testSuitJO.put("total_test_cases", testCaseList.getLength());
					testSuitsJA.add(testSuitJO);
				}
				
				bar_pass_data.add(casesPassCount);
				bar_fail_data.add(casesFailCount);
			}
			
			JSONArray chartlabs = new JSONArray();
			chartlabs.add("pass");
			chartlabs.add("fail");
			chartlabs.add("no_run");
			
			JSONArray chartdata = new JSONArray();
			chartdata.add(passCount);
			chartdata.add(failCount);
			chartdata.add(noRunCount);
			
			
			JSONObject bar_data = null;
			JSONArray bar_data_array = new JSONArray();
			
			bar_data = new JSONObject();
			bar_data.put("label", "pass");
			bar_data.put("data", bar_pass_data);
			bar_data_array.add(bar_data);
			
			bar_data = new JSONObject();
			bar_data.put("label", "fail");
			bar_data.put("data", bar_fail_data);
			bar_data_array.add(bar_data);
			
			bar_chart_data.put("bar_chart_data", bar_data_array);
			bar_chart_data.put("bar_chart_labels", bar_chart_labels);
			
			report.put("chart_name", "Overall");
			report.put("bar_chart_name", "Individual");
			report.put("chart_labels", chartlabs);
			report.put("total_execution_time", "total_execution_time");
			report.put("total_scenario_executed", "total_scenario_executed");
			report.put("tabular_data", testSuitsJA);
			report.put("chart_values", chartdata);
			report.put("bar_charts_value", bar_chart_data);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		reportJA.add(report);
		return reportJA;

	}

}
