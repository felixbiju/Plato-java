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

import com.PLATO.Threads.TEMDatabaseThread;
import com.PLATO.constants.ConstantQueries;

import com.PLATO.entities.TEMDatabaseDetail;

import com.PLATO.userTO.TEMDatabaseTO;

@Path(value="EnvironmentReadinessDatabaseService")
public class EnvironmentReadinessDatabaseService {


	private static final Logger logger=Logger.getLogger(EnvironmentReadinessDatabaseService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value="fetchatabaseDetail/{projectId}")
	public Response getEnvironmentReadinessDatabaseService(@PathParam("projectId") int projectId){
		
		
		logger.info("checking for database up ");
		
		logger.debug("retriving Databases for project "+projectId);
		
		
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<TEMDatabaseTO>temDbToList=new ArrayList<TEMDatabaseTO>();
		try
		{
		queryMap.put("projectId", projectId);
		
		
		
		List<Object>databaseList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_DATABASES_TO_MONITOR,queryMap);
		
		TEMDatabaseDetail temDbDetail;
		
		if(databaseList.size()<1){
			logger.debug(" No databases to check for project "+projectId);
			temDbToList=null;
			return Response.status(Response.Status.OK).entity(temDbToList).build();
		}
		
		for(int i=0;i<databaseList.size();i++){
			
			temDbDetail=(TEMDatabaseDetail) databaseList.get(i);
			
			TEMDatabaseTO temDbTO=new TEMDatabaseTO();
			
			temDbTO.setDatabaseName(temDbDetail.getDatabaseName());
			temDbTO.setDatabaseUrl(temDbDetail.getDatabaseURL());
			
			String dbUrl=temDbDetail.getDatabaseURL();
			
			String dbStatus=TEMDatabaseThread.getDatabaseStatus(dbUrl, temDbDetail.getUsername(), temDbDetail.getPassword(), temDbDetail.getDatabaseDriver());
			
			temDbTO.setDatabaseStatus(dbStatus);
			
			temDbToList.add(temDbTO);
			
			
			
			//update status in database
			try
			{
				temDbDetail.setMonitoringStatus(dbStatus);
			TEMDatabaseDetail temDbDetailNew=(TEMDatabaseDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(temDbDetail);
			
			if(temDbDetailNew!=null){
				logger.info("Database status Updated successfully");		
			}
			}catch(Exception e){
				logger.error("Error in updating database "+temDbDetail.getDatabaseName()+" "+e.getMessage());
			}
				
				

			/*	HashMap<String,Object> queryMap1=new HashMap<String,Object >();
				queryMap1.put("dbStatus", dbStatus);
				queryMap1.put("databaseId", temDbDetail.getDatabaseId());
				
				temDbDetail.setMonitoringStatus(dbStatus);
				int res=GenericDaoSingleton.getGenericDao().updateQuery(ConstantQueries.UPDATE_DATABASESS_STATUS,queryMap1);
			
			if(res>0){
				logger.info("Database status Updated successfully");		
			}
			}catch(Exception e){
				logger.error("Error in updating database "+temDbDetail.getDatabaseName()+" "+e.getMessage());
			}*/
				
			
			
			
			/*TEMApplicationThread temThread=new TEMApplicationThread(appUrl, temAppDetail.getApplicationName(), "");
			temThread.start();*/
			
			}
		
		//temAppToList=TEMApplicationThread.temAppToList;
			
			/*while(true){
				if(applicationList.size()==TEMApplicationThread.temAppToList.size());
				{
					temAppToList=TEMApplicationThread.temAppToList;
					System.out.println(applicationList.size()==TEMApplicationThread.temAppToList.size());
					break;
				}
			}*/
		
			logger.info("checking databases for up done successfully");
		
		}catch(Exception e){
			temDbToList=null;
			logger.error("error while fetching database list "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(temDbToList).build();
		}
		
		return Response.status(Response.Status.OK).entity(temDbToList).build();
		
	}
	

}
