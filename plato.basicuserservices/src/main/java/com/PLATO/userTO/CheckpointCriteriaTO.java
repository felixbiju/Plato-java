package com.PLATO.userTO;

public class CheckpointCriteriaTO
{
	private int checkpoint_criteria_id,order_number;
	private String checkpoint_name,pass_criteria,fail_criteria;
	private boolean checked;
	
	
	public int getCheckpoint_criteria_id() {
		return checkpoint_criteria_id;
	}
	public void setCheckpoint_criteria_id(int checkpoint_criteria_id) {
		this.checkpoint_criteria_id = checkpoint_criteria_id;
	}
	public String getCheckpoint_name() {
		return checkpoint_name;
	}
	public void setCheckpoint_name(String checkpoint_name) {
		this.checkpoint_name = checkpoint_name;
	}
	public String getPass_criteria() {
		return pass_criteria;
	}
	public void setPass_criteria(String pass_criteria) {
		this.pass_criteria = pass_criteria;
	}
	public String getFail_criteria() {
		return fail_criteria;
	}
	public void setFail_criteria(String fail_criteria) {
		this.fail_criteria = fail_criteria;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
