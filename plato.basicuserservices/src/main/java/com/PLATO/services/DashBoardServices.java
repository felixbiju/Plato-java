package com.PLATO.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.PLATO.constants.GlobalConstants;
import com.PLATO.dao.DashboardDao;
import com.PLATO.dao.MongoDbGenericDAO;
import com.PLATO.daoimpl.DashboardImpl;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.UserProjectMapping;
import com.PLATO.userTO.AllocatedProjects;
import com.PLATO.userTO.ModuleTO;
import com.PLATO.userTO.ModulesStatusCountTO;
import com.PLATO.userTO.RoleTO;


@Path("DashboardService")
public class DashBoardServices{
	private DashboardDao dashboardDao=new DashboardImpl();

	@GET
	@Path("getAllocatedProjectList/{username}")
	@Produces(MediaType.APPLICATION_JSON)	
	public Response  getAllocatedProjects(@PathParam("username") String user_id) throws IOException{

		List<AllocatedProjects>allocatedProjectNamesList=new ArrayList<AllocatedProjects>();

		AllocatedProjects  allocatedProjects;
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("user_id",user_id);
		List<ProjectMaster>allocatedProjectsList = new ArrayList<ProjectMaster>();
		try
		{
			List<Object>projectList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ALLOCATED_PROJECTS,queryMap);

			if(projectList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(projectList).build();
			}
			ProjectMaster prm;
			UserProjectMapping userProjectMapping;

			for(int i=0;i<projectList.size();i++){
				userProjectMapping=(UserProjectMapping) projectList.get(i);
				prm=userProjectMapping.getProjectMaster();
				allocatedProjectsList.add(prm);
			}

			for(int i=0;i<allocatedProjectsList.size();i++){
				allocatedProjects=new AllocatedProjects();
				String projectName=allocatedProjectsList.get(i).getProject_name();
				int projectId=allocatedProjectsList.get(i).getProject_id();
				allocatedProjects.setProjectName(projectName);
				allocatedProjects.setProject_id(projectId);
				allocatedProjectNamesList.add(allocatedProjects);
				projectName=null;
			}

		}catch(Exception e){
			allocatedProjectNamesList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(allocatedProjectNamesList).build();
		}

		return Response.status(Response.Status.OK).entity(allocatedProjectNamesList).build();

	}


	@GET
	@Path("getModuleList/{projectId}")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)
	public List<ModuleTO> getModuleList(@PathParam("projectId") int project_id) throws Exception{

		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("project_id",project_id);

		List<ModuleJobsJenkins> moduleJobJenkinsList;
		List<Object>moduleList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.MODULE_STATUS, queryMap);
		ModuleJobsJenkins moduleJobsJenkins;
		ModuleTO moduleTO;
		List<ModuleTO>moduleTOList=new ArrayList<ModuleTO>();
		for(int i=0;i<moduleList.size();i++){
			Object[] oarray=(Object[]) moduleList.get(i);

			String moduleName=(String) oarray[0];
			int moduleID=(Integer) oarray[1];
			String moduleStatus=(String) oarray[2];

			moduleTO=new ModuleTO();
			moduleTO.setModule_Id(moduleID);
			moduleTO.setModuleName(moduleName);
			moduleTO.setModuleStatus(moduleStatus);
			moduleTOList.add(moduleTO);

		}

		return moduleTOList;

	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("getModuleListOfProjects/{userId}/{accountName}/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModuleList(@PathParam("param") String param,@PathParam("accountName") String accountName,@PathParam("userId") String userId){

//		ArrayList<String>alltoolsList = new ArrayList<String>();

		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		List<ModuleJobsJenkins> moduleJobJenkinsList;
		List<Object>moduleList = null;
		List<ModuleTO>moduleTOList=new ArrayList<ModuleTO>();
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
			//for selected projects
			if(param.equalsIgnoreCase("all")){
				queryMap.put("user_id", userId);
				queryMap.put("account_name", accountName);
				moduleList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_MODULE_FOR_USER_ACCOUNT,queryMap);
			}else{
				queryMap.put("user_id", userId);
				queryMap.put("project_name", param);
				moduleList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_MODULE_FOR_USER_PROJECT,queryMap);
			}
			if(moduleList==null || moduleList.size()==0){
				return Response.status(Response.Status.NOT_FOUND).entity(moduleList).build();
			}
			if(moduleList!=null){
//				for(int i=0;i<moduleList.size();i++){
//					Object[] module=(Object[]) moduleList.get(i);
//					ModuleTO moduleTo=new ModuleTO();
//					moduleTo.setModule_Id(Integer.parseInt(module[0].toString()));
//					moduleTo.setModuleName(module[1].toString());
//					moduleTo.setModuleCreationDate(dateFormat.format(module[2]));
//					moduleTo.setModuleDescription(module[3].toString());
//					
//					queryMap=new HashMap<String,Object>();
//					queryMap.put("module_id", moduleTo.getModule_Id());
//					List<Object> statusList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_LAST_BUILD_DETAILS,queryMap);
//					if(statusList==null || statusList.size()==0)
//					{
//						moduleTo.setModuleLastBuildNumber(0);
//						moduleTo.setModuleLastExecution(null);
//						moduleTo.setModuleStatus(null);
//						moduleTo.setToolsStatus(null);
//					}
//					else
//					{
//
//						Object[] moduleLatestStatus=(Object[]) statusList.get(0);
//						moduleTo.setModuleLastBuildHistory(Integer.parseInt(moduleLatestStatus[0].toString()));
//						moduleTo.setModuleLastBuildNumber(Integer.parseInt(moduleLatestStatus[1].toString()));
//						
//						moduleTo.setModuleLastExecution(dateFormat.format(moduleLatestStatus[2]));
//						moduleTo.setModuleStatus(moduleLatestStatus[3].toString());
//						
//						MongoDbGenericDAO mongoDbGenericDao=MongoDBGenericDaoSingleton.getGenericDao();
//						Document document=null;
//						try
//						{
//							document=mongoDbGenericDao.getBySingleCondition("BuildHistory.build_history_id", String.valueOf(moduleTo.getModuleLastBuildHistory()));
//							if(document==null||document.isEmpty()==true)
//							{
//								System.out.println("Build History Document not found");
//								document=null;
//							}
//						}
//						catch(Exception e)
//						{
//							System.out.println("Exception in getting build history from mongodb");
//							e.printStackTrace();
//							document=null;
//						}
//						
//						if(document != null){
//							JSONParser parser = new JSONParser();
//							JSONObject json = (JSONObject) parser.parse(document.toJson());
//							
//							JSONObject jsonBuildHistory = (JSONObject) json.get("BuildHistory");
//							JSONObject jsonLiveBuildConsole = (JSONObject) jsonBuildHistory.get("LiveBuildConsole");
//							JSONArray jsonTools = (JSONArray) jsonLiveBuildConsole.get("tools");
//							
//							JSONObject tempData = new JSONObject();
//							
//							for (Object object : jsonTools) {
//								System.out.println(">>>>"+moduleTo.getModuleName());
//								tempData = new JSONObject();
//								JSONObject temp = (JSONObject) object;
//								tempData.put("toolName", temp.get("toolName"));
//								if(!alltoolsList.contains(temp.get("toolName"))){
//									try{
//									alltoolsList.add(temp.get("toolName").toString());
//									}catch(Exception e){
//										e.printStackTrace();
//									}
//								}
//							}
//						}
//					}
//				}
//				
//				System.out.println(">>> complete tool list "+alltoolsList);
//				
				
				for(int i=0;i<moduleList.size();i++){
					Object[] module=(Object[]) moduleList.get(i);
					ModuleTO moduleTo=new ModuleTO();
					moduleTo.setModule_Id(Integer.parseInt(module[0].toString()));
					moduleTo.setModuleName(module[1].toString());
					//moduleTo.setModuleCreationDate(dateFormat.parse(dateFormat.format(module[2])));
					//moduleTo.setModuleCreationDate(dateFormat.parse(dateFormat.format(module[2].toString())));
					moduleTo.setModuleCreationDate(dateFormat.format(module[2]));
					moduleTo.setModuleDescription(module[3].toString());
					
					queryMap=new HashMap<String,Object>();
					queryMap.put("module_id", moduleTo.getModule_Id());
					List<Object> statusList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_LAST_BUILD_DETAILS,queryMap);
					if(statusList==null || statusList.size()==0)
					{
						moduleTo.setModuleLastBuildNumber(0);
						moduleTo.setModuleLastExecution(null);
						moduleTo.setModuleStatus(null);
//						moduleTo.setToolsStatus(null);
					}
					else
					{

						Object[] moduleLatestStatus=(Object[]) statusList.get(0);
						moduleTo.setModuleLastBuildHistory(Integer.parseInt(moduleLatestStatus[0].toString()));
						moduleTo.setModuleLastBuildNumber(Integer.parseInt(moduleLatestStatus[1].toString()));
					
						moduleTo.setModuleLastExecution(dateFormat.format(moduleLatestStatus[2]));
						moduleTo.setModuleStatus(moduleLatestStatus[3].toString());
						
						MongoDbGenericDAO mongoDbGenericDao=MongoDBGenericDaoSingleton.getGenericDao();
						Document document=null;
						try
						{
							document=mongoDbGenericDao.getBySingleCondition("BuildHistory.build_history_id", String.valueOf(moduleTo.getModuleLastBuildHistory()));
							if(document==null||document.isEmpty()==true)
							{
								System.out.println("Build History Document not found");
								document=null;
							}
						}
						catch(Exception e)
						{
							System.out.println("Exception in getting build history from mongodb");
							e.printStackTrace();
							document=null;
						}
						
						if(document != null){
							
							JSONParser parser = new JSONParser();
							JSONObject json = (JSONObject) parser.parse(document.toJson());
							
							JSONObject jsonBuildHistory = (JSONObject) json.get("BuildHistory");
							JSONObject jsonLiveBuildConsole = (JSONObject) jsonBuildHistory.get("LiveBuildConsole");
							JSONArray jsonTools = (JSONArray) jsonLiveBuildConsole.get("tools");
							
							JSONArray tempArray = new JSONArray();
//							String [] toolStatusArray = new String[alltoolsList.size()];
							
//							for (int index=0; index<alltoolsList.size(); index++) {
//								toolStatusArray[index] = "not-present";
//							}
//							
//							for (Object object : jsonTools) {
//								JSONObject temp = (JSONObject) object;
//								for (String toolName : alltoolsList) {
//									System.out.println(toolName);
//									try{
//										if(toolName.equalsIgnoreCase(temp.get("toolName").toString())){
//											toolStatusArray[alltoolsList.indexOf(toolName)] = temp.get("tool_status").toString();
//										}
//									}catch (Exception e) {
//										e.printStackTrace();
//									}
//																	
//								}
//								
//							}
							
//							JSONObject toolsData = new JSONObject();
//							toolsData.put("toolList", alltoolsList);
//							toolsData.put("pertoolStatus", toolStatusArray);
							
//							moduleTo.setToolsStatus(toolsData);
							
						}
					}		
					
					moduleTOList.add(moduleTo);
				}
			}
		}catch(Exception e){
			moduleTOList=null;
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleTOList).build();
		}	
		
		return Response.status(Response.Status.OK).entity(moduleTOList).build();

	}

	@POST
	@Path("getBuildStatusWiseCount/{projectId}")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)
	public void getBuildStatusWiseCount(@PathParam("projectId") int project_id){

		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("project_id",project_id);
		//List<Object>buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(ConstantQueries.BUILD_WISE_STATUS_COUNT, queryMap);
		System.out.println("hello");

	}





	@GET
	@Path("getBuildStatusWiseCountOfModulesInProjects/{userId}/{accountName}/{param}")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBuildStatusWiseCount(@PathParam("param") String param,@PathParam("userId") String userId,@PathParam("accountName") String accountName){
		//ConstantQueries cq=new ConstantQueries();
		GlobalConstants.projectsIn="";
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		//queryMap.put("project_id",project_id);
		List<Object>buildStatusCount = null;

		queryMap.put("userId",userId);
		queryMap.put("accountName",accountName);

		List<String> projNamesLIST = Arrays.asList(param.split(","));
		queryMap.put("projNamesLIST", projNamesLIST);
		int failed=0;
		int completed=0;
		int inProgress=0;

		List<ModulesStatusCountTO>moduleBuildStatusCountList=new ArrayList<ModulesStatusCountTO>();

		ModulesStatusCountTO moduleBuildStatusCount=new ModulesStatusCountTO();
		//for selected projects
		try
		{
			 projNamesLIST = Arrays.asList(param.split(","));
			queryMap.put("projNamesLIST", projNamesLIST);
			if(param.contains(",")){

				String selectedProjects[]=param.split(",");
				//projNamesLIST = Arrays.asList(param.split(","));
				//queryMap.put("projNamesLIST", projNamesLIST);


				for(int i=0;i<selectedProjects.length;i++){
					if(i==selectedProjects.length-1){
						GlobalConstants.projectsIn=GlobalConstants.projectsIn+"'"+selectedProjects[i]+"'";
					}else{
						GlobalConstants.projectsIn=GlobalConstants.projectsIn+"'"+selectedProjects[i]+"',";
					}
				}
				ConstantQueries cq=new ConstantQueries(GlobalConstants.projectsIn);
				buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(cq.getBUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS(), queryMap);
			}else if(param.equalsIgnoreCase("all")){

				ConstantQueries cq=new ConstantQueries(GlobalConstants.projectsIn);
				buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(cq.BUILD_WISE_STATUS_COUNT_FOR_ALL_PROJECTS, queryMap);
				ModuleJobsJenkins moduleJobsJenkins;		

				//	int moduleID=moduleJobsJenkins.getJenkins_job_id();

			}else{
				GlobalConstants.projectsIn="'"+param+"'";
				ConstantQueries cq=new ConstantQueries(GlobalConstants.projectsIn);
				buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(cq.getBUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS(), queryMap);

			}

			if(buildStatusCount==null){
				ModulesStatusCountTO moduleBuildStatusCount1=new ModulesStatusCountTO();
				return Response.status(Response.Status.NOT_FOUND).entity(moduleBuildStatusCount1).build();
			}


			if(buildStatusCount!=null){

				int totalNoOfBuildModules=buildStatusCount.size();

				moduleBuildStatusCount.setTotal_modules(totalNoOfBuildModules);


				for(int i=0;i<buildStatusCount.size();i++){
					HashMap<String,Object> moduleResords=(HashMap<String,Object>) buildStatusCount.get(i);

					String statusName=(String) moduleResords.get("status_name");
					if(statusName.equalsIgnoreCase("failed")){
						failed++;
					}else if(statusName.equalsIgnoreCase("completed")){
						completed++;
					}else if(statusName.equalsIgnoreCase("In-Progress")){
						inProgress++;
					}

				}


				moduleBuildStatusCount.setCompleted(completed);
				moduleBuildStatusCount.setFailed(failed);
				moduleBuildStatusCount.setInProgress(inProgress);
				//moduleBuildStatusCount.setTotal_modules(completed+failed+inProgress);

			}

		}catch(Exception e){
			moduleBuildStatusCount=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleBuildStatusCount).build();
		}	

		return Response.status(Response.Status.OK).entity(moduleBuildStatusCount).build();



	}







	/*@POST
	@Path("getBuildStatusWiseCountOfModulesInProjectsPost")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBuildStatusWiseCountPost(String param, String userId,String accountName){
		//ConstantQueries cq=new ConstantQueries();
		GlobalConstants.projectsIn="";
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		//queryMap.put("project_id",project_id);
		List<Object>buildStatusCount = null;

		queryMap.put("userId",userId);
		queryMap.put("accountName",accountName);


		int failed=0;
		int completed=0;
		int inProgress=0;

		List<ModulesStatusCountTO>moduleBuildStatusCountList=new ArrayList<ModulesStatusCountTO>();

		ModulesStatusCountTO moduleBuildStatusCount=new ModulesStatusCountTO();
		//for selected projects
		try
		{
			if(param.contains(",")){

				String selectedProjects[]=param.split(",");
				List<String> projNamesLIST = Arrays.asList(param.split(","));
				queryMap.put("projNamesLIST", projNamesLIST);


				for(int i=0;i<selectedProjects.length;i++){
					if(i==selectedProjects.length-1){
						GlobalConstants.projectsIn=GlobalConstants.projectsIn+"'"+selectedProjects[i]+"'";
					}else{
						GlobalConstants.projectsIn=GlobalConstants.projectsIn+"'"+selectedProjects[i]+"',";
					}
				}
				ConstantQueries cq=new ConstantQueries(GlobalConstants.projectsIn);
				buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(cq.getBUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS(), queryMap);
			}else if(param.equalsIgnoreCase("all")){

				ConstantQueries cq=new ConstantQueries(GlobalConstants.projectsIn);
				buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(cq.BUILD_WISE_STATUS_COUNT_FOR_ALL_PROJECTS, queryMap);
				ModuleJobsJenkins moduleJobsJenkins;		

				//	int moduleID=moduleJobsJenkins.getJenkins_job_id();

			}else{
				GlobalConstants.projectsIn="'"+param+"'";
				ConstantQueries cq=new ConstantQueries(GlobalConstants.projectsIn);
				buildStatusCount=GenericDaoSingleton.getGenericDao().findBySQLQuery(cq.getBUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS(), queryMap);

			}

			if(buildStatusCount==null){
				ModulesStatusCountTO moduleBuildStatusCount1=new ModulesStatusCountTO();
				return Response.status(Response.Status.NOT_FOUND).entity(moduleBuildStatusCount1).build();
			}


			if(buildStatusCount!=null){

				int totalNoOfBuildModules=buildStatusCount.size();

				moduleBuildStatusCount.setTotal_modules(totalNoOfBuildModules);


				for(int i=0;i<buildStatusCount.size();i++){
					HashMap<String,Object> moduleResords=(HashMap<String,Object>) buildStatusCount.get(i);

					String statusName=(String) moduleResords.get("status_name");
					if(statusName.equalsIgnoreCase("failed")){
						failed++;
					}else if(statusName.equalsIgnoreCase("completed")){
						completed++;
					}else if(statusName.equalsIgnoreCase("In-Progress")){
						inProgress++;
					}

				}


				moduleBuildStatusCount.setCompleted(completed);
				moduleBuildStatusCount.setFailed(failed);
				moduleBuildStatusCount.setInProgress(inProgress);
				//moduleBuildStatusCount.setTotal_modules(completed+failed+inProgress);

			}

		}catch(Exception e){
			moduleBuildStatusCount=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleBuildStatusCount).build();
		}	

		return Response.status(Response.Status.OK).entity(moduleBuildStatusCount).build();



	}


*/



	/*@GET
	@Path("getRoleList/{userId}")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)
	public List<RoleTO> getROleList(@PathParam("userId") String user_id){

		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("user_id",user_id);
		List<Object>roleList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ROLE_LIST, queryMap);
		List<RoleTO>moduleTOList=new ArrayList<RoleTO>();
		RoleTO roleTO;
		for(int i=0;i<roleList.size();i++){
			Object[] oarray=(Object[]) roleList.get(i);

			String roleName=(String) oarray[0];
			int roleId=(Integer)oarray[1];

			roleTO=new RoleTO();
			roleTO.setRoleName(roleName);
			roleTO.setRoleId(roleId);
			moduleTOList.add(roleTO);
		}


		return moduleTOList;



	}*/


	@GET
	@Path("getRoleList/{userId}")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)
	public Response getROleList(@PathParam("userId") String user_id){

		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("user_id",user_id);
		List<RoleTO>moduleTOList=new ArrayList<RoleTO>();
		RoleTO roleTO;
		try
		{
			List<Object>roleList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ROLE_LIST, queryMap);

			if(roleList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(roleList).build();
			}	
			/*if(roleList.size()>0){

		}else{
			Response.
		}*/

			for(int i=0;i<roleList.size();i++){
				Object[] oarray=(Object[]) roleList.get(i);

				String roleName=(String) oarray[0];
				int roleId=(Integer)oarray[1];

				roleTO=new RoleTO();
				roleTO.setRoleName(roleName);
				roleTO.setRoleId(roleId);
				moduleTOList.add(roleTO);
			}

		}catch(Exception e){
			moduleTOList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(moduleTOList).build();
		}	

		return Response.status(Response.Status.OK).entity(moduleTOList).build();


	}


	@GET
	@Path("getAllocatedProjectList/{username}/{portfolioName}/{accountName}")
	//@Consumes("text/plain")   
	@Produces(MediaType.APPLICATION_JSON)	
	public Response  getAllocatedProjectsForAccount(@PathParam("username") String user_id,@PathParam("accountName") String accountName,@PathParam("portfolioName") String portfolioName) throws IOException{

		/*List<ProjectMaster>allocatedProjectsList=dashboardDao.getAllocatedProjectsForUser(user_id);

		List<AllocatedProjects>allocatedProjectNamesList=new ArrayList<AllocatedProjects>();

		AllocatedProjects  allocatedProjects;

		for(int i=0;i<allocatedProjectsList.size();i++){
			allocatedProjects=new AllocatedProjects();
			String projectName=allocatedProjectsList.get(i).getProject_name();
			allocatedProjects.setProjectName(projectName);
			allocatedProjectNamesList.add(allocatedProjects);
			projectName=null;
		}

		return  allocatedProjectNamesList;*/
		List<AllocatedProjects>allocatedProjectNamesList=new ArrayList<AllocatedProjects>();

		AllocatedProjects  allocatedProjects;
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		queryMap.put("user_id",user_id);
		queryMap.put("accountName",accountName);
		queryMap.put("portfolioName", portfolioName);
		List<ProjectMaster>allocatedProjectsList = new ArrayList<ProjectMaster>();
		try
		{
			List<Object>projectList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ALLOCATED_PROJECTS_FOR_ACCOUNT,queryMap);

			if(projectList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(projectList).build();
			}

			for(int i=0;i<projectList.size();i++){
				allocatedProjects=new AllocatedProjects();
				Object[] obj=(Object[]) projectList.get(i);
				allocatedProjects.setProject_id((Integer)obj[0]);
				//allocatedProjects.setProjectName((String)projectList.get(i));
				allocatedProjects.setProjectName((String)obj[1]);


				allocatedProjectNamesList.add(allocatedProjects);
			}

		}catch(Exception e){
			allocatedProjectNamesList=null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(allocatedProjectNamesList).build();
		}

		return Response.status(Response.Status.OK).entity(allocatedProjectNamesList).build();

	}


}
