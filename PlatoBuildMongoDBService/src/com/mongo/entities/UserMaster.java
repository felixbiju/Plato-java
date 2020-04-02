package com.mongo.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="user_master",schema="plato_db")
public class UserMaster
{
	@Id
	private String user_id;
	

	private String name;
	
	private String password;
	

	@OneToMany(mappedBy = "userMaster")
	private Set<UserProjectMapping> userProjectMapping;
	

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

	public Set<UserProjectMapping> getUserProjectMapping() {
		return userProjectMapping;
	}

	public void setUserProjectMapping(Set<UserProjectMapping> userProjectMapping) {
		this.userProjectMapping = userProjectMapping;
	}
	
	
	
	
}
