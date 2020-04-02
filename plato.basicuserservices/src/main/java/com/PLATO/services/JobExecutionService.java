package com.PLATO.services;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.Threads.ModuleThread;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.constants.GlobalConstants;
import com.PLATO.dao.GenericDao;
import com.PLATO.entities.ModuleBuildHistory;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkinsParameters;
import com.PLATO.entities.StatusMaster;
import com.PLATO.userTO.BuildHistoryTO;
import com.PLATO.userTO.NodeTO;
import com.PLATO.utilities.PlatoJsonTemplate;



@Path("buildModule")
public class JobExecutionService
{
	private static final Logger logger=Logger.getLogger(JobExecutionService.class);
	GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	private int build_history_id;
		
	@GET
	@Path("hello")
	@Produces("text/event-stream")
	public void hello(@Context SseEventSink sseEventSink,@Context Sse sse) throws InterruptedException{
		for(int i=0;i<10;i++) {
			try {
				JSONObject obj=new JSONObject();
				obj.put("id", i);
				obj.put("name", "hello");
				ModuleBuildHistory moduleBuildHistory=new ModuleBuildHistory();
				int build_number=0;
				moduleBuildHistory.setBuild_number(build_number);
				int module_build_history_id=0;
				moduleBuildHistory.setModule_build_history_id(module_build_history_id);
				ModuleJobsJenkins moduleJobsJenkins=new ModuleJobsJenkins();
				moduleBuildHistory.setModuleJobsJenkins(moduleJobsJenkins);
				StatusMaster statusMaster=new StatusMaster();
				moduleBuildHistory.setStatusMaster(statusMaster);
				Date timestamp=new Date();
				moduleBuildHistory.setTimestamp(timestamp);
				
				OutboundSseEvent sseEvent=sse.newEventBuilder().name("event"+i).data(moduleBuildHistory).id(Integer.toString(i)).mediaType(MediaType.APPLICATION_JSON_TYPE).build();
				System.out.println("sseEvent.getData() "+sseEvent.getData());
				sseEventSink.send(sseEvent);
				Thread.sleep(2000);				
			}catch(Exception e) {
				OutboundSseEvent sseEvent=sse.newEventBuilder().name("error event").data("error").id("error").build();
				sseEventSink.send(sseEvent);
				sseEventSink.close();
				return;
			}

		}
		sseEventSink.close();

	}
	


	@GET
	@Path("executeJob/{moduleId}")
	@Produces(MediaType.TEXT_PLAIN)
	//Service to execute Job
	public Response executeJob(@PathParam("moduleId") int moduleId)
	{
		logger.info("Inside function executeJob");
		JenkinsServices jenkinsService=new JenkinsServices();
		HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
		String result="";
		ExecutorService executorService=null;

		try
		{			
			//get the required details of module
			/*keyvalueMap.put("moduleId", moduleId);
			List<Object> moduleDataList=genericDao.findByQuery(ConstantQueries.GETMODULECONFIGBYID, keyvalueMap);

			//check if the data of required module exists
			if(moduleDataList==null||moduleDataList.isEmpty())
			{
				logger.debug("Module with id :"+moduleId+" does not exist");
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}

			//map the required data to respective variables
			/*Object[] moduleData=(Object[]) moduleDataList.get(0);
			String moduleName=moduleData[1].toString();
			String moduleSubJobOrder=moduleData[2].toString();*/

			//get the required details of module
			ModuleJobsJenkins moduleToBeExecuted=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class,moduleId);
			if(moduleToBeExecuted==null)
			{
				logger.debug("Module with id :"+moduleId+" does not exist");
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}else if((moduleToBeExecuted.getModule_subjobs_order()==null || moduleToBeExecuted.getModule_subjobs_order().equalsIgnoreCase(""))) {
				logger.debug("Module with :"+moduleId+" does not contain any subJobs");
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Contain subJobs").build();				
			}
			
			/**
			 * @author 10643380(Rahul Bhardwaj)
			 * in the code snippet below we are determining weather the nodes are online
			 * if not then don't proceed with the execution
			 * */
			NodeService nodeService=new NodeService();
			List<NodeTO> nodeToList=nodeService.fectchAllNodesWithStatusForJobExecution();
//			logger.debug("found nodeList:");
//			for(NodeTO nodeTo:nodeToList) {
//				logger.debug("nodeName: "+nodeTo.getNode_name()+" nodeStatus: "+nodeTo.getStatus());
//			}
			boolean nodesOffline=false;
			String nodesNotOnline="";
			for(ModuleSubJobsJenkins subModuleToBeExecuted:moduleToBeExecuted.getModuleSubJobsJenkins()) {
				boolean nodeFound=false;
				for(NodeTO nodeTo:nodeToList) {
					logger.debug("nodeName is "+nodeTo.getNode_name()+" node status is "+nodeTo.getStatus());
					if(subModuleToBeExecuted.getNodeMaster().getNode_name().equals(nodeTo.getNode_name())) {
						nodeFound=true;
						if(!nodeTo.getStatus().equalsIgnoreCase("Online")) { //here true means the node is offline
							nodesOffline=true;
							logger.debug("node with name :\n"+nodeTo.getNode_name()+" is not online");
							nodesNotOnline+=(nodeTo.getNode_name()+",");
						}else {
							break;
						}
					}
				}
				if(!nodeFound) {
					logger.debug("node not created for job "+subModuleToBeExecuted.getSubjob_name());
					nodesOffline=true;
					nodesNotOnline+=(subModuleToBeExecuted.getNodeMaster().getNode_name()+"\n");
				}
			}
			if(nodesOffline) {
				return Response.status(Response.Status.NOT_FOUND).entity("nodes "+nodesNotOnline+" are not online").build();
			}
			/**
			 * 
			 * */
			String moduleName=moduleToBeExecuted.getJenkins_job_name();
			String moduleSubJobOrder=moduleToBeExecuted.getModule_subjobs_order();

			//getting latest build history for module 
			logger.debug("Fetching latest build history for moduleId :"+moduleId);
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleId",moduleId);
			List<Object> latestBuildHistoryList=genericDao.findByQuery(ConstantQueries.GET_LATEST_BUILD_HISTORY, keyvalueMap);
			//if another execution of same build is in progress return - "not allowed. 
			if(!(latestBuildHistoryList==null||latestBuildHistoryList.isEmpty()||latestBuildHistoryList.size()==0))
			{
				logger.info("Found entry for moduleId :"+moduleId+" buildhistory table");
				Object[] buildHistory=(Object[]) latestBuildHistoryList.get(0);
				//if buildHistoryid is null this means this is first execution of given job
				if(!(buildHistory==null||buildHistory.length==0||buildHistory[0]==null))
				{
					String buildHistoryStatus=buildHistory[1].toString();
					if(buildHistoryStatus.equalsIgnoreCase("In-Progress"))
					{
						logger.info("Another Execution of same build is already in progress");
						return Response.status(Response.Status.ACCEPTED).entity("Execution not started..Another Execution of same build already in progress").build();
					}
				}
			}


			//create entry in buildhistory table for current execution with status= In-Progress 
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("status_name","In-Progress");
			StatusMaster statusMaster=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);
			
			if(!(jenkinsService.checkJenkinsJobExist(moduleName)!=null))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}
			int nexbuildNumber=jenkinsService.fetchNextBuildNumber(moduleName);

			logger.info("creating entry in build history table with status in-progress");
			ModuleBuildHistory moduleBuildHistory=new ModuleBuildHistory();
			moduleBuildHistory.setBuild_number(nexbuildNumber);
			moduleBuildHistory.setModuleJobsJenkins(moduleToBeExecuted);
			moduleBuildHistory.setStatusMaster(statusMaster);
			moduleBuildHistory.setTimestamp(getCurrentTimestamp());
			moduleBuildHistory=(ModuleBuildHistory) genericDao.createOrUpdate(moduleBuildHistory);
			build_history_id=moduleBuildHistory.getModule_build_history_id();
			logger.debug("Created entry in build history table with id :"+build_history_id);

			//create json template in mongodb
			logger.info("Calling function to create json template in mongodb for current execution");
			PlatoJsonTemplate pt=new PlatoJsonTemplate(nexbuildNumber,moduleName,build_history_id);		
			String s=pt.createMongoTemplateFunction();
			logger.debug("Result of template creation :"+s);
			
			//convert modulesubjoborder string to arraylist
			ArrayList<String> subJobOrderList=new ArrayList<String>(Arrays.asList(moduleSubJobOrder.split(",")));
			HashMap<String,Integer> subJobBuildNumberList=new HashMap<String,Integer>();
			
			//getting list of subJob buil numbers
			for(String subJob: subJobOrderList) {
				int buildNumber=jenkinsService.fetchNextBuildNumber(subJob);
				subJobBuildNumberList.put(subJob, buildNumber);
			}

			//call jenkins service to build job in jenkins
			logger.debug("Starting Jenkins execution for module :"+moduleName);
			jenkinsService.buildJob(moduleName);
			logger.debug("Successfully Started Jenkins execution for module :"+moduleName);
			
			
			
			//if the main build has failed or is unstable and if in such case no postBuild job should be built
			if (BuildJobService.shouldPostBuildBuildJobsContinue(nexbuildNumber,moduleName,build_history_id)==false) {
				
				logger.debug("Module with :"+moduleId+"cannot continue as post build jobs cannot be built if main job has failed or is unstable");
				
				//this code is to set the status of the moduleBuildHistory as failed
				keyvalueMap=new HashMap<String,Object>();
				keyvalueMap.put("status_name","Failed");
				statusMaster=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);
				moduleBuildHistory.setStatusMaster(statusMaster);
				moduleBuildHistory=(ModuleBuildHistory) genericDao.createOrUpdate(moduleBuildHistory);
				
				return Response.status(Response.Status.NOT_FOUND).entity("main job has failed or is unstable").build();	
					
			
			}
			
			
			logger.debug("size of subJobOrderList is "+subJobOrderList.size());
			//create module thread and start it
			logger.debug("Starting module thread for module :"+moduleName);
			ModuleThread moduleThread=new ModuleThread(moduleName,subJobOrderList,build_history_id,subJobBuildNumberList);
			executorService=Executors.newCachedThreadPool();
			ArrayList<Callable<String>> moduleThreadList=new ArrayList<Callable<String>>();
			moduleThreadList.add(moduleThread);
			List<Future<String>> futureList=executorService.invokeAll(moduleThreadList);

			//get result returned by module thread and update database accordingly
			Iterator<Future<String>> itr=futureList.iterator();
			while(itr.hasNext())
			{
				result=itr.next().get();
				logger.debug("Result from ModuleThread is for moduleId: "+moduleId+" is :"+result);
				if(result.contains("Success"))
				{
					logger.info("Updating status in moduleBuildHistory to Success for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Completed");
					StatusMaster statusMasterPass=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					moduleBuildHistory.setStatusMaster(statusMasterPass);
					genericDao.createOrUpdate(moduleBuildHistory);
				}else if(result.contains("Aborted")){
					logger.info("Updating status in moduleBuildHistory to Aborted for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Aborted");
					StatusMaster statusMasterAbort=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					moduleBuildHistory.setStatusMaster(statusMasterAbort);
					genericDao.createOrUpdate(moduleBuildHistory);			
				}
				else
				{
					logger.info("Updating status in moduleBuildHistory to Failed for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Failed");
					StatusMaster statusMasterFail=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					moduleBuildHistory.setStatusMaster(statusMasterFail);
					genericDao.createOrUpdate(moduleBuildHistory);
				}
			}

			return Response.status(Response.Status.OK).entity("Execution Completed").build();
		}
		catch(Exception e)
		{ 
			logger.error("Exception in JobExecution Service : "+e);
			e.printStackTrace();
			if(e.getMessage().equalsIgnoreCase("Job does not exist"))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does not exist").build();
			}
			if(e.getMessage().equals("Jenkins build not started"))
			{
				try
				{
					logger.debug("Jenkins Job failed to start build. Changing status in BuildHistory Table to 'Failed to start' ");
					logger.info("Updating status in moduleBuildHistory to Failed-To-Start for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Failed-To-Start");
					StatusMaster statusMaster=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					ModuleBuildHistory moduleBuildHistory=(ModuleBuildHistory) genericDao.findByID(ModuleBuildHistory.class,build_history_id);
					moduleBuildHistory.setStatusMaster(statusMaster);
					genericDao.createOrUpdate(moduleBuildHistory);
				}
				catch(Exception e1)
				{
					logger.error(e);
					logger.error("Exception while changing status for unstarted Build :"+e1);
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Start Build").build();
			}

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Build Module").build();
		}
		finally
		{
			logger.debug("Inside finally closing executor");
			if(executorService!=null)
			{
				executorService.shutdown();
			}
		}
	}
	
	@GET
	@Path("getJenkinsBuildNumbers/{moduleId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getJenkinsBuildNumbers(@PathParam("moduleId") int moduleId) {
		JenkinsServices jenkinsService=new JenkinsServices();
		try {
			ModuleJobsJenkins moduleToBeExecuted=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class,moduleId);
			Set<ModuleSubJobsJenkins> subModulesToBeExecuted=moduleToBeExecuted.getModuleSubJobsJenkins();
			for(ModuleSubJobsJenkins subModule:subModulesToBeExecuted) {
				boolean foundJenkinsBuildNo=false;
				List<ModuleSubJobsJenkinsParameters> moduleSubJobsJenkinsParameters= subModule.getModuleSubJobsJenkinsParametersList();
				for(ModuleSubJobsJenkinsParameters parameter:moduleSubJobsJenkinsParameters) {
					if(parameter.getParameter_key().equalsIgnoreCase("jenkins_build_no")){
						foundJenkinsBuildNo=true;
						parameter.setValue(Integer.toString(jenkinsService.fetchNextBuildNumber(subModule.getSubjob_name())));
						genericDao.createOrUpdate(parameter);
						break;
					}
				}
				if(foundJenkinsBuildNo==false) {
					ModuleSubJobsJenkinsParameters parameter=new ModuleSubJobsJenkinsParameters();
					parameter.setModuleSubJobsJenkins(subModule);
					parameter.setParameter_key("jenkins_build_no");
					parameter.setValue(Integer.toString(jenkinsService.fetchNextBuildNumber(subModule.getSubjob_name())));
					genericDao.createOrUpdate(parameter);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.OK).entity("OK").build();
	}

	@GET
	@Path("executeJobNoJenkinsTrigger/{moduleId}/{buildId}")
	@Produces(MediaType.TEXT_PLAIN)
	//Service to execute Job
	public Response executeJobNoJenkinsTrigger(@PathParam("moduleId") int moduleId,@PathParam("buildId") int buildId)
	{
		logger.info("Inside function executeJob");
		JenkinsServices jenkinsService=new JenkinsServices();
		HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
		String result="";
		ExecutorService executorService=null;

		try
		{			
			//get the required details of module
			/*keyvalueMap.put("moduleId", moduleId);
			List<Object> moduleDataList=genericDao.findByQuery(ConstantQueries.GETMODULECONFIGBYID, keyvalueMap);

			//check if the data of required module exists
			if(moduleDataList==null||moduleDataList.isEmpty())
			{
				logger.debug("Module with id :"+moduleId+" does not exist");
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}

			//map the required data to respective variables
			/*Object[] moduleData=(Object[]) moduleDataList.get(0);
			String moduleName=moduleData[1].toString();
			String moduleSubJobOrder=moduleData[2].toString();*/

			//get the required details of module
			ModuleJobsJenkins moduleToBeExecuted=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class,moduleId);
			//
			String query="FROM ModuleBuildHistory mbh WHERE mbh.moduleJobsJenkins.jenkins_job_id=:moduleId AND mbh.build_number=:buildId";
			keyvalueMap.put("moduleId",moduleId);
			keyvalueMap.put("buildId", buildId);
			List<Object> histList=(List<Object>)genericDao.findByQuery(query, keyvalueMap);
			if(histList.size()!=0) {
				return Response.status(Response.Status.NOT_FOUND).entity("Module executed in plato...").build();
			}
			keyvalueMap=new HashMap<String,Object>();
			//
			if(moduleToBeExecuted==null)
			{
				logger.debug("Module with id :"+moduleId+" does not exist");
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}else if((moduleToBeExecuted.getModule_subjobs_order()==null || moduleToBeExecuted.getModule_subjobs_order().equalsIgnoreCase(""))) {
				logger.debug("Module with :"+moduleId+" does not contain any subJobs");
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Contain jubJobs").build();				
			}
			
			/**
			 * @author 10643380(Rahul Bhardwaj)
			 * in the code snippet below we are determining weather the nodes are online
			 * if not then don't proceed with the execution
			 * */
			NodeService nodeService=new NodeService();
			List<NodeTO> nodeToList=nodeService.fectchAllNodesWithStatusForJobExecution();
//			logger.debug("found nodeList:");
//			for(NodeTO nodeTo:nodeToList) {
//				logger.debug("nodeName: "+nodeTo.getNode_name()+" nodeStatus: "+nodeTo.getStatus());
//			}
			boolean nodesOffline=false;
			String nodesNotOnline="";
			for(ModuleSubJobsJenkins subModuleToBeExecuted:moduleToBeExecuted.getModuleSubJobsJenkins()) {
				boolean nodeFound=false;
				for(NodeTO nodeTo:nodeToList) {
					logger.debug("nodeName is "+nodeTo.getNode_name()+" node status is "+nodeTo.getStatus());
					if(subModuleToBeExecuted.getNodeMaster().getNode_name().equals(nodeTo.getNode_name())) {
						nodeFound=true;
						if(!nodeTo.getStatus().equalsIgnoreCase("Online")) { //here true means the node is offline
							nodesOffline=true;
							logger.debug("node with name :\n"+nodeTo.getNode_name()+" is not online");
							nodesNotOnline+=(nodeTo.getNode_name()+",");
						}else {
							break;
						}
					}
				}
				if(!nodeFound) {
					logger.debug("node not created for job "+subModuleToBeExecuted.getSubjob_name());
					nodesOffline=true;
					nodesNotOnline+=(subModuleToBeExecuted.getNodeMaster().getNode_name()+"\n");
				}
			}
			if(nodesOffline) {
				return Response.status(Response.Status.NOT_FOUND).entity("nodes "+nodesNotOnline+" are not online").build();
			}
			/**
			 * 
			 * */
			String moduleName=moduleToBeExecuted.getJenkins_job_name();
			String moduleSubJobOrder=moduleToBeExecuted.getModule_subjobs_order();

			//getting latest build history for module 
			logger.debug("Fetching latest build history for moduleId :"+moduleId);
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleId",moduleId);
			List<Object> latestBuildHistoryList=genericDao.findByQuery(ConstantQueries.GET_LATEST_BUILD_HISTORY, keyvalueMap);
			//if another execution of same build is in progress return - "not allowed. 
			if(!(latestBuildHistoryList==null||latestBuildHistoryList.isEmpty()||latestBuildHistoryList.size()==0))
			{
				logger.info("Found entry for moduleId :"+moduleId+" buildhistory table");
				Object[] buildHistory=(Object[]) latestBuildHistoryList.get(0);
				//if buildHistoryid is null this means this is first execution of given job
				if(!(buildHistory==null||buildHistory.length==0||buildHistory[0]==null))
				{
					String buildHistoryStatus=buildHistory[1].toString();
					if(buildHistoryStatus.equalsIgnoreCase("In-Progress"))
					{
						logger.info("Another Execution of same build is already in progress");
						return Response.status(Response.Status.ACCEPTED).entity("Execution not started..Another Execution of same build already in progress").build();
					}
				}
			}


			//create entry in buildhistory table for current execution with status= In-Progress 
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("status_name","In-Progress");
			StatusMaster statusMaster=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);
			
			if(!(jenkinsService.checkJenkinsJobExist(moduleName)!=null))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}
			int nexbuildNumber=jenkinsService.fetchLastBuildNumber(moduleName);
			System.out.println("mainJob nexbuildNumber "+nexbuildNumber);

			logger.info("creating entry in build history table with status in-progress");
			ModuleBuildHistory moduleBuildHistory=new ModuleBuildHistory();
			moduleBuildHistory.setBuild_number(nexbuildNumber);
			moduleBuildHistory.setModuleJobsJenkins(moduleToBeExecuted);
			moduleBuildHistory.setStatusMaster(statusMaster);
			moduleBuildHistory.setTimestamp(getCurrentTimestamp());
			moduleBuildHistory=(ModuleBuildHistory) genericDao.createOrUpdate(moduleBuildHistory);
			build_history_id=moduleBuildHistory.getModule_build_history_id();
			logger.debug("Created entry in build history table with id :"+build_history_id);

			//create json template in mongodb
			logger.info("Calling function to create json template in mongodb for current execution");
			PlatoJsonTemplate pt=new PlatoJsonTemplate(nexbuildNumber,moduleName,build_history_id);		
			String s=pt.createMongoTemplateFunction();
			logger.debug("Result of template creation :"+s);
			
			//convert modulesubjoborder string to arraylist
			ArrayList<String> subJobOrderList=new ArrayList<String>(Arrays.asList(moduleSubJobOrder.split(",")));
			HashMap<String,Integer> subJobBuildNumberList=new HashMap<String,Integer>();
			
			//getting list of subJob buil numbers
			Set<ModuleSubJobsJenkins> subModulesToBeExecuted=moduleToBeExecuted.getModuleSubJobsJenkins();
			for(ModuleSubJobsJenkins subModule:subModulesToBeExecuted) {
				int buildNumber=0;
				boolean foundJenkinsBuildNo=false;
				List<ModuleSubJobsJenkinsParameters> moduleSubJobsJenkinsParameters= subModule.getModuleSubJobsJenkinsParametersList();
				for(ModuleSubJobsJenkinsParameters parameter:moduleSubJobsJenkinsParameters) {
					if(parameter.getParameter_key().equalsIgnoreCase("jenkins_build_no")){
						foundJenkinsBuildNo=true;
						buildNumber=Integer.parseInt(parameter.getValue());
						break;
					}
				}
				if(foundJenkinsBuildNo==false) {
					ModuleSubJobsJenkinsParameters parameter=new ModuleSubJobsJenkinsParameters();
					parameter.setModuleSubJobsJenkins(subModule);
					parameter.setParameter_key("jenkins_build_no");
					parameter.setValue(Integer.toString(jenkinsService.fetchNextBuildNumber(subModule.getSubjob_name())));
					genericDao.createOrUpdate(parameter);
					buildNumber=Integer.parseInt(parameter.getValue());
				}
				System.out.println("subJob "+subModule.getSubjob_name()+" buildNumber "+buildNumber);
				subJobBuildNumberList.put(subModule.getSubjob_name(), buildNumber);

			}
//			for(String subJob: subJobOrderList) {
//				int buildNumber=jenkinsService.fetchLastBuildNumber(subJob);
//				System.out.println("subJob "+subJob+" buildNumber "+buildNumber);
//				subJobBuildNumberList.put(subJob, buildNumber);
//			}

			//call jenkins service to build job in jenkins
//			logger.debug("Starting Jenkins execution for module :"+moduleName);
//			jenkinsService.buildJob(moduleName);
//			logger.debug("Successfully Started Jenkins execution for module :"+moduleName);
			
			
			
			//if the main build has failed or is unstable and if in such case no postBuild job should be built
			if (BuildJobService.shouldPostBuildBuildJobsContinue(nexbuildNumber,moduleName,build_history_id)==false) {
				
				logger.debug("Module with :"+moduleId+"cannot continue as post build jobs cannot be built if main job has failed or is unstable");
				
				//this code is to set the status of the moduleBuildHistory as failed
				keyvalueMap=new HashMap<String,Object>();
				keyvalueMap.put("status_name","Failed");
				statusMaster=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);
				moduleBuildHistory.setStatusMaster(statusMaster);
				moduleBuildHistory=(ModuleBuildHistory) genericDao.createOrUpdate(moduleBuildHistory);
				
				return Response.status(Response.Status.NOT_FOUND).entity("main job has failed or is unstable").build();	
					
			
			}
			
			
			logger.debug("size of subJobOrderList is "+subJobOrderList.size());
			//create module thread and start it
			logger.debug("Starting module thread for module :"+moduleName);
			ModuleThread moduleThread=new ModuleThread(moduleName,subJobOrderList,build_history_id,subJobBuildNumberList);
			executorService=Executors.newCachedThreadPool();
			ArrayList<Callable<String>> moduleThreadList=new ArrayList<Callable<String>>();
			moduleThreadList.add(moduleThread);
			List<Future<String>> futureList=executorService.invokeAll(moduleThreadList);

			//get result returned by module thread and update database accordingly
			Iterator<Future<String>> itr=futureList.iterator();
			while(itr.hasNext())
			{
				result=itr.next().get();
				logger.debug("Result from ModuleThread is for moduleId: "+moduleId+" is :"+result);
				if(result.contains("Success"))
				{
					logger.info("Updating status in moduleBuildHistory to Success for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Completed");
					StatusMaster statusMasterPass=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					moduleBuildHistory.setStatusMaster(statusMasterPass);
					genericDao.createOrUpdate(moduleBuildHistory);
				}else if(result.contains("Aborted")){
					logger.info("Updating status in moduleBuildHistory to Aborted for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Aborted");
					StatusMaster statusMasterAbort=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					moduleBuildHistory.setStatusMaster(statusMasterAbort);
					genericDao.createOrUpdate(moduleBuildHistory);			
				}
				else
				{
					logger.info("Updating status in moduleBuildHistory to Failed for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Failed");
					StatusMaster statusMasterFail=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					moduleBuildHistory.setStatusMaster(statusMasterFail);
					genericDao.createOrUpdate(moduleBuildHistory);
				}
			}

			return Response.status(Response.Status.OK).entity("Execution Completed").build();
		}
		catch(Exception e)
		{ 
			logger.error("Exception in JobExecution Service : "+e);
			e.printStackTrace();
			if(e.getMessage().equalsIgnoreCase("Job does not exist"))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does not exist").build();
			}
			if(e.getMessage().equals("Jenkins build not started"))
			{
				try
				{
					logger.debug("Jenkins Job failed to start build. Changing status in BuildHistory Table to 'Failed to start' ");
					logger.info("Updating status in moduleBuildHistory to Failed-To-Start for buildHistoryId :"+build_history_id);
					keyvalueMap=new HashMap<String,Object>();
					keyvalueMap.put("status_name","Failed-To-Start");
					StatusMaster statusMaster=(StatusMaster) genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);					
					ModuleBuildHistory moduleBuildHistory=(ModuleBuildHistory) genericDao.findByID(ModuleBuildHistory.class,build_history_id);
					moduleBuildHistory.setStatusMaster(statusMaster);
					genericDao.createOrUpdate(moduleBuildHistory);
				}
				catch(Exception e1)
				{
					logger.error(e);
					logger.error("Exception while changing status for unstarted Build :"+e1);
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Start Build").build();
			}

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Build Module").build();
		}
		finally
		{
			logger.debug("Inside finally closing executor");
			if(executorService!=null)
			{
				executorService.shutdown();
			}
		}
	}
	
	@GET
	@Path("abortBuild/{buildHistoryId}")
	@Produces(MediaType.TEXT_PLAIN)
	//Service to execute Job
	public Response abortBuild(@PathParam("buildHistoryId") int buildHistoryId)
	{
		logger.info("Inside function abortBuild");
		JenkinsServices jenkinsService=new JenkinsServices();
		HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
		String result="";
		
		try
		{			
			//get the required details of module
			
			keyvalueMap.put("buildHistoryId", buildHistoryId);
			//keyvalueMap.put("buildNumber", build_number);
			
			ModuleBuildHistory mbh =(ModuleBuildHistory)genericDao.findUniqueByQuery(ConstantQueries.GET_MODULE_HISTORY_DATA, keyvalueMap);

			int jobId=mbh.getModuleJobsJenkins().getJenkins_job_id();
			//String jobName=mbh.getModuleJobsJenkins().getJenkins_job_name();
			
			keyvalueMap.clear();
			keyvalueMap.put("jobId", jobId);
			//keyvalueMap.put("moduleName", jobName);
			

			ModuleSubJobsJenkins msjj;
			List<Object> listOfSubjobs=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ALL_SUBMODULES, keyvalueMap);
			
			if (listOfSubjobs == null || listOfSubjobs.size() == 0
					|| listOfSubjobs.isEmpty() == true) {
				logger.error("Projects not found");

				System.out.println("Projects not found");
				
				return Response.status(Response.Status.NOT_FOUND)
						.entity("SubJobs Dont Exist in Module").build();
			}
			
			
			for(int i=0;i<listOfSubjobs.size();i++){
				
				String sub_job_name="";
				int build_number;
				
				msjj = (ModuleSubJobsJenkins) listOfSubjobs.get(i);
				
				sub_job_name=msjj.getSubjob_name();
				
				build_number=jenkinsService.fetchNextBuildNumber(sub_job_name)-1;

				if(build_number!=0){
					try {
				result=jenkinsService.abortJob(sub_job_name, build_number);
				updateStatus(jobId,sub_job_name,build_number);
					}catch(Exception e) {
						updateStatus(jobId,sub_job_name,build_number);
						logger.error("Job has completed execution so couldnt abort");
						e.printStackTrace();
					}
				}else{
					result="Successfully Aborted Job";
					updateStatus(jobId,sub_job_name,build_number);
					logger.error("Build hasnt started but Aborted by user");
				}
				
			}
			
			keyvalueMap.clear();
			keyvalueMap.put("status_name","Aborted");
			StatusMaster sm=(StatusMaster)genericDao.findUniqueByQuery(ConstantQueries.GETSTATUSBYNAME, keyvalueMap);

		
			if(result.equalsIgnoreCase("Successfully Aborted Job")){
				
				mbh.setStatusMaster(sm);
				
				
				ModuleBuildHistory updatedModuleBH = (ModuleBuildHistory) GenericDaoSingleton
						.getGenericDao().createOrUpdate(mbh);

				if (updatedModuleBH != null) {
					//String read_fast_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/updateMainJobAbortStatus/"+buildHistoryId;
					//System.out.println(read_fast_console);
					return Response.status(Response.Status.OK).entity("Job Aborted Successfully").build();
				} else {
					return Response.status(Response.Status.BAD_REQUEST)
							.entity("Couldnt Update Status in Module Build Histroy").build();

				}
			
			
			}else
			{
				return Response.status(Response.Status.OK).entity("Couldnt Abort Job").build();
			}
			
		
		
		
		
		}
		catch(Exception e)
		{ 
			logger.error("Exception in JobExecution Service : "+e);
			e.printStackTrace();			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Abort Sub Job").build();
		}
		
	}
	
		
	public Date getCurrentTimestamp() throws ParseException
	{
		long currentTime=System.currentTimeMillis();
		//creating Date from millisecond
		DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
		String dateString=df.format(currentTime);
		return df.parse(dateString);
	}
	
	
	@GET
	@Path("abortJob/{sub_job_name}/{build_number}")
	@Produces(MediaType.TEXT_PLAIN)
	//Service to execute Job
	public Response abortJob(@PathParam("sub_job_name") String sub_job_name,@PathParam("build_number") int build_number) throws Exception
	{
		logger.info("Inside function abortJob");
		JenkinsServices jenkinsService=new JenkinsServices();
		HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
		String result="";
		int jobId=0;
		try
		{			
			//get the required details of module
			
			keyvalueMap.put("subJobName", sub_job_name);
			//keyvalueMap.put("buildNumber", build_number);
			

			//List<Object> moduleDataList=genericDao.findByQuery(ConstantQueries.GETSUBMODULEDATA, keyvalueMap);

			ModuleSubJobsJenkins msjj=(ModuleSubJobsJenkins)genericDao.findUniqueByQuery(ConstantQueries.GETSUBMODULEBYNAME, keyvalueMap);
			//check if the data of required module exists
			if(msjj==null)
			{
				logger.debug("Sub Job with name :"+sub_job_name+" does not exist");
				return Response.status(Response.Status.NOT_FOUND).entity("Job Does Not Exist").build();
			}

			jobId=msjj.getModuleJobsJenkins().getJenkins_job_id();
			
			keyvalueMap.clear();
			keyvalueMap.put("jenkins_job_id",jobId);
			
			result=jenkinsService.abortJob(sub_job_name, build_number);
			
						
			
			updateStatus(jobId,sub_job_name,build_number);
			
			//ModuleBuildHistory mbh=(ModuleBuildHistory) genericDao.findUniqueByQuery(ConstantQueries.GET_MBH, keyvalueMap);
			
			if(result.contains("Successfully Aborted Job")){				
			return Response.status(Response.Status.OK).entity("Job Aborted Successfully").build();
			}else
			{
				return Response.status(Response.Status.OK).entity("Couldnt Abort Job").build();
			}
			
		}
		catch(Exception e)
		{ 
			logger.error("Exception in JobExecution Service : "+e);
			updateStatus(jobId,sub_job_name,build_number);
			e.printStackTrace();			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Abort Sub Job").build();
		}
		
	}
	
	void updateStatus(int jenkins_job_id, String sub_job_name,int buildNumber) throws Exception {
		System.out.println(jenkins_job_id);
		System.out.println(sub_job_name);

		HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
		keyvalueMap.put("jenkins_job_id", jenkins_job_id);
		keyvalueMap.put("build_number", buildNumber);

		List<Object> moduleBuildHistory=genericDao.findByQuery(ConstantQueries.GET_MBH, keyvalueMap);
		System.out.println(moduleBuildHistory);
		int mbh= (int) moduleBuildHistory.get(0);
		System.out.println(mbh);
		String url_to_abort=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/updateParticularSubJobAbortStatus/"+mbh+"/"+sub_job_name;
		System.out.println(url_to_abort);
		
		URL url = new URL(url_to_abort);
		System.out.println("ABORTING URL IS:  "+url);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("GET");

		// reading the response
		InputStreamReader reader = new InputStreamReader( con.getInputStream() );
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[ 2048 ];
		int num;
		while ( -1 != (num=reader.read( cbuf )))
		{
			buf.append( cbuf, 0, num );
		}
		String result = buf.toString();
		System.out.println(result);
	}
	
	/**
	 * @author 10643380(Rahul Bhardwaj)
	 * */
	@GET
	@Path("getIfModuleInProgress/{moduleName}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getIfModuleInProgress(@PathParam("moduleName") String moduleName){
		List<BuildHistoryTO> buildHistoryTOList=new ArrayList<BuildHistoryTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
		try
		{
			keyvalueMap.put("moduleName", moduleName);
			List<Object> buildHistoryList= genericDao.findByQuery(ConstantQueries.GETBUILDHISTORYFORMODULE, keyvalueMap);
			if(buildHistoryList==null||buildHistoryList.size()==0||buildHistoryList.isEmpty()==true)
			{
				logger.info("Build History not found");
				buildHistoryTOList=null;
				return Response.status(Response.Status.OK).entity("Build History not found").build();
			}
			Iterator<Object> it=buildHistoryList.iterator();
			while(it.hasNext())
			{

				BuildHistoryTO buildHistoryTO=new BuildHistoryTO();
				Object[] buildHistory=(Object[])it.next();
				String dateString=dateFormat.format(buildHistory[2]);

				buildHistoryTO.setBuildHistoryId(Integer.parseInt(buildHistory[0].toString()));
				buildHistoryTO.setBuildNumber(buildHistory[1].toString());
				//buildHistoryTO.setTimestamp(dateFormat.parse(dateString));
				buildHistoryTO.setTimestamp(dateString);
				buildHistoryTO.setStatus(buildHistory[3].toString());	

				buildHistoryTOList.add(buildHistoryTO);
			}
			logger.debug("Returning build history for module :"+moduleName);
			return Response.status(Response.Status.OK).entity(buildHistoryTOList).build();
		}

		catch(Exception e)
		{
			logger.error("Error in getting build history in Module page service :"+e.getMessage());
			e.printStackTrace();
			buildHistoryTOList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(buildHistoryTOList).build();
		}	
	}
	/**
	 * @author 10643380(Rahul Bhardwaj)
	 * */
	@GET
	@Path("refresh/{subjobName}/{buildHistoryId}")
	@Produces()
	public Response refresh(@PathParam("subjobName") String subjobName,@PathParam("buildHistoryId") int buildHistoryId) {
		JenkinsServices jenkinsService=new JenkinsServices();
		NodeService nodeService=new NodeService();
//		try {
//			List<NodeTO> nodeToList=nodeService.fectchAllNodesWithStatusForJobExecution();
//			boolean nodesOffline=false;
//			for(NodeTO nodeTO:nodeToList) {
//				if(nodeTO.getNode_name().equalsIgnoreCase("nodeName")) {
//					nodesOffline=true;
//					break;
//				}
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error").build();
//		}		
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		String query="from ModuleBuildHistory where module_build_history_id=:buildHistoryId";
		keyvalueMap.put("buildHistoryId",buildHistoryId);
		HttpURLConnection con=null;
		InputStreamReader reader=null;
		try {
			List<Object> buildHistoryListObjList=genericDao.findByQuery(query, keyvalueMap);
			ModuleBuildHistory moduleBuildHistory=(ModuleBuildHistory)buildHistoryListObjList.get(0);
			
			query="from ModuleSubJobsJenkins where subjob_name=:subjobName";
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("subjobName",subjobName);
			List<Object> moduleSubJobsObjList=genericDao.findByQuery(query, keyvalueMap);
			ModuleSubJobsJenkins moduleSubJobsJenkins=(ModuleSubJobsJenkins)moduleSubJobsObjList.get(0);
			//
			List<NodeTO> nodeToList=nodeService.fectchAllNodesWithStatusForJobExecution();
			boolean nodeOffline=false;
			boolean nodeFound=false;
			for(NodeTO nodeTO:nodeToList) {
				if(moduleSubJobsJenkins.getNodeMaster().getNode_name().equalsIgnoreCase(nodeTO.getNode_name())) {
					nodeFound=true;
					if(nodeTO.getStatus().equals("online")) {
						nodeOffline=true;
					}
					break;
				}
			}
			if(!nodeFound) {
				return Response.status(Response.Status.NOT_FOUND).entity("node not found").build();
			}else if(nodeOffline) {
				return Response.status(Response.Status.NOT_FOUND).entity("node is not online").build();				
			}
			
			int nextBuildNumber=jenkinsService.fetchNextBuildNumber(moduleSubJobsJenkins.getSubjob_name());
			jenkinsService.buildJob(moduleSubJobsJenkins.getSubjob_name());
			//
			String threadName=subjobName;
			threadName=threadName.replaceAll(" ", "%20");
			String toolName=moduleSubJobsJenkins.getTool_name();
			toolName=toolName.replaceAll(" ", "_");
			String reportPath=moduleSubJobsJenkins.getReport_path();
			reportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+moduleSubJobsJenkins.getSubjob_name()+"/";
			reportPath=reportPath+nextBuildNumber+"/";
			reportPath=reportPath.replace(" ","%20");
			String read_GenericCommandJob_console=GlobalConstants.JENKINS_CONSOLE_SERVICES_URL+":"+GlobalConstants.JENKINS_CONSOLE_SERVICES_PORT+"/PlatoBuildMongoDBService/PlatoMongoTemplate/genericCommandReportService/"+buildHistoryId+"/"+threadName+"/"+nextBuildNumber+"/avc/"+toolName+"/?reportPath="+reportPath;
			
			logger.debug("URL is :"+read_GenericCommandJob_console);
			URL url = new URL(read_GenericCommandJob_console);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("GET");
			
			reader = new InputStreamReader( con.getInputStream() );
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[ 2048 ];
			int num;
			while ( -1 != (num=reader.read( cbuf )))
			{
				buf.append( cbuf, 0, num );
			}
			String result = buf.toString();
			logger.debug( "\nResponse from server after POST:\n" + result );
			logger.info("Returning Result");
			result=result+" :"+threadName;
			return Response.status(Response.Status.OK).entity(result).build();
		}catch(Exception e) {
			e.printStackTrace();
		}

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error").build();
	}
	

}
