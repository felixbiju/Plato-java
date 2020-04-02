package com.PLATO.userTO;
/**
 * @author 10643380(Rahul Bhardwaj)
 * this is a transfer object class of TEMServerDetails
 * */
public class TEMServerDetailTO {
	private int serverId,pullingInterval;
	private String serverName,serverURL,monitoringStatus;
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public int getPullingInterval() {
		return pullingInterval;
	}
	public void setPullingInterval(int pullingInterval) {
		this.pullingInterval = pullingInterval;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerURL() {
		return serverURL;
	}
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	public String getMonitoringStatus() {
		return monitoringStatus;
	}
	public void setMonitoringStatus(String monitoringStatus) {
		this.monitoringStatus = monitoringStatus;
	}
	

}
