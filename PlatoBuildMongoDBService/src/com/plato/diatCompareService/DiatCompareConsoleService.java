package com.plato.diatCompareService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lntinfotech.tcoe.excelImport.ExcelToJson;
import com.mongo.utilities.ReadCompareDiatConsole;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Path(value="DiatCompareConsoleService")
public class DiatCompareConsoleService {
	
	@GET
	@Path("returnDiatJson/{jobName}/{buildNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response returnDiatJson(@PathParam("jobName") String jobName,@PathParam("buildNumber") int buildNumber,@QueryParam("reportPath") String reportPath) {
		System.out.println("inside returnDiatJson");
		boolean status=true;
		ReadCompareDiatConsole readJenkinsConsole=new ReadCompareDiatConsole();
		ExcelToJson excelToJson=new ExcelToJson();
		JSONObject jsonDiatReport=new JSONObject();
		JSONParser parser = new JSONParser();
		String jenkinsConsoleOutput="";
		try {
			for(;;) {
				Thread.sleep(2000);
				jenkinsConsoleOutput=readJenkinsConsole.readJenkinsConsole(buildNumber,jobName,"");
				if(jenkinsConsoleOutput.contains("Finished: SUCCESS")||jenkinsConsoleOutput.contains("Finished: FAILURE") || jenkinsConsoleOutput.contains("Finished: ABORTED") || jenkinsConsoleOutput.contains("Finished: UNSTABLE") || jenkinsConsoleOutput.contains("FATAL: null")) {
					System.out.println("jenkins console output contains******** DIAT Execution Completed ");
					String reportString=excelToJson.getImpactData(reportPath);
					jsonDiatReport=(JSONObject) parser.parse(reportString);
					break;
				}/*else if(jenkinsConsoleOutput.contains("Finished: FAILURE") || jenkinsConsoleOutput.contains("Finished: ABORTED") || jenkinsConsoleOutput.contains("Finished: UNSTABLE") || jenkinsConsoleOutput.contains("FATAL: null")) {
					status=false;
					break;
				}*/			
			}

			
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(status==true) {
			return Response.status(Response.Status.OK).entity(jsonDiatReport).build();
		}else {
			return Response.status(Response.Status.NOT_FOUND).entity(jsonDiatReport).build();
		}
		
	}
	
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response printHello() {
		System.out.println("hello world");
		return Response.status(Response.Status.OK).build();
	}
}
