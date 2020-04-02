package com.plato.tem.entities;

import java.util.List;

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
@Table(name="checkpoint_template",schema="plato_db")
public class CheckpointTemplate {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int checkpoint_template_id;
	private int order_number;
	private String module_name;

	@OneToMany(fetch = FetchType.EAGER,mappedBy="checkpoint_template", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<CheckpointDetailsTemplate> checkpoint_details_template;
	
	@ManyToOne
	@JoinColumn(name="project_tool_mapping_id", nullable=false)
	private ProjectToolMapping projectToolMapping;
	
	public int getCheckpoint_template_id() {
		return checkpoint_template_id;
	}
	public void setCheckpoint_template_id(int checkpoint_template_id) {
		this.checkpoint_template_id = checkpoint_template_id;
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
	public List<CheckpointDetailsTemplate> getCheckpoint_details_template() {
		return checkpoint_details_template;
	}
	public void setCheckpoint_details_template(
			List<CheckpointDetailsTemplate> checkpoint_details_template) {
		this.checkpoint_details_template = checkpoint_details_template;
	}
	public ProjectToolMapping getProjectToolMapping() {
		return projectToolMapping;
	}
	public void setProjectToolMapping(ProjectToolMapping projectToolMapping) {
		this.projectToolMapping = projectToolMapping;
	}
	
	
	
}
