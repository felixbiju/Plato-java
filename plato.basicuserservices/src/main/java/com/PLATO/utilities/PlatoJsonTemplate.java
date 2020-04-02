package com.PLATO.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.constants.GlobalConstants;
import com.PLATO.entities.ModuleBuildHistory;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.SubjobCheckpoint;
import com.PLATO.entities.SubjobCheckpointDetails;
import com.PLATO.entities.TEMApplicationDetail;
import com.PLATO.entities.TEMDatabaseDetail;
import com.PLATO.entities.TEMServerDetail;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public class PlatoJsonTemplate {


	//private static String platoJson;
	static int subjobId=0; 

	private int buildHistoryId;

	//private int buildHistoryId;
	private String jobName;
	private int buildNumber;

	public PlatoJsonTemplate(int buildNumber,String jobName, int buildHistoryId){
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
		
	}

	//private static String jenkinsConsoleOutputForParsing="";
	//public static String createMongoTemplateFunction(int buildHistoryId) throws Exception{

	@SuppressWarnings("unchecked")
	public String createMongoTemplateFunction() throws Exception{

		JSONObject platoJsonObj = new JSONObject();

		String finalPlatoJson = null;

		HashMap<String,Object> keyvalueMap = new HashMap<String,Object>();

		keyvalueMap.put("buildHistoryId", buildHistoryId);
		ModuleBuildHistory mbh=(ModuleBuildHistory) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_MODULE_HISTORY_DATA, keyvalueMap);

		//fetching project id 
		int projectId=mbh.getModuleJobsJenkins().getProjectMaster().getProject_id();
		
		//int projectId=6;
		HashMap<String,Object> keyvalueMapForDB = new HashMap<String,Object>();
		keyvalueMapForDB.put("projectId", projectId);


		//retrieving tem environment data
		List<Object>dbDetail=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_DATABASES_BY_PROJECTID, keyvalueMapForDB);

		List<Object>serverDetail=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_SERVERS_BY_PROJECTID, keyvalueMapForDB);

		List<Object>appDetail=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_APPLICATIONS_BY_PROJECTID, keyvalueMapForDB);



		//creating json array for database,server,applications containing json objects as per json fromat
		JSONArray dataBaseList = new JSONArray();
		JSONArray serverList = new JSONArray();
		JSONArray appList = new JSONArray();


		//creating database json Array
		for(int i=0;i<dbDetail.size();i++){

			TEMDatabaseDetail temDbDetail=(TEMDatabaseDetail) dbDetail.get(i);

			JSONObject dbJsonObj=new JSONObject();
			dbJsonObj.put("name", temDbDetail.getDatabaseName());
			dbJsonObj.put("url", temDbDetail.getDatabaseURL());
			dbJsonObj.put("status", getDatabaseStatus(temDbDetail));

			dataBaseList.add(dbJsonObj);
		}


		//creating Servers json Array
		for(int i=0;i<serverDetail.size();i++){

			TEMServerDetail temServerDetail=(TEMServerDetail) serverDetail.get(i);

			JSONObject serverJsonObj=new JSONObject();
			serverJsonObj.put("name", temServerDetail.getServerName());
			serverJsonObj.put("ip", temServerDetail.getServerURL());
//			serverJsonObj.put("status", temServerDetail.getMonitoringStatus());

			//get server status when starting execution
			serverJsonObj.put("status", getServerStatus(temServerDetail.getServerURL()));
			serverList.add(serverJsonObj);
		}



		//creating Applications json Array
		for(int i=0;i<appDetail.size();i++){

			TEMApplicationDetail temAppDetail=(TEMApplicationDetail) appDetail.get(i);

			JSONObject appJsonObj=new JSONObject();
			appJsonObj.put("name", temAppDetail.getApplicationName());
			appJsonObj.put("url", temAppDetail.getApplicationURL());
//			appJsonObj.put("status", temAppDetail.getMonitoringStatus());
			
			//fetch status when this execution starts
			
			appJsonObj.put("status", getApplicationStatus(temAppDetail.getApplicationURL()));
			appList.add(appJsonObj);
		}


		
		//creating environment object
		LinkedHashMap<String, JSONArray> preBuildCheckObj = new LinkedHashMap<String, JSONArray>();		
		preBuildCheckObj.put("Applications", appList);
		preBuildCheckObj.put("Servers", serverList);
		preBuildCheckObj.put("Databases", dataBaseList);


		JSONObject toolsLBC=new JSONObject();
		JSONArray toolsObj=new JSONArray();
		
		JSONArray toolsDetailArray=new JSONArray();
		
		JSONObject automationTestingObj=new JSONObject();
		JSONObject dataTestingObj=new JSONObject();
		JSONObject performanceTestingObj=new JSONObject();
		JSONObject securityTestingObj=new JSONObject();
		JSONObject notificationTestingObj=new JSONObject();
		
		
	
		
		JSONArray toolsObj1=new JSONArray();
		JSONArray toolsObj2=new JSONArray();
		JSONArray toolsObj3=new JSONArray();
		JSONArray toolsObj4=new JSONArray();
		JSONArray toolsObj5=new JSONArray();
		JSONArray toolsObj6=new JSONArray();
		

		List<Object>jeniknsJobId=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_JENKINS_JOB_ID, keyvalueMap);
		
		
		
		for(int l=0;l<jeniknsJobId.size();l++){

			Integer jobId=(Integer) jeniknsJobId.get(l);

			HashMap<String,Object> keyvalueMapJenkinsJobId = new HashMap<String,Object>();
			keyvalueMapJenkinsJobId.clear();
			keyvalueMapJenkinsJobId.put("jenkinsJobId", jobId);
			
			ModuleJobsJenkins mjj=(ModuleJobsJenkins)GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.JENKINS_NAME, keyvalueMapJenkinsJobId);
			
			//String jobName=(String) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_JENKINS_JOB_NAME, keyvalueMapJenkinsJobId);
			
			//commented
			//List<Object>jeniknsSubJobId=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_JENKINS_SUB_JOB_ID, keyvalueMapJenkinsJobId);
			//added			
			List<Object>jeniknsSubJobList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_JENKINS_SUB_JOBS, keyvalueMapJenkinsJobId);
		
			LinkedList<Object> jeniknsSubJobs=new LinkedList<>();
			
			//to add main build job
			for(int m=0;m<jeniknsSubJobList.size();m++){
				ModuleSubJobsJenkins msbj=(ModuleSubJobsJenkins) jeniknsSubJobList.get(m);
				if(msbj.getTool_name().equalsIgnoreCase("build"))
				{
					if(msbj.getSubjob_name().equals(mjj.getJenkins_job_name())){
						jeniknsSubJobs.add(msbj);
					}
				}
			}
			
			//to add other build jobs
			for(int m=0;m<jeniknsSubJobList.size();m++){
				ModuleSubJobsJenkins msbj=(ModuleSubJobsJenkins) jeniknsSubJobList.get(m);
				if(msbj.getTool_name().equalsIgnoreCase("build"))
				{
					if(msbj.getSubjob_name().equals(mjj.getJenkins_job_name())){
						System.out.println("Already added");
					}else{
						jeniknsSubJobs.add(msbj);
					}
				}
			}
			//to add remaining jobs
			for(int m=0;m<jeniknsSubJobList.size();m++){
				ModuleSubJobsJenkins msbj=(ModuleSubJobsJenkins) jeniknsSubJobList.get(m);
				if(msbj.getTool_name().equalsIgnoreCase("build")){
					System.out.println("Already added");
				}else{
				jeniknsSubJobs.add(msbj);
				}
			}
		
			
			for(int m=0;m<jeniknsSubJobs.size();m++){

				ModuleSubJobsJenkins msbj=(ModuleSubJobsJenkins) jeniknsSubJobs.get(m);
				
				String tool_name=msbj.getTool_name();
				
				keyvalueMap.clear();
				keyvalueMap.put("tool_name",msbj.getTool_name());
				int category_id= (int) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(ConstantQueries.GET_TOOL_CATEGORY, keyvalueMap);
				
				if(category_id==1){
					JSONObject toolObject = new JSONObject();

					
					
					
					toolObject.put("tool_name", jobName);
					toolObject.put("tool_status", "");
					toolObject.put("build_number", "");
					toolObject.put("isLadybugChecked", msbj.isIs_ladyBug_checked());
					toolObject.put("isAlmChecked", msbj.isIs_alm_checked());
					toolObject.put("ladyBugNotification", "");
					toolObject.put("almNotification", "");
					toolObject.put("toolName", msbj.getTool_name());
					
					
					
					
					Integer subJobId=(Integer) msbj.getSubjob_id();
					HashMap<String,Object> keyvalueMapSubJobId = new HashMap<String,Object>();
					keyvalueMapSubJobId.put("subJobId", subJobId);
					
					//COMMENTED FOR SONAR STATUS
					/*
					String report_path=(String) msbj.getReport_path();
					
					String[] reportPathArray=report_path.split(",");
					
					if(reportPathArray[1].equalsIgnoreCase("true")) {
						
						
						
						
						
					}
					
					*/
					
					//listOfCheckPointsModules <----contains all the checkpoints of a tool/subjob
					List<Object> listOfCheckPointsModules=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_SUBJOB_CHECKPOINTS, keyvalueMapSubJobId);

					if(!listOfCheckPointsModules.isEmpty()){
					
					JSONArray modulesArray=new JSONArray();
					JSONObject module_object=new JSONObject();
					JSONArray checkpointArray=new JSONArray();
					
					
					//sonar,svn,maven,git,juint
					for(int i=0;i<listOfCheckPointsModules.size();i++){
						
						//subJobCheckPoint<---- each checkpoint
						SubjobCheckpoint subJobCheckPoint=(SubjobCheckpoint) listOfCheckPointsModules.get(i);
						
						//subjobCheckpointDetailsList <---- criteria of particular
						List<SubjobCheckpointDetails> subjobCheckpointDetailsList=new ArrayList<>(subJobCheckPoint.getCheckpoint_criteria());

						if(subjobCheckpointDetailsList!=null){
							Collections.sort(subjobCheckpointDetailsList);
							}	
						
						for(int k=0;k<subjobCheckpointDetailsList.size();k++){
							SubjobCheckpointDetails subjobCheckpointDetails=subjobCheckpointDetailsList.get(k);
							
							JSONObject checkpointsObj=new JSONObject();

							checkpointsObj.put("checkpoint_name",subjobCheckpointDetails.getCheckpoint_name());
							checkpointsObj.put("checkpoint_order", subjobCheckpointDetails.getOrder_number());
							checkpointsObj.put("pass_criteria",subjobCheckpointDetails.getPass_criteria());
							checkpointsObj.put("fail_criteria",subjobCheckpointDetails.getFail_criteria());
							checkpointsObj.put("status","");						
							
							
							checkpointArray.add(checkpointsObj);
						}						
					
					
						module_object.put("module_name", "Build");//incase job name is needed then fetch from db with job id
						module_object.put("checkpoints", checkpointArray);
						modulesArray.add(module_object);
						
					}
					
				
					toolObject.put("modules", modulesArray);
					
					}//checking if there are checkpoints
				//if loop of BUILD
				
					
					toolsDetailArray.add(toolObject);
				
					JSONObject buildObject = new JSONObject();
					platoJsonObj.put("Build", buildObject);

				}else {
					
					
					
					JSONObject toolObject = new JSONObject();

					Integer subJobId=(Integer) msbj.getSubjob_id();
					HashMap<String,Object> keyvalueMapSubJobId = new HashMap<String,Object>();
					keyvalueMapSubJobId.put("subJobId", subJobId);
					
					
					JSONObject particularToolObject=new JSONObject();
					
					JSONArray toolReportArray=new JSONArray();
					
					Object subjobNam = null;

					subjobNam=GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_SUBJOB_NAME, keyvalueMapSubJobId);
					String subjobName=subjobNam.toString();
					particularToolObject.put("tool_name", subjobName);
					particularToolObject.put("ToolReport", toolReportArray);
					
					if(tool_name.equalsIgnoreCase("diat")){
						
						JSONObject diatDetailsObj=new JSONObject();
						platoJsonObj.put("Diat", diatDetailsObj);
						
					}else{
					//this for live build
					toolObject.put("tool_name", subjobName);
					toolObject.put("tool_status", "");
					toolObject.put("build_number", "");
					toolObject.put("isLadybugChecked", msbj.isIs_ladyBug_checked());
					toolObject.put("isAlmChecked", msbj.isIs_alm_checked());
					toolObject.put("ladyBugNotification", "");
					toolObject.put("almNotification", "");
					toolObject.put("toolName", msbj.getTool_name());
				
					//listOfCheckPointsModules <----contains all the checkpoints of a tool/subjob
					List<Object> listOfCheckPointsModules=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_SUBJOB_CHECKPOINTS, keyvalueMapSubJobId);

					if(!listOfCheckPointsModules.isEmpty()){
					JSONArray modulesArray=new JSONArray();
	
					//returns list of modules
					for(int i=0;i<listOfCheckPointsModules.size();i++){
						JSONArray checkpointArray=new JSONArray();
						JSONObject module_object=new JSONObject();
						
						//subJobCheckPoint<---- each checkpoint
						SubjobCheckpoint subJobCheckPoint=(SubjobCheckpoint) listOfCheckPointsModules.get(i);
						
						//subjobCheckpointDetailsList <---- criteria of particular
						//List<SubjobCheckpointDetails> subjobCheckpointDetailsList=new ArrayList<>(subJobCheckPoint.getCheckpoint_criteria());
						
						List<SubjobCheckpointDetails> subjobCheckpointDetailsList = (List<SubjobCheckpointDetails>) GenericDaoSingleton.getGenericDao().fetchResultFromDB(SubjobCheckpointDetails.class, "subjob_checkpoint.subjob_checkpoint_id", subJobCheckPoint.getSubjob_checkpoint_id(), null);


						if(subjobCheckpointDetailsList!=null){
							Collections.sort(subjobCheckpointDetailsList);
							
						
						for(int k=0;k<subjobCheckpointDetailsList.size();k++){
							SubjobCheckpointDetails subjobCheckpointDetails=subjobCheckpointDetailsList.get(k);
							
							JSONObject checkpointsObj=new JSONObject();
							
							checkpointsObj.put("checkpoint_name",subjobCheckpointDetails.getCheckpoint_name());
							checkpointsObj.put("checkpoint_order", subjobCheckpointDetails.getOrder_number());
							checkpointsObj.put("status","");
							checkpointsObj.put("pass_criteria",subjobCheckpointDetails.getPass_criteria());
							checkpointsObj.put("fail_criteria",subjobCheckpointDetails.getFail_criteria());
							checkpointArray.add(checkpointsObj);
							
						}						
						}//check that checkpoint criteria is not null 	
						module_object.put("module_order", subJobCheckPoint.getOrder_number());
						module_object.put("module_name", subJobCheckPoint.getModule_name() );//incase job name is needed then fetch from db with job id
						module_object.put("checkpoints", checkpointArray);
						modulesArray.add(module_object);
					
						
					}//close of checkpointModules
									
					toolObject.put("modules", modulesArray);
				
					}//checking if there are checkpoints
				
					toolsDetailArray.add(toolObject);
				
					
			
					
					
					if(category_id==2){				
					
						if(tool_name.equalsIgnoreCase("webservice")) {
							particularToolObject.put("dynamic", "true");
						}else {
						particularToolObject.put("dynamic", "false");
						}
						
						toolsObj2.add(particularToolObject);
						automationTestingObj.put("Tools", toolsObj2);
						platoJsonObj.put("AutomationTesting", automationTestingObj);
					}else if(category_id==3){
						
						if(tool_name.equalsIgnoreCase("idiscover")) {
							toolsObj3.add(particularToolObject);
							dataTestingObj.put("Tools", toolsObj3);
							platoJsonObj.put("DataTesting", dataTestingObj);	
							
							
							
							
							
						}else {
						toolsObj3.add(particularToolObject);
						dataTestingObj.put("Tools", toolsObj3);
						platoJsonObj.put("DataTesting", dataTestingObj);	
						}
					}else if(category_id==4){
						toolsObj4.add(particularToolObject);
						performanceTestingObj.put("Tools", toolsObj4);
						platoJsonObj.put("PerformanceTesting", performanceTestingObj);
					}else if(category_id==5){
						toolsObj5.add(particularToolObject);
						securityTestingObj.put("Tools", toolsObj5);
						platoJsonObj.put("SecurityTesting", securityTestingObj);
					}else if(category_id==6){
						toolsObj6.add(particularToolObject);
						notificationTestingObj.put("Tools", toolsObj6);
						platoJsonObj.put("NotificationTesting", notificationTestingObj);
					}
					
				}
				}
				
				
			}//for-loop of all sub jobs in main job
			
		}//for loop of jobs
		
		
		toolsLBC.put("tools", toolsDetailArray);

		platoJsonObj.put("build_history_id", String.valueOf(buildHistoryId));
		platoJsonObj.put("timestamp",mbh.getTimestamp().toString());
		platoJsonObj.put("PreBuildCheck", preBuildCheckObj);
		platoJsonObj.put("LiveBuildConsole", toolsLBC);
		
		
		
		
		JSONObject platoFinalJsonObj = new JSONObject();
		platoFinalJsonObj.put("BuildHistory", platoJsonObj);
		platoFinalJsonObj.put("readCount",String.valueOf(0));
		
		
		try{
			finalPlatoJson=platoFinalJsonObj.toString();
			System.out.println(finalPlatoJson);
		}catch(Exception e){
			e.printStackTrace();

		}
		
		UpdateJsonInMongoDB(platoFinalJsonObj);		
		
		return finalPlatoJson;

	}

	

	//gaurav will call this function
	@SuppressWarnings("unused")
	private void readJenkinsTotalResponse(PlatoJsonTemplate platoJsonTemplate) throws ParseException, InterruptedException {
		//int count =0;
		String jenkinsConsoleOutput;
		for(;;){
			//count++;
			Thread.sleep(10000);
			jenkinsConsoleOutput=readJenkinsConsole(platoJsonTemplate);

			readJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);

			System.out.println(jenkinsConsoleOutput);
			//if(count==5){
			if(jenkinsConsoleOutput.contains("FAST Execution Completed")){
				System.out.println("jenkins console output contains******** Fast Execution Completed ");
				break;
			}
		}

		//return jenkinsConsoleOutput;
	}

	private void readJenkinsConsoleAndUpdateStatus(String jenkinsConsoleOutput) throws ParseException {
		
		String consoleOutput[];

		//String jenkinsConsoleOutput=readJenkinsConsole();

		//to avoid traversing whole console output again and again
		/*if(jenkinsConsoleOutputForParsing.equalsIgnoreCase("")){
		jenkinsConsoleOutputForParsing=jenkinsConsoleOutput;
	}else{
		consoleOutput=jenkinsConsoleOutput.split(jenkinsConsoleOutputForParsing);
		jenkinsConsoleOutputForParsing=jenkinsConsoleOutput;
		jenkinsConsoleOutput=consoleOutput[1];
	}*/



		JSONParser parser = new JSONParser();
		JSONObject finalPlatoJsonObj = (JSONObject) parser.parse(fetchJsonFromMongoDB());

		//JSONObject finalPlatoJsonObj = (JSONObject) parser.parse(jenkinsConsoleOutput);

		JSONObject obj1= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2= (JSONObject) obj1.get("livebuildconsole");

		JSONObject obj3= (JSONObject) obj2.get("application_under_test");

		JSONArray aa1= (JSONArray) obj3.get("tools");

		int len=aa1.size();


		for(int i=0;i<len;i++){
			JSONObject obj4= (JSONObject)aa1.get(i);

			String nn=(String) obj4.get("tool_name");
			JSONArray aa2= (JSONArray) obj4.get("module_kdt");
			for(int j=0;j<aa2.size();j++){
				JSONObject obj5= (JSONObject)aa2.get(j);
				JSONArray aa3= (JSONArray) obj5.get("checkpoints");
				for(int k=0;k<aa3.size();k++){
					JSONObject obj6= (JSONObject)aa3.get(k);
					String checkpoint_name=(String) obj6.get("checkpoint_name");

					if(jenkinsConsoleOutput.contains(checkpoint_name +" Test Case Passed")){
						obj6.replace("status", "", "pass");
					}else if(jenkinsConsoleOutput.contains(checkpoint_name +" Test Case Failed")){
						obj6.replace("status", "", "fail");
					}else{
						//break;
					}


				}
			}
		}


		//
		//	update the json in mongoDb

		UpdateJsonInMongoDB(finalPlatoJsonObj);
	}





	private void UpdateJsonInMongoDB(JSONObject finalPlatoJsonObj) {
		

		boolean flag=false;

		String finalPlatoJson=finalPlatoJsonObj.toJSONString();

		MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
		
		try{
			MongoDatabase database = mongoClient.getDatabase(GlobalConstants.MONGODB_DATABASE_NAME);
		
		MongoCollection collection = database.getCollection(GlobalConstants.MONGODB_COLLECTION);
		Document doc = Document.parse(finalPlatoJson);

		/*Document query;
		String platoJson="";*/

		FindIterable<Document> iterDoc = collection.find(); 

		Iterator<Document> it = iterDoc.iterator(); 

		/*	if(it.hasNext()==false){
			query=(Document) it.next();
			platoJson=query.toString();
			if(platoJson.contains("build_history_id="+buildHistoryId)){
				platoJson=query.toJson();
				break;

			collection.insertOne(doc); 
		}else{*/
		Document query = null;
		String platoJson;
		while (it.hasNext()) {  
			query=(Document) it.next();
			platoJson=query.toString();
			if(platoJson.contains("build_history_id="+String.valueOf(buildHistoryId))){
				//Bson filter = new Document("build_history_id", buildHistoryId);
				//collection.replaceOne(filter, doc);
				//collection.deleteOne(Filters.eq("build_history_id", buildHistoryId)); 
				//collection.insertOne(doc); 

				flag=true;
				break;				

			}
		}	

		if(flag){
			BasicDBObject query1 = new BasicDBObject();

			query.append("build_history_id", String.valueOf(buildHistoryId));

			UpdateResult result = collection.replaceOne(query1, doc,

					(new UpdateOptions()).upsert(true));

		}else{
			collection.insertOne(doc); 
		}





		//Bson filter = new Document("build_history_id", buildHistoryId);
		//Bson updateOperationDocument = new Document("$set", doc);
		//	collection.updateOne(Filters.eq("build_history_id", buildHistoryId), updateOperationDocument);
		//collection.updateOne(Filters.eq("build_history_id", buildHistoryId), updateOperationDocument);
		//}
		//}

		/*    while (it.hasNext()) {  
    	if(it.next()==null){
    		collection.insertOne(doc); 
    	}else{

    		Bson filter = new Document("build_history_id", "3");
    		Bson updateOperationDocument = new Document("$set", doc);
    		collection.updateOne(filter, updateOperationDocument);
    	}

    }*/
		}
		finally {
			mongoClient.close();
		}
		

	}



	private String fetchJsonFromMongoDB() {
		

		//String finalPlatoJson=finalPlatoJsonObj.toJSONString();

		String platoJson="";

		MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
		try {
		MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
		MongoCollection collection = database.getCollection("BuildHistoryDetails");
		//Document doc = Document.parse(finalPlatoJson);

		Document query;

		FindIterable<Document> iterDoc = collection.find(); 

		Iterator<Document> it = iterDoc.iterator();
		while (it.hasNext()) {  
			query=(Document) it.next();
			platoJson=query.toString();
			if(platoJson.contains("build_history_id="+buildHistoryId)){
				platoJson=query.toJson();
				break;

			}
		}
		//query.put("build_history_id", String.valueOf(buildHistoryId));

		/*BasicDBObject getQuery=new BasicDBObject();
	getQuery.put("build_history_id", new BasicDBObject("$eq",buildHistoryId));

	DBCursor cursor=(DBCursor) collection.find(getQuery);

	while(cursor.hasNext()){
		System.out.println(cursor.next());
	}*/

		//Iterator it = iterDoc.iterator(); 

		//if(it.hasNext()==false){
		//	platoJson="";
		//	}else{
		//platoJson=((Document)it.next()).toString();
		//	}

		/*if(iterDoc==null){
	}

		//for(Document dc:iterDoc){

		platoJson=(iterDoc.first()).toString();*/
		//}

		/*    while (it.hasNext()) {  
    	if(it.next()==null){
    		collection.insertOne(doc); 
    	}else{

    		Bson filter = new Document("build_history_id", "3");
    		Bson updateOperationDocument = new Document("$set", doc);
    		collection.updateOne(filter, updateOperationDocument);
    	}

    }*/
		
		}finally {
			mongoClient.close();
			System.out.println("In finally block. Mongo Client connection closed!");

		}
		return platoJson;
	}






	private String readJenkinsConsole(PlatoJsonTemplate platoJsonTemplate) throws ParseException, InterruptedException {
		
		String jenkinsConsoleOutput = null;

		//int buildNumber=1;
		String resp="";

		//String url= "http://172.25.9.140:8080/jenkins";
		//  String jenkinsDir="C:/Users/10621390/.jenkins";
		System.out.println("in getconsoleOutput");
		try {			
			//url=url+"/job/"+platoJsonTemplate.jobName+"/"+platoJsonTemplate.buildNumber+"/consoleText";			


			String url="http://172.25.14.111:8080/jenkins/job/job_1234/"+platoJsonTemplate.buildNumber+"/consoleText";

			resp=readJenkinsResponse(url);

			//if(platoJsonTemplate.buildNumber != 0){
			/*Client client = Client.create();				
			WebResource webResource = client.resource(url+"/job/"+jobName+"/"+buildNumber+"/consoleText");

			ClientResponse response = webResource.get(ClientResponse.class);
			resp = response.getEntity(String.class);
			 */


			//}
		} catch (Exception e) {
			
			e.printStackTrace();
		}	

		jenkinsConsoleOutput=resp;




		//readJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
		Thread.sleep(2000);
		return jenkinsConsoleOutput;

		//return "hi";	
	}





	private String readJenkinsResponse(String url) throws IOException {
		



		URL object=null;

		//JSONObject msgobj=new JSONObject(jsonData);	
		try {
			object = new URL(url);
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("GET");

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

		//used for create account POST request
		//wr.write(jsonData);

		System.out.println("connected to jenkins");

		wr.flush();

		//display what returns the POST request

		StringBuilder sb = new StringBuilder();  
		int HttpResult = con.getResponseCode(); 
		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;  
			while ((line = br.readLine()) != null) {  
				sb.append(line + "\n");  
			}
			br.close();
			System.out.println("Received Response :" + sb.toString());  
		} else {
			System.out.println("Response is :"+con.getResponseMessage()+" : "+con.getRequestMethod()+ ": "+con.getHeaderFields());  
		}  

		return sb.toString();
	}



	public JSONArray traverseLoopAndFormJsonArray(List<Object>typeDetail,String type){

		JSONArray typeList = new JSONArray();

		for(int i=0;i<typeDetail.size();i++){

			if(type.equalsIgnoreCase("database")){

			}

		}
		return typeList;


	}


	private String getApplicationStatus(String applicationURL) {
		String status = "Stopped";
		URL u;
		try {
			u = new URL (applicationURL);
			HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection (); 
			huc.setRequestMethod ("HEAD");
			huc.connect () ; 
			int code = huc.getResponseCode() ;
			if (huc.getResponseCode() == HttpURLConnection.HTTP_OK)
				status = "Running";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	private String getServerStatus(String serverURL) {
		String status = "Stopped";
		try {
			InetAddress geek = InetAddress.getByName(serverURL); 
		    if (geek.isReachable(5000)) 
		    	status = "Running";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	private String getDatabaseStatus(TEMDatabaseDetail temDbDetail) {
		String dbStatus = "stopped";
		Connection conn1 = null;
		
		int databaseUP;
		try {

			Class.forName(temDbDetail.getDatabaseDriver());
			System.out.println("Connecting to database...");
			conn1 = DriverManager.getConnection(temDbDetail.getDatabaseURL(), temDbDetail.getUsername(), temDbDetail.getPassword());
			System.out.println("Connected successfully to database");
			dbStatus = "Running";
		} catch (Exception e) {
			System.out.println("Connection to database failed");
			e.printStackTrace();
			dbStatus = "stopped";
		}

		return dbStatus;
	}
	
	public String myInputStreamReader(InputStream in) throws IOException {

        StringBuilder sb = null;
        try {
            InputStreamReader reader = new InputStreamReader(in);
            sb = new StringBuilder();
            int c = reader.read();
            while (c != -1) {
                sb.append((char) c);
                c = reader.read();
            }
            reader.close();
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return sb.toString();
    }

}
