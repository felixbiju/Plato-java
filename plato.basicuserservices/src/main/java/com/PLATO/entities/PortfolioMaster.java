package com.PLATO.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="portfolio_master",schema="plato_db")
public class PortfolioMaster {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int portfolio_id;
	
	private String portfolio_name;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="portfolioMaster")
	private Set<AccountMaster> accountMaster;

	public int getPortfolio_id() {
		return portfolio_id;
	}

	public void setPortfolio_id(int portfolio_id) {
		this.portfolio_id = portfolio_id;
	}

	public String getPortfolio_name() {
		return portfolio_name;
	}

	public void setPortfolio_name(String portfolio_name) {
		this.portfolio_name = portfolio_name;
	}

	public Set<AccountMaster> getAccountMaster() {
		return accountMaster;
	}

	public void setAccountMaster(Set<AccountMaster> accountMaster) {
		this.accountMaster = accountMaster;
	}

}
