package com.adapters;

import java.io.File;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class RsaImpactAdapter {
	public JSONObject GetJsonObject(String reportPath) throws Exception {
		JSONObject report=new JSONObject();
		report.put("name", "chart");
		Workbook workBook=null;
		File f=new File(reportPath);
		try {
			workBook=WorkbookFactory.create(f);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		Sheet jlPetSheet=workBook.getSheet("Impact-JL Pet");
		Sheet jlEventSheet=workBook.getSheet("Impact-JL Event");
		JSONObject jlPetObj=new JSONObject();
		JSONObject jlEventObj=new JSONObject();
		JSONArray jlPetChildren=new JSONArray();
		JSONArray jlEventChildren=new JSONArray();
		jlPetObj.put("name","Impact-JL Pet");
		jlEventObj.put("name","Impact-JL Event");
		report.put("children",new JSONArray());
		JSONArray jlLevel=(JSONArray)report.get("children");
		jlLevel.add(jlPetObj);
		jlLevel.add(jlEventObj);
		jlPetObj.put("children", jlPetChildren);
		jlEventObj.put("children",jlEventChildren);
		Iterator<Row> itr=jlPetSheet.iterator();
		while(itr.hasNext()) {
			Row currentRow=itr.next();
			if(currentRow.getRowNum()==0 ||currentRow.getRowNum()==1 ||currentRow.getRowNum()==2 ) {
				continue;
			}
			if(currentRow.getCell(2).getCellType()==Cell.CELL_TYPE_BLANK) {
				JSONObject component=new JSONObject();
				component.put("name",currentRow.getCell(1).getStringCellValue());
				component.put("children", new JSONArray());
				jlPetChildren.add(component);
				System.out.println("header found");
			}else{
				System.out.println("else-header found");
				JSONObject component=(JSONObject)jlPetChildren.get(jlPetChildren.size()-1);
				JSONArray componentChildren=(JSONArray)component.get("children");
				JSONObject componentAttr=new JSONObject();
				componentAttr.put("name",currentRow.getCell(1).getStringCellValue());
				componentAttr.put("children", new JSONArray());
				componentChildren.add(componentAttr);
				Iterator<Cell> cellIterator=currentRow.iterator();
				while(cellIterator.hasNext()) {
					Cell currentCell=cellIterator.next();
					if(currentCell.getColumnIndex()<4) {
						continue;
					}
					if(currentCell.getCellType()==Cell.CELL_TYPE_BLANK) {
						continue;
					}
					if(jlPetSheet.getRow(2).getCell(currentCell.getColumnIndex()).getCellType()!=Cell.CELL_TYPE_BLANK) {
						JSONArray componentAttrChildren=(JSONArray)componentAttr.get("children");
						JSONObject product=new JSONObject();
						product.put("name",jlPetSheet.getRow(2).getCell(currentCell.getColumnIndex()).getStringCellValue());
						product.put("children", new JSONArray());
						componentAttrChildren.add(product);
					}
					JSONArray componentAttrChildren=(JSONArray)componentAttr.get("children");
					JSONObject product=(JSONObject)componentAttrChildren.get(componentAttrChildren.size()-1);
					JSONArray productChildren=(JSONArray)product.get("children");
					JSONObject transaction=new JSONObject();
					transaction.put("name",jlPetSheet.getRow(1).getCell(currentCell.getColumnIndex()).getStringCellValue()+
							""+jlPetSheet.getRow(3).getCell(currentCell.getColumnIndex()).getStringCellValue());
					transaction.put("size",currentCell.getNumericCellValue());
					productChildren.add(transaction);
				}
			}
		}
		
		itr=jlEventSheet.iterator();
		while(itr.hasNext()) {
			Row currentRow=itr.next();
			if(currentRow.getRowNum()==0 ||currentRow.getRowNum()==1 ||currentRow.getRowNum()==2 ) {
				continue;
			}
			if(currentRow.getCell(2).getCellType()==Cell.CELL_TYPE_BLANK) {
				JSONObject component=new JSONObject();
				component.put("name",currentRow.getCell(1).getStringCellValue());
				component.put("children", new JSONArray());
				jlEventChildren.add(component);
				System.out.println("header found");
			}else{
				System.out.println("else-header found");
				JSONObject component=(JSONObject)jlEventChildren.get(jlEventChildren.size()-1);
				JSONArray componentChildren=(JSONArray)component.get("children");
				JSONObject componentAttr=new JSONObject();
				componentAttr.put("name",currentRow.getCell(1).getStringCellValue());
				componentAttr.put("children", new JSONArray());
				componentChildren.add(componentAttr);
				Iterator<Cell> cellIterator=currentRow.iterator();
				while(cellIterator.hasNext()) {
					Cell currentCell=cellIterator.next();
					if(currentCell.getColumnIndex()<4) {
						continue;
					}
					if(currentCell.getCellType()==Cell.CELL_TYPE_BLANK) {
						continue;
					}
					if(jlPetSheet.getRow(2).getCell(currentCell.getColumnIndex()).getCellType()!=Cell.CELL_TYPE_BLANK) {
						JSONArray componentAttrChildren=(JSONArray)componentAttr.get("children");
						JSONObject product=new JSONObject();
						product.put("name",jlPetSheet.getRow(2).getCell(currentCell.getColumnIndex()).getStringCellValue());
						product.put("children", new JSONArray());
						componentAttrChildren.add(product);
					}
					JSONArray componentAttrChildren=(JSONArray)componentAttr.get("children");
					JSONObject product=(JSONObject)componentAttrChildren.get(componentAttrChildren.size()-1);
					JSONArray productChildren=(JSONArray)product.get("children");
					JSONObject transaction=new JSONObject();
					transaction.put("name",jlPetSheet.getRow(1).getCell(currentCell.getColumnIndex()).getStringCellValue()+
							""+jlPetSheet.getRow(3).getCell(currentCell.getColumnIndex()).getStringCellValue());
					transaction.put("size",currentCell.getNumericCellValue());
					productChildren.add(transaction);
				}
			}
		}		
		System.out.println("report is "+report.toJSONString());
		return report;
	}

}
