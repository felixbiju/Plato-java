package com.PLATO.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.CategoryMaster;
import com.PLATO.entities.NodeMaster;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.ProjectToolMapping;
import com.PLATO.entities.ToolMaster;
import com.PLATO.userTO.CategoryTO;
import com.PLATO.userTO.ProjectToolMappingTO;
import com.PLATO.userTO.ToolTO;

@Path("ToolConfigService")
public class ToolConfigService {
	
	/*
	 * Description : Service to perform various operation on tools page
	 * Author: Sueanne Alphonso
	 * 
	 * */

	private static final Logger logger = Logger
			.getLogger(ToolConfigService.class);
	
	//Fetching all tools from tool master
	@GET
	@Path("allfromToolMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllTools() {

		JSONObject categoryObj=new JSONObject();
		JSONArray toolsArray=new JSONArray();
		List<ToolTO> toolMasterList = new ArrayList<ToolTO>();
		ToolMaster toolMaster;
		ToolTO toolTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> toolList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_TOOLS, queryMap);

			if (toolList == null || toolList.size() == 0
					|| toolList.isEmpty() == true) {
				System.out.println("Tools not found");
				toolTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(toolTO).build();
			}

			for (int i = 0; i < toolList.size(); i++) {
				
				
				toolTO = new ToolTO();
				toolMaster = (ToolMaster) toolList.get(i);
				toolTO.setTool_id(toolMaster.getTool_id());
				toolTO.setTool_name(toolMaster.getTool_name());
				toolTO.setTool_logo(toolMaster.getTool_logo());
				toolTO.setCommand_to_execute(toolMaster.getCommand_to_execute());
				toolTO.setReport_path(toolMaster.getReport_path());
				toolTO.setNode_id(toolMaster.getNodeMaster().getNode_id());
				toolTO.setNode_name(toolMaster.getNodeMaster().getNode_name());
				toolTO.setCategory_id(toolMaster.getCategoryMaster().getCategory_id());
				toolTO.setCategory_name(toolMaster.getCategoryMaster().getCategory_name());
				
				if(categoryObj.containsKey(toolMaster.getCategoryMaster().getCategory_name())){
					toolsArray=(JSONArray) categoryObj.get(toolMaster.getCategoryMaster().getCategory_name());
					//System.out.println(categoryObj.get(toolMaster.getCategoryMaster().getCategory_name()));
					
				}else{
					toolsArray=new JSONArray();
					
					
				}
				
				toolsArray.add(toolTO);
				categoryObj.put(toolMaster.getCategoryMaster().getCategory_name(), toolsArray);
/*
				if (userRoleProjectMap.containsKey(user_id)) {
					userProjectMap = userRoleProjectMap.get(user_id);
				} else {
					userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();
				}
				*/
				//toolsArray.add(toolTO);
				
				
				
				//toolMasterList.add(toolTO);
			}

			return Response.status(Response.Status.OK).entity(categoryObj).build();

		} catch (Exception e) {
			toolTO = null;
			System.out
					.println("Error while getting tools in Tool Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(toolTO).build();
		}

	}
	
	
	/*
	//***********************ALL FROM TPM(Tool Project Mapping) 11/1/18*************
	@GET
	@Path("allToolMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllToolMappings() {

		List<ProjectToolMappingTO> toolMappingMasterList = new ArrayList<ProjectToolMappingTO>();
		ProjectToolMapping projectToolMapping;
		ProjectToolMappingTO projectToolMappingTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> toolMappingList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_TOOLS_MAPPINGS, queryMap);

			if (toolMappingList == null || toolMappingList.size() == 0
					|| toolMappingList.isEmpty() == true) {
				System.out.println("Tools Mappings not found");
				projectToolMappingTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectToolMappingTO).build();
			}

			for (int i = 0; i < toolMappingList.size(); i++) {
				projectToolMappingTO = new ProjectToolMappingTO();
				projectToolMapping = (ProjectToolMapping) toolMappingList.get(i);
				projectToolMappingTO.setTool_project_mapping_id(projectToolMapping.getTool_project_mapping_id());
				projectToolMappingTO.setTool_id(projectToolMapping.getToolMaster().getTool_id());
				projectToolMappingTO.setCommand_to_execute(projectToolMapping.getCommand_to_execute());
				projectToolMappingTO.setReport_path(projectToolMapping.getReport_path());
				projectToolMappingTO.setNode_name(projectToolMapping.getNodeMaster().getNode_name());
				
				projectToolMappingTO.setProject_id(projectToolMapping.getProjectMaster().getProject_id());
							
				

				toolMappingMasterList.add(projectToolMappingTO);
			}

			return Response.status(Response.Status.OK).entity(toolMappingMasterList).build();

		} catch (Exception e) {
			projectToolMappingTO = null;
			System.out
					.println("Error while getting project Tool Mapping in Tool Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectToolMappingTO).build();
		}

	}
	
	*/
	
	//fetching tools for project
	@GET
	@Path("fetchMappedTools/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchMappedTools(@PathParam("projectId") int projectId) throws IOException{
		
		List<ToolTO> toolTOList = new ArrayList<ToolTO>();
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		try
		{
			queryMap.put("projectId", projectId);		
			List<Object> toolList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_TOOLS_FOR_PORTFOLIO,queryMap);
			//ProjectMaster prm;
			//UserProjectMapping userProjectMapping;
			
			if(toolList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(toolList).build();
			}
			
			
			for(int i=0;i<toolList.size();i++){
				ToolTO toolTO=new ToolTO();
				Object[] tool=(Object[]) toolList.get(i);
				toolTO.setTool_id((int) tool[0]);
				toolTO.setTool_name((String) tool[1]);
				toolTO.setCommand_to_execute((String) tool[2]);
				toolTO.setReport_path((String) tool[3]);
				toolTO.setTool_logo((String) tool[4]);
				toolTO.setCategory_id((int) tool[5]);
				toolTO.setCategory_name((String) tool[6]);
				toolTO.setNode_id((int) tool[7]);
				toolTO.setNode_name((String) tool[8]);
				
				toolTOList.add(toolTO);
			}
		
		}catch(Exception e){
			toolTOList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(toolTOList).build();
		}
		
		return  Response.status(Response.Status.OK).entity(toolTOList).build();
	
	}
	
	
	//******************TO FETCH ALL TOOLS FROM PROJECT TOOL MAPPING TABLE 15/1/18***********
	@GET
	@Path("fetchMappedToolsFromProjectMapping/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchMappedToolsFromProjectMapping(@PathParam("projectId") int projectId) throws IOException{
		
		List<ProjectToolMappingTO> projectToolMappingList = new ArrayList<ProjectToolMappingTO>();
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		try
		{
			queryMap.put("projectId", projectId);		
			List<Object> projectToolMappList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_TOOLS_FOR_PROJECT,queryMap);
			//ProjectMaster prm;
			//UserProjectMapping userProjectMapping;
			
			if(projectToolMappList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(projectToolMappList).build();
			}
			
			
			for(int i=0;i<projectToolMappList.size();i++){
				ProjectToolMappingTO projToolMappTO=new ProjectToolMappingTO();
				Object[] toolMapping=(Object[]) projectToolMappList.get(i);
				projToolMappTO.setTool_project_mapping_id((int) toolMapping[0]);
				projToolMappTO.setCommand_to_execute((String) toolMapping[1]);
				projToolMappTO.setReport_path((String) toolMapping[2]);
				projToolMappTO.setTool_id((int) toolMapping[3]);
				projToolMappTO.setTool_name((String) toolMapping[4]);
				projToolMappTO.setTool_logo((String) toolMapping[5]);
				projToolMappTO.setCategory_id((int) toolMapping[6]);
				projToolMappTO.setCategory_name((String) toolMapping[7]);
				projToolMappTO.setNode_name((String) toolMapping[8]);
				
				
				
				
				
				
				
//				
//				toolTO.setTool_name((String) tool[1]);
//				toolTO.setCommand_to_execute((String) tool[2]);
//				toolTO.setReport_path((String) tool[3]);
//				toolTO.setTool_logo((String) tool[4]);
//				toolTO.setCategory_id((int) tool[5]);
//				toolTO.setCategory_name((String) tool[6]);
//				toolTO.setNode_id((int) tool[7]);
//				toolTO.setNode_name((String) tool[8]);
				
				projectToolMappingList.add(projToolMappTO);
			}
		
		}catch(Exception e){
			projectToolMappingList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(projectToolMappingList).build();
		}
		
		return  Response.status(Response.Status.OK).entity(projectToolMappingList).build();
	
	}
	
	
	
	
	/*
	@GET
	@Path("fetchMappedToolsForProject/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchMappedToolsForProject(@PathParam("projectId") int projectId) throws IOException{
		
		List<ProjectToolMappingTO> projectToolMappingMaster = new ArrayList<ProjectToolMappingTO>();
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		try
		{
			queryMap.put("projectId", projectId);		
			List<Object> toolMappingList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_TOOLS_FOR_PROJECT,queryMap);
			ProjectToolMappingTO toolTO;
			//ProjectMaster prm;
			//UserProjectMapping userProjectMapping;
			
			if(toolMappingList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(toolMappingList).build();
			}
			
			
			for(int i=0;i<toolMappingList.size();i++){
				toolTO=new ProjectToolMappingTO();
				Object[] tool=(Object[]) toolMappingList.get(i);
				toolTO.setTool_id((int) tool[0]);
				toolTO.setTool_name((String) tool[1]);
				toolTO.setCommand_to_execute((String) tool[2]);
				toolTO.setReport_path((String) tool[3]);
				toolTO.setTool_logo((String) tool[4]);
				toolTO.setCategory_id((int) tool[5]);
				toolTO.setCategory_name((String) tool[6]);
				toolTO.setNode_name((String) tool[8]);
				
				projectToolMappingMaster.add(toolTO);
			}
		
		}catch(Exception e){
			projectToolMappingMaster=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(projectToolMappingMaster).build();
		}
		
		return  Response.status(Response.Status.OK).entity(projectToolMappingMaster).build();
	
	}
	
	
	*/
	
	//***********************Create Project-Tool Mapping 8/1/18****************

			@GET
			@Path("create/{projectId}/{toolId}")
			@Consumes(MediaType.APPLICATION_JSON)
			@Produces(MediaType.APPLICATION_JSON)
			public Response createUserwithToolProjMapping(@PathParam("projectId") int projectId,@PathParam("toolId") int toolId) throws Exception {

				// /{defaultProject}
				// ,@PathParam("defaultProject") String defaultProject
				//logger.debug("Mapping User to projectId "+projectId+" with RoleId. "+roleId+" Default project : "+defaultProject);
				
				HashMap<String, Object> queryMapTool = new HashMap<String, Object>();
				queryMapTool.put("toolId", toolId);
				
			
				ToolMaster toolDetails= (ToolMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(ConstantQueries.GET_DEFAULT_FROM_TOOL_MASTER, queryMapTool);
				
				
				if (toolDetails== null) {
				//	logger.error("Could Not Find Projet");
				return Response.status(Response.Status.OK)
						.entity("Could Not Find Tool").build();
				}
				
				HashMap<String, Object> queryMapProject = new HashMap<String, Object>();
				queryMapProject.put("projectId", projectId);
				
				ProjectMaster projectMaster =(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
								ConstantQueries.GET_PARTICULAR_PROJECT_BY_ID, queryMapProject);
				
				if (projectMaster== null) {
					//logger.error("Could Not Find Role");
					return Response.status(Response.Status.OK)
							.entity("Could Not Find Project").build();
				}
			
				ProjectToolMapping projToolMapping=new ProjectToolMapping();
				
				projToolMapping.setProjectMaster(projectMaster);
				projToolMapping.setToolMaster(toolDetails);
				projToolMapping.setCommand_to_execute(toolDetails.getCommand_to_execute());
				projToolMapping.setReport_path(toolDetails.getReport_path());
				projToolMapping.setNodeMaster(toolDetails.getNodeMaster());
				
				try {
					ProjectToolMapping projectToolMapping = (ProjectToolMapping) GenericDaoSingleton
						.getGenericDao().createOrUpdate(projToolMapping);

				if (projectToolMapping != null) {
					//logger.info("User Mapped Successfully");
					return Response.status(Response.Status.OK)
							.entity("Tool Mapping Created Successfully").build();
				} else {
					//logger.error("User Mapping Failed");
					return Response.status(Response.Status.OK)
							.entity("Tool Mapping Creation Failed").build();
				}
				
				
				}catch (Exception e) {			
					
					System.out.println(e.getCause());
					System.out.println(e.getMessage());
					if(e.getMessage().equals("Duplicate Row in table"))
					{
						//logger.error("User Already Exists");
						return Response.status(Response.Status.CONFLICT).entity("User Already Exists").build();
					}
					
					//logger.error("Error Creating User");

					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating User").build();

				}

			}
			
			
			
			
			
			
			//***********************TO GET ALL CATEGORIES 8/1/18 *********
			
			@GET
			@Path("allCategories")
			@Produces(MediaType.APPLICATION_JSON)
			public Response fetchAllCategories() {

				List<CategoryTO> categoryMasterList = new ArrayList<CategoryTO>();
				CategoryMaster categoryMaster;
				CategoryTO categoryTO = null;

				try {

					HashMap<String, Object> queryMap = new HashMap<String, Object>();
					List<Object> categoryList = GenericDaoSingleton.getGenericDao()
							.findByQuery(ConstantQueries.GET_ALL_CATEGORIES, queryMap);

					if (categoryList == null || categoryList.size() == 0
							|| categoryList.isEmpty() == true) {
						System.out.println("Tools not found");
						categoryTO = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity(categoryTO).build();
					}

					for (int i = 0; i < categoryList.size(); i++) {
						categoryTO = new CategoryTO();
						categoryMaster = (CategoryMaster) categoryList.get(i);
						categoryTO.setCategory_id(categoryMaster.getCategory_id());
						categoryTO.setCategory_name(categoryMaster.getCategory_name()); 
						
						categoryMasterList.add(categoryTO);
					}

					return Response.status(Response.Status.OK).entity(categoryMasterList).build();

				} catch (Exception e) {
					categoryTO = null;
					System.out
							.println("Error while getting categories in Tool Config service");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(categoryTO).build();
				}

			}
			

	
//***********************TO GET ALL TOOLS IN CATEGORY 8/1/18 *********
			
			@GET
			@Path("allToolsInCategory/{categoryId}")
			@Produces(MediaType.APPLICATION_JSON)
			public Response fetchAllToolsInCategory(@PathParam("categoryId") int categoryId) {

				List<ToolTO> toolMasterList = new ArrayList<ToolTO>();
				ToolMaster toolMaster;
				ToolTO toolTO = null;

				try {

					HashMap<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("categoryId", categoryId);
					
					List<Object> toolList = GenericDaoSingleton.getGenericDao()
							.findByQuery(ConstantQueries.GET_ALL_TOOLS_IN_CATEGORY, queryMap);

					if (toolList == null || toolList.size() == 0
							|| toolList.isEmpty() == true) {
						System.out.println("Tools not found");
						toolTO = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity(toolTO).build();
					}

					for (int i = 0; i < toolList.size(); i++) {
						toolTO = new ToolTO();
						toolMaster = (ToolMaster) toolList.get(i);
						toolTO.setTool_id(toolMaster.getTool_id());
						toolTO.setTool_name(toolMaster.getTool_name());
						toolTO.setCommand_to_execute(toolMaster.getCommand_to_execute());
						toolTO.setReport_path(toolMaster.getReport_path());
						toolTO.setCategory_id(categoryId);
						toolTO.setCategory_name(toolMaster.getCategoryMaster().getCategory_name());
						toolTO.setNode_id(toolMaster.getNodeMaster().getNode_id());
						toolTO.setNode_name(toolMaster.getNodeMaster().getNode_name());
						
						
						
						
						toolMasterList.add(toolTO);
					}

					return Response.status(Response.Status.OK).entity(toolMasterList).build();

				} catch (Exception e) {
					toolTO = null;
					System.out
							.println("Error while getting categories in Tool Config service");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(toolTO).build();
				}

			}
			
//***********************TO PARTICULAR TOOL DETAILS 8/1/18 *********
			
			@GET
			@Path("getParticularToolDetails/{projectToolMappingId}")
			@Produces(MediaType.APPLICATION_JSON)
			public Response getParticularToolDetails(@PathParam("projectToolMappingId") int projectToolMappingId) {

				List<CategoryTO> categoryMasterList = new ArrayList<CategoryTO>();
				ProjectToolMapping projToolMaster;
				ProjectToolMappingTO projToolMAppingTO = null;

				try {

					HashMap<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("projectToolMappingId",projectToolMappingId);
					//queryMap.put("projectId",projectId);
					
					projToolMaster =(ProjectToolMapping) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_MAPPING, queryMap);
			
					
				
					if (projToolMaster == null ) {
						System.out.println("Tool not present");
						projToolMAppingTO = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity(projToolMAppingTO).build();
					}

					
					projToolMAppingTO = new ProjectToolMappingTO();
					projToolMAppingTO.setTool_project_mapping_id(projToolMaster.getTool_project_mapping_id());
					projToolMAppingTO.setTool_id(projToolMaster.getToolMaster().getTool_id());
					projToolMAppingTO.setProject_id(projToolMaster.getProjectMaster().getProject_id());
					projToolMAppingTO.setCommand_to_execute(projToolMaster.getCommand_to_execute());
					projToolMAppingTO.setReport_path(projToolMaster.getReport_path());
					projToolMAppingTO.setNode_name(projToolMaster.getNodeMaster().getNode_name());
					
					
					
					
					
					
					
					
					
					/*toolTO.setTool_id(toolMaster.getTool_id());
					toolTO.setTool_logo(toolMaster.getTool_logo());
					toolTO.setCommand_to_execute(toolMaster.getCommand_to_execute());
					toolTO.setNode_id(toolMaster.getNodeMaster().getNode_id());
					toolTO.setNode_name(toolMaster.getNodeMaster().getNode_name());
					toolTO.setCategory_id(toolMaster.getCategoryMaster().getCategory_id());
					toolTO.setCategory_name(toolMaster.getCategoryMaster().getCategory_name());
					toolTO.setReport_path(toolMaster.getReport_path());
					toolTO.setTool_name(toolMaster.getTool_name());*/
					
					
					

					return Response.status(Response.Status.OK).entity(projToolMAppingTO).build();

				} catch (Exception e) {
					projToolMAppingTO = null;
					System.out
							.println("Error while getting categories in Tool Config service");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(projToolMAppingTO).build();
				}

			}
			

			
		//***********************Update Tool 9/1/18*****************

			@PUT
			@Path(value = "update/")
			@Consumes(MediaType.APPLICATION_JSON)
			@Produces(MediaType.APPLICATION_JSON)
			public Response updateToolMapping(ProjectToolMappingTO projToolMapping) {
				
			
				try {
					HashMap<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("projectToolMappingId", projToolMapping.getTool_project_mapping_id());
					ProjectToolMapping existingToolMapping = (ProjectToolMapping) GenericDaoSingleton
							.getGenericDao().findUniqueByQuery(
									ConstantQueries.GET_MAPPING, queryMap);

					if (existingToolMapping == null) {
						
						//logger.error("User Does Not Exists");
						//System.out.println("User Does Not Exists");
						existingToolMapping = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Tool Mapping Does Not Exists").build();
					}

					existingToolMapping.setTool_project_mapping_id(projToolMapping.getTool_project_mapping_id());
					existingToolMapping.setCommand_to_execute(projToolMapping.getCommand_to_execute());
					existingToolMapping.setReport_path(projToolMapping.getReport_path());
					
					queryMap.clear();
					
					queryMap.put("node_name", projToolMapping.getNode_name());
					NodeMaster nodeMaster= (NodeMaster) GenericDaoSingleton
							.getGenericDao().findUniqueByQuery(
									ConstantQueries.GET_NODE_BY_NAME, queryMap);
					
					existingToolMapping.setNodeMaster(nodeMaster);
					ProjectToolMapping projToolMappingMstr = (ProjectToolMapping) GenericDaoSingleton
							.getGenericDao().createOrUpdate(existingToolMapping);

					if (projToolMappingMstr != null) {
						//logger.info("Tool Mapping updated Successfully");
						return Response.status(Response.Status.OK)
								.entity("Tool Mapping updated Successfully").build();
					} else {
						return Response.status(Response.Status.BAD_REQUEST)
								.entity(projToolMappingMstr).build();

					}
				} catch (Exception e) {
					//logger.error("Failed to update User");
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Failed to update Tool Mapping").build();
				}

			}
			
			
			

		
			//***********************Delete Tool Mapping from PTM 16/1/18********************

			@DELETE
			@Path(value = "/delete/{projectToolMappingId}")
			@Produces(MediaType.APPLICATION_JSON)
			public Response deleteProjectToolMapping(@PathParam("projectToolMappingId") int projectToolMappingId) {
				HashMap<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("projectToolMappingId", projectToolMappingId);

				try {
					
					System.out.println("inside update deleteProjectToolMapping");
					ProjectToolMapping projectToolMappingMaster = (ProjectToolMapping) GenericDaoSingleton
							.getGenericDao().findUniqueByQuery(
									ConstantQueries.GET_MAPPING, queryMap);

					if (projectToolMappingMaster == null) {
						System.out.println("Project-Tool Mapping Does Not Exists");
						projectToolMappingMaster = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Project-Tool Mapping Does Not Exists").build();
					}

					GenericDaoSingleton.getGenericDao().delete(ProjectToolMapping.class,
							projectToolMappingId);

					return Response.status(Response.Status.OK)
							.entity("Project-Tool Mapping Deleted Successfully").build();
				} catch (Exception e) {

					System.out
							.println("Error while getting project-tool Mapping in Tool Config service");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Project-Tool Mapping Deletion Failed").build();
				}

			}
	
	
			//***********************Create Tool in Tool Master 11/4/18****************

			@POST
			@Path("createTool/")
			@Consumes(MediaType.APPLICATION_JSON)
			@Produces(MediaType.APPLICATION_JSON)
			public Response createTool(ToolTO toolTO) throws Exception {

				// /{defaultProject}
				// ,@PathParam("defaultProject") String defaultProject
				//logger.debug("Mapping User to projectId "+projectId+" with RoleId. "+roleId+" Default project : "+defaultProject);
				
				HashMap<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("toolId", toolTO.getTool_id());
				
			
				ToolMaster toolDetails= (ToolMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(ConstantQueries.GET_DEFAULT_FROM_TOOL_MASTER, queryMap);
				
				
				if (toolDetails!= null) {
				//	logger.error("Could Not Find Projet");
				return Response.status(Response.Status.CONFLICT)
						.entity("Tool Already Exists").build();
				}
				queryMap.clear();
				queryMap.put("categoryId", toolTO.getCategory_id());
				
				CategoryMaster categoryMaster=(CategoryMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
						ConstantQueries.GET_PARTICULAR_CATEGORY, queryMap);
				
				
				if (categoryMaster== null) {
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Category Doesnt Exist").build();
				}
				
				queryMap.clear();
				queryMap.put("node_name", toolTO.getNode_name());
				NodeMaster nodeMaster= (NodeMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
						ConstantQueries.GET_NODE_BY_NAME, queryMap);
				
				if (nodeMaster== null) {
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Node Doesnt Exist").build();
				}
				
				ToolMaster newTool=new ToolMaster();
				newTool.setCategoryMaster(categoryMaster);
				newTool.setNodeMaster(nodeMaster);
				newTool.setCommand_to_execute(toolTO.getCommand_to_execute());
				newTool.setReport_path(toolTO.getReport_path());
				newTool.setTool_name(toolTO.getTool_name());
			
				try {
					ToolMaster toolMst = (ToolMaster) GenericDaoSingleton
						.getGenericDao().createOrUpdate(newTool);

				if (toolMst != null) {
					//logger.info("User Mapped Successfully");
					return Response.status(Response.Status.OK)
							.entity("Tool Created Successfully").build();
				} else {
					//logger.error("User Mapping Failed");
					return Response.status(Response.Status.OK)
							.entity("Tool Creation Failed").build();
				}
				
				
				}catch (Exception e) {			
					
					System.out.println(e.getCause());
					System.out.println(e.getMessage());
					if(e.getMessage().equals("Duplicate Row in table"))
					{
						//logger.error("User Already Exists");
						return Response.status(Response.Status.CONFLICT).entity("Tool Already Exists").build();
					}
					
					//logger.error("Error Creating User");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating User").build();

				}

			}
			
		
			//***********************Update Tool 9/1/18*****************

			@PUT
			@Path(value = "updateTool/")
			@Consumes(MediaType.APPLICATION_JSON)
			@Produces(MediaType.APPLICATION_JSON)
			public Response updateTool(ToolTO toolTo) {
				
			
				try {
					HashMap<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("toolId", toolTo.getTool_id());
					
					ToolMaster existingTool = (ToolMaster) GenericDaoSingleton
							.getGenericDao().findUniqueByQuery(
									ConstantQueries.GET_PARTICULAR_TOOL_BY_ID, queryMap);

					if (existingTool == null) {
						
						//logger.error("User Does Not Exists");
						//System.out.println("User Does Not Exists");
						existingTool = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Tool Does Not Exists").build();
					}

					queryMap.clear();
					queryMap.put("node_name", toolTo.getNode_name());
					NodeMaster nodeMaster= (NodeMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_NODE_BY_NAME, queryMap);
					
					if (nodeMaster== null) {
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Node Doesnt Exist").build();
					}
					
					
					existingTool.setTool_name(toolTo.getTool_name());
					existingTool.setCommand_to_execute(toolTo.getCommand_to_execute());
					existingTool.setNodeMaster(nodeMaster);
					
					existingTool.setReport_path(toolTo.getReport_path());
					
					
				
					ToolMaster toolMst = (ToolMaster) GenericDaoSingleton
							.getGenericDao().createOrUpdate(existingTool);

					if (toolMst != null) {
						//logger.info("Tool Mapping updated Successfully");
						return Response.status(Response.Status.OK)
								.entity("Tool Mapping updated Successfully").build();
					} else {
						return Response.status(Response.Status.BAD_REQUEST)
								.entity(toolMst).build();

					}
				} catch (Exception e) {
					//logger.error("Failed to update User");
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Failed to update Tool ").build();
				}

			}
			
			
			

	
			//***********************Delete Tool Mapping from PTM 14/4/18********************

			@DELETE
			@Path(value = "/deleteTool/{toolId}")
			public Response deleteTool(@PathParam("toolId") int toolId) {
				HashMap<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("toolId", toolId);

				try {
					
					System.out.println("inside delete Tool");
					ToolMaster toolMaster = (ToolMaster) GenericDaoSingleton
							.getGenericDao().findUniqueByQuery(
									ConstantQueries.GET_PARTICULAR_TOOL_BY_ID, queryMap);

					if (toolMaster == null) {
						System.out.println("Tool Does Not Exists");
						toolMaster = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Tool Does Not Exists").build();
					}

					GenericDaoSingleton.getGenericDao().delete(ToolMaster.class,
							toolId);

					return Response.status(Response.Status.OK)
							.entity("Tool Deleted Successfully").build();
				} catch (Exception e) {

					System.out
							.println("Error while getting tool in Tool Config service");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Tool Deletion Failed").build();
				}

			}

	
			// ***********************Fetch Particular Tool 14/4/18********
			@GET
			@Path("fetchParticularTool/{toolId}")
			@Produces(MediaType.APPLICATION_JSON)
			public Response fetchParticularTool(
					@PathParam("toolId") Integer toolId) {

			//	logger.debug("Fetching Particular : " + toolId);

				//ToolMaster toolMaster = new ToolMaster();
				ToolTO toolTO = null;

				try {

					HashMap<String, Object> queryMap = new HashMap<String, Object>();

					queryMap.put("toolId", toolId);

					ToolMaster toolMaster = (ToolMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_TOOL_BY_ID,
									queryMap);

					if (toolMaster == null) {
						logger.info("Tool Does Not Exists");
						toolMaster = null;
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Tool Does Not Exists").build();
					}

					
					toolTO = new ToolTO();
						//userMaster = (Object[]) userList.get(i);
					toolTO.setTool_id(toolMaster.getTool_id());
					toolTO.setTool_name(toolMaster.getTool_name());
					toolTO.setCommand_to_execute(toolMaster.getCommand_to_execute());
					toolTO.setReport_path(toolMaster.getReport_path());
					toolTO.setCategory_id(toolMaster.getCategoryMaster().getCategory_id());
					toolTO.setCategory_name(toolMaster.getCategoryMaster().getCategory_name());
					toolTO.setNode_id(toolMaster.getNodeMaster().getNode_id());
					toolTO.setNode_name(toolMaster.getNodeMaster().getNode_name());
		
					logger.info("Particular tool fetched successfully");

					return Response.status(Response.Status.OK).entity(toolTO)
							.build();

				} catch (Exception e) {
					toolTO = null;
					logger.error("Error while getting users");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(toolTO).build();
				}

			}
	
	
}
