package com.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class OwaspZapAdapter {
	public JSONArray getToolReportJsonArray(String reportPath) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,SAXException,ParserConfigurationException, Exception {
		int countLow=0,countMedium=0,countHigh=0,countInfo=0;
		JSONArray toolReport=new JSONArray();
		try {
			File file=new File(reportPath);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			NodeList nodeList=doc.getElementsByTagName("alertitem");
			for(int i=0;i<nodeList.getLength();i++) {
				Element alertItem=(Element)nodeList.item(i);
				Element riskDesc=(Element)alertItem.getElementsByTagName("riskdesc");
				String riskDescVal=riskDesc.getTextContent();
				String[] riskDescValArr=riskDescVal.split(" ");
				switch(riskDescValArr[0]) {
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
			}
			ArrayList<String>chartLabelArray=new ArrayList<String>();
			chartLabelArray.add("Low");
			chartLabelArray.add("Medium");
			chartLabelArray.add("High");
			chartLabelArray.add("Informational");
			JSONObject chartLabels=new JSONObject();
			chartLabels.put("chart_labels", chartLabelArray);
			ArrayList<Integer>chartValueArray=new ArrayList<Integer>();
			chartValueArray.add(countLow);
			chartValueArray.add(countMedium);
			chartValueArray.add(countHigh);
			chartValueArray.add(countInfo);
			JSONObject chartValues=new JSONObject();
			chartValues.put("chart_values",chartValueArray);
			toolReport.add(chartLabels);
			toolReport.add(chartValues);
			return toolReport;
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
//			return toolReport;
		}

	}
}
