package com.PLATO.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Entity
@Table(name="role_permission_mapping",schema="plato_db")
public class RolePermissionMapping{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    int id;
	
//	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//	@JoinColumn(name = "role_id")  
//	private RoleMaster roleMaster;
	private int role_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "screen_functionality_id")  
	private ScreenFunctionality screenFunctionality;
	
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean permission;

//	private String create_permission;
//	private String update_permission;
//	private String delete_permission;
//	private String view_permission;
	
//	public RoleMaster getRoleMaster() {
//		return roleMaster;
//	}
	public ScreenFunctionality getScreenFunctionality() {
		return screenFunctionality;
	}
	public void setScreenFunctionality(ScreenFunctionality screenFunctionality) {
		this.screenFunctionality = screenFunctionality;
	}
	public boolean getPermission() {
		return permission;
	}
	public void setPermission(boolean permission) {
		this.permission = permission;
	}
//	public void setRoleMaster(RoleMaster roleMaster) {
//		this.roleMaster = roleMaster;
//	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


}
