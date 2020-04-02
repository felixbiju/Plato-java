package com.mongo.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_project_mapping",schema="plato_db")
public class UserProjectMapping
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    int id;
	
	@ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")  
	private UserMaster userMaster;
	
	private String default_project;
	
	
	@ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")  
	private ProjectMaster projectMaster;
	
	@ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")  
	private RoleMaster roleMaster;
	
	@ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")  
	private AccountMaster accountMaster;
	
	@ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfolio_id")  
	private PortfolioMaster portfolioMaster;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserMaster getUserMaster() {
		return userMaster;
	}

	public void setUserMaster(UserMaster userMaster) {
		this.userMaster = userMaster;
	}

	public String getDefault_project() {
		return default_project;
	}

	public void setDefault_project(String default_project) {
		this.default_project = default_project;
	}

	public ProjectMaster getProjectMaster() {
		return projectMaster;
	}

	public void setProjectMaster(ProjectMaster projectMaster) {
		this.projectMaster = projectMaster;
	}

	public RoleMaster getRoleMaster() {
		return roleMaster;
	}

	public void setRoleMaster(RoleMaster roleMaster) {
		this.roleMaster = roleMaster;
	}

	public AccountMaster getAccountMaster() {
		return accountMaster;
	}

	public void setAccountMaster(AccountMaster accountMaster) {
		this.accountMaster = accountMaster;
	}

	public PortfolioMaster getPortfolioMaster() {
		return portfolioMaster;
	}

	public void setPortfolioMaster(PortfolioMaster portfolioMaster) {
		this.portfolioMaster = portfolioMaster;
	}
	
	
	
}

    