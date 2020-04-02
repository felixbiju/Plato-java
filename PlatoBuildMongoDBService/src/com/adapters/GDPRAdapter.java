package com.adapters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GDPRAdapter {
	private static final Logger logger=Logger.getLogger(WebServiceAdapter.class);

	public JSONArray getGDPRReport(String reportPath,String jobName,int buildNumber)throws IOException,SecurityException,NullPointerException,NumberFormatException,StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,InvalidFormatException,SQLException,ClassNotFoundException,Exception{
		
		System.out.println("report Path: "+reportPath);
		System.out.println("jobName: "+jobName);
		System.out.println("buildNumber: "+buildNumber);
		
		JSONArray toolReport=new JSONArray();
		
		try {
			logger.info("encoded url is "+reportPath);
			reportPath=URLDecoder.decode( reportPath, "UTF-8" );
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			
			logger.error("Error in url decoding");
			e1.printStackTrace();
		}  
		
		String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
		 //static final String DB_URL = "jdbc:mysql://localhost/STUDENTS";
		 
		String[] databaseDetails=reportPath.split(",");
		
		//String DB_URL = "jdbc:sqlserver://172.20.40.76:0;databaseName=Compliance_Portal";

		String DB_URL = "jdbc:sqlserver://"+databaseDetails[0]+";databaseName="+databaseDetails[1];
logger.info("reportPath---> :  "+reportPath);
logger.info("reportPath---> :  "+reportPath);
logger.info("databaseDetails"+databaseDetails[0]);
logger.info("databaseDetails"+databaseDetails[1]);
logger.info("databaseDetails"+databaseDetails[2]);
logger.info("databaseDetails"+databaseDetails[3]);
		

logger.info("database url: "+DB_URL);
		 //  Database credentials
		/*String USER = "sa";
		String PASS = "Newuser123";*/


		//databaseDetails[3]=databaseDetails[3].substring(0,databaseDetails[3].length() - 3);
		String USER=databaseDetails[2];
		String PASS=databaseDetails[3];
		
		Connection conn = null;
		Statement stmt = null;

		 try{
		    //STEP 2: Register JDBC driver
		    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		    //STEP 3: Open a connection
		    logger.info("Connecting to a selected database...");
		    conn = DriverManager.getConnection(DB_URL, USER, PASS);
		    logger.info("Connected database successfully...");
		    
		    //STEP 4: Execute a query
		    logger.info("Creating statement...");
		    stmt = conn.createStatement();
		    logger.info("created statement ");
		    //String sql = "SELECT id, first, last, age FROM Registration";
		    String sql = "select * from ["+databaseDetails[1]+"].[dbo].JobDetails where jobId=7;";
		    logger.info("sql query created");

		    ResultSet rs = stmt.executeQuery(sql);
		    logger.info("query executed");
		    logger.info("1 Found rows : "+rs.getFetchSize());
		    //STEP 5: Extract data from result set
		    while(rs.next()){
		       //Retrieve by column name
		       int iDiscoverJobId  = rs.getInt("jobId");
		       String iDiscoverJobName = rs.getString("jobName");     
		     

		       //Display values
		       logger.info("Job ID: " + iDiscoverJobId);
		       logger.info("Job Name : " + iDiscoverJobName);
		           	       
		       
		       
		    }
		    
		    sql = "SELECT [report_id],[IP_address],[Mac_address],[PC_name],[report],[reportGeneratedTime],[reportType],[sensitiveFieldCnt],[connection_id_ID],[jobDetails_jobId],[userId_user_id],[scannedFolderPath],[dataSize],[filecount],[foldersize] FROM ["+databaseDetails[1]+"].[dbo].[Reports] where jobDetails_jobId=7 ";
		    logger.info("again query executed");

		    rs=stmt.executeQuery(sql);
		    
		    logger.info("2 Found rows : "+rs.getFetchSize());
		  //  String reportIds="";
		    int reportID=0;
		    ArrayList arr= new ArrayList();
		    while(rs.next()){
		        //Retrieve by column name
		        int report_id  = rs.getInt("report_id");
		       // String jobName = rs.getString("jobName");
		        
		      

		        //Display values
		        logger.info("report_id : " + report_id);
		       // System.out.println("Job Name : " + jobName);
		         //reportIds=reportIds+report_id+",";    
		        
		        arr.add(report_id);
		        
		     }
		  /*  if(reportIds.endsWith(","))
		    {
		    	reportIds = reportIds.substring(0,reportIds.length() - 1);
		    }
		    */
		    
		    reportID=(int) Collections.max(arr);
/*		    iDiscover_TestingConfig1
		    databaseDetails[1]*/
		    //reportIds=reportIds.replace(reportIds.charAt(reportIds.length()-1),' ');
		//  System.out.println(reportIds);
		  //sql="select * from ["+databaseDetails[1]+"].[dbo].ReportDetails where reportID_report_id in ("+reportID+")";
		  sql="select * from ["+databaseDetails[1]+"].[dbo].ReportDetails where reportID_report_id in (2)";
		  logger.info(sql);
		  rs=stmt.executeQuery(sql);
		  logger.info("3 Found rows : "+rs.getFetchSize());
		  

		  JSONArray dataArray=new JSONArray();
		  
		  /*while(rs.next()){
			  JSONObject sqlTableData=new JSONObject();
		      //Retrieve by column name
		      //int report_id  = rs.getInt("report_id");
		     String tableName = rs.getString("tableName");
		     String columnName = rs.getString("columnName");
		     String sensitiveType = rs.getString("sensitiveType");
		     String pkTableName = rs.getString("pkTableName");
		     String elementType = rs.getString("elementType");

		      //Display values
		     // System.out.println("{ \"tableName\" : \"" + tableName+"\" , \"sensitiveType\" : \""+sensitiveType+"\" , \"columnName\" : \""+columnName+" \", \"pkTableName\" : \""+pkTableName+"\" , \"elementType\" :\" "+elementType+"\"}");
		     // System.out.println("Job Name : " + jobName);
		      // reportIds=report_id+",";    
		      sqlTableData.put("tableName", tableName);
		      sqlTableData.put("columnName", columnName);
		      sqlTableData.put("sensitiveType", sensitiveType);
		      sqlTableData.put("pkTableName", pkTableName);
		      sqlTableData.put("elementType", elementType);
		   
		      dataArray.add(sqlTableData);  
		      
		  }
		    
		  */
		
		 
		JSONObject toolReportObj=new JSONObject();

		
		
		JSONArray tabular_data =new JSONArray();
		
		int sensitiveTypeCount=0;
		int totalScannedTables=0;
		int sensitiveFieldCount=0;
		logger.info("Creating the tool report");
		
		while(rs.next()){
			
						      //Retrieve by column name
						      //int report_id  = rs.getInt("report_id");
						     String tableName = rs.getString("tableName");
						     String columnName = rs.getString("columnName");
						     String sensitiveType = rs.getString("sensitiveType");
						     String pkTableName = rs.getString("pkTableName");
						     String elementType = rs.getString("elementType");
						   
						     
		     
		     
		     
		     
		     // tabularDataObj.put("components",componentvalueArr);
						     if(tabular_data.size()>0) {
						    	 boolean tableExists=checkTableExists(tabular_data,tableName);										  

									    	  if(!tableExists) {
									    	  JSONObject tabularDataObj=new JSONObject();
									    	  JSONArray componentvalueArr=new JSONArray();
									    	  JSONObject componentvalueObj=new JSONObject();
									    	
									    	  JSONArray componentsArrayval=new JSONArray();
									    	  sensitiveFieldCount++;
									    	  JSONArray compArr=new JSONArray();
									    	  JSONObject coloumObj=new JSONObject();
									    	  coloumObj.put("Coloumn_Name",columnName);
									    	  compArr.add(coloumObj);
									    	  componentsArrayval.add(compArr);
									    	  
									    	  sensitiveTypeCount++;
									    	  
									    	  componentvalueObj.put("componentid", "Sensitive Field Type");
									    	  componentvalueObj.put("componentsArray", componentsArrayval);
									    	  componentvalueObj.put("overallStatus", sensitiveType);
									    	  
									    	  componentvalueArr.add(componentvalueObj);
									    	  
								    		  tabularDataObj.put("components",componentvalueArr);
								    		  tabularDataObj.put("pageTitle",tableName);
										      tabularDataObj.put("OverAll_Status","");
										      tabularDataObj.put("total_test_cases","");
										      tabular_data.add(tabularDataObj);
										     
									    	  }else {
									    		  //JSONObject tabularDataObj=new JSONObject();
									    		  
									    		  for(int i=0;i<=tabular_data.size()-1;i++) {
								    		    	  JSONObject tabObj = (JSONObject) tabular_data.get(i);
								    		    	  String table_Name=(String) tabObj.get("pageTitle");
								    		    	  if(table_Name.equalsIgnoreCase(tableName)) {
								    		    		  JSONArray componentvalueArr=(JSONArray) tabObj.get("components");
								    		    		  
								    		    		  boolean sensitiveTypeExists=checkSensitiveTypeExists(componentvalueArr,sensitiveType);
								    		    		  
								    		    		  if(!sensitiveTypeExists) {
								    		    			  sensitiveTypeCount++;
								    		    			  JSONObject componentvalueObj=new JSONObject();
									    		    		  componentvalueObj.put("componentid", "Sensitive Field Type");
									    		    		  sensitiveFieldCount++;
									    		    		  JSONArray componentsArrayval=new JSONArray();
									    		    		  JSONArray compArr=new JSONArray();
													    	  JSONObject coloumObj=new JSONObject();
													    	  coloumObj.put("Coloumn_Name",columnName);
													    	  compArr.add(coloumObj);
													    	  componentsArrayval.add(compArr);
									    		    		  
									    		    		  
													    	  componentvalueObj.put("componentsArray", componentsArrayval);
													    	  componentvalueObj.put("overallStatus", sensitiveType);
													    	  
													    	  componentvalueArr.add(componentvalueObj);
								    		    			  
								    		    		  }else{
								    		    			  
								    		    			  
								    		    			  
								    		    			  for(int j=0;j<=componentvalueArr.size()-1;j++) {
								    		    				  JSONObject compValObj = (JSONObject) componentvalueArr.get(j);
								    		    				  String sensitiveTyp=(String) compValObj.get("overallStatus");
								    		    				  if(sensitiveTyp.equalsIgnoreCase(sensitiveType)) {
								    		    					  JSONObject componentvalueObj=compValObj;
								    		    					  JSONArray componentsArrayval=(JSONArray) componentvalueObj.get("componentsArray");
								    		    					  sensitiveFieldCount++;
								    		    					  JSONArray compArr=new JSONArray();
															    	  JSONObject coloumObj=new JSONObject();
															    	  coloumObj.put("Coloumn_Name",columnName);
															    	  compArr.add(coloumObj);
															    	  componentsArrayval.add(compArr);
											    		    		  
											    		    		  
															    	  componentvalueObj.put("componentsArray", componentsArrayval);
															    	  componentvalueObj.put("overallStatus", sensitiveType);
								    		    					  
								    		    					  break;
								    		    				  }
								    		    				  
								    		    			  }
								    		    			  
								    		    			  
								    		    			  
								    		    			  
								    		    			  
								    		    		  }
								    		    		  
								    		    		  
												    	  //tabularDataObj.put("components",componentvalueArr);
								    		    		
								    		    		  
								    		    	  }
								    		    	  							    	  
								    		    	  
								    		      }
									    		  
									    			
									    		  
									    		  
									    	  }
										      
										      
						      }else {
						    	  JSONObject tabularDataObj=new JSONObject();
						    	  JSONArray componentvalueArr=new JSONArray();
						    	  JSONObject componentvalueObj=new JSONObject();
						    	  
						    	  JSONArray componentsArrayval=new JSONArray();
						    	  sensitiveFieldCount++;
						    	  JSONArray compArr=new JSONArray();
						    	  JSONObject coloumObj=new JSONObject();
						    	  coloumObj.put("Coloumn_Name",columnName);
						    	  compArr.add(coloumObj);
						    	  componentsArrayval.add(compArr);						    	  
						    	
						    	  sensitiveTypeCount++;
						    	  componentvalueObj.put("componentid", "Sensitive Field Type");
						    	  componentvalueObj.put("componentsArray", componentsArrayval);
						    	  componentvalueObj.put("overallStatus", sensitiveType);
						    	  
						    	  componentvalueArr.add(componentvalueObj);
						    	  
						    	  
						    	  
						    	  
						    	  tabularDataObj.put("components",componentvalueArr);
						    	  tabularDataObj.put("pageTitle",tableName);
							      tabularDataObj.put("OverAll_Status","");
							      tabularDataObj.put("total_test_cases","");
							      tabular_data.add(tabularDataObj);
						      }
		      
		      
		      
		  }
		
		
		JSONArray chart_labels=new JSONArray();
		JSONArray chart_values=new JSONArray();
		//chart_labels.add("Total tables scanned");
		chart_labels.add("Sensitive tables found");
		chart_labels.add("Sensitive fields found");
		
		//chart_values.add(totalScannedTables);
		chart_values.add(sensitiveTypeCount);
		chart_values.add(sensitiveFieldCount);
		
		
		
		
		
		
		
		
		
		toolReportObj.put("chart_name", "Summary");
		toolReportObj.put("chart_labels", chart_labels);
		toolReportObj.put("chart_values", chart_values);
		toolReportObj.put("tabular_data", tabular_data);
		toolReportObj.put("level", "levelthree");
		
		toolReport.add(toolReportObj);
		logger.info("Completed reading GDPR Report");
		return toolReport;
		 }catch(Exception e) {
			 logger.error("Exception in GDPR Report function");
			 logger.error("StringWriter");
			 
			 StringWriter stack = new StringWriter();
			 e.printStackTrace(new PrintWriter(stack));
			 logger.error("end of StringWriter");
			 
			 logger.error(e.getMessage());
			 e.printStackTrace();
			 throw e;
//			 return null;
		 }
	}
	
	boolean checkTableExists(JSONArray tabular_data,String tableName) throws Exception{
		// TODO Auto-generated method stub
		
		
		for(int i=0;i<=tabular_data.size()-1;i++) {
	    	  JSONObject tabObj = (JSONObject) tabular_data.get(i);
	    	  String table_Name=(String) tabObj.get("pageTitle");
	    	  if(table_Name.equalsIgnoreCase(tableName)) {
	    		 return true;
	    		
	    	  }
	    	  							    	  
	    	  
	      }
		return false;
	}

	boolean checkSensitiveTypeExists(JSONArray componentvalueArr,String sensitiveType) throws Exception{
		for(int j=0;j<=componentvalueArr.size()-1;j++) {
			  JSONObject compValObj = (JSONObject) componentvalueArr.get(j);
			  String sensitiveTyp=(String) compValObj.get("overallStatus");
			  if(sensitiveTyp.equalsIgnoreCase(sensitiveType)) {
				  
				  return true;
				  
			  }
			  
		  }
		
		return false;
	}
	
}
