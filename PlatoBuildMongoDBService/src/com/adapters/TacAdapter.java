package com.adapters;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.adapters.utilities.AdapterXmlToJson;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class TacAdapter {
	public JSONArray getReport(String reportPath,String jobName,int buildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,Exception {
		File f=new File(reportPath+"/Quote_Batch Results.xml");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(f);
		}catch(Exception e) {
			e.printStackTrace();
		}
		AdapterXmlToJson adapterXmlToJson=new AdapterXmlToJson();
		Element report=(Element)doc.getElementsByTagName("Reports").item(0);
		Element testScript=(Element)report.getElementsByTagName("TestScript").item(0);
		Element reporter=(Element)testScript.getElementsByTagName("Reporter").item(0);
		String failedCount=reporter.getElementsByTagName("FailedCount").item(0).getTextContent();
		String passedCount=reporter.getElementsByTagName("PassedCount").item(0).getTextContent();
		JSONArray tabularData=new JSONArray();
		JSONObject tabularDataObj=new JSONObject();
		JSONArray components=new JSONArray();
		JSONObject componentsObj=new JSONObject();
		JSONArray componentsArray=new JSONArray();
		tabularData.add(tabularDataObj);
		tabularDataObj.put("components", components);
		tabularDataObj.put("pageTitle",testScript.getAttribute("name"));
		components.add(componentsObj);
		componentsObj.put("componentsArray",componentsArray);
		try {
			if(Integer.parseInt(failedCount)>0) {
				componentsObj.put("overallStatus", "fail");
			}else {
				componentsObj.put("overallStatus", "pass");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		componentsObj.put("componentid",testScript.getAttribute("name"));

		Element reportItems=(Element)reporter.getElementsByTagName("ReportItems").item(0);
		System.out.println("reporter size "+reportItems.getElementsByTagName("ReportItem").getLength());
		for(int i=0;i<reportItems.getElementsByTagName("ReportItem").getLength();i++) {
			System.out.println("hi");
			JSONArray componentsArrayRow=new JSONArray();
			Element reportItem=(Element)reportItems.getElementsByTagName("ReportItem").item(i);
			String captionValue=reportItem.getAttribute("caption");
			String actualResultValue="";
			String expectedResultValue=""; 
			if(reportItem.getElementsByTagName("ActualResult")!=null&&reportItem.getElementsByTagName("ActualResult").item(0)!=null)
				actualResultValue=reportItem.getElementsByTagName("ActualResult").item(0).getTextContent();
			if(reportItem.getElementsByTagName("ExpectedResult")!=null &&reportItem.getElementsByTagName("ExpectedResult").item(0)!=null )
				expectedResultValue=reportItem.getElementsByTagName("ExpectedResult").item(0).getTextContent();
			JSONObject caption=new JSONObject();
			JSONObject actualResult=new JSONObject();
			JSONObject expectedResult=new JSONObject();
			System.out.println("captionValue actualResultValue expectedResultValue "+captionValue+","+actualResultValue+","+expectedResultValue);
			caption.put("caption", captionValue);
			actualResult.put("actualResult",actualResultValue );
			expectedResult.put("expectedResult", expectedResultValue);
//			System.out.println("caption actualResult expectedResult "+caption.toString()+","+actualResult.toString()+","+expectedResult.toString());
			componentsArrayRow.add(caption);
			componentsArrayRow.add(actualResult);
			componentsArrayRow.add(expectedResult);
			componentsArray.add(componentsArrayRow);
			//System.out.println("componentArrayRow is "+componentsArrayRow.toJSONString());
		}
		JSONArray chartLabels=new JSONArray();
		chartLabels.add("passed");
		chartLabels.add("failed");
		JSONArray chartValues=new JSONArray();
		chartValues.add(passedCount);
		chartValues.add(failedCount);
//		org.json.JSONObject obj= adapterXmlToJson.convertXmlToJson(doc);
		JSONObject obj=new JSONObject();
		obj.put("chart_name","Quote_Batch Results");
		obj.put("chart_labels",chartLabels);
		obj.put("chart_values", chartValues);
		obj.put("tabular_data",tabularData);
		JSONArray arr=new JSONArray();
		arr.add(obj);
		System.out.println("arr is "+arr.toString());
		return arr;
	}

}
