package com.mongo.entities;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Entity
@Table(name="module_job_jenkins_parameters",schema="plato_db")
public class ModuleJobsJenkinsParameters {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String parameter_key;
	private String value;
	
	@ManyToOne(cascade=CascadeType.ALL, targetEntity = ModuleJobsJenkins.class)
	@JoinColumn(name="module_id", nullable=false)
	private ModuleJobsJenkins moduleJobsJenkins;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParameter_key() {
		return parameter_key;
	}

	public void setParameter_key(String parameter_key) {
		this.parameter_key = parameter_key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ModuleJobsJenkins getModuleJobsJenkins() {
		return moduleJobsJenkins;
	}

	public void setModuleJobsJenkins(ModuleJobsJenkins moduleJobsJenkins) {
		this.moduleJobsJenkins = moduleJobsJenkins;
	}
	
	
}
