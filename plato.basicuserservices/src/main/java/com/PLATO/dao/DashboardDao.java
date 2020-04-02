package com.PLATO.dao;

import java.util.List;

import com.PLATO.entities.ProjectMaster;

public interface DashboardDao {
	public List<ProjectMaster> getAllocatedProjectsForUser(String username);
}
