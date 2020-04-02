package com.PLATO.entities;

import javax.persistence.CascadeType;
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
@Table(name="screen_functionality",schema="plato_db")
public class ScreenFunctionality {
	public int getScreen_functionality_id() {
		return screen_functionality_id;
	}

	public void setScreen_functionality_id(int screen_functionality_id) {
		this.screen_functionality_id = screen_functionality_id;
	}

	public String getFunc_name() {
		return func_name;
	}

	public void setFunc_name(String func_name) {
		this.func_name = func_name;
	}

	public String getFunc_checked() {
		return func_checked;
	}

	public void setFunc_checked(String func_checked) {
		this.func_checked = func_checked;
	}

	public String getFunc_btn() {
		return func_btn;
	}

	public void setFunc_btn(String func_btn) {
		this.func_btn = func_btn;
	}

	public PermissionScreenMaster getPermissionScreenMaster() {
		return permissionScreenMaster;
	}

	public void setPermissionScreenMaster(PermissionScreenMaster permissionScreenMaster) {
		this.permissionScreenMaster = permissionScreenMaster;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int screen_functionality_id;
	
	private String func_name;
	private String func_checked;
	private String func_btn;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "screen_id")
	private PermissionScreenMaster permissionScreenMaster;
	

}
