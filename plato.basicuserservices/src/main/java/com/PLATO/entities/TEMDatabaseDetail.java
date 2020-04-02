package com.PLATO.entities;

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
@Table(name="tem_database_details",schema="plato_db")
public class TEMDatabaseDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="DatabaseId")
	private int databaseId;
	
	@Column(name="DatabaseName")
	private String databaseName;
	
	@Column(name="DatabaseUrl")
	private String databaseURL;
	
	@Column(name="DatabasePort")
	private String databasePort;
	
	@Column(name="Username")
	private String username;
	
	@Column(name="Password")
	private String password;
	
	@Column(name="PullingInterval")
	private int pullingInterval;
	
	@Column(name="MonitoringStatus")
	private String monitoringStatus;
	
	@Column(name="DatabaseDriver")
	private String databaseDriver;
	
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ProjectId", nullable=false)
	private ProjectMaster projectMaster;
	
	@OneToMany(mappedBy = "temDatabaseDetail",cascade=CascadeType.ALL)
	private Set<TemDatabaseResponse>temDatabaseResponse;
	
	public Set<TemDatabaseResponse> getTemDatabaseResponse() {
		return temDatabaseResponse;
	}
	public void setTemDatabaseResponse(Set<TemDatabaseResponse> temDatabaseResponse) {
		this.temDatabaseResponse = temDatabaseResponse;
	}
	public ProjectMaster getProjectMaster() {
		return projectMaster;
	}
	public void setProjectMaster(ProjectMaster projectMaster) {
		this.projectMaster = projectMaster;
	}
	public int getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
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
	public String getDatabaseDriver() {
		return databaseDriver;
	}
	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}
	
}
