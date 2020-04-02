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
@Table(name="status_master",schema="plato_db")
public class StatusMaster {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int status_id;

	private String status_name;

	@OneToMany(cascade=CascadeType.ALL,mappedBy="statusMaster")
	private Set<ModuleBuildHistory>moduleBuildHistory;

	public int getStatus_id() {
		return status_id;
	}

	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}

	public String getStatus_name() {
		return status_name;
	}

	public void setStatus_name(String status_name) {
		this.status_name = status_name;
	}

	public Set<ModuleBuildHistory> getModuleBuildHistory() {
		return moduleBuildHistory;
	}

	public void setModuleBuildHistory(Set<ModuleBuildHistory> moduleBuildHistory) {
		this.moduleBuildHistory = moduleBuildHistory;
	}



}
