package com.PLATO.userTO;

/**
* FAST-Java-Common: QCDetails is the entity class that defines details for connecting to QC.
* @author Deepika
* @version 1.0
*/
public class QCDetails {

	private String qcURL;
	private String qcUsername;
	private String qcPassword;
	private String qcDomain;
	private String qcProject;
	
	public String getQcURL() {
		return qcURL;
	}
	
	public void setQcURL(String qcURL) {
		this.qcURL = qcURL;
	}
	
	public String getQcUsername() {
		return qcUsername;
	}
	
	public void setQcUsername(String qcUsername) {
		this.qcUsername = qcUsername;
	}
	
	public String getQcPassword() {
		return qcPassword;
	}
	
	public void setQcPassword(String qcPassword) {
		this.qcPassword = qcPassword;
	}
	
	public String getQcDomain() {
		return qcDomain;
	}
	
	public void setQcDomain(String qcDomain) {
		this.qcDomain = qcDomain;
	}
	
	public String getQcProject() {
		return qcProject;
	}
	
	public void setQcProject(String qcProject) {
		this.qcProject = qcProject;
	}	
	
}
