package com.plato.buildTemplate;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.adapters.RsaImpactAdapter;
import com.mongo.utilities.MongoDBOperations;
import com.plato.template.PlatoJsonTemplate;
import com.reportingServices.BuildReportService;
import com.reportingServices.DIATReportService;
import com.reportingServices.DuckCreekReportingService;
import com.reportingServices.FASTReportService;
import com.reportingServices.GenericCommandReportService;
import com.reportingServices.MAVENReportService;
import com.reportingServices.SONARReportService;


@Path(value = "PlatoMongoTemplate")
public class PlatoMongoTemplate {
	
	public PlatoMongoTemplate(){
		logger.info("Inside Constructor of PlatoMongoTemplate");
	}
	
	private static final Logger logger=Logger.getLogger(PlatoMongoTemplate.class);


	@GET
	@Path("getMongoTemplate/{buildHistoryId}/{jobName}/{buildNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response getMongoTemplateFunction(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber) throws Exception{
		
		logger.info("inside getMongoTemplate");
		PlatoJsonTemplate pt=new PlatoJsonTemplate(buildHistoryId,buildNumber,jobName);
		JSONObject platoJsonObject = null;
		
		try{
			platoJsonObject=pt.createMongoTemplateFunction(pt);
		 if(platoJsonObject==null){
				return Response.status(Response.Status.NOT_FOUND).entity(platoJsonObject).build();
		 }
		 return  Response.status(Response.Status.OK).entity(platoJsonObject).build();		
		}catch(Exception e){
			return  Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(platoJsonObject).build();
		}
		
	}
	
	//********Written by Nilesh
	@GET
	@Path("fastReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}/{toolName}")
	public String updateMongoTemplateFunction(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl,@PathParam("toolName") String toolName) throws Exception{
		logger.info("inside fastReport Service");
		logger.info("Job name:  "+jobName);
				
 		FASTReportService fastReportService=new FASTReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,toolName);	
		try{
		
			logger.info("Reading FAST Jenkins Console");
			String resp=fastReportService.readFASTJenkinsTotalResponse();
			resp=fastReportService.createFASTStepWiseReport(buildHistoryId);
			
			logger.info("Successfully completed Reporting Service of FAST with Status  "+resp);
			return resp;
	
		
		}catch(Exception e){
				logger.error("Exception in PlatoMongoTemplate");
			e.printStackTrace();
			return "Failed";
		}
	}
	

	//Written By Sueanne
	@GET
	@Path("mavenReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}")
	public String updateMongoTemplateFunctionForMaven(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl) throws Exception{
		logger.info("inside Maven Report Service");
		MAVENReportService mavenReportService=new MAVENReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl);
		
		try{
			String resp=mavenReportService.readMAVENJenkinsTotalResponse();
			logger.info("Successfully completed Reporting Service of MAVEN with Status  "+resp);
			return resp;
		}catch(Exception e){
			return "Failed";
		}	
	}	
	
	//Written By Jyoti
	@GET
	@Path("sonarReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}/{sonarKey}/")
	public String updateMongoTemplateFunctionForSONAR(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl,@PathParam("sonarKey") String sonarKey,@QueryParam("reportPath") String reportPath) throws Exception{
		logger.info("inside SONAR Report Service");
		System.out.println(sonarKey);
		System.out.println(reportPath);
		System.out.println("printed both");
		
		logger.debug("Sonar Key: "+sonarKey+" Report Path: "+reportPath);
		SONARReportService sonarReportService=new SONARReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,sonarKey,reportPath);
		
		try{
			String resp=sonarReportService.readSONARJenkinsTotalResponse();
			logger.info("Successfully completed Reporting Service of SONAR with Status  "+resp);
			return resp;

		}catch(Exception e){
			//return  Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(platoJsonObject).build();
		return "Failed";
		}		
	}	
	
	//Rahul
		@GET
		@Path("diatReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}/")
		public String updateMongoTemplateFunctionForDIAT(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl,@QueryParam("reportPath") String reportPath) throws Exception{
			System.out.println(reportPath);
			System.out.println("printed");
			
			DIATReportService diatReportService=new DIATReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,reportPath);
			
			try{
				String resp=diatReportService.readDIATJenkinsTotalResponse();
				return resp;
			}catch(Exception e){
				e.printStackTrace();
			return "Failed";
			}		
		}
		
		//Rahul
		@GET
		@Path("uftReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}/")
		public String updateMongoTemplateFunctionForUft(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl,@QueryParam("reportPath") String reportPath) throws Exception{
			System.out.println(reportPath);
			System.out.println("printed");			
			
			DIATReportService diatReportService=new DIATReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,reportPath);
			
			JSONObject platoJsonObject = null;
			try{
				String resp=diatReportService.readDIATJenkinsTotalResponse();
				return resp;
			}catch(Exception e){
				e.printStackTrace();
			return "Failed";
			}		
		}
		
		//Rahul
		@GET
		@Path("genericCommandReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}/{toolName}")		
		public String updateMongoTemplateFunctionForGenericCommand(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl,@PathParam("toolName") String toolName,@QueryParam("reportPath") String reportPath) throws Exception{
			System.out.println("PlatoMongoTool-159:in genericCommandReportService");
			System.out.println("PlatoMongoTool-161:jobName is "+jobName);
			GenericCommandReportService genericCommandReportService=new GenericCommandReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,toolName,reportPath);
			JSONObject platoJsonObject = null;
			try{
				String resp=genericCommandReportService.readGenericCommandJenkinsTotalResponse();
				return resp;
			}catch(Exception e){
				e.printStackTrace();
				return "Failed";
			}
		}
		
		@POST
		@Path("rsaImpact")
		public Response rsaImpact(String reportPath) throws Exception {
			RsaImpactAdapter rsaImpactAdapter=new RsaImpactAdapter();
			JSONObject report=new JSONObject();
			report=rsaImpactAdapter.GetJsonObject(reportPath);
			return Response.status(Response.Status.OK).entity(report).build();
		}
		@PUT
		@Path("updateMongo")
		public Response updateMongo(String finalPlatoJson) {
			JSONParser parser=new JSONParser();
			JSONObject finalPlatoJsonObj=new JSONObject();
			try {
				finalPlatoJsonObj=(JSONObject)parser.parse(finalPlatoJson);
			}catch(Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("cannot parse "+finalPlatoJsonObj).build();
			}

			JSONObject buildHistory=(JSONObject)finalPlatoJsonObj.get("BuildHistory");
			int buildHistoryId=Integer.parseInt((String)buildHistory.get("build_history_id"));
			MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj,buildHistoryId);
			return Response.status(Response.Status.OK).entity(finalPlatoJsonObj).build();
		}
		@GET
		@Path("duckCreekReportService/{buildHistoryId}/{jobName}/{buildNumber}/{jenkinsUrl}")
		public String updateMongoTemplateFunctionForDuckCreek(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@PathParam("jenkinsUrl") String jenkinsUrl,@QueryParam("reportPath") String reportPath,@QueryParam("duckCreekUrl") String duckCreekUrl) {
			System.out.println("PlatoMongoTool-173:in duckCreekReportService");
			DuckCreekReportingService duckCreekReportingService=new DuckCreekReportingService(buildHistoryId,buildNumber,jobName,duckCreekUrl,reportPath);
			try {
				String resp=duckCreekReportingService.readDuckCreekTotalResponse();
				return resp;
			}catch(Exception e) {
				return "Failed";
			}
		}
		
		@GET
		@Path("shouldPostBuildJobsContinue/{buildHistoryId}/{jobName}/{buildNumber}")
		public String shouldPostBuildJobsContinue(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber) {
			BuildReportService buildReportService=new BuildReportService(buildHistoryId,jobName,buildNumber);
			boolean result=false;
			try {
				result=buildReportService.shouldPostBuildJobsContinue();
				return Boolean.toString(result);
			}catch(Exception e) {
				e.printStackTrace();
				return Boolean.toString(false);
			}
		}
		@GET
		@Path("buildJunit/{buildHistoryId}/{jobName}/{buildNumber}")
		public String buildJunit(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber) {
			BuildReportService buildReportService=new BuildReportService(buildHistoryId,jobName,buildNumber);
			boolean result=false;
			try {
				result=buildReportService.shouldPostBuildJobsContinue();
				return Boolean.toString(result);
			}catch(Exception e) {
				e.printStackTrace();
				return Boolean.toString(false);
			}
		}
		@GET
		@Path("buildTestNg/{buildHistoryId}/{jobName}/{buildNumber}")
		public String buildTestNg(@PathParam("buildHistoryId") int buildHistoryId,@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber) {
			BuildReportService buildReportService=new BuildReportService(buildHistoryId,jobName,buildNumber);
			boolean result=false;
			try {
				result=buildReportService.shouldPostBuildJobsContinue();
				return Boolean.toString(result);
			}catch(Exception e) {
				e.printStackTrace();
				return Boolean.toString(false);
			}
		}
		
		//Sueanne
		@GET
		@Path("updateMainJobAbortStatus/{buildHistroyId}")
		public String updateAbortStatus(@PathParam("buildHistoryId") int buildHistoryId) throws Exception{
			System.out.println("Updating Abort status for "+buildHistoryId);
			JSONObject finalPlatoJsonObj = null;
			JSONParser parser = new JSONParser();
			String finalPlatoJsonString = MongoDBOperations.fetchJsonFromMongoDB(buildHistoryId);
			
			while (finalPlatoJsonString == null) {
				logger.debug("Inside while loop. Fetching template for build history id :"
						+ buildHistoryId);
				finalPlatoJsonString = MongoDBOperations
						.fetchJsonFromMongoDB(buildHistoryId);
			}
			logger.debug("Retrived data : " + finalPlatoJsonString);
			finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
			
			
			
			return finalPlatoJsonString;
			
			//DIATReportService diatReportService=new DIATReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,reportPath);
			
			/*try{
				String resp=diatReportService.readDIATJenkinsTotalResponse();
				return resp;
			}catch(Exception e){
				e.printStackTrace();
			return "Failed";
			}	*/	
		}
		
		
		//Sueanne
				@GET
				@Path("updateParticularSubJobAbortStatus/{buildHistroyId}/{subjob_name}")
				public String updateParticularSubJobAbortStatus(@PathParam("buildHistroyId") int buildHistroyId,@PathParam("subjob_name") String subjob_name) throws Exception{
					System.out.println("Updating Abort status for "+buildHistroyId);
					JSONObject finalPlatoJsonObj = null;
					JSONParser parser = new JSONParser();
					String finalPlatoJsonString=null; 
					
					while (finalPlatoJsonString == null) {
						logger.debug("Inside while loop. Fetching template for build history id :"
								+ buildHistroyId);
						finalPlatoJsonString = MongoDBOperations
								.fetchJsonFromMongoDB(buildHistroyId);
					}
					logger.debug("Retrived data : " + finalPlatoJsonString);
					finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
					
					// added for read count
					finalPlatoJsonObj.replace("readCount", "1", "0");
					// -----
					JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
					JSONObject obj2 = (JSONObject) obj1.get("LiveBuildConsole");

					JSONArray subJobList = (JSONArray) obj2.get("tools");
					

					//JSONArray subJobList = (JSONArray) obj3.get("tools");

					int len = subJobList.size();
					String tool_status="";
					for (int i = 0; i < len; i++) {
						JSONObject obj4 = (JSONObject) subJobList.get(i);
						
						
						String jName = (String) obj4.get("tool_name");
						if (jName.equalsIgnoreCase(subjob_name)) {
							tool_status=(String) obj4.get("tool_status");
							if(tool_status.equalsIgnoreCase("In_Progress"))
								obj4.replace("tool_status", tool_status, "Aborted");
					
						}
						
					}
					
					MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistroyId);

					logger.debug("Updated Status to Aborted in MongoDB for build history id :"+ buildHistroyId + " and sub job name :  " + subjob_name);
					
					return finalPlatoJsonString;
					
					//DIATReportService diatReportService=new DIATReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,reportPath);
					
					/*try{
						String resp=diatReportService.readDIATJenkinsTotalResponse();
						return resp;
					}catch(Exception e){
						e.printStackTrace();
					return "Failed";
					}	*/	
				}
				
				//Sueanne
				@GET
				@Path("idiscoverReportService/")
				public String updateidiscoverReportServiceStatus(@PathParam("buildHistroyId") int buildHistroyId,@PathParam("subjob_name") String subjob_name) throws Exception{
					System.out.println("Service to fetch data from database ");
					
					
					JSONObject finalPlatoJsonObj = null;
					JSONParser parser = new JSONParser();
					String finalPlatoJsonString=null; 
					
					while (finalPlatoJsonString == null) {
						logger.debug("Inside while loop. Fetching template for build history id :"
								+ buildHistroyId);
						finalPlatoJsonString = MongoDBOperations
								.fetchJsonFromMongoDB(buildHistroyId);
					}
					logger.debug("Retrived data : " + finalPlatoJsonString);
					finalPlatoJsonObj = (JSONObject) parser.parse(finalPlatoJsonString);
					
					// added for read count
					finalPlatoJsonObj.replace("readCount", "1", "0");
					// -----
					JSONObject obj1 = (JSONObject) finalPlatoJsonObj.get("BuildHistory");
					JSONObject obj2 = (JSONObject) obj1.get("LiveBuildConsole");

					JSONArray subJobList = (JSONArray) obj2.get("tools");
					

					//JSONArray subJobList = (JSONArray) obj3.get("tools");

					int len = subJobList.size();
					String tool_status="";
					for (int i = 0; i < len; i++) {
						JSONObject obj4 = (JSONObject) subJobList.get(i);
						
						
						String jName = (String) obj4.get("tool_name");
						if (jName.equalsIgnoreCase(subjob_name)) {
							tool_status=(String) obj4.get("tool_status");
							if(tool_status.equalsIgnoreCase("In_Progress"))
								obj4.replace("tool_status", tool_status, "Aborted");
					
						}
						
					}
					
					MongoDBOperations.UpdateJsonInMongoDB(finalPlatoJsonObj, buildHistroyId);

					logger.debug("Updated Status to Aborted in MongoDB for build history id :"+ buildHistroyId + " and sub job name :  " + subjob_name);
					
					return finalPlatoJsonString;
					
					//DIATReportService diatReportService=new DIATReportService(buildHistoryId,buildNumber,jobName,jenkinsUrl,reportPath);
					
					/*try{
						String resp=diatReportService.readDIATJenkinsTotalResponse();
						return resp;
					}catch(Exception e){
						e.printStackTrace();
					return "Failed";
					}	*/	
				}
}
