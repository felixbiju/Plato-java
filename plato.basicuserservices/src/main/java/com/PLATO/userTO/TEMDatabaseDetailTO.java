package com.PLATO.userTO;
/**
 * @author 10643380(Rahul Bhardwaj)
 * this is a transfer object class of TEMDatabaseDetails
 * */
public class TEMDatabaseDetailTO {
	private int databaseId,pullingInterval;
	private String databaseName,databaseURL,databasePort,username,password,monitoringStatus,databaseDriver;
	public int getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
	public int getPullingInterval() {
		return pullingInterval;
	}
	public void setPullingInterval(int pullingInterval) {
		this.pullingInterval = pullingInterval;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDatabaseURL() {
		return databaseURL;
	}
	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}
	public String getDatabasePort() {
		return databasePort;
	}
	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMonitoringStatus() {
		return monitoringStatus;
	}
	public void setMonitoringStatus(String monitoringStatus) {
		this.monitoringStatus = monitoringStatus;
	}
	public String getDatabaseDriver() {
		return databaseDriver;
	}
	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}
	

}
