package com.plato.tem.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="tool_category_master",schema="plato_db")
public class CategoryMaster
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int category_id;
    private String category_name;
    
    @OneToMany(cascade=CascadeType.ALL,mappedBy="categoryMaster")
    private Set<ToolMaster> toolMaster;
    
    
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
	public Set<ToolMaster> getToolMaster() {
		return toolMaster;
	}
	public void setToolMaster(Set<ToolMaster> toolMaster) {
		this.toolMaster = toolMaster;
	}
	
    
    
}
