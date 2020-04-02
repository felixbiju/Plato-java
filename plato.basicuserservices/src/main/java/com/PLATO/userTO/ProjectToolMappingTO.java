package com.PLATO.userTO;

public class ProjectToolMappingTO {
	
	
    private int tool_project_mapping_id;	
	private int tool_id;
	private int project_id;
	private String command_to_execute;
	private String report_path;
	private String node_name;
	
	
	private String tool_name;
	private String tool_logo;
	private int category_id;
	private String category_name;
	
	
	
	
	
	public String getTool_name() {
		return tool_name;
	}
	public void setTool_name(String tool_name) {
		this.tool_name = tool_name;
	}
	public String getTool_logo() {
		return tool_logo;
	}
	public void setTool_logo(String tool_logo) {
		this.tool_logo = tool_logo;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public int getTool_project_mapping_id() {
		return tool_project_mapping_id;
	}
	public void setTool_project_mapping_id(int tool_project_mapping_id) {
		this.tool_project_mapping_id = tool_project_mapping_id;
	}
	public int getTool_id() {
		return tool_id;
	}
	public void setTool_id(int tool_id) {
		this.tool_id = tool_id;
	}
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int project_id) {
		this.project_id = project_id;
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
	public String getNode_name() {
		return node_name;
	}
	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}
	
	
	
	
	
	
	
	

}
