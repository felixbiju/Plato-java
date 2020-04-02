package com.plato.tem.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tem_webservicedetails",schema="plato_db")
public class TEMWebserviceDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="webserviceID")
	int webserviceId;
	
	@Column(name="webserviceName")
	String webserviceName;
	
	@Column(name="webserviceURL")
	String webserviceURL;
	
	@Column(name="MonitoringStatus")
	String monitoringStatus;

	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ProjectId")
	private ProjectMaster projectMaster;

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

	public String getMonitoringStatus() {
		return monitoringStatus;
	}

	public void setMonitoringStatus(String monitoringStatus) {
		this.monitoringStatus = monitoringStatus;
	}

	public ProjectMaster getProjectMaster() {
		return projectMaster;
	}

	public void setProjectMaster(ProjectMaster projectMaster) {
		this.projectMaster = projectMaster;
	}
	
	
}
