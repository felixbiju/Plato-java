package com.reportingServices;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.mongo.utilities.MongoDBOperations;
import com.mongo.utilities.ReadJenkinsConsole;
/**
 * @author 10643331(Sueanne Alphonso)
 **/
public class MAVENReportService {
	private static final Logger logger = Logger
			.getLogger(MAVENReportService.class);

	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status = "Passed";
	private String jenkinsUrl;

	String jenkinsConsoleOutput = "";

	public MAVENReportService(int buildHistoryId, int buildNumber,
			String jobName, String jenkinsUrl) {
		this.buildNumber = buildNumber;
		this.jobName = jobName;
		this.buildHistoryId = buildHistoryId;
		this.jenkinsUrl = jenkinsUrl;
	}

	public String readMAVENJenkinsTotalResponse() throws Exception {
		logger.info("in readMAVENJenkinsTotalResponse");
		ReadJenkinsConsole readJenkinsConsole = new ReadJenkinsConsole();
		String response;
		JSONObject finalPlatoJsonObj;
		String jenkinsConsoleOutput;
		logger.info("Reading Jenkins Console");
		for (;;) {
			Thread.sleep(10000);
			jenkinsConsoleOutput = readJenkinsConsole.readJenkinsConsole(
					buildNumber, jobName, jenkinsUrl);
			finalPlatoJsonObj = readMAVENJenkinsConsoleAndUpdateStatus(jenkinsConsoleOutput);
			if (jenkinsConsoleOutput.contains("Finished: SUCCESS")
					|| jenkinsConsoleOutput.contains("Finished: FAILURE")
					|| jenkinsConsoleOutput.contains("Finished: ABORTED")) {
				System.out.println("jenkins console output contains FINISHED ");
				break;
			}
		}
		logger.info("Completed Reading Jenkins Console");
		if (status.equalsIgnoreCase("Failed")) {
			response = "Failed";
		} else if (status.equalsIgnoreCase("Aborted")) {
			response = "Aborted";
		} else {
			response = "Passed";
		}
		return response;
	}

	private JSONObject readMAVENJenkinsConsoleAndUpdateStatus(
			String jenkinsConsoleOutput) throws Exception {
		System.out
				.println("in readMAVENJenkinsConsoleAndUpdateStatus----------------------");
		String consoleOutput;
		consoleOutput = jenkinsConsoleOutput;

		if (!(this.jenkinsConsoleOutput.equalsIgnoreCase(""))) {
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
		finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
		// ------------------------------------------end

		// added for read count
		finalPlatoJsonObj.replace("readCount", "1", "0");
		// -----

		JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
		JSONObject obj2 = (JSONObject) obj1.get("LiveBuildConsole");
		JSONObject obj3 = (JSONObject) obj2.get("Build");

		String mavenStatus = (String) obj3.get("status");

		obj3.replace("job_name", "", jobName);
		obj3.replace("build_number", "", buildNumber);

		if (jenkinsConsoleOutput.contains("Finished: SUCCESS")) {
			System.out
					.println("Updating Build Status in Mongo ---------------------------------");
			obj3.replace("status", mavenStatus, "Passed");
		} else if (jenkinsConsoleOutput.contains("Finished: FAILURE")) {
			System.out
					.println("Updating Build Status in Mongo ---------------------------------");
			obj3.replace("status", mavenStatus, "Failed");
			status = "Failed";
		} else if (jenkinsConsoleOutput.contains("Finished: ABORTED")) {
			obj3.replace("status", mavenStatus, "Aborted");
			status = "Aborted";
		} else if (jenkinsConsoleOutput != null
				|| !(jenkinsConsoleOutput.contains("Finished: FAILURE"))
				|| !(jenkinsConsoleOutput.contains("Finished: SUCCESS") || !(jenkinsConsoleOutput
						.contains("Finished: ABORTED")))) {
			obj3.replace("status", mavenStatus, "In_Progress");
			System.out
					.println("In Progress Updated in Mongo DB----------------------------------------");

		}

		this.jenkinsConsoleOutput = consoleOutput;

		// update the json in mongoDb

		MongoDBOperations
				.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistoryId);

		System.out.println(finalPlatoJsonObj);

		return finalPlatoJsonObj;
	}

}
