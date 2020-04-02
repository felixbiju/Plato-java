package com.PLATO.userTO;

public class TEMWebserviceDetailTO {

	private int webserviceId;
	private String webserviceName,webserviceURL,monitorStatus;
	public int getWebserviceId() {
		return webserviceId;
	}
	public void setWebserviceId(int webserviceId) {
		this.webserviceId = webserviceId;
	}
	public String getWebserviceName() {
		return webserviceName;
	}
	public void setWebserviceName(String webserviceName) {
		this.webserviceName = webserviceName;
	}
	public String getWebserviceURL() {
		return webserviceURL;
	}
	public void setWebserviceURL(String webserviceURL) {
		this.webserviceURL = webserviceURL;
	}
	public String getMonitorStatus() {
		return monitorStatus;
	}
	public void setMonitorStatus(String monitorStatus) {
		this.monitorStatus = monitorStatus;
	}
	
}
