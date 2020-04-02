package com.plato.tem.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Entity
@Table(name="permission_screen_master",schema="plato_db")
public class PermissionScreenMaster{
	
	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getScreen_checked() {
		return screen_checked;
	}

	public void setScreen_checked(String screen_checked) {
		this.screen_checked = screen_checked;
	}

	public PermissionModuleMaster getPermissionModuleMaster() {
		return permissionModuleMaster;
	}

	public void setPermissionModuleMaster(PermissionModuleMaster permissionModuleMaster) {
		this.permissionModuleMaster = permissionModuleMaster;
	}

//	public Set<RolePermissionMapping> getRolePermissionMapping() {
//		return rolePermissionMapping;
//	}
//
//	public void setRolePermissionMapping(Set<RolePermissionMapping> rolePermissionMapping) {
//		this.rolePermissionMapping = rolePermissionMapping;
//	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int screen_id;
	
    private String screen_name;
    private String screen_checked;
    
	@ManyToOne
	@JoinColumn(name="permission_module_id")
	private PermissionModuleMaster permissionModuleMaster;
    
    public int getScreen_id() {
		return screen_id;
	}

	public void setScreen_id(int screen_id) {
		this.screen_id = screen_id;
	}

//	@OneToMany(mappedBy = "PermissionScreenMaster")
//    private Set<RolePermissionMapping> rolePermissionMapping;


    
    
}
