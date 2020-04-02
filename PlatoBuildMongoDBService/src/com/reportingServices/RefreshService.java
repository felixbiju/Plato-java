package com.reportingServices;

import org.apache.log4j.Logger;

/**
 * @author 10643380(Rahul Bhardwaj)
 * 
 * this is for tmnas, as we haven't got a complete solution for burp suite and performance center
 * so if the reports are copied after the execution, this will still give an output
 *  
 * */
public class RefreshService {
	private static final Logger logger=Logger.getLogger(RefreshService.class);
	
	private int buildHistoryId;
	private String jobName;
	private int buildNumber;
	private String status="Passed";
	private String jenkinsUrl;
	private String toolName;
	private String reportPath;
	private boolean lastCheckpoint=false;
	
	public RefreshService(int buildHistoryId, int buildNumber, String jobName,String jenkinsUrl,String toolName,String reportPath) {
		// TODO Auto-generated constructor stub
		this.buildNumber=buildNumber;
		this.jobName=jobName;
		this.buildHistoryId=buildHistoryId;
		this.jenkinsUrl=jenkinsUrl;
		this.toolName=toolName;
		this.reportPath=reportPath;
	}
}
