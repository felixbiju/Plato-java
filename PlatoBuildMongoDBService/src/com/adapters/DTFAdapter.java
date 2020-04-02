package com.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.adapters.utilities.AdapterExcelToJson;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class DTFAdapter {
	AdapterExcelToJson excelToJson=new AdapterExcelToJson();
	public JSONArray GetJsonArray(String reportPath) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception{
		JSONArray jsonArray=new JSONArray();
		try {
			System.out.println("report path is "+reportPath);
			File f=new File(reportPath);
			File[] fileList=f.listFiles();
			System.out.println("fileList length is "+fileList.length);
			for(File folder:fileList) {
				if(folder.isDirectory()) {
					System.out.println("1.reportPath is "+reportPath);
					reportPath=reportPath+"/"+folder.getName();
					System.out.println("folder is "+folder);
					System.out.println("2.reportPath is "+reportPath);
					File[] fileList2=folder.listFiles();
					System.out.println("fileList2 length is "+fileList2.length);
					for(File folder2:fileList2) {
						if(folder2.isDirectory()) {
							System.out.println("folder2 "+folder2.getName());
							reportPath=reportPath+"/"+folder2.getName();
							f=new File(reportPath+"\\ConsolidatedTestCaseReport.xlsm");
							break;
						}
					}
					break;
				}
			}
			System.out.println("report path is "+reportPath);
			f=new File(reportPath+"/ConsolidatedTestCaseReport.xlsm");
			System.out.println("f absolute path is "+f.getAbsolutePath());
			FileInputStream fileInput=new FileInputStream(f);
			XSSFWorkbook workBook=new XSSFWorkbook(fileInput);
			XSSFSheet sheet=workBook.getSheet("Detail Summary");
			jsonArray=excelToJson.ConvertExcelTOJsonArray(sheet);
			return jsonArray;
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
//			return jsonArray;
		}

	}
	
	public org.json.simple.JSONArray getChartDataAsJSONArray(String reportPath,JSONArray jsonArray) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception{
		org.json.simple.JSONArray result=new org.json.simple.JSONArray();
		for(int j=0;j<jsonArray.length();j++) {
			try {
				org.json.JSONObject obj=(org.json.JSONObject)jsonArray.get(j);
				JSONObject chartReport=new JSONObject();
				chartReport.put("chart_name", obj.get("Configuration_File_Name"));
				ArrayList<String> s=new ArrayList<String>();
				s.add("Mismatched_Records");
				s.add("Source_Extra_Records");
				s.add("Target_Extra_Records");
				chartReport.put("chart_labels",s);
				ArrayList<Double> d=new ArrayList<Double>();
				d.add((double)obj.get("Mismatched_Records"));
				d.add((double)obj.get("Source_Extra_Records"));
				d.add((double)obj.get("Target_Extra_Records"));
				chartReport.put("chart_values", d);
				chartReport.put("level", "levelone");
				JSONArray tabularData=new JSONArray();
				tabularData.put(obj);
				chartReport.put("tabular_data",readDtfFilesMST(reportPath,obj));
				System.out.println("chart report is "+chartReport);
				result.add(chartReport);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("getChartDataAsJSONArray : result is  "+result);
		return result;
	}
	
	//reading files for getting mismatched,source extra And target extra
	public org.json.simple.JSONArray readDtfFilesMST(String reportPath,org.json.JSONObject obj) throws Exception {
		File f=new File(reportPath);
		File[] fileList=f.listFiles();
		for(File folder:fileList) {
			if(folder.isDirectory()) {
				System.out.println("1.reportPath is "+reportPath);
				reportPath=reportPath+"/"+folder.getName();
				System.out.println("folder is "+folder);
				System.out.println("2.reportPath is "+reportPath);
				File[] fileList2=folder.listFiles();
				System.out.println("fileList2 length is "+fileList2.length);
				for(File folder2:fileList2) {
					if(folder2.isDirectory()) {
						System.out.println("folder2 "+folder2.getName());
						reportPath=reportPath+"/"+folder2.getName();
						f=new File(reportPath);
						break;
					}
				}
				break;
			}
		}
		fileList=f.listFiles();
		org.json.simple.JSONArray returnedJsonArray=new org.json.simple.JSONArray();
			try {
				System.out.println("configuration file name is "+(String)obj.get("Configuration_File_Name"));
				System.out.println("mismached records is "+(double)obj.get("Mismatched_Records"));
				if((double)obj.get("Mismatched_Records")>0) {
					for(File file:fileList) {
						//converting both to lowercases and then checking if it is contained in the file
						if(file.isFile()&&file.toString().toLowerCase().contains(((String)obj.get("Configuration_File_Name")).toLowerCase())
							&&(!file.toString().contains("SourceExtraRecords"))&&(!file.toString().contains("TargetExtraRecords"))) {
							System.out.println("file is "+file);
							FileInputStream fileInput=new FileInputStream(file);
							XSSFWorkbook workBook=new XSSFWorkbook(fileInput);
							XSSFSheet sheet=workBook.getSheet("Mismatched Records1");
							returnedJsonArray=convertMisMatchedRecordsTOJSONArray(sheet);
							System.out.println("readDtfFilesMST: returnedJsonArray is  "+returnedJsonArray);
							return returnedJsonArray;
						}
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				throw e;
			}

		return new org.json.simple.JSONArray();
	}
	
	public org.json.simple.JSONArray convertMisMatchedRecordsTOJSONArray(XSSFSheet sheet)throws Exception {
		//sheet.removeRow(sheet.getRow(1));
		//sheet.removeRow(sheet.getRow(1));
		sheet.removeRow(sheet.getRow(sheet.getFirstRowNum()));
		sheet.removeRow(sheet.getRow(sheet.getFirstRowNum()));
		System.out.println(sheet.getRow(1));
		System.out.println("first Row Num"+sheet.getFirstRowNum());
		System.out.println("last row num" +sheet.getLastRowNum());
		System.out.println("convertMisMatchedRecordsTOJSONArray :returned value is "+excelToJson.ConvertExcelTOJsonArrayPreserveOrder(sheet));
		return excelToJson.dtfConvertExcelTOJsonArrayPreserveOrder(sheet);
		/*Iterator<Row> itr=sheet.iterator();
		Row headerRow=sheet.getRow(4);
		ArrayList<String> source=new ArrayList<String>();
		ArrayList<String> target=new ArrayList<String>();
		Iterator<Cell> headerCellItr=headerRow.iterator();
		int count=0;
		while(headerCellItr.hasNext()) {
			Cell currentCell=headerCellItr.next();
			if(count%2==0) {
				source.add(currentCell.getStringCellValue());
			}else {
				target.add(currentCell.getStringCellValue());
			}
			count++;
		}
		ArrayList<Object> sourceValues=new ArrayList<Object>();
		ArrayList<Object> targetValues=new ArrayList<Object>();
		int rowCount=5;
		Row valueRow=sheet.getRow(5);
		while(valueRow!=null) {
			Iterator<Cell> valueCellItr=valueRow.iterator();
			count=0;
			ArrayList<String> sourceValuesRow=new ArrayList<String>();
			ArrayList<String> targetValuesRow=new ArrayList<String>();
			while(valueCellItr.hasNext()) {
				Cell currentCell=valueCellItr.next();
				if(count%2==0) {
					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
						sourceValuesRow.add(currentCell.getStringCellValue().replaceAll(" ", "_"));
						//jsonObj.put(currentCell.getStringCellValue().replaceAll(" ", "_"),"");
					}else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
						sourceValuesRow.add(Double.toString(currentCell.getNumericCellValue()));
					}
				}else {
					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
						targetValuesRow.add(currentCell.getStringCellValue().replaceAll(" ", "_"));
						//jsonObj.put(currentCell.getStringCellValue().replaceAll(" ", "_"),"");
					}else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
						targetValuesRow.add(Double.toString(currentCell.getNumericCellValue()));
					}				
					
				}
			}
			sourceValues.add(sourceValuesRow);
			targetValues.add(targetValuesRow);
			
		}*/
		//return new JSONArray();
	} 
}
