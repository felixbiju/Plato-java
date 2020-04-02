package com.adapters;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class JiraAdapter {

	 private Logger logger = Logger.getLogger(JiraAdapter.class);
	 public void test(String jiraUrl) throws Exception{
		 String url=jiraUrl+"/rest/api/2/search";
		 JSONObject query=new JSONObject();
	 }
//	 public String indexJira(int id,String jiraurl,String username,String password,String indexDirectoryPath,String isSync,String syncTimeStamp,String classifier,String projectList,String labelList,String issueType)
//	 {
//		 logger.info("inside indexJira");
//		 String auth =new String(Base64.encode(username+":"+password));
//	     String res="";
//		 IndexWriter writer=null;
//		 Date latestdt=new Date();
//		 boolean isSyncFlag=false;
//		 
//		 
//		try 
//		{	
//			  SimpleDateFormat sdfall = new SimpleDateFormat("yyyy-MM-dd");
//			  Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
//
//		      Client client =Client.create();
//			  ClientResponse response=null,resTotalDefect=null;
//				    
//			  TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
//				{
//				    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//				        return null;
//				    }
//					public void checkClientTrusted(X509Certificate[] arg0, String arg1)
//							throws CertificateException {
//						// TODO Auto-generated method stub
//						
//					}
//				
//					public void checkServerTrusted(X509Certificate[] arg0, String arg1)
//							throws CertificateException {
//						// TODO Auto-generated method stub
//						
//					}
//				}};
//			  
//			  try{
//					// Install the all-trusting trust manager
//					SSLContext sc = SSLContext.getInstance("SSL");
//					sc.init(null, trustAllCerts, new java.security.SecureRandom());
//					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//					
//					// Create all-trusting host name verifier
//					HostnameVerifier allHostsValid = new HostnameVerifier()
//					{
//					  	public boolean verify(String arg0, SSLSession arg1) 
//						{
//							// TODO Auto-generated method stub
//							return false;
//						}
//					};
//				}
//				catch(Exception e){}
//			  
//			  int total=0;
//			  String issue[]=issueType.split(",");
//			  String issuetypefinal= new String();
//			  for(int i=0;i<issue.length;i++)
//				{
//				  issuetypefinal=issuetypefinal+"\""+issue[i]+"\""+",";
//					System.out.println("inside for loop edit fir subproject"+issuetypefinal);
//				}
//				
//				//prjName="'"+prjName+"'";
//				
//			  issuetypefinal=issuetypefinal.substring(0, issuetypefinal.length()-1);
//			  logger.info("issuetypfinal>>>>>>>>>>>>"+issuetypefinal);
//			  WebResource webResource = client.resource(jiraurl+"/rest/api/2/search");
//			System.out.println("issuetype>>>>>>>>>>>"+issueType);
//			  int flag=0;
//			  //check that is it already sync? 
//				 if(isSync.equalsIgnoreCase("True"))
//				 {
//					// finding out total defects that are available 
////				    resTotalDefect = webResource.queryParam("jql","issuetype%20%3D%20Bug%20AND%20created%20>%3D%20"+syncTimeStamp).queryParam("maxResults","0").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//					 logger.info("Labels : "+labelList);
//					 if(labelList=="" || labelList==null||labelList.equals("")) {
//						resTotalDefect = webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND project in("+projectList +")").queryParam("maxResults","0").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//					 
//						  logger.info("sync true: no label JQL--> "+"issuetype in ("+issueType+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND project in("+projectList +")");
//
//					 }else {
//						resTotalDefect = webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND (project in("+projectList +") OR labels in ("+labelList+"))").queryParam("maxResults","0").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//					 
//						  logger.info("sync true: with labels JQL--->"+"issuetype in ("+issueType+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND (project in("+projectList +") OR labels in ("+labelList+"))");
//
//					 }
//					 
//
//					String dataTotalDefect = resTotalDefect.getEntity(String.class);
//					JSONObject jsonTotalDefect = new JSONObject(dataTotalDefect);
//				    total=Integer.parseInt(jsonTotalDefect.get("total").toString());
//				    System.out.println("resTotalDefect:"+resTotalDefect);
//				    logger.info("jsonTotalDefect:"+jsonTotalDefect);
//					
//				    isSyncFlag=true;
//				   /* String OLD_FORMAT =determineDateFormat(syncTimeStamp);
//					SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
//					latestdt = sdf.parse(syncTimeStamp);*/
//					
//					// open writer in appending order
//					writer = new IndexWriter(indexDirectory,new StandardAnalyzer(Version.LUCENE_36),false,IndexWriter.MaxFieldLength.UNLIMITED);
//					flag=1;
//				 }
//				 else	 
//				{
//					// System.out.println(projectList);
//					// response= webResource.queryParam("jql","issuetype%20in%20(standardIssueTypes()%2C%20subTaskIssueTypes())").queryParam("startAt","0").queryParam("maxResults","10000").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//				   
//					// finding out total defects that are available 
////				    resTotalDefect = webResource.queryParam("jql","issuetype%20%3D%20Bug").queryParam("maxResults","0").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//					/* if(projectList) {}*/
//					 if(labelList=="" || labelList==null||labelList.equals(""))
//						 resTotalDefect = webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND project in ( "+projectList +") ").queryParam("maxResults","0").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//					else
//					     resTotalDefect = webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND ( project in ( "+projectList +" ) OR labels in ( "+labelList+") )").queryParam("maxResults","0").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//				  logger.info("sync False: JQL is ----->"+"issuetype in ("+issueType+") AND (project in("+projectList +") OR labels in ("+labelList+"))");
//					 String dataTotalDefect = resTotalDefect.getEntity(String.class);
//					logger.info("String Response from URL: "+dataTotalDefect);
//					JSONObject jsonTotalDefect = new JSONObject(dataTotalDefect);
//				
//				    total=Integer.parseInt(jsonTotalDefect.get("total").toString());
//					logger.info("Jira JSON DATA: jsonTotalDefect:"+jsonTotalDefect);
//				
//					//open writer by flushing everything
//					writer = new IndexWriter(indexDirectory,new StandardAnalyzer(Version.LUCENE_36),true,IndexWriter.MaxFieldLength.UNLIMITED);
//				}
//
//				 //Calculate slices 
//				 int slice=0;
//				 if(total>500)
//				    slice=total/500;
//				 logger.info("Total defects:  "+total);
//				 logger.info("No of Slices starting from 0: "+slice);
//				 // Fetching 2000 defect at a time and dumping them
//				 for(int k=0;k<=slice;k++)
//				 {	 
//				 
//				 try {
//					 if(flag==1)
//					 {	 
//						 if(labelList=="" || labelList==null||labelList.equals("")) {
//							response= webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND project in("+projectList +")").queryParam("startAt",""+(k*500)).queryParam("maxResults","500").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class); 
//							logger.info("sync False");
//							logger.info("No label query");
//							logger.info("jql for fetching defects --->"+"issuetype in ("+issueType+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND project in("+projectList +")");
//							logger.info("startAt"+(k*500));
//						 }else {
//							 logger.info("With label query");
//							response= webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND (project in("+projectList +") OR labels in ("+labelList+"))").queryParam("startAt",""+(k*500)).queryParam("maxResults","500").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class); 
//							logger.info("jql is---->"+"issuetype in ("+issueType+") AND ( created >"+syncTimeStamp+ " OR updated >"+syncTimeStamp+ ") AND (project in("+projectList +") OR labels in ("+labelList+"))");
//							logger.info("startAt"+(k*500));
//						 }
//					 }else
//						{
//						 if(labelList=="" || labelList==null||labelList.equals("")) {
//							  response= webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND project in("+projectList +") ").queryParam("startAt",""+(k*500)).queryParam("maxResults","500").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//							  logger.info("sync False");
//								logger.info("No label query");
//							  
//							  logger.info("jql-->"+"issuetype in ("+issueType+") AND project in("+projectList +") ");
//							  logger.info("startAt"+(k*500));
//							  
//						 }else {	
//							 logger.info("With label query");
//							 response= webResource.queryParam("jql","issuetype in ("+issuetypefinal+") AND (project in("+projectList +") OR labels in ("+labelList+"))").queryParam("startAt",""+(k*500)).queryParam("maxResults","500").header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
//						logger.info("jql--->"+"issuetype in ("+issueType+") AND (project in("+projectList +") OR labels in ("+labelList+"))");
//						logger.info("startAt"+(k*500));
//						 }
//						}
//				 
//					String data="";
//					JSONObject jsonResponse;
//					
//					try {
//						data = response.getEntity(String.class);
//						jsonResponse = new JSONObject(data);
//					} catch (Exception e1) {
//						// TODO Auto-generated catch block
//						logger.error("exception in String entity",e1);
//						e1.printStackTrace();
//						res="Error : Unble to Connect to Jira!";
//						return res;
//					}
//					logger.info("JIRA response Slice "+k+":"+jsonResponse);
//										  
//					JSONArray contents=jsonResponse.getJSONArray("issues");
//				
//					logger.info("Jira id :: "+id);
//					HashMap<Integer, ArrayList<String>> configdatamap = jiraService.loadJIRAVariables(id);
//					
//					 File rootdir=new File(indexDirectoryPath);
//					 HashMap<String, String> s = ExcelUtils.INSTANCE.loadModelFiles(classifier);
//				
//					for(int i=0;i<contents.length();i++)
//					{
//						JSONObject jb=contents.getJSONObject(i);
//						Document d = new Document();
//						 
//							
//						Iterator it = configdatamap.entrySet().iterator();
//						try {
//							while (it.hasNext())
//							{
//								
//								Map.Entry pair = (Map.Entry) it.next();
//								ArrayList<String> qcdata = (ArrayList<String>) pair.getValue();
//								String field = qcdata.get(1);
//								logger.info("Initializing var >>> "+field);
//								JSONObject fields=(JSONObject) jb.get("fields");
//								switch(field)
//								{
//								   case "detectiondate" :{ 
//									   					 String datenew="",fielddata="";
//														  try {
//																
//																
//																if(qcdata.get(2).contains("custom") && !fields.getString(qcdata.get(2)).equalsIgnoreCase("null"))
//																{ 
//																	
//																	JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																	 fielddata=(String) customdata.get("value").toString().substring(0, 10);
//																	 logger.info("custom value : "+fielddata);
//																}
//																else
//																{
//																	 fielddata=(String) fields.optString("created").toString().substring(0, 10);
//																	 logger.info("created value:  "+fielddata);
//																}		
//																	
//																 String OLD_FORMAT =determineDateFormat(fielddata);
//																			final String NEW_FORMAT = "yyyy-MM-dd";
//									
//																SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
//																Date dt = sdf.parse(fielddata);
//																sdf.applyPattern(NEW_FORMAT);
//																datenew = sdf.format(dt);
//																
//																 /*if(!isSyncFlag)
//																   latestdt=dt;
//																 
//																 isSyncFlag=true;
//																 if (dt.compareTo(latestdt) > 0) 
//																	 latestdt=dt;*/
//																 
//															} catch (Exception e) {
//																logger.error("Detection Date Exceptionnnn  ",e);
//																// TODO Auto-generated catch block
//																e.printStackTrace();
//															}
//														  
//														  logger.info("formatted detection date "+datenew);
//														
//														  d.add(new Field(field,datenew,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//								   						}break;
//									                       
//								   case "Status" :  
//									   String statusname="";
//								   try{
//									   
//								  	
//									   					   
//														   if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)) )
//															{
//																
//																JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																statusname=customdata.optString("value");
//																  logger.info("custom status "+statusname);
//															}
//														   else
//														   {  
//										   					if(fields.has("status")) {
//									   							JSONObject statusObj=(JSONObject) fields.get("status");
//									   							statusname=statusObj.optString("name");
//									   						  logger.info("status value : "+statusname);
//										                    } 
//														   }
//									   						 
//														     }
//								   catch(Exception e){
//									   logger.error("Status Exceptionnnn ",e);
//									   e.printStackTrace();
//									   
//								   }
//								    d.add(new Field(field,statusname,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//							       						   break;	                       
//								   case "Severity" :    String severityname="";
//								   try{
//									  
//								                          
//								                        	  if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)))
//																{
//								                        		   
//																	JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																	severityname=customdata.optString("value");
//																	 logger.info("custom value : "+severityname);
//																}
//								                        	  else
//								                        	  {
//								                        		if(fields.has("priority")) {
//																	JSONObject priority=(JSONObject) fields.get("priority");
//																	severityname=(String) priority.optString("name");
//																	 logger.info("severity is same as priority value : "+severityname);
//								                        	  }
//								                        	  }
//								                          }
//								   catch(Exception e){
//									   logger.error("Severity Exceptionnnn ",e);
//									   e.printStackTrace();
//								   }
//									                  	    d.add(new Field(field,severityname,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//								   						 
//							       						   break;
//								 
//								   case "Id" :             
//									   
//									   						String jiraid="";
//									   						
//									   						try{
//					                  	                    if(qcdata.get(2).contains("custom"))
//																{
//					                  	                    
//																JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																jiraid=customdata.optString("value");
//																logger.info("custom value : "+jiraid);
//																}
//						                  	                    else
//						                  	                     {
//						                  	                    	jiraid=jb.get("key").toString();
//						                  	                  	logger.info("ID value is Key : "+jiraid);
//						                  	                     }
//					                  	                    Term term = new Term("Id", jiraid);
//					                  	                    writer.deleteDocuments(term);
//									   						}
//									   						catch(Exception e){
//									   							logger.error(" Id Exceptionnnn ",e);
//									   							e.printStackTrace();
//									   							}
//									   						
//					                  	                    d.add(new Field(field,jiraid,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//									   						 
//								   						  break;
//								   case "DetectedBy" :    
//														   
//															    String name="";
//															    
//															    try{
//															    	
//						                  	                    if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)))
//																	{
//							                  	                    	
//																		JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																		name=customdata.optString("value");
//																		logger.info("custom value is: "+name);
//																	}
//							                  	                    else
//							                  	                     {
//																		  if(fields.has("reporter")) {
//																		  JSONObject reporter=(JSONObject) fields.get("reporter");
//																		  name= reporter.optString("displayName");
//																			logger.info("detected by is reporter>name : "+name);
//																		  }
//																	 }}
//															    catch(Exception e){
//															    	logger.error("DetectedBy Exceptionnnn ",e);
//															    	e.printStackTrace();
//															    	
//															    }
//															    
//						                  	            	  d.add(new Field(field,name,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//											   				 
//									   
//								   						  break;
//								   case "Priority" : 	   
//									   							String priorityname="";
//									   						try{
//									   							
//						             	                    if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)))
//																{
//						                 	                    	
//																	JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																	priorityname=customdata.optString("value");
//																	logger.info("custom value priority  "+priorityname);
//																}
//						                 	                    else
//						                 	                     { 
//						                 	                    	if(fields.has("priority")) {
//																	
//																	JSONObject priority=(JSONObject) fields.get("priority");
//																	 priorityname= priority.optString("name");	
//																		logger.info("priosity value "+priorityname);
//						                 	                     }
//						                 	                     
//						                 	                     }
//									   						}
//										                  	 catch(Exception e){
//										                  		logger.error(" Priority Exceptionnnn ",e);
//										                  		 e.printStackTrace();
//										                  		 }   
//										                  	 
//									   						 
//								   d.add(new Field(field,priorityname,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//								       						   break;
//								   case "Summary" : 	 {
//									   						String summary="";
//									   						try{
//									   							;
//									   							if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)))
//																{
//							            	                    	
//																	JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																	summary=customdata.optString("value");
//																	logger.info("custom value summary  "+summary);
//																}
//							            	                    else
//							            	                     {
//																	if(fields.has("summary")) {
//											                         summary=fields.optString("summary");
//											                         logger.info("summary "+summary);
//																	}}}
//									   						catch(Exception e){
//									   							logger.error(" Summary Exceptionnnn ",e);
//									   							e.printStackTrace();
//									   						}
//									   						    d.add(new Field(field,summary,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//								   							
//									   							String clasumm = ExcelUtils.INSTANCE.fetchCategoryFromModel(s,summary,"0");
//																d.add(new Field("classificationsumm",clasumm,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));	
//															
//								                            }
//								   						break;
//								   case "Description" :   
//									   					{
//									   						String desc="";
//									   						try{
//									   							
//								   							if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)))
//															{
//						            	                    	
//																JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																desc=customdata.optString("value");
//																logger.info("custom value desc  "+desc);
//															}
//						            	                    else
//						            	                     {
//										   						if(fields.has("description"))
//										   					     desc= fields.optString("description");
//										   						logger.info("desc  "+desc);
//									   		      	         }}
//									   						catch(Exception e){
//									   							logger.error("Description Exceptionnnn ",e);
//									   							e.printStackTrace();
//									   						}
//								   						   d.add(new Field(field,desc,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//											   			
//								   						String cladesc = ExcelUtils.INSTANCE.fetchCategoryFromModel(s,desc,"0");
//														d.add(new Field("classificationdesc",cladesc,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));	
//														logger.info("cladesc  "+cladesc);
//									   					}
//								   						  break;
//								   case "ResponsibleTeam":
//														   {
//															    String respteam="";
//															    try{
//															    	
//									   							if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2))) 
//																{
//							            	                    	
//							            	                    	if(fields.has(qcdata.get(2))) {
//																	JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																	respteam=customdata.optString("value");
//																	logger.info("custom respteam  "+respteam);
//																}
//																}
//							            	                    else
//							            	                     { 
//							            	                    	JSONObject resol;
//																   
//																    if(fields.has("assignee") && !(fields.getString("assignee").equalsIgnoreCase("null"))) {
//																    	 resol=(JSONObject) fields.get("assignee");
//																    	 if(resol.has("displayName")) {
//																    	 respteam=resol.optString("displayName");
//																    		logger.info("respteam  displayName  "+respteam);
//																    	 }
//																    	 else if(resol.has("name")) {
//																    		 respteam=resol.optString("name");
//																    		 logger.info("respteam  name  "+respteam);
//																    	 }
//																    }
//									   							  
//																		
//																
//										   		      	         }
//									   							}
//															    catch(Exception e){
//															    	logger.error("ResponsibleTeam Exceptionnnn ",e);
//															    	e.printStackTrace();
//															    	
//															    }
//															    
//															 	 if(respteam.equals("")||respteam=="")
//															 		respteam="UNASSIGNED";
//															 	logger.info("final respteam is    "+respteam);
//									   						   d.add(new Field(field,respteam,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//								            	                
//										   					}
//								   						  break;
//								   case "FunctionalArea" :
//															   {
//																   String funarea="";
//																   try{
//										   							if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)))
//																	{
//								            	                    	
//																		JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																		funarea=customdata.optString("value");
//																		logger.info("custom functionalarea    "+funarea);
//																	}
//								            	                    else
//								            	                     {
//																	  if(fields.has("project")) {
//										   							   JSONObject resol=(JSONObject) fields.get("project");
//										   							    funarea=resol.optString("name");
//										   							 logger.info(" functionalarea    "+funarea);
//																	  }
//								            	                     }}
//																   
//																   catch(Exception e){
//																	   logger.error("FunctionalArea Exceptionnnn ",e);
//																	   e.printStackTrace();
//																   }
//											   		      	       d.add(new Field(field,funarea,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//												   				
//											   					}
//								   						  break;
//								   case "closingdate" :	  { 
//											   					 String datenew="",fielddata="";
//											   					
//																  try {
//																		
//																		
//																		if(qcdata.get(2).contains("custom"))
//																		{ 
//																			JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																			 fielddata=(String) customdata.get("value").toString().substring(0, 10);
//																			 logger.info("custom closingdate    "+fielddata);
//																		}
//																		else
//																		{
//																			 fielddata=(String) fields.get("updated").toString().substring(0, 10);
//																			 logger.info(" closingdate    "+fielddata);
//																		
//																		}		
//																			
//																		 String OLD_FORMAT =determineDateFormat(fielddata);
//																				final String NEW_FORMAT = "yyyy-MM-dd";
//											
//																		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
//																		Date dt = sdf.parse(fielddata);
//																		sdf.applyPattern(NEW_FORMAT);
//																		datenew = sdf.format(dt);
//																		
//																	} catch (Exception e) {
//																		logger.error("closingdate Exceptionnnn ",e);
//																		// TODO Auto-generated catch block
//																		e.printStackTrace();
//																	}
//																	 logger.info("formatted closingdate    "+datenew);
//
//																  d.add(new Field(field,datenew,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//										   						}
//								   							break;
//								   case "resolution" :  	
//									   String statusname1="";
//									   						try {
//									   							
//												   					 
//																	   if(qcdata.get(2).contains("custom") && fields.has(qcdata.get(2)) && !fields.isNull(qcdata.get(2)))
//																		{
//																			
//																			JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																			statusname1=customdata.optString("value");
//																			 logger.info("custom resolution    "+statusname1);
//																		}
//																	   else
//																	   {  
//													   					
//													   						if(fields.has("resolution") && !fields.isNull("resolution")) {
//													   							
//												   							JSONObject resol=(JSONObject) fields.get("resolution");
//												   							statusname1=resol.optString("name");
//												   							logger.info(" resolution    "+statusname1);
//													                    } 
//																	   }
//																} catch (Exception e1) {
//																	logger.error(" resolution Exceptionnnn ",e1);
//																	// TODO Auto-generated catch block
//																	e1.printStackTrace();
//																}  
//													  	    d.add(new Field(field,statusname1,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//
//		       						    					break;
//		       						    					
//								   case "defectrootcause" :   
//									   String statusname2="";
//									   try {
//											
//												   					  
//																	   if(qcdata.get(2).contains("custom"))
//																		{
//																		
//																			if(fields.has(qcdata.get(2)) && !fields.isNull(qcdata.get(2))) {
//																			JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//																			statusname2=customdata.optString("value");
//																			logger.info("custom resolution    "+statusname2);
//																			}
//																		}
//																		logger.info("no defect root cause "+statusname2);
//
//																} catch (Exception e) {
//																	logger.error("defectrootcause Exceptionnnn ",e);
//																	// TODO Auto-generated catch block
//																	e.printStackTrace();
//																}  
//									   d.add(new Field(field,statusname2,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//
//				    										break;
//				    										
//								    	
//								}
//							}
//							
//							 
//					      	String projectname=""; JSONObject fields=null;
//					      	try{
//					         fields=(JSONObject) jb.get("fields");
//							JSONObject customdata=(JSONObject) fields.get("project");
//							projectname=customdata.optString("key").trim();
//					
//					      	}
//					      	catch(Exception e){
//					      		logger.error(" Project Exceptionnnn ",e);
//					      		e.printStackTrace();
//					      	}
//		                    d.add(new Field("project",projectname,Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//		                 logger.info(" Project: "+projectname+"  <<<--------------------------");
//		                    
//		                    String labelname="";
//					        //JSONObject fields=(JSONObject) jb.get("fields");
//		                    try{
//							JSONArray labelsArray=(JSONArray) fields.get("labels");
//							if(labelsArray==null || labelsArray.length()==0) {
//								
//								d.add(new Field("label","",Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//							}else {
//								labelname=labelsArray.get(0).toString();
//			                    d.add(new Field("label",labelname.toUpperCase(),Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES));
//					     	}
//							  logger.info(" Label: "+labelname+"ooooooooooo");
//							String defectage="";
//					        //JSONObject fields=(JSONObject) jb.get("fields");
//							
//						
//		                    }
//		                    catch(Exception e)
//		                    {
//		                    	e.printStackTrace();
//		                    }
//						/*	if(qcdata.get(2).contains("custom"))
//							{ 
//								JSONObject customdata=(JSONObject) fields.get(qcdata.get(2));
//								 fielddata=(String) customdata.get("value").toString().substring(0, 10);
//							}
//							else
//							{
//								 fielddata=(String) fields.get("created").toString().substring(0, 10);
//							}		
//								
//							 String OLD_FORMAT =determineDateFormat(fielddata);
//										final String NEW_FORMAT = "yyyy-MM-dd";
//
//							SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
//							Date dt = sdf.parse(fielddata);
//							sdf.applyPattern(NEW_FORMAT);
//							datenew = sdf.format(dt);
//							*/
//		                    try{
//							String createddate=(String) fields.get("created").toString().substring(0, 10);
//		                    }catch(Exception e)
//		                    {
//		                    	e.printStackTrace();
//		                    }
//							
//		                    try{
//							JSONObject resol=(JSONObject) fields.get("status");
//							String status=resol.getString("name");
//		                    }
//		                    catch(Exception e)
//		                    {
//		                    	e.printStackTrace();
//		                    }
//							 
//							 
//						} catch (Exception e) {
//							logger.error("Exceptionnnn ",e);
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//						   writer.addDocument(d);
//				
//				 }
//					
//}catch(Exception e) {
//					 logger.error("Error in slice "+k,e);
//				 }
//			   }
//			   writer.close();
//			
//			   logger.info("Formatting Date string: "+sdfall.format(latestdt));
//			   Calendar c = Calendar.getInstance();
//		       c.setTime(latestdt);
//		       c.add(Calendar.DATE, -1);
//		       latestdt = c.getTime();
//
//			  jiraService.updateIsSync(id,sdfall.format(latestdt));
//		}
//		catch(Exception e)
//		{	logger.error("Exceptionnnn ",e);
//			e.printStackTrace();
//			if(logger.isDebugEnabled()){
//			    logger.debug("This is debug");
//			}			
//			//e.printStackTrace();
//			logger.error("Failed while Indexing Jira",e);
//			//e.printStackTrace();			
//			
//			try {
//				writer.close();
//			} catch (CorruptIndexException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//				
//		}
//		res="SUCCESS";
//		return res;
//		
//	}
}
