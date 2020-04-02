package com.PLATO.userTO;

import java.util.List;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class PermissionDataScreenTO {
	private String screenName;
	private List<PermissionDataComponentTO> components;
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
//	public List<PermissionDataComponentTO> getPermissionDataComponentTOList() {
//		return permissionDataComponentTOList;
//	}
//	public void setPermissionDataComponentTOList(List<PermissionDataComponentTO> permissionDataComponentTOList) {
//		this.permissionDataComponentTOList = permissionDataComponentTOList;
//	}
	public List<PermissionDataComponentTO> getComponents() {
		return components;
	}
	public void setComponents(List<PermissionDataComponentTO> components) {
		this.components = components;
	}
	
}
