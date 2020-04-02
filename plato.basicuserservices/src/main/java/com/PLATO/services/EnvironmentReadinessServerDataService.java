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
import com.PLATO.entities.TEMServerDetail;
import com.PLATO.userTO.ServerDataTO;


@Path("EnvironmentReadinessServerDataService")
public class EnvironmentReadinessServerDataService {

	private static final Logger logger=Logger.getLogger(EnvironmentReadinessServerService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value="fetchServerData/{projectId}")
	public Response getEnvironmentReadinessServerService(@PathParam("projectId") int projectId){
		
	
		logger.info("checking for Server up ");
		
		logger.debug("retriving Servers for project "+projectId);
		
	
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<ServerDataTO>temServerToList=new ArrayList<ServerDataTO>();
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
			
			ServerDataTO temServerTo=new ServerDataTO();
			
			temServerTo.setServerName(temServerDetail.getServerName());
			temServerTo.setServerUrl(temServerDetail.getServerURL());
			
			temServerToList.add(temServerTo);
		
			}
		

		logger.info("checking Servers for up done successfully");
		
		}catch(Exception e){

			temServerToList=null;
			logger.error("error while fetching servers list "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(temServerToList).build();
		
		}
		
		return Response.status(Response.Status.OK).entity(temServerToList).build();
		
	}
	


}
