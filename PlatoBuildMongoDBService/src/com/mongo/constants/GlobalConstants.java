package com.mongo.constants;

import com.adapters.utilities.PropertiesFileUtility;

public class GlobalConstants
{
	/**
	 * @author 10643331(Sueanne Alphonso)
	 **/
	
	/*Jenkins Constants*/
	/*public static final String JENKINS_URL="http://localhost";
	public static final String JENKINS_PORT="9080";*/
	
	public static final String JENKINS_URL=PropertiesFileUtility.getProperty("JENKINS_URL");
	public static final String JENKINS_PORT=PropertiesFileUtility.getProperty("JENKINS_PORT");
	
	public static final String JENKINS_JOBCONFIG_XML="xmlFiles/jobConfig.xml";
	public static final String JOBCONFIG_XML="d:/jobConfig.xml";
	public static final String JENKINS_WORKSPACE="C:/Users/10635492.PWIODC/.jenkins";
	
	
	
    /*public static final String MONGODB_URL="localhost";
    public static final int MongoDB_PORT=27011;  //27017;
    public static final String MONGODB_DATABASE_NAME="PLATODB";
    public static final String MONGODB_COLLECTION="Build_History_Coll";
    public static final String ENTITY_PACKAGE="com.PLATO.entities";*/
	
	public static final String MONGODB_URL=PropertiesFileUtility.getProperty("MONGODB_URL");
    public static final int MongoDB_PORT=Integer.parseInt(PropertiesFileUtility.getProperty("MongoDB_PORT"));  //27017;
    public static final String MONGODB_DATABASE_NAME="BuildHistoryJson";
    public static final String MONGODB_COLLECTION="BuildHistoryDetails";
    public static final String ENTITY_PACKAGE="com.PLATO.entities";
    
    
    /* Response Constant Strings  */
    public static final String FORBIDDEN="Access Denied";
    public static final String UNAUTHORIZED="You Are Not Authorized to access this Resource";
    public static final String LOGIN_REQUIRED="Login Again !!";
  
    //Secret Key for Signing JWT
    public static final String SECRET="PlAto_By_Tsl@TCoe";
    
    public static String projectsIn="";
    
    /*Constants for Jenkins console services*/
    public static final String JENKINS_CONSOLE_SERVICES_URL=PropertiesFileUtility.getProperty("JENKINS_CONSOLE_SERVICES_URL");
    public static final String JENKINS_CONSOLE_SERVICES_PORT=PropertiesFileUtility.getProperty("JENKINS_CONSOLE_SERVICES_PORT");
    
    public static final String PROPERTIES_FILE_PATH="/resources/Constants.properties";
    
    //constants for proxy in temAppService,temService
    public static final String PROXY_URL_TEM_APP=PropertiesFileUtility.getProperty("PROXY_URL_TEM_APP");
    public static final String PROXY_PORT_TEM_APP=PropertiesFileUtility.getProperty("PROXY_PORT_TEM_APP");
    public static final String PROXY_URL_TEM_SERVER=PropertiesFileUtility.getProperty("PROXY_URL_TEM_SERVER");
    public static final String PROXY_PORT_TEM_SERVER=PropertiesFileUtility.getProperty("PROXY_PORT_TEM_SERVER");
    
    //jenkins home directory
    public static final String JENKINS_HOME=PropertiesFileUtility.getProperty("JENKINS_HOME");
  
}
