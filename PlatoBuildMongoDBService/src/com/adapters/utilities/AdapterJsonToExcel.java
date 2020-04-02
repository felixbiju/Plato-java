package com.adapters.utilities;
import java.io.File;
import org.json.CDL;
import org.json.JSONArray;
import org.apache.commons.io.FileUtils;
public class AdapterJsonToExcel {
	public void convertJsonToExcel(String jsonString,String reportPath) {
		try {
			//JSONObject obj=new JSONObject(jsonString);
			System.out.println("jsonString is "+jsonString);
			JSONArray arr=new JSONArray(jsonString);
			//arr.put(obj);
			File f=new File(reportPath+"/jsonToExcel.csv");
			String csv=CDL.toString(arr);
			System.out.println("csv is "+csv);
			FileUtils.writeStringToFile(f, csv);
		}catch(Exception e) {
			e.printStackTrace();
		}

		
	}

}
