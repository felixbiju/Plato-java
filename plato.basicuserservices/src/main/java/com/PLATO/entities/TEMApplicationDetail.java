package com.PLATO.entities;

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
@Table(name="tem_applicationdetails",schema="plato_db")
public class TEMApplicationDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ApplicationID")
	int applicationId;
	
	@Column(name="ApplicationName")
	String applicationName;
	
	@Column(name="ApplicationURL")
	String applicationURL;
	
	@Column(name="PullingInterval")
	int pullingInterval;
	
	@Column(name="MonitoringStatus")
	String monitoringStatus;

	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ProjectId")
	private ProjectMaster projectMaster;

	
	/*@OneToMany(mappedBy = "temApplicationDetail",cascade=CascadeType.ALL)
	private Set<TemApplicationResponse>temApplicationResponse;*/
	
	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
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

	public int getPullingInterval() {
		return pullingInterval;
	}

	public void setPullingInterval(int pullingInterval) {
		this.pullingInterval = pullingInterval;
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
