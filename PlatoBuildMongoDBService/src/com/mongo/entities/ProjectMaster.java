package com.mongo.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="project_master",schema="plato_db")
public class ProjectMaster
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int project_id;
	
	private String project_name;
	private String project_status;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date project_creation_date;
	
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	private AccountMaster accountMaster;
	
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy = "projectMaster")
	private Set<UserProjectMapping> userProjectMapping;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy = "projectMaster")
	private Set<ModuleJobsJenkins> moduleJobsJenkins;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy = "projectMaster")
	private Set<ProjectToolMapping> projectToolMapping;


	@OneToMany(mappedBy = "projectMaster")
	private Set<TEMApplicationDetail>temApplicationDetail;
	
	@OneToMany(mappedBy = "projectMaster")
	private Set<TEMServerDetail>temServerDetail;
	
	@OneToMany(mappedBy = "projectMaster")
	private Set<TEMDatabaseDetail>temDatabaseDetail;
	
	
	
	public Set<TEMApplicationDetail> getTemApplicationDetail() {
		return temApplicationDetail;
	}


	public void setTemApplicationDetail(
			Set<TEMApplicationDetail> temApplicationDetail) {
		this.temApplicationDetail = temApplicationDetail;
	}


	public Set<TEMServerDetail> getTemServerDetail() {
		return temServerDetail;
	}


	public void setTemServerDetail(Set<TEMServerDetail> temServerDetail) {
		this.temServerDetail = temServerDetail;
	}


	public Set<TEMDatabaseDetail> getTemDatabaseDetail() {
		return temDatabaseDetail;
	}


	public void setTemDatabaseDetail(Set<TEMDatabaseDetail> temDatabaseDetail) {
		this.temDatabaseDetail = temDatabaseDetail;
	}


	public int getProject_id() {
		return project_id;
	}


	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}


	public String getProject_name() {
		return project_name;
	}


	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}


	public String getProject_status() {
		return project_status;
	}


	public void setProject_status(String project_status) {
		this.project_status = project_status;
	}


	public Date getProject_creation_date() {
		return project_creation_date;
	}


	public void setProject_creation_date(Date project_creation_date) {
		this.project_creation_date = project_creation_date;
	}


	public AccountMaster getAccountMaster() {
		return accountMaster;
	}


	public void setAccountMaster(AccountMaster accountMaster) {
		this.accountMaster = accountMaster;
	}


	public Set<UserProjectMapping> getUserProjectMapping() {
		return userProjectMapping;
	}


	public void setUserProjectMapping(Set<UserProjectMapping> userProjectMapping) {
		this.userProjectMapping = userProjectMapping;
	}


	public Set<ModuleJobsJenkins> getModuleJobsJenkins() {
		return moduleJobsJenkins;
	}


	public void setModuleJobsJenkins(Set<ModuleJobsJenkins> moduleJobsJenkins) {
		this.moduleJobsJenkins = moduleJobsJenkins;
	}


	public Set<ProjectToolMapping> getProjectToolMapping() {
		return projectToolMapping;
	}


	public void setProjectToolMapping(Set<ProjectToolMapping> projectToolMapping) {
		this.projectToolMapping = projectToolMapping;
	}
	
	
	
	
}
