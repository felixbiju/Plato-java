package com.mongo.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="tem_serverdetails",schema="plato_db")
public class TEMServerDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ServerID")
	private int serverId;
	
	@Column(name="ServerName")
	private String serverName;
	
	@Column(name="ServerURL")
	private String serverURL;
	
	@Column(name="PullingInterval")
	private int pullingInterval;
	
	@Column(name="MonitoringStatus")
	private String monitoringStatus;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ProjectId", nullable=false)
	private ProjectMaster projectMaster;

	
	@OneToMany(mappedBy = "temServerDetail",cascade=CascadeType.ALL)
	private Set<TemServerResponse>temServerResponse;
	
	
	public Set<TemServerResponse> getTemServerResponse() {
		return temServerResponse;
	}

	public void setTemServerResponse(Set<TemServerResponse> temServerResponse) {
		this.temServerResponse = temServerResponse;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
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
