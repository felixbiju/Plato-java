package com.PLATO.userTO;

import java.util.Set;

public class ModuleConfigTO
{	
	private int moduleId;     //jenkins_job_id
	private String moduleName; //jenkins_job_name
	private String moduleSubJobsOrder;
	private String description;
	private Set<SubModuleTO> subModules;

	public int getModuleId() {
		return moduleId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleSubJobsOrder() {
		return moduleSubJobsOrder;
	}
	public void setModuleSubJobsOrder(String moduleSubJobsOrder) {
		this.moduleSubJobsOrder = moduleSubJobsOrder;
	}
	public Set<SubModuleTO> getSubModules() {
		return subModules;
	}
	public void setSubModules(Set<SubModuleTO> subModules) {
		this.subModules = subModules;
	}



}
