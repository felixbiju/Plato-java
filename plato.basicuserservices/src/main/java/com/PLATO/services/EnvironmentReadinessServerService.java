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
import com.PLATO.Threads.TEMServerThread;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.TEMServerDetail;
import com.PLATO.userTO.TEMServerTO;

@Path(value="EnvironmentReadinessServerService")
public class EnvironmentReadinessServerService {



	private static final Logger logger=Logger.getLogger(EnvironmentReadinessServerService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value="fetchServerDetail/{projectId}")
	public Response getEnvironmentReadinessServerService(@PathParam("projectId") int projectId){
		
	
		logger.info("checking for Server up ");
		
		logger.debug("retriving Servers for project "+projectId);
		
	
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<TEMServerTO>temServerToList=new ArrayList<TEMServerTO>();
		try
		{
		queryMap.put("projectId", projectId);
		
		
		
		List<Object>serverList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.FETCH_SERVERS_TO_MONITOR,queryMap);
		
		if(serverList.size()<1){
			logger.debug(" No Servers to check for project "+projectId);
			temServerToList=null;
			return Response.status(Response.Status.OK).entity(temServerToList).build();
		}
		
		
		TEMServerDetail temServerDetail;
		
		for(int i=0;i<serverList.size();i++){
			
			temServerDetail=(TEMServerDetail) serverList.get(i);
			
			TEMServerTO temServerTo=new TEMServerTO();
			
			temServerTo.setServerName(temServerDetail.getServerName());
			temServerTo.setServerUrl(temServerDetail.getServerURL());
			
			String serverUrl=temServerDetail.getServerURL();
			
			String serverStatus=TEMServerThread.getServerStatus(serverUrl);
			
			temServerTo.setServerStatus(serverStatus);
			
			temServerToList.add(temServerTo);
			/*TEMApplicationThread temThread=new TEMApplicationThread(appUrl, temAppDetail.getApplicationName(), "");
			temThread.start();*/
			
			
			
			//update server status in database
			try
			{
				temServerDetail.setMonitoringStatus(serverStatus);
				TEMServerDetail temServerDetailNew=(TEMServerDetail) GenericDaoSingleton.getGenericDao().createOrUpdate(temServerDetail);
			
			if(temServerDetailNew!=null){
				logger.info("Server status Updated successfully");		
			}
			}catch(Exception e){
				logger.error("Error in updating Server "+temServerDetail.getServerName()+" "+e.getMessage());
			}
			
				
				/*HashMap<String,Object> queryMap1=new HashMap<String,Object >();
				queryMap1.put("serverStatus", serverStatus);
				queryMap1.put("serverId", temServerDetail.getServerId());
				
				temServerDetail.setMonitoringStatus(serverStatus);
				int res=GenericDaoSingleton.getGenericDao().updateQuery(ConstantQueries.UPDATE_DATABASESS_STATUS,queryMap1);
			
			if(res>0){
				logger.info("Database status Updated successfully");		
			}
			}catch(Exception e){
				logger.error("Error in updating Server "+temServerDetail.getServerName()+" "+e.getMessage());
			}*/
			
			
			
			
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
		logger.info("checking Servers for up done successfully");
		
		}catch(Exception e){

			temServerToList=null;
			logger.error("error while fetching servers list "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(temServerToList).build();
		
		}
		
		return Response.status(Response.Status.OK).entity(temServerToList).build();
		
	}
	

}
