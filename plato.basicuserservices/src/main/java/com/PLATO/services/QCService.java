package com.PLATO.services;

import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import com.mercury.qualitycenter.otaclient.IBaseFactory;
import com.mercury.qualitycenter.otaclient.IList;
import com.mercury.qualitycenter.otaclient.IRun;
import com.mercury.qualitycenter.otaclient.IRunFactory;
import com.mercury.qualitycenter.otaclient.ITDConnection;
import com.mercury.qualitycenter.otaclient.ITSTest;
import com.mercury.qualitycenter.otaclient.ITestSet;
import com.mercury.qualitycenter.otaclient.ITestSetFactory;
import com.PLATO.constants.GlobalConstants;


import com4j.Com4jObject;
/**
 * @author 10643380(Rahul Bhardwaj)
 * @author Rohit Chaudhari
 * */
@Path("QCSevice")
public class QCService {
	
	private static final Logger logger = Logger.getLogger(QCService.class);
	private ITDConnection connection;
	@Path("updateQC")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response QCServiceUpdate(String resultJSON) {
		initializeQcConnection();
		logger.debug("QC service updateQC");
		logger.debug("resultJSON is "+resultJSON);
		System.out.println("resultJSON is "+resultJSON);
		JSONParser parser=new JSONParser();
		JSONObject result=new JSONObject();
		try {
			result=(JSONObject)parser.parse(resultJSON);
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error in parsing json").build();
		}
		System.out.println("result is "+result.toJSONString());
		JSONObject buildHistory=(JSONObject)result.get("BuildHistory");
		JSONObject liveBuildConsole=(JSONObject)buildHistory.get("LiveBuildConsole");
		System.out.println("LiveBuildConsole"+liveBuildConsole);
		logger.debug("LiveBuildConsole"+liveBuildConsole);
		JSONArray toolsLiveBuildConsole=(JSONArray)liveBuildConsole.get("tools");
		for(int i=0;i<toolsLiveBuildConsole.size();i++) {
			JSONObject obj=(JSONObject)toolsLiveBuildConsole.get(i);
			boolean isAlmChecked=(boolean)obj.get("isAlmChecked");
			System.out.println("isAlmChecked "+isAlmChecked);
			logger.debug("isAlmChecked "+isAlmChecked);
			if(isAlmChecked==true) {
				String toolName=(String)obj.get("toolName");
				toolName=toolName.replaceAll(" ", "_");
				String subJobName=(String)obj.get("tool_name");
				String status=(String)obj.get("tool_status");
				if(toolName.equalsIgnoreCase("junit")||toolName.equalsIgnoreCase("testng")) {
					System.out.println("junit or testng");
					logger.info("junit or testng");
					//if tool comes under build
					buildAlm(buildHistory,subJobName,status);
					
				}else if(toolName.equalsIgnoreCase("uipath") || toolName.equalsIgnoreCase("hp_qtp")||toolName.equalsIgnoreCase("selenium")||toolName.equalsIgnoreCase("x_check")||toolName.equalsIgnoreCase("selenium_framework")||toolName.equalsIgnoreCase("SELENIUM_FRAMEWORK")||toolName.equalsIgnoreCase("hp qtp")) { 
					//if tool comes under automation
					logger.info("automation tool");
					automationAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("dtf") || toolName.equalsIgnoreCase("pdf") || toolName.equalsIgnoreCase("pdf_diff")) {
					System.out.println("dtf job :D");
					//if tool comes under data testing
					dataTestingAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("jmeter")) {
					//if tool comes under performance
					performanceAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("owasp_zap")) {
					//if tool comes under security
					securityAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("ibm_app_scan")){
					securityAlm(buildHistory,subJobName,status);
				}
				obj.put("almNotification", "success");
			}
		}
		return Response.status(Response.Status.OK).entity(result).build();
	}
	
	void buildAlm(JSONObject buildHistory,String subJobName,String status){
		
	}
	
	void automationAlm(JSONObject buildHistory,String subJobName,String status) {
		JSONObject automationTesting=(JSONObject)buildHistory.get("AutomationTesting");
		JSONArray tools=(JSONArray)automationTesting.get("Tools");
		System.out.println("automation testing");
		logger.debug("subJobName "+subJobName);
		for(int i=0;i<tools.size();i++) {
			logger.debug("i "+i);
			JSONObject temp=(JSONObject)tools.get(i);
			String tempJobName=(String)temp.get("tool_name");
			logger.debug("tempJobName "+tempJobName);
			if(tempJobName.equals(subJobName)) {
				logger.debug("matched ");
				Integer testSetID=1;
				try {
					testSetID=Integer.parseInt(GlobalConstants.QC_TEST_SET_ID);
					String[] subJobNameArr=subJobName.split("_");
					if(subJobNameArr.length>=2) {
						testSetID=Integer.parseInt(subJobNameArr[subJobNameArr.length-1]);
					}
					logger.debug("testSetID is "+testSetID);
					JSONArray toolReport=(JSONArray)temp.get("ToolReport");
					logger.debug("toolReport "+toolReport);
					for(int j=0;j<toolReport.size();j++) {
						JSONObject toolReportObj=(JSONObject)toolReport.get(j);
						String chartName=(String)toolReportObj.get("chart_name");
//						String chartNameLower=chartName.toLowerCase();
//						String subJobNameLower=subJobName.toLowerCase();
//						if(chartNameLower.contains("event")&&subJobNameLower.contains("ais")) {
//							testSetID=Integer.parseInt(GlobalConstants.QC_TS_ID_AIS_EVENT);
//						}else if(chartNameLower.contains("pet")&&subJobNameLower.contains("ais")) {
//							testSetID=Integer.parseInt(GlobalConstants.QC_TS_ID_AIS_PET);
//						}else if(chartNameLower.contains("wedding")&&subJobNameLower.contains("ais")) {
//							testSetID=Integer.parseInt(GlobalConstants.QC_TS_ID_AIS_WED);
//						}else if(chartNameLower.contains("event")&&subJobNameLower.contains("web")) {
//							testSetID=Integer.parseInt(GlobalConstants.QC_TS_ID_DW_EVENT);
//						}else if(chartNameLower.contains("pet")&&subJobNameLower.contains("web")) {
//							testSetID=Integer.parseInt(GlobalConstants.QC_TS_ID_DW_PET);
//						}else if(chartNameLower.contains("wedding")&&subJobNameLower.contains("web")) {
//							testSetID=Integer.parseInt(GlobalConstants.QC_TS_ID_DW_WED);
//						}
						System.out.println("testSetID "+testSetID);
						logger.debug("testSetID "+testSetID);
						JSONArray tabularData=(JSONArray)toolReportObj.get("tabular_data");
						logger.debug("tabular_data "+tabularData.size());
						for(int k=0;k<tabularData.size();k++) {
							JSONObject tabularDataObj=(JSONObject)tabularData.get(k);
							JSONArray components=(JSONArray)tabularDataObj.get("components");
							for(int k1=0;k1<components.size();k1++) {
								JSONObject componentsObject=(JSONObject)components.get(k1);
								String componentId=(String)componentsObject.get("componentid");
								String overAllStatus=(String)componentsObject.get("overallStatus");
								System.out.println("TCO:: "+testSetID+" "+componentId+" "+overAllStatus);
								logger.debug("TCO:: "+testSetID+" "+componentId+" "+overAllStatus);
								updateRunStatus(testSetID,componentId,overAllStatus);
							}
						}
					}
//					updateRunStatus(testSetID,subJobName,status);
				}catch(Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}		
	}
	
	void  dataTestingAlm(JSONObject buildHistory,String subJobName,String status) {
		
	}
	void performanceAlm(JSONObject buildHistory,String subJobName,String status) {
		
	}
	void securityAlm(JSONObject buildHistory,String subJobName,String status) {

	}
	private String initializeQcConnection()
	{	logger.debug("initializeQCConnection");
		String url=GlobalConstants.QC_URL;
		String userName=GlobalConstants.QC_USERNAME;
		String password=GlobalConstants.QC_PASSWORD;
		String domain=GlobalConstants.QC_DOMAIN;
		String project=GlobalConstants.QC_PROJECT;
		QCConnection qc=new QCConnection(url,userName, password, domain, project);
		
		try {
				qc.connect();
				connection=qc.getQCConnection();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return "";
	}
	
	 	
	
//		
//	public Integer updateRunStatus(Integer testCaseID, Integer testSetID, Integer testDataID, String testStatus) throws Exception{
//		ITestSetFactory testSetFactory = (connection.testSetFactory()).queryInterface(ITestSetFactory.class);
//		
//		//Search for TestSet if present
//		ITestSet testSet = (testSetFactory.item(testSetID)).queryInterface(ITestSet.class);
//		
//		if(testSet != null){
//			IBaseFactory baseFactory = testSet.tsTestFactory().queryInterface(IBaseFactory.class);
//			IList instances = baseFactory.newList(AlmConstants.BLANK); //No filters for instances
//			ITSTest test = null;
//			
//			//Check all existing instances for testcaseid and iteration
//			for(Com4jObject testInstance : instances){
//				ITSTest currentTest = testInstance.queryInterface(ITSTest.class);
//				Integer currentIteration = Integer.parseInt((String)currentTest.field("TC_ITERATIONS"));
//				if(testCaseID == Integer.parseInt(currentTest.testId().toString())
//						&& testDataID == currentIteration){
//					test = currentTest;
//					break;
//				}
//			}
//			
//			//Create a new instance if it doesnt exist
//			if(test == null){
//				ITestFactory testFactory = (connection.testFactory()).queryInterface(ITestFactory.class);		
//				ITest iTest = (testFactory.item(testCaseID)).queryInterface(ITest.class);		
//				test = (baseFactory.addItem(iTest)).queryInterface(ITSTest.class);			
//				test.field("TC_ITERATIONS", testDataID);
//			}
//			
//			//Add run to the instance
//			IRunFactory runfactory = (test.runFactory()).queryInterface(IRunFactory.class);
//			IRun run = runfactory.addItem(AlmConstants.RUN_DEFAULT_NAME).queryInterface(IRun.class);
//			run.status(testStatus);				
//			run.post();
//			test.post();
//			test.refresh();	
//			System.out.println("Execution status updated: TestSetID/Run ID/Iteration/Status :: " + 
//					testSetID + AlmConstants.BACK_SLASH_SEPARATOR +
//					run.id() + AlmConstants.BACK_SLASH_SEPARATOR + 
//					testDataID + AlmConstants.BACK_SLASH_SEPARATOR +
//					run.status());
//			logger.info("Execution status updated: TestSetID/Run ID/Iteration/Status :: " + 
//					testSetID + AlmConstants.BACK_SLASH_SEPARATOR +
//					run.id() + AlmConstants.BACK_SLASH_SEPARATOR + 
//					testDataID + AlmConstants.BACK_SLASH_SEPARATOR +
//					run.status());
//			
//			return (Integer)run.id();
//		}
//		else{
//			throw new Exception("Test set id: " + testSetID + " not present in QC.");
//		}
//		
//	}
	@Path("updateFinalQC")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateQCFinal(JSONArray liveBuildConsoleArray) {
		initializeQcConnection();
		Integer testSetID=1;
		for(int i=0;i<liveBuildConsoleArray.size();i++) {
			JSONObject liveBuildConsole=(JSONObject)liveBuildConsoleArray.get(i);
			String subJobName=(String)liveBuildConsole.get("tool_name");
			String status=(String)liveBuildConsole.get("tool_status");
			try {
				testSetID=Integer.parseInt(GlobalConstants.QC_TEST_SET_ID);
				updateRunStatus(testSetID,subJobName,status);
			}catch(Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
			}			
		}

		return Response.status(Response.Status.OK).entity("ALM updated").build();
	}
	
	private  Integer updateRunStatus( Integer testSetID, String testcaseName, String testStatus) throws Exception{
		logger.debug("updateRunStatus");
		//testCaseID=161;
//		testSetID=1;
		//testDataID=27950;
//		String testcaseName="[1]27951";
		testcaseName=testcaseName.trim();
		testcaseName="[1]"+testcaseName;
		testStatus=testStatus.trim();
		if(testStatus.equalsIgnoreCase("pass")) {
			testStatus="Passed";
		}else if(testStatus.equalsIgnoreCase("fail")) {
			testStatus="Failed";
		}
		logger.debug("recieved parameters after suitable modifications:: testSetID: "+testSetID+" testcaseName: "+testcaseName+" testStatus: "+testStatus);
		ITestSetFactory testSetFactory = (connection.testSetFactory()).queryInterface(ITestSetFactory.class);
		
		//Search for TestSet if present
		ITestSet testSet = (testSetFactory.item(testSetID)).queryInterface(ITestSet.class);
		logger.debug("testSet "+testSet);
		logger.debug("testSet.name() "+testSet.name());
		
		if(testSet != null){
			IBaseFactory baseFactory = testSet.tsTestFactory().queryInterface(IBaseFactory.class);
			IList instances = baseFactory.newList(""); //No filters for instances
			ITSTest test = null;
			logger.debug("instances.count() "+instances.count());
			System.out.println(instances.count());
			//Check all existing instances for testcaseid and iteration
			for(Com4jObject testInstance : instances)
			{
				ITSTest currentTest = testInstance.queryInterface(ITSTest.class);
				logger.debug("currentTest"+currentTest);
				logger.debug("currentTest.name().trim() "+currentTest.name().trim());
				logger.debug("currentTest.status() "+currentTest.status());
				logger.debug("currentTest.testName()"+currentTest.testName());
				logger.debug("currentTest.type() "+currentTest.type());
				logger.debug("testcaseName "+testcaseName);
//				
//				Integer currentIteration = Integer.parseInt((String)currentTest.field("TC_ITERATIONS"));
//				System.out.println(currentTest.name()+"::"+Integer.parseInt(currentTest.testId().toString()));
//				logger.debug(currentTest.name()+"::"+Integer.parseInt(currentTest.testId().toString()));
				/*if(testCaseID == Integer.parseInt(currentTest.testId().toString())&& testDataID == currentIteration){
					test = currentTest;
					break;
				}*/
				
				if(currentTest.name().trim().equals(testcaseName))
				{
					test = currentTest;
					break;	
				}
			}
			
			//Create a new instance if it doesnt exist
			/*if(test == null){
				ITestFactory testFactory = (connection.testFactory()).queryInterface(ITestFactory.class);		
				ITest iTest = (testFactory.item(testCaseID)).queryInterface(ITest.class);		
				test = (baseFactory.addItem(iTest)).queryInterface(ITSTest.class);			
				test.field("TC_ITERATIONS", testDataID);
			}*/
			
			//Add run to the instance
			IRunFactory runfactory = (test.runFactory()).queryInterface(IRunFactory.class);
			IRun run = runfactory.addItem("Plato Automation").queryInterface(IRun.class);
			run.status(testStatus);				
			run.post();
			test.post();
			test.refresh();	
			
			
			return (Integer)run.id();
		}
		else{
			throw new Exception("Test set id: " + testSetID + " not present in QC.");
		}
		
	}
}
