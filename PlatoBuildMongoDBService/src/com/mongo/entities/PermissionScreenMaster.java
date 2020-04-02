package com.mongo.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="permission_screen_master",schema="plato_db")
public class PermissionScreenMaster
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int permission_screen_id;
    
    /*private String dashboard;
    private String menu;
    private String parent_id;
    private String dom_id;*/
    
    @OneToMany(mappedBy = "permissionScreenMaster")
    private Set<RolePermissionMapping> rolePermissionMapping;

	public int getPermission_screen_id() {
		return permission_screen_id;
	}

	public void setPermission_screen_id(int permission_screen_id) {
		this.permission_screen_id = permission_screen_id;
	}

/*	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getDom_id() {
		return dom_id;
	}

	public void setDom_id(String dom_id) {
		this.dom_id = dom_id;
	}
*/
	public Set<RolePermissionMapping> getRolePermissionMapping() {
		return rolePermissionMapping;
	}

	public void setRolePermissionMapping(
			Set<RolePermissionMapping> rolePermissionMapping) {
		this.rolePermissionMapping = rolePermissionMapping;
	}
    
    
}
