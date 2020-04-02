package com.PLATO.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="module_build_history",schema="plato_db")
public class ModuleBuildHistory {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int module_build_history_id;
		
	@ManyToOne
	@JoinColumn(name="jenkins_job_id", nullable=false)
	private ModuleJobsJenkins moduleJobsJenkins;
	
	private int build_number;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	@ManyToOne
	@JoinColumn(name="status_id", nullable=false)
	private StatusMaster statusMaster;

	

	public int getModule_build_history_id() {
		return module_build_history_id;
	}

	public void setModule_build_history_id(int module_build_history_id) {
		this.module_build_history_id = module_build_history_id;
	}

	public ModuleJobsJenkins getModuleJobsJenkins() {
		return moduleJobsJenkins;
	}

	public void setModuleJobsJenkins(ModuleJobsJenkins moduleJobsJenkins) {
		this.moduleJobsJenkins = moduleJobsJenkins;
	}

	public int getBuild_number() {
		return build_number;
	}

	public void setBuild_number(int build_number) {
		this.build_number = build_number;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public StatusMaster getStatusMaster() {
		return statusMaster;
	}

	public void setStatusMaster(StatusMaster statusMaster) {
		this.statusMaster = statusMaster;
	}
	
	
}
