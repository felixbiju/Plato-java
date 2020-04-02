package com.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.adapters.utilities.AdapterExcelToJson;

public class ETDMAdapter {
	AdapterExcelToJson excelToJson=new AdapterExcelToJson();
	long countTrue,countFalse;
	long ttoalcount=0;
	public JSONArray GetJsonArray(String reportPath,String jobName)throws Exception {
		JSONArray jsonArray=new JSONArray();
		JSONArray tabularData=new JSONArray();
		JSONObject chartReport=new JSONObject();
		try {
			//File f=new File(reportPath+"scriptresults.csv");
			/*FileInputStream fileInput=new FileInputStream(f);
			XSSFWorkbook workBook=new XSSFWorkbook(fileInput);
			XSSFSheet sheet=workBook.getSheet("Detail Summary");*/
			//XSSFSheet sheet=csvToXLSX(reportPath+"scriptresults.csv",reportPath+"test.xlsx");
			//jsonArray=excelToJson.convertExcelTOJsonArrayJMeter(sheet);
			
			//tabularData=csvToJSONArrayNew(reportPath);
			tabularData=xlsToJsonArray(reportPath);
			chartReport.put("chart_name", jobName);
			ArrayList<String> chartLabels=new ArrayList<String>();
			chartLabels.add("Total Records");
			//chartLabels.add("FALSE");
			chartReport.put("chart_labels",chartLabels);
			ArrayList<Long> chartValues=new ArrayList<Long>();
			chartValues.add(ttoalcount);
			//chartValues.add(countFalse);
			chartReport.put("chart_values",chartValues);
			chartReport.put("tabular_data",tabularData);
			jsonArray.add(chartReport);
			return jsonArray;
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
//			return jsonArray;
		}
	}
	
	public JSONArray csvToJsonArray(String reportPath)throws Exception {
		JSONArray jsonArray=new JSONArray();
		LinkedHashMap<Object, Object> jsonOrderedMap = new LinkedHashMap<Object, Object>();
		JSONArray tabularData=new JSONArray();
		countTrue=0;
		countFalse=0;
		try {
			//File f=new File(reportPath+"scriptresults.csv");
			tabularData=new JSONArray();
			String currentLine=null;
			BufferedReader br = new BufferedReader(new FileReader(reportPath+"scriptresults.csv"));
			ArrayList<String> fields=new ArrayList<String>();
			fields.add("Transaction Name");
			fields.add("Status");
			fields.add("Response Time");
			fields.add("Throughput (Byte)");
			fields.add("Response Resource information");
			while((currentLine=br.readLine())!=null) {
				String[] str=currentLine.split("\"");
				tabularData=new JSONArray();
				if(str.length<=1 || str==null ){
					str=currentLine.split(",");
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(0),str[2]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(1),str[7]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(2),str[1]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(3),str[8]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(4),str[4]);
					tabularData.add(jsonOrderedMap);
					jsonArray.add(tabularData);
					if(str[7].equalsIgnoreCase("true") || str[7].equalsIgnoreCase("passed")) {
						countTrue++;
					}else if(str[7].equalsIgnoreCase("false") || str[7].equalsIgnoreCase("failed")) {
						countFalse++;
					}						
					
				}else {
					str=currentLine.split(",");
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(0),str[2]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(1),str[8]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(2),str[1]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(3),str[9]);
					tabularData.add(jsonOrderedMap);
					jsonOrderedMap = new LinkedHashMap<Object, Object>();
					jsonOrderedMap.put(fields.get(4),str[4]+str[5]);
					tabularData.add(jsonOrderedMap);
					jsonArray.add(tabularData);
					if(str[8].equalsIgnoreCase("true") || str[8].equalsIgnoreCase("passed")) {
						countTrue++;
					}else if(str[8].equalsIgnoreCase("false") || str[8].equalsIgnoreCase("failed")) {
						countFalse++;
					}					
				}
			}
			br.close();
			return jsonArray;
			
		}catch(Exception e) {
			
			e.printStackTrace();
			return jsonArray;
		}
	
	}
	
	public JSONArray xlsToJsonArray(String reportPath)throws Exception{
		FileInputStream fileInput;
		Workbook workBook;
		JSONArray tabularData=new JSONArray();
		File f=new File(reportPath);
		for(File file:f.listFiles()) {
			if(file.isFile()) {
				String fileName=file.getName();
				fileName=fileName.toLowerCase();
				if(fileName.contains(".xls")||fileName.contains(".xlsx")) {
					f=file;
					break;
				}
			}
		}
		
		countTrue=0;
		countFalse=0;
		
		try {
			fileInput=new FileInputStream(f);
//			workBook=new HSSFWorkbook(fileInput);
			workBook=WorkbookFactory.create(f);
			fileInput.close();
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
//			return null;
		}
		Sheet sheet=workBook.getSheetAt(0);
		Iterator<Row> itr=sheet.iterator();
		Row headersRow=sheet.getRow(0);
		
		Iterator<Cell> cellItr=headersRow.cellIterator();
		ArrayList<String> attributes=new ArrayList<String>();
		while(cellItr.hasNext()) {
			Cell currentCell=cellItr.next();
			attributes.add(currentCell.getStringCellValue());
			System.out.println("attribute "+currentCell.getStringCellValue());
			}
		while(itr.hasNext()) {
			ttoalcount++;
			System.out.println(ttoalcount+ " ==============suzzzy");
			Row currentRow=itr.next();
			if(currentRow.getRowNum()==0) {
				continue;
			}
			cellItr=currentRow.cellIterator();
			int attrCount=0;
			JSONArray tabularDataObjArray=new JSONArray();
			for(int i=0;i<attributes.size();i++) {
				JSONObject tabularObj=new JSONObject();
				Cell currentCell=currentRow.getCell(i,Row.CREATE_NULL_AS_BLANK);
				String attribute=attributes.get(i);
				attrCount++;
				System.out.println("attr is "+attribute);
				if(currentCell.getCellType()==Cell.CELL_TYPE_BOOLEAN) {
					tabularObj.put(attribute,currentCell.getBooleanCellValue());
					if(currentCell.getBooleanCellValue()==true) {
						countTrue++;
					}else if(currentCell.getBooleanCellValue()==false) {
						countFalse++;
					}
					System.out.println("value is "+currentCell.getBooleanCellValue());
				}else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
					tabularObj.put(attribute,currentCell.getNumericCellValue());
					System.out.println("value is "+currentCell.getNumericCellValue());
				}else if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
					tabularObj.put(attribute,currentCell.getStringCellValue());
					System.out.println("value is "+currentCell.getStringCellValue());
					if(currentCell.getStringCellValue().equalsIgnoreCase("true") ||currentCell.getStringCellValue().equalsIgnoreCase("passed") ) {
						countTrue++;
					}else if(currentCell.getStringCellValue().equalsIgnoreCase("false") ||currentCell.getStringCellValue().equalsIgnoreCase("failed")) {
						countFalse++;
					}
				}else if(currentCell.getCellType()==Cell.CELL_TYPE_BLANK) {
					tabularObj.put(attribute,"");
					System.out.println("empty ");
				}
				tabularDataObjArray.add(tabularObj);
			}
			tabularData.add(tabularDataObjArray);
		}
		return tabularData;
	}
	
	public JSONArray csvToJSONArrayNew(String reportPath)throws Exception {
		File f=new File(reportPath);
		JSONArray tabularData=new JSONArray();
		for(File file:f.listFiles()) {
			if(file.isFile()) {
				String fileName=file.getName();
				fileName=fileName.toLowerCase();
				if(fileName.contains(".csv")) {
					if(fileName.equalsIgnoreCase("result.csv")) {
						f=file;
						break;						
					}
				}
			}
		}
		countTrue=0;
		countFalse=0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f.toString()));
			String currentLine=null;
			int lineCount=1;
			ArrayList<String> fields=new ArrayList<String>();
			while((currentLine=br.readLine())!=null) {
				String[] lineSplit=currentLine.split(",");
//				System.out.println(lineSplit.toString());
				for(int i=0;i<lineSplit.length;i++) {
					System.out.println("lineSplit "+lineSplit[i]);
				}
				ArrayList<String> values=new ArrayList<String>();
				if(lineCount==1) {
					int i=0;
					while(i<lineSplit.length) {
						String val;
						if(lineSplit[i].charAt(0)=='"') {
							int j=i+1;
							val=lineSplit[i];
							while(lineSplit[j].charAt(lineSplit[j].length()-1)!='"') {
								j++;
								val+=lineSplit[j];
							}
							val=val+lineSplit[j];
							i=j;
						}else {
							val=lineSplit[i];
						}
						fields.add(val);
						i++;					
					}
					lineCount++;
					continue;
				}
				int i=0;
				while(i<lineSplit.length) {
					System.out.println("WHILE lineSplit["+i+"] = "+lineSplit[i]);
					if(lineSplit[i]==null) {
						values.add("");
						i++;
						continue;
					}
					lineSplit[i]=lineSplit[i].trim();
					if(lineSplit[i].length()==0) {
						values.add("");
						i++;
						continue;
					}
					if(lineSplit[i].charAt(0)=='"') {
						int j=i+1;
						String val=lineSplit[i]+",";
						while(lineSplit[j].charAt(lineSplit[j].length()-1)!='"') {
							j++;
							val+=lineSplit[j]+",";
						}
						val=val+lineSplit[j];
						val=val.replaceAll("\"", "");
						i=j;
						values.add(val);
					}else {
						String val=lineSplit[i];
						values.add(val);
					}
					i++;
				}
				lineCount++;
				JSONArray tabularDataObjArray=new JSONArray();
				System.out.println("fileds size "+fields.size());
				System.out.println("values.size "+values.size());
				for(int k=0;k<fields.size();k++) {
					JSONObject tabularObj=new JSONObject();
					try {
						tabularObj.put(fields.get(k),values.get(k));
						if(values.get(k).equalsIgnoreCase("false")) {
							countFalse++;
						}else if(values.get(k).equalsIgnoreCase("true")) {
							countTrue++;
						}
					}catch(IndexOutOfBoundsException e) {
						tabularObj.put(fields.get(k),"");
					}

					tabularDataObjArray.add(tabularObj);
				}
				tabularData.add(tabularDataObjArray);
			}
			br.close();
			return tabularData;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		
	}
	
	public static XSSFSheet csvToXLSX(String csvFileAddress, String xlsxFileAddress )throws Exception {
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("sheet1");
		try {
	        String currentLine=null;
	        int RowNum=0;
	        BufferedReader br = new BufferedReader(new FileReader(csvFileAddress));
	        while ((currentLine = br.readLine()) != null) {
	            String str[] = currentLine.split(",");
	            RowNum++;
	            XSSFRow currentRow=sheet.createRow(RowNum);
	            for(int i=0;i<str.length;i++){
	                currentRow.createCell(i).setCellValue(str[i]);
	            }
	        }

	        FileOutputStream fileOutputStream =  new FileOutputStream(xlsxFileAddress);
	        workBook.write(fileOutputStream);
	        fileOutputStream.close();
			File f=new File(xlsxFileAddress);
			FileInputStream fileInput=new FileInputStream(f);
			workBook=new XSSFWorkbook(fileInput);
			sheet=workBook.getSheet("sheet1");
	        System.out.println("Done");
	        br.close();
	        return sheet;
	    } catch (Exception e) {
	       e.printStackTrace();
	       return sheet;
	    }
	}
}
