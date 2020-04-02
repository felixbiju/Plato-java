package com.adapters;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongo.utilities.Base64Encoder;


/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class JiraAdapterNew {
	 private Logger logger = Logger.getLogger(JiraAdapterNew.class);
	 public void test(String jiraUrl,String issueType,String projectName,String username,String password) throws MalformedURLException,IOException, ParseException,Exception{
		 
		 byte[] credBytes = (username + ":" + password).getBytes();
		 String credEncodedString=Base64Encoder.encode(credBytes);
		 String urlString=jiraUrl+"/rest/api/2/search";
		 String jqlQueryString="";
		 int maxResults=100;
		 int startAt=0;
		 
		 JSONArray resultArray=new JSONArray();
		 JSONParser parser=new JSONParser();
		 JSONObject resultObj=new JSONObject();
		 
		 String result=""; 

		 JSONObject jqlQuery=createJqlForProject(projectName,issueType,startAt,maxResults);
		 result=postQuery(credEncodedString,jqlQuery,urlString);
		 resultObj=(JSONObject)parser.parse(result);
		 resultArray.add(resultObj);
		 int totalResults=(int)resultObj.get("total");
		 if(totalResults>maxResults) {
			 for(int i=maxResults;i<totalResults;i=i+maxResults) {
			 	 jqlQuery=createJqlForProject(projectName,issueType,i,maxResults);
				 result=postQuery(credEncodedString,jqlQuery,urlString);
				 resultObj=(JSONObject)parser.parse(result);
				 resultArray.add(resultObj);
			 }
		 }
	 }
	 
	 public String postQuery(String credEncodedString,JSONObject jqlQuery,String urlString)throws MalformedURLException,IOException,Exception{
		 URL url = new URL(urlString);
		 HttpURLConnection con = (HttpURLConnection) url.openConnection();
		 con.setDoOutput(true);
		 con.setDoInput(true);
		 con.setRequestProperty("Content-Type", "text/json");
		 con.setRequestProperty("Accept", "text/json");
		 con.setRequestProperty("Authorization","Basic "+credEncodedString);
		 con.setRequestMethod("POST");
			
		 OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		 wr.write(jqlQuery.toJSONString());
		 wr.flush();
		 wr.close();
			
		 InputStreamReader reader = new InputStreamReader( con.getInputStream() );
		 StringBuilder buf = new StringBuilder();
		 char[] cbuf = new char[ 2048 ];
		 int num;
		 while ( -1 != (num=reader.read( cbuf )))
		 {
			 buf.append( cbuf, 0, num );
		 }
		 String result= buf.toString();
		 return result;
	 }
	 
	 public JSONObject createJqlForProject(String projectName,int startAt,int maxResult) {
		 JSONObject jqlQuery=new JSONObject();
		 jqlQuery.put("jql", "project="+projectName);
		 jqlQuery.put("startAt", startAt);
		 jqlQuery.put("maxResults", maxResult);
		 return jqlQuery;
	 }
	 
	 public JSONObject createJqlForProject(String projectName,String issueType,int startAt,int maxResult)throws Exception {
		 JSONObject jqlQuery=new JSONObject();
		 jqlQuery.put("jql", "project="+projectName+" AND issuetype="+issueType);
		 jqlQuery.put("startAt", startAt);
		 jqlQuery.put("maxResults", maxResult);
		 return jqlQuery;
	 }
	 
}
