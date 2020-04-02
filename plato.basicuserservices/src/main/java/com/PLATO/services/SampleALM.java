package com.PLATO.services;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.PLATO.alm.Infrastucture.AlmConnector;
import com.PLATO.alm.Infrastucture.Base64Encoder;
import com.PLATO.alm.Infrastucture.Constants;
import com.PLATO.alm.Infrastucture.RestConnector;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Path("sampleALM")
public class SampleALM {
	RestConnector restConnector;
	@GET
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login() {
		AlmConnector alm = new AlmConnector();
		 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		try {
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			boolean logoutResponse=alm.logout();
			return Response.status(Response.Status.OK).entity(loginResponse+" "+logoutResponse).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		}

	}
	
	@GET
	@Path("read")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read() {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			String readUrl=restConnector.buildUrlForQC12("qcbin/rest/domains/DEFAULT/projects/TestProject/defects");
			System.out.println("readurl "+readUrl);
			com.PLATO.alm.Infrastucture.Response response=restConnector.httpGet(readUrl, null, null);
			alm.logout();
			System.out.println("read response code "+response.getStatusCode());
			return Response.status(Response.Status.OK).entity(response.getResponseData()).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();			
		}

		
	}
	
	@GET
	@Path("write")
	@Produces(MediaType.APPLICATION_JSON)
	public Response write() {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			String writeUrl=restConnector.buildUrlForQC12("qcbin/rest/domains/DEFAULT/projects/TestProject/defects/3");
			com.PLATO.alm.Infrastucture.Response readResponse=restConnector.httpGet(writeUrl, null, null);
			String xmlString=new String(readResponse.getResponseData());
			Map<String, String> map = new HashMap<String, String>();
			map.put("Accept", "application/xml");
			map.put("Content-Type","application/xml");
//			map.put("Slug", "myfilename.txt");
			System.out.println("writeurl "+writeUrl);
//			xmlString=Base64Encoder.encode(xmlString.getBytes());
			com.PLATO.alm.Infrastucture.Response writeResponse=restConnector.httpPut(writeUrl,xmlString.getBytes(), map);
			alm.logout();
			System.out.println("write response code "+writeResponse.getStatusCode());
			return Response.status(Response.Status.OK).entity(writeResponse.getResponseData()).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();					
		}
	} 
	
	

}
