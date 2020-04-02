package com.PLATO.userTO;

import java.util.ArrayList;
import java.util.HashMap;

import com.PLATO.alm.Infrastucture.AlmConnector;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class AlmInfoTO {
private String domain;
private String project;
private String testName;
private String execStatus;
private String releaseName;
private ArrayList<String>subFolderNames;
public String getReleaseName() {
	return releaseName;
}
public void setReleaseName(String releaseName) {
	this.releaseName = releaseName;
}
public ArrayList<String> getSubFolderNames() {
	return subFolderNames;
}
public void setSubFolderNames(ArrayList<String> subFolderNames) {
	this.subFolderNames = subFolderNames;
}
public HashMap<String, String> getTestCaseStatusMap() {
	return testCaseStatusMap;
}
public void setTestCaseStatusMap(HashMap<String, String> testCaseStatusMap) {
	this.testCaseStatusMap = testCaseStatusMap;
}
private HashMap<String,String>testCaseStatusMap;
public String getTestName() {
	return testName;
}
public void setTestName(String testName) {
	this.testName = testName;
}
public String getExecStatus() {
	return execStatus;
}
public void setExecStatus(String execStatus) {
	this.execStatus = execStatus;
}
private boolean above12;
public boolean isAbove12() {
	return above12;
}
public void setAbove12(boolean above12) {
	this.above12 = above12;
}
public String getDomain() {
	return domain;
}
public void setDomain(String domain) {
	this.domain = domain;
}
public String getProject() {
	return project;
}
public void setProject(String project) {
	this.project = project;
}

}
