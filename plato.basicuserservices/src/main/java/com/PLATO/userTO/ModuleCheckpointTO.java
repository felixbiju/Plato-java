package com.PLATO.userTO;

import java.util.List;


public class ModuleCheckpointTO 
{
	private int module_checkpoint_id;
	private int order_number;
	private String module_name;
	private List<CheckpointCriteriaTO> checkpoint_criteria;
	private int module_subjob_id;
	private String tool_name;
	
	public int getModule_checkpoint_id() {
		return module_checkpoint_id;
	}
	public void setModule_checkpoint_id(int module_checkpoint_id) {
		this.module_checkpoint_id = module_checkpoint_id;
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
	public List<CheckpointCriteriaTO> getCheckpoint_criteria() {
		return checkpoint_criteria;
	}
	public void setCheckpoint_criteria(List<CheckpointCriteriaTO> checkpoint_criteria) {
		this.checkpoint_criteria = checkpoint_criteria;
	}
	public int getModule_subjob_id() {
		return module_subjob_id;
	}
	public void setModule_subjob_id(int module_subjob_id) {
		this.module_subjob_id = module_subjob_id;
	}
	public String getTool_name() {
		return tool_name;
	}
	public void setTool_name(String tool_name) {
		this.tool_name = tool_name;
	}
	
	
		
}
