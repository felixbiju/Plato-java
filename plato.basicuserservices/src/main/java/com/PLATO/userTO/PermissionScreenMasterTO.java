package com.PLATO.userTO;


import com.PLATO.entities.PermissionModuleMaster;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class PermissionScreenMasterTO {
    private int screenId;
	private String screenName;
    private String screenChecked;
	private PermissionModuleMaster permissionModuleMaster;
	public int getScreenId() {
		return screenId;
	}
	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getScreenChecked() {
		return screenChecked;
	}
	public void setScreenChecked(String screenChecked) {
		this.screenChecked = screenChecked;
	}
	public PermissionModuleMaster getPermissionModuleMaster() {
		return permissionModuleMaster;
	}
	public void setPermissionModuleMaster(PermissionModuleMaster permissionModuleMaster) {
		this.permissionModuleMaster = permissionModuleMaster;
	}

}
