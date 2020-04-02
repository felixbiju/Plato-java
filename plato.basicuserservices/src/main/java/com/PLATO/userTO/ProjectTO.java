package com.PLATO.userTO;

import java.util.Date;

import javax.json.bind.annotation.JsonbDateFormat;

public class ProjectTO {

	
	private int project_id;
	
	private String project_name;
	private String project_status;
	
	@JsonbDateFormat("dd-MMM-yyyy")
    private Date project_creation_date;
    
	private int account_id;
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getProject_status() {
		return project_status;
	}
	public void setProject_status(String project_status) {
		this.project_status = project_status;
	}
	public Date getProject_creation_date() {
		return project_creation_date;
	}
	public void setProject_creation_date(Date project_creation_date) {
		this.project_creation_date = project_creation_date;
	}
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	
	
	
}
