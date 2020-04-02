package com.adapters;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class JUnitAdapter {
	private static final Logger logger=Logger.getLogger(JUnitAdapter.class);
	public JSONObject getJsonObject(String reportPath) throws Exception{
		logger.debug("report path is "+reportPath);
		File f=new File(reportPath);
		File[] fileList=f.listFiles();
		int totalPassed=0,totalFailures=0,totalErrors=0,totalSkipped=0;
		JSONObject report=new JSONObject();
		JSONObject testTotal=new JSONObject();
		JSONArray tests=new JSONArray();
		try {
			testTotal.put("errors",totalErrors);
			testTotal.put("passed",totalPassed );
			testTotal.put("failures",totalFailures);
			testTotal.put("skipped",totalSkipped);
			report.put("testTotal",testTotal);
		}catch(Exception e) {
			e.printStackTrace();
		}
		for(File file :fileList) {
			if(file.isFile()) {
				if(file.toString().contains(".xml")) {
					try {
						DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
						Document doc = docBuilder.parse(file);
						Element testSuite=(Element)doc.getElementsByTagName("testsuite").item(0);
						if(testSuite!=null) {
							JSONObject obj=new JSONObject();
							int test=Integer.parseInt(testSuite.getAttribute("tests"));
							int failures=Integer.parseInt(testSuite.getAttribute("failures"));
							int errors=Integer.parseInt(testSuite.getAttribute("errors"));
							int skipped=Integer.parseInt(testSuite.getAttribute("skipped"));
							String name=testSuite.getAttribute("name");
							if(name.contains(".")) {
								System.out.println("name is "+name);
								String[] nameArr=name.split("\\.");
								System.out.println("nameArr.length is "+nameArr.length);
								name=nameArr[nameArr.length-1];
							}
							int passed=test-failures-errors-skipped;
							obj.put("test_name",name);
							obj.put("errors",errors);
							obj.put("passed",passed);
							obj.put("failures",failures);
							obj.put("skipped",skipped);
							tests.put(obj);
							totalPassed+=passed;
							totalFailures+=failures;
							totalErrors+=errors;
							totalSkipped+=skipped;
						}
						
					}catch(Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		try {
			testTotal.put("errors",totalErrors);
			testTotal.put("passed",totalPassed );
			testTotal.put("failures",totalFailures);
			testTotal.put("skipped",totalSkipped);
			report.put("testTotal",testTotal);
			report.put("tests",tests);
			
		}catch(Exception e) {
			e.printStackTrace();
		}

		return report;
	}
}
