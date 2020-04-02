package com.PLATO.userTO;

import java.util.Set;

public class UserWithRoleArrayTO {
	//this class is being used to display a userTO of a particular project with all roles
	private String user_id;
	private String name;
	private String password;
	private String project_name;
	private int project_id;
	private String default_project;
	public String getDefault_project() {
		return default_project;
	}
	public void setDefault_project(String default_project) {
		this.default_project = default_project;
	}
	private Set<String> role_name;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public Set<String> getRole_name() {
		return role_name;
	}
	public void setRole_name(Set<String> role_name) {
		this.role_name = role_name;
	}
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}
}
