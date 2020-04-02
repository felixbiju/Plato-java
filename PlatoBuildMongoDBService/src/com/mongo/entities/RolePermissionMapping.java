package com.mongo.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="role_permission_mapping",schema="plato_db")
public class RolePermissionMapping
{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    int id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "role_id")  
	private RoleMaster roleMaster;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "permission_screen_id")  
	private PermissionScreenMaster permissionScreenMaster;

	private String create_permission;
	private String update_permission;
	private String delete_permission;
	private String view_permission;
	
	public String getView_permission() {
		return view_permission;
	}
	public void setView_permission(String view_permission) {
		this.view_permission = view_permission;
	}
	public RoleMaster getRoleMaster() {
		return roleMaster;
	}
	public void setRoleMaster(RoleMaster roleMaster) {
		this.roleMaster = roleMaster;
	}
	public PermissionScreenMaster getPermissionScreenMaster() {
		return permissionScreenMaster;
	}
	public void setPermissionScreenMaster(
			PermissionScreenMaster permissionScreenMaster) {
		this.permissionScreenMaster = permissionScreenMaster;
	}
	public String getCreate_permission() {
		return create_permission;
	}
	public void setCreate_permission(String create_permission) {
		this.create_permission = create_permission;
	}
	public String getUpdate_permission() {
		return update_permission;
	}
	public void setUpdate_permission(String update_permission) {
		this.update_permission = update_permission;
	}
	public String getDelete_permission() {
		return delete_permission;
	}
	public void setDelete_permission(String delete_permission) {
		this.delete_permission = delete_permission;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


}
