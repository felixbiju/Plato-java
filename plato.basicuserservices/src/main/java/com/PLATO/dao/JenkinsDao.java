package com.PLATO.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkinsParameters;

public interface JenkinsDao
{
	public int updateQuery(String queryString, HashMap<String, Object> keyvalueMap) throws Exception;
	public ModuleJobsJenkins createModule(ModuleJobsJenkins module) throws Exception;
	public void createSubModule(ModuleJobsJenkins module) throws Exception;
	//adding new createSubModule for postBuild
	public void createSubModule(ModuleJobsJenkins module,Set<ModuleSubJobsJenkins> moduleSetForPostBuild)throws Exception;
	//public void createSubModule(ModuleSubJobsJenkins subModule, String moduleSubJobOrder, int moduleId) throws Exception;
	public void delete(Class<?> type, Object id, String jobName) throws Exception;
	public void deleteSubModuleByQuery(String subModuleName) throws Exception;
	public void updateSubModule(ModuleSubJobsJenkins subModule) throws Exception;
	public void deleteModule(ModuleJobsJenkins module) throws Exception;
	ModuleJobsJenkins updateModule(ModuleJobsJenkins module) throws Exception;
}
