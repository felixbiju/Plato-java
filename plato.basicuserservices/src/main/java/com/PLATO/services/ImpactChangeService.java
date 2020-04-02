package com.PLATO.services;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.PLATO.userTO.ImpactChangeDiatTO;
import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.ProjectToolMapping;
import com.PLATO.entities.ToolMaster;
/*
 *@author 10643380 (Rahul Bhardwaj)
 * 
 * **/
@Path("impactChangeService")
public class ImpactChangeService {

	@GET
	@Path("fetchDiatSubJobDetails/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchDiatSubJobDetails(@PathParam("projectId") int projectId) throws Exception{
		ImpactChangeDiatTO impactChangeDiatTO=new ImpactChangeDiatTO();
		String toolName="DIAT";
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		keyvalueMap.put("project_id", projectId);
		keyvalueMap.put("tool_name", toolName);
		Integer projectToolMappingId=(Integer)GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PROJECT_TOOL_MAPPING, keyvalueMap);
		if(projectToolMappingId==null) {
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("toolName",toolName);
			ToolMaster toolMaster=new ToolMaster();
			String query="from ToolMaster where tool_name=:toolName";
			toolMaster=(ToolMaster)GenericDaoSingleton.getGenericDao().findUniqueByQuery(query,keyvalueMap);
			impactChangeDiatTO.setCommandToExecute(toolMaster.getCommand_to_execute());
			return Response.status(Response.Status.OK).entity(impactChangeDiatTO).build();
		}
		ProjectToolMapping projectToolMapping=(ProjectToolMapping)GenericDaoSingleton.getGenericDao().findByID(ProjectToolMapping.class,projectToolMappingId);
		String commandToExecute=projectToolMapping.getCommand_to_execute();
//		String[] s=commandToExecute.split("\"");
		//System.out.println("s.length "+s.length);
		/*for(int i=0;i<s.length;i++) {
			System.out.println(" i is "+s[i]);
		}*/
//		impactChangeDiatTO.setPreviousVersion(s[1].replace("\\", "/"));
//		impactChangeDiatTO.setCurrentVersion(s[3].replace("\\", "/"));
//		impactChangeDiatTO.setMappingFile(s[5].replace("\\", "/"));
		impactChangeDiatTO.setCommandToExecute(commandToExecute);
		return Response.status(Response.Status.OK).entity(impactChangeDiatTO).build();
		//System.out.println("projectToolMapping "+projectToolMappingId);
		
	}
}
