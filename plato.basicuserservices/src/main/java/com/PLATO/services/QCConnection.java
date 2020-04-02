package com.PLATO.services;

import org.apache.log4j.Logger;

import com.PLATO.userTO.QCDetails;
import com.mercury.qualitycenter.otaclient.ClassFactory;
import com.mercury.qualitycenter.otaclient.ITDConnection;


/**
* FAST-Java-Common: QCConnection is the class that manages connecting to QC and its report uploads.
* @author Deepika
* @version 1.0
*/
public class QCConnection {
	
	private static final Logger logger = Logger.getLogger(QCConnection.class);
	
	//Connection details
	private QCDetails qcDetails;
	
	//Connection object
	private ITDConnection connection;
	
	//Is connection possible
	private boolean connect;
	
	//Whether to update test plan/test lab--from FAST UI
	private boolean updateTestPlan;
	private boolean updateTestLab;
	private boolean continueOnInvalidTestCaseID;
	
	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	/**
	 * QCConnection constructor. It initializes connection to QC and check if its valid.
	 * @param url QC url
	 * @param username QC username to connect
	 * @param password Password for above mentioned user
	 * @param domain Domain to connect in QC
	 * @param project Project to connect in QC
	 */
	public QCConnection(String url, String username, String password, String domain, String project){
		connect = false;
		
		//Set all details
		this.qcDetails = new QCDetails();
		this.qcDetails.setQcURL(url);
		this.qcDetails.setQcUsername(username);
		this.qcDetails.setQcPassword(password);
		this.qcDetails.setQcDomain(domain);
		this.qcDetails.setQcProject(project);
		
	
	}
	
	/**
	 * Method to initialize connection to QC
	 * @throws Exception
	 */
	public void connect() throws Exception{
		connection = ClassFactory.createTDConnection();
		logger.debug("Connecting to QC: " + connection.connected());
		connection.initConnectionEx(qcDetails.getQcURL());
		logger.debug("Connection initiated.");
		connection.connectProjectEx(qcDetails.getQcDomain(), qcDetails.getQcProject(), qcDetails.getQcUsername(), qcDetails.getQcPassword());
		logger.debug("Connected to QC: " + connection.connected());
	}
	
	/**
	 * Method to disconnect to QC.
	 */
	public void disconnect(){
		if(connection != null){
			connection.disconnectProject();
			connection.dispose();
			logger.debug("QC Connection closed!!");
		}
	}
	
	public ITDConnection getQCConnection(){
		return connection;
	}
	
	public QCDetails getQCDetails(){
		return qcDetails;
	}

	public boolean isUpdateTestPlan() {
		return updateTestPlan;
	}

	public boolean isUpdateTestLab() {
		return updateTestLab;
	}

	public boolean isContinueOnInvalidTestCaseID() {
		return continueOnInvalidTestCaseID;
	}

}
