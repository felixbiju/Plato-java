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
import com.PLATO.entities.TEMApplicationDetail;
import com.PLATO.userTO.TEMApplicationDetailTO;

@Path("EnvironmentApplicationConfigService")
public class EnvironmentApplicationConfigService {
	
	
	private static final Logger logger=Logger.getLogger(EnvironmentApplicationConfigService.class);
	
	@GET
	@Path("fetchAllApplications")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllApplications() {
		System.out.println("fetching all applications");
		List<TEMApplicationDetailTO> temApplicationDetailTOList=new ArrayList<TEMApplicationDetailTO>();
		List<Object> temApplicationDetailList;
		//HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		//String queryString="SELECT ApplicationId, ApplicationName ,ApplicationURL FROM tem_applicationdetails  where projectMaster.project_id=:project_id";
		try {
			//keyvalueMap.put("project_id",projectId);
			temApplicationDetailList=GenericDaoSingleton.getGenericDao().findAll(TEMApplicationDetail.class);
			if(temApplicationDetailList==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).build();
			}
			System.out.println("FOUND!!!!!!!!!!");
			Iterator<Object> it=temApplicationDetailList.iterator();
			while(it.hasNext()) {
				TEMApplicationDetailTO temApplicationDetailTO=new TEMApplicationDetailTO();
				TEMApplicationDetail temp=(TEMApplicationDetail)it.next();
				temApplicationDetailTO.setApplicationId(temp.getApplicationId());
				temApplicationDetailTO.setApplicationName(temp.getApplicationName());
				temApplicationDetailTO.setApplicationURL(temp.getApplicationURL());
				temApplicationDetailTO.setPullingInterval(temp.getPullingInterval());
				System.out.println(">>interval..."+temp.getPullingInterval());
				temApplicationDetailTOList.add(temApplicationDetailTO);
			}
			System.out.println("iterator has been used");
			return Response.status(Response.Status.OK).entity(temApplicationDetailTOList).build();
		}catch(Exception e) {
			System.out.println("Error while fetching project");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	@GET
	@Path("fetchAllApplicationProjectWise/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllApplicationProjectWise(@PathParam("projectId") int projectId) {
		System.out.println("showing Application details for project ID "+projectId);
		List<TEMApplicationDetailTO> temApplicationDetailTOList=new ArrayList<TEMApplicationDetailTO>();
		List<Object> temApplicationDetailList;
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		String queryString="SELECT tm.applicationId, tm.applicationName ,tm.applicationURL, tm.pullingInterval FROM TEMApplicationDetail tm where tm.projectMaster.project_id=:projectId";
		try {
			keyvalueMap.put("projectId",projectId);
			temApplicationDetailList=GenericDaoSingleton.getGenericDao().findByQuery(queryString, keyvalueMap);
			if(temApplicationDetailList==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).build();
			}
			Iterator<Object> it=temApplicationDetailList.iterator();
			while(it.hasNext()) {
				TEMApplicationDetailTO temApplicationDetailTO=new TEMApplicationDetailTO();
				Object[] temp=(Object[])it.next();
				temApplicationDetailTO.setApplicationId(Integer.parseInt(temp[0].toString()));
				temApplicationDetailTO.setApplicationName(temp[1].toString());
				temApplicationDetailTO.setApplicationURL(temp[2].toString());
				temApplicationDetailTO.setPullingInterval(Integer.parseInt(temp[3].toString()));
				temApplicationDetailTOList.add(temApplicationDetailTO);
			}
			return Response.status(Response.Status.OK).entity(temApplicationDetailTOList).build();
		}catch(Exception e) {
			System.out.println("Error while fetching project");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	
	//fetch application by Id for manipulation
	@GET
	@Path("fetchApllication/{appId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchApplicationById(@PathParam("appId") int appId) {
		
		logger.info("fetching application");
		try{	
			HashMap<String,Object> queryMap=new HashMap<String,Object >();
			queryMap.put("appId", appId);
		
			TEMApplicationDetail temAppDetail=(TEMApplicationDetail) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_APPLICATION_BY_ID,queryMap);
			
			
			
			logger.info("fetching application done");
			
			if(temAppDetail==null){
				//logger.debug("Application "+temApplicationDetailbNew.getApplicationName()+" Created Successfully ");
				return Response.status(Response.Status.CREATED)
		        .entity("Failed to fetch Application").build();		
			}
			
			TEMApplicationDetailTO temApplicationDetailTO=new TEMApplicationDetailTO();
			

			temApplicationDetailTO.setApplicationId(temAppDetail.getApplicationId());
			temApplicationDetailTO.setApplicationName(temAppDetail.getApplicationName());
			temApplicationDetailTO.setApplicationURL(temAppDetail.getApplicationURL());
			temApplicationDetailTO.setPullingInterval(temAppDetail.getPullingInterval());
			
			return Response.status(Response.Status.CREATED)
			        .entity(temApplicationDetailTO).build();		
			
		
		}catch(Exception e){
			//e.printStackTrace();
			
			logger.error("Error in fetching Application "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			        .entity("Application Dosen't Exist").build();	
		}
		
		
		
	}
	
	
	
	
	
	
	@POST
	@Path(value="create/{projectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(TEMApplicationDetail temApplicationDetail,@PathParam ("projectId") int projectId){

	logger.info("Creating Application ");
	try{	
	HashMap<String,Object> queryMap=new HashMap<String,Object >();
	queryMap.put("projectId", projectId);
	
	logger.info("Reftrieving project for given application ");
	ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB, queryMap);
	
	temApplicationDetail.setProjectMaster(projectMaster);
	
	logger.debug("project "+projectMaster.getProject_name()+" instance set to application");
	
	
	TEMApplicationDetail temApplicationDetailbNew=(TEMApplicationDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(temApplicationDetail);
	
	
	if(temApplicationDetailbNew!=null){
		logger.debug("Application "+temApplicationDetailbNew.getApplicationName()+" Created Successfully ");
		return Response.status(Response.Status.CREATED)
        .entity("Application created Successfully").build();		
	}else{
		return Response.status(Response.Status.NOT_FOUND)
		        .entity(temApplicationDetailbNew).build();	
		
	}
	}catch(Exception e){
		//e.printStackTrace();
		logger.error("Error in creating Application "+e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		        .entity("Failed to create Application").build();	
	}
	
	//return jsonObj;
}
	
	
	
	
	@GET
	@Path(value="delete/{applicationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(@PathParam("applicationId")int applicationId){
		
		logger.info("Deleting Application ");
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
	//	keyvalueMap.put("portfolioName", portfolioName);
		try
		{
	//	PortfolioMaster portfolioMst=(PortfolioMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_REQ_PORTFOLIO_TO_DELETE, keyvalueMap);
	
			GenericDaoSingleton.getGenericDao().delete(TEMApplicationDetail.class, applicationId);
			logger.debug("Application "+applicationId+" deleted successfully ");
		return Response.status(Response.Status.OK)
	        .entity("Application deleted Successfully").build();		
		
		}catch(Exception e){
			logger.error("error in deleting server "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			        .entity("Failed to delete Application").build();	
		}
		
	}
	
	
	
	//update
	
	@PUT
	@Path(value="updateApp/{projectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(TEMApplicationDetail temApplicationDetail,@PathParam ("projectId") int projectId){
		
		HashMap<String,Object> queryMapProject=new HashMap<String,Object >();
		queryMapProject.put("projectId", projectId);
		
		logger.info("Updating Application ");
		
		try{
		
		logger.info("Retrieving project for given Application");	
		ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB, queryMapProject);
		logger.debug("Project "+projectMaster.getProject_name()+" Retrieved successfully for Application "+temApplicationDetail.getApplicationId());
		temApplicationDetail.setProjectMaster(projectMaster);
		
		HashMap<String,Object> queryMapApplication=new HashMap<String,Object >();
		queryMapApplication.put("applicationId", temApplicationDetail.getApplicationId());
		TEMApplicationDetail existingApplicationDetail=(TEMApplicationDetail) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_APPLICATION_TO_UPDATE, queryMapApplication);
		
		
		existingApplicationDetail.setApplicationName(temApplicationDetail.getApplicationName());
		existingApplicationDetail.setApplicationURL(temApplicationDetail.getApplicationURL());
		existingApplicationDetail.setPullingInterval(temApplicationDetail.getPullingInterval());
		
		/*existingAccountMaster.setAccount_status("Active");*/
		/*existingAccountMaster.setBackground_image(accountMaster.getBackground_image());
		existingAccountMaster.setPortfolioMaster(accountMaster.getPortfolioMaster());*/
		
		TEMApplicationDetail applicationDetail=(TEMApplicationDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(existingApplicationDetail);
		
		if(applicationDetail!=null){
			logger.debug("Application "+existingApplicationDetail.getApplicationId()+" updated Successfully");
			return Response.status(Response.Status.CREATED)
	        .entity("Application is Updated Successfully").build();		
		}else{
			
			return Response.status(Response.Status.NOT_FOUND)
			        .entity(applicationDetail).build();	
			
		}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("Error in Updating Application "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			        .entity("Failed to Update Application").build();	
		}
		
	}
	
	
	
	
	
	

}
