package com.mongo.entities;

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


@Entity
@Table(name="subjob_checkpoint",schema="plato_db")
public class SubjobCheckpoint {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int subjob_checkpoint_id;
	private int order_number;
	private String module_name;
	
	@ManyToOne
	@JoinColumn(name="subjob_id", nullable=false)
	private ModuleSubJobsJenkins moduleSubJob;
	
	@OneToMany(mappedBy="subjob_checkpoint", cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private Set<SubjobCheckpointDetails> checkpoint_criteria;
	
	public int getSubjob_checkpoint_id() {
		return subjob_checkpoint_id;
	}
	public void setSubjob_checkpoint_id(int subjob_checkpoint_id) {
		this.subjob_checkpoint_id = subjob_checkpoint_id;
	}
	public ModuleSubJobsJenkins getModuleSubJob() {
		return moduleSubJob;
	}
	public void setModuleSubJob(ModuleSubJobsJenkins moduleSubJob) {
		this.moduleSubJob = moduleSubJob;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	public String getModule_name() {
		return module_name;
	}
	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}
	public Set<SubjobCheckpointDetails> getCheckpoint_criteria() {
		return checkpoint_criteria;
	}
	public void setCheckpoint_criteria(Set<SubjobCheckpointDetails> checkpoint_criteria) {
		this.checkpoint_criteria = checkpoint_criteria;
	}
	
}
