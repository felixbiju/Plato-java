package com.PLATO.userTO;

public class BuildHistoryTO
{
    private int buildHistoryId;
    private String buildNumber;
   
  //  @JsonbDateFormat("dd-MMM-yyyy HH:mm:ss a")
    private String timestamp;
    private String Status;
    
    
	public int getBuildHistoryId() {
		return buildHistoryId;
	}
	public void setBuildHistoryId(int buildHistoryId) {
		this.buildHistoryId = buildHistoryId;
	}
	public String getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
    
    
}
