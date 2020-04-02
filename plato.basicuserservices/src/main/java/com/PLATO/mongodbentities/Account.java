package com.PLATO.mongodbentities;

import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;


@Entity("AccountCollection")
//@Indexes({@Index(fields={@Field("account_id"),@Field("projects.project_id"),@Field("projects.modules.module_id"),@Field("projects.modules.executions.execution_id"),@Field("projects.modules.executions.jobs_built.build_id")},options =@IndexOptions(unique=true,name="AccountIndex"))}) 
//@Indexes({@Index(fields= {@Field("account_id"), @Field("projects.project_id"),options= @IndexOptions(unique = true)), @Index(options= @IndexOptions(unique = true), fields= {@Field("c")})})
/*@Indexes({
    @Index("account_id,projects.project_id")
})*/
@Indexes({@Index(fields = @Field("account_id"), options = @IndexOptions(unique=true,name = "account_id_index")),
	@Index(fields = @Field("account_name"), options = @IndexOptions(unique=true,name = "account_name_index"))	
})
public class Account extends BaseEntity
{
	//@Indexed(options = @IndexOptions(unique=true))
	private String account_id;
	private String account_name,account_manager;

	@Reference
	private Set<Project> projects;

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public String getAccount_manager() {
		return account_manager;
	}

	public void setAccount_manager(String account_manager) {
		this.account_manager = account_manager;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}







}
