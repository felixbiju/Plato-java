package com.mongo.constants;

public class ConstantQueries
{

	String param="";

	public ConstantQueries(String param){
		this.param=param;
	}

	/****************Queries by Gaurav*************/

	//query for login service to validate user  
	//	public static final String VALIDATEUSERQUERY="FROM UserMaster where user_id=:user_id AND password=:password";

	public static final String GET_NODE_BY_NAME="FROM NodeMaster where node_name=:node_name";
	public static final String VALIDATEUSERQUERY="SELECT usrmst.name, upm.projectMaster.project_id, upm.projectMaster.project_name,upm.accountMaster.account_id, upm.accountMaster.account_name, upm.portfolioMaster.portfolio_id, upm.portfolioMaster.portfolio_name  FROM UserMaster usrmst, UserProjectMapping upm where usrmst.user_id=:user_id AND usrmst.password=:password and usrmst.user_id=upm.userMaster.user_id and upm.default_project=:default_project";

	//query to get list of module names for project   
	public static final String GETMODULENAMELIST="SELECT mj.jenkins_job_id, mj.jenkins_job_name FROM ModuleJobsJenkins mj where mj.projectMaster.project_name=:project_name";

	//query to get list of all tools
	public static final String GETTOOLLIST="SELECT tl.tool_id, tl.tool_name,tl.command_to_execute,tl.report_path,tl.tool_logo,tl.nodeMaster.node_id,tl.nodeMaster.node_name FROM ToolMaster tl";
	//get list of tools with category details
	public static final String GETCATEGORYWISETOOLS="SELECT cm.category_id, cm.category_name, tm.tool_id, tm.tool_name,tm.command_to_execute,tm.report_path,tm.tool_logo,tm.nodeMaster.node_id,tm.nodeMaster.node_name FROM ToolMaster tm, ProjectToolMapping ptm, CategoryMaster cm WHERE ptm.projectMaster.project_id=:projectId AND tm.categoryMaster.category_id=cm.category_id AND tm.tool_id=ptm.toolMaster.tool_id";

	//get some data of module by giving its id
	public static final String GETMODULECONFIGBYID="SELECT mj.jenkins_job_id, mj.jenkins_job_name, mj.module_subjobs_order FROM ModuleJobsJenkins mj WHERE mj.jenkins_job_id=:moduleId";
	//query to get details of a module
	public static final String GETMODULECONFIG="SELECT mj.jenkins_job_id, mj.jenkins_job_name, mj.module_subjobs_order FROM ModuleJobsJenkins mj WHERE mj.jenkins_job_name=:moduleName";
	//query to get details of a sub module
	public static final String GETSUBMODULEDATA="SELECT msj.subjob_id, msj.subjob_name, msj.nodeMaster.node_id, msj.nodeMaster.node_name, msj.command_to_execute, msj.report_path, msj.subjob_description, msj.tool_name, msj.postbuild_subjob FROM  ModuleSubJobsJenkins msj WHERE msj.moduleJobsJenkins.jenkins_job_name=:moduleName";
	//query to get the build history for a module
	public static final String GETBUILDHISTORYFORMODULE="SELECT mbh.module_build_history_id, mbh.build_number, mbh.timestamp, mbh.statusMaster.status_name FROM ModuleBuildHistory mbh WHERE mbh.moduleJobsJenkins.jenkins_job_name=:moduleName";

	//get project by name
	public static final String GETPROJECTBYNAME="FROM ProjectMaster where project_name=:projectName";

	//query to get module config
	// public static final String GETMODULECONFIG="SELECT mj.jenkins_job_id, mj.jenkins_job_name, mj.module_subjobs_order, msj.subjob_id, msj.subjob_name, msj.nodeMaster.node_id, msj.nodeMaster.node_name, msj.command_to_execute, msj.report_path, msj.subjob_description, msj.tool_name FROM ModuleJobsJenkins mj, ModuleSubJobsJenkins msj where mj.jenkins_job_name=:moduleName AND mj.jenkins_job_name=msj.moduleJobsJenkins.jenkins_job_name";

	//update the order of subjobs in module table
	public static String UPDATEMODULESUBJOBORDER="UPDATE ModuleJobsJenkins mj SET mj.module_subjobs_order=:moduleSubJobsOrder WHERE mj.jenkins_job_id=:moduleId";
	//get order of submodule for given module
	public static String GETMODULESUBJOBORDER="SELECT mj.module_subjobs_order FROM ModuleJobsJenkins mj WHERE mj.jenkins_job_id=:moduleId";

	//get id of module for given name
	public static String GETMAINMODULEID="SELECT msj.moduleJobsJenkins.jenkins_job_id FROM ModuleSubJobsJenkins msj WHERE msj.subjob_name=:subJobName";

	//delete module for given name
	public static final String DELETESUBMODULEBYNAME="DELETE ModuleSubJobsJenkins msj WHERE msj.subjob_name=:subJobName";
	//get submodule object by name
	public static final String GETSUBMODULEBYNAME="FROM ModuleSubJobsJenkins msj WHERE msj.subjob_name=:subJobName";
	
	/**********************************************************************************/

	//nilesh
	public static String GET_PORTFOLIO_LIST="from PortfolioMaster";
	public static String GET_ALLOCATED_PROJECTS="FROM UserProjectMapping where user_id=:user_id";

	//public static final String FETCH_MODULES="FROM ModuleJobsJenkins where project_id=:project_id";

	public static String MODULE_STATUS="select a.jenkins_job_name,a.jenkins_job_id,c.status_name from ModuleJobsJenkins a,ModuleBuildHistory b,StatusMaster c where a.projectMaster.project_id=:project_id and b.build_number=(select max(build_number)from ModuleBuildHistory where a.jenkins_job_id=b.moduleJobsJenkins.jenkins_job_id) and b.statusMaster.status_id=c.status_id";

	// public static final String BUILD_WISE_STATUS_COUNT="SELECT tt.moduleJobsJenkins.jenkins_job_id,tt.build_number,st.status_name,mjm.projectMaster.project_id FROM ModuleBuildHistory tt inner join ModuleJobsJenkins mjm on tt.moduleJobsJenkins.jenkins_job_id=mjm.jenkins_job_id INNER JOIN (SELECT mj.moduleJobsJenkins.jenkins_job_id as jenkins_job_id, MAX(mj.build_number) AS build_number FROM ModuleBuildHistory mj GROUP BY mj.moduleJobsJenkins.jenkins_job_id) groupedtt inner join StatusMaster st ON tt.moduleJobsJenkins.jenkins_job_id = groupedtt.jenkins_job_id  and tt.build_number = groupedtt.build_number and tt.statusMaster.status_id=st.status_id and mjm.projectMaster.project_id=:project_id";


	public String BUILD_WISE_STATUS_COUNT="SELECT tt.jenkins_job_id,tt.build_number,st.status_name "+
			"FROM module_build_history tt inner join Module_job_jenkins_master mjm on tt.jenkins_job_id=mjm.jenkins_job_id "+
			"INNER JOIN "+
			" (SELECT jenkins_job_id, MAX(build_number) AS build_number"+
			" FROM module_build_history "+
			" GROUP BY jenkins_job_id) as groupedtt inner join status_master st"+
			" ON tt.jenkins_job_id = groupedtt.jenkins_job_id"+ 

 			" and tt.build_number = groupedtt.build_number"+
 			" and tt.status_id=st.status_id"+
 			" and mjm.project_id=1";


	public static String BUILD_WISE_STATUS_COUNT_FOR_ALL_PROJECTS="SELECT tt.jenkins_job_id,tt.build_number,st.status_name "+
			"FROM module_build_history tt inner join Module_job_jenkins_master mjm on tt.jenkins_job_id=mjm.jenkins_job_id "+
			"INNER JOIN "+
			" (SELECT jenkins_job_id, MAX(build_number) AS build_number"+
			" FROM module_build_history "+
			" GROUP BY jenkins_job_id) as groupedtt inner join status_master st"+
			" ON tt.jenkins_job_id = groupedtt.jenkins_job_id"+ 

    	 	" and tt.build_number = groupedtt.build_number"+
    	 	" and tt.status_id=st.status_id"+
    	 	" and mjm.project_id in (select upm.project_id from user_project_mapping upm inner join project_master pm on pm.project_id=upm.project_id"+ 
    	 	" where upm.user_id=:userId"+
    	 	" and pm.account_id in (select acc.account_id from account_master acc where acc.account_name=:accountName))";
	/*" and mjm.project_id in(select upm.project_id from user_project_mapping upm"+ 
    	     " where upm.user_id='mohammad.sohail@lntinfotech.com')";*/





	//commented
	/*public String BUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS="SELECT tt.jenkins_job_id,tt.build_number,st.status_name "+
 			"FROM module_build_history tt inner join Module_job_jenkins_master mjm on tt.jenkins_job_id=mjm.jenkins_job_id "+
 			"INNER JOIN "+
 			    " (SELECT jenkins_job_id, MAX(build_number) AS build_number"+
 			    " FROM module_build_history "+
 			    " GROUP BY jenkins_job_id) as groupedtt inner join status_master st"+
 			" ON tt.jenkins_job_id = groupedtt.jenkins_job_id"+ 

 			" and tt.build_number = groupedtt.build_number"+
 			" and tt.status_id=st.status_id"+
 			" and mjm.project_id in (select pm.project_id from project_master pm inner join user_project_mapping upm"+ 
 " on pm.project_id=upm.project_id and"+
 " upm.user_id='mohammad.sohail@lntinfotech.com'"+
 " and pm.project_name in("+param+"))";*/

	public String BUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS;





	public String getBUILD_WISE_STATUS_COUNT_FOR_SELECTED_PROJECTS() {
		return "SELECT tt.jenkins_job_id,tt.build_number,st.status_name "+
				"FROM module_build_history tt inner join Module_job_jenkins_master mjm on tt.jenkins_job_id=mjm.jenkins_job_id "+
				"INNER JOIN "+
				" (SELECT jenkins_job_id, MAX(build_number) AS build_number"+
				" FROM module_build_history "+
				" GROUP BY jenkins_job_id) as groupedtt inner join status_master st"+
				" ON tt.jenkins_job_id = groupedtt.jenkins_job_id"+ 

     			" and tt.build_number = groupedtt.build_number"+
     			" and tt.status_id=st.status_id"+
     			" and mjm.project_id in (select pm.project_id from project_master pm inner join user_project_mapping upm"+ 
     			" on pm.project_id=upm.project_id and"+
     			" upm.user_id=:userId"+
     			" and pm.project_name in("+param+")"+
     			" and pm.account_id in (select acc.account_id from account_master acc where acc.account_name=:accountName))";
	}





	public static String GET_ROLE_LIST="select rm.role_name,upm.roleMaster.role_id from RoleMaster rm , UserProjectMapping upm where upm.roleMaster.role_id=rm.role_id and upm.userMaster.user_id=:user_id";


	/*public static final String GET_ALL_PROJECTS_MODULES="select a.jenkins_job_name,a.jenkins_job_id,c.status_name,b.timestamp as LastExecution,max(b.build_number) as build_number,a.module_creation_date"+ 
 " from module_job_jenkins_master a inner join module_build_history b on"+ 
 " a.jenkins_job_id=b.jenkins_job_id"+
 " inner join status_master c on c.status_id=b.status_id"+ 
 " where a.project_id in(select upm.project_id from user_project_mapping upm"+ 
 " where upm.user_id='mohammad.sohail@lntinfotech.com') "+ 
 " group by b.jenkins_job_id";*/

	public static String GET_ALL_PROJECTS_MODULES="SELECT"+ 
			" tt.jenkins_job_id,tt.build_number,st.status_name,mjm.jenkins_job_name,tt.timestamp as LastExecution,mjm.module_creation_date"+
			" FROM module_build_history tt inner join module_job_jenkins_master mjm on"+ 
			" tt.jenkins_job_id=mjm.jenkins_job_id"+
			" INNER JOIN"+
			" (SELECT jenkins_job_id, MAX(build_number) AS build_number"+
			" FROM module_build_history"+
			" GROUP BY jenkins_job_id) groupedtt"+ 
			" ON tt.jenkins_job_id = groupedtt.jenkins_job_id"+ 
			" inner join status_master st on  tt.status_id=st.status_id"+
			" and tt.build_number = groupedtt.build_number"+
			" and mjm.project_id in (select upm.project_id from user_project_mapping upm inner join project_master pm on pm.project_id=upm.project_id"+ 
			" where upm.user_id=:userId"+
			" and pm.account_id in (select acc.account_id from account_master acc where acc.account_name=:accountName))";

	/*public static final String GET_SELECTED_PROJECTS_MODULES="select a.jenkins_job_name,a.jenkins_job_id,c.status_name,b.timestamp as LastExecution,max(b.build_number) as build_number,a.module_creation_date"+ 
 		   " from module_job_jenkins_master a inner join module_build_history b on"+ 
 		   " a.jenkins_job_id=b.jenkins_job_id"+
 		   " inner join status_master c on c.status_id=b.status_id"+ 
 		   " where a.project_id in(select pm.project_id from project_master pm inner join user_project_mapping upm"+ 
 " on pm.project_id=upm.project_id and"+
 " upm.user_id='mohammad.sohail@lntinfotech.com'"+
 " and pm.project_name in("+GlobalConstants.projectsIn+"))"+  
 " group by b.jenkins_job_id";*/




	//commented
	/*public String GET_SELECTED_PROJECTS_MODULES="SELECT"+ 

 " tt.jenkins_job_id,tt.build_number,st.status_name,mjm.jenkins_job_name,tt.timestamp as LastExecution,mjm.module_creation_date"+ 
 " FROM module_build_history tt inner join module_job_jenkins_master mjm on "+ 

 " tt.jenkins_job_id=mjm.jenkins_job_id"+ 
 " INNER JOIN"+ 
     " (SELECT jenkins_job_id, MAX(build_number) AS build_number"+ 
     " FROM module_build_history"+ 
     " GROUP BY jenkins_job_id) groupedtt "+ 
 " ON tt.jenkins_job_id = groupedtt.jenkins_job_id "+ 
 " inner join status_master st on  tt.status_id=st.status_id"+ 
 " and tt.build_number = groupedtt.build_number"+ 
 " and mjm.project_id in (select pm.project_id from project_master pm "+ 
  " inner join user_project_mapping upm on pm.project_id=upm.project_id "+ 
  " and upm.user_id='mohammad.sohail@lntinfotech.com' and pm.project_name "+ 
  " in("+param+"))";*/


	public String GET_SELECTED_PROJECTS_MODULES;

	public String getGET_SELECTED_PROJECTS_MODULES() {
		return "SELECT"+ 

 " tt.jenkins_job_id,tt.build_number,st.status_name,mjm.jenkins_job_name,tt.timestamp as LastExecution,mjm.module_creation_date"+ 
 " FROM module_build_history tt inner join module_job_jenkins_master mjm on "+ 

 " tt.jenkins_job_id=mjm.jenkins_job_id"+ 
 " INNER JOIN"+ 
 " (SELECT jenkins_job_id, MAX(build_number) AS build_number"+ 
 " FROM module_build_history"+ 
 " GROUP BY jenkins_job_id) groupedtt "+ 
 " ON tt.jenkins_job_id = groupedtt.jenkins_job_id "+ 
 " inner join status_master st on  tt.status_id=st.status_id"+ 
 " and tt.build_number = groupedtt.build_number"+ 
 " and mjm.project_id in (select pm.project_id from project_master pm "+ 
 " inner join user_project_mapping upm on pm.project_id=upm.project_id "+ 
 // " and upm.user_id='mohammad.sohail@lntinfotech.com' and pm.project_name "+ 
 " and upm.user_id=:userId and pm.project_name "+ 
 " in("+param+")" +
 " and pm.account_id in (select acc.account_id from account_master acc where acc.account_name=:accountName)" +
 ")";
	}

	public static String GET_ACTIVE_ACCOUNTS_FOR_PORTFOLIO="select am.account_id,am.account_name,am.account_head, am.account_logo FROM AccountMaster am , PortfolioMaster pm where "+ 
			" am.portfolioMaster.portfolio_id=pm.portfolio_id"+
			" and am.portfolioMaster.portfolio_id = :portfolioId)"
			/*+ "and am.account_status='Active'"*/;




	//Account Queries

	public static String GET_ALL_ACCOUNTS="from AccountMaster";   
	public static String GET_PARTICULAR_ACCOUNT="from AccountMaster where account_id=:accountId";  

	public static String DELETE_ACCOUNT="update AccountMaster set account_status='InActive' where account_name=:accountName";

	public static String GET_PARTICULAR_PORFOLIO="from PortfolioMaster where portfolio_id=:portfolioId";   


	//nilesh
	public static String GET_ALLOCATED_PROJECTS_FOR_ACCOUNT="select pm2.project_id,pm2.project_name FROM AccountMaster am , PortfolioMaster pm ,ProjectMaster pm2 where"+ 
			" am.portfolioMaster.portfolio_id=pm.portfolio_id"+" and am.account_id=pm2.accountMaster.account_id"+
			" and pm2.accountMaster.account_id=(select aim.account_id from AccountMaster aim where aim.account_name=:accountName)"+
			" and am.portfolioMaster.portfolio_id = (select mn.portfolio_id from PortfolioMaster mn where mn.portfolio_name=:portfolioName)"+
			" and pm2.project_id in(select upm.projectMaster.project_id from UserProjectMapping upm where upm.userMaster.user_id=:user_id)";
	//"group by pm2.project_id";

	public static String GET_ACCOUNTS_FOR_PORTFOLIO="select am.account_name FROM AccountMaster am , PortfolioMaster pm ,ProjectMaster pm2 where "+ 
			" am.portfolioMaster.portfolio_id=pm.portfolio_id"+" and am.account_id=pm2.accountMaster.account_id"+
			" and am.portfolioMaster.portfolio_id = (select mn.portfolio_id from PortfolioMaster mn where mn.portfolio_name=:portfolioName)"+
			" and pm2.accountMaster.account_id=am.account_id"+
			" and am.account_id in"+
			" (select pim.accountMaster.account_id from ProjectMaster pim , UserProjectMapping uipm"+ 
			" where pim.project_id=uipm.projectMaster.project_id and uipm.userMaster.user_id=:user_id)"+
			" group by am.account_id";


	//query to get project list for particular user
	//rupali
	public static final String GETPROJECTLIST = "SELECT upm.projectMaster.project_id FROM UserProjectMapping upm where upm.userMaster.user_id=:userid";

	//query to get account list for particular project
	//rupali
	public static final String GETACCOUNTLIST = "SELECT pm.accountMaster.account_name FROM ProjectMaster pm where pm.project_id=:projectid";

	//query to get portfolio list
	//rupali
	public static final String GETPORTFOLIOLIST = "SELECT am.portfolioMaster.portfolio_id,am.portfolioMaster.portfolio_name FROM AccountMaster am where am.account_name=:accountid"; 



	//sueanne
	//Project Queries

	public static String GET_ALL_PROJECTS="from ProjectMaster"; 

	public static String GET_PARTICULAR_PROJECT="from ProjectMaster where project_id=:projectId";

	public static String GET_PARTICULAR_ACCOUNT_BY_ID="from AccountMaster where account_id=:accountId";

	public static String GET_PARTICULAR_PROJECT_BY_ID="from ProjectMaster where project_id=:projectId";

	public static String GET_PARTICULAR_PROJECT_BY_NAME="from ProjectMaster where project_name=:projectName";

	public static String GET_ALL_PROJECT_NAMES="SELECT pm.project_id, pm.project_name from ProjectMaster pm";

	public static String GET_ALLOCATED_PROJECTS_FOR_PORTFOLIO_AND_ACCOUNT="select pm2.project_id, pm2.project_name, pm2.project_status, pm2.project_creation_date,pm2.accountMaster.account_id FROM AccountMaster am , PortfolioMaster pm ,ProjectMaster pm2 where"+ 
			" am.portfolioMaster.portfolio_id=pm.portfolio_id"+" and am.account_id=pm2.accountMaster.account_id"+
			" and pm2.accountMaster.account_id=:accountId"+
			" and am.portfolioMaster.portfolio_id =:portfolioId";









	//Nodes Queries

	public static String GET_ALL_NODES="from NodeMaster"; 

	//Users Queries

	//	 
	//	 public static String GET_ALL_USERS="SELECT distinct um.user_id, um.name, upm.projectMaster.project_name, upm.roleMaster.role_name from UserMaster um, UserProjectMapping upm where um.user_id in (SELECT distinct upm2.userMaster.user_id from UserProjectMapping upm2)"; 
	//	 
	//	 public static String GET_PARTICULAR_USER="from UserMaster where user_id=:userId";
	//	 

	public static String GET_ALL_USERS="SELECT distinct um.user_id, um.name, upm.projectMaster.project_name, upm.roleMaster.role_name " +
			"from UserMaster um, UserProjectMapping upm " +
			"where um.user_id = upm.userMaster.user_id AND um.user_id in" +
			" (SELECT distinct upm2.userMaster.user_id from UserProjectMapping upm2)"; 





	public static String GET_ALL_USERS_in_PROJECT="SELECT um.user_id,um.name,um.password " +
			"FROM UserMaster um, UserProjectMapping upm " +
			"where um.user_id=upm.userMaster.user_id and upm.projectMaster.project_id=" +
			"(Select pm.project_id from ProjectMaster pm where pm.project_id=:projectId)";

	public static String GET_ALL_USERS_in_ACCOUNT="SELECT um.user_id,um.name,um.password " +
			"FROM UserMaster um, UserProjectMapping upm " +
			"where um.user_id=upm.userMaster.user_id " +
			"and upm.projectMaster.project_id in " +
			"(Select pm.project_id from ProjectMaster pm where pm.accountMaster.account_id=" +
			"(Select am.account_id from AccountMaster am where am.account_id=:accountId))";



	public static String GET_ALL_USERS_in_PORTFOLIO="SELECT um.user_id,um.name,um.password " +
			"FROM UserMaster um, UserProjectMapping upm " +
			"where um.user_id=upm.userMaster.user_id and " +
			"upm.projectMaster.project_id in " +
			"(Select pm.project_id from ProjectMaster pm where pm.accountMaster.account_id in " +
			"(Select am.account_id from AccountMaster am where am.portfolioMaster.portfolio_id in " +
			"(Select pom.portfolio_id from PortfolioMaster pom where pom.portfolio_id=:portfolioId)))";







	public static String GET_COMPLETELY_ALL_USERS="SELECT distinct um.user_id, upm.projectMaster.project_name, upm.roleMaster.role_name " +
			"from UserProjectMapping upm "; 

	public static String ALL_USERS="select user_id, name from UserMaster"; 

	public static String ALL_USERS_FROM_UPM="select upm.userMaster.user_id from UserProjectMapping upm"; 

	public static String GET_PARTICULAR_USER="from UserMaster where user_id=:userId";

	public static String GET_PARTICULAR_USER_FROM_UPM="from UserProjectMapping upm where upm.userMaster.user_id=:userId";


	public static String ALL_IN_UPM=" SELECT distinct upm.userMaster.user_id, upm.projectMaster.project_name, upm.roleMaster.role_name from UserProjectMapping upm"; 






	public static String GET_ALL_USERS_in_selected_PROJECT="SELECT um.user_id, um.name, upm.projectMaster.project_name, upm.roleMaster.role_name " +
			"FROM UserMaster um, UserProjectMapping upm " +
			"where um.user_id=upm.userMaster.user_id and upm.projectMaster.project_id=:projectId " +
			"and upm.accountMaster.account_id=:accountId and upm.portfolioMaster.portfolio_id=:portfolioId";



	public static String GET_ALL_USERS_in_selected_ACCOUNT="SELECT um.user_id,um.name,upm.projectMaster.project_name, upm.roleMaster.role_name " +
			"FROM UserMaster um, UserProjectMapping upm " +
			"where um.user_id=upm.userMaster.user_id " +
			"and upm.accountMaster.account_id=:accountId "+
			"and upm.portfolioMaster.portfolio_id=:portfolioId";



	public static String GET_ALL_USERS_in_Selected_PORTFOLIO="SELECT um.user_id, um.name, upm.projectMaster.project_name, upm.roleMaster.role_name  " +
			"FROM UserMaster um, UserProjectMapping upm " +
			"where um.user_id=upm.userMaster.user_id and " +
			"upm.portfolioMaster.portfolio_id=:portfolioId ";
	
	public static String GET_ALL_MAPPINGS="select upm.id from UserProjectMapping upm where upm.userMaster.user_id=:userId";


















	//Account Queries
	public static String GET_PARTICULAR_ACCOUNT_FROM_PROJECT="from AccountMaster am " +
			"where am.account_id=" +
			"(SELECT pm.accountMaster.account_id from ProjectMaster pm where project_id=:projectId)";

	//Portfolio Queries
	public static String GET_PARTICULAR_PORTFOLIO_FROM_PROJECT="from PortfolioMaster pom " +
			"where pom.portfolio_id =" +
			"(SELECT am.portfolioMaster.portfolio_id from AccountMaster am where am.account_id=" +
			"(SELECT pm.accountMaster.account_id from ProjectMaster pm where pm.project_id=:projectId))";




	//Tools Queries

public static String GET_ALL_TOOLS="from ToolMaster"; 
	
	public static String GET_DEFAULT_FROM_TOOL_MASTER="from ToolMaster where tool_id=:toolId";
	
	public static String GET_ALL_CATEGORIES="from CategoryMaster"; 
	
	public static String GET_ALL_TOOLS_IN_CATEGORY="select tm.tool_id, tm.tool_name  from ToolMaster tm where tm.categoryMaster.category_id=:categoryId";
	
	public static String GET_PARTICULAR_TOOL_BY_ID="from ToolMaster where tool_id=:toolId";




	//Role Queries

	public static String GET_ALL_ROLES="from RoleMaster"; 

	public static String GET_ALL_PERMISSIONS="from RoleMaster"; 

	public static String GET_PARTICULAR_ROLE="from RoleMaster where role_id=:roleId";

	public static String GET_PARTICULAR_ROLE_BY_ID="from RoleMaster where role_id=:roleId";

	public static String GET_ROLES_FOR_PROJECT="SELECT rm.role_id, rm.role_name from RoleMaster rm " +
			"where rm.role_id in" +
			"(Select upm.roleMaster.role_id from UserProjectMapping upm " +
			"where upm.projectMaster.project_id=:projectId " +
			"AND upm.userMaster.user_id=:userId)";


	/* public static String GET_ROLES_FOR_PROJECT="SELECT rm.role_id, rm.role_name from RoleMaster rm " +
			 		"where rm.role_id IN" +
			 		"(Select upm.role_id from UserProjectMapping upm " +
			 		"where upm.project id = " +
			 		"(select p.project_id from ProjectMaster p where p.project_name=:projectName) " +
			 		"AND user_id= (Select u.user_id from UserMaster u where u.name=:userName))";*/



	public static String GET_PORTFOLIO_DATA = "SELECT pm.portfolio_id,pm.portfolio_name From PortfolioMaster pm where pm.portfolio_id in "
			+ "(select am.portfolioMaster.portfolio_id FROM AccountMaster am where am.account_id in "
			+ "(SELECT pm.accountMaster.account_id FROM ProjectMaster pm where pm.project_id in "
			+ "(select upm.projectMaster.project_id from UserProjectMapping upm where upm.userMaster.user_id=:userid))) group by pm.portfolio_id";


	//TEM QUERIES
	 
	 public static String FETCH_APPLICATIONS_TO_MONITOR="from TEMApplicationDetail tm where tm.projectMaster.project_id=:projectId";
	 
	 public static String FETCH_DATABASES_TO_MONITOR="from TEMDatabaseDetail db where db.projectMaster.project_id=:projectId";
	 
	 public static String FETCH_SERVERS_TO_MONITOR="from TEMServerDetail ts where ts.projectMaster.project_id=:projectId";
	 
	 public static String FETCH_WEBSERVICES_TO_MONITOR="from TEMWebserviceDetail tm where tm.projectMaster.project_id=:projectId";
	 
	 
	 public static String UPDATE_APPLICATIONS_STATUS="update TEMApplicationDetail tm set tm.monitoringStatus=:appStatus where tm.applicationId=:applicationId";
	 public static String UPDATE_DATABASESS_STATUS="update TEMDatabaseDetail tmdb set tmdb.monitoringStatus=:dbStatus where tmdb.databaseId=:databaseId";
	 public static String UPDATE_SERVERS_STATUS="update TEMServerDetail tms set tms.monitoringStatus=:serverStatus where tms.serverId=:serverId";
	 
	 
	 public static String GET_PARTICULAR_PROJECT_FOR_APPLICATION_SERVER_DB="from ProjectMaster where project_id=:projectId";
	 
	 
	 public static String GET_APPLICATION_TO_UPDATE="from TEMApplicationDetail tm where tm.applicationId=:applicationId";
	 public static String GET_SERVER_TO_UPDATE="from TEMServerDetail ts where ts.serverId=:serverId";
	 public static String GET_DATABASE_TO_UPDATE="from TEMDatabaseDetail tb where tb.databaseId=:databaseId";
	

	 public static final String FETCH_APPLICATION_BY_ID = "from TEMApplicationDetail tm where tm.applicationId=:appId"; 
	 public static final String FETCH_DATABASE_BY_ID = "from TEMDatabaseDetail tb where tb.databaseId=:dbId"; 
	 public static final String FETCH_SERVER_BY_ID = "from TEMServerDetail ts where ts.serverId=:serverId";
	 


	//Rupali for Tool
	 
		 public static String GET_TOOLS_FOR_PORTFOLIO = "select tm.tool_id,tm.tool_name,tm.command_to_execute,tm.report_path,tm.tool_logo,tm.categoryMaster.category_id,tm.categoryMaster.category_name,tm.nodeMaster.node_id,tm.nodeMaster.node_name from ToolMaster tm, ProjectToolMapping ptm where "
		 		+ "tm.tool_id=ptm.toolMaster.tool_id "
		 		+ "and ptm.projectMaster.project_id=:projectId ";
		 
		 
		 
		 
		 public static final String GET_MODULE_HISTORY_DATA = "from ModuleBuildHistory where module_build_history_id=:buildHistoryId";
		 public static final String FETCH_DATABASES_BY_PROJECTID = "from TEMDatabaseDetail tb where tb.projectMaster.project_id=:projectId";
		 public static final String FETCH_SERVERS_BY_PROJECTID = "from TEMServerDetail ts where ts.projectMaster.project_id=:projectId";
		 public static final String FETCH_APPLICATIONS_BY_PROJECTID = "from TEMApplicationDetail tapp where tapp.projectMaster.project_id=:projectId";
		 
		/* public static final String FETCH_SUBJOB_CHECKPOINTS = "select sc.* from SubjobCheckpoint sc ,ModuleBuildHistory mbh,ModuleSubjobJenkinsMaster msjm"+
				 					" where sc.moduleSubJob.subjob_id= msjm.subjob_id"+
				 					" and mbh.jenkins_job_id=msjm.jenkins_job_id"+
				 					" and mbh.module_build_history_id=:buildHistoryId order by sc.order_number,sc.subjob_id";*/

		 public static final String GET_SUBJOB_NAME ="select mj.subjob_name from ModuleSubJobsJenkins mj where mj.subjob_id=:subJobId";
		 
		 public static final String GET_JENKINS_JOB_ID =" select mbh.moduleJobsJenkins.jenkins_job_id from ModuleBuildHistory mbh where mbh.module_build_history_id=:buildHistoryId";
		 
		// public static final String GET_JENKINS_SUB_JOB_ID =" select msbj.subjob_id from ModuleSubJobsJenkins msbj where msbj.moduleJobsJenkins.jenkins_job_id=:jenkinsJobId order by msbj.subjob_id";
		 public static final String GET_JENKINS_SUB_JOB_ID =" select distinct msbj.subjob_id from ModuleSubJobsJenkins msbj,SubjobCheckpoint sc where msbj.subjob_id=sc.moduleSubJob.subjob_id and msbj.moduleJobsJenkins.jenkins_job_id=:jenkinsJobId order by sc.order_number,msbj.subjob_id";
 
		 public static final String FETCH_SUBJOB_CHECKPOINTS ="from SubjobCheckpoint sc where sc.moduleSubJob.subjob_id=:subJobId";

		 public static final String GET_SUBMODULE_BY_NAME="from  ModuleSubJobsJenkins as mjb where mjb.subjob_name=:subModuleName";
		 
		 
		

}
