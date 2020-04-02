package com.mongo.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="role_master",schema="plato_db")
public class RoleMaster {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int role_id;

	private String role_name;

	@OneToMany(mappedBy = "roleMaster")
	private Set<UserProjectMapping> userProjectMapping;

	@OneToMany(mappedBy = "roleMaster")
	private Set<RolePermissionMapping> rolePermissionMapping;

	public int getRole_id() {
		return role_id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public Set<UserProjectMapping> getUserProjectMapping() {
		return userProjectMapping;
	}

	public void setUserProjectMapping(Set<UserProjectMapping> userProjectMapping) {
		this.userProjectMapping = userProjectMapping;
	}

	public Set<RolePermissionMapping> getRolePermissionMapping() {
		return rolePermissionMapping;
	}

	public void setRolePermissionMapping(
			Set<RolePermissionMapping> rolePermissionMapping) {
		this.rolePermissionMapping = rolePermissionMapping;
	}

	

}
