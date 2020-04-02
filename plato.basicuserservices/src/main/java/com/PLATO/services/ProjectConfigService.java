package com.PLATO.services;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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



import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.AccountMaster;

import com.PLATO.entities.ProjectMaster;

import com.PLATO.userTO.AllocatedProjects;
import com.PLATO.userTO.ProjectTO;

@Path("ProjectConfigService")
public class ProjectConfigService {
	

	/*
	 * Description : Service to perform various operation on project page
	 * Author: Sueanne Alphonso
	 * 
	 * */

	private static final Logger logger=Logger.getLogger(ProjectConfigService.class);

	

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllProjects() {
		
		logger.info("Fetching all Projects");


		List<ProjectTO> projectMasterList = new ArrayList<ProjectTO>();
		ProjectMaster projectMaster;
		ProjectTO projectTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> projectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_PROJECTS, queryMap);

			if (projectList == null || projectList.size() == 0
					|| projectList.isEmpty() == true) {
				logger.error("Projects not found");

				System.out.println("Projects not found");
				projectTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectTO).build();
			}

			for (int i = 0; i < projectList.size(); i++) {
				projectTO = new ProjectTO();
				projectMaster = (ProjectMaster) projectList.get(i);
				projectTO.setProject_id(projectMaster.getProject_id());
				projectTO.setProject_name(projectMaster.getProject_name());
				projectTO.setProject_status(projectMaster.getProject_status());
				projectTO.setProject_creation_date(projectMaster
						.getProject_creation_date());
				projectTO.setAccount_id(projectMaster.getAccountMaster()
						.getAccount_id());

				projectMasterList.add(projectTO);
			}

			logger.info("Projects fetched Successfully");

			return Response.status(Response.Status.OK).entity(projectMasterList)
					.build();
		} catch (Exception e) {
			projectTO = null;
			logger.error("Error while getting Projects in Project Config service");

			System.out
					.println("Error while getting Projects in Project Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectTO).build();
		}

	}
	

	//*************************************all project info for particular portfolio and account 13/12/17******
	
	@GET
	@Path("allProjectDetailsInPortfolioAndAccount/{portfolioId}/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllProjectsInPort(@PathParam("portfolioId") Integer portfolioId,@PathParam("accountId") Integer accountId) throws Exception{
		

		logger.info("Fetching projects for particular portfolio"+portfolioId+" and account"+accountId);

		
		List<ProjectTO> projectMasterList = new ArrayList<ProjectTO>();
		Object projectMaster[];
		ProjectTO projectTO = null;
		

		try {

			
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("portfolioId",portfolioId);
			queryMap.put("accountId",accountId);
			
			List<Object> projectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALLOCATED_PROJECTS_FOR_PORTFOLIO_AND_ACCOUNT, queryMap);

		
			
			if (projectList == null || projectList.size() == 0
					|| projectList.isEmpty() == true) {
				System.out.println("Projects not found");
				projectTO = null;
				logger.error("Projets not found for particular portfolio and account");

				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectTO).build();
			}

			for (int i = 0; i < projectList.size(); i++) {
				projectTO = new ProjectTO();
				projectMaster = (Object[]) projectList.get(i);
				projectTO.setProject_id(Integer.parseInt(projectMaster[0].toString()));
				projectTO.setProject_name(projectMaster[1].toString());
				projectTO.setProject_status(projectMaster[2].toString());
				projectTO.setProject_creation_date((Date) projectMaster[3]);
				projectTO.setAccount_id(Integer.parseInt(projectMaster[4].toString()));

				projectMasterList.add(projectTO);
			}

			logger.error("Projects fetched successfully");

			return Response.status(Response.Status.OK).entity(projectMasterList)
					.build();
		} catch (Exception e) {

			e.printStackTrace();
			projectTO = null;
			logger.error("Error while getting Projects in Project Config service");

			System.out
					.println("Error while getting Projects in Project Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectTO).build();
		}

	}
	
	
	//*****************************Get particular project 13/12/17*************
	
	@GET
	@Path("getParticularProject/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getParticularProject(
			@PathParam("projectId") Integer projectId) throws Exception {

		ProjectTO projectTO = null;

		try {
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("projectId", projectId);

			ProjectMaster project = (ProjectMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_PROJECT_BY_ID,
							queryMap);

			if (project == null) {
				System.out.println("Project not found");
				projectTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectTO).build();
			}

			projectTO = new ProjectTO();
			// projectMaster = (Object[]) projectList.get(i);
			projectTO.setProject_id(project.getProject_id());
			projectTO.setProject_name(project.getProject_name());
			projectTO.setProject_status(project.getProject_status());
			projectTO.setProject_creation_date(project
					.getProject_creation_date());
			projectTO.setAccount_id(project.getAccountMaster().getAccount_id());

			return Response.status(Response.Status.OK).entity(projectTO)
					.build();
		} catch (Exception e) {

			e.printStackTrace();
			projectTO = null;
			System.out
					.println("Error while getting Projects in Project Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectTO).build();
		}

	}
	
	//*******************************to get all projects name and project id under a portfolio and account*********12/12/17
		
	@GET
	@Path("allProjectsNamesAndIDInPortfolioAndAccount/{portfolioId}/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)	
	public List<AllocatedProjects>  getAllocatedProjectsForAccount(@PathParam("portfolioId") Integer portfolioId,@PathParam("accountId") Integer accountId) throws Exception{
		
		/*List<ProjectMaster>allocatedProjectsList=dashboardDao.getAllocatedProjectsForUser(user_id);
		
		List<AllocatedProjects>allocatedProjectNamesList=new ArrayList<AllocatedProjects>();

		AllocatedProjects  allocatedProjects;
		
		for(int i=0;i<allocatedProjectsList.size();i++){
			allocatedProjects=new AllocatedProjects();
			String projectName=allocatedProjectsList.get(i).getProject_name();
			allocatedProjects.setProjectName(projectName);
			allocatedProjectNamesList.add(allocatedProjects);
			projectName=null;
		}
		
		return  allocatedProjectNamesList;*/
		List<AllocatedProjects>allocatedProjectNamesList=new ArrayList<AllocatedProjects>();
		
		Object projectDetails[];

		AllocatedProjects  allocatedProjects;
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("portfolioId",portfolioId);
		queryMap.put("accountId",accountId);
		
		//List<ProjectMaster>allocatedProjectsList = new ArrayList<ProjectMaster>();
		List<Object>projectList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ALLOCATED_PROJECTS_FOR_PORTFOLIO_AND_ACCOUNT,queryMap);
		
		for(int i=0;i<projectList.size();i++){
			allocatedProjects=new AllocatedProjects();
			projectDetails=(Object[]) projectList.get(i);
			allocatedProjects.setProjectName(projectDetails[1].toString());
			allocatedProjects.setProject_id(Integer.parseInt(projectDetails[0].toString()));
			allocatedProjectNamesList.add(allocatedProjects);
		}
		
		
		
		return  allocatedProjectNamesList;
		
	}
	/*
	@GET
	@Path("allProjectsByName")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllProjectNames() {

		List<ProjectTO> projectMasterList = new ArrayList<ProjectTO>();
		Object projectMaster[];
		ProjectTO projectTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> projectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_PROJECT_NAMES, queryMap);

			if (projectList == null || projectList.size() == 0
					|| projectList.isEmpty() == true) {
				System.out.println("Projects not found");
				projectTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectTO).build();
			}

			for (int i = 0; i < projectList.size(); i++) {
				projectTO = new ProjectTO();
				projectMaster = (Object[]) projectList.get(i);
				projectTO.setProject_id((int) projectMaster[0]);
				projectTO.setProject_name((String)projectMaster[1]);
				

				projectMasterList.add(projectTO);
			}

			return Response.status(Response.Status.OK).entity(projectMasterList)
					.build();
		} catch (Exception e) {
			projectTO = null;
			System.out
					.println("Error while getting Projects in Project Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectTO).build();
		}

	}


	@GET
	@Path("allProjectNames")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchOnlyProjectNames() {

		List<ProjectTO> projectMasterList = new ArrayList<ProjectTO>();
		Object projectMaster[];
		ProjectTO projectTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> projectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_PROJECT_NAMES, queryMap);

			if (projectList == null || projectList.size() == 0
					|| projectList.isEmpty() == true) {
				System.out.println("Projects not found");
				projectTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectTO).build();
			}

			for (int i = 0; i < projectList.size(); i++) {
				projectTO = new ProjectTO();
				projectMaster = (Object[]) projectList.get(i);
				projectTO.setProject_id((int) projectMaster[0]);
				projectTO.setProject_name((String)projectMaster[1]);
				

				projectMasterList.add(projectTO);
			}

			return Response.status(Response.Status.OK).entity(projectMasterList)
					.build();
		} catch (Exception e) {
			projectTO = null;
			System.out
					.println("Error while getting Projects in Project Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectTO).build();
		}

	}
*/
	@POST
	@Path(value = "create/{accountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProject(ProjectMaster projectMaster,
			@PathParam("accountId") int accountId) {

		try {
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("accountId", accountId);

			AccountMaster accountMaster = (AccountMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_ACCOUNT_BY_ID,
							queryMap);
			
			if(accountMaster==null)
			{
				return Response.status(Response.Status.NOT_FOUND).entity("Account does not exist! Give valid Account ").build();
			}

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Instant instant = timestamp.toInstant();
		    //System.out.println(instant);
		    Date myDate = Date.from(instant);
		    
			projectMaster.setProject_creation_date(myDate);
			projectMaster.setAccountMaster(accountMaster);

			
			
						
			ProjectMaster proMst = (ProjectMaster) GenericDaoSingleton
					.getGenericDao().createOrUpdate(projectMaster);

			if (proMst != null) {
				return Response.status(Response.Status.OK)
						.entity("Project created Successfully").build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(proMst).build();

			}
		} catch (Exception e) {
			
			
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			if(e.getMessage().equals("Duplicate Row in table"))
			{
				return Response.status(Response.Status.CONFLICT).entity("Project Already Exists").build();
			}
			
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating Project").build();
			
//			
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//					.entity("Failed to create Project").build();
		}

	}

	@DELETE
	@Path(value = "/delete/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("projectId") int projectId) {
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("projectId", projectId);

		try {

			System.out.println("inside update deleteProject");
			ProjectMaster projectMaster = (ProjectMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_PROJECT, queryMap);

			if (projectMaster == null) {
				System.out.println("Project Does Not Exists");
				projectMaster = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Project Does Not Exists").build();
			}

			GenericDaoSingleton.getGenericDao().delete(ProjectMaster.class,
					projectMaster.getProject_id());

			return Response.status(Response.Status.OK)
					.entity("Project Deleted Successfully").build();
		} catch (Exception e) {

			System.out
					.println("Error while getting projects in Project Config service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Project Deletion Failed").build();
		}

	}

	@PUT
	@Path(value = "update/{accountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(ProjectMaster projectMaster,
			@PathParam("accountId") int accountId) {

		HashMap<String, Object> queryMapAccount = new HashMap<String, Object>();
		queryMapAccount.put("accountId", accountId);
		try {
			AccountMaster accountMaster = (AccountMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_ACCOUNT_BY_ID,
							queryMapAccount);

			projectMaster.setAccountMaster(accountMaster);

			HashMap<String, Object> queryMapProject = new HashMap<String, Object>();
			queryMapProject.put("projectId", projectMaster.getProject_id());
			ProjectMaster existingProjectMaster = (ProjectMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_PROJECT_BY_ID,
							queryMapProject);

			if(existingProjectMaster==null){
				return Response.status(Response.Status.NOT_FOUND)
						.entity("User Doesnt Exist").build();
			}
			
			existingProjectMaster.setProject_id(projectMaster.getProject_id());
			existingProjectMaster.setProject_name(projectMaster
					.getProject_name());
			existingProjectMaster.setProject_status(projectMaster
					.getProject_status());
			existingProjectMaster.setProject_creation_date(projectMaster
					.getProject_creation_date());
			existingProjectMaster.setAccountMaster(projectMaster
					.getAccountMaster());

			ProjectMaster projMst = (ProjectMaster) GenericDaoSingleton
					.getGenericDao().createOrUpdate(existingProjectMaster);

			if (projMst != null) {
				return Response.status(Response.Status.OK)
						.entity("Project updated Successfully").build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(projMst).build();

			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to update Project").build();
		}

	}

}
