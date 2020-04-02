package com.PLATO.userTO;

import java.util.List;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class PermissionDataModuleTO {
	private String moduleName;
	private List<PermissionDataScreenTO> screens;
	private boolean checked;
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
//	public List<PermissionDataScreenTO> getPermissionDataScreenTOList() {
//		return permissionDataScreenTOList;
//	}
//	public void setPermissionDataScreenTOList(List<PermissionDataScreenTO> permissionDataScreenTOList) {
//		this.permissionDataScreenTOList = permissionDataScreenTOList;
//	}
	public List<PermissionDataScreenTO> getScreens() {
		return screens;
	}
	public void setScreens(List<PermissionDataScreenTO> screens) {
		this.screens = screens;
	}
}
