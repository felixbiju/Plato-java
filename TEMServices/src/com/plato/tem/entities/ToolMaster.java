package com.plato.tem.entities;


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

@Entity
@Table(name="tool_master",schema="plato_db")
public class ToolMaster
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int tool_id;

	private String tool_name;
	private String command_to_execute;
	private String report_path;
	private String tool_logo;
	

	@ManyToOne
	@JoinColumn(name="node_name", nullable=false)
	private NodeMaster nodeMaster;

	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	private CategoryMaster categoryMaster;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="toolMaster")
	private Set<ProjectToolMapping> projectToolMapping;
	
	public int getTool_id() {
		return tool_id;
	}

	public void setTool_id(int tool_id) {
		this.tool_id = tool_id;
	}

	public String getTool_name() {
		return tool_name;
	}

	public void setTool_name(String tool_name) {
		this.tool_name = tool_name;
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

	public String getTool_logo() {
		return tool_logo;
	}

	public void setTool_logo(String tool_logo) {
		this.tool_logo = tool_logo;
	}

	public NodeMaster getNodeMaster() {
		return nodeMaster;
	}

	public void setNodeMaster(NodeMaster nodeMaster) {
		this.nodeMaster = nodeMaster;
	}

	public CategoryMaster getCategoryMaster() {
		return categoryMaster;
	}

	public void setCategoryMaster(CategoryMaster categoryMaster) {
		this.categoryMaster = categoryMaster;
	}

	public Set<ProjectToolMapping> getProjectToolMapping() {
		return projectToolMapping;
	}

	public void setProjectToolMapping(Set<ProjectToolMapping> projectToolMapping) {
		this.projectToolMapping = projectToolMapping;
	}

    

}
