package com.reportingServices;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.json.JSONException;
import org.json.XML;

import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;


public class UftReportService {
	
	private static final Logger logger=Logger.getLogger(DIATReportService.class);


	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private boolean status=true;
	private String jenkinsUrl;
	private String reportPath;
	//MongoDBOperations mongoDBOperations=new  MongoDBOperations();
	
	String jenkinsConsoleOutput="";
	
	public UftReportService(int buildHistoryId, int buildNumber, String jobName,String jenkinsUrl,String reportPath) {
		// TODO Auto-generated constructor stub
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
		this.jenkinsUrl=jenkinsUrl;
		this.reportPath=reportPath;
	}
	
	public String readUftJenkinsTotalResponse() throws ParseException, InterruptedException, ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException, JSONException {
		
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
			if(jenkinsConsoleOutput.contains("Finished: SUCCESS")|| jenkinsConsoleOutput.contains("Finished: FAILURE") || jenkinsConsoleOutput.contains("Finished: ABORTED") || jenkinsConsoleOutput.contains("Finished: UNSTABLE") || jenkinsConsoleOutput.contains("FATAL: null")){
				System.out.println("jenkins console output contains******** DIAT Execution Completed ");
				finalPlatoJsonObj=readUftJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
				break;
			}
			
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
	
	public JSONObject readUftJenkinsConsoleAndUpdateStatus(String jenkinsConsoleOutput) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException, ParseException, JSONException  {
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
		JSONObject jsonUftReport=new JSONObject();
		JSONObject finalPlatoJsonObj=new JSONObject();
		
		File f=new File(reportPath);
		File[] flist=f.listFiles();
		for(File file: flist) {
			if(f.toString().contains(".xml")) {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(file);
				
				StringWriter sw = new StringWriter(); 
				Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
				serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
				String reportString=sw.toString();
				
				
				System.out.println("reportString is "+reportString);
				JSONParser parser = new JSONParser();
				jsonUftReport=(JSONObject)parser.parse(XML.toJSONObject(reportString).toString());
				
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
					
					jsonUftReport = (JSONObject) parser.parse(reportString);
					System.out.println("jsonDiatReport is "+jsonUftReport);
				}catch(Exception e) {
					e.printStackTrace();
				}
				JSONObject obj1= (JSONObject) finalPlatoJsonObj.get("BuildHistory");
				JSONObject obj2= (JSONObject) obj1.get("LiveBuildConsole");

				this.jenkinsConsoleOutput=consoleOutput;
				
				obj1.put("Diat",jsonUftReport);
				System.out.println("obj1 is "+obj1);
				
				logger.info("Updating the MongoDB template");
				logger.debug(finalPlatoJsonObj);
				logger.debug("Updating template for build history id :"+buildHistoryId+ " and job name :  "+jobName );
				MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
				logger.debug("Updated Status of DIAT in MongoDB for build history id :"+buildHistoryId+ " and job name :  "+jobName);				
			}
		}


		
		return finalPlatoJsonObj;
		}
	
}