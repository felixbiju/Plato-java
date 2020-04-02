package com.plato.tem.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="checkpoint_details_template",schema="plato_db")
public class CheckpointDetailsTemplate {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int checkpoint_details_template_id;
	private int order_number;
	private String checkpoint_name,pass_criteria,fail_criteria;
	
	@ManyToOne
	@JoinColumn(name="checkpoint_template_id", nullable=false)
	private CheckpointTemplate checkpoint_template;
	
	public int getCheckpoint_details_template_id() {
		return checkpoint_details_template_id;
	}
	public void setCheckpoint_details_template_id(int checkpoint_details_template_id) {
		this.checkpoint_details_template_id = checkpoint_details_template_id;
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
	public CheckpointTemplate getCheckpoint_template() {
		return checkpoint_template;
	}
	public void setCheckpoint_template(CheckpointTemplate checkpoint_template) {
		this.checkpoint_template = checkpoint_template;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	@Override
	public boolean equals(Object arg0) {
		CheckpointDetailsTemplate checkpointDetailsTemplate=(CheckpointDetailsTemplate) arg0;
	    if(this.checkpoint_details_template_id==checkpointDetailsTemplate.checkpoint_details_template_id)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	
	
		
}
