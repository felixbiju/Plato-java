package com.PLATO.userTO;

import java.util.List;

public class MainCheckpointTO 
{
    private int subjob_id;
    private String subjob_name;
    private int project_tool_mapping_id;
    private List<ModuleCheckpointTO> subjob_checkpoint;
    
	public int getSubjob_id() {
		return subjob_id;
	}
	public void setSubjob_id(int subjob_id) {
		this.subjob_id = subjob_id;
	}
	public List<ModuleCheckpointTO> getSubjob_checkpoint() {
		return subjob_checkpoint;
	}
	public void setSubjob_checkpoint(List<ModuleCheckpointTO> subjob_checkpoint) {
		this.subjob_checkpoint = subjob_checkpoint;
	}
	public int getProject_tool_mapping_id() {
		return project_tool_mapping_id;
	}
	public void setProject_tool_mapping_id(int project_tool_mapping_id) {
		this.project_tool_mapping_id = project_tool_mapping_id;
	}
	public String getSubjob_name() {
		return subjob_name;
	}
	public void setSubjob_name(String subjob_name) {
		this.subjob_name = subjob_name;
	}
	
}
