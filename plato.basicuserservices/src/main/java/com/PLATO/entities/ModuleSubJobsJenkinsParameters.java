package com.PLATO.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Entity
@Table(name="module_subjob_jenkins_parameters",schema="plato_db")
public class ModuleSubJobsJenkinsParameters {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String parameter_key;
	private String value;
	@OneToOne
	@JoinColumn(name="subjob_id", nullable=false)
	private ModuleSubJobsJenkins moduleSubJobsJenkins;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ModuleSubJobsJenkins getModuleSubJobsJenkins() {
		return moduleSubJobsJenkins;
	}
	public void setModuleSubJobsJenkins(ModuleSubJobsJenkins moduleSubJobsJenkins) {
		this.moduleSubJobsJenkins = moduleSubJobsJenkins;
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
	
}
