package com.PLATO.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.PLATO.constants.ConstantQueries;
import com.PLATO.dao.GenericDao;
import com.PLATO.entities.CheckpointDetailsTemplate;
import com.PLATO.entities.CheckpointTemplate;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ProjectToolMapping;
import com.PLATO.entities.SubjobCheckpoint;
import com.PLATO.entities.SubjobCheckpointDetails;
import com.PLATO.userTO.CheckpointCriteriaTO;
import com.PLATO.userTO.MainCheckpointTO;
import com.PLATO.userTO.ModuleCheckpointTO;


@Path("CheckpointService")
public class ModuleCheckpointService 
{

	/*
	 * Description : Service to Create, retrieve and update the Checkpoints for Subjob and Checkpoints template for tool
	 * Author: Gaurav Kulkarni.
	 * */

	private static final Logger logger=Logger.getLogger(ModuleCheckpointService.class);
	private GenericDao genericDao=GenericDaoSingleton.getGenericDao();


	@POST
	@Path("createCheckpointTemplate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	//Service to create checkpoint templates for a tool
	public Response createTemplate(MainCheckpointTO mainCheckpointTO)
	{  	 
		try
		{
			logger.debug("Creating checkpoint template for project tool mapping :"+mainCheckpointTO.getProject_tool_mapping_id());
			List<ModuleCheckpointTO> moduleCheckpointTOList=mainCheckpointTO.getSubjob_checkpoint();
			List<CheckpointDetailsTemplate> checkpointDetailsTemplateList=null;
			int project_tool_id=mainCheckpointTO.getProject_tool_mapping_id();

			ProjectToolMapping projectToolMapping= (ProjectToolMapping) genericDao.findByID(ProjectToolMapping.class,project_tool_id);
			if(projectToolMapping==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Give a valid tool or Project").build();
			}
			//int outer_order_num=1;
			for(ModuleCheckpointTO moduleCheckpointTO: moduleCheckpointTOList)
			{
				CheckpointTemplate checkpointTemplate=new CheckpointTemplate();
				checkpointTemplate.setModule_name(moduleCheckpointTO.getModule_name());
				checkpointTemplate.setOrder_number(moduleCheckpointTO.getOrder_number());
				//checkpointTemplate.setOrder_number(outer_order_num);
				checkpointTemplate.setProjectToolMapping(projectToolMapping);

				List<CheckpointCriteriaTO> checkpointCriteriaTOList=moduleCheckpointTO.getCheckpoint_criteria();
				checkpointDetailsTemplateList=new ArrayList<CheckpointDetailsTemplate>();
				//int order_num=1;
				for(CheckpointCriteriaTO checkpointCriteriaTO: checkpointCriteriaTOList)
				{

					CheckpointDetailsTemplate checkpointDetailsTemplate=new CheckpointDetailsTemplate();
					//checkpointDetailsTemplate.setOrder_number(order_num);
					checkpointDetailsTemplate.setOrder_number(checkpointCriteriaTO.getOrder_number());
					checkpointDetailsTemplate.setCheckpoint_name(checkpointCriteriaTO.getCheckpoint_name());
					checkpointDetailsTemplate.setFail_criteria(checkpointCriteriaTO.getFail_criteria());
					checkpointDetailsTemplate.setPass_criteria(checkpointCriteriaTO.getPass_criteria());
					checkpointDetailsTemplate.setCheckpoint_template(checkpointTemplate);

					checkpointDetailsTemplateList.add(checkpointDetailsTemplate);
					//order_num++;
				}
				checkpointTemplate.setCheckpoint_details_template(checkpointDetailsTemplateList);
				genericDao.createOrUpdate(checkpointTemplate);
				//outer_order_num++;
				//logger.debug("Created checkpoint with id :"+checkpointTemplate.getCheckpoint_template_id());
			}

			return Response.status(Response.Status.OK).entity("Checkpoints Created Successfully for tool :"+projectToolMapping.getToolMaster().getTool_name()).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in creating one or more Checkpoints for Tool").build();
		}
	}

	@POST
	@Path("createCheckpoint")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	//Service to create checkpoints for a subjob
	public Response createCheckpoint(MainCheckpointTO mainCheckpointTO)
	{  	 
		try
		{
			logger.debug("Creating checkpoint for subjob :"+mainCheckpointTO.getSubjob_name());
			logger.debug("..."+mainCheckpointTO.getSubjob_checkpoint().size());
			List<ModuleCheckpointTO> moduleCheckpointTOList=mainCheckpointTO.getSubjob_checkpoint();
			//Set<SubjobCheckpoint> subjobCheckpointSet=new HashSet<SubjobCheckpoint>();
			List<SubjobCheckpointDetails> subjobCheckpointDetailsList=null;
			//int subjob_id=mainCheckpointTO.getSubjob_id();
		
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("subJobName",mainCheckpointTO.getSubjob_name());
			ModuleSubJobsJenkins moduleSubJobsJenkins= (ModuleSubJobsJenkins) genericDao.findUniqueByQuery(ConstantQueries.GETSUBMODULEBYNAME, keyvalueMap);
			//ModuleSubJobsJenkins moduleSubJobsJenkins= (ModuleSubJobsJenkins) genericDao.findByID(ModuleSubJobsJenkins.class,subjob_id);
			
			if(moduleSubJobsJenkins==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Give a valid Subjob").build();
			}
			Set<SubjobCheckpoint> subjobCheckpointSet=moduleSubJobsJenkins.getModuleCheckpoint();
			subjobCheckpointSet.clear();
			//int outer_order_num=1;
			for(ModuleCheckpointTO moduleCheckpointTO: moduleCheckpointTOList)
			{
				SubjobCheckpoint subjobCheckpoint=new SubjobCheckpoint();
				subjobCheckpoint.setModule_name(moduleCheckpointTO.getModule_name());
				subjobCheckpoint.setOrder_number(moduleCheckpointTO.getOrder_number());
				//subjobCheckpoint.setOrder_number(outer_order_num);
				subjobCheckpoint.setModuleSubJob(moduleSubJobsJenkins);

				List<CheckpointCriteriaTO> checkpointCriteriaTOList=moduleCheckpointTO.getCheckpoint_criteria();
				subjobCheckpointDetailsList=new ArrayList<SubjobCheckpointDetails>();

				//int order_num=1;
				for(CheckpointCriteriaTO checkpointCriteriaTO: checkpointCriteriaTOList)
				{

					SubjobCheckpointDetails subjobCheckpointDetails=new SubjobCheckpointDetails();
					subjobCheckpointDetails.setOrder_number(checkpointCriteriaTO.getOrder_number());
					//subjobCheckpointDetails.setOrder_number(order_num);
					subjobCheckpointDetails.setCheckpoint_name(checkpointCriteriaTO.getCheckpoint_name());
					subjobCheckpointDetails.setFail_criteria(checkpointCriteriaTO.getFail_criteria());
					subjobCheckpointDetails.setPass_criteria(checkpointCriteriaTO.getPass_criteria());
					subjobCheckpointDetails.setSubjob_checkpoint(subjobCheckpoint);

					subjobCheckpointDetailsList.add(subjobCheckpointDetails);
					//order_num++;
				}
				
				logger.debug("size of checkpoint list > "+subjobCheckpointDetailsList.size());

				subjobCheckpoint.setCheckpoint_criteria(subjobCheckpointDetailsList);
				subjobCheckpointSet.add(subjobCheckpoint);
				
				//genericDao.createOrUpdate(subjobCheckpoint);
				//outer_order_num++;
				//logger.debug("Created checkpoint with id :"+checkpointTemplate.getCheckpoint_template_id());
			}
			
			moduleSubJobsJenkins.setModuleCheckpoint(subjobCheckpointSet);
			genericDao.createOrUpdate(moduleSubJobsJenkins);
			return Response.status(Response.Status.OK).entity("Checkpoints Created Successfully for :"+moduleSubJobsJenkins.getSubjob_name()).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in creating one or more Checkpoints for Subjob").build();
		}
	}

	@GET
	@Path("getCheckpointsForSubjob/{subjobId}")
	@Produces(MediaType.APPLICATION_JSON)
	//Function to get all checkpoints for subjob
	public Response getSubjobCheckpoint(@PathParam("subjobId") int subjobId)
	{  	 
		try
		{
			logger.debug("Fetching checkpoint for subjob :"+subjobId);
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			List<ModuleCheckpointTO> moduleCheckpointTOList=new ArrayList<ModuleCheckpointTO>();

			keyvalueMap.put("subjob_id", subjobId);
			List<Object> subjobCheckpointList=genericDao.findByQuery(ConstantQueries.GETCHECKPOINTFORSUBJOB, keyvalueMap);

			if(subjobCheckpointList==null||subjobCheckpointList.isEmpty())
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Checkpoints not yet configured for Subjob").build();
			}
			logger.info("Fetching subjob checkpoint");
			Iterator<Object> it=subjobCheckpointList.iterator();
			while(it.hasNext())
			{
				SubjobCheckpoint subjobCheckpoint=(SubjobCheckpoint) it.next();
				ModuleCheckpointTO moduleCheckpointTO=new ModuleCheckpointTO();
				moduleCheckpointTO.setModule_checkpoint_id(subjobCheckpoint.getSubjob_checkpoint_id());
				moduleCheckpointTO.setModule_name(subjobCheckpoint.getModule_name());
				moduleCheckpointTO.setOrder_number(subjobCheckpoint.getOrder_number());
				moduleCheckpointTO.setModule_subjob_id(subjobCheckpoint.getModuleSubJob().getSubjob_id());

				List<SubjobCheckpointDetails> subjobCheckpointDetailsList=subjobCheckpoint.getCheckpoint_criteria();
				List<CheckpointCriteriaTO> checkpointCriteriaTOList=new ArrayList<CheckpointCriteriaTO>();
				for(SubjobCheckpointDetails subjobCheckpointDetails:subjobCheckpointDetailsList)
				{
					CheckpointCriteriaTO checkpointCriteriaTO=new CheckpointCriteriaTO();
					checkpointCriteriaTO.setOrder_number(subjobCheckpointDetails.getOrder_number());
					checkpointCriteriaTO.setCheckpoint_criteria_id(subjobCheckpointDetails.getSubjob_checkpoint_details_id());
					checkpointCriteriaTO.setCheckpoint_name(subjobCheckpointDetails.getCheckpoint_name());
					checkpointCriteriaTO.setPass_criteria(subjobCheckpointDetails.getPass_criteria());
					checkpointCriteriaTO.setFail_criteria(subjobCheckpointDetails.getFail_criteria());
					checkpointCriteriaTO.setChecked(true);
					checkpointCriteriaTOList.add(checkpointCriteriaTO);
				}
				moduleCheckpointTO.setCheckpoint_criteria(checkpointCriteriaTOList);
				moduleCheckpointTOList.add(moduleCheckpointTO);
			}

			return Response.status(Response.Status.OK).entity(moduleCheckpointTOList).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in fetchng Checkpoints for Subjob").build();
		}
	}


	@GET
	@Path("getCheckpointTemplateToolProject/{toolName}/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	//Function to get all checkpoints using toolid and projectid
	public Response getCheckpointTemplateforToolProject(@PathParam("toolName") String toolName, @PathParam("projectId") int projectId)
	{  	 
		try
		{
			logger.debug("Fetching checkpoint template for tool :"+toolName+" project :"+projectId);
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			List<ModuleCheckpointTO> moduleCheckpointTOList=new ArrayList<ModuleCheckpointTO>();

			logger.info("Fetching subjob checkpoint for tool");
			keyvalueMap=new HashMap<String,Object>();
			moduleCheckpointTOList=new ArrayList<ModuleCheckpointTO>();

			keyvalueMap.put("tool_name", toolName);
			keyvalueMap.put("project_id", projectId);
			List<Object> projectToolMappingList=genericDao.findByQuery(ConstantQueries.GET_PROJECT_TOOL_MAPPING, keyvalueMap);

			if(projectToolMappingList==null || projectToolMappingList.isEmpty())
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Checkpoints not yet configured for Subjob Or tool").build();
			}
			Iterator<Object> itr=projectToolMappingList.iterator();
			int projectToolMappingId=Integer.parseInt(itr.next().toString());
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("project_tool_mapping_id", projectToolMappingId);
			List<Object> checkpointTemplateList=genericDao.findByQuery(ConstantQueries.GETCHECKPOINTFORTOOL, keyvalueMap);

			if(checkpointTemplateList==null||checkpointTemplateList.isEmpty())
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Checkpoints not yet configured for Tool").build();
			}

			Iterator<Object> it=checkpointTemplateList.iterator();
			while(it.hasNext())
			{
				CheckpointTemplate checkpointTemplate=(CheckpointTemplate) it.next();
				ModuleCheckpointTO moduleCheckpointTO=new ModuleCheckpointTO();
				moduleCheckpointTO.setModule_checkpoint_id(checkpointTemplate.getCheckpoint_template_id());
				moduleCheckpointTO.setModule_name(checkpointTemplate.getModule_name());
				moduleCheckpointTO.setOrder_number(checkpointTemplate.getOrder_number());
				moduleCheckpointTO.setTool_name(checkpointTemplate.getProjectToolMapping().getToolMaster().getTool_name());

				List<CheckpointDetailsTemplate> checkpointDetailsTemplateList=checkpointTemplate.getCheckpoint_details_template();
				List<CheckpointCriteriaTO> checkpointCriteriaTOList=new ArrayList<CheckpointCriteriaTO>();
				for(CheckpointDetailsTemplate checkpointDetailsTemplate:checkpointDetailsTemplateList)
				{
					CheckpointCriteriaTO checkpointCriteriaTO=new CheckpointCriteriaTO();
					checkpointCriteriaTO.setOrder_number(checkpointDetailsTemplate.getOrder_number());
					checkpointCriteriaTO.setCheckpoint_criteria_id(checkpointDetailsTemplate.getCheckpoint_details_template_id());
					checkpointCriteriaTO.setCheckpoint_name(checkpointDetailsTemplate.getCheckpoint_name());
					checkpointCriteriaTO.setPass_criteria(checkpointDetailsTemplate.getPass_criteria());
					checkpointCriteriaTO.setFail_criteria(checkpointDetailsTemplate.getFail_criteria());

					checkpointCriteriaTOList.add(checkpointCriteriaTO);
				}
				moduleCheckpointTO.setCheckpoint_criteria(checkpointCriteriaTOList);
				moduleCheckpointTOList.add(moduleCheckpointTO);
			}

			return Response.status(Response.Status.OK).entity(moduleCheckpointTOList).build();	

		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in fetchng Checkpoints for Subjob").build();
		}
	}

	@GET
	@Path("getCheckpointTemplate/{projectToolMappingId}")
	@Produces(MediaType.APPLICATION_JSON)
	//Function to get all checkpoints for a tool using project tool mapping id
	public Response getCheckpointTemplate(@PathParam("projectToolMappingId") int projectToolMappingId)
	{  	 
		try
		{
			logger.debug("Fetching checkpoint template for toolprojectmappingid :"+projectToolMappingId);
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			List<ModuleCheckpointTO> moduleCheckpointTOList=new ArrayList<ModuleCheckpointTO>();

			keyvalueMap.put("project_tool_mapping_id", projectToolMappingId);
			List<Object> checkpointTemplateList=genericDao.findByQuery(ConstantQueries.GETCHECKPOINTFORTOOL, keyvalueMap);

			if(checkpointTemplateList==null||checkpointTemplateList.isEmpty())
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Checkpoints not yet configured for Tool").build();
			}

			Iterator<Object> it=checkpointTemplateList.iterator();
			while(it.hasNext())
			{
				CheckpointTemplate checkpointTemplate=(CheckpointTemplate) it.next();
				ModuleCheckpointTO moduleCheckpointTO=new ModuleCheckpointTO();
				moduleCheckpointTO.setModule_checkpoint_id(checkpointTemplate.getCheckpoint_template_id());
				moduleCheckpointTO.setModule_name(checkpointTemplate.getModule_name());
				moduleCheckpointTO.setOrder_number(checkpointTemplate.getOrder_number());
				moduleCheckpointTO.setTool_name(checkpointTemplate.getProjectToolMapping().getToolMaster().getTool_name());

				List<CheckpointDetailsTemplate> checkpointDetailsTemplateList=checkpointTemplate.getCheckpoint_details_template();
				List<CheckpointCriteriaTO> checkpointCriteriaTOList=new ArrayList<CheckpointCriteriaTO>();
				for(CheckpointDetailsTemplate checkpointDetailsTemplate:checkpointDetailsTemplateList)
				{
					CheckpointCriteriaTO checkpointCriteriaTO=new CheckpointCriteriaTO();
					checkpointCriteriaTO.setOrder_number(checkpointDetailsTemplate.getOrder_number());
					checkpointCriteriaTO.setCheckpoint_criteria_id(checkpointDetailsTemplate.getCheckpoint_details_template_id());
					checkpointCriteriaTO.setCheckpoint_name(checkpointDetailsTemplate.getCheckpoint_name());
					checkpointCriteriaTO.setPass_criteria(checkpointDetailsTemplate.getPass_criteria());
					checkpointCriteriaTO.setFail_criteria(checkpointDetailsTemplate.getFail_criteria());

					checkpointCriteriaTOList.add(checkpointCriteriaTO);
				}
				moduleCheckpointTO.setCheckpoint_criteria(checkpointCriteriaTOList);
				moduleCheckpointTOList.add(moduleCheckpointTO);
			}

			return Response.status(Response.Status.OK).entity(moduleCheckpointTOList).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in fetchng Checkpoints for Tool").build();
		}
	}


	@GET
	@Path("getSingleCheckpointTemplate/{checkpointId}")
	@Produces(MediaType.APPLICATION_JSON)
	//Function to get single checkpoint for a tool
	public Response getCheckpoint(@PathParam("checkpointId") int checkpointId)
	{  	 
		try
		{
			logger.debug("Fetching single checkpoint template with id :"+checkpointId);
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();


			keyvalueMap.put("checkpointId", checkpointId);
			CheckpointTemplate checkpointTemplate=(CheckpointTemplate) genericDao.findByID(CheckpointTemplate.class, checkpointId);
			if(checkpointTemplate==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Checkpoint not found").build();
			}


			ModuleCheckpointTO moduleCheckpointTO=new ModuleCheckpointTO();
			moduleCheckpointTO.setModule_checkpoint_id(checkpointTemplate.getCheckpoint_template_id());
			moduleCheckpointTO.setModule_name(checkpointTemplate.getModule_name());
			moduleCheckpointTO.setOrder_number(checkpointTemplate.getOrder_number());
			moduleCheckpointTO.setTool_name(checkpointTemplate.getProjectToolMapping().getToolMaster().getTool_name());

			List<CheckpointDetailsTemplate> checkpointDetailsTemplateList=checkpointTemplate.getCheckpoint_details_template();
			List<CheckpointCriteriaTO> checkpointCriteriaTOList=new ArrayList<CheckpointCriteriaTO>();
			for(CheckpointDetailsTemplate checkpointDetailsTemplate:checkpointDetailsTemplateList)
			{
				CheckpointCriteriaTO checkpointCriteriaTO=new CheckpointCriteriaTO();
				checkpointCriteriaTO.setOrder_number(checkpointDetailsTemplate.getOrder_number());
				checkpointCriteriaTO.setCheckpoint_criteria_id(checkpointDetailsTemplate.getCheckpoint_details_template_id());
				checkpointCriteriaTO.setCheckpoint_name(checkpointDetailsTemplate.getCheckpoint_name());
				checkpointCriteriaTO.setPass_criteria(checkpointDetailsTemplate.getPass_criteria());
				checkpointCriteriaTO.setFail_criteria(checkpointDetailsTemplate.getFail_criteria());

				checkpointCriteriaTOList.add(checkpointCriteriaTO);
			}
			moduleCheckpointTO.setCheckpoint_criteria(checkpointCriteriaTOList);
			return Response.status(Response.Status.OK).entity(moduleCheckpointTO).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in fetchng Checkpoint with id :"+checkpointId).build();
		}
	}


	@GET
	@Path("getSingleCheckpointForSubjob/{subjobCheckpointId}")
	@Produces(MediaType.APPLICATION_JSON)
	//Function to get all checkpoints for subjob
	public Response getSingleCheckpointForSubjob(@PathParam("subjobCheckpointId") int subjobCheckpointId)
	{  	 
		try
		{
			logger.debug("Fetching single subjob checkpoint with id :"+subjobCheckpointId);
			SubjobCheckpoint subjobCheckpoint=(SubjobCheckpoint)genericDao.findByID(SubjobCheckpoint.class, subjobCheckpointId);
			ModuleCheckpointTO moduleCheckpointTO=new ModuleCheckpointTO();
			moduleCheckpointTO.setModule_checkpoint_id(subjobCheckpoint.getSubjob_checkpoint_id());
			moduleCheckpointTO.setModule_name(subjobCheckpoint.getModule_name());
			moduleCheckpointTO.setOrder_number(subjobCheckpoint.getOrder_number());
			moduleCheckpointTO.setModule_subjob_id(subjobCheckpoint.getModuleSubJob().getSubjob_id());

			List<SubjobCheckpointDetails> subjobCheckpointDetailsList=subjobCheckpoint.getCheckpoint_criteria();
			List<CheckpointCriteriaTO> checkpointCriteriaTOList=new ArrayList<CheckpointCriteriaTO>();
			for(SubjobCheckpointDetails subjobCheckpointDetails:subjobCheckpointDetailsList)
			{
				CheckpointCriteriaTO checkpointCriteriaTO=new CheckpointCriteriaTO();
				checkpointCriteriaTO.setOrder_number(subjobCheckpointDetails.getOrder_number());
				checkpointCriteriaTO.setCheckpoint_criteria_id(subjobCheckpointDetails.getSubjob_checkpoint_details_id());
				checkpointCriteriaTO.setCheckpoint_name(subjobCheckpointDetails.getCheckpoint_name());
				checkpointCriteriaTO.setPass_criteria(subjobCheckpointDetails.getPass_criteria());
				checkpointCriteriaTO.setFail_criteria(subjobCheckpointDetails.getFail_criteria());

				checkpointCriteriaTOList.add(checkpointCriteriaTO);
			}
			moduleCheckpointTO.setCheckpoint_criteria(checkpointCriteriaTOList);



			return Response.status(Response.Status.OK).entity(moduleCheckpointTO).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in fetchng single Checkpoint for Subjob").build();
		}
	}


	@PUT
	@Path("updateSingleCheckpointTemplate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	//update single checkpoint template for given tool
	public Response updateSingleTemplate(ModuleCheckpointTO moduleCheckpointTO)
	{  	 
		try
		{
			logger.debug("Updating single checkpoint template with id :"+moduleCheckpointTO.getModule_checkpoint_id());
			List<CheckpointDetailsTemplate> checkpointDetailsTemplateList=null;
			CheckpointTemplate checkpointTemplate=(CheckpointTemplate) genericDao.findByID(CheckpointTemplate.class, moduleCheckpointTO.getModule_checkpoint_id());
			checkpointTemplate.setCheckpoint_template_id(moduleCheckpointTO.getModule_checkpoint_id());
			checkpointTemplate.setModule_name(moduleCheckpointTO.getModule_name());

			List<CheckpointCriteriaTO> checkpointCriteriaTOList=moduleCheckpointTO.getCheckpoint_criteria();
			checkpointDetailsTemplateList=new ArrayList<CheckpointDetailsTemplate>();
			int order_num=1;
			for(CheckpointCriteriaTO checkpointCriteriaTO: checkpointCriteriaTOList)
			{

				CheckpointDetailsTemplate checkpointDetailsTemplate=new CheckpointDetailsTemplate();
				//checkpointDetailsTemplate.setOrder_number(checkpointCriteriaTO.getOrder_number());
				checkpointDetailsTemplate.setOrder_number(order_num);
				checkpointDetailsTemplate.setCheckpoint_details_template_id(checkpointCriteriaTO.getCheckpoint_criteria_id());
				checkpointDetailsTemplate.setCheckpoint_name(checkpointCriteriaTO.getCheckpoint_name());
				checkpointDetailsTemplate.setFail_criteria(checkpointCriteriaTO.getFail_criteria());
				checkpointDetailsTemplate.setPass_criteria(checkpointCriteriaTO.getPass_criteria());
				checkpointDetailsTemplate.setCheckpoint_template(checkpointTemplate);

				checkpointDetailsTemplateList.add(checkpointDetailsTemplate);
				order_num++;
			}
			checkpointTemplate.setCheckpoint_details_template(checkpointDetailsTemplateList);
			genericDao.createOrUpdate(checkpointTemplate);


			return Response.status(Response.Status.OK).entity("Checkpoints Updated Successfully").build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in updating Checkpoint").build();
		}
	}



	@PUT
	@Path("updateCheckpointTemplate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	//update all checkpoint templates for given tool
	public Response updateTemplate(MainCheckpointTO mainCheckpointTO)
	{  	 
		try
		{
			logger.debug("Updating all checkpoint templates for projecttoolmapping :"+mainCheckpointTO.getProject_tool_mapping_id());
			List<ModuleCheckpointTO> moduleCheckpointTOList=mainCheckpointTO.getSubjob_checkpoint();
			List<CheckpointDetailsTemplate> checkpointDetailsTemplateList=null;
			int project_tool_id=mainCheckpointTO.getProject_tool_mapping_id();

			ProjectToolMapping projectToolMapping= (ProjectToolMapping) genericDao.findByID(ProjectToolMapping.class,project_tool_id);
			if(projectToolMapping==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Give a valid tool or Project").build();
			}
			int outer_order_num=1;
			for(ModuleCheckpointTO moduleCheckpointTO: moduleCheckpointTOList)
			{
				CheckpointTemplate checkpointTemplate=new CheckpointTemplate();
				checkpointTemplate.setCheckpoint_template_id(moduleCheckpointTO.getModule_checkpoint_id());
				checkpointTemplate.setModule_name(moduleCheckpointTO.getModule_name());
				//checkpointTemplate.setOrder_number(moduleCheckpointTO.getOrder_number());
				checkpointTemplate.setOrder_number(outer_order_num);
				checkpointTemplate.setProjectToolMapping(projectToolMapping);

				List<CheckpointCriteriaTO> checkpointCriteriaTOList=moduleCheckpointTO.getCheckpoint_criteria();
				checkpointDetailsTemplateList=new ArrayList<CheckpointDetailsTemplate>();
				int order_num=1;
				for(CheckpointCriteriaTO checkpointCriteriaTO: checkpointCriteriaTOList)
				{

					CheckpointDetailsTemplate checkpointDetailsTemplate=new CheckpointDetailsTemplate();
					//checkpointDetailsTemplate.setOrder_number(checkpointCriteriaTO.getOrder_number());
					checkpointDetailsTemplate.setOrder_number(order_num);
					checkpointDetailsTemplate.setCheckpoint_details_template_id(checkpointCriteriaTO.getCheckpoint_criteria_id());
					checkpointDetailsTemplate.setCheckpoint_name(checkpointCriteriaTO.getCheckpoint_name());
					checkpointDetailsTemplate.setFail_criteria(checkpointCriteriaTO.getFail_criteria());
					checkpointDetailsTemplate.setPass_criteria(checkpointCriteriaTO.getPass_criteria());
					checkpointDetailsTemplate.setCheckpoint_template(checkpointTemplate);

					checkpointDetailsTemplateList.add(checkpointDetailsTemplate);
					order_num++;
				}
				checkpointTemplate.setCheckpoint_details_template(checkpointDetailsTemplateList);
				genericDao.createOrUpdate(checkpointTemplate);
				outer_order_num++;
				//logger.debug("Created checkpoint with id :"+checkpointTemplate.getCheckpoint_template_id());
			}

			return Response.status(Response.Status.OK).entity("Checkpoints Created Successfully for tool :"+projectToolMapping.getToolMaster().getTool_name()).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in creating one or more Checkpoints for Tool").build();
		}
	}

	@PUT
	@Path("updateCheckpoint")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	//update all checkpoints for given subjob
	public Response updateCheckpoint(MainCheckpointTO mainCheckpointTO)
	{  	 
		try
		{
			logger.debug("Updating all subjob checkpoints for subjob :"+mainCheckpointTO.getSubjob_id());
			List<ModuleCheckpointTO> moduleCheckpointTOList=mainCheckpointTO.getSubjob_checkpoint();
			List<SubjobCheckpointDetails> subjobCheckpointDetailsList=null;
			int subjob_id=mainCheckpointTO.getSubjob_id();

			ModuleSubJobsJenkins moduleSubJobsJenkins= (ModuleSubJobsJenkins) genericDao.findByID(ModuleSubJobsJenkins.class,subjob_id);
			if(moduleSubJobsJenkins==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Give a valid Subjob").build();
			}
			int outer_order_num=1;
			for(ModuleCheckpointTO moduleCheckpointTO: moduleCheckpointTOList)
			{
				SubjobCheckpoint subjobCheckpoint=new SubjobCheckpoint();
				subjobCheckpoint.setSubjob_checkpoint_id(moduleCheckpointTO.getModule_checkpoint_id());
				subjobCheckpoint.setModule_name(moduleCheckpointTO.getModule_name());
				subjobCheckpoint.setOrder_number(outer_order_num);
				//subjobCheckpoint.setOrder_number(moduleCheckpointTO.getOrder_number());
				subjobCheckpoint.setModuleSubJob(moduleSubJobsJenkins);

				List<CheckpointCriteriaTO> checkpointCriteriaTOList=moduleCheckpointTO.getCheckpoint_criteria();
				subjobCheckpointDetailsList=new ArrayList<SubjobCheckpointDetails>();

				int order_num=1;
				for(CheckpointCriteriaTO checkpointCriteriaTO: checkpointCriteriaTOList)
				{

					SubjobCheckpointDetails subjobCheckpointDetails=new SubjobCheckpointDetails();
					//subjobCheckpointDetails.setOrder_number(checkpointCriteriaTO.getOrder_number());
					subjobCheckpointDetails.setOrder_number(order_num);
					subjobCheckpointDetails.setSubjob_checkpoint_details_id(checkpointCriteriaTO.getCheckpoint_criteria_id());
					subjobCheckpointDetails.setCheckpoint_name(checkpointCriteriaTO.getCheckpoint_name());
					subjobCheckpointDetails.setFail_criteria(checkpointCriteriaTO.getFail_criteria());
					subjobCheckpointDetails.setPass_criteria(checkpointCriteriaTO.getPass_criteria());
					subjobCheckpointDetails.setSubjob_checkpoint(subjobCheckpoint);

					subjobCheckpointDetailsList.add(subjobCheckpointDetails);
					order_num++;
				}


				subjobCheckpoint.setCheckpoint_criteria(subjobCheckpointDetailsList);
				genericDao.createOrUpdate(subjobCheckpoint);
				outer_order_num++;
				//logger.debug("Created checkpoint with id :"+checkpointTemplate.getCheckpoint_template_id());
			}

			return Response.status(Response.Status.OK).entity("Checkpoints Created Successfully for :"+moduleSubJobsJenkins.getSubjob_name()).build();	
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in creating one or more Checkpoints for Subjob").build();
		}
	}

	@DELETE
	@Path("deleteCheckpointTemplate/{checkpointTemplateId}")
	@Produces(MediaType.TEXT_PLAIN)
	//Function to delete checkpoint for a tool
	public Response deleteCheckpointTemplate(@PathParam("checkpointTemplateId") int checkpointTemplateId)
	{  	 
		try
		{
			logger.debug("Deleting checkpoint template with id :"+checkpointTemplateId);
			CheckpointTemplate checkpointTemplate=(CheckpointTemplate) genericDao.findByID(CheckpointTemplate.class,checkpointTemplateId);

			genericDao.delete(CheckpointTemplate.class,checkpointTemplateId);
			
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("order_number", checkpointTemplate.getOrder_number());
			keyvalueMap.put("project_tool_mapping_id", checkpointTemplate.getProjectToolMapping().getTool_project_mapping_id());
			
			genericDao.updateQuery(ConstantQueries.UPDATECHECKPOINTTEMPLATEORDER, keyvalueMap);			
			
			return Response.status(Response.Status.OK).entity("Deleted CheckpointTemplate :"+checkpointTemplateId).build();
		}
		catch(Exception e)
		{
			logger.error("Exception while deleting the CheckpointTemplate "+checkpointTemplateId+" :"+e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in deleting Checkpointtemplate with id :"+checkpointTemplateId+" for Tool").build();
		}
	}

	@DELETE
	@Path("deleteCheckpointDetailTemplate/{checkpointDetailTemplateId}")
	@Produces(MediaType.TEXT_PLAIN)
	//Function to delete checkpointdetail template for a tool
	public Response deleteCheckpointDetailTemplate(@PathParam("checkpointDetailTemplateId") int checkpointDetailTemplateId)
	{  	 
		try
		{
			logger.debug("Deleting checkpoint detail template with id :"+checkpointDetailTemplateId);
			CheckpointDetailsTemplate checkpointDetailsTemplate=(CheckpointDetailsTemplate) genericDao.findByID(CheckpointDetailsTemplate.class,checkpointDetailTemplateId);
			CheckpointTemplate checkpointTemplate=checkpointDetailsTemplate.getCheckpoint_template();
			List<CheckpointDetailsTemplate> checkpointDetailsTemplateList= checkpointTemplate.getCheckpoint_details_template();
			checkpointDetailsTemplateList.remove(checkpointDetailsTemplate);
			checkpointTemplate.setCheckpoint_details_template(checkpointDetailsTemplateList);
			genericDao.createOrUpdate(checkpointTemplate);


			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();

			keyvalueMap.put("order_number", checkpointDetailsTemplate.getOrder_number());
			keyvalueMap.put("checkpoint_template_id", checkpointDetailsTemplate.getCheckpoint_template().getCheckpoint_template_id());

			genericDao.updateQuery(ConstantQueries.UPDATECHECKPOINTDETAILORDER, keyvalueMap);
			//genericDao.delete(CheckpointDetailsTemplate.class,checkpointDetailTemplateId);
			return Response.status(Response.Status.OK).entity("Deleted CheckpointDetailTemplate :"+checkpointDetailTemplateId).build();
		}
		catch(Exception e)
		{
			logger.error("Exception while deleting the CheckpointTemplate "+checkpointDetailTemplateId+" :"+e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in deleting Checkpointtemplate with id :"+checkpointDetailTemplateId+" for Tool").build();
		}
	}

	@DELETE
	@Path("deleteCheckpoint/{checkpointId}")
	@Produces(MediaType.TEXT_PLAIN)
	//Function to delete checkpoint for a subjob
	public Response deleteCheckpoint(@PathParam("checkpointId") int checkpointId)
	{  	 
		try
		{
			logger.debug("Deleting subjob checkpoint with id :"+checkpointId);
			genericDao.delete(SubjobCheckpoint.class,checkpointId);
			return Response.status(Response.Status.OK).entity("Deleted SubjobCheckpoint :"+checkpointId).build();
		}
		catch(Exception e)
		{
			logger.error("Exception while deleting the CheckpointTemplate "+checkpointId+" :"+e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in deleting Checkpoint with id :"+checkpointId+" for Subjob").build();
		}
	}

}


