package com.plato.tem.constants;


import com.plato.tem.utilities.PropertiesFileUtility;

public class GlobalConstants
{
	/*Description: class having all global constants
	 * Author: Gaurav Kulkarni
	 * 
	 * 
	 * */
	
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
    
    public static final String PROPERTIES_FILE_PATH="propertiesFile/Constants.properties";
    
    //constants for proxy in temAppService,temService
    public static final String PROXY_URL_TEM_APP=PropertiesFileUtility.getProperty("PROXY_URL_TEM_APP");
    public static final String PROXY_PORT_TEM_APP=PropertiesFileUtility.getProperty("PROXY_PORT_TEM_APP");
    public static final String PROXY_URL_TEM_SERVER=PropertiesFileUtility.getProperty("PROXY_URL_TEM_SERVER");
    public static final String PROXY_PORT_TEM_SERVER=PropertiesFileUtility.getProperty("PROXY_PORT_TEM_SERVER");
    
    //jenkins home directory
    public static final String JENKINS_HOME=PropertiesFileUtility.getProperty("JENKINS_HOME");
    
    //ALM QC constants for updateQC QCConnection
    public static final String QC_URL=PropertiesFileUtility.getProperty("QC_URL");
    public static final String QC_USERNAME=PropertiesFileUtility.getProperty("QC_USERNAME");
    public static final String QC_PASSWORD=PropertiesFileUtility.getProperty("QC_PASSWORD");
    public static final String QC_DOMAIN=PropertiesFileUtility.getProperty("QC_DOMAIN");
    public static final String QC_PROJECT=PropertiesFileUtility.getProperty("QC_PROJECT");
    public static final String QC_TEST_SET_ID=PropertiesFileUtility.getProperty("QC_TEST_SET_ID");
    
    public static final String QC_TS_ID_AIS_PET=PropertiesFileUtility.getProperty("QC_TS_ID_AIS_PET");
    public static final String QC_TS_ID_AIS_EVENT=PropertiesFileUtility.getProperty("QC_TS_ID_AIS_EVENT");
    public static final String QC_TS_ID_AIS_WED=PropertiesFileUtility.getProperty("QC_TS_ID_AIS_WED");
    public static final String QC_TS_ID_DW_PET=PropertiesFileUtility.getProperty("QC_TS_ID_DW_PET");
    public static final String QC_TS_ID_DW_EVENT=PropertiesFileUtility.getProperty("QC_TS_ID_DW_EVENT");
    public static final String QC_TS_ID_DW_WED=PropertiesFileUtility.getProperty("QC_TS_ID_DW_WED");
}
