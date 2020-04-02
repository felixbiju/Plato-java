package com.PLATO.mongodbentities;

import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

@Entity("ModuleCollection")
@Indexes({@Index(fields = @Field("module_id"), options = @IndexOptions(unique=true,name = "module_id_index")),
	@Index(fields = @Field("module_name"), options = @IndexOptions(unique=true,name = "module_name_index"))	
})
public class Module extends BaseEntity
{
	private String module_id;
	private String module_name,master_job_name;
	
	private Set<Job> jobs;
	private Set<Execution> executions;
	public String getModule_id() {
		return module_id;
	}
	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}
	public String getModule_name() {
		return module_name;
	}
	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}
	public String getMaster_job_name() {
		return master_job_name;
	}
	public void setMaster_job_name(String master_job_name) {
		this.master_job_name = master_job_name;
	}
	public Set<Job> getJobs() {
		return jobs;
	}
	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}
	public Set<Execution> getExecutions() {
		return executions;
	}
	public void setExecutions(Set<Execution> executions) {
		this.executions = executions;
	}
	
	


}
