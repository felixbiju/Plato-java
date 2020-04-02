package com.reportingServices;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;
import com.mongo.utilities.ReadSonarDashboard;
/**
 * @author 10643331(Sueanne Alphonso)
 **/
public class SONARReportService {

	private static final Logger logger = Logger
			.getLogger(SONARReportService.class);

	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status = "Passed";
	private String jenkinsUrl;
	private String sonarKey;
	private String reportPath;

	String jenkinsConsoleOutput = "";
	String sonarReportDashboard = "";

	public SONARReportService(int buildHistoryId, int buildNumber,
			String jobName, String jenkinsUrl, String sonarKey,
			String reportPath) {
		this.buildNumber = buildNumber;
		this.jobName = jobName;
		this.buildHistoryId = buildHistoryId;
		this.jenkinsUrl = jenkinsUrl;
		this.sonarKey = sonarKey;
		this.reportPath = reportPath;

	}

	public String readSONARJenkinsTotalResponse() throws Exception {

		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();
		logger.info("in readSONARJenkinsTotalResponse");
		ReadSonarDashboard readSonarDashboard = new ReadSonarDashboard();

		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput=new String();
		String sonarReportDashboard;
		for (;;) {
			Thread.sleep(10000);
			jenkinsConsoleOutput = readJenkinsConsole.readJenkinsConsole(
					buildNumber, jobName, jenkinsUrl);

			finalPlatoJsonObj = readSONARJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);

			if (jenkinsConsoleOutput.contains("Finished: SUCCESS")
					|| jenkinsConsoleOutput.contains("Finished: FAILURE")
					|| jenkinsConsoleOutput.contains("Finished: ABORTED")
					|| jenkinsConsoleOutput.contains("Finished: UNSTABLE")
					|| jenkinsConsoleOutput.contains("FATAL: null")) {
				System.out.println("jenkins console out finihsed");

				String sonarReportUrl = reportPath.trim()
						+ "/api/resources?resource="
						+ sonarKey
						+ "&metrics=duplicated_lines_density,functions,files,classes,ncloc,violations,sqale_index,blocker_violations,critical_violations,major_violations,minor_violations,info_violations,duplicated_blocks,duplicated_files,duplicated_lines,complexity,class_complexity,file_complexity,function_complexity";
				System.out.println("earnix url is" + sonarReportUrl);

				sonarReportDashboard = readSonarDashboard
						.readSonarDashboard(sonarReportUrl);
				System.out.println(sonarReportDashboard);

				finalPlatoJsonObj = readSONARReportAndUpdateMongoDB(sonarReportDashboard);

				break;
			}

		}

		if (status.equalsIgnoreCase("Failed")) {
			response = "Failed";
		} else if (status.equalsIgnoreCase("Aborted")) {
			response = "Aborted";
		} else {
			response = "Passed";
		}
		return response;

	}

	private JSONObject readSONARJenkinsConsoleAndUpdateStatus(
			String jenkinsConsoleOutput) throws Exception {
		String consoleOutput;
		consoleOutput = jenkinsConsoleOutput;

		if (!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))) {
			// String
			// jenkinsOutput[]=jenkinsConsoleOutput.split(this.jenkinsConsoleOutput);
			if (jenkinsConsoleOutput.contains(this.jenkinsConsoleOutput.trim())) {
				jenkinsConsoleOutput = jenkinsConsoleOutput.replace(
						this.jenkinsConsoleOutput.trim(), "");
			}

		} else {
			System.out.println("this.jenkinsConsoleOutput is blank");
		}

		JSONParser parser = new JSONParser();

		// ------------------------------changed for mongo read count
		JSONObject finalPlatoJsonObj = null;
		String finalPlatoJsonString = null;
		while (finalPlatoJsonString == null) {
			logger.debug("Inside while loop. Fetching template for build history id :"
					+ buildHistoryId + " and job name :  " + jobName);
			finalPlatoJsonString = MongoDBOperations
					.fetchJsonFromMongoDB(buildHistoryId);

		}
		logger.debug("Retrived data : " + finalPlatoJsonString);
		System.out.println("Retrived data : " + finalPlatoJsonString);
		finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);

		// added for read count
		finalPlatoJsonObj.replace("readCount", "1", "0");
		// -----
//
//		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
//		
//		
//		
		
		
/*
		JSONObject obj3 = (JSONObject) buildObject.get("StaticCodeAnalysis");

		JSONObject obj4 = (JSONObject) obj3.get("Sonar");*/

	/*	String sonarStatus = (String) obj4.get("status");
		obj4.replace("job_name", "", jobName);
		obj4.replace("build_number", "", buildNumber);

		if (sonarStatus == null) {
			sonarStatus = "";
		}

		if (jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
			obj4.replace("status", sonarStatus, "Passed");
		} else if (jenkinsConsoleOutput.contains("Finished: FAILURE")) {
			obj4.replace("status", sonarStatus, "Failed");

			if (status.equalsIgnoreCase("Passed")) {
				status = "Failed";
			}

		} else if (jenkinsConsoleOutput.contains("Finished: ABORTED")) {
			obj4.replace("status", sonarStatus, "Aborted");
			status = "Aborted";

		} else if (jenkinsConsoleOutput != null
				|| !(jenkinsConsoleOutput.contains("Finished: FAILURE"))
				|| !(jenkinsConsoleOutput.contains("Finished: SUCCESS") || !(jenkinsConsoleOutput
						.contains("Finished: ABORTED")))) {
			obj4.replace("status", sonarStatus, "In_Progress");

		}
*/
		this.jenkinsConsoleOutput = consoleOutput;
		// update the json in mongoDb

		MongoDBOperations
				.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistoryId);
		
		System.out.println(finalPlatoJsonObj);

		return finalPlatoJsonObj;
	}

	private JSONObject readSONARReportAndUpdateMongoDB(
			String sonarReportDashboard) throws Exception {
		String sonarReport;
		sonarReport = sonarReportDashboard;
		HashMap<String, String> queryMap = new HashMap<String, String>();

		if (sonarReport != null) {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(
					sonarReport)));

			Node resource = doc.getElementsByTagName("resource").item(0);
			NodeList res = resource.getChildNodes();

			for (int count = 0; count < res.getLength(); count++) {

				Node msr = doc.getElementsByTagName("msr").item(count);
				if (msr != null) {
					Element eElement = (Element) msr;
					Node key = eElement.getElementsByTagName("key").item(0);
					Node value = eElement.getElementsByTagName("frmt_val")
							.item(0);

					String key1 = key.getTextContent();
					String value1 = value.getTextContent();

					queryMap.put(key1, value1);

				} else {
					continue;
				}
			}

			System.out.println(queryMap);

		}

		JSONParser parser = new JSONParser();
		// added for readCount-------------------
		JSONObject finalPlatoJsonObj = null;
		String finalPlatoJsonString = null;
		while (finalPlatoJsonString == null) {
			logger.debug("Inside while loop. Fetching template for build history id :"
					+ buildHistoryId + " and job name :  " + jobName);
			System.out.println("Inside while loop. Fetching template for build history id :"
					+ buildHistoryId + " and job name :  " + jobName);
			finalPlatoJsonString = MongoDBOperations
					.fetchJsonFromMongoDB(buildHistoryId);
		}
		
		

	
		
		
		
		logger.debug("Retrived data : " + finalPlatoJsonString);
		finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
		// end----------------------------------

		// added for read count
		finalPlatoJsonObj.replace("readCount", "1", "0");
		
		
		String gg;
		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		

		JSONObject buildObject = (JSONObject) obj1.get("Build");
		JSONObject obj = (JSONObject) obj1.get("LiveBuildConsole");
		JSONArray subJobList = (JSONArray) obj.get("tools");

		//JSONArray subJobList = (JSONArray) obj3.get("tools");

		int len = subJobList.size();

		//len is the size of the subJob list
		for (int i = 0; i < len; i++) {
			int flag=0;
			
			//get the first subjob and check if its the current threads job, because every job is running on a different thread
			JSONObject subobj = (JSONObject) subJobList.get(i);

			String jName = (String) subobj.get("tool_name");
			
			//checking job name with thread job
			if (jName.equalsIgnoreCase(jobName)) {
				
				JSONArray modules = (JSONArray) subobj.get("modules");
				
				String toolsts=(String) subobj.get("tool_status");
				
				if (jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
					System.out
							.println("Updating Sonar Status in Mongo LBC---------------------------------"
									+ jName);
					subobj.replace("tool_status", toolsts, "Passed");
					
					if(status.equalsIgnoreCase("failed")) {
						subobj.replace("tool_status", toolsts, "Failed");
					status="Failed";
					}
					
					
				} else if (jenkinsConsoleOutput.contains("Finished: FAILURE")) {
					System.out
							.println("Updating Sonar Status in Mongo LBC ---------------------------------"
									+ jName);
					subobj.replace("tool_status", toolsts, "Failed");
					status = "Failed";

				} else if (jenkinsConsoleOutput.contains("Finished: ABORTED")) {
					subobj.replace("tool_status", toolsts, "Aborted");
					status = "Aborted";
				} else if (jenkinsConsoleOutput != null
						|| !(jenkinsConsoleOutput.contains("Finished: FAILURE"))
						|| !(jenkinsConsoleOutput.contains("Finished: SUCCESS"))
						|| !(jenkinsConsoleOutput.contains("Finished: ABORTED"))) {
					subobj.replace("tool_status", toolsts, "In_Progress");
					System.out
							.println("In Progress Updated in Mongo DB LBC----------------------------------------"
									+ jName);

				}
		
		
		
			}
		
		
		
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//2.1.creating sonar object
		JSONObject totalDataObject=new JSONObject();
		JSONObject issuesDataObject=new JSONObject();
		JSONObject complexityDataObject=new JSONObject();
		JSONObject duplicationsDataObject=new JSONObject();




		totalDataObject.put("issues", "");
		totalDataObject.put("complexity", "");
		totalDataObject.put("duplications", "");
		totalDataObject.put("technical_debt", "");


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
		sonarDataObject.put("total",totalDataObject );
		sonarDataObject.put("issues",issuesDataObject );
		sonarDataObject.put("complexity",complexityDataObject );
		sonarDataObject.put("duplications", duplicationsDataObject);
		
		//sonarDataObject.put("technicalDebt","" );
		
		sonarDataObject.put("technical_debt","" );
		sonarDataObject.put("reportlink", "");
		sonarDataObject.put("status", "");
		sonarDataObject.put("job_name", "");
		sonarDataObject.put("build_number", "");
		



		JSONObject sonarObject=new JSONObject();
		sonarObject.put("Sonar", sonarDataObject);

		//end creating sonar object	
		
		//putting the object in livebuildconsoleObj
		buildObject.put("StaticCodeAnalysis", sonarObject);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		JSONObject obj3 = (JSONObject) buildObject.get("StaticCodeAnalysis");

		JSONObject obj4 = (JSONObject) obj3.get("Sonar");

		JSONObject obj5 = (JSONObject) obj4.get("total");

		String issues = (String) obj5.get("issues");
		if (issues == null) {
			issues = "";
		}
		gg = queryMap.get("violations");

		obj5.replace("issues", issues, gg);

		String complexity = (String) obj5.get("complexity");
		if (complexity == null) {
			complexity = "";
		}
		gg = queryMap.get("complexity");
		obj5.replace("complexity", complexity, gg);

		String duplications = (String) obj5.get("duplications");
		if (duplications == null) {
			duplications = "";
		}
		gg = queryMap.get("duplicated_lines_density");
		obj5.replace("duplications", duplications, gg);

		String technical_debt = (String) obj5.get("technical_debt");
		if (technical_debt == null) {
			technical_debt = "";
		}
		gg = queryMap.get("sqale_index");
		obj5.replace("technical_debt", technical_debt, gg);

		JSONObject obj6 = (JSONObject) obj4.get("issues");

		String blocker = (String) obj6.get("blocker");
		if (blocker == null) {
			blocker = "";
		}
		gg = queryMap.get("blocker_violations");
		obj6.replace("blocker", blocker, gg);

		String critical = (String) obj6.get("critical");
		if (critical == null) {
			critical = "";
		}
		gg = queryMap.get("critical_violations");
		obj6.replace("critical", critical, gg);

		String major = (String) obj6.get("major");
		if (major == null) {
			major = "";
		}
		gg = queryMap.get("major_violations");
		obj6.replace("major", major, gg);

		String minor = (String) obj6.get("minor");
		if (minor == null) {
			minor = "";
		}
		gg = queryMap.get("minor_violations");
		obj6.replace("minor", minor, gg);

		String info = (String) obj6.get("info");
		if (info == null) {
			info = "";
		}
		gg = queryMap.get("info_violations");
		obj6.replace("info", info, gg);

		JSONObject obj7 = (JSONObject) obj4.get("complexity");
		String file = (String) obj7.get("file");
		if (file == null) {
			file = "";
		}
		gg = queryMap.get("file_complexity");
		obj7.replace("file", file, gg);

		String class1 = (String) obj7.get("class");
		if (class1 == null) {
			class1 = "";
		}
		gg = queryMap.get("class_complexity");
		obj7.replace("class", class1, gg);

		String function = (String) obj7.get("function");
		if (function == null) {
			function = "";
		}
		gg = queryMap.get("function_complexity");
		obj7.replace("function", function, gg);

		JSONObject obj8 = (JSONObject) obj4.get("duplications");
		String lines = (String) obj8.get("lines");
		if (lines == null) {
			lines = "";
		}
		gg = queryMap.get("duplicated_lines");
		obj8.replace("lines", lines, gg);

		String blocks = (String) obj8.get("blocks");
		if (blocks == null) {
			blocks = "";
		}
		gg = queryMap.get("duplicated_blocks");
		obj8.replace("blocks", blocks, gg);

		String files = (String) obj8.get("files");
		if (files == null) {
			files = "";
		}
		gg = queryMap.get("duplicated_files");
		obj8.replace("files", files, gg);

		this.sonarReportDashboard = sonarReport;
		// update the json in mongoDb
		MongoDBOperations
				.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistoryId);
		System.out.println(finalPlatoJsonObj);
		return finalPlatoJsonObj;
	}
}
