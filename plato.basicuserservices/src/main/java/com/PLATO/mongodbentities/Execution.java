package com.PLATO.mongodbentities;


import java.util.Set;

public class Execution 
{
     private String execution_id;
     private String master_execution_status;
     private String execution_date_time;
     
     private Set<Job_build> jobs_built;

	public String getExecution_id() {
		return execution_id;
	}

	public void setExecution_id(String execution_id) {
		this.execution_id = execution_id;
	}

	public String getMaster_execution_status() {
		return master_execution_status;
	}

	public void setMaster_execution_status(String master_execution_status) {
		this.master_execution_status = master_execution_status;
	}

	public String getExecution_date_time() {
		return execution_date_time;
	}

	public void setExecution_date_time(String execution_date_time) {
		this.execution_date_time = execution_date_time;
	}

	public Set<Job_build> getJobs_built() {
		return jobs_built;
	}

	public void setJobs_built(Set<Job_build> jobs_built) {
		this.jobs_built = jobs_built;
	}
     
     
}
