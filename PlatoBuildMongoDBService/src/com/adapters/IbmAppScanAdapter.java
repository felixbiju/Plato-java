package com.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */

public class IbmAppScanAdapter {
	public JSONArray getToolReportJsonArray(String reportPath) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,SAXException,ParserConfigurationException,Exception{
		int countLow=0,countMedium=0,countHigh=0,countInfo=0;
		JSONArray toolReport=new JSONArray();
		JSONArray tabularDataArray=new JSONArray();
		try {
			File file=new File(reportPath);
			File[] fileList=file.listFiles();
			for(File f:fileList) {
				if(f.toString().contains(".xml")) {
					file=f;
					System.out.println("file is "+file);
					break;
				}
			}
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			Element issueGroup=(Element)doc.getElementsByTagName("issue-group").item(0);
			Element urlGroup=(Element)doc.getElementsByTagName("url-group").item(0);
			Element entityGroup=(Element)doc.getElementsByTagName("entity-group").item(0);
			NodeList issueItemList=issueGroup.getElementsByTagName("item");
			for(int i=0;i<issueItemList.getLength();i++) {
				Element item=(Element)issueItemList.item(i);
				Element severity=(Element)item.getElementsByTagName("severity").item(0);
				if(severity!=null) {
					switch(severity.getTextContent()) {
					case "Low":
						countLow++;
						break;
						
					case "Medium":
						countMedium++;
						break;
						
					case "High":
						countHigh++;
						break;
						
					case "Informational":
						countInfo++;
						break;
					default:
						break;
					
					}
					LinkedHashMap jsonData=new LinkedHashMap();
					JSONArray tableRow=new JSONArray();
					Element advisory=(Element)item.getElementsByTagName("advisory").item(0);
					jsonData.put("advisory",advisory.getElementsByTagName("ref").item(0).getTextContent());
					tableRow.add(jsonData);
					jsonData=new LinkedHashMap();
					//jsonData.put("item_id",item.getAttribute("id"));
					Element urlElement=(Element)item.getElementsByTagName("url").item(0);
					Element urlElementRef=(Element)urlElement.getElementsByTagName("ref").item(0);
					NodeList urlGroupItems=urlGroup.getElementsByTagName("item");
					for(int j=0;j<urlGroupItems.getLength();j++) {
						Element urlGroupItem=(Element)urlGroupItems.item(j);
						String urlGroupItemId=urlGroupItem.getAttribute("id");
						if(urlGroupItemId.equalsIgnoreCase(urlElementRef.getTextContent())){
							jsonData.put("url",urlGroupItem.getElementsByTagName("name").item(0).getTextContent());
							tableRow.add(jsonData);
							jsonData=new LinkedHashMap();
							break;
						}
					}
					Element entityElement=(Element)item.getElementsByTagName("entity").item(0);
					Element entityElementRef=(Element)entityElement.getElementsByTagName("ref").item(0);
					NodeList entityGroupItems=entityGroup.getElementsByTagName("item");
					for(int j=0;j<entityGroupItems.getLength();j++) {
						Element entityGroupItem=(Element)entityGroupItems.item(j);
						String entityGroupItemId=entityGroupItem.getAttribute("id");
						if(entityGroupItemId.equalsIgnoreCase(entityElementRef.getTextContent())) {
							jsonData.put("entity",entityGroupItem.getElementsByTagName("name").item(0).getTextContent()
									+"("+entityGroupItem.getElementsByTagName("entity-type").item(0).getTextContent()+")");
							tableRow.add(jsonData);
							jsonData=new LinkedHashMap();
							break;
						}
					}
					jsonData.put("severity",item.getElementsByTagName("severity").item(0).getTextContent());
					tableRow.add(jsonData);
					jsonData=new LinkedHashMap();
					jsonData.put("cvss-score",item.getElementsByTagName("cvss-score").item(0).getTextContent());
					tableRow.add(jsonData);
					jsonData=new LinkedHashMap();
					tabularDataArray.add(tableRow);
				}
				
			}
			ArrayList<String> chartLabelsArray=new ArrayList<String>();
			ArrayList<Integer> chartValuesArray=new ArrayList<Integer>();
			JSONObject chartLabels=new JSONObject();
			JSONObject chartValues=new JSONObject();
			JSONObject tabularData=new JSONObject();
			chartLabelsArray.add("low");
			chartLabelsArray.add("medium");
			chartLabelsArray.add("high");
			chartLabelsArray.add("informational");
			chartLabels.put("chart_labels", chartLabelsArray);
			chartValuesArray.add(countLow);
			chartValuesArray.add(countMedium);
			chartValuesArray.add(countHigh);
			chartValuesArray.add(countInfo);
			chartValues.put("chart_values", chartValuesArray);
			chartValues.put("chart_name",file.getName());
			tabularData.put("tabular_data",tabularDataArray);
			JSONObject toolReportObject=new JSONObject();
			toolReportObject.put("chart_name", file.getName());
			toolReportObject.put("chart_labels", chartLabelsArray);
			toolReportObject.put("chart_values", chartValuesArray);
			toolReportObject.put("tabular_data",tabularDataArray);
			toolReport.add(toolReportObject);
			return toolReport;	
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
//			return toolReport;
		}
	}

}
