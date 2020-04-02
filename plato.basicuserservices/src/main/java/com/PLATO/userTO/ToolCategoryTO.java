package com.PLATO.userTO;

import java.util.Set;

public class ToolCategoryTO
{
	
	private int category_id;
	private String category_name;
	private Set<ToolTO> tools;
	
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public Set<ToolTO> getTools() {
		return tools;
	}
	public void setTools(Set<ToolTO> tools) {
		this.tools = tools;
	}
	
	
}
