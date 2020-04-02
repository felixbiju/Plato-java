package com.PLATO.services;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.daoimpl.MongodbDao;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.mongodbentities.Account;
import com.PLATO.mongodbentities.Person;
import com.PLATO.mongodbentities.Project;
import com.PLATO.userTO.ProjectTO;
import com.PLATO.userTO.UserTO;



@Path("sampleservice")
public class SampleService
{

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getRequest")
	public Person sampleGet()
	{
		Person person=new Person();
		person.setFirstName("Gaurav");
		person.setLastName("Kulkarni");
		System.out.println("Got get request");
		return  person;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getProjectIDS")
	public Response sampleServiceDemo()
	{
		
		List<ProjectTO> projectMasterList = new ArrayList<ProjectTO>();
		
		try {
			List<Object> projectList = GenericDaoSingleton.getGenericDao()
					.findAll(ProjectMaster.class);
			
			ProjectTO projectTO;
			
			ProjectMaster projectMaster=new ProjectMaster();
			
			for (int i = 0; i < projectList.size(); i++) {
				projectTO = new ProjectTO();
				projectMaster = (ProjectMaster) projectList.get(i);
				projectTO.setProject_id(projectMaster.getProject_id());
				projectTO.setAccount_id(projectMaster.getAccountMaster().getAccount_id());
				
				
				
				projectMasterList.add(projectTO);
			}

			
			
	
			System.out.println(projectList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error while fetching project IDS").build();
			// TODO Auto-generated catch block
			
		}
		
		//ConstantQueries.GET_ALL_PROJECT_IDS
		
		return Response.status(Response.Status.OK)
				.entity(projectMasterList).build();
		
		
	}
	
	
	

	@POST
	@Path("postRequest")
	/*@Consumes({ "application/json" })
	@Produces({ "application/json" })*/
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Person samplePost(Person person)
	{
		System.out.println("Got post request");
		System.out.println("The data received is name :"+person.getFirstName()+" surname :"+person.getLastName());
		Person person2=new Person();
		person2.setFirstName("Nilesh");
		person2.setLastName("Dandage");
		return person2;
	}
	
	
	@POST
	@Path("postAccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Project> accountPost(Account acc) throws UnknownHostException
	{
		System.out.println("Got post request");
		System.out.println("The data received is acc_id :"+acc.getAccount_id()+" acc_name :"+acc.getAccount_name());
		Person person2=new Person();
		person2.setFirstName("Nilesh");
		person2.setLastName("Dandage");
		MongodbDao dao=new MongodbDao();
		//dao.connectDB(acc);
		//Account ac1=dao.getAccountFromDb(2);
		Account ac1=dao.saveAccToDb(acc);
		Set<Project> p=ac1.getProjects();
		return p;
	}

	/*@POST
	@Path("postGeneric")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public List<Account> genericQuery(Account acc)
    {
		MongoDbGenericDAOImpl m=new MongoDbGenericDAOImpl();
		Object document=m.insertDocument(acc);
    	List<Account> acc2= (List<Account>) m.getAllDocuments(Account.class);
    	return acc2;
    }*/
	
	
	
	
	

}
