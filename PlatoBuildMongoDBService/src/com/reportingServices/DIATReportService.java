package com.reportingServices;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.lntinfotech.tcoe.excelImport.ExcelToJson;
import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class DIATReportService {
	
	private static final Logger logger=Logger.getLogger(DIATReportService.class);


	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private boolean status=true;
	private String jenkinsUrl;
	private String reportPath;
	//MongoDBOperations mongoDBOperations=new  MongoDBOperations();
	
	String jenkinsConsoleOutput="";
	
	public DIATReportService(int buildHistoryId, int buildNumber, String jobName,String jenkinsUrl,String reportPath) {
		// TODO Auto-generated constructor stub
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
		this.jenkinsUrl=jenkinsUrl;
		this.reportPath=reportPath;
	}
	
	public String readDIATJenkinsTotalResponse() throws ParseException, InterruptedException {
		
		ReadJenkinsConsole readJenkinsConsole=new ReadJenkinsConsole();

		//int count =0;s
		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		logger.info("Reading DIAT Execution from Jenkins Console ");
		for(;;){
			//count++;
			Thread.sleep(10000);
			jenkinsConsoleOutput=readJenkinsConsole.readJenkinsConsole(buildNumber,jobName,jenkinsUrl);
				
			
			System.out.println(jenkinsConsoleOutput);
			//if(count==5){
			if(jenkinsConsoleOutput.contains("Finished: SUCCESS")||jenkinsConsoleOutput.contains("Finished: FAILURE") || jenkinsConsoleOutput.contains("Finished: ABORTED") || jenkinsConsoleOutput.contains("Finished: UNSTABLE") || jenkinsConsoleOutput.contains("FATAL: null")){
				System.out.println("jenkins console output contains******** DIAT Execution Completed ");
				finalPlatoJsonObj=readDIATJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
				break;
			}/*else if() {
				status=false;
				break;
			}*/
			
			//return finalPlatoJsonObj;
		}
		
		logger.info("COMPLETED reading DIAT Execution from Jenkins Console ");
		//return finalPlatoJsonObj;
		
		if(status==false){
			response="Failed";
		}else{
			response="Passed";
		}
		
		return response;
		//return jenkinsConsoleOutput;
	
	}
	
	public JSONObject readDIATJenkinsConsoleAndUpdateStatus(String jenkinsConsoleOutput)  {
		String consoleOutput=jenkinsConsoleOutput;
		if(!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))){
			//String jenkinsOutput[]=jenkinsConsoleOutput.split(this.jenkinsConsoleOutput);
			if(jenkinsConsoleOutput.contains(this.jenkinsConsoleOutput.trim())){
				logger.info("Removing previous console");
				jenkinsConsoleOutput=jenkinsConsoleOutput.replace(this.jenkinsConsoleOutput.trim(), "");
			}
			/*jenkinsConsoleOutput=jenkinsOutput[1];
		System.out.println("jenkins output 0 ***"+jenkinsOutput[0]);
		System.out.println("jenkins output 1 ***"+jenkinsOutput[1]);*/
		}else{
			logger.info("this.jenkinsConsoleOutput is blank");
		}
		
		JSONObject jsonDiatReport=new JSONObject();
		JSONObject finalPlatoJsonObj=new JSONObject();
		ExcelToJson excelToJson=new ExcelToJson();
		String reportString=excelToJson.getImpactData(reportPath);
		System.out.println("reportString is "+reportString);
		JSONParser parser = new JSONParser();
		
		//JSONObject finalPlatoJsonObj = (JSONObject) parser.parse(jenkinsConsoleOutput);
		
		try {
			//finalPlatoJsonObj = (JSONObject) parser.parse(mongoDBOperations.fetchJsonFromMongoDB(buildHistoryId));
			
			//------------------------------changed for mongo read count
			finalPlatoJsonObj=null;
			String finalPlatoJsonString=null;
			while(finalPlatoJsonString==null)
			{
				logger.debug("Inside while loop. Fetching template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				finalPlatoJsonString=MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
				
			
			}
			logger.debug("Retrived data : " +finalPlatoJsonString);
			finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
			//------------------------------------------end
			
			jsonDiatReport = (JSONObject) parser.parse(reportString);
			System.out.println("jsonDiatReport is "+jsonDiatReport);
		}catch(Exception e) {
			e.printStackTrace();
		}
		JSONObject obj1= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
	//	JSONObject obj2= (JSONObject) obj1.get("LiveBuildConsole");
		/*String diatStatus=(String)obj2.get("status");
		if(diatStatus==null) {
			diatStatus=" ";
		}
		if(diatStatus.equalsIgnoreCase("Finished: SUCCESS")){
			
			obj2.replace("Build", diatStatus, "Success");
		}else{
			
			if(diatStatus.equalsIgnoreCase("Finished: FAILURE")){
				obj2.replace("Build", diatStatus, "Failed");		
			}
			
			if(status){
				status=false;
				}	
			
		}*/
		this.jenkinsConsoleOutput=consoleOutput;
		
		obj1.put("Diat",jsonDiatReport);
		//obj2.put("Diat",jsonDiatReport);
		System.out.println("obj1 is "+obj1);
		
		logger.info("Updating the MongoDB template");
		logger.debug(finalPlatoJsonObj);
		logger.debug("Updating template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
		MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
		logger.debug("Updated Status of DIAT in MongoDB for build history id :"+buildHistoryId+ " and job name :  "+jobName);

		
		return finalPlatoJsonObj;
		}
	
}
