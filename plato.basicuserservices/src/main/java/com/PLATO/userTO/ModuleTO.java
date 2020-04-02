package com.PLATO.userTO;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ModuleTO {

	private String moduleName;
	private String moduleDescription;
	
	public String getModuleDescription() {
		return moduleDescription;
	}

	public void setModuleDescription(String moduleDescription) {
		this.moduleDescription = moduleDescription;
	}

	private String moduleStatus;
	
	private int module_Id;
	
	private int moduleLastBuildNumber;

	private int moduleLastBuildHistory;
	
//	private JSONObject toolsStatus;
	
	/*@JsonbDateFormat("dd-MMM-yyyy hh:mm:ss a")
	private Date moduleCreationDate;
	
	@JsonbDateFormat("dd-MMM-yyyy hh:mm:ss a")
	private Date moduleLastExecution;*/
	
	public int getModuleLastBuildHistory() {
		return moduleLastBuildHistory;
	}

	public void setModuleLastBuildHistory(int moduleLastBuildHistory) {
		this.moduleLastBuildHistory = moduleLastBuildHistory;
	}

	private String moduleCreationDate;
	
	private String moduleLastExecution;
	
	public String getModuleCreationDate() {
		return moduleCreationDate;
	}

	public void setModuleCreationDate(String moduleCreationDate) {
		this.moduleCreationDate = moduleCreationDate;
	}

	public String getModuleLastExecution() {
		return moduleLastExecution;
	}

	public void setModuleLastExecution(String moduleLastExecution) {
		this.moduleLastExecution = moduleLastExecution;
	}

	public int getModuleLastBuildNumber() {
		return moduleLastBuildNumber;
	}

	public void setModuleLastBuildNumber(int moduleLastBuildNumber) {
		this.moduleLastBuildNumber = moduleLastBuildNumber;
	}

	
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleStatus() {
		return moduleStatus;
	}

	public void setModuleStatus(String moduleStatus) {
		this.moduleStatus = moduleStatus;
	}

	public int getModule_Id() {
		return module_Id;
	}

	public void setModule_Id(int module_Id) {
		this.module_Id = module_Id;
	}

//	public JSONObject getToolsStatus() {
//		return toolsStatus;
//	}
//
//	public void setToolsStatus(JSONObject toolsStatus) {
//		this.toolsStatus = toolsStatus;
//	}

}
