package com.PLATO.mongodbentities;

public class Job_build
{
    private String build_id;
    
    private String job_name,build_status,duration,execution_report_path,report_type;

	public String getBuild_id() {
		return build_id;
	}

	public void setBuild_id(String build_id) {
		this.build_id = build_id;
	}

	public String getJob_id() {
		return job_name;
	}

	public void setJob_id(String job_name) {
		this.job_name = job_name;
	}

	public String getBuild_status() {
		return build_status;
	}

	public void setBuild_status(String build_status) {
		this.build_status = build_status;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getExecution_report_path() {
		return execution_report_path;
	}

	public void setExecution_report_path(String execution_report_path) {
		this.execution_report_path = execution_report_path;
	}

	public String getReport_type() {
		return report_type;
	}

	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}
    
}
