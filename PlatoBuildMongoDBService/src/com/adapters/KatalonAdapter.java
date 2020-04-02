package com.adapters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongo.constants.GlobalConstants;

public class KatalonAdapter {

    public static void main(String[] args)throws Exception {
    	

		

    	
    	String line = "";
        String cvsSplitBy = ",";
    	int passCount=0;
		int failCount=0;
		int otherCount=0;
		int testSuiteflag=0;
		int testCaseflag=0;
	
		
    	JSONParser parser = new JSONParser();
    	JSONObject testCase=new JSONObject();
    	
    	JSONArray toolReportArray=new JSONArray();
		JSONObject toolReportObj=new JSONObject();
		JSONArray tabularDataArray=new JSONArray();
		JSONObject tabularDataObj=new JSONObject();
		JSONArray componentsArray=new JSONArray();
		JSONObject componentsObj=new JSONObject();
		
    
    try {

    	//String jobName=
    	
    	String  jenkinsReportPath="D:\\KatalonReport\\F20180705_031418";
    	//String  jenkinsReportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+jobName+"/"+buildNumber+"/";
    	File f=new File(jenkinsReportPath);
    	
    	 File[] fList = f.listFiles();
    	 for (File file : fList){
             System.out.println(file.getName());
             
             File actualReportPath=new File(jenkinsReportPath+"\\"+file.getName());
             
             File jsnRepo=new File(jenkinsReportPath+"\\"+file.getName()+"\\JSON_Report.json");
             System.out.println(jenkinsReportPath+"\\"+file.getName()+"\\JSON_Report.json");
                
             
         
    	
    	System.out.println(f.isDirectory());
    	
        Object obj = parser.parse(new FileReader(jenkinsReportPath+"\\"+file.getName()+"\\JSON_Report.json"));

        JSONObject jsonObject = (JSONObject) obj;

        String testSuiteName = (String) jsonObject.get("name");

        JSONArray testCases = (JSONArray) jsonObject.get("childRecords");

        System.out.println("testSuiteName: " + testSuiteName);
      
        Iterator<JSONObject> iterator = testCases.iterator();
        while (iterator.hasNext()) {
        	
        	testCase=iterator.next();
        	String testCaseStatus=testCase.get("interuppted").toString();
        	if(testCaseStatus.equals("false")) {
        		testCaseStatus="Pass";
        	}else {
        		testCaseStatus="Fail";
        	}
        	String testCaseName = (String) testCase.get("description");
        	
        	JSONArray testStepsArr = (JSONArray) testCase.get("childRecords");
        	JSONArray stepsArray=new JSONArray();
        	 Iterator<JSONObject> iteTestSteps = testStepsArr.iterator();
             while (iteTestSteps.hasNext()) {
            	 JSONObject testStep=new JSONObject();
            	 testStep=iteTestSteps.next();
            	 
            	 if(!testStep.get("name").toString().equals("")) {
            	JSONObject stepNoObj=new JSONObject();
            	stepNoObj.put("Step No", testStep.get("index"));
                	 
            		 
            	JSONObject stepNameObj=new JSONObject();
            	String stepName=testStep.get("name").toString();
            	if(stepName.contains("=")) {
            		String[] stepArr=stepName.split("=");
               	 stepNameObj.put("Step Name", stepArr[0]);
            	}else {
            	 stepNameObj.put("Step Name", testStep.get("name"));
            	} 
 				
            	 JSONObject descriptionObj=new JSONObject();
            	 descriptionObj.put("Description", testStep.get("message").toString().replaceAll("\\u0027", "\'"));
  				
 				//JSONObject executionTimeObj=new JSONObject();
 				//executionTimeObj.put("Execution Time", csvLine[5]);
 				
 				JSONObject statusObj=new JSONObject();
 				String status=testStep.get("interuppted").toString();
 				if(status.equals("false")) { 				
 				statusObj.put("Status","Pass");
 				passCount++;
 				}else {
 					statusObj.put("Status", "Fail");
 					failCount++;
 				}
 				
 				JSONArray eachStepArray=new JSONArray();
 				eachStepArray.add(stepNoObj);
 				eachStepArray.add(stepNameObj);
 				eachStepArray.add(descriptionObj);
 				
 			//	eachStepArray.add(executionTimeObj);
 				eachStepArray.add(statusObj);
 				
 				stepsArray.add(eachStepArray);
 	    	    		
            }
             
             }
             componentsObj.put("componentid", testCaseName);
     		componentsObj.put("componentsArray",stepsArray);
     		componentsObj.put("overallStatus", testCaseStatus); 

				

        }
        
      
        toolReportObj.put("chart_name",testSuiteName);
        
        JSONArray chartLabels=new JSONArray();
        chartLabels.add("pass");
		chartLabels.add("fail");
        toolReportObj.put("chart_labels", chartLabels);
        
        JSONArray chartValues=new JSONArray();
        chartValues.add(passCount);
    	chartValues.add(failCount);
    	toolReportObj.put("chart_values", chartValues);

    
    	
    	componentsArray.add(componentsObj);
    	
    	
    	tabularDataObj.put("components",componentsArray);
    	tabularDataObj.put("pageTitle", testSuiteName);//testCaseName
    //	tabularDataObj.put("OverAll_Status", testSuiteStatus);//testCaseStatus
    	tabularDataObj.put("total_test_cases", "1");
    	
 
    	
    	tabularDataArray.add(tabularDataObj);
    	
    	toolReportObj.put("tabular_data", tabularDataArray);
    	toolReportArray.add(toolReportObj);
    	System.out.println(toolReportArray);
       
        
    	 }
        
    } catch (Exception e) {
        e.printStackTrace();
    }

    
    
		
		
	
    }

	public JSONArray getKatalonReports(String reportPath, String jobName, int buildNumber) throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,ParseException,Exception{

    	
    	int passCount=0;
		int failCount=0;
	
    	JSONParser parser = new JSONParser();
    	JSONObject testCase=new JSONObject();
    	
    	JSONArray toolReportArray=new JSONArray();
		JSONObject toolReportObj=new JSONObject();
		JSONArray tabularDataArray=new JSONArray();
		JSONObject tabularDataObj=new JSONObject();
		JSONArray componentsArray=new JSONArray();
		JSONObject componentsObj=new JSONObject();
		
    
    try {

    	//String jenkinsReportPath=reportPath;
    	
    	//String  jenkinsReportPath="D:\\KatalonReport\\F20180705_031418";
    	String  jenkinsReportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+jobName+"/"+buildNumber+"\\JSON_Report.json";
    	
    	System.out.println("Report Path is:  "+jenkinsReportPath);
    //	File f=new File(jenkinsReportPath);
    	
    	// File[] fList = f.listFiles();
    	 //for (File file : fList){
            // System.out.println(file.getName());
             
      //       File actualReportPath=new File(jenkinsReportPath+"\\"+f.getName());
             
           //  File jsnRepo=new File(jenkinsReportPath+"\\"+f.getName()+"\\JSON_Report.json");
          //   System.out.println(jenkinsReportPath+"\\"+f.getName()+"\\JSON_Report.json");
                
             
         
    	
    
    	
        Object obj = parser.parse(new FileReader(jenkinsReportPath));

        JSONObject jsonObject = (JSONObject) obj;

        String testSuiteName = (String) jsonObject.get("name");

        JSONArray testCases = (JSONArray) jsonObject.get("childRecords");

        System.out.println("testSuiteName: " + testSuiteName);
      
        Iterator<JSONObject> iterator = testCases.iterator();
        while (iterator.hasNext()) {
        	
        	testCase=iterator.next();
        	String testCaseStatus=testCase.get("interuppted").toString();
        	if(testCaseStatus.equals("false")) {
        		testCaseStatus="Pass";
        	}else {
        		testCaseStatus="Fail";
        	}
        	String testCaseName = (String) testCase.get("description");
        	
        	JSONArray testStepsArr = (JSONArray) testCase.get("childRecords");
        	JSONArray stepsArray=new JSONArray();
        	 Iterator<JSONObject> iteTestSteps = testStepsArr.iterator();
             while (iteTestSteps.hasNext()) {
            	 JSONObject testStep=new JSONObject();
            	 testStep=iteTestSteps.next();
            	 
            	 if(!testStep.get("name").toString().equals("")) {
            	JSONObject stepNoObj=new JSONObject();
            	stepNoObj.put("Step No", testStep.get("index"));
                	 
            		 
            	JSONObject stepNameObj=new JSONObject();
            	String stepName=testStep.get("name").toString();
            	if(stepName.contains("=")) {
            		String[] stepArr=stepName.split("=");
               	 stepNameObj.put("Step Name", stepArr[0]);
            	}else {
            	 stepNameObj.put("Step Name", testStep.get("name"));
            	} 
 				
            	 JSONObject descriptionObj=new JSONObject();
            	 descriptionObj.put("Message", testStep.get("message").toString().replaceAll("\\u0027", "\'"));
  				
 				//JSONObject executionTimeObj=new JSONObject();
 				//executionTimeObj.put("Execution Time", csvLine[5]);
 				
 				JSONObject statusObj=new JSONObject();
 				String status=testStep.get("interuppted").toString();
 				if(status.equals("false")) { 				
 				statusObj.put("Status","Pass");
 				passCount++;
 				}else {
 					statusObj.put("Status", "Fail");
 					failCount++;
 				}
 				
 				JSONArray eachStepArray=new JSONArray();
 				eachStepArray.add(stepNoObj);
 				eachStepArray.add(stepNameObj);
 				eachStepArray.add(descriptionObj);
 				
 			//	eachStepArray.add(executionTimeObj);
 				eachStepArray.add(statusObj);
 				
 				stepsArray.add(eachStepArray);
 	    	    		
            }
             
             }
             componentsObj.put("componentid", testCaseName);
     		componentsObj.put("componentsArray",stepsArray);
     		componentsObj.put("overallStatus", testCaseStatus); 

				

        }
        
      
        toolReportObj.put("chart_name",testSuiteName);
        
        JSONArray chartLabels=new JSONArray();
        chartLabels.add("pass");
		chartLabels.add("fail");
        toolReportObj.put("chart_labels", chartLabels);
        
        JSONArray chartValues=new JSONArray();
        chartValues.add(passCount);
    	chartValues.add(failCount);
    	toolReportObj.put("chart_values", chartValues);

    
    	
    	componentsArray.add(componentsObj);
    	
    	
    	tabularDataObj.put("components",componentsArray);
    	tabularDataObj.put("pageTitle", testSuiteName);//testCaseName
    //	tabularDataObj.put("OverAll_Status", testSuiteStatus);//testCaseStatus
    	tabularDataObj.put("total_test_cases", "1");
    	
 
    	
    	tabularDataArray.add(tabularDataObj);
    	
    	toolReportObj.put("tabular_data", tabularDataArray);
    	toolReportArray.add(toolReportObj);
    	System.out.println(toolReportArray);
       
        
    	 
        
    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
	return toolReportArray;

    
    
		
		
	
    
		
	}
	
	
	
	

}