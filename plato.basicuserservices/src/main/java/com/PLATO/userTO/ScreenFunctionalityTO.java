package com.PLATO.userTO;

import com.PLATO.entities.PermissionScreenMaster;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class ScreenFunctionalityTO {
	   	
	private int screenFunctionalityId;
	private String funcName;
	private String funcChecked;
	private String funcBtn;
	private PermissionScreenMaster permissionScreenMaster;
	public int getScreenFunctionalityId() {
		return screenFunctionalityId;
	}
	public void setScreenFunctionalityId(int screenFunctionalityId) {
		this.screenFunctionalityId = screenFunctionalityId;
	}
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public String getFuncChecked() {
		return funcChecked;
	}
	public void setFuncChecked(String funcChecked) {
		this.funcChecked = funcChecked;
	}
	public String getFuncBtn() {
		return funcBtn;
	}
	public void setFuncBtn(String funcBtn) {
		this.funcBtn = funcBtn;
	}
	public PermissionScreenMaster getPermissionScreenMaster() {
		return permissionScreenMaster;
	}
	public void setPermissionScreenMaster(PermissionScreenMaster permissionScreenMaster) {
		this.permissionScreenMaster = permissionScreenMaster;
	}
	
}
