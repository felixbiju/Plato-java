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
import com.PLATO.entities.TEMDatabaseDetail;
import com.PLATO.userTO.DatabaseDataTO;


@Path("EnvironmentReadinessDatabaseDataService")
public class EnvironmentReadinessDatabaseDataService {
private static final Logger logger=Logger.getLogger(EnvironmentReadinessDatabaseService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value="fetchatabaseData/{projectId}")
	public Response getEnvironmentReadinessDatabaseDataService(@PathParam("projectId") int projectId){
		
		
		logger.info("checking for database up ");
		
		logger.debug("retriving Databases for project "+projectId);
		
		
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<DatabaseDataTO>temDbToList=new ArrayList<DatabaseDataTO>();
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
			
			DatabaseDataTO temDbTO=new DatabaseDataTO();
			
			temDbTO.setDatabaseName(temDbDetail.getDatabaseName());
			temDbTO.setDatabaseUrl(temDbDetail.getDatabaseURL());
		
			temDbToList.add(temDbTO);
				
			}
		
		
		
			logger.info("checking databases for up done successfully");
		
		}catch(Exception e){
			temDbToList=null;
			logger.error("error while fetching database list "+e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(temDbToList).build();
		}
		
		return Response.status(Response.Status.OK).entity(temDbToList).build();
		
	}
	

}
