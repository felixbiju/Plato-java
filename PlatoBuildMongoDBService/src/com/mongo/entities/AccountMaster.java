package com.mongo.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="account_master",schema="plato_db")
public class AccountMaster {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int account_id;
	private String account_name;
	private String account_head;
	
	//private String account_status;
	
	/*private byte[] account_logo;
	private byte[] background_image;*/
	
	private String account_logo;
	private String background_image;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="accountMaster")
	private Set<ProjectMaster> projectMaster;
	
	@ManyToOne
	@JoinColumn(name="portfolio_id", nullable=false)
	private PortfolioMaster portfolioMaster;
	
	
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	public String getAccount_name() {
		return account_name;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	public String getAccount_head() {
		return account_head;
	}
	public void setAccount_head(String account_head) {
		this.account_head = account_head;
	}
	/*public byte[] getAccount_logo() {
		return account_logo;
	}
	public void setAccount_logo(byte[] account_logo) {
		this.account_logo = account_logo;
	}
	public byte[] getBackground_image() {
		return background_image;
	}
	public void setBackground_image(byte[] background_image) {
		this.background_image = background_image;
	}*/
	public Set<ProjectMaster> getProjectMaster() {
		return projectMaster;
	}
	public void setProjectMaster(Set<ProjectMaster> projectMaster) {
		this.projectMaster = projectMaster;
	}
	/*public String getAccount_status() {
		return account_status;
	}
	public void setAccount_status(String account_status) {
		this.account_status = account_status;
	}*/
	public String getAccount_logo() {
		return account_logo;
	}
	public void setAccount_logo(String account_logo) {
		this.account_logo = account_logo;
	}
	public String getBackground_image() {
		return background_image;
	}
	public void setBackground_image(String background_image) {
		this.background_image = background_image;
	}
	public PortfolioMaster getPortfolioMaster() {
		return portfolioMaster;
	}
	public void setPortfolioMaster(PortfolioMaster portfolioMaster) {
		this.portfolioMaster = portfolioMaster;
	}
	
	
	
	
	
}
