package com.PLATO.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="node_master",schema="plato_db")
public class NodeMaster {


	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int node_id;

	@Id
	private String node_name;

	@OneToMany(cascade=CascadeType.ALL,mappedBy="nodeMaster")
	private Set<ModuleSubJobsJenkins> moduleSubJobsJenkins;

	public int getNode_id() {
		return node_id;
	}

	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}

	public String getNode_name() {
		return node_name;
	}

	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}

	public Set<ModuleSubJobsJenkins> getModuleSubJobsJenkins() {
		return moduleSubJobsJenkins;
	}

	public void setModuleSubJobsJenkins(
			Set<ModuleSubJobsJenkins> moduleSubJobsJenkins) {
		this.moduleSubJobsJenkins = moduleSubJobsJenkins;
	}

	/*@OneToMany(cascade=CascadeType.ALL,mappedBy="nodeMaster")
	private Set<ToolMaster> toolMaster;*/





}
