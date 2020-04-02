package com.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.mongo.constants.GlobalConstants;

public class XcheckAdapter {
	private int pass;
	private int fail;
	private int total;
	private int noRun;
	public JSONObject getXcheckReports(String reportPath,String jobName,int nextBuildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception {
		System.out.println("inside getXcheckReports");
		File f=new File(reportPath+"ExcelReports/");
		File[] fileList=f.listFiles();
		for(File file:fileList) {
			System.out.println("getXcheckReports: fileName is "+file.getName());
			if(file.getName().contains(".xls") && !file.getName().contains(".xlsx")) {
				FileInputStream fileInput;
				HSSFWorkbook workBook;
				JSONArray jsonArray=new JSONArray();
				try {
					fileInput=new FileInputStream(file);
					workBook=new HSSFWorkbook(fileInput);
				}catch(Exception e) {
					e.printStackTrace();
					throw e;
				}
				HSSFSheet sheet=workBook.getSheet("Result");
				if(sheet!=null) {
					System.out.println("report is not null for "+file.getName());
					return convertExcelTOJSON3(sheet,reportPath,jobName,nextBuildNumber);
				}
			}

		}
		return null;	
		//return convertExcelTOJSON(sheet);
	}
	
	public JSONArray getXcheckReportsForFast(String reportPath,String jobName,int nextBuildNumber) throws Exception{
		System.out.println("inside getXcheckReports");
		File f=new File(reportPath+"ExcelReports/");
		File[] fileList=f.listFiles();
		for(File file:fileList) {
			System.out.println("getXcheckReports: fileName is "+file.getName());
			if(file.getName().contains(".xls") && !file.getName().contains(".xlsx")) {
				FileInputStream fileInput;
				HSSFWorkbook workBook;
				JSONArray jsonArray=new JSONArray();
				try {
					fileInput=new FileInputStream(file);
					workBook=new HSSFWorkbook(fileInput);
				}catch(Exception e) {
					e.printStackTrace();
					return null;
				}
				HSSFSheet sheet=workBook.getSheet("Result");
				if(sheet!=null) {
					System.out.println("report is not null for "+file.getName());
					return convertExcelTOJSONForFast(sheet,reportPath,jobName,nextBuildNumber);
				}
			}

		}
		return null;	
		//return convertExcelTOJSON(sheet);
	}
	
	public JSONArray convertExcelTOJSON(HSSFSheet sheet)throws Exception{
		Iterator<Row> itr=sheet.iterator();
		JSONObject jsonObj=new JSONObject();
		JSONArray jsonArr=new JSONArray();
		Row row1=sheet.getRow(1);
		Row row2=sheet.getRow(2);
		Iterator<Cell> cellItr1=row1.iterator();
		ArrayList<String> fields1=new ArrayList<String>();
		ArrayList<String> fields2=new ArrayList<String>();
		ArrayList<String> finalFields=new ArrayList<String>();
		int noOfTargetBrowsers=0;
		while(cellItr1.hasNext()) {
			Cell cell=cellItr1.next();
			if(cell==null)
				break;
			fields1.add(cell.getStringCellValue());
			
		}
		for(int i=6;;i=i+2) {
			Cell cell=row2.getCell(i);
			if(cell!=null) {
				if(cell.getStringCellValue().equalsIgnoreCase("ie")||cell.getStringCellValue().equalsIgnoreCase("firefox")
						||cell.getStringCellValue().equalsIgnoreCase("chrome")||cell.getStringCellValue().equalsIgnoreCase("safari")
						||cell.getStringCellValue().equalsIgnoreCase("android")||cell.getStringCellValue().equalsIgnoreCase("ios")) {
					noOfTargetBrowsers++;
				}else {
					break;
				}
			}else {
				break;
			}
		}
		for(int i=5;i<=5+(noOfTargetBrowsers*2);i++) {
			Cell cell=row2.getCell(i);
			fields2.add(cell.getStringCellValue());
		}
		finalFields.add(fields1.get(3));
		finalFields.add(fields1.get(4));
		for(int i=0;i<fields2.size();i++) {
			finalFields.add(fields2.get(i));
		}
//		finalFields.add(fields2.get(0));
//		finalFields.add(fields2.get(1));
//		finalFields.add(fields2.get(2));
		for(int i=0;i<finalFields.size();i++) {
			System.out.println(finalFields.get(i));
		}
		while(itr.hasNext()) {
			System.out.println("outer while");
			JSONObject pageTitle=new JSONObject();
			Row currentRow=itr.next();
			if(currentRow.getRowNum()==0 ||currentRow.getRowNum()==1 ||currentRow.getRowNum()==2 ||currentRow.getRowNum()==3)
				continue;
			if(currentRow.getCell(0)!=null) {
				System.out.println("bazinga");
					pageTitle=new JSONObject();
					try {
						pageTitle.put("pageTitle",currentRow.getCell(0).getStringCellValue());					
					}catch(Exception e) {
						e.printStackTrace();
					}
	
					JSONArray components=new JSONArray();
					currentRow=itr.next();
					System.out.println("currentRow" +currentRow.getRowNum());
					if(currentRow.getCell(1)==null) {
						currentRow=itr.next();
						System.out.println("currentRow" +currentRow.getRowNum());
					}
					while(currentRow.getCell(0)==null && itr.hasNext()) {
						System.out.println("inner while");
						JSONObject componentObjects=new JSONObject();
						JSONArray inner=new JSONArray();
						System.out.println(currentRow.getCell(1).getStringCellValue());
						String componentId=currentRow.getCell(1).getStringCellValue();
						String componentIdentifier=currentRow.getCell(2).getStringCellValue();
						currentRow=itr.next();
						while(currentRow.getCell(1)==null) {
						for(int j=3;j<=5+(noOfTargetBrowsers*2);j++) {
							JSONObject jsonObjInner=new JSONObject();
							if(finalFields.get(j-3).equalsIgnoreCase("status")) {
								total++;
								if(currentRow.getCell(j)!=null && currentRow.getCell(j).getStringCellValue().equalsIgnoreCase("pass")) {
									pass++;
								}else if(currentRow.getCell(j)!=null &&currentRow.getCell(j).getStringCellValue().equalsIgnoreCase("fail")) {
									fail++;
								}
							}
							if(currentRow.getCell(j)!=null && currentRow.getCell(j).getCellType()==Cell.CELL_TYPE_STRING) {
								try {
									jsonObjInner.put(finalFields.get(j-3), currentRow.getCell(j).getStringCellValue());
								}catch(Exception e) {
									e.printStackTrace();
								}
	
							}else if(currentRow.getCell(j)!=null && currentRow.getCell(j).getCellType()==Cell.CELL_TYPE_NUMERIC){
								try {
									jsonObjInner.put(finalFields.get(j-3), currentRow.getCell(j).getNumericCellValue());
								}catch(Exception e) {
									e.printStackTrace();
								}
	
							}
							inner.add(jsonObjInner);
	
								}
						if(!itr.hasNext())
							break;
						currentRow=itr.next();
							}
						try {
							componentObjects.put("componentId", componentId);
							componentObjects.put("componentIdentifier", componentIdentifier);
							componentObjects.put("componentAttributes",inner);
							components.add(componentObjects);
						}catch(Exception e) {
							e.printStackTrace();
						}
	
						}
					try {
						pageTitle.put("components",components);					
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
//				if(currentRow.getCell(1)!=null) {
//					JSONArray inner=new JSONArray();
//					String componentId=currentRow.getCell(1).getStringCellValue();
//					String componentIdentifier=currentRow.getCell(1).getStringCellValue();
//					currentRow=itr.next();
//					while(currentRow.getCell(1)==null) {
//						for(int j=3;j<=7;j++) {
//							JSONObject jsonObjInner=new JSONObject();
//							if(currentRow.getCell(j).getCellType()==Cell.CELL_TYPE_STRING) {
//								try {
//									jsonObjInner.put(finalFields.get(j-3), currentRow.getCell(j).getStringCellValue());
//								}catch(Exception e) {
//									e.printStackTrace();
//								}
//
//							}else {
//								try {
//									jsonObjInner.put(finalFields.get(j-3), currentRow.getCell(j).getNumericCellValue());
//								}catch(Exception e) {
//									e.printStackTrace();
//								}
//
//							}
//							inner.add(jsonObjInner);
//
//						}
//					}
//					
//				}
//			}else if(currentRow.getCell(1)!=null) {
//				JSONArray inner=new JSONArray();
//				String componentId=currentRow.getCell(1).getStringCellValue();
//				String componentIdentifier=currentRow.getCell(1).getStringCellValue();
//				currentRow=itr.next();
//				while(currentRow.getCell(1)==null) {
//					for(int j=3;j<=7;j++) {
//						JSONObject jsonObjInner=new JSONObject();
//						if(currentRow.getCell(j).getCellType()==Cell.CELL_TYPE_STRING) {
//							try {
//								jsonObjInner.put(finalFields.get(j-3), currentRow.getCell(j).getStringCellValue());
//							}catch(Exception e) {
//								
//							}
//
//						}else {
//							try {
//								jsonObjInner.put(finalFields.get(j-3), currentRow.getCell(j).getNumericCellValue());
//							}catch(Exception e) {
//								
//							}
//
//						}
//						inner.add(jsonObjInner);
//					}
//				}
//			}
			jsonArr.add(pageTitle);
		}
		System.out.println("jsonArr "+jsonArr);
		return jsonArr;
	}
	public JSONArray convertExcelTOJSON2(HSSFSheet sheet)throws Exception{
		Iterator<Row> itr=sheet.iterator();
		JSONObject jsonObj=new JSONObject();
		JSONArray jsonArr=new JSONArray();
		Row row1=sheet.getRow(1);
		Row row2=sheet.getRow(2);
		Iterator<Cell> cellItr1=row1.iterator();
		ArrayList<String> fields1=new ArrayList<String>();
		ArrayList<String> fields2=new ArrayList<String>();
		ArrayList<String> finalFields=new ArrayList<String>();
		int noOfTargetBrowsers=0;
		while(cellItr1.hasNext()) {
			Cell cell=cellItr1.next();
			if(cell==null)
				break;
			fields1.add(cell.getStringCellValue());
			
		}
		for(int i=6;;i=i+2) {
			Cell cell=row2.getCell(i);
			if(cell!=null && cell.getCellType()!=Cell.CELL_TYPE_BLANK) {
				if(cell.getStringCellValue().equalsIgnoreCase("ie")||cell.getStringCellValue().equalsIgnoreCase("firefox")
						||cell.getStringCellValue().equalsIgnoreCase("chrome")||cell.getStringCellValue().equalsIgnoreCase("safari")
						||cell.getStringCellValue().equalsIgnoreCase("android")||cell.getStringCellValue().equalsIgnoreCase("ios")) {
					noOfTargetBrowsers++;
				}else {
					break;
				}
			}else {
				break;
			}
		}
		for(int i=5;i<=5+(noOfTargetBrowsers*2);i++) {
			Cell cell=row2.getCell(i);
			fields2.add(cell.getStringCellValue());
		}
		finalFields.add(fields1.get(3));
		finalFields.add(fields1.get(4));
		for(int i=0;i<fields2.size();i++) {
			finalFields.add(fields2.get(i));
		}
		Row row=itr.next();
		while(itr.hasNext()) {
			System.out.println("outer");
			if(row.getRowNum()==0 || row.getRowNum()==1 || row.getRowNum()==2 || row.getRowNum()==3) {
				row=itr.next();
				continue;
			}
			if(row.getCell(0)!=null) {
				System.out.println("pageTitle "+row.getCell(0).getStringCellValue());
				JSONObject pageTitle=new JSONObject();
				try {
					pageTitle.put("pageTitle",row.getCell(0).getStringCellValue());					
				}catch(Exception e) {
					e.printStackTrace();
				}
				while((row.getCell(1)==null || row.getCell(1).getCellType()==Cell.CELL_TYPE_BLANK )&& itr.hasNext()) {
					System.out.println("row is empty");
					row=itr.next();
				}
				JSONArray allComponents=new JSONArray();
				while((row.getCell(0)==null) && itr.hasNext()) {
					if(row.getCell(1)!=null && row.getCell(1).getCellType()!=Cell.CELL_TYPE_BLANK) {
						JSONObject components=new JSONObject();
						String componentId=row.getCell(1).getStringCellValue();
						String componentIdentifier=row.getCell(2).getStringCellValue();
						try{
							components.put("componentId",componentId);
							components.put("componentIdentifier", componentIdentifier);
						}catch(Exception e) {
							e.printStackTrace();
						}
						System.out.println("componentId"+componentId);
						System.out.println("componentIdentifier"+componentIdentifier);
						row=itr.next();
						JSONArray ComponentsArray=new JSONArray();
						while(row.getCell(3)!=null && row.getCell(3).getCellType()!=Cell.CELL_TYPE_BLANK) {
							JSONArray testedComponentsArray=new JSONArray();
							for(int j=3;j<=5+(noOfTargetBrowsers*2);j++) {
								JSONObject testedComponents=new JSONObject();
								try {
									if(row.getCell(j)!=null && row.getCell(j).getCellType()==Cell.CELL_TYPE_STRING) {
										testedComponents.put(finalFields.get(j-3),row.getCell(j).getStringCellValue());
										System.out.println(finalFields.get(j-3)+" "+row.getCell(j).getStringCellValue());
									}else if(row.getCell(j)!=null && row.getCell(j).getCellType()==Cell.CELL_TYPE_NUMERIC) {
										testedComponents.put(finalFields.get(j-3),row.getCell(j).getNumericCellValue());
										System.out.println(finalFields.get(j-3)+" "+row.getCell(j).getNumericCellValue());
									}
									
								}catch(Exception e) {
									e.printStackTrace();
								}
								testedComponentsArray.add(testedComponents);
							}
							ComponentsArray.add(testedComponentsArray);
							components.put("componentsArray",ComponentsArray);
							if(itr.hasNext()) {
								row=itr.next();
							}else {
								break;
							}

						}
						allComponents.add(components);
					}
					if(itr.hasNext() && row.getCell(1)==null) {
						row=itr.next();
					}else {
						break;
					}

				}
				pageTitle.put("components",allComponents);
				jsonArr.add(pageTitle);
			}else {
				System.out.println("hello else");
				if(itr.hasNext()) {
					row=itr.next();
				}else {
					break;
				}

			}
			System.out.println("row num is "+row.getRowNum());
		}
		if(row.getCell(0)!=null) {
			System.out.println("pageTitle "+row.getCell(0).getStringCellValue());
			JSONObject pageTitle=new JSONObject();
			pageTitle.put("pageTitle",row.getCell(0).getStringCellValue());
			jsonArr.add(pageTitle);
		}
		System.out.println("jsonArr is "+jsonArr);
		return jsonArr;
	}
	
	public JSONObject convertExcelTOJSON3(HSSFSheet sheet,String reportPath,String jobName,int nextBuildNumber)throws Exception {
		Iterator<Row> itr=sheet.iterator();
		JSONObject jsonObj=new JSONObject();
		JSONArray jsonArr=new JSONArray();
		Row row1=sheet.getRow(1);
		Row row2=sheet.getRow(2);
		Iterator<Cell> cellItr1=row1.iterator();
		ArrayList<String> fields1=new ArrayList<String>();
		ArrayList<String> fields2=new ArrayList<String>();
		ArrayList<String> finalFields=new ArrayList<String>();
		int noOfTargetBrowsers=0;
		while(cellItr1.hasNext()) {
			Cell cell=cellItr1.next();
			if(cell==null)
				break;
			fields1.add(cell.getStringCellValue());
			
		}
		for(int i=6;;i=i+2) {
			Cell cell=row2.getCell(i);
			if(cell!=null && cell.getCellType()!=Cell.CELL_TYPE_BLANK) {
				if(cell.getStringCellValue().equalsIgnoreCase("ie")||cell.getStringCellValue().equalsIgnoreCase("internet explorer")||
						cell.getStringCellValue().equalsIgnoreCase("firefox")
						||cell.getStringCellValue().equalsIgnoreCase("chrome")||cell.getStringCellValue().equalsIgnoreCase("safari")
						||cell.getStringCellValue().equalsIgnoreCase("android")||cell.getStringCellValue().equalsIgnoreCase("ios")) {
					noOfTargetBrowsers++;
				}else {
					break;
				}
			}else {
				break;
			}
		}
		for(int i=5;i<=5+(noOfTargetBrowsers*2);i++) {
			Cell cell=row2.getCell(i);
			fields2.add(cell.getStringCellValue());
		}
		finalFields.add(fields1.get(3));
		finalFields.add(fields1.get(4));
		for(int i=0;i<fields2.size();i++) {
			finalFields.add(fields2.get(i));
		}
		JSONArray pages=new JSONArray();
		int k1=0;
		while(k1<=sheet.getLastRowNum()) {
			Row row=sheet.getRow(k1);
			k1++;
			if(row==null)
				continue;
			//Row row=itr.next();
			System.out.println("row.getRowNum is "+row.getRowNum());
			System.out.println();
			if(row.getRowNum()==0 || row.getRowNum()==1 || row.getRowNum()==2 || row.getRowNum()==3) {
				row=itr.next();
				continue;
			}
			System.out.println("row 4 is "+sheet.getRow(4).getCell(0).toString());
			System.out.println("RowNum is "+row.getRowNum());
			if(row.getCell(0)!=null) {
				if(row.getCell(0).getCellType()!=Cell.CELL_TYPE_BLANK) {
					System.out.println("page type "+row.getCell(0).getStringCellValue());
					JSONObject page=new JSONObject();
					page.put("pageTitle",row.getCell(0).getStringCellValue());
					page.put("components", new JSONArray());
					page.put("screenShots", new JSONArray());
					jsonArr.add(page);
					continue;
				}
			}
			if(row.getCell(1)!=null) {
				if(row.getCell(1).getCellType()!=Cell.CELL_TYPE_BLANK) {
					JSONObject page=(JSONObject)jsonArr.get(jsonArr.size()-1);
					JSONArray components=(JSONArray)page.get("components");
					JSONObject component=new JSONObject();
					component.put("componentid",row.getCell(1).getStringCellValue());
					component.put("componentIdentifier",row.getCell(2).getStringCellValue());
					component.put("componentsArray",new JSONArray());
					component.put("overallStatus", "pass");
					components.add(component);
					System.out.println("componentid "+row.getCell(1).getStringCellValue());
					System.out.println("componentIdentifier "+row.getCell(1).getStringCellValue());
					continue;
				}
			}
			if(row.getCell(3)!=null) {
				if(row.getCell(3).getCellType()!=Cell.CELL_TYPE_BLANK) {
				JSONObject page=(JSONObject)jsonArr.get(jsonArr.size()-1);
					JSONArray components=(JSONArray)page.get("components");
					JSONObject component=(JSONObject)components.get(components.size()-1);	
					JSONArray componentsArray=(JSONArray)component.get("componentsArray");
					JSONArray rowComponents=new JSONArray();
					for(int j=3;j<=5+(noOfTargetBrowsers*2);j++) {
						JSONObject cellComponent=new JSONObject();
						if(row.getCell(j)!=null && row.getCell(j).getCellType()==Cell.CELL_TYPE_STRING) {
							cellComponent.put(finalFields.get(j-3),row.getCell(j).getStringCellValue());
							if(finalFields.get(j-3).equalsIgnoreCase("status") && row.getCell(j).getStringCellValue().equalsIgnoreCase("fail")) {
								component.put("overallStatus","fail");
								fail++;
							}else if(finalFields.get(j-3).equalsIgnoreCase("status") && row.getCell(j).getStringCellValue().equalsIgnoreCase("pass")) {
								pass++;
							}else if(finalFields.get(j-3).equalsIgnoreCase("status")) {
								noRun++;
							}
							System.out.println(finalFields.get(j-3)+" "+row.getCell(j).getStringCellValue());
						}else if(row.getCell(j)!=null && row.getCell(j).getCellType()==Cell.CELL_TYPE_NUMERIC) {
							cellComponent.put(finalFields.get(j-3),row.getCell(j).getNumericCellValue());
							System.out.println(finalFields.get(j-3)+" "+row.getCell(j).getNumericCellValue());
						}
						rowComponents.add(cellComponent);
					}
					componentsArray.add(rowComponents);
					continue;
				}
			}
			if(row.getCell(5)!=null) {
				if(row.getCell(5).getCellType()!=Cell.CELL_TYPE_BLANK) {
					System.out.println("row5 is not empty");
					JSONObject page=(JSONObject)jsonArr.get(jsonArr.size()-1);
					JSONArray screenShots=(JSONArray)page.get("screenShots");
					for(int j=5;j<=5+(noOfTargetBrowsers*2);j++) {
						String screenShotPathMashup=row.getCell(j).getStringCellValue();
						String[] screenShotPathMashupArray=screenShotPathMashup.split("\"");
						String screenShotPath=screenShotPathMashupArray[1];
						String pattern = Pattern.quote(System.getProperty("file.separator"));
						String[] screenShotPathArray=screenShotPath.split(pattern);
						String screenShotName=screenShotPathArray[screenShotPathArray.length-1];
						File f=new File(reportPath);
						String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
						screenshotUrl=screenshotUrl+"Screenshots/"+screenShotName;
						JSONObject screenShotObject =new JSONObject();
						screenShotObject.put("screenShot",screenShotName);
						screenShotObject.put("url",screenshotUrl);
						screenShots.add(screenShotObject);
						//this code is pretty good but very cumbersome :'(
//						File f=new File(reportPath);
//						File[] fileList=f.listFiles();
//						File screenShotFile=getFileWithScreenShotName(fileList,screenShotName);
//						Path pathScreenShotAbsolute=Paths.get(screenShotPath);
						
					}
					continue;
				}
			}
			
		}
		System.out.println("jsonArr is "+jsonArr);
		JSONObject toolReports=new JSONObject();
		JSONArray chartLabels=new JSONArray();
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("noRun");
		JSONArray chartValues=new JSONArray();
		chartValues.add(pass);
		chartValues.add(fail);
		chartValues.add(noRun);
		JSONArray tabularData=jsonArr;
		JSONObject chartName=new JSONObject();
		toolReports.put("chart_name", jobName);
		toolReports.put("chart_labels", chartLabels);
		toolReports.put("chart_values", chartValues);
		toolReports.put("tabular_data", tabularData);
		
		return toolReports;
	}
	
	public JSONArray convertExcelTOJSONForFast(HSSFSheet sheet,String reportPath,String jobName,int nextBuildNumber)throws Exception {
		Iterator<Row> itr=sheet.iterator();
		JSONObject jsonObj=new JSONObject();
		JSONArray jsonArr=new JSONArray();
		Row row1=sheet.getRow(1);
		Row row2=sheet.getRow(2);
		Iterator<Cell> cellItr1=row1.iterator();
		ArrayList<String> fields1=new ArrayList<String>();
		ArrayList<String> fields2=new ArrayList<String>();
		ArrayList<String> finalFields=new ArrayList<String>();
		int noOfTargetBrowsers=0;
		while(cellItr1.hasNext()) {
			Cell cell=cellItr1.next();
			if(cell==null)
				break;
			fields1.add(cell.getStringCellValue());
			
		}
		for(int i=6;;i=i+2) {
			Cell cell=row2.getCell(i);
			if(cell!=null && cell.getCellType()!=Cell.CELL_TYPE_BLANK) {
				if(cell.getStringCellValue().equalsIgnoreCase("ie")||cell.getStringCellValue().equalsIgnoreCase("internet explorer")||
						cell.getStringCellValue().equalsIgnoreCase("firefox")
						||cell.getStringCellValue().equalsIgnoreCase("chrome")||cell.getStringCellValue().equalsIgnoreCase("safari")
						||cell.getStringCellValue().equalsIgnoreCase("android")||cell.getStringCellValue().equalsIgnoreCase("ios")) {
					noOfTargetBrowsers++;
				}else {
					break;
				}
			}else {
				break;
			}
		}
		for(int i=5;i<=5+(noOfTargetBrowsers*2);i++) {
			Cell cell=row2.getCell(i);
			fields2.add(cell.getStringCellValue());
		}
		finalFields.add(fields1.get(3));
		finalFields.add(fields1.get(4));
		for(int i=0;i<fields2.size();i++) {
			finalFields.add(fields2.get(i));
		}
		JSONArray pages=new JSONArray();
		while(itr.hasNext()) {
			Row row=itr.next();
			if(row.getRowNum()==0 || row.getRowNum()==1 || row.getRowNum()==2 || row.getRowNum()==3) {
				row=itr.next();
				continue;
			}
			if(row.getCell(0)!=null) {
				if(row.getCell(0).getCellType()!=Cell.CELL_TYPE_BLANK) {
					System.out.println("page type "+row.getCell(0).getStringCellValue());
					JSONObject page=new JSONObject();
					page.put("pageTitle",row.getCell(0).getStringCellValue());
					page.put("components", new JSONArray());
					page.put("screenShots", new JSONArray());
					jsonArr.add(page);
					continue;
				}
			}
			if(row.getCell(1)!=null) {
				if(row.getCell(1).getCellType()!=Cell.CELL_TYPE_BLANK) {
					JSONObject page=(JSONObject)jsonArr.get(jsonArr.size()-1);
					JSONArray components=(JSONArray)page.get("components");
					JSONObject component=new JSONObject();
					component.put("componentid",row.getCell(1).getStringCellValue());
					component.put("componentIdentifier",row.getCell(2).getStringCellValue());
					component.put("componentsArray",new JSONArray());
					component.put("overallStatus", "pass");
					components.add(component);
					System.out.println("componentid "+row.getCell(1).getStringCellValue());
					System.out.println("componentIdentifier "+row.getCell(1).getStringCellValue());
					continue;
				}
			}
			if(row.getCell(3)!=null) {
				if(row.getCell(3).getCellType()!=Cell.CELL_TYPE_BLANK) {
				JSONObject page=(JSONObject)jsonArr.get(jsonArr.size()-1);
					JSONArray components=(JSONArray)page.get("components");
					JSONObject component=(JSONObject)components.get(components.size()-1);	
					JSONArray componentsArray=(JSONArray)component.get("componentsArray");
					JSONArray rowComponents=new JSONArray();
					for(int j=3;j<=5+(noOfTargetBrowsers*2);j++) {
						JSONObject cellComponent=new JSONObject();
						if(row.getCell(j)!=null && row.getCell(j).getCellType()==Cell.CELL_TYPE_STRING) {
							cellComponent.put(finalFields.get(j-3),row.getCell(j).getStringCellValue());
							if(finalFields.get(j-3).equalsIgnoreCase("status") && row.getCell(j).getStringCellValue().equalsIgnoreCase("fail")) {
								component.put("overallStatus","fail");
								fail++;
							}else if(finalFields.get(j-3).equalsIgnoreCase("status") && row.getCell(j).getStringCellValue().equalsIgnoreCase("pass")) {
								pass++;
							}else if(finalFields.get(j-3).equalsIgnoreCase("status")) {
								noRun++;
							}
							System.out.println(finalFields.get(j-3)+" "+row.getCell(j).getStringCellValue());
						}else if(row.getCell(j)!=null && row.getCell(j).getCellType()==Cell.CELL_TYPE_NUMERIC) {
							cellComponent.put(finalFields.get(j-3),row.getCell(j).getNumericCellValue());
							System.out.println(finalFields.get(j-3)+" "+row.getCell(j).getNumericCellValue());
						}
						rowComponents.add(cellComponent);
					}
					componentsArray.add(rowComponents);
					continue;
				}
			}
			if(row.getCell(5)!=null) {
				if(row.getCell(5).getCellType()!=Cell.CELL_TYPE_BLANK) {
					System.out.println("row5 is not empty");
					JSONObject page=(JSONObject)jsonArr.get(jsonArr.size()-1);
					JSONArray screenShots=(JSONArray)page.get("screenShots");
					for(int j=5;j<=5+(noOfTargetBrowsers*2);j++) {
						String screenShotPathMashup=row.getCell(j).getStringCellValue();
						String[] screenShotPathMashupArray=screenShotPathMashup.split("\"");
						String screenShotPath=screenShotPathMashupArray[1];
						String pattern = Pattern.quote(System.getProperty("file.separator"));
						String[] screenShotPathArray=screenShotPath.split(pattern);
						String screenShotName=screenShotPathArray[screenShotPathArray.length-1];
						String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
						screenshotUrl=screenshotUrl+"Screenshots/"+screenShotName;
//						String screenshotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/htmlreports/HTML_Report/MostRecent/";
//						screenshotUrl=screenshotUrl+"Screenshots/"+screenShotName;
						JSONObject screenShotObject =new JSONObject();
						screenShotObject.put("screenShot",screenShotName);
						screenShotObject.put("url",screenshotUrl);
						screenShots.add(screenShotObject);
						//this code is pretty good but very cumbersome :'(
//						File f=new File(reportPath);
//						File[] fileList=f.listFiles();
//						File screenShotFile=getFileWithScreenShotName(fileList,screenShotName);
//						Path pathScreenShotAbsolute=Paths.get(screenShotPath);
						
					}
					continue;
				}
			}
			
		}
		System.out.println("jsonArr is "+jsonArr);
		JSONObject toolReports=new JSONObject();
		JSONArray chartLabels=new JSONArray();
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("noRun");
		JSONArray chartValues=new JSONArray();
		chartValues.add(pass);
		chartValues.add(fail);
		chartValues.add(noRun);
		JSONArray tabularData=jsonArr;
		JSONObject chartName=new JSONObject();
		toolReports.put("chart_name", jobName);
		toolReports.put("chart_labels", chartLabels);
		toolReports.put("chart_values", chartValues);
		toolReports.put("tabular_data", tabularData);
		
		return tabularData;
	}
	//this is for general files, but i wont be doing that right now
	public File getFileWithScreenShotName(File[] fileList,String screenShotName) {
		for(File file:fileList) {
			if(file.isDirectory()) {
				File[] fileListNew=file.listFiles();
				getFileWithScreenShotName(fileList,screenShotName);
			}else if(file.isFile()) {
				if(file.getName().equals(screenShotName)) {
					return file;
				}
			}
		}
		return null;
	}
	public JSONObject getToolReport(JSONArray tabularData){
		JSONObject obj=new JSONObject();
		JSONArray chartLabels=new JSONArray();
		JSONArray chartValues=new JSONArray();
		JSONObject chartNameObj=(JSONObject)tabularData.get(0);
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("total");
		chartValues.add(pass);
		chartValues.add(fail);
		chartValues.add(total);
		obj.put("chart_name", chartNameObj.get("pageTitle"));
		obj.put("chart_labels", chartLabels);
		obj.put("chart_values", chartValues);
		obj.put("tabular_data", tabularData);
		return obj;
	}
}
