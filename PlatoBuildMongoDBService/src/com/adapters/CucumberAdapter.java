package com.adapters;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.mongo.constants.GlobalConstants;
import com.reportingServices.FASTReportService;


public class CucumberAdapter {
	
	private static final Logger logger = Logger
			.getLogger(CucumberAdapter.class);

	static final FilenameFilter HTML_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(final File dir, final String name) {
			if (name.endsWith(".html")) {
				return (true);
			}
			return (false);
		}
	};

	@SuppressWarnings({ "unchecked" })
	public JSONArray getReport(String reportPath, String jobName, int buildNumber) throws Exception {
		
		JSONArray report = new JSONArray();
		File dir = new File(reportPath);
		for (final File f : dir.listFiles(HTML_FILTER)) {
			System.out.println("reading file: "+ f.getName());
			JSONObject tool = new JSONObject();
			Document doc = Jsoup.parse(f, null);
			String chartName = "report";
			
			try {
				chartName = doc.getElementsByClass("report-name").get(0).text();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tool.put("chart_name", chartName);
			}
			
			
			JSONArray chart_labels = new JSONArray();
			chart_labels.add("passed");
			chart_labels.add("failed");
			tool.put("chart_labels", chart_labels);
			
			String total_scenario_executed = "NA";
			
			try {
				total_scenario_executed = doc.getElementsByClass("panel-lead").get(1).text();
			} catch( Exception e ){
				e.printStackTrace();
			} finally {
				tool.put("total_scenario_executed", total_scenario_executed);
			}
			
			String total_execution_time = "NA";
			
			try {
				total_execution_time = doc.getElementsByClass("panel-lead").get(5).text();
			} catch ( Exception e ){
				e.printStackTrace();
			} finally {
				tool.put("total_execution_time", total_execution_time);
			}
			
			JSONArray chart_values = new JSONArray();
			String pass = "0";
			String fail = "0";
			
			try {
				pass = doc.getElementsByClass("strong").get(3).text();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				chart_values.add(pass);
			}
			
			try {
				fail = doc.getElementsByClass("strong").get(4).text();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				chart_values.add(fail);
			}
			
			tool.put("chart_values", chart_values);
			
			JSONArray tabular_data = new JSONArray();
			
			Elements testScenarios = doc.getElementsByClass("test");
			
			for (Element testScenario : testScenarios) {
				JSONObject testscenarioOBJ = new JSONObject();
				String name = testScenario.getElementsByClass("test-name").get(0).text();
				
				testscenarioOBJ.put("pageTitle", name);
				testscenarioOBJ.put("OverAll_Status", testScenario.attr("status"));
				
				JSONArray testCases = new JSONArray();
				
				Elements testCasesScenarioElements = testScenario.select("div.node");
				
				for (Element testCaseElement : testCasesScenarioElements) {
					JSONObject testCase = new JSONObject();
					String tcName = testCaseElement.getElementsByClass("scenario-name").text()
									.replaceAll(testCaseElement.getElementsByClass("status").get(0).text(), "")
									.replaceAll("Scenario: ", "")
									.replaceAll("ScenarioOutline:", "");
					testCase.put("componentid", tcName);
					
					String status = (testCaseElement.getElementsByClass("status").get(0).text().equalsIgnoreCase("check_circle"))? "pass" : "fail";
					
					JSONArray teststeps = new JSONArray();
					
					Elements teststepsElements = testCaseElement.select("ul.steps > li");
					
					int count = 1;
					for (Element teststepElement : teststepsElements) {
						JSONArray step = new JSONArray();
						
						JSONObject Step_No = new JSONObject();
						Step_No.put("Step_No", count++);
						
						JSONObject Step = new JSONObject();
						String stepStr ="";
						
						try{
							stepStr = teststepElement.getElementsByClass("step-name").get(0).text().replaceAll(teststepElement.getElementsByClass("status").get(0).text(), "");
						} catch (Exception e){
							stepStr = "-";
						} finally {
							Step.put("Step", stepStr);
						}
						
						
						JSONObject Step_Description = new JSONObject();
						String Step_Description_str = "";
						try{
							Step_Description_str = teststepElement.getElementsByClass("node-step").get(0).text();
						} catch (Exception e) {
							Step_Description_str = "-";
						} finally {
							Step_Description.put("Step_Description", Step_Description_str);
						}
						
						JSONObject stepStatus = new JSONObject();
						String stepstats = "";
						try{
							stepstats = (teststepElement.getElementsByClass("node-step").get(1).text().equalsIgnoreCase("passed"))? "pass": "fail";
						} catch (Exception e) {
							stepstats = "fail";
						} finally {
							stepStatus.put("status", stepstats);
						}
						
						JSONObject screenshot = new JSONObject();
						String screenshotName = "";
						try{
							screenshotName = teststepElement.select("ul.screenshots > li").first().select("a").attr("href");
							screenshotName = screenshotName.substring(screenshotName.lastIndexOf("\\")+1, screenshotName.length());
							screenshotName = GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/images/"+screenshotName;
						} catch (Exception e) {
							screenshotName = "";
						} finally {
							screenshot.put("screenshot",screenshotName);
						}
						
						step.add(Step_No);
						step.add(Step);
						step.add(Step_Description);
						step.add(stepStatus);
						step.add(screenshot);
						teststeps.add(step);
					}
					
					testCase.put("componentsArray", teststeps);
					testCase.put("overallStatus", status);
					testCases.add(testCase);
				}
				
				testscenarioOBJ.put("components", testCases);					
				tabular_data.add(testscenarioOBJ);
			}
			
			tool.put("tabular_data", tabular_data);
			
			report.add(tool);
		}
		return report;
	}
	
	// Show Extend report directly
	public JSONArray getExtendReport(String reportpath, String jobName, int buildNumber) throws Exception {
		System.out.println("reportpath :"+reportpath+"jobName :"+jobName+"buildNumber "+buildNumber);
		logger.info("inside Cucumber adapter");
		File f = new File(reportpath + "/overview-features.html");
		
		JSONArray report = new JSONArray();
		
		if (f.exists()) {
			logger.info("overview-features.html exists");
			System.out.println(" overview-features.html exists");
			report = getCLoudCucumberReport(f, jobName, buildNumber);
			
		} else {
			logger.info("overview-features.html does not exists");
			System.out.println(" overview-features.html does not exists");		
			report = getHTMLExtendReport(new File(reportpath), jobName, buildNumber);
			
			
		}

		return report;
	}

	@SuppressWarnings("unchecked")
	private static JSONArray getHTMLExtendReport(File f, String jobName, int buildNumber) throws Exception {
		
		JSONArray report = new JSONArray();
		logger.info(" inside getHTMLExtendReport func");
		System.out.println("inside getHTMLExtendReport func");
		for (final File file : f.listFiles(HTML_FILTER)) {
			System.out.println("reading file: " + file.getName());
			
			JSONObject tool = new JSONObject();
			Document doc = Jsoup.parse(file, null);
			JSONArray chart_labels = new JSONArray();
			JSONArray chart_values = new JSONArray();
			String pass = "0";
			String fail = "0";
			String chartName = "report";
			String total_scenario_executed = "NA";
			String total_execution_time = "NA";
			
			try {
				chartName = doc.getElementsByClass("report-name").get(0).text();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tool.put("chart_name", chartName);
				System.out.println("chartName "+chartName);
				logger.info("chartname "+chartName);
			}

			chart_labels.add("passed");
			chart_labels.add("failed");
			tool.put("chart_labels", chart_labels);
			
			try {
				total_scenario_executed = doc.getElementsByClass("panel-lead").get(1).text();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tool.put("total_scenario_executed", total_scenario_executed);
			}

			try {
				total_execution_time = doc.getElementsByClass("panel-lead").get(5).text();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tool.put("total_execution_time", total_execution_time);
			}

			try {
				pass = doc.getElementsByClass("strong").get(3).text();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				chart_values.add(pass);
			}

			try {
				fail = doc.getElementsByClass("strong").get(4).text();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				chart_values.add(fail);
			}
			
			tool.put("chart_values", chart_values);
			tool.put("report_url", GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/"+file.getName());
			System.out.println("report url GlobalConstants.JENKINS_URL GlobalConstants.JENKINS_PORT"+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/"+file.getName());
			report.add(tool);
		}

		return report;
	}

	@SuppressWarnings("unchecked")
	protected static JSONArray getCLoudCucumberReport(File f, String jobName, int buildNumber) throws Exception {
		System.out.println("reading file: " + f.getName());
		
		JSONArray report = new JSONArray();
		JSONObject tool = new JSONObject();
		Document doc = Jsoup.parse(f, null);
		JSONArray chart_labels = new JSONArray();
		JSONArray chart_values = new JSONArray();
		String chartName = "report";
		String total_scenario_executed = "NA";
		String total_execution_time = "NA";
		String pass = "0";
		String fail = "0";
		
		try {
			tool.put("chart_name", chartName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		chart_labels.add("passed");
		chart_labels.add("failed");
		tool.put("chart_labels", chart_labels);
		
		try {
			total_scenario_executed = doc.select("#tablesorter > tfoot > tr:eq(0) > td:eq(11)").text();
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			tool.put("total_scenario_executed", total_scenario_executed);
		}
		
		try {
			total_execution_time = doc.getElementsByClass("duration").last().text();
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			tool.put("total_execution_time", total_execution_time);
		}


		try {
			double executed = Double.parseDouble(total_scenario_executed);
			double percent = Double.parseDouble(
					doc.select("#tablesorter > tfoot > tr:eq(1) > td:eq(11)").text().replaceAll("%", ""));
			pass = String.valueOf((int) Math.ceil((percent * executed) / 100));
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			chart_values.add(pass);
		}

		try {
			fail = String.valueOf((Integer.parseInt(total_scenario_executed) - Integer.parseInt(pass)));
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			chart_values.add(fail);
		}
		
		tool.put("chart_values", chart_values);
		tool.put("report_url", GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+buildNumber+"/"+f.getName());
		report.add(tool);
		return report;
	}
}
