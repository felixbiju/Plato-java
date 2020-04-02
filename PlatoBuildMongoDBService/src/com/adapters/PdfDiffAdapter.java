package com.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongo.constants.GlobalConstants;

public class PdfDiffAdapter {
	private static final Logger logger=Logger.getLogger( PdfDiffAdapter.class);
	public JSONArray getPdfDiffReportRSA(String reportPath,String jobName,int nextBuildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception {
		File reportFolder=new File(reportPath);
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
		toolReport.add(toolReportObject);
		JSONArray tabularData=new JSONArray();
		JSONArray chartLabels=new JSONArray();
		JSONArray chartValues=new JSONArray();
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("no_run");
		toolReportObject.put("chart_name", "pdfDiffReports");
		toolReportObject.put("tabular_data", tabularData);
		toolReportObject.put("chart_labels",chartLabels);
		toolReportObject.put("chart_values",chartValues);
		toolReportObject.put("level", "levelRSA");
		for(File folder:reportFolder.listFiles()) {
			if(folder.isDirectory() && folder.getName().contains("~")) {
				String folderName=folder.getName();
				String[] splittedNames=folderName.split("~");
				if(splittedNames[splittedNames.length-1].equalsIgnoreCase("AllComparisonReport")) {
					JSONObject tabularDataObject=new JSONObject();
					tabularData.add(tabularDataObject);
					tabularDataObject.put("pageTitle",splittedNames[1]+"~"+splittedNames[2]);
					tabularDataObject.put("OverAll_Status","pass");
					JSONArray components=new JSONArray();
					tabularDataObject.put("components",components);
					File[] files=folder.listFiles();
					String url=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
					for(int i=0;i<files.length;i=i+2) {
						if(files[i].getName().equals("Thumbs.db")) {
							continue;
						}
						System.out.println("i "+i);
						System.out.println("folder.getName() "+folder.getName()+" files[i].getName() "+files[i].getName());
						JSONObject componentObject=new JSONObject();
						components.add(componentObject);
						componentObject.put("componentid",files[i].getName());
						JSONArray componentsArray=new JSONArray();
						componentObject.put("componentsArray",componentsArray);
						JSONArray componentsArrayArray=new JSONArray();
						componentsArray.add(componentsArrayArray);
						JSONObject left=new JSONObject();
						JSONObject right=new JSONObject();
						left.put("left",url+folder.getName()+"/"+files[i].getName());
						right.put("right",url+folder.getName()+"/"+files[i+1].getName());
						componentsArrayArray.add(left);
						componentsArrayArray.add(right);
					}
				}
			}
		}
		return toolReport;
	}
	public JSONArray getPdfDiffReport(String reportPath,String jobName,int nextBuildNumber)throws Exception {
		logger.info("getPdfDiffReport report path is "+reportPath);
		System.out.println("getPdfDiffReport report path is "+reportPath);
		File f=new File(reportPath);
		File[] fileList=f.listFiles();
		File detailedReports=new File(reportPath);
		File summaryReports=new File(reportPath);
		File summary=new File(reportPath);
		String summaryTimeStamp="0";
		System.out.println("file name is "+summaryReports.getName());
		System.out.println("file length is "+summaryReports.listFiles().length);
		for(File file:summaryReports.listFiles()){
			System.out.println(" 30 file is "+file.getName());
			if(file.isFile()) {
				if(file.getName().contains(".xls")) {
					String[] fileNameArr=file.getName().split("~");
					String currentFileTimeStamp=fileNameArr[fileNameArr.length-1];
					System.out.println("currentFileTimeStamp is "+currentFileTimeStamp);
					if(currentFileTimeStamp.compareToIgnoreCase(summaryTimeStamp)>0) {
						System.out.println("currentFileTimeStamp "+currentFileTimeStamp+" is newer than "+summaryTimeStamp);
						summaryTimeStamp=currentFileTimeStamp;
						summary=file;
					}else {
						System.out.println("currentFileTimeStamp "+currentFileTimeStamp+" is not newer than "+summaryTimeStamp);
					}

				}

			}
		}
		HSSFWorkbook workBook;
		FileInputStream fileInput;
		try {
			fileInput=new FileInputStream(summary);
			workBook=new HSSFWorkbook(fileInput);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
//			return null;
		}
		return excelToJson(workBook,reportPath,jobName,nextBuildNumber,detailedReports,
				summaryReports,summary);
	}
	
	public JSONArray excelToJson(HSSFWorkbook workBook,String reportPath,String jobName,int nextBuildNumber,File detailedReports,
			File summaryReports,File summary)throws Exception {
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
//		JSONObject chartName=new JSONObject();
//		JSONObject chartValue=new JSONObject();
//		JSONObject chartLabels=new JSONObject();
//		JSONObject tabularData=new JSONObject();
		int pass=0;
		int fail=0;
		HSSFSheet comparisonResult=workBook.getSheet("Comparison Results");
		Iterator<Row> itr=comparisonResult.iterator();
		JSONArray chartValueArray=new JSONArray();
		JSONArray chartLabelsArray=new JSONArray();
		JSONArray tabularDataArray=new JSONArray();
		ArrayList<String> attributes=new ArrayList<String>();
		while(itr.hasNext()) {
			Row row=itr.next();
			Iterator<Cell> cellItr=row.iterator();
			if(row.getRowNum()==0) {
				while(cellItr.hasNext()) {
					Cell cell=cellItr.next();
					if(cell!=null) {
						if(cell.getCellType()!=Cell.CELL_TYPE_BLANK) {
							String cellHeader=cell.getStringCellValue();
							cellHeader=cellHeader.replaceAll(" ","_");
							attributes.add(cellHeader);
						}else {
							break;
						}
					}else {
						break;
					}
				}
				continue;
			}
			System.out.println("attributes.size() "+attributes.size());
			JSONArray table=new JSONArray();
			for(int i=0;i<attributes.size();i++) {
				Cell cell=row.getCell(i);
				if(cell!=null) {
					if(cell.getCellType()!=Cell.CELL_TYPE_BLANK) {
						if(i==attributes.size()-1) {
							String cellValue=cell.getStringCellValue();
//							cellValue.replaceAll("\\", "/");
							String[] urlElems=cellValue.split("\\\\");
							String htmlName=urlElems[urlElems.length-1];
							String htmlPath=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
							htmlPath=htmlPath+htmlName;
							JSONObject obj=new JSONObject();
							obj.put(attributes.get(i),htmlPath);
							System.out.println("attribute"+attributes.get(i)+" htmlPath "+htmlPath);
							table.add(obj);
						}else {
							if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
								double cellValue=cell.getNumericCellValue();
								JSONObject obj=new JSONObject();
								obj.put(attributes.get(i),cellValue);
								System.out.println("attribute"+attributes.get(i)+" cellValue "+cellValue);
								table.add(obj);
							}else if(cell.getCellType()==Cell.CELL_TYPE_STRING) {
								String cellValue=cell.getStringCellValue();
								JSONObject obj=new JSONObject();
								boolean isSourceFileCell=false;
								boolean isTargetFileCell=false;
								if(i==0) {
									String[] urlElem=cellValue.split("\\\\");
									cellValue=urlElem[urlElem.length-1];
									isSourceFileCell=true;
								}else if(i==1) {
									String[] urlElem=cellValue.split("\\\\");
									cellValue=urlElem[urlElem.length-1];
									isTargetFileCell=true;
								}
								if(isSourceFileCell==true) {
									obj.put(attributes.get(i), cellValue);
									table.add(obj);
									obj=new JSONObject();
									obj.put("source_link",GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/Source/"+cellValue);
									table.add(obj);
									continue;
								}else if(isTargetFileCell==true) {
									obj.put(attributes.get(i), cellValue);
									table.add(obj);
									obj=new JSONObject();
									obj.put("target_link",GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/Target/"+cellValue);
									table.add(obj);
									continue;									
								}
								obj.put(attributes.get(i), cellValue);
								table.add(obj);
								System.out.println("attribute"+attributes.get(i)+" cellValue "+cellValue);
									if(cellValue.equalsIgnoreCase("fail")) {
										fail++;
									}else if(cellValue.equalsIgnoreCase("pass")) {
										pass++;
									}
							}
						}

					}
				}
			}
			tabularDataArray.add(table);
		}
		chartLabelsArray.add("pass");
		chartLabelsArray.add("fail");
		toolReportObject.put("chart_labels",chartLabelsArray);
		chartValueArray.add(pass);
		chartValueArray.add(fail);
		toolReportObject.put("chart_values",chartValueArray);
		toolReportObject.put("chart_name", "results");
		toolReportObject.put("tabular_data",tabularDataArray);
		toolReportObject.put("level","levelRSA");
		toolReport.add(toolReportObject);
		return toolReport;
	}
	
	public JSONArray excelToJson2(String reportPath,String jobName,int nextBuildNumber,File detailedReports)throws Exception {
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
//		JSONObject chartName=new JSONObject();
//		JSONObject chartValue=new JSONObject();
//		JSONObject chartLabels=new JSONObject();
//		JSONObject tabularData=new JSONObject();
		int pass=0;
		int fail=0;
//		HSSFSheet comparisonResult=workBook.getSheet("Comparison Results");
//		Iterator<Row> itr=comparisonResult.iterator();
		JSONArray chartValueArray=new JSONArray();
		JSONArray chartLabelsArray=new JSONArray();
		JSONArray tabularDataArray=new JSONArray();
		ArrayList<String> attributes=new ArrayList<String>();
		
		String currentTimeStamp="0";
		File latestFile=new File(reportPath);
		for(File file:detailedReports.listFiles()) {
			if(file.getName().contains(".html")) {
				String fileName=file.getName();
				String[] fileNameArr=fileName.split("~");
				String fileTimeStamp=fileNameArr[fileNameArr.length-2];
				System.out.println("fileTimeStamp is "+fileTimeStamp+" currentTimeStampIs "+currentTimeStamp);
				if(currentTimeStamp.compareTo(fileTimeStamp)<0) {
					System.out.println(fileTimeStamp+" is newer compared to "+currentTimeStamp);
					currentTimeStamp=fileTimeStamp;
					latestFile=file;
				}
			}
		}
		String htmlPath=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
		htmlPath=htmlPath+latestFile.getName();
		JSONArray table=new JSONArray();
		JSONObject obj=new JSONObject();
		obj.put("HTML_REPORT", htmlPath);
		table.add(obj);
		tabularDataArray.add(table);
		
		chartLabelsArray.add("pass");
		chartLabelsArray.add("fail");
		toolReportObject.put("chart_labels",chartLabelsArray);
		chartValueArray.add(pass);
		chartValueArray.add(fail);
		toolReportObject.put("chart_values",chartValueArray);
		toolReportObject.put("chart_name", "results");
		toolReportObject.put("tabular_data",tabularDataArray);
		toolReport.add(toolReportObject);
		return toolReport;
	}

}
