package com.mongo.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="project_tool_mapping",schema="plato_db")
public class ProjectToolMapping
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int tool_project_mapping_id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "tool_id")  
	private ToolMaster toolMaster;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id")  
	private ProjectMaster projectMaster;

	public int getTool_project_mapping_id() {
		return tool_project_mapping_id;
	}

	public void setTool_project_mapping_id(int tool_project_mapping_id) {
		this.tool_project_mapping_id = tool_project_mapping_id;
	}

	public ToolMaster getToolMaster() {
		return toolMaster;
	}

	public void setToolMaster(ToolMaster toolMaster) {
		this.toolMaster = toolMaster;
	}

	public ProjectMaster getProjectMaster() {
		return projectMaster;
	}

	public void setProjectMaster(ProjectMaster projectMaster) {
		this.projectMaster = projectMaster;
	}
	
	
   
}
