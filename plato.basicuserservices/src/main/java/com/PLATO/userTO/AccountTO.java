package com.PLATO.userTO;

public class AccountTO {

	private int accountId;
	private String accountName;
	private String accountHead;
	
	private String accountStatus;
	
	private String accountLogo;
	private String backgroundImage;
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountHead() {
		return accountHead;
	}
	public void setAccountHead(String accountHead) {
		this.accountHead = accountHead;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getAccountLogo() {
		return accountLogo;
	}
	public void setAccountLogo(String accountLogo) {
		this.accountLogo = accountLogo;
	}
	public String getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	
	
}
