package com.PLATO.userTO;

/*
 *@author 10643380 (Rahul Bhardwaj)
 * 
 * **/
public class ImpactChangeDiatTO {
	private String previousVersion;
	private String currentVersion;
	private String mappingFile;
	private String commandToExecute;
	
	public String getCommandToExecute() {
		return commandToExecute;
	}
	public void setCommandToExecute(String commandToExecute) {
		this.commandToExecute = commandToExecute;
	}
	public String getPreviousVersion() {
		return previousVersion;
	}
	public void setPreviousVersion(String previousVersion) {
		this.previousVersion = previousVersion;
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	public String getMappingFile() {
		return mappingFile;
	}
	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}
	

}
