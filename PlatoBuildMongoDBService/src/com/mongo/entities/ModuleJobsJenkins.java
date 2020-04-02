package com.mongo.entities;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;




@Entity
@Table(name="module_job_jenkins_master",schema="plato_db")
public class ModuleJobsJenkins {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int jenkins_job_id;
	
	private String jenkins_job_name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date module_creation_date;
	
	private String module_subjobs_order;
	
	private String description;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="moduleJobsJenkins")
	private Set<ModuleBuildHistory> moduleBuildHistory;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="moduleJobsJenkins",fetch = FetchType.EAGER)
	private Set<ModuleSubJobsJenkins>moduleSubJobsJenkins;
	
	@ManyToOne
	@JoinColumn(name="project_id", nullable=false)
	private ProjectMaster projectMaster;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="moduleJobsJenkins",fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<ModuleJobsJenkinsParameters> moduleJobsJenkinsParametersList;


	public int getJenkins_job_id() {
		return jenkins_job_id;
	}

	public void setJenkins_job_id(int jenkins_job_id) {
		this.jenkins_job_id = jenkins_job_id;
	}

	public String getJenkins_job_name() {
		return jenkins_job_name;
	}

	public void setJenkins_job_name(String jenkins_job_name) {
		this.jenkins_job_name = jenkins_job_name;
	}

	public String getModule_subjobs_order() {
		return module_subjobs_order;
	}

	public void setModule_subjobs_order(String module_subjobs_order) {
		this.module_subjobs_order = module_subjobs_order;
	}

	public Set<ModuleBuildHistory> getModuleBuildHistory() {
		return moduleBuildHistory;
	}

	public void setModuleBuildHistory(Set<ModuleBuildHistory> moduleBuildHistory) {
		this.moduleBuildHistory = moduleBuildHistory;
	}

	public Set<ModuleSubJobsJenkins> getModuleSubJobsJenkins() {
		return moduleSubJobsJenkins;
	}

	public void setModuleSubJobsJenkins(
			Set<ModuleSubJobsJenkins> moduleSubJobsJenkins) {
		this.moduleSubJobsJenkins = moduleSubJobsJenkins;
	}

	public ProjectMaster getProjectMaster() {
		return projectMaster;
	}

	public void setProjectMaster(ProjectMaster projectMaster) {
		this.projectMaster = projectMaster;
	}

	public Date getModule_creation_date() {
		return module_creation_date;
	}

	public void setModule_creation_date(Date module_creation_date) {
		this.module_creation_date = module_creation_date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<ModuleJobsJenkinsParameters> getModuleJobsJenkinsParametersList() {
		return moduleJobsJenkinsParametersList;
	}

	public void setModuleJobsJenkinsParametersList(List<ModuleJobsJenkinsParameters> moduleJobsJenkinsParametersList) {
		this.moduleJobsJenkinsParametersList = moduleJobsJenkinsParametersList;
	}
}
