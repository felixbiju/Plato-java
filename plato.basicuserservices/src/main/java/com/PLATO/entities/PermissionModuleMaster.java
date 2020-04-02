package com.PLATO.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Entity
@Table(name="permission_module_master",schema="plato_db")
public class PermissionModuleMaster {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int permission_module_id;
	
	private String permission_module_name;
	private String module_checked;
	
	public int getPermission_module_id() {
		return permission_module_id;
	}
	public void setPermission_module_id(int permission_module_id) {
		this.permission_module_id = permission_module_id;
	}
	public String getPermission_module_name() {
		return permission_module_name;
	}
	public void setPermission_module_name(String permission_module_name) {
		this.permission_module_name = permission_module_name;
	}
	public String getModule_checked() {
		return module_checked;
	}
	public void setModule_checked(String module_checked) {
		this.module_checked = module_checked;
	}
	
	

}
