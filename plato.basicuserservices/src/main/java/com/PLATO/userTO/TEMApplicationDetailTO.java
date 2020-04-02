package com.PLATO.userTO;
/**
 * @author 10643380(Rahul Bhardwaj)
 * this is a transfer object class of TEMApplicaionDetails
 * */
public class TEMApplicationDetailTO {
	private int applicationId,pullingInterval;
	private String applicationName,applicationURL,monitorStatus;
	
	//getter and setters
	public int getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}
	public int getPullingInterval() {
		return pullingInterval;
	}
	public void setPullingInterval(int pullingInterval) {
		this.pullingInterval = pullingInterval;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getApplicationURL() {
		return applicationURL;
	}
	public void setApplicationURL(String applicationURL) {
		this.applicationURL = applicationURL;
	}
	public String getMonitorStatus() {
		return monitorStatus;
	}
	public void setMonitorStatus(String monitorStatus) {
		this.monitorStatus = monitorStatus;
	}
	

}
