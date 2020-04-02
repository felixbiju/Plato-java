package com.PLATO.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.Singletons.JenkinsDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.dao.GenericDao;
import com.PLATO.dao.JenkinsDao;
import com.PLATO.daoimpl.GenericDaoImpl;
import com.PLATO.entities.ModuleBuildHistory;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleJobsJenkinsParameters;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkinsParameters;
import com.PLATO.entities.NodeMaster;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.userTO.BuildHistoryTO;
import com.PLATO.userTO.ModuleConfigTO;
import com.PLATO.userTO.ModuleTO;
import com.PLATO.userTO.SubModuleParametersTO;
import com.PLATO.userTO.SubModuleTO;
import com.PLATO.userTO.ToolOrderTO;
import com.PLATO.userTO.ToolTO;
import com.PLATO.utilities.GenericDBUtil;


@Path("ModulePage")
public class ModulePageService
{
	/*
	 * Description : Service to perform various operation on module page
	 * Author: Gaurav Kulkarni
	 * 
	 * */

	GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	JenkinsDao jenkinsDao= JenkinsDaoSingleton.getJenkinsDao();
	private static final Logger logger=Logger.getLogger(ModulePageService.class);
	
	/**
	 * @author 10643380(Rahul Bhardwaj)
	 * */
	@GET
	@Path("getAllModules")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllModules() {
		List<ModuleTO> moduleTOList=new ArrayList<ModuleTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		try {
			String query="from ModuleJobsJenkins";
			List<Object> moduleNames=genericDao.findByQuery(query, keyvalueMap);
			if(moduleNames==null|| moduleNames.size()==0 || moduleNames.isEmpty()==true)
			{
				logger.error("Modules not found");
				moduleTOList=null;
				return Response.status(Response.Status.NOT_FOUND).entity(moduleTOList).build();
			}
			Iterator<Object> it=moduleNames.iterator();
			while(it.hasNext())
			{
				ModuleTO moduleTO=new ModuleTO();
				ModuleJobsJenkins moduleDetails=(ModuleJobsJenkins)it.next();
				logger.info("moduleJobsJenkins Parameters "+moduleDetails.getModuleJobsJenkinsParametersList().toString());
				moduleTO.setModule_Id(moduleDetails.getJenkins_job_id());
				moduleTO.setModuleName(moduleDetails.getJenkins_job_name());	
				moduleTO.setModuleDescription(moduleDetails.getDescription());

				moduleTOList.add(moduleTO);
			}
			logger.info("Returning the retrieved module list");
			return Response.status(Response.Status.OK).entity(moduleTOList).build();
		}catch(Exception e) {
			moduleTOList=null;
			logger.error("Error while getting modules for project in Module page service :"+e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleTOList).build();			
		}
	}

	@GET
	@Path("getModuleNameList/{projectName}")
	@Produces(MediaType.APPLICATION_JSON)
	//Get the names of modules
	public Response getModuleList(@PathParam("projectName") String projectName)
	{  	 
		logger.info("inside the function getModuleList");
		logger.debug("got projectName parameter with value :"+projectName);
		logger.debug("Getting module list for project :"+projectName);
		List<ModuleTO> moduleTOList=new ArrayList<ModuleTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		
		try
		{
			keyvalueMap.put("project_name", projectName);
			List<Object> moduleNames=genericDao.findByQuery(ConstantQueries.GETMODULENAMELIST, keyvalueMap);
			if(moduleNames==null|| moduleNames.size()==0 || moduleNames.isEmpty()==true)
			{
				logger.error("Modules not found");
				moduleTOList=null;
				return Response.status(Response.Status.NOT_FOUND).entity(moduleTOList).build();
			}

			Iterator<Object> it=moduleNames.iterator();
			while(it.hasNext())
			{
				ModuleTO moduleTO=new ModuleTO();
				Object[] moduleDetails=(Object[])it.next();
				moduleTO.setModule_Id(Integer.parseInt(moduleDetails[0].toString()));
				moduleTO.setModuleName(moduleDetails[1].toString());	
				moduleTO.setModuleDescription(moduleDetails[2].toString());

				moduleTOList.add(moduleTO);
			}
			logger.info("Returning the retrieved module list");
			return Response.status(Response.Status.OK).entity(moduleTOList).build();
		}
		catch(Exception e)
		{
			moduleTOList=null;
			logger.error("Error while getting modules for project in Module page service :"+e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleTOList).build();
		}



	}

	@GET
	@Path("toolList/{projectId}")
	//@RolesAllowed({"admin","user"})
	@Produces(MediaType.APPLICATION_JSON)
	//Returns the list of all tools
	public Response getToolList(@PathParam("projectId") String projectId)
	{ 
		logger.info("inside the function getToolList");
		logger.debug("got projectId parameter with value :"+projectId);
		logger.debug("Getting tool list for project :"+projectId);
		//Map<String,ToolOrderTO> toolCategoryMap=new HashMap<String,ToolOrderTO>();
		ArrayList<ToolOrderTO> toolCategoryArray=new ArrayList<ToolOrderTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		ToolOrderTO toolOrderTo=null; 
		/*List<ToolOrderTO> toolOrderList=null;
		ToolOrderTO buildConfiguration=new ToolOrderTO(1,new HashSet<ToolTO>());
		ToolOrderTO automationStack=new ToolOrderTO(2,new HashSet<ToolTO>());
		ToolOrderTO dataAssuranceStack=new ToolOrderTO(3,new HashSet<ToolTO>());
		ToolOrderTO performanceStack=new ToolOrderTO(4,new HashSet<ToolTO>());
		ToolOrderTO securityStack=new ToolOrderTO(5,new HashSet<ToolTO>());
		ToolOrderTO Notification=new ToolOrderTO(6,new HashSet<ToolTO>());*/
		
		try
		{
			logger.info("Getting tool List");
			keyvalueMap.put("projectId", Integer.parseInt(projectId));
			List<Object> toolCategoryList= genericDao.findByQuery(ConstantQueries.GETCATEGORYWISETOOLS, keyvalueMap);
			if(toolCategoryList.size()==0||toolCategoryList==null)
			{
				toolCategoryArray=null;
				//toolCategoryMap=null;
				logger.error("tools Not Found");
				return Response.status(Response.Status.OK).entity("Tools not Mapped").build();
			}

			Iterator<Object> it=toolCategoryList.iterator();
			while(it.hasNext())
			{
				ToolTO toolTO=new ToolTO();

				Object[] toolDetails=(Object[])it.next();
				int category_id=Integer.parseInt(toolDetails[0].toString());
				String category_name=toolDetails[1].toString();

				toolTO.setCategory_id(category_id);
				toolTO.setTool_id(Integer.parseInt(toolDetails[2].toString()));
				toolTO.setTool_name(toolDetails[3].toString());
				toolTO.setCommand_to_execute(toolDetails[4].toString());
				toolTO.setReport_path(toolDetails[5].toString());
				//toolTO.setTool_logo(toolDetails[4].toString().getBytes());
				toolTO.setNode_id(Integer.parseInt(toolDetails[6].toString()));
				toolTO.setNode_name(toolDetails[7].toString());
				if(toolCategoryArray.isEmpty()) {
					HashSet<ToolTO> toolToSet=new HashSet<ToolTO>();
					toolToSet.add(toolTO);
					toolCategoryArray.add(new ToolOrderTO(category_id,category_name,toolToSet));
				}
				for(int i=0;i<toolCategoryArray.size();i++) {
					ToolOrderTO temp=toolCategoryArray.get(i);
					if(temp.getCategoryName().equals(category_name)) {
						HashSet<ToolTO> toolToSet=(HashSet<ToolTO>)temp.getToolToSet();
						toolToSet.add(toolTO);
						break;
					}else if(i==toolCategoryArray.size()-1) {
						toolOrderTo=new ToolOrderTO(category_id,category_name,new HashSet<ToolTO>());
						HashSet<ToolTO> toolToSet=(HashSet<ToolTO>)toolOrderTo.getToolToSet();
						toolToSet.add(toolTO);
						toolCategoryArray.add(toolOrderTo);
					}
				}
//				if(toolCategoryMap.containsKey(category_name))
//				{
//					toolOrderTo=toolCategoryMap.get(category_name);
//				}
//				else
//				{
//					toolOrderTo=new ToolOrderTO(category_id,category_name,new HashSet<ToolTO>());
//				}
//				HashSet<ToolTO> toolToSet=(HashSet<ToolTO>)toolOrderTo.getToolTo();
//				toolToSet.add(toolTO);
				//toolList.add(toolTO);
				//toolCategoryMap.put(category_name,toolOrderTo);
				logger.debug("Tools successfully retrieved for project id :"+projectId);	

			}
		}
		catch(Exception e)
		{
			logger.error("Exception in getting tools List in Module page service"+e.getMessage());
			e.printStackTrace();
			toolCategoryArray=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(toolCategoryArray).build();
		}
		return Response.status(Response.Status.OK).entity(toolCategoryArray).build();


	}

	/*@GET
	@Path("toolList/{projectId}")
	@RolesAllowed({"admin","user"})
	@Produces(MediaType.APPLICATION_JSON)
	//Returns the list of all tools
	public Response getToolList(@PathParam("projectId") String projectId)
	{ 
		System.out.println("Getting tool list for project :"+projectId);
		Map<String,List<ToolTO>> toolCategoryMap=new HashMap<String,List<ToolTO>>(); 
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		List<ToolTO> toolList=new ArrayList<ToolTO>(); 
		try
		{
			System.out.println("Getting tool List");
			keyvalueMap.put("projectId", Integer.parseInt(projectId));
			List<Object> toolCategoryList= genericDao.findByQuery(ConstantQueries.GETCATEGORYWISETOOLS, keyvalueMap);
			if(toolCategoryList==null||toolCategoryList.size()==0||toolCategoryList.isEmpty()==true)
			{
				toolCategoryMap=null;
				toolList=null;
				System.out.println("Tools Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(toolList).build();
			}

			Iterator<Object> it=toolCategoryList.iterator();
			while(it.hasNext())
			{
				ToolTO toolTO=new ToolTO();

				Object[] toolDetails=(Object[])it.next();
				toolTO.setCategory_id(Integer.parseInt(toolDetails[0].toString()));
				toolTO.setCategory_name(toolDetails[1].toString());

				toolTO.setTool_id(Integer.parseInt(toolDetails[2].toString()));
				toolTO.setTool_name(toolDetails[3].toString());
				toolTO.setCommand_to_execute(toolDetails[4].toString());
				toolTO.setReport_path(toolDetails[5].toString());
				//toolTO.setTool_logo(toolDetails[4].toString());
				toolTO.setNode_id(Integer.parseInt(toolDetails[7].toString()));
				toolTO.setNode_name(toolDetails[8].toString());	
				toolList.add(toolTO);
			}
			return Response.status(Response.Status.OK).entity(toolList).build();
		}
		catch(Exception e)
		{
			System.out.println("Exception in getting tools List in Module page service");
			//toolCategoryMap=null;
			toolList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(toolList).build();
		}
	}*/

	@GET
	@Path("moduleConfig/{moduleName}")
	@Produces(MediaType.APPLICATION_JSON)
	//Returns the Configuration for given ModuleName
	public Response getModuleConfig(@PathParam("moduleName") String moduleName)
	{  	 
		logger.info("inside the function getModuleConfig");
		logger.debug("got moduleName parameter with value :"+moduleName);
		ModuleConfigTO moduleConfigTO=new ModuleConfigTO();
		Set<SubModuleTO> subModuleTOList=new LinkedHashSet<SubModuleTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		try
		{
			keyvalueMap.put("moduleName", moduleName);

			Object[] moduleJobsJenkins= (Object[]) genericDao.findUniqueByQuery(ConstantQueries.GETMODULECONFIG,keyvalueMap);

			if(moduleJobsJenkins==null || moduleJobsJenkins.length==0)
			{
				logger.info("Module not found");
				moduleConfigTO=null;
				return Response.status(Response.Status.NOT_FOUND).entity(moduleConfigTO).build();
			}

			moduleConfigTO.setModuleId(Integer.parseInt(moduleJobsJenkins[0].toString()));
			moduleConfigTO.setModuleName(moduleJobsJenkins[1].toString());
			moduleConfigTO.setDescription(moduleJobsJenkins[3].toString());
			
			if(!(moduleJobsJenkins[2]==null||moduleJobsJenkins[2].toString().isEmpty()))
				moduleConfigTO.setModuleSubJobsOrder(moduleJobsJenkins[2].toString());

			List<Object> subJobsList=genericDao.findByQuery(ConstantQueries.GETSUBMODULEDATA,keyvalueMap);

			if(subJobsList==null||subJobsList.size()==0||subJobsList.isEmpty()==true)
			{
				logger.info("Subjobs not found");
				moduleConfigTO=null;
				return Response.status(Response.Status.OK).entity("SubJobs Not Found").build();

			}			
			Iterator<Object> subJobIt=subJobsList.iterator();
			while(subJobIt.hasNext())
			{	
				Object[] moduleSubJob=(Object[])subJobIt.next();
				
				logger.debug("id is "+Integer.parseInt(moduleSubJob[0].toString()));
				
				SubModuleTO subModuleTo=new SubModuleTO();
				subModuleTo.setSubModuleId(Integer.parseInt(moduleSubJob[0].toString()));
				subModuleTo.setSubjob_name(moduleSubJob[1].toString());
				subModuleTo.setNodeId(Integer.parseInt(moduleSubJob[2].toString()));
				subModuleTo.setNode_name(moduleSubJob[3].toString());
				subModuleTo.setCommand_to_execute(moduleSubJob[4].toString());
				subModuleTo.setReport_path(moduleSubJob[5].toString());
				subModuleTo.setSubjob_description(moduleSubJob[6].toString());
				subModuleTo.setTool_name(moduleSubJob[7].toString());
				subModuleTo.setModuleId(Integer.parseInt(moduleJobsJenkins[0].toString()));
				subModuleTo.setIsladyBugChecked(Boolean.parseBoolean(moduleSubJob[10].toString()));
				subModuleTo.setIsAlmChecked(Boolean.parseBoolean(moduleSubJob[11].toString()));
				subModuleTo.setOrder_number(Integer.parseInt(moduleSubJob[12].toString()));
				subModuleTo.setPostBuild_trigger_option(String.valueOf(moduleSubJob[13]));
				subModuleTo.setSubModuleParametersList(new ArrayList<SubModuleParametersTO>());
				
				keyvalueMap=new HashMap<String,Object>();
				String queryString="from ModuleSubJobsJenkinsParameters where moduleSubJobsJenkins.subjob_name=:subjobName";
				keyvalueMap.put("subjobName",subModuleTo.getSubjob_name());
				List<Object> subJobParametersList=genericDao.findByQuery(queryString, keyvalueMap);
				for(Object parameterObj:subJobParametersList) {
					ModuleSubJobsJenkinsParameters moduleSubJobsJenkinsParameters=(ModuleSubJobsJenkinsParameters)parameterObj;
					SubModuleParametersTO subModuleParametersTO=new SubModuleParametersTO();
					subModuleParametersTO.setId(moduleSubJobsJenkinsParameters.getId());
					subModuleParametersTO.setKey(moduleSubJobsJenkinsParameters.getParameter_key());
					subModuleParametersTO.setValue(moduleSubJobsJenkinsParameters.getValue());
					subModuleTo.getSubModuleParametersList().add(subModuleParametersTO);
				}
				
				if(moduleSubJob[8]!=null)
				{
					subModuleTo.setPostbuild_subjob(moduleSubJob[8].toString());
				}
				subModuleTOList.add(subModuleTo);
			}

			moduleConfigTO.setSubModules(subModuleTOList);
			logger.debug("Returning module config for module name :"+moduleName);
			return Response.status(Response.Status.OK).entity(moduleConfigTO).build();
		}

		catch(Exception e)
		{
			logger.error("Error in getting module configuration in Module page service"+e.getMessage());
			e.printStackTrace();
			moduleConfigTO=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleConfigTO).build();
		}		
	}


	@GET
	@Path("buildHistory/{moduleName}")
	@Produces(MediaType.APPLICATION_JSON)
	//Returns the Configuration for given ModuleName
	public Response getModuleBuildHistory(@PathParam("moduleName") String moduleName)
	{
		logger.info("inside the function getModuleBuildHistory");
		logger.debug("got moduleName parameter with value :"+moduleName);
		
		logger.debug("Getting Build History for : "+moduleName);

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
	@GET
	@Path("getModule")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModules() {
		ModuleJobsJenkins module=new ModuleJobsJenkins();
		module.setDescription("");
		module.setJenkins_job_name("");
		try {
			module.setModule_creation_date(getCurrentTimestamp());			
		}catch(Exception e) {
			e.printStackTrace();
		}
		module.setModule_subjobs_order("");
		module.setProjectMaster(new ProjectMaster());
		ModuleJobsJenkinsParameters moduleParameters=new ModuleJobsJenkinsParameters();
		List<ModuleJobsJenkinsParameters> moduleParametersList=new ArrayList<ModuleJobsJenkinsParameters>();
		moduleParameters.setModuleJobsJenkins(new ModuleJobsJenkins());
		moduleParameters.setParameter_key("");
		moduleParameters.setValue("");
		moduleParametersList.add(moduleParameters);
		module.setModuleJobsJenkinsParametersList(moduleParametersList);
		return Response.status(Response.Status.OK).entity(module).build();
	}

	@POST
	@Path("createModule/{projectName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//Create a module job in db and a job in jenkins
	public Response createModule(@PathParam("projectName") String projectName, ModuleJobsJenkins module)
	{
		logger.info("inside the function createModule");
		logger.debug("got projectName parameter with value :"+projectName+" and moduleJobsJenkinsObject with modulename "+module.getJenkins_job_name());
		ModuleConfigTO moduleJobsJenkinsTO=new ModuleConfigTO();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		try
		{
			logger.debug("Creating module with name "+module.getJenkins_job_name()+" in project :"+projectName);
			keyvalueMap.put("projectName", projectName);
			ProjectMaster projectMaster= (ProjectMaster) genericDao.findUniqueByQuery(ConstantQueries.GETPROJECTBYNAME,keyvalueMap);
			//	ProjectMaster projectMaster=(ProjectMaster) genericDao.findByID(ProjectMaster.class, Integer.parseInt(projectId));
			if(projectMaster==null)
			{
				logger.debug("Project does not exist :"+projectName+"  so cannot create job");
				return Response.status(Response.Status.NOT_FOUND).entity("Project Does not exist! Give valid Project").build();
			}	
			String moduleName=module.getJenkins_job_name();
			module.setModule_creation_date(getCurrentTimestamp());
			module.setProjectMaster(projectMaster);
			for(ModuleJobsJenkinsParameters m:module.getModuleJobsJenkinsParametersList()) {
				m.setModuleJobsJenkins(module);
			}
			module=jenkinsDao.createModule(module);
					
			if(module==null)
			{
				logger.info("Error wile creating module");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating module with name :"+moduleName+" inside project :"+projectName).build();
			}
			
			moduleJobsJenkinsTO.setModuleId(module.getJenkins_job_id());
			moduleJobsJenkinsTO.setModuleName(module.getJenkins_job_name());
			moduleJobsJenkinsTO.setDescription(module.getDescription());
			
			System.out.println(module);
			logger.info("Successfully Created module");
			return Response.status(Response.Status.OK).entity(moduleJobsJenkinsTO).build();
		}
		catch(Exception e)
		{
		
			logger.error("Error in create module :"+e.getCause());
			e.printStackTrace();
			if(e.getMessage().equals("Duplicate Module"))
			{
				return Response.status(Response.Status.CONFLICT).entity("Module Already Exists").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating Module").build();
		}

	}


	@GET
	@Path("getSchedule/{moduleName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//get schedule of a job
	public Response getModuleSchedule(@PathParam("moduleName") String moduleName)
	{
		logger.info("inside the function getModuleSchedule");
		logger.info("modulename= "+moduleName);
		
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		keyvalueMap.put("moduleName", moduleName);//check db for correct name
		ModuleJobsJenkins dbModule = null,module = new ModuleJobsJenkins();
		try {
			
			dbModule = (ModuleJobsJenkins) genericDao.findUniqueByQuery(ConstantQueries.GET_MODULE_BY_NAME,keyvalueMap);
		
			if(dbModule==null)
			{
				logger.info("Job does not exist in db:"+dbModule.getJenkins_job_name());
				return Response.status(Response.Status.NOT_FOUND).entity("Job Does not exist!").build();
				
			}	
			else
			{	
				
				logger.info(dbModule.getJenkins_job_id());
				keyvalueMap.clear();
				keyvalueMap.put("module_id", dbModule.getJenkins_job_id());
				//List<ModuleJobsJenkinsParameters> parametersList=(List<ModuleJobsJenkinsParameters>)(Object)genericDao.findByQuery(ConstantQueries.GET_PARAMETERS_BY_MODULE_ID, keyvalueMap);
				List<ModuleJobsJenkinsParameters> parametersList=dbModule.getModuleJobsJenkinsParametersList();
				logger.info("parameters lis size "+parametersList.size());
							
				//module.setModuleBuildHistory(new HashSet<ModuleBuildHistory>());
				List<ModuleJobsJenkinsParameters> parametersList2=new ArrayList<ModuleJobsJenkinsParameters>();
				for(ModuleJobsJenkinsParameters moduleParam:parametersList){
					ModuleJobsJenkinsParameters param=new ModuleJobsJenkinsParameters();
					param.setId(moduleParam.getId());
					param.setParameter_key(moduleParam.getParameter_key());
					param.setValue(moduleParam.getValue());
					
					parametersList2.add(param);
				}
				module.setModuleJobsJenkinsParametersList(parametersList2);
				logger.info("returning schedule details of "+dbModule.getJenkins_job_name());			
				return Response.status(Response.Status.OK).entity(module).build();
				
			}
			
			
			
		} catch (Exception e) {

			logger.error("Error in getting schedule details from db :"+e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting schedule of Module from db").build();
		}
		
			
	}
	
	
	@POST
	@Path("updateModule/{projectName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//Update a module job in db and a job in jenkins
	public Response updateModule(@PathParam("projectName") String projectName, ModuleJobsJenkins module)
	{
		logger.info("inside the function updateModule");
		logger.debug("got projectName parameter with value :"+projectName+" and moduleJobsJenkinsObject with modulename "+module.getJenkins_job_name());
		ModuleConfigTO moduleJobsJenkinsTO=new ModuleConfigTO();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		try
		{
			logger.debug("Updating module with name "+module.getJenkins_job_name()+" in project :"+projectName);
			keyvalueMap.put("projectName", projectName);
			ProjectMaster projectMaster= (ProjectMaster) genericDao.findUniqueByQuery(ConstantQueries.GETPROJECTBYNAME,keyvalueMap);
			//	ProjectMaster projectMaster=(ProjectMaster) genericDao.findByID(ProjectMaster.class, Integer.parseInt(projectId));
			if(projectMaster==null)
			{
				logger.debug("Project does not exist :"+projectName+"  so cannot update job");
				return Response.status(Response.Status.NOT_FOUND).entity("Project Does not exist! Give valid Project").build();
			}	
			
		
			keyvalueMap.clear();
			keyvalueMap.put("moduleName", module.getJenkins_job_name());//check db for correct name
			ModuleJobsJenkins dbModule= (ModuleJobsJenkins) genericDao.findUniqueByQuery(ConstantQueries.GET_MODULE_BY_NAME,keyvalueMap);
			if(dbModule==null)
			{
				logger.debug("Job does not exist in db:"+dbModule.getJenkins_job_name()+"  so cannot update job");
				return Response.status(Response.Status.NOT_FOUND).entity("Job Does not exist! Give valid Job").build();
				
			}	
			
			/*boolean flag =true;
			for(ModuleJobsJenkinsParameters m:dbModule.getModuleJobsJenkinsParametersList()) {
				if(m.getParameter_key().equals("scm_trigger")||m.getParameter_key().equals("timer_trigger"))
				{
					flag=true;
					for(ModuleJobsJenkinsParameters m2:module.getModuleJobsJenkinsParametersList())
					{
						m.setValue(m2.getValue());
					}
				}
			
			}
			if(flag)
			{
				dbModule.setModuleJobsJenkinsParametersList(module.getModuleJobsJenkinsParametersList());
			}*/
			
			logger.info("total parameter list size :"+ dbModule.getModuleJobsJenkinsParametersList().size());
			
			
			for(ModuleJobsJenkinsParameters m:dbModule.getModuleJobsJenkinsParametersList()) {
				logger.info("key"+m.getParameter_key());
				if(m.getParameter_key().equals("scm_timer")||m.getParameter_key().equals("timer_trigger"))
				{
					
					for(ModuleJobsJenkinsParameters m2:module.getModuleJobsJenkinsParametersList()) {
						if(m.getParameter_key().equals("scm_timer") && m2.getParameter_key().equals("scm_timer"))
						{	
						
								m2.setId(m.getId()); logger.info(" parameter key id "+m.getId());
								
							
						}
						else if(m.getParameter_key().equals("timer_trigger") && m2.getParameter_key().equals("timer_trigger"))
						{
									
							{
								m2.setId(m.getId()); logger.info("parameter key id "+m.getId());
							}
						}
					}
				}
			
			}
			dbModule.setModuleJobsJenkinsParametersList(module.getModuleJobsJenkinsParametersList());
			
			logger.info("moduleId: "+dbModule.getJenkins_job_id());
			logger.info("module   creation date"+dbModule.getModule_creation_date());
			String moduleName=module.getJenkins_job_name();
			for(ModuleJobsJenkinsParameters m:module.getModuleJobsJenkinsParametersList()) {
				m.setModuleJobsJenkins(dbModule);
			}
			module=jenkinsDao.updateModule(dbModule);
					
			if(module==null)
			{
				logger.info("Error wile updating module");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating module with name :"+moduleName+" inside project :"+projectName).build();
			}
			
			
		/*	ListIterator<ModuleJobsJenkinsParameters> iter = dbModule.getModuleJobsJenkinsParametersList().listIterator();
			while(iter.hasNext()){
				ModuleJobsJenkinsParameters m=(ModuleJobsJenkinsParameters)iter.next();
			//for(ModuleJobsJenkinsParameters m:dbModule.getModuleJobsJenkinsParametersList()) {
				logger.info("key"+m.getParameter_key());
				if(m.getParameter_key().equals("scm_timer")||m.getParameter_key().equals("timer_trigger"))
				{
					//flag=false;
					ListIterator<ModuleJobsJenkinsParameters> iter2 = dbModule.getModuleJobsJenkinsParametersList().listIterator();
					while(iter2.hasNext()){
						ModuleJobsJenkinsParameters m2=(ModuleJobsJenkinsParameters)iter2.next();
						if(m.getParameter_key().equals("scm_timer") && m2.getParameter_key().equals("scm_timer"))
						{
							
							if(m2.getValue().equals(""))
							{
								new GenericDaoImpl().delete(ModuleJobsJenkinsParameters.class,m.getId());
								iter2.remove();
								logger.info("deleted scm trigger");
							}
							else
							{
								m2.setId(m.getId()); logger.info(" parameter key id "+m.getId());
							}
						}
						else if(m.getParameter_key().equals("timer_trigger") && m2.getParameter_key().equals("timer_trigger"))
						{
							
							if(m2.getValue().equals(""))
							{
								new GenericDaoImpl().delete(ModuleJobsJenkinsParameters.class,m.getId());
								iter2.remove();
								logger.info("deleted timer tigger ");
							}
							else
							{
								m2.setId(m.getId()); logger.info("parameter key id "+m.getId());
							}
						}
					}
				}
			
			}*/
			
			moduleJobsJenkinsTO.setModuleId(module.getJenkins_job_id());
			moduleJobsJenkinsTO.setModuleName(module.getJenkins_job_name());
			moduleJobsJenkinsTO.setDescription(module.getDescription());
			
			//System.out.println(module);
			logger.info("Successfully updated module");
			return Response.status(Response.Status.OK).entity(moduleJobsJenkinsTO).build();
		}
		catch(Exception e)
		{
		
			logger.error("Error in updating module :"+e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Updating Module").build();
		}

	}
	
	
	@POST
	@Path("createSubJobs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//Create a job in PLATO. Creates a moduleSubJob in DB and creates a job in Jenkins under module job
	public Response createSubModule(ModuleConfigTO moduleConfigTO)
	{
		logger.info("inside the function createSubModule");
		logger.debug("got ModuleConfigTO object as parameter with moduleName :"+moduleConfigTO.getModuleName());
		try
		{
			//check if main module exists
			logger.info("Creating submodule");
			ModuleJobsJenkins module=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class, moduleConfigTO.getModuleId());
			//author:Rahul Bhardwaj(10643380)
			//basically, this set we are going to use for checking which subJobs are in postbuild of others
			//So jobs in postBuild of other subjobs are not going to be in post build of main job
			
			if(module==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module does not exist! Give valid Module").build();
			}
			Set<ModuleSubJobsJenkins> moduleSetForPostBuild=module.getModuleSubJobsJenkins();
			//add all submodules to module object
			Set<ModuleSubJobsJenkins> subJobSet=new HashSet<ModuleSubJobsJenkins>();
			Set<SubModuleTO> subModuleTOSet=moduleConfigTO.getSubModules();
			for(SubModuleTO subModuleTO : subModuleTOSet)
			{
				ModuleSubJobsJenkins subJob=new ModuleSubJobsJenkins();
				subJob.setSubjob_name(subModuleTO.getSubjob_name());
				subJob.setCommand_to_execute(subModuleTO.getCommand_to_execute());
				subJob.setReport_path(subModuleTO.getReport_path());
				subJob.setSubjob_description(subModuleTO.getSubjob_description());
				subJob.setTool_name(subModuleTO.getTool_name());
				subJob.setModuleJobsJenkins(module);
				subJob.setPostbuild_subjob(subModuleTO.getPostbuild_subjob());
				subJob.setIs_ladyBug_checked(subModuleTO.getIsladyBugChecked());
				subJob.setIs_alm_checked(subModuleTO.getIsAlmChecked());
				subJob.setOrder_number(subModuleTO.getOrder_number());
				subJob.setPostBuild_trigger_option(subModuleTO.getPostBuild_trigger_option());
				subJob.setModuleSubJobsJenkinsParametersList(new ArrayList<ModuleSubJobsJenkinsParameters>());
//				ArrayList<ModuleSubJobsJenkinsParameters> moduleSubJobsJenkinsParametersList=new ArrayList<ModuleSubJobsJenkinsParameters>();
				for(SubModuleParametersTO subModuleParametersTO:subModuleTO.getSubModuleParametersList()) {
					ModuleSubJobsJenkinsParameters moduleSubJobsJenkinsParameters=new ModuleSubJobsJenkinsParameters();
					moduleSubJobsJenkinsParameters.setParameter_key(subModuleParametersTO.getKey());
					moduleSubJobsJenkinsParameters.setValue(subModuleParametersTO.getValue());
					moduleSubJobsJenkinsParameters.setModuleSubJobsJenkins(subJob);
//					moduleSubJobsJenkinsParameters.setId(i);
					subJob.getModuleSubJobsJenkinsParametersList().add(moduleSubJobsJenkinsParameters);
//					moduleSubJobsJenkinsParametersList.add(moduleSubJobsJenkinsParameters);
//					i++;
				}

				//check if node provided is valid
				NodeMaster node=(NodeMaster) genericDao.findByID(NodeMaster.class, subModuleTO.getNode_name());
				if(node==null)
				{
					return Response.status(Response.Status.NOT_FOUND).entity("Node does not exist! Give valid Node").build();
				}
				subJob.setNodeMaster(node);	
				subJobSet.add(subJob);
				
				
				moduleSetForPostBuild.add(subJob);
				
				
				
			}

			module.setModuleSubJobsJenkins(subJobSet);
			module.setModule_subjobs_order(moduleConfigTO.getModuleSubJobsOrder());
			/*if(module.getModule_subjobs_order()==null||module.getModule_subjobs_order()=="")
			{
					module.setModule_subjobs_order(module.getJenkins_job_name());
			}*/
			
			
			//update main module
			//module=(ModuleJobsJenkins)genericDao.createOrUpdate(module); 
			jenkinsDao.createSubModule(module,moduleSetForPostBuild); 
			//just to be sure that proper subOrder is maintained
			mainJobProperSubJobOrder2(module.getJenkins_job_id());
			logger.info("Successfully created subjob");
			return Response.status(Response.Status.CREATED).entity("Jobs Created Successfully for Module :"+ module.getJenkins_job_name()).build();

		}
		catch(Exception e)
		{
			logger.error("error in create submodule :"+e);
			e.printStackTrace();
			if(e.getMessage().equals("Duplicate Row in table"))
			{
				logger.error("Duplicate subjob"+e);
				return Response.status(Response.Status.CONFLICT).entity("One or more Jobs Already Exist").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating Jobs").build();
		}
	}



	/*@POST
	@Path("createSubJob")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//Create a job in PLATO. Creates a moduleSubJob in DB and creates a job in Jenkins
	public Response createSubModule(SubModuleTO subModuleTO)
	{
		try
		{
			//check if main module exists
			ModuleJobsJenkins module=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class, subModuleTO.getModuleId());  
			if(module==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module does not exist! Give valid Module").build();
			}

			ModuleSubJobsJenkins subJob=new ModuleSubJobsJenkins();
			subJob.setSubjob_name(subModuleTO.getSubModuleName());
			subJob.setCommand_to_execute(subModuleTO.getCommandToExecute());
			subJob.setReport_path(subModuleTO.getReportPath());
			subJob.setSubjob_description(subModuleTO.getSubModuleDescription());
			subJob.setTool_name(subModuleTO.getToolName());
			subJob.setModuleJobsJenkins(module);
			subJob.setPostbuild_subjob(subModuleTO.getPostbuildSubjob());

			//check if node provided is valid
			NodeMaster node=(NodeMaster) genericDao.findByID(NodeMaster.class, subModuleTO.getNodeName());
			if(node==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Node does not exist! Give valid Node").build();
			}
			subJob.setNodeMaster(node);	

			jenkinsDao.createSubModule(subJob,module.getModule_subjobs_order(),module.getJenkins_job_id());

			return Response.status(Response.Status.CREATED).entity("Jobs Created Successfully for Module :"+ module.getJenkins_job_name()).build();

		}
		catch(Exception e)
		{
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			if(e.getMessage().equals("Duplicate Row in table")|| e.getMessage().equals("Duplicate Module"))
			{
				return Response.status(Response.Status.CONFLICT).entity("Job Already Exist").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating Jobs").build();
		}
	}*/

	@PUT
	@Path("updateSubJob")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	////update existing subjob in db as well as jenkins
	public Response updateSubModule(Set<SubModuleTO> subModuleTOSet)
	{
		logger.info("inside the function updateSubModule");
		if(subModuleTOSet==null) {
			return Response.status(Response.Status.NOT_FOUND).entity("sub Module set is null").build();
		}
		//logger.debug("got SubModuleTO object as parameter with subjobName :"+subModuleTO.getSubjob_name());
		for(SubModuleTO subModuleTO:subModuleTOSet)
		try
		{
			logger.debug("got SubModuleTO object as parameter with subjobName :"+subModuleTO.getSubjob_name());
			ModuleSubJobsJenkins subJob=new ModuleSubJobsJenkins();
			subJob.setSubjob_name(subModuleTO.getSubjob_name());
			subJob.setCommand_to_execute(subModuleTO.getCommand_to_execute());
			subJob.setReport_path(subModuleTO.getReport_path());
			subJob.setSubjob_description(subModuleTO.getSubjob_description());
			subJob.setTool_name(subModuleTO.getTool_name());
			subJob.setSubjob_id(subModuleTO.getSubModuleId());
			subJob.setIs_ladyBug_checked(subModuleTO.getIsladyBugChecked());
			subJob.setIs_alm_checked(subModuleTO.getIsAlmChecked());
			subJob.setOrder_number(subModuleTO.getOrder_number());
			subJob.setPostBuild_trigger_option(subModuleTO.getPostBuild_trigger_option());
			subJob.setModuleSubJobsJenkinsParametersList(new ArrayList<ModuleSubJobsJenkinsParameters>());
//			ArrayList<ModuleSubJobsJenkinsParameters> moduleSubJobsJenkinsParametersList=new ArrayList<ModuleSubJobsJenkinsParameters>();
			for(SubModuleParametersTO subModuleParametersTO:subModuleTO.getSubModuleParametersList()) {
				ModuleSubJobsJenkinsParameters moduleSubJobsJenkinsParameters=new ModuleSubJobsJenkinsParameters();
				moduleSubJobsJenkinsParameters.setParameter_key(subModuleParametersTO.getKey());
				moduleSubJobsJenkinsParameters.setValue(subModuleParametersTO.getValue());
				moduleSubJobsJenkinsParameters.setModuleSubJobsJenkins(subJob);
//				moduleSubJobsJenkinsParameters.setId(i);
				moduleSubJobsJenkinsParameters.setId(subModuleParametersTO.getId());
				subJob.getModuleSubJobsJenkinsParametersList().add(moduleSubJobsJenkinsParameters);
//				moduleSubJobsJenkinsParametersList.add(moduleSubJobsJenkinsParameters);
//				i++;
			}
			ModuleJobsJenkins module=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class, subModuleTO.getModuleId());
			if(module==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module does not exist! Give valid Module").build();
			}

			subJob.setModuleJobsJenkins(module);

			subJob.setPostbuild_subjob(subModuleTO.getPostbuild_subjob());

			//check if node provided is valid
			NodeMaster node=(NodeMaster) genericDao.findByID(NodeMaster.class, subModuleTO.getNode_name());
			if(node==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Node does not exist! Give valid Node").build();
			}
			subJob.setNodeMaster(node);	

			jenkinsDao.updateSubModule(subJob);
			logger.info("Successfully updated subjob");
			mainJobProperSubJobOrder2(module.getJenkins_job_id());

		}
		catch(Exception e)
		{
			logger.error("Error in update sub Module :"+e.getMessage());
			e.printStackTrace();
			if(e.getMessage().equals("Module Does Not Exist"))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does Not Exist").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating Jobs").build();
		}
		
		return Response.status(Response.Status.CREATED).entity("Jobs "+" Updated Successfully").build();
	}


	
	@PUT
	@Path("deleteSubModule")
	@Produces(MediaType.TEXT_PLAIN)
	//delete a subjob from db as well as jenkins
	public Response deleteSubModule(Set<String> subModuleNameSet)
	{
		logger.info("inside the function deleteSubModule");
		//logger.debug("got subModuleName as parameter having value :"+subModuleName);
		if(subModuleNameSet==null) {
			return Response.status(Response.Status.NO_CONTENT).entity("sub Module name set is null").build();
		}
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object >();
		System.out.println("subModuleNameSet.iterator().next() is "+subModuleNameSet.iterator().next());
		keyvalueMap.put("subJobName", subModuleNameSet.iterator().next());
		int moduleId=0;
		try {
			moduleId=(int)GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GETMAINMODULEID, keyvalueMap);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("module Id is "+moduleId);
		for(String subModuleName:subModuleNameSet)
		try
		{   logger.debug("got subModuleName as parameter having value :"+subModuleName);
			jenkinsDao.deleteSubModuleByQuery(subModuleName);
			logger.info("Successfully deleted subjob");
			//return Response.status(Response.Status.OK).entity("SubJob deleted Successfully").build();
		}
		catch(Exception e)
		{
			logger.error("Error in deleting subjob :"+e.getMessage());
			e.printStackTrace();
			if(e.getMessage().equalsIgnoreCase("Job does not exist"))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("SubJob Does not exist").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete SubModule").build();
		}
		mainJobProperSubJobOrder2(moduleId);
		return Response.status(Response.Status.OK).entity("SubJobs deleted Successfully").build();
	}


	@DELETE
	@Path("deleteAllSubModules/{moduleId}")
	@Produces(MediaType.TEXT_PLAIN)
	//delete all submodules inside given module from both db and jenkins
	public Response deleteAllSubModules(@PathParam("moduleId") int moduleId)
	{
		logger.info("inside the function deleteAllSubModules");
		logger.debug("got moduleId as parameter having value :"+moduleId);
		try
		{
			HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleId", moduleId);
			List<Object> moduleSubjobList=genericDao.findByQuery(ConstantQueries.GETMODULESUBJOBORDER, keyvalueMap);	
			if(moduleSubjobList==null||moduleSubjobList.size()==0)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("No subJobs found").build();
			}
			String subJobOrder=(String) moduleSubjobList.get(0);
			if(subJobOrder==null||subJobOrder.isEmpty()||subJobOrder.length()==0)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("No subJobs found").build();
			}
			LinkedList<String> subJobOrderList=new LinkedList<String>(Arrays.asList(subJobOrder.split(",")));
			for(String subModuleName:subJobOrderList)
			{
				jenkinsDao.deleteSubModuleByQuery(subModuleName);
			}
			logger.info("Successfully deleted all subjobs");
			return Response.status(Response.Status.OK).entity("All SubJobs deleted Successfully").build();
		}
		catch(Exception e)
		{
			logger.error("Error in deleting all subjobs :"+e.getMessage());
			e.printStackTrace();
			if(e.getMessage().equalsIgnoreCase("Job does not exist"))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("One or more subjobs do not exist").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete some SubJobs").build();
		}
	}

	@DELETE
	@Path("deleteModule/{moduleId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteModule(@PathParam("moduleId") int moduleId)
	{
		logger.info("inside the function deleteModule");
		logger.debug("got moduleId as parameter having value :"+moduleId);
		try
		{
			ModuleJobsJenkins module=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class,moduleId);
			if(module==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does not exist! Give valid Module").build();
			}
			jenkinsDao.deleteModule(module);
			logger.info("Successfully deleted module");
			return Response.status(Response.Status.OK).entity("Module deleted Successfully").build();
		}
		catch(Exception e)
		{
			logger.error("Error in deleting module :"+e.getMessage());
			e.printStackTrace();
			if(e.getMessage().equalsIgnoreCase("Job does not exist"))
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Module Does not exist").build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete Module").build();
		}

	}

	public Date getCurrentTimestamp() throws ParseException
	{
		logger.info("inside the function getCurrentTimestamp");
		long currentTime=System.currentTimeMillis();
		//creating Date from millisecond
		DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
		String dateString=df.format(currentTime);
		logger.debug("Returning current timestamp :"+dateString);
		return df.parse(dateString);
	}
	
	
	@GET
	@Path("getAllSubJobs")
	@Produces(MediaType.APPLICATION_JSON)
	//Get the names of modules
	public Response getSubjobList()
	{  	 
		logger.info("Validating Subjob Names");
		
		List<SubModuleTO> subModuleTOList=new ArrayList<SubModuleTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();

		try
		{			
			List<Object> subModuleNames=genericDao.findByQuery(ConstantQueries.GETALLSUBJOBS, keyvalueMap);
			if(subModuleNames==null|| subModuleNames.size()==0 || subModuleNames.isEmpty()==true)
			{
				logger.error("No Subjobs Present");
				subModuleTOList=null;
				return Response.status(Response.Status.NOT_FOUND).entity(subModuleTOList).build();
			}

		/*	Iterator<Object> it=subModuleNames.iterator();
			while(it.hasNext())
			{
				SubModuleTO subModuleTO=new SubModuleTO();
				Object[] subMmoduleDetails=(Object[])it.next();
				subModuleTO.setSubjob_name(subMmoduleDetails.toString());
				
				subModuleTOList.add(subModuleTO);
			}
			logger.info("Returning the retrieved module list");*/
			return Response.status(Response.Status.OK).entity(subModuleNames).build();
		}
		catch(Exception e)
		{
			subModuleTOList=null;
			logger.error("Error while getting modules for project in Module page service :"+e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(subModuleTOList).build();
		}



	}
	
	/**
	 * @author 10643380(Rahul Bhardwaj)
	 * */
	
	@GET
	@Path("mainJobProperSubJobOrder/{moduleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mainJobProperSubJobOrder(@PathParam("moduleId") int moduleId) {
		try {
			ModuleJobsJenkins module=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class, moduleId);
			Set<ModuleSubJobsJenkins> moduleSetForPostBuild=module.getModuleSubJobsJenkins();
			JenkinsServices jenkinsService=new JenkinsServices();
			jenkinsService.setChildProjectsInMainJob(moduleSetForPostBuild,module.getJenkins_job_name());
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error while updating").build();
		}

		return Response.status(Response.Status.OK).entity("successfully updated ").build();
	}
	
	public Response mainJobProperSubJobOrder2(int moduleId) {
		try {
			ModuleJobsJenkins module=(ModuleJobsJenkins) genericDao.findByID(ModuleJobsJenkins.class, moduleId);
			Set<ModuleSubJobsJenkins> moduleSetForPostBuild=module.getModuleSubJobsJenkins();
			//for build tools which have same name as main job
			for(ModuleSubJobsJenkins subModule:moduleSetForPostBuild) {
				if(subModule.getSubjob_name().equals(module.getJenkins_job_name())) {
					moduleSetForPostBuild.remove(subModule);
					break;
				}
			}
			JenkinsServices jenkinsService=new JenkinsServices();
			jenkinsService.setChildProjectsInMainJob(moduleSetForPostBuild,module.getJenkins_job_name());
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error while updating").build();
		}

		return Response.status(Response.Status.OK).entity("successfully updated ").build();
	}
	
	

}
