package com.PLATO.userTO;

import java.util.Set;

public class ToolOrderTO {
	private int order;
	private String categoryName;
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Set<ToolTO> getToolToSet() {
		return toolToSet;
	}
	public void setToolToSet(Set<ToolTO> toolToSet) {
		this.toolToSet = toolToSet;
	}
	private Set<ToolTO> toolToSet;
	public ToolOrderTO() {
		
	}
	public ToolOrderTO(int order,String categoryName,Set<ToolTO> toolToSet) {
		this.categoryName=categoryName;
		this.order=order;
		this.toolToSet=toolToSet;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
//	public Set<ToolTO> getToolTo() {
//		return toolToSet;
//	}
//	public void setToolTo(Set<ToolTO> toolToSet) {
//		this.toolToSet = toolToSet;
//	}

}
