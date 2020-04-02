package com.PLATO.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.userTO.ModuleTO;
import com.PLATO.userTO.SubModuleTO;

@Path("search")
public class SearchService {
	@GET
	@Path("subJobs/{param}")
	public Response searchSubJob(@PathParam("param") String searchQuery ){
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<SubModuleTO> subModuleToList=new ArrayList<SubModuleTO>();
		String query="from ModuleSubJobsJenkins where subjob_name like :name";
		queryMap.put("name", "%"+searchQuery+"%");
		List<Object> searchResult;
		try {
			searchResult=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
			System.out.println("value returned");
			if(searchResult==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).entity("not found").build();
			}
			if(searchResult.isEmpty()) {
				System.out.println("no entry found");
				return  Response.status(Response.Status.NOT_FOUND).entity("no entry found").build();
			}
			Iterator<Object> it=searchResult.iterator();
			while(it.hasNext()) {
				ModuleSubJobsJenkins subModule=(ModuleSubJobsJenkins)it.next();
				SubModuleTO subModuleTo=new SubModuleTO();
				subModuleTo.setModuleId(subModule.getSubjob_id());
				subModuleTo.setSubjob_name(subModule.getSubjob_name());
				subModuleTo.setCommand_to_execute(subModule.getCommand_to_execute());
				subModuleTo.setNode_name(subModule.getNodeMaster().getNode_name());
				subModuleTo.setNodeId(subModule.getNodeMaster().getNode_id());
				subModuleTo.setPostbuild_subjob(subModule.getPostbuild_subjob());
				subModuleTo.setReport_path(subModule.getReport_path());
				subModuleTo.setSubjob_description(subModule.getSubjob_description());
				subModuleTo.setTool_name(subModule.getTool_name());
				subModuleToList.add(subModuleTo);
				System.out.print("id "+subModule.getSubjob_id()+" ");
				System.out.print("name "+subModule.getSubjob_name()+" ");
				System.out.println();
			}
			return Response.status(Response.Status.OK).entity(subModuleToList).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN).entity("aw shnaps :(").build();
		}
	}
	
	@GET
	@Path("jobs/{param}")
	public Response searchJob(@PathParam("param") String searchQuery) {
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<ModuleTO> moduleToList=new ArrayList<ModuleTO>();
		String query="from ModuleJobsJenkins where jenkins_job_name like :name";
		queryMap.put("name", "%"+searchQuery+"%");
		List<Object> searchResult;
		try {
			searchResult=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
			System.out.println("value returned");
			if(searchResult==null) {
				System.out.println("not found");
				return  Response.status(Response.Status.NOT_FOUND).entity("not found").build();
			}
			if(searchResult.isEmpty()) {
				System.out.println("no entry found");
				return  Response.status(Response.Status.NOT_FOUND).entity("no entry found").build();
			}
			Iterator<Object> it=searchResult.iterator();
			while(it.hasNext()) {
				ModuleJobsJenkins module=(ModuleJobsJenkins)it.next();
				ModuleTO moduleTo=new ModuleTO();
				moduleTo.setModule_Id(module.getJenkins_job_id());
				moduleTo.setModuleCreationDate(module.getModule_creation_date().toString());
				//moduleTo.setModuleLastBuildNumber();
				//moduleTo.setModuleLastExecution();
				moduleTo.setModuleName(module.getJenkins_job_name());
				//moduleTo.setModuleStatus();
				moduleToList.add(moduleTo);
			}
			return Response.status(Response.Status.OK).entity(moduleToList).build();
			
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("aw shnaps :(").build();
		}
		
	}

}
