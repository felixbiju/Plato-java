package com.adapters.utilities;

import org.json.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class AdapterExcelToJson {
	public JSONArray ConvertExcelTOJsonArray(XSSFSheet sheet) {
		Iterator<Row> itr=sheet.iterator();
		LinkedHashMap<Object, Object> jsonOrderedMap = new LinkedHashMap<Object, Object>();
		JSONObject jsonObj=new JSONObject(jsonOrderedMap);
		JSONArray jsonArr=new JSONArray();
		ArrayList<String> fields=new ArrayList<String>();
		int count=0;
		try {
			while(itr.hasNext()) {
				Row currentRow=itr.next();
				Iterator<Cell> cellItr=currentRow.iterator();
				JSONObject currJsonObj=new JSONObject(jsonOrderedMap);
				if(count==0) {
					while(cellItr.hasNext()) {
						Cell currentCell=cellItr.next();
    					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
    						String currentVal=currentCell.getStringCellValue().replaceAll(" ", "_");
    						currentVal=currentVal.replaceAll("\\.", "");
    						fields.add(currentVal);
    					}else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    						String currentVal=Double.toString(currentCell.getNumericCellValue());
    						currentVal=currentVal.replaceAll("\\.", "");
    						currentVal=currentVal.replaceAll(" ", "_");
    						fields.add(currentVal);
    					}
					}
				}else {
					int countFields=0;
    				while(cellItr.hasNext()) {
    					Cell currentCell=cellItr.next();
    					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
    						currJsonObj.put(fields.get(countFields),currentCell.getStringCellValue() );
    						//System.out.print(currentCell.getStringCellValue()+" ");
    					}
    						
    					else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    						currJsonObj.put(fields.get(countFields),currentCell.getNumericCellValue());
       						//System.out.print(currentCell.getNumericCellValue()+" ");
    					}
    					countFields++;

    				}
    				
    				System.out.println("json is "+currJsonObj.toString()); 
    				jsonArr.put(currJsonObj);
				}
				count++;
				
			}
			System.out.println("jsonArray is "+jsonArr.toString());
			return jsonArr;
		}catch(Exception e) {
			e.printStackTrace();
			return jsonArr;
		}
	}
	
	public org.json.simple.JSONArray ConvertExcelTOJsonArrayPreserveOrder(XSSFSheet sheet) {
		Iterator<Row> itr=sheet.iterator();
		LinkedHashMap<Object, Object> jsonOrderedMap = new LinkedHashMap<Object, Object>();
		JSONObject jsonObj=new JSONObject(jsonOrderedMap);
		org.json.simple.JSONArray jsonArr=new org.json.simple.JSONArray();
		ArrayList<String> fields=new ArrayList<String>();
		int count=0;
		try {
			while(itr.hasNext()) {
				Row currentRow=itr.next();
				Iterator<Cell> cellItr=currentRow.iterator();
				//JSONObject currJsonObj=new JSONObject(jsonOrderedMap);
				jsonOrderedMap = new LinkedHashMap<Object, Object>();
				if(count==0) {
					while(cellItr.hasNext()) {
						Cell currentCell=cellItr.next();
    					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
    						String currentVal=currentCell.getStringCellValue().replaceAll(" ", "_");
    						currentVal=currentVal.replaceAll("\\.", "");
    						fields.add(currentVal);
    						//jsonObj.put(currentCell.getStringCellValue().replaceAll(" ", "_"),"");
    					}else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    						String currentVal=Double.toString(currentCell.getNumericCellValue());
    						currentVal=currentVal.replaceAll("\\.", "");
    						currentVal=currentVal.replaceAll(" ", "_");
    						fields.add(currentVal);
    					}
					}
				}else {
					int countFields=0;
    				while(cellItr.hasNext()) {
    					Cell currentCell=cellItr.next();
    					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
    						jsonOrderedMap.put(fields.get(countFields),currentCell.getStringCellValue() );
    						//System.out.print(currentCell.getStringCellValue()+" ");
    					}
    						
    					else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    						jsonOrderedMap.put(fields.get(countFields),currentCell.getNumericCellValue());
       						//System.out.print(currentCell.getNumericCellValue()+" ");
    					}
    					countFields++;

    				}
    				
    				System.out.println("jsonOrderedMap is "+jsonOrderedMap.toString()); 
    				jsonArr.add(jsonOrderedMap);
				}
				count++;
				
			}
			System.out.println("jsonArray is "+jsonArr.toString());
			return jsonArr;
		}catch(Exception e) {
			e.printStackTrace();
			return jsonArr;
		}
	}
	
	public org.json.simple.JSONArray dtfConvertExcelTOJsonArrayPreserveOrder(XSSFSheet sheet) {
		Iterator<Row> itr=sheet.iterator();
		LinkedHashMap<Object, Object> jsonOrderedMap = new LinkedHashMap<Object, Object>();
		//JSONObject jsonObj=new JSONObject(jsonOrderedMap);
		org.json.simple.JSONArray jsonArr=new org.json.simple.JSONArray();
		org.json.simple.JSONArray tabularData=new org.json.simple.JSONArray();
		ArrayList<String> fields=new ArrayList<String>();
		int count=0;
		try {
			while(itr.hasNext()) {
				Row currentRow=itr.next();
				Iterator<Cell> cellItr=currentRow.iterator();
				//JSONObject currJsonObj=new JSONObject(jsonOrderedMap);
				jsonOrderedMap = new LinkedHashMap<Object, Object>();
				if(count==0) {
					int fieldOddEvenCount=0;
					while(cellItr.hasNext()) {
						Cell currentCell=cellItr.next();
    					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
    						String currentVal=currentCell.getStringCellValue().replaceAll(" ", "_");
    						currentVal=currentVal.replaceAll("\\.", "");
    						if(fieldOddEvenCount%2==0) {
    							currentVal="s_"+currentVal;
    						}else {
    							currentVal="t_"+currentVal;
    						}
    						fieldOddEvenCount++;
    						fields.add(currentVal);
    						//jsonObj.put(currentCell.getStringCellValue().replaceAll(" ", "_"),"");
    					}else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    						String currentVal=Double.toString(currentCell.getNumericCellValue());
    						currentVal=currentVal.replaceAll("\\.", "");
    						currentVal=currentVal.replaceAll(" ", "_");
    						if(fieldOddEvenCount%2==0) {
    							currentVal="s_"+currentVal;
    						}else {
    							currentVal="t_"+currentVal;
    						}
    						fieldOddEvenCount++;
    						fields.add(currentVal);
    					}
					}
				}else {
					int countFields=0;
					tabularData=new org.json.simple.JSONArray();
    				while(cellItr.hasNext()) {
    					Cell currentCell=cellItr.next();
    					if(currentCell.getCellType()==Cell.CELL_TYPE_STRING) {
    						jsonOrderedMap.put(fields.get(countFields),currentCell.getStringCellValue() );
    						tabularData.add(jsonOrderedMap);
    						jsonOrderedMap=new LinkedHashMap<Object, Object>();
    						//System.out.print(currentCell.getStringCellValue()+" ");
    					}
    						
    					else if(currentCell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    						jsonOrderedMap.put(fields.get(countFields),currentCell.getNumericCellValue());
    						tabularData.add(jsonOrderedMap);
    						jsonOrderedMap=new LinkedHashMap<Object, Object>();
       						//System.out.print(currentCell.getNumericCellValue()+" ");
    					}
    					countFields++;

    				}
    				
    				System.out.println("jsonOrderedMap is "+jsonOrderedMap.toString()); 
    				jsonArr.add(tabularData);
				}
				count++;
				
			}
			System.out.println("jsonArray is "+jsonArr.toString());
			return jsonArr;
		}catch(Exception e) {
			e.printStackTrace();
			return jsonArr;
		}
	}
	
	public org.json.simple.JSONArray convertExcelTOJsonArrayJMeter(XSSFSheet sheet) {
		Iterator<Row> itr=sheet.iterator();
		LinkedHashMap<Object, Object> jsonOrderedMap = new LinkedHashMap<Object, Object>();
		JSONObject jsonObj=new JSONObject(jsonOrderedMap);
		org.json.simple.JSONArray jsonArr=new org.json.simple.JSONArray();
		ArrayList<String> fields=new ArrayList<String>();
		fields.add("Response Time");
		fields.add("Transaction Name");
		fields.add("Response Resource information");
		fields.add("Status");
		fields.add("Throughput (Byte)");
		int count=0;
		try {
			while(itr.hasNext()) {
				Row currentRow=itr.next();
				jsonOrderedMap = new LinkedHashMap<Object, Object>();
				jsonOrderedMap.put(fields.get(1), currentRow.getCell(2).getStringCellValue());
				jsonOrderedMap.put(fields.get(3), currentRow.getCell(7).getStringCellValue());
				jsonOrderedMap.put(fields.get(0), currentRow.getCell(1).getNumericCellValue());
				jsonOrderedMap.put(fields.get(4), currentRow.getCell(8).getNumericCellValue());
				jsonOrderedMap.put(fields.get(2), currentRow.getCell(4).getStringCellValue());
				jsonArr.add(jsonOrderedMap);
			}
			return jsonArr;
		}catch(Exception e) {
			e.printStackTrace();
			return jsonArr;
		}
	}
}
