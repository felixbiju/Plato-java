package com.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author 10650463 (Harsh Mathur)
 * @version 3.2
 * 
 */

public class QtpAdapterTMNAS {
	
	@SuppressWarnings({ "unchecked" })
	public JSONArray getReport_format_3(String reportPath, String jobName,int buildNumber) throws IOException, Exception{

		int chart_pass = 0;
		int chart_fail = 0;
		int chart_no_run = 0;
	
		String ReportControllerPath = reportPath+"ReportController.txt";
		String fileName = "";
		String[] sheetNames = new String[100];
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + reportPath);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + ReportControllerPath);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		File reportController = new File(ReportControllerPath);
        FileReader fr = new FileReader(reportController);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            System.out.println(line);
            if(line.startsWith("FILE_NAME")){
                System.out.println("Reading FILE_NAME : ");
                fileName = line.split("=")[1];
                fileName = reportPath+fileName;
            }
            if(line.startsWith("SHEET_NAME")){
                System.out.println("Reading SHEET_NAME : ");
                sheetNames = line.split("=")[1].split(",");
            }
        }
        
        System.out.println("FILE_NAME : "+fileName);
        System.out.println("SHEET_NAME : "+sheetNames.toString());
        
        br.close();
        fr.close();

		JSONArray ToolReport = new JSONArray();
		JSONObject report = new JSONObject();
		JSONArray chart_labels_array = new JSONArray();
		JSONArray chart_values_array = new JSONArray();
		JSONArray tabular_data = new JSONArray();
		File file = new File(fileName);
		
		
		if (!file.isDirectory() && (file.getName().contains(".xlsx"))) {
				JSONObject scenarioObj = new JSONObject();
				JSONArray test_case_array = new JSONArray();
				String test_scenario_status = "pass";
				int total_test_cases = 0;

				FileInputStream inputStream = new FileInputStream(file);
				XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
				total_test_cases = workbook.getNumberOfSheets();
				
				for(String sheetName : sheetNames){
					System.out.println("Test Case: "+sheetName);
					if(workbook.getSheet(sheetName) != null){

						JSONObject test_case_obj = new JSONObject();
						JSONArray test_step_array = new JSONArray();

						XSSFSheet sheet = workbook.getSheet(sheetName);
						String test_case_status = "pass";
						test_case_obj.put("componentid", sheetName);
						
						int latestReportIndex = 2;
						System.out.println(">>Sheet found : "+sheet.getSheetName());
						if(sheet.getRow(2) != null){
							String testcaseName = sheet.getRow(2).getCell(0).toString().trim();
							System.out.println(testcaseName);
							for (int row_num = 3; row_num < sheet.getLastRowNum(); row_num++) {
								try{								
									if(sheet.getRow(row_num).getCell(0).toString().equalsIgnoreCase(testcaseName)){
										latestReportIndex = row_num;
									}
								}catch (Exception e) {
									System.out.println("empty row at " +row_num);
								}	
							}
							
							for (int row_num = latestReportIndex+1; row_num <= sheet.getLastRowNum(); row_num++) {

								Row row = sheet.getRow(row_num);
								JSONArray test_step = new JSONArray();

								JSONObject test_step_number = new JSONObject();
								JSONObject test_step_expected = new JSONObject();
								JSONObject test_step_actual = new JSONObject();
								JSONObject test_step_status = new JSONObject();
								JSONObject test_step_exeuction_Time = new JSONObject();

								if (row.getCell(0) != null) {
									String parts[] = row.getCell(0).getStringCellValue().split(" ");
									test_step_number.put("Step_No", parts[1].trim());
								}
								if (row.getCell(1) != null) {
									test_step_expected.put("Expected_Result", row.getCell(1).getStringCellValue().trim());
								}
								if (row.getCell(2) != null) {
									test_step_actual.put("Actual_Result", row.getCell(2).getStringCellValue().trim());
								}
								if (row.getCell(3) != null) {
									String status = row.getCell(3).getStringCellValue();
									if(status.equalsIgnoreCase("Passed") || status.equalsIgnoreCase("Pass")){
										status = "Pass";
									}else{
										status = "Fail";
										test_case_status = "fail";
										test_scenario_status = "fail";
									}
									test_step_status.put("status", status);
								}

								if (row.getCell(5) != null) {
									test_step_exeuction_Time.put("Exeuction_Time",
											row.getCell(5).getDateCellValue().toString());
								}

								test_step.add(test_step_number);
								test_step.add(test_step_expected);
								test_step.add(test_step_actual);
								test_step.add(test_step_status);
								test_step.add(test_step_exeuction_Time);
								test_step_array.add(test_step);
							}
							
						}

						test_case_obj.put("componentsArray", test_step_array);

						test_case_obj.put("overallStatus", test_case_status);
						test_case_array.add(test_case_obj);
					}
				}
				
				

				scenarioObj.put("components", test_case_array);
				scenarioObj.put("pageTitle", file.getName().replace(".xlsx", ""));
				scenarioObj.put("OverAll_Status", test_scenario_status);
				scenarioObj.put("total_test_cases", total_test_cases);

				if (test_scenario_status == "pass") {
					chart_pass++;
				} else if (test_scenario_status == "fail") {
					chart_fail++;
				} else {
					chart_no_run++;
				}

				tabular_data.add(scenarioObj);
		}
	

		report.put("chart_name", "Over Execution Report");
		chart_labels_array.add("pass");
		chart_labels_array.add("fail");
		chart_labels_array.add("no_run");
		report.put("chart_labels", chart_labels_array);
		report.put("total_execution_time", "total execution time");
		report.put("total_scenario_executed", "total scenario executed");

		report.put("tabular_data", tabular_data);

		chart_values_array.add(chart_pass);
		chart_values_array.add(chart_fail);
		chart_values_array.add(chart_no_run);

		report.put("chart_values", chart_values_array);
		ToolReport.add(report);

		System.out.println(ToolReport);
		
		return ToolReport;
	
	
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getReport_format_2(String reportPath, String jobName,int buildNumber) throws IOException{

		int chart_pass = 0;
		int chart_fail = 0;
		int chart_no_run = 0;
		
//		reportPath = "D:\\PLATO_COMPLETE\\AllReports\\QTP";
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + reportPath);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		File folder = new File(reportPath);

		JSONArray ToolReport = new JSONArray();
		JSONObject report = new JSONObject();
		JSONArray chart_labels_array = new JSONArray();
		JSONArray chart_values_array = new JSONArray();
		JSONArray tabular_data = new JSONArray();
		
		Optional<File> mostRecentFile =
			    Arrays
			        .stream(folder.listFiles())
			        .filter(f -> f.isFile())
			        .max(
			            (f1, f2) -> Long.compare(f1.lastModified(),
			                f2.lastModified()));
		
		if (mostRecentFile.get().exists()) {
			File file = mostRecentFile.get();
			if (!file.isDirectory()) {
					JSONObject scenarioObj = new JSONObject();
					JSONArray test_case_array = new JSONArray();
					String test_scenario_status = "pass";
					int total_test_cases = 0;

					FileInputStream inputStream = new FileInputStream(file);
					XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
					total_test_cases = workbook.getNumberOfSheets();

					for (int sheet_num = 0; sheet_num < workbook.getNumberOfSheets(); sheet_num++) {
						JSONObject test_case_obj = new JSONObject();
						JSONArray test_step_array = new JSONArray();

						XSSFSheet sheet = workbook.getSheetAt(sheet_num);
						String test_case_status = "pass";
						test_case_obj.put("componentid", sheet.getSheetName());

						for (int row_num = 3; row_num < sheet.getLastRowNum(); row_num++) {
							Row row = sheet.getRow(row_num);
							JSONArray test_step = new JSONArray();

							JSONObject test_step_number = new JSONObject();
							JSONObject test_step_expected = new JSONObject();
							JSONObject test_step_actual = new JSONObject();
							JSONObject test_step_status = new JSONObject();
							JSONObject test_step_exeuction_Time = new JSONObject();

							if (row.getCell(0) != null) {
								String parts[] = row.getCell(0).getStringCellValue().split(" ");
								test_step_number.put("Step_No", parts[1].trim());
							}
							if (row.getCell(1) != null) {
								test_step_expected.put("Expected_Result", row.getCell(1).getStringCellValue().trim());
							}
							if (row.getCell(2) != null) {
								test_step_actual.put("Actual_Result", row.getCell(2).getStringCellValue().trim());
							}
							if (row.getCell(3) != null) {
								String status = row.getCell(3).getStringCellValue();
								if(status.equalsIgnoreCase("Passed")){
									status = "Pass";
								}else{
									status = "Fail";
									test_case_status = "fail";
									test_scenario_status = "fail";
								}
								test_step_status.put("status", status);
							}

							if (row.getCell(5) != null) {
								test_step_exeuction_Time.put("Exeuction_Time",
										row.getCell(5).getDateCellValue().toString());
							}

							test_step.add(test_step_number);
							test_step.add(test_step_expected);
							test_step.add(test_step_actual);
							test_step.add(test_step_status);
							test_step.add(test_step_exeuction_Time);
							test_step_array.add(test_step);
						}
						test_case_obj.put("componentsArray", test_step_array);

						test_case_obj.put("overallStatus", test_case_status);
						test_case_array.add(test_case_obj);

					}
					
					scenarioObj.put("components", test_case_array);
					scenarioObj.put("pageTitle", file.getName().replace(".xlsx", ""));
					scenarioObj.put("OverAll_Status", test_scenario_status);
					scenarioObj.put("total_test_cases", total_test_cases);

					if (test_scenario_status == "pass") {
						chart_pass++;
					} else if (test_scenario_status == "fail") {
						chart_fail++;
					} else {
						chart_no_run++;
					}

					tabular_data.add(scenarioObj);
			}
		}

		report.put("chart_name", "Over Execution Report");
		chart_labels_array.add("pass");
		chart_labels_array.add("fail");
		chart_labels_array.add("no_run");
		report.put("chart_labels", chart_labels_array);
		report.put("total_execution_time", "total execution time");
		report.put("total_scenario_executed", "total scenario executed");

		report.put("tabular_data", tabular_data);

		chart_values_array.add(chart_pass);
		chart_values_array.add(chart_fail);
		chart_values_array.add(chart_no_run);

		report.put("chart_values", chart_values_array);
		ToolReport.add(report);
		return ToolReport;	
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getReport(String reportPath, String jobName,int buildNumber) throws IOException{

		int chart_pass = 0;
		int chart_fail = 0;
		int chart_no_run = 0;
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + reportPath);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		File f = new File(reportPath);
		File[] allSubFiles = f.listFiles();

		JSONArray ToolReport = new JSONArray();
		JSONObject report = new JSONObject();
		JSONArray chart_labels_array = new JSONArray();
		JSONArray chart_values_array = new JSONArray();
		JSONArray tabular_data = new JSONArray();
		

		for (File file : allSubFiles) {
			if (!file.isDirectory()) {
					JSONObject scenarioObj = new JSONObject();
					JSONArray test_case_array = new JSONArray();
					String test_scenario_status = "pass";
					int total_test_cases = 0;

					FileInputStream inputStream = new FileInputStream(file);
					XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
					total_test_cases = workbook.getNumberOfSheets();

					for (int sheet_num = 0; sheet_num < workbook.getNumberOfSheets(); sheet_num++) {
						JSONObject test_case_obj = new JSONObject();
						JSONArray test_step_array = new JSONArray();

						XSSFSheet sheet = workbook.getSheetAt(sheet_num);
						String test_case_status = "pass";
						test_case_obj.put("componentid", sheet.getSheetName());

						for (int row_num = 1; row_num < sheet.getLastRowNum(); row_num++) {
							Row row = sheet.getRow(row_num);
							JSONArray test_step = new JSONArray();

							JSONObject test_step_number = new JSONObject();
							JSONObject test_step_description = new JSONObject();
							JSONObject test_step_status = new JSONObject();
							JSONObject test_step_screenshot = new JSONObject();
							JSONObject test_step_exeuction_Time = new JSONObject();

							if (row.getCell(0) != null) {
								String parts[] = row.getCell(0).getStringCellValue().split(":");
								test_step_number.put("Step_No", parts[0].substring(4).trim());
								test_step_description.put("Step_Description", parts[1]);

							}
							if (row.getCell(1) != null) {
								String status = row.getCell(1).getStringCellValue();
								if(status.equalsIgnoreCase("Passed")){
									status = "Pass";
								}else{
									status = "Fail";
									test_case_status = "fail";
									test_scenario_status = "fail";
								}
								test_step_status.put("status", status);
							}
							test_step_screenshot.put("Screenshot", "");

							if (row.getCell(3) != null) {
								test_step_exeuction_Time.put("Exeuction_Time",
										row.getCell(3).getDateCellValue().toString());
							}

							String extra_string = "";
							if ((row_num+1 < sheet.getLastRowNum()) && (sheet.getRow(row_num + 1).getCell(0) != null)) {
								if(!sheet.getRow(row_num + 1).getCell(0).getStringCellValue().contains("Step")){
									row_num += 2;
									
									while ((row_num < sheet.getLastRowNum()) && sheet.getRow(row_num).getCell(0) == null) {

										Iterator<Cell> cellIterator = sheet.getRow(row_num).cellIterator();
										while (cellIterator.hasNext()) {
											Cell cell = cellIterator.next();
											switch (cell.getCellType()) {
											case Cell.CELL_TYPE_STRING:
												extra_string += cell.getStringCellValue() + " ";
												break;
											case Cell.CELL_TYPE_NUMERIC:
												extra_string += cell.getNumericCellValue() + " ";
												break;
											case Cell.CELL_TYPE_BOOLEAN:
												extra_string += cell.getBooleanCellValue() + " ";
												break;
											default:
												extra_string += "---" + " ";
											}
										}
										row_num++;
									}									
									row_num--;
								}								
								
							}
							
							JSONObject extra_data = new JSONObject();									
							extra_data.put("extra_data", extra_string);
							

							test_step.add(test_step_number);
							test_step.add(test_step_description);
							test_step.add(test_step_status);
//							test_step.add(test_step_screenshot);
							test_step.add(test_step_exeuction_Time);
							test_step.add(extra_data);
							test_step_array.add(test_step);
						}
						test_case_obj.put("componentsArray", test_step_array);

						test_case_obj.put("overallStatus", test_case_status);
						test_case_array.add(test_case_obj);

					}

					scenarioObj.put("components", test_case_array);
					scenarioObj.put("pageTitle", file.getName().replace(".xlsx", ""));
					scenarioObj.put("OverAll_Status", test_scenario_status);
					scenarioObj.put("total_test_cases", total_test_cases);

					if (test_scenario_status == "pass") {
						chart_pass++;
					} else if (test_scenario_status == "fail") {
						chart_fail++;
					} else {
						chart_no_run++;
					}

					tabular_data.add(scenarioObj);
			}
		}

		report.put("chart_name", "Over Execution Report");
		chart_labels_array.add("pass");
		chart_labels_array.add("fail");
		chart_labels_array.add("no_run");
		report.put("chart_labels", chart_labels_array);
		report.put("total_execution_time", "total execution time");
		report.put("total_scenario_executed", "total scenario executed");

		report.put("tabular_data", tabular_data);

		chart_values_array.add(chart_pass);
		chart_values_array.add(chart_fail);
		chart_values_array.add(chart_no_run);

		report.put("chart_values", chart_values_array);
		ToolReport.add(report);

		return ToolReport;	
	}
}
