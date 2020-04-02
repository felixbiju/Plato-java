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
@Table(name="project_tool_mapping",schema="plato_db")
public class ProjectToolMapping
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
   private int tool_project_mapping_id;
	
	@ManyToOne//(cascade = CascadeType.ALL)
	@JoinColumn(name = "tool_id")  
	private ToolMaster toolMaster;
	
	@ManyToOne//(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id")  
	private ProjectMaster projectMaster;
	
	@OneToMany(mappedBy="projectToolMapping", cascade=CascadeType.ALL)
	private Set<CheckpointTemplate> checkpointTemplate;
	

private String command_to_execute;
	private String report_path;
	
	@ManyToOne
	@JoinColumn(name="node_name", nullable=false)
	private NodeMaster nodeMaster;
	

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

	public NodeMaster getNodeMaster() {
		return nodeMaster;
	}

	public void setNodeMaster(NodeMaster nodeMaster) {
		this.nodeMaster = nodeMaster;
	}

	
	
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

	public Set<CheckpointTemplate> getCheckpointTemplate() {
		return checkpointTemplate;
	}

	public void setCheckpointTemplate(Set<CheckpointTemplate> checkpointTemplate) {
		this.checkpointTemplate = checkpointTemplate;
	}
	
}
