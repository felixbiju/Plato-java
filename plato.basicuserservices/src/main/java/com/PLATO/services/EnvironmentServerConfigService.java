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
import com.PLATO.entities.TEMServerDetail;
import com.PLATO.userTO.TEMServerDetailTO;

@Path("EnvironmentServerConfigService")
public class EnvironmentServerConfigService {
	
	private static final Logger logger=Logger.getLogger(EnvironmentServerConfigService.class);
	
	@GET
	@Path("fetchAllServers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllServers() {
		System.out.println("fetching all servers");
		List<TEMServerDetailTO> temServerDetailTOList=new ArrayList<TEMServerDetailTO>();
		List<Object> temServerDetailList;
		//HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		//String queryString="SELECT ApplicationId, ApplicationName ,ApplicationURL FROM tem_applicationdetails  where projectMaster.project_id=:project_id";
		try {
			//keyvalueMap.put("project_id",projectId);
			temServerDetailList=GenericDaoSingleton.getGenericDao().findAll(TEMServerDetail.class);
			if(temServerDetailList==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).build();
			}
			System.out.println("FOUND!!!!!!!!!!");
			Iterator<Object> it=temServerDetailList.iterator();
			while(it.hasNext()) {
				TEMServerDetailTO temServerDetailTO=new TEMServerDetailTO();
				TEMServerDetail temp=(TEMServerDetail)it.next();
				temServerDetailTO.setServerId(temp.getServerId());
				temServerDetailTO.setServerName(temp.getServerName());
				temServerDetailTO.setServerURL(temp.getServerURL());
				temServerDetailTO.setPullingInterval(temp.getPullingInterval());
				temServerDetailTO.setMonitoringStatus(temp.getMonitoringStatus());
				temServerDetailTOList.add(temServerDetailTO);
			}
			System.out.println("iterator has been used");
			return Response.status(Response.Status.OK).entity(temServerDetailTOList).build();
		}catch(Exception e) {
			System.out.println("Error while fetching project");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}	

	//////////////////////
	//this is to get all the list of servers project wise
	@GET
	@Path("fetchAllServersProjectWise/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllServersProjectWise(@PathParam("projectId") int projectId) {
		System.out.println("showing server details for project ID "+projectId);
		List<TEMServerDetailTO> temServerDetailTOList=new ArrayList<TEMServerDetailTO>();
		List<Object> temServerDetailList;
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		String queryString="SELECT tm.serverId, tm.serverName ,tm.serverURL,tm.pullingInterval,tm.monitoringStatus FROM TEMServerDetail tm where tm.projectMaster.project_id=:projectId";
		try {
			keyvalueMap.put("projectId",projectId);
			temServerDetailList=GenericDaoSingleton.getGenericDao().findByQuery(queryString, keyvalueMap);
			if(temServerDetailList==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).build();
			}
			Iterator<Object> it=temServerDetailList.iterator();
			while(it.hasNext()) {
				TEMServerDetailTO temServerDetailTO=new TEMServerDetailTO();
				Object[] temp=(Object[])it.next();
				temServerDetailTO.setServerId(Integer.parseInt(temp[0].toString()));
				temServerDetailTO.setServerName(temp[1].toString());
				temServerDetailTO.setServerURL(temp[2].toString());
				temServerDetailTO.setPullingInterval(Integer.parseInt(temp[3].toString()));
				if(temp[4]!=null)
					temServerDetailTO.setMonitoringStatus(temp[4].toString());
				temServerDetailTOList.add(temServerDetailTO);
			}
			return Response.status(Response.Status.OK).entity(temServerDetailTOList).build();
		}catch(Exception e) {
			System.out.println("Error while fetching project");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	
	
	
	//fetch application by Id for manipulation
		@GET
		@Path("fetchServer/{serverId}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response fetchApplicationById(@PathParam("serverId") int serverId) {
			
			logger.info("fetching Server");
			try{	
				HashMap<String,Object> queryMap=new HashMap<String,Object >();
				queryMap.put("serverId", serverId);
			
				TEMServerDetail temServerDetail=(TEMServerDetail) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_SERVER_BY_ID,queryMap);
				
				
				
				logger.info("fetching Server done");
				
				if(temServerDetail==null){
					//logger.debug("Application "+temApplicationDetailbNew.getApplicationName()+" Created Successfully ");
					return Response.status(Response.Status.CREATED)
			        .entity("Failed to fetch Server").build();		
				}
				
				TEMServerDetailTO temServerDetailTO=new TEMServerDetailTO();
				

				temServerDetailTO.setServerId(temServerDetail.getServerId());
				temServerDetailTO.setServerName(temServerDetail.getServerName());
				temServerDetailTO.setServerURL(temServerDetail.getServerURL());
				temServerDetailTO.setPullingInterval(temServerDetail.getPullingInterval());
				
				return Response.status(Response.Status.CREATED)
				        .entity(temServerDetailTO).build();		
				
			
			}catch(Exception e){
				//e.printStackTrace();
				
				logger.error("Error in fetching Server "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Server Dosen't Exist").build();	
			}
			
			
			
		}
	
	
	
	//nilesh
	
	//server
	
	
	
		@POST
		@Path(value="create/{projectId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response createServer(TEMServerDetail temServerDetail,@PathParam ("projectId") int projectId){

		logger.info("Creating Server ");
		try{	
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("projectId", projectId);
		
		logger.info("Reftrieving project for given Server ");
		ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB, queryMap);
		
		temServerDetail.setProjectMaster(projectMaster);
		
		logger.debug("project "+projectMaster.getProject_name()+" instance set to server");
		
		
		TEMServerDetail temServerDetailNew=(TEMServerDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(temServerDetail);
		
		
		if(temServerDetailNew!=null){
			logger.debug("Server "+temServerDetailNew.getServerName()+" Created Successfully ");
			return Response.status(Response.Status.CREATED)
	        .entity("Server created Successfully").build();		
		}else{
			return Response.status(Response.Status.NOT_FOUND)
			        .entity(temServerDetailNew).build();	
			
		}
		}catch(Exception e){
			//e.printStackTrace();
			logger.error("Error in creating Server "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			        .entity("Failed to create Server").build();	
		}
		
		//return jsonObj;
	}
		
		
		
		
		@GET
		@Path(value="delete/{serverId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteServer(@PathParam("serverId")int serverId){
			
			logger.info("Deleting Server ");
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		//	keyvalueMap.put("portfolioName", portfolioName);
			try
			{
		//	PortfolioMaster portfolioMst=(PortfolioMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_REQ_PORTFOLIO_TO_DELETE, keyvalueMap);
		
				GenericDaoSingleton.getGenericDao().delete(TEMServerDetail.class, serverId);
				logger.debug("Server "+serverId+" deleted successfully ");
			return Response.status(Response.Status.OK)
		        .entity("Server deleted Successfully").build();		
			
			}catch(Exception e){
				logger.error("error in deleting Server "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Failed to delete Server").build();	
			}
			
		}
		
	
	
		
		
		
		//update
		
		@PUT
		@Path(value="updateServer/{projectId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response updateServer(TEMServerDetail temServerDetail,@PathParam ("projectId") int projectId){
			
			HashMap<String,Object> queryMapProject=new HashMap<String,Object >();
			queryMapProject.put("projectId", projectId);
			
			logger.info("Updating Server ");
			
			try{
			
			logger.info("Retrieving project for given Server");	
			ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB, queryMapProject);
			logger.debug("Project "+projectMaster.getProject_name()+" Retrieved successfully for Server "+temServerDetail.getServerId());
			temServerDetail.setProjectMaster(projectMaster);
			
			HashMap<String,Object> queryMapServer=new HashMap<String,Object >();
			queryMapServer.put("serverId", temServerDetail.getServerId());
			
			TEMServerDetail existingServerDetail=(TEMServerDetail) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_SERVER_TO_UPDATE, queryMapServer);
			
			
			existingServerDetail.setServerName(temServerDetail.getServerName());
			existingServerDetail.setServerURL(temServerDetail.getServerURL());
			existingServerDetail.setPullingInterval(temServerDetail.getPullingInterval());
			
			System.out.println("............."+temServerDetail.getPullingInterval());
			
			/*existingAccountMaster.setAccount_status("Active");*/
			/*existingAccountMaster.setBackground_image(accountMaster.getBackground_image());
			existingAccountMaster.setPortfolioMaster(accountMaster.getPortfolioMaster());*/
			
			TEMServerDetail serverDetail=(TEMServerDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(existingServerDetail);
			
			if(serverDetail!=null){
				logger.debug("Server "+existingServerDetail.getServerId()+" updated Successfully");
				return Response.status(Response.Status.CREATED)
		        .entity("Server is Updated Successfully").build();		
			}else{
				
				return Response.status(Response.Status.NOT_FOUND)
				        .entity(serverDetail).build();	
				
			}
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Error in Updating Server "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Failed to Update Server").build();	
			}
			
		}
		
		
		
		
		
	

}
