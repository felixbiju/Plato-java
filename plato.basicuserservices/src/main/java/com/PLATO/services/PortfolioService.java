package com.PLATO.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.PortfolioMaster;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.UserProjectMapping;
import com.PLATO.userTO.PortfolioTo;

@Path(value = "Portfolio")
public class PortfolioService {
	
//	GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	
	@GET
	@Path("portfolioList/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	//get portfolio list
	public List<PortfolioTo> getPortfolioList(@PathParam("userid") String userid)
	{
		System.out.println("Getting Portfolio list for username  :"+userid);
		List<PortfolioTo> portfolioList = new ArrayList<PortfolioTo>();
		HashMap<String,Object> keyvalueMapUserid = new HashMap<String,Object>();
		HashSet<Object> accountId = new HashSet<Object>();

		try{
			keyvalueMapUserid.put("userid",userid);
			List<Object> list = GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_PORTFOLIO_DATA, keyvalueMapUserid);
			for(int i=0;i<list.size();i++)
			{
				PortfolioTo portfolioTo = new PortfolioTo();
				Object[] obj=(Object[]) list.get(i);
				portfolioTo.setPortfolio_id((int)obj[0]);
				portfolioTo.setPortfolio_name((String)obj[1]);
				portfolioList.add(portfolioTo);
			}
			
	       		
		}
		catch(Exception e){
			System.out.println("Error while fetching portfolio list");
			e.printStackTrace();
		}
		
		
		//
		try {
			
			//String s=PlatoJsonTemplate.createMongoTemplateFunction(3);
			//String jobName="job_1234";
			//int buildNumber=16;
		//	PlatoJsonTemplate.
		//	PlatoJsonTemplate pt=new PlatoJsonTemplate(buildNumber,jobName,87);
		//	String s=pt.createMongoTemplateFunction();
			//PlatoJsonTemplate.
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return portfolioList;
	}
	

	
	
	
	
	//updated account services
	
		@GET
		@Path("getPortfolioList")
		//@Consumes("text/plain")   
		@Produces(MediaType.APPLICATION_JSON)	
		public Response  getPortfolios() throws IOException{
			
			/*List<ProjectMaster>allocatedProjectsList=dashboardDao.getAllocatedProjectsForUser(user_id);
			
			List<AllocatedProjects>allocatedProjectNamesList=new ArrayList<AllocatedProjects>();

			AllocatedProjects  allocatedProjects;
			
			for(int i=0;i<allocatedProjectsList.size();i++){
				allocatedProjects=new AllocatedProjects();
				String projectName=allocatedProjectsList.get(i).getProject_name();
				allocatedProjects.setProjectName(projectName);
				allocatedProjectNamesList.add(allocatedProjects);
				projectName=null;
			}
			
			return  allocatedProjectNamesList;*/
			
			List<PortfolioTo>portfolioTOList=new ArrayList<PortfolioTo>();
			HashMap<String,Object> queryMap=new HashMap<String,Object >();
			try
			{	
			List<Object>portfolioList=GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_PORTFOLIO_LIST,queryMap);
			ProjectMaster prm;
			UserProjectMapping userProjectMapping;
			
			if(portfolioList==null){
				return Response.status(Response.Status.NOT_FOUND).entity(portfolioList).build();
			}
			
			
			for(int i=0;i<portfolioList.size();i++){
				PortfolioTo portfolioTO=new PortfolioTo();
				PortfolioMaster portMaster = (PortfolioMaster) portfolioList.get(i);
				String portfolio_name=portMaster.getPortfolio_name();
				int portfolio_id = portMaster.getPortfolio_id();
				portfolioTO.setPortfolio_name(portfolio_name);
				portfolioTO.setPortfolio_id(portfolio_id);
				portfolio_name=null;
				portfolioTOList.add(portfolioTO);
			}
			
			}catch(Exception e){
				portfolioTOList=null;
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(portfolioTOList).build();
			}
			
			return  Response.status(Response.Status.OK).entity(portfolioTOList).build();
			
		}
		
		
		
		
		@POST
		@Path(value="create")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response createPortfolio(PortfolioMaster portfolioMaster){
			

		/*JSONParser jsnParser=new JSONParser();
		jsonObj = (JSONObject) jsnParser.parse(createAccountString);
		String accountName=(String) jsonObj.get("accountName");
		String accountHead=(String) jsonObj.get("accountHead");
		String accountLogo=(String) jsonObj.get("accountLogo");
		String backgroundImage=(String) jsonObj.get("backgroundImage");
		String accountStatus=(String) jsonObj.get("accountStatus");
		String portFolioId=(String) jsonObj.get("portFolioId");*/
			
		try{	
		JSONObject jsonObj=new JSONObject();   
		HashMap<String,Object> queryMap=new HashMap<String,Object>();

		PortfolioMaster portfolioMst=(PortfolioMaster) GenericDaoSingleton.getGenericDao().createOrUpdate(portfolioMaster);
		
		//sending json object
		/*if(accountMst!=null){
			jsonObj.put("accountDeleteMessage", "Account created Successfully");
		}else{
			jsonObj.put("accountDeleteMessage", "Failed to create Account");
		}	*/

		if(portfolioMst!=null){
			return Response.status(Response.Status.CREATED)
	        .entity("Account created Successfully").build();		
		}else{
			return Response.status(Response.Status.NOT_FOUND)
			        .entity(portfolioMst).build();	
			
		}
		}catch(Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			        .entity("Failed to create Account").build();	
		}
		
		//return jsonObj;
	}	
	
	
	
		@PUT
		@Path(value = "update/{portfolioId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response updatePortfolio(PortfolioMaster portfolioMaster, @PathParam("portfolioId") int portfolioId) {
			
		
			try {
				HashMap<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("portfolioId", portfolioId);
				PortfolioMaster existingPortfolioMaster = (PortfolioMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(
								ConstantQueries.GET_PARTICULAR_PORTFOLIO, queryMap);
				
				if (existingPortfolioMaster == null) {
					
					//logger.error("User Does Not Exists");
					//System.out.println("User Does Not Exists");
					existingPortfolioMaster = null;
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Portfolio Does Not Exists").build();
				}
				
				existingPortfolioMaster.setPortfolio_id(portfolioMaster.getPortfolio_id());
				existingPortfolioMaster.setPortfolio_name(portfolioMaster.getPortfolio_name());
							
				PortfolioMaster portfolioMst=(PortfolioMaster) GenericDaoSingleton.getGenericDao().createOrUpdate(existingPortfolioMaster);
				
				if(portfolioMst!=null){
					return Response.status(Response.Status.OK)
			        .entity("Portfolio Updated Successfully").build();		
				}else{
					portfolioMst=null;
					return Response.status(Response.Status.NOT_FOUND)
					        .entity(portfolioMst).build();	
					
				}
				} catch (Exception e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					        .entity("Failed to Update Portfolio").build();	
				}

		}
		
	
		
		
		
		
		
		
	
	
	
	
		@DELETE
		@Path(value="delete/{portfolioId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response deletePortfolio(@PathParam("portfolioId")int portfolioId){
			
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		//	keyvalueMap.put("portfolioName", portfolioName);
			try
			{
		//	PortfolioMaster portfolioMst=(PortfolioMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.FETCH_REQ_PORTFOLIO_TO_DELETE, keyvalueMap);
		
				GenericDaoSingleton.getGenericDao().delete(PortfolioMaster.class, portfolioId);
			return Response.status(Response.Status.OK)
		        .entity("Portfolio deleted Successfully").build();		
			
			}catch(Exception e){
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				        .entity("Failed to delete Portfolio").build();	
			}
			
		}
	
	
	
	
	
	
	
	
	
	
	
}
