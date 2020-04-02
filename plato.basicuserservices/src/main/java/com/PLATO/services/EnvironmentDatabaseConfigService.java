package com.PLATO.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
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
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.TEMDatabaseDetail;
import com.PLATO.userTO.TEMDatabaseDetailTO;

@Path("EnvironmentDatabaseConfigService")
public class EnvironmentDatabaseConfigService {
	
	private static final Logger logger=Logger.getLogger(EnvironmentDatabaseConfigService.class);
	
	@GET
	@Path("fetchAllDatabases")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllDatabases() {
		System.out.println("fetching all Databases");
		List<TEMDatabaseDetailTO> temDatabaseDetailTOList=new ArrayList<TEMDatabaseDetailTO>();
		List<Object> temDatabaseDetailList;
		//HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		//String queryString="SELECT ApplicationId, ApplicationName ,ApplicationURL FROM tem_applicationdetails  where projectMaster.project_id=:project_id";
		try {
			//keyvalueMap.put("project_id",projectId);
			temDatabaseDetailList=GenericDaoSingleton.getGenericDao().findAll(TEMDatabaseDetail.class);
			if(temDatabaseDetailList==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).build();
			}
			System.out.println("FOUND!!!!!!!!!!");
			Iterator<Object> it=temDatabaseDetailList.iterator();
			while(it.hasNext()) {
				TEMDatabaseDetailTO temDatabaseDetailTO=new TEMDatabaseDetailTO();
				TEMDatabaseDetail temp=(TEMDatabaseDetail)it.next();
				temDatabaseDetailTO.setDatabaseId(temp.getDatabaseId());
				temDatabaseDetailTO.setDatabaseName(temp.getDatabaseName());
				temDatabaseDetailTO.setDatabaseURL(temp.getDatabaseURL());
				temDatabaseDetailTO.setDatabasePort(temp.getDatabasePort());
				temDatabaseDetailTO.setUsername(temp.getUsername());
				temDatabaseDetailTO.setPassword(temp.getPassword());
				temDatabaseDetailTO.setPullingInterval(temp.getPullingInterval());
				temDatabaseDetailTO.setMonitoringStatus(temp.getMonitoringStatus());
				temDatabaseDetailTO.setDatabaseDriver(temp.getDatabaseDriver());
				temDatabaseDetailTOList.add(temDatabaseDetailTO);
			}
			System.out.println("iterator has been used");
			return Response.status(Response.Status.OK).entity(temDatabaseDetailTOList).build();
		}catch(Exception e) {
			System.out.println("Error while fetching project");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}	
	
	@GET
	@Path("fetchAllDatabaseDetailProjectWise/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllDatabaseDetailProjectWise(@PathParam("projectId") int projectId) {
		System.out.println("showing database details for project ID "+projectId);
		List<TEMDatabaseDetailTO> temDatabaseDetailTOList=new ArrayList<TEMDatabaseDetailTO>();
		List<Object> temDatabaseDetailList;
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		String queryString="SELECT tm.databaseId, tm.databaseName ,tm.databaseURL,tm.databasePort,tm.username,tm.password,tm.pullingInterval,tm.monitoringStatus,tm.databaseDriver FROM TEMDatabaseDetail tm where tm.projectMaster.project_id=:projectId";
		try {
			keyvalueMap.put("projectId",projectId);
			temDatabaseDetailList=GenericDaoSingleton.getGenericDao().findByQuery(queryString, keyvalueMap);
			if(temDatabaseDetailList==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).build();
			}
			Iterator<Object> it=temDatabaseDetailList.iterator();
			while(it.hasNext()) {
				TEMDatabaseDetailTO temDatabaseDetailTO=new TEMDatabaseDetailTO();
				Object[] temp=(Object[])it.next();
				if(temp[0]!=null)
					temDatabaseDetailTO.setDatabaseId(Integer.parseInt(String.valueOf(temp[0])));
				temDatabaseDetailTO.setDatabaseName(String.valueOf(temp[1]));
				temDatabaseDetailTO.setDatabaseURL(String.valueOf(temp[2]));
				temDatabaseDetailTO.setDatabasePort(String.valueOf(temp[3]));
				temDatabaseDetailTO.setUsername(String.valueOf(temp[4]));
				temDatabaseDetailTO.setPassword(String.valueOf(temp[5]));
				if(temp[6]!=null)
					temDatabaseDetailTO.setPullingInterval(Integer.parseInt(temp[6].toString()));
				temDatabaseDetailTO.setMonitoringStatus(String.valueOf(temp[7]));
				temDatabaseDetailTO.setDatabaseDriver(String.valueOf(temp[8]));
				temDatabaseDetailTOList.add(temDatabaseDetailTO);
			}
			return Response.status(Response.Status.OK).entity(temDatabaseDetailTOList).build();
		}catch(Exception e) {
			System.out.println("Error while fetching Application Details");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	
	
	
	
	
	
	//fetch database by Id for manipulation
		@GET
		@Path("fetchDatabse/{dbId}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response fetchApplicationById(@PathParam("dbId") int dbId) {
			
			logger.info("fetching database");
			try{	
				HashMap<String,Object> queryMap=new HashMap<String,Object >();
				queryMap.put("dbId", dbId);
			
				TEMDatabaseDetail temDbDetail=(TEMDatabaseDetail) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_DATABASE_BY_ID,queryMap);
				
				
				
				logger.info("fetching database done");
				
				if(temDbDetail==null){
					//logger.debug("Application "+temApplicationDetailbNew.getApplicationName()+" Created Successfully ");
					return Response.status(Response.Status.CREATED)
			        .entity("Failed to fetch database").build();		
				}
				
				TEMDatabaseDetailTO temDatabaseDetailTO=new TEMDatabaseDetailTO();
				

				temDatabaseDetailTO.setDatabaseId(temDbDetail.getDatabaseId());
				temDatabaseDetailTO.setDatabaseName(temDbDetail.getDatabaseName());
				temDatabaseDetailTO.setDatabaseURL(temDbDetail.getDatabaseURL());
				temDatabaseDetailTO.setDatabaseDriver(temDbDetail.getDatabaseDriver());
				temDatabaseDetailTO.setUsername(temDbDetail.getUsername());
				temDatabaseDetailTO.setPassword(temDbDetail.getPassword());
				temDatabaseDetailTO.setDatabasePort(temDbDetail.getDatabasePort());
				
				return Response.status(Response.Status.CREATED)
				        .entity(temDatabaseDetailTO).build();		
				
			
			}catch(Exception e){
				
				logger.error("Error in fetching Database "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Database Dosen't Exist").build();	
			}
			
			
			
		}
	
	
	//nilesh
	
	//database
	
	
	
	
		@POST
		@Path(value="create/{projectId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response createDatabase(TEMDatabaseDetail temDatabaseDetail,@PathParam ("projectId") int projectId){

		logger.info("Creating Database ");
		try{	
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("projectId", projectId);
		
		logger.info("Reftrieving project for given Database ");
		ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB, queryMap);
		
		temDatabaseDetail.setProjectMaster(projectMaster);
		
		logger.debug("project "+projectMaster.getProject_name()+" instance set to database");
		
		
		TEMDatabaseDetail temDatabaseDetailNew=(TEMDatabaseDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(temDatabaseDetail);
		//logger.debug("database password is "+temDatabaseDetail.getPassword());
		
		if(temDatabaseDetailNew!=null){
			logger.debug("Server "+temDatabaseDetailNew.getDatabaseName()+" Created Successfully ");
			return Response.status(Response.Status.CREATED)
	        .entity("Database created Successfully").build();		
		}else{
			return Response.status(Response.Status.NOT_FOUND)
			        .entity(temDatabaseDetailNew).build();	
			
		}
		}catch(Exception e){
			//e.printStackTrace();
			logger.error("Error in creating Database "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			        .entity("Failed to create Database").build();	
		}
		
		//return jsonObj;
	}
		
		
		
		
		@GET
		@Path(value="delete/{databaseId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteDatabase(@PathParam("databaseId")int databaseId){
			
			logger.info("Deleting Database ");
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		//	keyvalueMap.put("portfolioName", portfolioName);
			try
			{
		//	PortfolioMaster portfolioMst=(PortfolioMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_REQ_PORTFOLIO_TO_DELETE, keyvalueMap);
		
				GenericDaoSingleton.getGenericDao().delete(TEMDatabaseDetail.class, databaseId);
				logger.debug("Database "+databaseId+" deleted successfully ");
			return Response.status(Response.Status.OK)
		        .entity("Database deleted Successfully").build();		
			
			}catch(Exception e){
				logger.error("error in deleting Database "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Failed to delete Database").build();	
			}
			
		}
		
	
	
		
		
		
		//update
		
		@PUT
		@Path(value="updateDb/{projectId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response updateDatabase(TEMDatabaseDetail temDatabaseDetail,@PathParam ("projectId") int projectId){
			
			HashMap<String,Object> queryMapProject=new HashMap<String,Object >();
			queryMapProject.put("projectId", projectId);
			
			logger.info("Updating Database ");
			
			try{
			
			logger.info("Retrieving project for given Database");	
			ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB, queryMapProject);
			logger.debug("Project "+projectMaster.getProject_name()+" Retrieved successfully for Database "+temDatabaseDetail.getDatabaseId());
			temDatabaseDetail.setProjectMaster(projectMaster);
			
			HashMap<String,Object> queryMapDatabase=new HashMap<String,Object >();
			queryMapDatabase.put("databaseId", temDatabaseDetail.getDatabaseId());
			TEMDatabaseDetail existingDatabaseDetail=(TEMDatabaseDetail) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_DATABASE_TO_UPDATE, queryMapDatabase);
			
			
			existingDatabaseDetail.setDatabaseName(temDatabaseDetail.getDatabaseName());
			existingDatabaseDetail.setDatabaseURL(temDatabaseDetail.getDatabaseURL());
			
			existingDatabaseDetail.setUsername(temDatabaseDetail.getUsername());
			existingDatabaseDetail.setDatabaseDriver(temDatabaseDetail.getDatabaseDriver());
			existingDatabaseDetail.setDatabasePort(temDatabaseDetail.getDatabasePort());
			existingDatabaseDetail.setPassword(temDatabaseDetail.getPassword());
			
			/*existingAccountMaster.setAccount_status("Active");*/
			/*existingAccountMaster.setBackground_image(accountMaster.getBackground_image());
			existingAccountMaster.setPortfolioMaster(accountMaster.getPortfolioMaster());*/
			
			TEMDatabaseDetail databaseDetail=(TEMDatabaseDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(existingDatabaseDetail);
			
			if(databaseDetail!=null){
				logger.debug("Database "+existingDatabaseDetail.getDatabaseId()+" updated Successfully");
				return Response.status(Response.Status.CREATED)
		        .entity("Database is Updated Successfully").build();		
			}else{
				
				return Response.status(Response.Status.NOT_FOUND)
				        .entity(databaseDetail).build();	
				
			}
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Error in Updating Database "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Failed to Update Database").build();	
			}
			
		}
	
	
	
	

}
