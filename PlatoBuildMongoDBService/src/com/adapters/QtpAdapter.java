package com.adapters;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongo.constants.GlobalConstants;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class QtpAdapter {
	private static final Logger logger=Logger.getLogger( QtpAdapter.class);
	public JSONArray getQtpReport (String reportPath,String jobName,int nextBuildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception {
		logger.info("getQtpReport report path is "+reportPath);
		System.out.println("getQtpReport report path is "+reportPath);
		File f=new File(reportPath);
		File[] fileList=f.listFiles();
		JSONArray multipleReports=new JSONArray();
		for(File file:fileList) {
			if(file.isDirectory()) {
				File folder=file;	
				logger.info("file is "+f.getName()+" "+f.toString());
				System.out.println("file is "+f.getName()+" "+f.toString());
				f=new File(reportPath+"/"+folder.getName()+"/"+"Execution Report/");
				fileList=f.listFiles();
				for(File file1:fileList) {
					System.out.println("file1 "+file1.toString());
					if(file1.isFile()) {
						f=file1;		
						System.out.println("file is "+f.getName()+" "+f.toString());
						logger.info("file is "+f.getName()+" "+f.toString());
						break;			
					}
				}
				Workbook workBook;
				JSONArray jsonArray=new JSONArray();
				try {
//					workBook=new HSSFWorkbook(fileInput);
					workBook=WorkbookFactory.create(f);
				}catch(Exception e) {
					e.printStackTrace();
					throw e;
//					return null;
				}
				System.out.println("folder path "+folder.getPath());
				multipleReports.add(convertExcelTOJSON(workBook,folder.getPath(),jobName,nextBuildNumber).get(0));
			}
		}

//		HSSFSheet sheet=workBook.getSheet("result");
		return multipleReports;
	}
	public JSONArray convertExcelTOJSON(Workbook workBook,String reportPath,String jobName,int nextBuildNumber)throws Exception {
		File f=new File(reportPath);
		File mainFolder=new File(reportPath);//egs 14_05_2018-13_02_59_JL_Event
		File screenShot=new File(reportPath);
		File[] fileList=f.listFiles();
		
		DataFormatter formatter = new DataFormatter();
//		for(File file: fileList) {
//			if(file.isDirectory()) {
//				mainFolder=file;
//				logger.info("mainFolder is "+mainFolder.getName());
//				System.out.println("mainFolder is "+mainFolder.getName());
//				break;
//			}
//		}
		fileList=mainFolder.listFiles();
		for(File file:fileList) {
			if(file.isDirectory()) {
				if(file.getName().equalsIgnoreCase("ScreenShot")) {
					screenShot=file;
				}
			}
		}
		File[] screenShotList=screenShot.listFiles();
		File[] scenarioFolders=new File[screenShotList.length];
		File screenShotDocX=new File(screenShot.getPath());
		String screenShotDocxName="";
		int j=0;
		for(File file:screenShotList) {
			scenarioFolders[j]=file;
			//if folder contains the doc
			if(file.getName().equals("ScreenShots")) {
				if(file.listFiles().length>0) {
					screenShotDocX=file;
					screenShotDocxName=file.getName();
				}
			}else if(file.getName().equals("Fail TestCases Screenshots")) {
				if(file.listFiles().length>0) {
					screenShotDocX=file;
					screenShotDocxName=file.getName();
				}
			}
			j++;
		}
		//taking the doc folder and finding the doc file
		for(File file:screenShotDocX.listFiles()) {
			if(file.isFile()) {
				screenShotDocX=file;
				break;
			}
		}
		
		int pass,fail,noRun;
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
		Sheet dashboard=workBook.getSheet("Dashboard");
		Row dashboardRow=dashboard.getRow(4);
		JSONArray chartLabels=new JSONArray();
		JSONArray chartValues=new JSONArray();
		JSONArray tabularData=new JSONArray();
		toolReportObject.put("chart_name",mainFolder.getName());
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("no_run");
		
		pass=(int)dashboardRow.getCell(4).getNumericCellValue();
		fail=(int)dashboardRow.getCell(5).getNumericCellValue();
		noRun=(int)dashboardRow.getCell(7).getNumericCellValue();
//		String qcPath=dashboardRow.getCell(12).getStringCellValue();
		
		chartValues.add(pass);
		chartValues.add(fail);
		chartValues.add(noRun);
		toolReportObject.put("chart_labels", chartLabels);
		toolReportObject.put("chart_values",chartValues);
		toolReportObject.put("tabular_data",tabularData );
//		toolReportObject.put("qcPath",qcPath);
		Sheet statusTestcase=workBook.getSheet("Status_TestCases");
		
		//this will have values for each scenario
		LinkedHashMap ScenarioValuesHashMap=new LinkedHashMap();
		Iterator<Row> itr=statusTestcase.iterator();
		int noOfScenarios=0;
		while(itr.hasNext()) {
			Row row=itr.next();
			if(row.getRowNum()==0) {
				continue;
			}
			noOfScenarios++;
			int srNo=(int)row.getCell(1).getNumericCellValue();
			String scenarioName=row.getCell(2).getStringCellValue();
			String overAllStatus=row.getCell(5).getStringCellValue();
//			double executionDuration=row.getCell(8).getNumericCellValue();
			String executionDuration=formatter.formatCellValue(row.getCell(8));
			String qcPath=formatter.formatCellValue(row.getCell(12));
			JSONArray scenarioValuesArr=new JSONArray();
			JSONObject obj=new JSONObject();
			obj.put("Sr_No", srNo);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("Scenario_Description",scenarioName);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("OverAll_Status",overAllStatus);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("Exeuction_Duration",executionDuration);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("qcPath", qcPath);
			scenarioValuesArr.add(obj);
			
			ScenarioValuesHashMap.put(scenarioName,scenarioValuesArr);
		}
		
		Sheet statusTestcaseSteps=workBook.getSheet("Status_TestCase_Steps");
		itr=statusTestcaseSteps.iterator();
		File scenarioFolder=new File(reportPath);
		File transactionFolder=new File(reportPath);
		File stepScreenShotFile=new File(reportPath);
		String docx=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
		docx=docx+mainFolder.getName()+"/"+screenShot.getName()+"/"+screenShotDocxName+"/"+screenShotDocX.getName();
		toolReportObject.put("documentName",screenShotDocX.getName());
		toolReportObject.put("documentUrl",docx);
		while(itr.hasNext()) {
			Row row=itr.next();
			if(row.getCell(0).getCellType()!=Cell.CELL_TYPE_NUMERIC && ScenarioValuesHashMap.containsKey(row.getCell(0).getStringCellValue())) {
				JSONArray scenarioValues=(JSONArray)ScenarioValuesHashMap.get(row.getCell(0).getStringCellValue());
				JSONObject overAllStatus=(JSONObject)scenarioValues.get(2);
				JSONObject pageTitle=new JSONObject();
				pageTitle.put("pageTitle",row.getCell(0).getStringCellValue());
				pageTitle.put("components", new JSONArray());
				pageTitle.put("OverAll_Status", overAllStatus.get("OverAll_Status"));
				JSONObject executionTimeObj=(JSONObject)scenarioValues.get(scenarioValues.size()-2);
				pageTitle.put("execution_time",executionTimeObj.get("Exeuction_Duration"));
				JSONObject qcPathObj=(JSONObject)scenarioValues.get(scenarioValues.size()-1);
				pageTitle.put("qcPath",qcPathObj.get("qcPath"));
				tabularData.add(pageTitle);
				for(File scenarioFolderI:scenarioFolders) {
					System.out.println("scenarioFolderI is "+scenarioFolderI.getName());
					logger.info("scenarioFolderI is "+scenarioFolderI.getName());
					System.out.println("row.getCell(0).getStringCellValue() "+row.getCell(0).getStringCellValue());
					if(scenarioFolderI.getName().equalsIgnoreCase(row.getCell(0).getStringCellValue())) {
						scenarioFolder=scenarioFolderI;
						break;
					}
				}
				continue;
			}
			if(row.getCell(0).getCellType()!=Cell.CELL_TYPE_NUMERIC && row.getCell(0).getStringCellValue().equalsIgnoreCase("s.no.")) {
				logger.info("contains s.no.");
				System.out.println("contains s.no.");
				continue;
			}
			if(row.getCell(1)!=null) {
				if(row.getCell(1).getCellType()!=Cell.CELL_TYPE_BLANK) {
					if(row.getCell(4)==null) {
						JSONObject pageTitle=(JSONObject)tabularData.get(tabularData.size()-1);
						JSONArray components=(JSONArray)pageTitle.get("components");
						JSONObject componentObject=new JSONObject();
						componentObject.put("componentid",row.getCell(1).getStringCellValue());
						componentObject.put("overallStatus", "pass");
						File[] transactionFolders=scenarioFolder.listFiles();
						for(File transactionFolderI:transactionFolders) {
							String[] transactionNameSplit=row.getCell(1).getStringCellValue().split(" ");
							if(transactionFolderI.getName().equalsIgnoreCase(transactionNameSplit[0])) {
								transactionFolder=transactionFolderI;
								break;
							}
						}
						continue;
					}else if(row.getCell(4).getCellType()==Cell.CELL_TYPE_BLANK) {
						JSONObject pageTitle=(JSONObject)tabularData.get(tabularData.size()-1);
						JSONArray components=(JSONArray)pageTitle.get("components");
						JSONObject componentObject=new JSONObject();
						componentObject.put("componentid",row.getCell(1).getStringCellValue());
						componentObject.put("overallStatus", "pass");
						componentObject.put("componentsArray", new JSONArray());
						components.add(componentObject);
						File[] transactionFolders=scenarioFolder.listFiles();
//						logger.info("row.getCell(1).getStringCellValue()"+row.getCell(1).getStringCellValue());
//						System.out.println("row.getCell(1).getStringCellValue()"+row.getCell(1).getStringCellValue());
						for(File transactionFolderI:transactionFolders) {
							System.out.println("transactionFolderI"+transactionFolderI.getName());
							String[] transactionNameSplit=row.getCell(1).getStringCellValue().split(" ");
							if(transactionFolderI.getName().equalsIgnoreCase(transactionNameSplit[0])) {
//								logger.info("got row.getCell(1).getStringCellValue() "+row.getCell(1).getStringCellValue());
//								System.out.println("got row.getCell(1).getStringCellValue() "+row.getCell(1).getStringCellValue());
								transactionFolder=transactionFolderI;
								break;
							}
						}
						continue;
					}
					JSONObject pageTitle=(JSONObject)tabularData.get(tabularData.size()-1);
					JSONArray components=(JSONArray)pageTitle.get("components");
					JSONObject componentObject=(JSONObject)components.get(components.size()-1);
					JSONArray componentsArray=(JSONArray)componentObject.get("componentsArray");
					JSONArray steps=new JSONArray();
					JSONObject stepDescription=new JSONObject();
					stepDescription.put("step_description",formatter.formatCellValue(row.getCell(1)));
					JSONObject expectedResult=new JSONObject();
					expectedResult.put("expected_result",formatter.formatCellValue(row.getCell(2)));
					JSONObject actualResult=new JSONObject();
					actualResult.put("actual_result",formatter.formatCellValue(row.getCell(3)));
					JSONObject status=new JSONObject();
					status.put("status",formatter.formatCellValue(row.getCell(4)));
					if(row.getCell(4).getStringCellValue().equalsIgnoreCase("fail")) {
						componentObject.put("overallStatus", "fail");
					}
					
					File[] stepScreenShotFileList=transactionFolder.listFiles();
					for(File file:stepScreenShotFileList) {
						if(file.getName().equalsIgnoreCase(formatter.formatCellValue(row.getCell(1))+".png")) {
							stepScreenShotFile=file;
							String screenShotName=stepScreenShotFile.getName();
							String screenShotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
							screenShotUrl=screenShotUrl+mainFolder.getName()+"/"+screenShot.getName()+"/"+scenarioFolder.getName()+
									"/"+transactionFolder.getName()+"/"+stepScreenShotFile.getName();
							JSONObject screenShotObject=new JSONObject();
							screenShotObject.put("screenShot",screenShotUrl);
							//screenShotObject.put("url",screenShotUrl);
							steps.add(screenShotObject);
							break;
						}
					}
					if(steps.size()<=0  || steps.isEmpty()) {
						JSONObject screenShotObject=new JSONObject();
						screenShotObject.put("screenShot","");						
						steps.add(screenShotObject);
					}
					steps.add(stepDescription);
					steps.add(expectedResult);
					steps.add(actualResult);
					steps.add(status);
					componentsArray.add(steps);
				}
				
			}
		}
		
		
		
		toolReport.add(toolReportObject);
		return toolReport;
	}
	
	public JSONArray convertExcelTOJSONOld(HSSFWorkbook workBook)throws Exception {
		int pass,fail,noRun;
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
		HSSFSheet dashboard=workBook.getSheet("Dashboard");
		Row dashboardRow=dashboard.getRow(4);
		JSONArray chartLabels=new JSONArray();
		JSONArray chartValues=new JSONArray();
		JSONArray tabularData=new JSONArray();
		toolReportObject.put("chart_name",dashboardRow.getCell(1).getStringCellValue());
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("no_run");
		
		pass=(int)dashboardRow.getCell(4).getNumericCellValue();
		fail=(int)dashboardRow.getCell(5).getNumericCellValue();
		noRun=(int)dashboardRow.getCell(7).getNumericCellValue();
		
		chartValues.add(pass);
		chartValues.add(fail);
		chartValues.add(noRun);
		toolReportObject.put("chart_labels", chartLabels);
		toolReportObject.put("chart_values",chartValues);
		toolReportObject.put("tabular_data",tabularData );
		
		HSSFSheet statusTestcase=workBook.getSheet("Status_TestCases");
		
		//this will have values for each scenario
		LinkedHashMap ScenarioValuesHashMap=new LinkedHashMap();
		Iterator<Row> itr=statusTestcase.iterator();
		int noOfScenarios=0;
		while(itr.hasNext()) {
			Row row=itr.next();
			if(row.getRowNum()==0) {
				continue;
			}
			noOfScenarios++;
			int srNo=(int)row.getCell(1).getNumericCellValue();
			String scenarioName=row.getCell(2).getStringCellValue();
			String overAllStatus=row.getCell(5).getStringCellValue();
			double executionDuration=row.getCell(8).getNumericCellValue();
			JSONArray scenarioValuesArr=new JSONArray();
			JSONObject obj=new JSONObject();
			obj.put("Sr_No", srNo);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("Scenario_Description",scenarioName);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("OverAll_Status",overAllStatus);
			scenarioValuesArr.add(obj);
			
			obj=new JSONObject();
			obj.put("Exeuction_Duration",executionDuration);
			scenarioValuesArr.add(obj);
			
			ScenarioValuesHashMap.put(scenarioName,scenarioValuesArr);
		}
		
		HSSFSheet statusTestcaseSteps=workBook.getSheet("Status_TestCase_Steps");
		itr=statusTestcaseSteps.iterator();
		while(itr.hasNext()) {
			Row row=itr.next();
			if(row.getCell(0).getCellType()!=Cell.CELL_TYPE_NUMERIC && ScenarioValuesHashMap.containsKey(row.getCell(0).getStringCellValue())) {
				JSONArray scenarioValuesArr=(JSONArray)ScenarioValuesHashMap.get(row.getCell(0).getStringCellValue());
				tabularData.add(scenarioValuesArr);
				JSONObject transactions=new JSONObject();
				transactions.put("transactions", new JSONArray());
				scenarioValuesArr.add(transactions);
				System.out.println("added new scenario "+row.getCell(0).getStringCellValue());
				continue;
			}
			if(row.getCell(0).getCellType()!=Cell.CELL_TYPE_NUMERIC && row.getCell(0).getStringCellValue().equalsIgnoreCase("s.no.")) {
				System.out.println("contains s.no.");
				continue;
			}
			if(row.getCell(1)!=null) {
				if(row.getCell(1).getCellType()!=Cell.CELL_TYPE_BLANK) {
					if(row.getCell(4)==null) {
						JSONArray scenarioValuesArr=(JSONArray)tabularData.get(tabularData.size()-1);
						JSONObject transactionObject=(JSONObject)scenarioValuesArr.get(scenarioValuesArr.size()-1);
						JSONArray transactions=(JSONArray)transactionObject.get("transactions");
						JSONArray transactionArray=new JSONArray();
						JSONObject transactionName=new JSONObject();
						transactionName.put("transaction_name",row.getCell(1).getCellType());
						JSONObject currentTransactionStatus=new JSONObject();
						currentTransactionStatus.put("transaction_status", "Pass");
						JSONObject steps=new JSONObject();
						steps.put("steps",new JSONArray());
						transactionArray.add(transactionName);
						transactionArray.add(currentTransactionStatus);
						transactionArray.add(steps);
						transactions.add(transactionArray);
						logger.info("transactions size is "+transactions.size());
						System.out.println("transactions size is "+transactions.size());
						continue;
					}else if(row.getCell(4).getCellType()==Cell.CELL_TYPE_BLANK) {
						JSONArray scenarioValuesArr=(JSONArray)tabularData.get(tabularData.size()-1);
						JSONObject transactionObject=(JSONObject)scenarioValuesArr.get(scenarioValuesArr.size()-1);
						JSONArray transactions=(JSONArray)transactionObject.get("transactions");
						JSONArray transactionArray=new JSONArray();
						JSONObject transactionName=new JSONObject();
						transactionName.put("transaction_name",row.getCell(1).getCellType());
						JSONObject currentTransactionStatus=new JSONObject();
						currentTransactionStatus.put("transaction_status", "Pass");
						JSONObject steps=new JSONObject();
						steps.put("steps",new JSONArray());
						transactionArray.add(transactionName);
						transactionArray.add(currentTransactionStatus);
						transactionArray.add(steps);
						transactions.add(transactionArray);
						logger.info("transactions size is "+transactions.size());
						System.out.println("transactions size is "+transactions.size());
						continue;
					}
					//if the row contains step data
					JSONArray scenarioValuesArr=(JSONArray)tabularData.get(tabularData.size()-1);
					JSONObject transactionObject=(JSONObject)scenarioValuesArr.get(scenarioValuesArr.size()-1);
					JSONArray transactions=(JSONArray)transactionObject.get("transactions");
					JSONArray transactionArray=(JSONArray)transactions.get(transactions.size()-1);
					JSONObject stepObject=(JSONObject)transactionArray.get(transactionArray.size()-1);
					JSONArray steps=(JSONArray)stepObject.get("steps");
					JSONArray stepNew=new JSONArray();
					JSONObject stepDescription =new JSONObject();
					stepDescription.put("step_description",row.getCell(1).getStringCellValue());
					JSONObject expectedResult=new JSONObject();
					expectedResult.put("expected_result", row.getCell(2).getStringCellValue());
					JSONObject actualResult=new JSONObject();
					actualResult.put("actual_result",row.getCell(3).getStringCellValue() );
					JSONObject resultStatus=new JSONObject();
					resultStatus.put("status",row.getCell(4).getStringCellValue());
					if(row.getCell(4).getStringCellValue().equalsIgnoreCase("fail")) {
						JSONObject currentTransactionStatus=(JSONObject)transactionArray.get(2);
						currentTransactionStatus.put("transaction_status", "fail");
					}
					stepNew.add(stepDescription);
					stepNew.add(expectedResult);
					stepNew.add(actualResult);
					stepNew.add(resultStatus);
					steps.add(stepNew);
					System.out.println("step added "+stepDescription.get("step_description"));
				}
				
			}
		}
		
		toolReport.add(toolReportObject);
		return toolReport;
	}

}


