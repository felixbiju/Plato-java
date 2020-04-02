package com.adapters;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class ScaniaToolAdapter{
	public JSONArray getToolReportJsonArray(String reportPath) throws IOException,SAXException,ParserConfigurationException,Exception {
		JSONArray toolReport=new JSONArray();
		JSONObject toolReportObject=new JSONObject();
		
		File reportfolder=new File(reportPath);
		File[] fileList=reportfolder.listFiles();
		File xmlReportFile=null;
		for(File f:fileList) {
			if(f.toString().contains(".xml")) {
				xmlReportFile=f;
				System.out.println("xmlReportFile is "+xmlReportFile);
				break;
			}
		}
		
		JSONArray chartLabels=new JSONArray();
		JSONArray chartValues=new JSONArray();
		JSONArray tabularData=new JSONArray();
		toolReportObject.put("chart_name",xmlReportFile.getName());
		chartLabels.add("pass");
		chartLabels.add("fail");
		chartLabels.add("no_run");
		int pass=0;
		int fail=0;
		int noRun=0;
		toolReportObject.put("chart_labels", chartLabels);
		toolReportObject.put("chart_values",chartValues);
		toolReportObject.put("tabular_data",tabularData );
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlReportFile);
		NodeList testSuiteList=doc.getElementsByTagName("test-suite");
		for(int i=0;i<testSuiteList.getLength();i++) {
			Element testSuite=(Element)testSuiteList.item(i);
			if(testSuite.getAttribute("type").equals("TestSuite")) {
				JSONObject pageTitle=new JSONObject();
				JSONArray components=new JSONArray();
				tabularData.add(pageTitle);
				pageTitle.put("components", components);
				pageTitle.put("pageTitle", testSuite.getAttribute("name"));
				pageTitle.put("OverAll_Status", testSuite.getAttribute("result"));
				if(testSuite.getAttribute("result").equalsIgnoreCase("Success")) {
					pageTitle.put("OverAll_Status","pass");
				}else {
					pageTitle.put("OverAll_Status", "fail");
				}
				NodeList testFixtureList=testSuite.getElementsByTagName("test-suite");
				for(int j=0;j<testFixtureList.getLength();j++) {
					Element testFixture=(Element)testFixtureList.item(j);
					if(testFixture.getAttribute("type").equals("TestFixture")) {
						JSONObject componentsObject=new JSONObject();
						JSONArray componentsArray=new JSONArray();
						componentsObject.put("componentid", testFixture.getAttribute("name"));
						componentsObject.put("overallStatus",testFixture.getAttribute("result"));
						if(testFixture.getAttribute("result").equalsIgnoreCase("Success")) {
							componentsObject.put("overallStatus","pass");
						}else {
							componentsObject.put("overallStatus","fail");
						}
						componentsObject.put("componentsArray",componentsArray);
						NodeList testSteps=testFixture.getElementsByTagName("test-case");
						for(int k=0;k<testSteps.getLength();k++) {
							Element testStep=(Element)testSteps.item(k);
							JSONArray componentsArrayArray=new JSONArray();
							JSONObject obj=new JSONObject();
							obj.put("name", testStep.getAttribute("name"));
							componentsArrayArray.add(obj);
							obj=new JSONObject();
							obj.put("description", testStep.getAttribute("description"));
							componentsArrayArray.add(obj);
							obj=new JSONObject();
							obj.put("result", testStep.getAttribute("result"));
							componentsArrayArray.add(obj);
							obj=new JSONObject();
							obj.put("time", testStep.getAttribute("time"));
							componentsArrayArray.add(obj);
							obj=new JSONObject();
							obj.put("success", testStep.getAttribute("success"));
							componentsArrayArray.add(obj);
							obj=new JSONObject();
							obj.put("executed", testStep.getAttribute("executed"));
							componentsArrayArray.add(obj);
							obj=new JSONObject();
							obj.put("asserts", testStep.getAttribute("asserts"));
							componentsArrayArray.add(obj);
							componentsArray.add(componentsArrayArray);
							if(testStep.getAttribute("result").equalsIgnoreCase("success")) {
								pass++;
							}else {
								fail++;
							}
						}
						components.add(componentsObject);
					}
				}
			}
		}
		chartValues.add(pass);
		chartValues.add(fail);
		chartValues.add(noRun);
		toolReport.add(toolReportObject);
		return toolReport;
	}

}
