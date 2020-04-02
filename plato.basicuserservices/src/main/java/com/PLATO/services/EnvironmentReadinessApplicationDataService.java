package com.PLATO.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.TEMApplicationDetail;
import com.PLATO.userTO.AccountTO;
import com.PLATO.userTO.ApplicationDataTO;

@Path("EnvironmentReadinessApplicationDataService")
public class EnvironmentReadinessApplicationDataService {
	private static final Logger logger=Logger.getLogger(EnvironmentReadinessApplicationDataService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value="fetchApplicationData/{projectId}")
	public Response getEnvironmentReadinessApplicationDataService(@PathParam("projectId") int projectId){
		
		//	TEMApplicationThread.temAppToList=null;
			logger.info("checking for application up ");
			
			logger.debug("retriving Applications for project "+projectId);
			
			List<AccountTO>accountTOList=new ArrayList<AccountTO>();
			HashMap<String,Object> queryMap=new HashMap<String,Object >();
			List<ApplicationDataTO>temAppToList=new ArrayList<ApplicationDataTO>();
			try
			{
			queryMap.put("projectId", projectId);
			
			
			
			List<Object>applicationList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_APPLICATIONS_TO_MONITOR,queryMap);
			
			if(applicationList.size()<1){
				logger.debug(" No Applications to check for project "+projectId);
				temAppToList=null;
				return Response.status(Response.Status.OK).entity(temAppToList).build();
			}
			
			TEMApplicationDetail temAppDetail;
			
			for(int i=0;i<applicationList.size();i++){
				
				temAppDetail=(TEMApplicationDetail) applicationList.get(i);
				
				ApplicationDataTO temAppTo=new ApplicationDataTO();
				
				temAppTo.setApplicationName(temAppDetail.getApplicationName());
				temAppTo.setApplicationUrl(temAppDetail.getApplicationURL());
				
//				String appUrl=temAppDetail.getApplicationURL();
//				
//				String appStatus=TEMApplicationThread.getApplicationStatus(appUrl);
//				
//				temAppTo.setApplicationStatus(appStatus);
				
				temAppToList.add(temAppTo);
				/*TEMApplicationThread temThread=new TEMApplicationThread(appUrl, temAppDetail.getApplicationName(), "");
				temThread.start();*/
				
				
				
				//update application status in database
//				try
//				{
//					
//					
//					temAppDetail.setMonitoringStatus(appStatus);
//					TEMApplicationDetail temApplicationDetailNew=(TEMApplicationDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(temAppDetail);
//					
//					if(temApplicationDetailNew!=null){
//						logger.info("Application status Updated successfully");		
//					}
//					}catch(Exception e){
//						logger.error("Error in updating Application "+temAppDetail.getApplicationName()+" "+e.getMessage());
//					}
//			
//				
//				}
			
			//temAppToList=TEMApplicationThread.temAppToList;
				
				/*while(true){
					if(applicationList.size()==TEMApplicationThread.temAppToList.size());
					{
						temAppToList=TEMApplicationThread.temAppToList;
						System.out.println(applicationList.size()==TEMApplicationThread.temAppToList.size());
						break;
					}
				}*/
			logger.info("checking Applications for up done successfully");
			
			}
			}catch(Exception e){
				temAppToList=null;
				logger.error("error while fetching Applications list "+e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(temAppToList).build();
				
			}
			
			return Response.status(Response.Status.OK).entity(temAppToList).build();
			
		}
		
	

}
