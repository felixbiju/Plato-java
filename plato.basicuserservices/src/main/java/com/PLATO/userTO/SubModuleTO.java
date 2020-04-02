package com.PLATO.userTO;

import java.util.ArrayList;

public class SubModuleTO
{
	private int subModuleId,nodeId,moduleId;  //subjobid
	private String subjob_name,node_name,command_to_execute,report_path,subjob_description,tool_name,postbuild_subjob,postBuild_trigger_option;
	private boolean isladyBugChecked,isAlmChecked;
	private int order_number;
	private ArrayList<SubModuleParametersTO> subModuleParametersList;
	
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	public boolean getIsAlmChecked() {
		return isAlmChecked;
	}
	public void setIsAlmChecked(boolean isAlmChecked) {
		this.isAlmChecked = isAlmChecked;
	}
	public boolean getIsladyBugChecked() {
		return isladyBugChecked;
	}
	public void setIsladyBugChecked(boolean isladyBugChecked) {
		this.isladyBugChecked = isladyBugChecked;
	}
	public int getSubModuleId() {
		return subModuleId;
	}
	public void setSubModuleId(int subModuleId) {
		this.subModuleId = subModuleId;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	public String getSubjob_name() {
		return subjob_name;
	}
	public void setSubjob_name(String subjob_name) {
		this.subjob_name = subjob_name;
	}
	public String getNode_name() {
		return node_name;
	}
	public void setNode_name(String node_name) {
		this.node_name = node_name;
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
	public String getPostbuild_subjob() {
		return postbuild_subjob;
	}
	public void setPostbuild_subjob(String postbuild_subjob) {
		this.postbuild_subjob = postbuild_subjob;
	}
	public String getPostBuild_trigger_option() {
		return postBuild_trigger_option;
	}
	public void setPostBuild_trigger_option(String postBuild_trigger_option) {
		this.postBuild_trigger_option = postBuild_trigger_option;
	}
	public ArrayList<SubModuleParametersTO> getSubModuleParametersList() {
		return subModuleParametersList;
	}
	public void setSubModuleParametersList(ArrayList<SubModuleParametersTO> subModuleParametersList) {
		this.subModuleParametersList = subModuleParametersList;
	}
	

}
