package com.plato.tem.entities;

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

import org.hibernate.annotations.Type;

@Entity
@Table(name="module_subjob_jenkins_master",schema="plato_db")
public class ModuleSubJobsJenkins {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int subjob_id;
	
	private String subjob_name;
	private String command_to_execute;
	private String report_path;
	private String subjob_description;
	private String tool_name;
	private String postbuild_subjob;
	private int order_number;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean is_ladyBug_checked;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean is_alm_checked;
	
	

	public boolean isIs_alm_checked() {
		return is_alm_checked;
	}

	public void setIs_alm_checked(boolean is_alm_checked) {
		this.is_alm_checked = is_alm_checked;
	}

	public boolean isIs_ladyBug_checked() {
		return is_ladyBug_checked;
	}

	public void setIs_ladyBug_checked(boolean is_ladyBug_checked) {
		this.is_ladyBug_checked = is_ladyBug_checked;
	}

	@ManyToOne
	@JoinColumn(name="jenkins_job_id", nullable=false)
	private ModuleJobsJenkins moduleJobsJenkins;
	
	@ManyToOne
	@JoinColumn(name="node_name", nullable=false)
	private NodeMaster nodeMaster;
	
	@OneToMany(fetch = FetchType.EAGER,mappedBy="moduleSubJob", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<SubjobCheckpoint> moduleCheckpoint;

	public int getSubjob_id() {
		return subjob_id;
	}

	public void setSubjob_id(int subjob_id) {
		this.subjob_id = subjob_id;
	}

	public String getSubjob_name() {
		return subjob_name;
	}

	public void setSubjob_name(String subjob_name) {
		this.subjob_name = subjob_name;
	}

	public String getCommand_to_execute() {
		return command_to_execute;
	}

	public void setCommand_to_execute(String command_to_execute) {
		this.command_to_execute = command_to_execute;
	}

	public String getReport_path() {
		return report_path;
	}

	public void setReport_path(String report_path) {
		this.report_path = report_path;
	}

	public String getSubjob_description() {
		return subjob_description;
	}

	public void setSubjob_description(String subjob_description) {
		this.subjob_description = subjob_description;
	}

	public String getTool_name() {
		return tool_name;
	}

	public void setTool_name(String tool_name) {
		this.tool_name = tool_name;
	}

	public ModuleJobsJenkins getModuleJobsJenkins() {
		return moduleJobsJenkins;
	}

	public void setModuleJobsJenkins(ModuleJobsJenkins moduleJobsJenkins) {
		this.moduleJobsJenkins = moduleJobsJenkins;
	}

	public NodeMaster getNodeMaster() {
		return nodeMaster;
	}

	public void setNodeMaster(NodeMaster nodeMaster) {
		this.nodeMaster = nodeMaster;
	}

	public String getPostbuild_subjob() {
		return postbuild_subjob;
	}

	public void setPostbuild_subjob(String postbuild_subjob) {
		this.postbuild_subjob = postbuild_subjob;
	}

	public Set<SubjobCheckpoint> getModuleCheckpoint() {
		return moduleCheckpoint;
	}

	public void setModuleCheckpoint(Set<SubjobCheckpoint> moduleCheckpoint) {
		this.moduleCheckpoint = moduleCheckpoint;
	}

	public int getOrder_number() {
		return order_number;
	}

	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	
	
	
}
