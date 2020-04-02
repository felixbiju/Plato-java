package com.plato.tem.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="subjob_checkpoint_details",schema="plato_db")
public class SubjobCheckpointDetails implements Comparable<SubjobCheckpointDetails>{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int subjob_checkpoint_details_id;
	private int order_number;
	private String checkpoint_name,pass_criteria,fail_criteria;

	
	@ManyToOne
	@JoinColumn(name="subjob_checkpoint_id", nullable=false)
	private SubjobCheckpoint subjob_checkpoint;
	
	public int getSubjob_checkpoint_details_id() {
		return subjob_checkpoint_details_id;
	}
	public void setSubjob_checkpoint_details_id(int subjob_checkpoint_details_id) {
		this.subjob_checkpoint_details_id = subjob_checkpoint_details_id;
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
	public SubjobCheckpoint getSubjob_checkpoint() {
		return subjob_checkpoint;
	}
	public void setSubjob_checkpoint(SubjobCheckpoint subjob_checkpoint) {
		this.subjob_checkpoint = subjob_checkpoint;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	@Override
	public int compareTo(SubjobCheckpointDetails o) {
		// TODO Auto-generated method stub
		int order_number = ((SubjobCheckpointDetails) o).getOrder_number();
		return this.order_number-order_number;
	}

}
