package com.PLATO.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.Singletons.MongoDBGenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.dao.GenericDao;
import com.PLATO.dao.MongoDbGenericDAO;
import com.PLATO.userTO.BuildHistoryTO;

@Path("DetailBuildHistory")
public class BuildHistoryFromMongoDBService
{
	
	/*
	 * Description : This class has functions to read/update build history in mongodb
	 * Author: Gaurav Kulkarni
	 */

	MongoDbGenericDAO mongoDbGenericDao=MongoDBGenericDaoSingleton.getGenericDao();
	GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	
	@GET
	@Path("getBuildHistory/{buildId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBuildHistory(@PathParam("buildId") int buildId)
	{		
		Document document=null;
		try
		{
			document=mongoDbGenericDao.getBySingleCondition("BuildHistory.build_history_id", String.valueOf(buildId));
			if(document==null||document.isEmpty()==true)
			{
				System.out.println("Build History Document not found");
				document=null;
				return Response.status(Response.Status.NOT_FOUND).entity(document).build();
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception in getting build history from mongodb");
			e.printStackTrace();
			document=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(document).build();
		}

		return Response.status(Response.Status.OK).entity(document).build();
	}
	
	@GET
	@Path("getBuildHistoryTest/{buildId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBuildHistoryTest(@PathParam("buildId") int buildId)
	{		
		ArrayList<Document> document=null;
		try
		{
			document=mongoDbGenericDao.getAllBySingleCondition("BuildHistory.build_history_id", String.valueOf(buildId));
			if(document==null||document.isEmpty()==true)
			{
				System.out.println("Build History Document not found");
				document=null;
				return Response.status(Response.Status.NOT_FOUND).entity(document).build();
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception in getting build history from mongodb");
			e.printStackTrace();
			document=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(document).build();
		}

		return Response.status(Response.Status.OK).entity(document).build();
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("getAllBuildHistory/{moduleName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllBuildHistory(@PathParam("moduleName") String moduleName)
	{
		JSONObject data = new JSONObject();
		JSONArray records = new JSONArray();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		try
		{
			keyvalueMap.put("moduleName", moduleName);
			List<Object> buildHistoryList= genericDao.findByQuery(ConstantQueries.GETBUILDHISTORYFORMODULE, keyvalueMap);
			if(buildHistoryList==null||buildHistoryList.size()==0||buildHistoryList.isEmpty()==true)
			{
				return Response.status(Response.Status.OK).entity("Build History not found").build();
			}
			Iterator<Object> it=buildHistoryList.iterator();
			int count = 0;
			int buildID = 0;
			while(it.hasNext() && (count++ < 10)){
				JSONObject eachRecord = new JSONObject();
				Object[] buildHistory=(Object[])it.next();
//				Document document=null;
				
				buildID = Integer.parseInt(buildHistory[0].toString());
				
//				try
//				{
//					document=mongoDbGenericDao.getBySingleCondition("BuildHistory.build_history_id", String.valueOf(buildID));
//					if(document==null||document.isEmpty()==true)
//					{
//						System.out.println("Build History Document not found");
//						document=null;
//					}
//				}
//				catch(Exception e)
//				{
//					System.out.println("Exception in getting build history from mongodb");
//					e.printStackTrace();
//					document=null;
//				}

//				JSONParser parser = new JSONParser(); 
//				JSONObject json = (JSONObject) parser.parse(document.toJson());
//				JSONObject BuildHistory = (JSONObject) json.get("BuildHistory");
				
				eachRecord.put("buildHistoryId", String.valueOf(buildID));
				eachRecord.put("buildNumber", buildHistory[1].toString());
//				if(BuildHistory.containsKey("AutomationTesting"))
//					eachRecord.put("AutomationTesting", BuildHistory.get("AutomationTesting"));
//				if(BuildHistory.containsKey("DataTesting"))
//					eachRecord.put("DataTesting", BuildHistory.get("DataTesting"));
//				if(BuildHistory.containsKey("PerformanceTesting"))
//					eachRecord.put("PerformanceTesting", BuildHistory.get("PerformanceTesting"));
//				if(BuildHistory.containsKey("SecurityTesting"))
//					eachRecord.put("SecurityTesting", BuildHistory.get("SecurityTesting"));
//				if(BuildHistory.containsKey("PreBuildCheck"))
//					eachRecord.put("PreBuildCheck", BuildHistory.get("PreBuildCheck"));
//				if(BuildHistory.containsKey("LiveBuildConsole")){
//					JSONObject LiveBuild = (JSONObject) BuildHistory.get("LiveBuildConsole");
//					JSONArray tools = (JSONArray) LiveBuild.get("tools");
//					eachRecord.put("LiveBuildConsole", tools);
//				}
					
				
				records.add(eachRecord);
			}
			
			data.put("buildRecords", records);
			
			return Response.status(Response.Status.OK).entity(data).build();
			
		}

		catch(Exception e)
		{
			System.out.println("Exception in getting build history from mongodb for module name: "+moduleName);
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error ").build();
		}	
	}
}
