package com.plato.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.mongo.constants.GlobalConstants;
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
	
	//private static int buildHistoryId=3;
	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private boolean status=true;
	public PlatoJsonTemplate(int buildHistoryId,int buildNumber,String jobName){
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
	}
	
	//private static String jenkinsConsoleOutputForParsing="";
	//public static String createMongoTemplateFunction(int buildHistoryId) throws Exception{

		public JSONObject createMongoTemplateFunction(PlatoJsonTemplate platoJsonTemplate) throws Exception{/*

		JSONObject platoJsonObj = new JSONObject();

		String finalPlatoJson = null;

		HashMap<String,Object> keyvalueMap = new HashMap<String,Object>();

		keyvalueMap.put("buildHistoryId", platoJsonTemplate.buildHistoryId);
		ModuleBuildHistory mbh=(ModuleBuildHistory) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_MODULE_HISTORY_DATA, keyvalueMap);


		//fetching project id 
		//	int projectId=mbh.getModuleJobsJenkins().getProjectMaster().getProject_id();
		int projectId=6;
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
			dbJsonObj.put("status", temDbDetail.getMonitoringStatus());

			dataBaseList.add(dbJsonObj);
		}


		//creating Servers json Array
		for(int i=0;i<serverDetail.size();i++){

			TEMServerDetail temServerDetail=(TEMServerDetail) serverDetail.get(i);

			JSONObject serverJsonObj=new JSONObject();
			serverJsonObj.put("name", temServerDetail.getServerName());
			serverJsonObj.put("ip", temServerDetail.getServerId());
			serverJsonObj.put("status", temServerDetail.getMonitoringStatus());

			serverList.add(serverJsonObj);
		}



		//creating Applications json Array
		for(int i=0;i<appDetail.size();i++){

			TEMApplicationDetail temAppDetail=(TEMApplicationDetail) appDetail.get(i);

			JSONObject appJsonObj=new JSONObject();
			appJsonObj.put("name", temAppDetail.getApplicationName());
			appJsonObj.put("url", temAppDetail.getApplicationURL());
			appJsonObj.put("status", temAppDetail.getMonitoringStatus());

			appList.add(appJsonObj);
		}



		//creating environment object
		JSONObject environmentObj = new JSONObject();
		environmentObj.put("Databases", dataBaseList);
		environmentObj.put("Servers", serverList);
		environmentObj.put("Applications", appList);


		//creating live build console object
		//2.1.creating sonar object
		JSONObject totalDataObject=new JSONObject();
		JSONObject issuesDataObject=new JSONObject();
		JSONObject complexityDataObject=new JSONObject();
		JSONObject duplicationsDataObject=new JSONObject();




		totalDataObject.put("issues", "");
		totalDataObject.put("complexity", "");
		totalDataObject.put("duplications", "");
		totalDataObject.put("technicalDebt", "");


		issuesDataObject.put("blocker", "");
		issuesDataObject.put("critical", "");
		issuesDataObject.put("major", "");
		issuesDataObject.put("minor", "");
		issuesDataObject.put("info", "");


		complexityDataObject.put("file", "");
		complexityDataObject.put("class", "");
		complexityDataObject.put("function", "");


		duplicationsDataObject.put("lines", "");
		duplicationsDataObject.put("blocks", "");
		duplicationsDataObject.put("files", "");



		JSONObject sonarDataObject=new JSONObject();
		sonarDataObject.put("total","" );
		sonarDataObject.put("issues","" );
		sonarDataObject.put("complexity","" );
		sonarDataObject.put("duplications", "");
		sonarDataObject.put("technicalDebt","" );
		sonarDataObject.put("technicaldebt","" );
		sonarDataObject.put("reportlink", "");




		JSONObject sonarObject=new JSONObject();
		sonarObject.put("sonar", sonarDataObject);

		//end creating sonar object	




		//cretaing unit testing object

		JSONObject testStatusDataObject=new JSONObject();
		testStatusDataObject.put("errors", "");
		testStatusDataObject.put("passed", "");
		testStatusDataObject.put("skipped", "");
		testStatusDataObject.put("failures", "");


		JSONObject testDataObject=new JSONObject();
		testDataObject.put("testName", "");
		testDataObject.put("status", testStatusDataObject);

		JSONArray testsDataArray=new JSONArray();
		testsDataArray.add(testDataObject);

		JSONObject testTotalDataObject=new JSONObject();
		testTotalDataObject.put("errors", "");
		testTotalDataObject.put("passed", "");
		testTotalDataObject.put("skipped", "");
		testTotalDataObject.put("failures", "");




		JSONObject junitDataObject=new JSONObject();																																																							
		junitDataObject.put("testTotal", testTotalDataObject);
		junitDataObject.put("tests", testsDataArray);
		junitDataObject.put("reportlink", "");

		JSONObject junitObject=new JSONObject();		
		junitObject.put("jUnit", junitDataObject);

		//unit testing end


		
		
		
		
		
		
		
		


		//creating application_under_test object

		//fetching data for application_under_test

		List<Object>jeniknsJobId=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_JENKINS_JOB_ID, keyvalueMap);

	
		JSONArray checkPointArray =new JSONArray();
		JSONArray checkPointToolsArray =new JSONArray();
		
		for(int l=0;l<jeniknsJobId.size();l++){

			Integer jobId=(Integer) jeniknsJobId.get(l);

			HashMap<String,Object> keyvalueMapJenkinsJobId = new HashMap<String,Object>();
			keyvalueMapJenkinsJobId.put("jenkinsJobId", jobId);

			List<Object>jeniknsSubJobId=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_JENKINS_SUB_JOB_ID, keyvalueMapJenkinsJobId);

			for(int m=0;m<jeniknsSubJobId.size();m++){

				Integer subJobId=(Integer) jeniknsSubJobId.get(m);
				HashMap<String,Object> keyvalueMapSubJobId = new HashMap<String,Object>();
				keyvalueMapSubJobId.put("subJobId", subJobId);

				//problem here
				List<Object>checkPointDetail=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_SUBJOB_CHECKPOINTS, keyvalueMapSubJobId);

				JSONArray checkPointDetailArray =new JSONArray();
				JSONArray checkPointArray =new JSONArray();
				JSONArray checkPointToolsArray =new JSONArray();

				for(int i=0;i<checkPointDetail.size();i++){

					SubjobCheckpoint subJobCheckPoint=(SubjobCheckpoint) checkPointDetail.get(i);

					JSONArray checkPointDetailArray =new JSONArray();
					
					List<SubjobCheckpointDetails> subjobCheckpointDetailsList=new ArrayList<>(subJobCheckPoint.getCheckpoint_criteria());


					for(int k=0;k<subjobCheckpointDetailsList.size();k++){
						SubjobCheckpointDetails subjobCheckpointDetails=subjobCheckpointDetailsList.get(k);
						JSONObject subjobCheckpointDetailsObj=new JSONObject();
						subjobCheckpointDetailsObj.put("testcase_name",subjobCheckpointDetails.getCheckpoint_name());
						subjobCheckpointDetailsObj.put("status","");
					//	subjobCheckpointDetailsObj.put("pass_criteria",subjobCheckpointDetails.getPass_criteria());
					//	subjobCheckpointDetailsObj.put("fail_criteria",subjobCheckpointDetails.getFail_criteria());
						checkPointDetailArray.add(subjobCheckpointDetailsObj);
					}

					JSONObject subjobCheckpointObj=new JSONObject();
					subjobCheckpointObj.put("module_name", subJobCheckPoint.getModule_name());
					subjobCheckpointObj.put("testcases", checkPointDetailArray);
					checkPointArray.add(subjobCheckpointObj);

					JSONObject toolCheckpointObj=new JSONObject();
			Object subjobNam;
			HashMap<String,Object> keyvalueMapsub = new HashMap<String,Object>();
			keyvalueMapsub.put("subjobId", subjobId);
			subjobNam=GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_SUBJOB_NAME, keyvalueMapsub);

			toolCheckpointObj.put("tool_name", subjobNam);


					JSONObject toolCheckpointObj=new JSONObject();
					Object subjobNam = null;

					if((subjobId==subJobCheckPoint.getModuleSubJob().getSubjob_id())){
				subjobId=subJobCheckPoint.getModuleSubJob().getSubjob_id();		

			}else if(subjobId==0){

				subjobId=subJobCheckPoint.getModuleSubJob().getSubjob_id();
				HashMap<String,Object> keyvalueMapsub = new HashMap<String,Object>();
				keyvalueMapsub.put("subjobId", subjobId);
				subjobNam=GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_SUBJOB_NAME, keyvalueMapsub);
			}else{
				subjobId=subJobCheckPoint.getModuleSubJob().getSubjob_id();
				String subjobName=subjobNam.toString();
				toolCheckpointObj.put("tool_name", subjobName);
				toolCheckpointObj.put("module_kdt", checkPointArray);
				checkPointToolsArray.add(toolCheckpointObj);
			}

				}//i loop
				
				JSONObject toolCheckpointObj=new JSONObject();
				Object subjobNam = null;
				
				subjobNam=GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_SUBJOB_NAME, keyvalueMapSubJobId);
				String subjobName=subjobNam.toString();
				toolCheckpointObj.put("tool_name", subjobName);
				toolCheckpointObj.put("module_kdt", checkPointArray);
				checkPointToolsArray.add(toolCheckpointObj);
				
			}//m loop

		}//l loop	


		JSONObject testcaseObj=new JSONObject();
		testcaseObj.put("testcase_name", "");
		testcaseObj.put("status", "");

		JSONArray testCasesDataArray =new JSONArray();
		testCasesDataArray.add(testcaseObj);





		JSONObject moduleObj=new JSONObject();
		moduleObj.put("module_name", "");
		moduleObj.put("testcases", testCasesDataArray);


		JSONArray moduleKdtDataArray =new JSONArray();
		moduleKdtDataArray.add(moduleObj);


		JSONObject toolObj=new JSONObject();
		toolObj.put("tool_name", "");
		toolObj.put("module_kdt", moduleKdtDataArray);


		JSONArray toolsDataArray =new JSONArray();
		toolsDataArray.add(toolObj);


		JSONObject applicationUnderTestDataObject=new JSONObject();
		//applicationUnderTestDataObject.put("tools", toolsDataArray);

		applicationUnderTestDataObject.put("tools", checkPointToolsArray);

		//end creating application_under_test object 


		
		
		
		
		
		
		
		
		
		

		//creating multiplatform object

		JSONObject testCaseObject=new JSONObject();
		testCaseObject.put("testcase_name", "");
		testCaseObject.put("status", "");

		JSONArray testcasesdataArray=new JSONArray();
		testcasesdataArray.add(testCaseObject);


		JSONArray moduleskdtArray=new JSONArray();
		moduleskdtArray.add(testcasesdataArray);

		JSONObject browserDataObject=new JSONObject();
		browserDataObject.put("browser_name", "");
		browserDataObject.put("modules_kdt", testcasesdataArray);


		JSONArray browserArray=new JSONArray();
		browserArray.add(browserArray);

		JSONObject toolsDataObject=new JSONObject();
		toolsDataObject.put("tool_name", "");
		toolsDataObject.put("bowsers", browserArray);

		JSONObject multiPlatformObject=new JSONObject();
		multiPlatformObject.put("tool", toolsDataObject);

		//end creating multiplatform object





		//creating performance object

		JSONObject transactionObject=new JSONObject();
		transactionObject.put("transaction_name", "");
		transactionObject.put("status", "");
		transactionObject.put("response_time", "");
		transactionObject.put("throughput", "");
		transactionObject.put("response_resource_information", "");


		JSONArray transactionArray=new JSONArray();
		transactionArray.add(transactionObject);


		JSONObject toolObject=new JSONObject();
		toolObject.put("tool_name", "");
		toolObject.put("report_link", "");
		toolObject.put("pass", "");
		toolObject.put("Fail", "");
		toolObject.put("transactions", transactionArray);



		JSONArray toolArray=new JSONArray();
		toolArray.add(toolObject);


		JSONObject performanceObject=new JSONObject();
		performanceObject.put("tool", toolArray);







		JSONObject livebuildconsoleObj=new JSONObject();
		//1.creating svn object
		livebuildconsoleObj.put("SVN checkout", "");
		//2.creating staticCodeAnalysis object
		livebuildconsoleObj.put("staticCodeAnalysis", sonarObject);
		livebuildconsoleObj.put("uniteTesting", junitObject);
		livebuildconsoleObj.put("application_under_test", applicationUnderTestDataObject);	
		//livebuildconsoleObj.put("multiplatform", multiPlatformObject);
		livebuildconsoleObj.put("performance", performanceObject);






		//creating BuildHistory json
		//platoJsonObj.put("BuildHistory", "");
		platoJsonObj.put("buid_history_id", mbh.getModule_build_history_id());
				platoJsonObj.put("timestamp",mbh.getTimestamp().toString());

		//platoJsonObj.put("buid_history_id", 3);
		platoJsonObj.put("build_history_id", platoJsonTemplate.buildHistoryId);
	//	platoJsonObj.put("timestamp","248238432");
		platoJsonObj.put("timestamp",mbh.getTimestamp().toString());
		platoJsonObj.put("Environment", environmentObj);
		platoJsonObj.put("livebuildconsole", livebuildconsoleObj);


		JSONObject platoFinalJsonObj = new JSONObject();
		platoFinalJsonObj.put("BuildHistory", platoJsonObj);

		try{
			finalPlatoJson=platoFinalJsonObj.toString();
			System.out.println(finalPlatoJson);
		}catch(Exception e){
			e.printStackTrace();

		}
		
		
		
		// for testing.........later on finalPlatoJson will be loaded from mongodb or formed as above
		//readJenkinsConsoleAndUpdateStatus(finalPlatoJson);
		//platoJson=finalPlatoJson;
		
		UpdateJsonInMongoDB(platoFinalJsonObj);
		
	//	readJenkinsTotalResponse(platoJsonTemplate);
		
		UpdateJsonInMongoDB(platoFinalJsonObj);
		
		readJenkinsConsoleAndUpdateStatus(finalPlatoJson);
		
		
		
		
		//return finalPlatoJson;
		return platoFinalJsonObj;

	*/
			
	return null;}


		//gaurav will call this function
	public String readJenkinsTotalResponse() throws ParseException, InterruptedException {
		//int count =0;
		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		for(;;){
			//count++;
			Thread.sleep(10000);
			jenkinsConsoleOutput=readJenkinsConsole();
				
			finalPlatoJsonObj=readJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
			
			System.out.println(jenkinsConsoleOutput);
			//if(count==5){
			if(jenkinsConsoleOutput.contains("FAST Execution Completed")){
				System.out.println("jenkins console output contains******** Fast Execution Completed ");
				break;
			}
			
			//return finalPlatoJsonObj;
		}
		//return finalPlatoJsonObj;
		
		if(status==false){
			response="Failed";
		}else{
			response="Passed";
		}
		
		return response;
		//return jenkinsConsoleOutput;
	}
	
private JSONObject readJenkinsConsoleAndUpdateStatus(String jenkinsConsoleOutput) throws ParseException {
		// TODO Auto-generated method stub
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
    		JSONArray aa3= (JSONArray) obj5.get("testcases");
    		for(int k=0;k<aa3.size();k++){
    			JSONObject obj6= (JSONObject)aa3.get(k);
    			String testCaseName=(String) obj6.get("testcase_name");
    			
    			if(jenkinsConsoleOutput.contains(testCaseName+" Test Case Passed")){
    				if(((String)(obj6.get("status"))).equalsIgnoreCase("")){
    					System.out.println("status of test case "+testCaseName + " : "+obj6.get("status"));
    				obj6.replace("status", "", "pass");
    				}
    			}else if(jenkinsConsoleOutput.contains(testCaseName+" Test Case Failed")){
    				//obj6.replace("status", "", "fail");
    				if(((String)(obj6.get("status"))).equalsIgnoreCase("")){
    					System.out.println("status of test case "+testCaseName + " : "+obj6.get("status"));
    				obj6.replace("status", "", "fail");
    				}
    				if(status){
    				status=false;
    				}
    			}else{
    				//break;
    			}
    			
    			
    		}
    	}
	}
	

    		//
    		//	update the json in mongoDb
    
    			UpdateJsonInMongoDB(finalPlatoJsonObj);
    			
    			
    	return finalPlatoJsonObj;		
	}





private void UpdateJsonInMongoDB(JSONObject finalPlatoJsonObj) {/*
	// TODO Auto-generated method stub
	
	String finalPlatoJson=finalPlatoJsonObj.toJSONString();
	
	MongoClient mongoClient = new MongoClient( "172.25.9.140" , 27011 );
	MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
	MongoCollection collection = database.getCollection("BuildHistoryDetails");
	Document doc = Document.parse(finalPlatoJson);
	
	
	
	
	FindIterable<Document> iterDoc = collection.find(); 
	
	Iterator it = iterDoc.iterator(); 
	
	if(it.hasNext()==false){
		collection.insertOne(doc); 
	}else{
		while (it.hasNext()) {  
			Document query=(Document) it.next();
    		String platoJson=query.toString();
    		if(platoJson.contains("build_history_id="+buildHistoryId)){
    			//Bson filter = new Document("build_history_id", buildHistoryId);
    			//collection.replaceOne(filter, doc);
    			//collection.deleteOne(Filters.eq("build_history_id", buildHistoryId)); 
    			//collection.insertOne(doc); 
    			
    			
    			BasicDBObject query1 = new BasicDBObject();
    
    			    query.append("build_history_id", buildHistoryId);
    	 		
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
	}
	
    while (it.hasNext()) {  
    	if(it.next()==null){
    		collection.insertOne(doc); 
    	}else{
    		
    		Bson filter = new Document("build_history_id", "3");
    		Bson updateOperationDocument = new Document("$set", doc);
    		collection.updateOne(filter, updateOperationDocument);
    	}
    	
    }
	
*/
	


	// TODO Auto-generated method stub

	boolean flag=false;
	
	String finalPlatoJson=finalPlatoJsonObj.toJSONString();
	MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
try {
	
	MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
	MongoCollection collection = database.getCollection("BuildHistoryDetails");
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
			if(platoJson.contains("build_history_id="+buildHistoryId)){
				//Bson filter = new Document("build_history_id", buildHistoryId);
				//collection.replaceOne(filter, doc);
				//collection.deleteOne(Filters.eq("build_history_id", buildHistoryId)); 
				//collection.insertOne(doc); 

				flag=true;
				break;				

			}
		}	
			
			if(flag){
				
				collection.deleteOne(query);
				
				BasicDBObject query1 = new BasicDBObject();

				query1.append("build_history_id", buildHistoryId);

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

}finally {
	mongoClient.close();
	System.out.println("In finally block. Mongo Client connection closed!");

}


}



private String fetchJsonFromMongoDB() {
	// TODO Auto-generated method stub
	
	//String finalPlatoJson=finalPlatoJsonObj.toJSONString();
	
	String platoJson="";
	
	
	MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
	
	try{
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






private String readJenkinsConsole() throws ParseException, InterruptedException {
	// TODO Auto-generated method stub
	String jenkinsConsoleOutput = null;
	
	//int buildNumber=1;
	String resp="";
	
	//String url= "http://172.25.9.140:8080/jenkins";
  //  String jenkinsDir="C:/Users/10621390/.jenkins";
	System.out.println("in getconsoleOutput");
	try {			
		//url=url+"/job/"+platoJsonTemplate.jobName+"/"+platoJsonTemplate.buildNumber+"/consoleText";			
		
		
		String url= GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/job_1234/"+buildNumber+"/consoleText";
		
		resp=readJenkinsResponse(url);
		
		//if(platoJsonTemplate.buildNumber != 0){
			/*Client client = Client.create();				
			WebResource webResource = client.resource(url+"/job/"+jobName+"/"+buildNumber+"/consoleText");

			ClientResponse response = webResource.get(ClientResponse.class);
			resp = response.getEntity(String.class);
			*/
			

		//}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	

	jenkinsConsoleOutput=resp;
	
	
	
	
	//readJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
	Thread.sleep(2000);
	return jenkinsConsoleOutput;

  //return "hi";	
}





private String readJenkinsResponse(String url) throws IOException {
	// TODO Auto-generated method stub
	
	
	
	URL object=null;

	//JSONObject msgobj=new JSONObject(jsonData);	
	try {
		object = new URL(url);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
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




}
