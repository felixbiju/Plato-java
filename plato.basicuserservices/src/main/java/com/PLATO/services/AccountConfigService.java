package com.PLATO.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

//import org.eclipse.persistence.tools.file.FileUtil;
import org.json.simple.JSONObject;
import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.AccountMaster;
import com.PLATO.entities.PortfolioMaster;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.UserProjectMapping;
import com.PLATO.userTO.AccountTO;

@Path("AccountConfigService")
public class AccountConfigService {

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllAccounts() throws IOException {

		List<AccountTO> accountMasterList = new ArrayList<AccountTO>();
		AccountMaster accountMaster;
		AccountTO accountTO = null;

		try {
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> accountList = GenericDaoSingleton.getGenericDao().findByQuery(ConstantQueries.GET_ALL_ACCOUNTS,
					queryMap);

			if (accountList == null) {
				return Response.status(Response.Status.NOT_FOUND).entity(accountMasterList).build();
			}

			for (int i = 0; i < accountList.size(); i++) {
				accountTO = new AccountTO();
				accountMaster = (AccountMaster) accountList.get(i);
				accountTO.setAccountId(accountMaster.getAccount_id());
				accountTO.setAccountName(accountMaster.getAccount_name());
				accountTO.setAccountHead(accountMaster.getAccount_head());

				/*
				 * if(accountMaster.getAccount_logo()!=null){ file1 = new
				 * File(classLoader.getResource(accountMaster.getAccount_logo())
				 * .getFile()); String
				 * image[]=file1.toString().split("plato.basicuserservices");
				 * 
				 * imageURL=ipAddress+"\\"+"plato.basicuserservices"+image[1];
				 * 
				 * accountTO.setAccountLogo(imageURL); imageURL="";
				 * 
				 * } if(accountMaster.getBackground_image()!=null){ file2 = new
				 * File(classLoader.getResource(accountMaster.
				 * getBackground_image()).getFile());
				 * 
				 * String
				 * image[]=file2.toString().split("plato.basicuserservices");
				 * 
				 * imageURL=ipAddress+"\\"+"plato.basicuserservices"+image[1];
				 * 
				 * accountTO.setBackgroundImage(imageURL); }
				 */
				accountTO.setAccountLogo(accountMaster.getAccount_logo());

				accountTO.setBackgroundImage(accountMaster.getBackground_image());

				// accountTO.setAccountStatus(accountMaster.getAccount_status());

				accountMasterList.add(accountTO);
			}

		} catch (Exception e) {
			accountMasterList = null;
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(accountMasterList).build();
		}
		return Response.status(Response.Status.OK).entity(accountMasterList).build();
	}

	/*
	 * @PUT
	 * 
	 * @Path(value="delete/{accountName}")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * deleteAccount(@PathParam ("accountName") String accountName){ JSONObject
	 * jsonObj=new JSONObject(); HashMap<String,Object> queryMap=new
	 * HashMap<String,Object >(); queryMap.put("accountName", accountName);
	 * AccountMaster accMaster;
	 * System.out.println("inside update deleteAccount");
	 * 
	 * try { AccountMaster accountMaster=(AccountMaster)
	 * GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.
	 * GET_PARTICULAR_ACCOUNT, queryMap);
	 * 
	 * 
	 * 
	 * accountMaster.setAccount_status("InActive");
	 * 
	 * //int account_id=(int)
	 * GenericDaoSingleton.getGenericDao().updateQuery(ConstantQueries.
	 * DELETE_ACCOUNT, queryMap); accMaster=(AccountMaster)
	 * GenericDaoSingleton.getGenericDao().createOrUpdate(accountMaster);
	 * 
	 * if(accMaster==null){ return Response.status(Response.Status.NOT_FOUND).
	 * entity("Requested Account not found").build();
	 * //jsonObj.put("accountDeleteMessage", "Account Deleted Successfully"); }
	 * //return jsonObj;
	 * 
	 * }catch(Exception e){ accMaster=null;
	 * Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(accMaster).
	 * build(); } return
	 * Response.status(Response.Status.OK).entity("Account Deleted Successfully"
	 * ).build(); }
	 */

	/*
	 * @POST
	 * 
	 * @Path("create/{portfolioId}")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * createAccount(AccountMaster accountMaster,@PathParam ("portfolioId") int
	 * portfolioId){
	 * 
	 * 
	 * JSONParser jsnParser=new JSONParser(); jsonObj = (JSONObject)
	 * jsnParser.parse(createAccountString); String accountName=(String)
	 * jsonObj.get("accountName"); String accountHead=(String)
	 * jsonObj.get("accountHead"); String accountLogo=(String)
	 * jsonObj.get("accountLogo"); String backgroundImage=(String)
	 * jsonObj.get("backgroundImage"); String accountStatus=(String)
	 * jsonObj.get("accountStatus"); String portFolioId=(String)
	 * jsonObj.get("portFolioId");
	 * 
	 * try{ JSONObject jsonObj=new JSONObject(); HashMap<String,Object>
	 * queryMap=new HashMap<String,Object >(); queryMap.put("portfolioId",
	 * portfolioId); PortfolioMaster portfolioMaster=(PortfolioMaster)
	 * GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.
	 * GET_PARTICULAR_PORTFOLIO, queryMap);
	 * System.out.println(accountMaster.getAccount_name());
	 * accountMaster.setPortfolioMaster(portfolioMaster);
	 * 
	 * AccountMaster accountMst=(AccountMaster)
	 * GenericDaoSingleton.getGenericDao().createOrUpdate(accountMaster);
	 * 
	 * //sending json object if(accountMst!=null){
	 * jsonObj.put("accountDeleteMessage", "Account created Successfully");
	 * }else{ jsonObj.put("accountDeleteMessage", "Failed to create Account"); }
	 * 
	 * if(accountMst!=null){ return Response.status(Response.Status.CREATED)
	 * .entity("Account created Successfully").build(); }else{ return
	 * Response.status(Response.Status.NOT_FOUND) .entity(accountMst).build();
	 * 
	 * } }catch(Exception e){ e.printStackTrace(); return
	 * Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	 * .entity("Failed to create Account").build(); }
	 * 
	 * //return jsonObj; }
	 */

	@PUT
	@Path(value = "update/{portfolioId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAccount(AccountMaster accountMaster, @PathParam("portfolioId") int portfolioId) {

		HashMap<String, Object> queryMapPortfolio = new HashMap<String, Object>();
		queryMapPortfolio.put("portfolioId", portfolioId);
		try {
			PortfolioMaster portfolioMaster = (PortfolioMaster) GenericDaoSingleton.getGenericDao()
					.findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PORTFOLIO, queryMapPortfolio);

			accountMaster.setPortfolioMaster(portfolioMaster);
			System.out.println("--->" + accountMaster.getAccount_name());
			HashMap<String, Object> queryMapAccount = new HashMap<String, Object>();
			queryMapAccount.put("accountId", accountMaster.getAccount_id());
			AccountMaster existingAccountMaster = (AccountMaster) GenericDaoSingleton.getGenericDao()
					.findUniqueByQuery(ConstantQueries.GET_PARTICULAR_ACCOUNT_BY_ID, queryMapAccount);

			existingAccountMaster.setAccount_head(accountMaster.getAccount_head());
			existingAccountMaster.setAccount_logo(accountMaster.getAccount_logo());
			existingAccountMaster.setAccount_name(accountMaster.getAccount_name());
			/* existingAccountMaster.setAccount_status("Active"); */
			/*
			 * existingAccountMaster.setBackground_image(accountMaster.
			 * getBackground_image());
			 * existingAccountMaster.setPortfolioMaster(accountMaster.
			 * getPortfolioMaster());
			 */

			AccountMaster accountMst = (AccountMaster) GenericDaoSingleton.getGenericDao()
					.createOrUpdate(existingAccountMaster);

			if (accountMst != null) {
				return Response.status(Response.Status.CREATED).entity("Account is Updated Successfully").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity(accountMst).build();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Update Account").build();
		}

	}

	@GET
	@Path("getAccountListForPortfolio/{username}/{portfolioName}")
	// @Consumes("text/plain")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccounts(@PathParam("username") String user_id, @PathParam("portfolioName") String portfolioName)
			throws IOException {

		Object[] account_details;

		List<AccountTO> accountTOList = new ArrayList<AccountTO>();
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		try {
			queryMap.put("user_id", user_id);
			queryMap.put("portfolioName", portfolioName);

			List<Object> accountList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ACCOUNTS_FOR_PORTFOLIO, queryMap);

			if (accountList == null) {
				return Response.status(Response.Status.NOT_FOUND).entity(accountList).build();
			}

			for (int i = 0; i < accountList.size(); i++) {
				AccountTO accountTO = new AccountTO();
				account_details = (Object[]) accountList.get(i);
				accountTO.setAccountId(Integer.parseInt(account_details[1].toString()));
				accountTO.setAccountName(account_details[0].toString());

				accountTOList.add(accountTO);
			}

		} catch (Exception e) {
			accountTOList = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(accountTOList).build();
		}

		return Response.status(Response.Status.OK).entity(accountTOList).build();

	}

	@GET
	@Path("fetchParticularAccount/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AccountTO getParticularAccountList(@PathParam("accountId") int accountId) {
		System.out.println("Getting Portfolio list for accountId  :" + accountId);
		List<AccountTO> accountList = new ArrayList<AccountTO>();
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
		AccountTO accountTO = new AccountTO();
		try {
			keyvalueMap.put("accountId", accountId);
			List<Object> accountData = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_PARTICULAR_ACCOUNT, keyvalueMap);
			/*
			 * Iterator itr = accountData.iterator(); while(itr.hasNext()) {
			 * 
			 * Object[] account_details=(Object[])itr.next();
			 * accTo.setAccountId(Integer.parseInt((String)
			 * account_details[0])); accTo.setAccountName((String)
			 * account_details[1]); accTo.setAccountHead((String)
			 * account_details[2]);
			 * accTo.setAccountLogo((String)account_details[3]);
			 * accTo.setBackgroundImage((String)account_details[4]);
			 * accTo.setAccountStatus((String)account_details[5]); }
			 */

			for (int i = 0; i < accountData.size(); i++) {

				AccountMaster accountMaster = (AccountMaster) accountData.get(i);
				accountTO.setAccountId(accountMaster.getAccount_id());
				accountTO.setAccountName(accountMaster.getAccount_name());
				accountTO.setAccountHead(accountMaster.getAccount_head());
				accountTO.setAccountLogo(accountMaster.getAccount_logo());
				accountTO.setBackgroundImage(accountMaster.getBackground_image());
				// accountTO.setAccountStatus(accountMaster.getAccount_status());
			}
			// accountList.add(accountTO);
		} catch (Exception e) {
			System.out.println("Error while fetching Particular Account list");
			e.printStackTrace();
		}
		return accountTO;
	}

	// updated account services

	@GET
	@Path("getAccountListForPortfolio/{portfolioId}")
	// @Consumes("text/plain")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveAccounts(@PathParam("portfolioId") int portfolioId) throws IOException {

		/*
		 * List<ProjectMaster>allocatedProjectsList=dashboardDao.
		 * getAllocatedProjectsForUser(user_id);
		 * 
		 * List<AllocatedProjects>allocatedProjectNamesList=new
		 * ArrayList<AllocatedProjects>();
		 * 
		 * AllocatedProjects allocatedProjects;
		 * 
		 * for(int i=0;i<allocatedProjectsList.size();i++){
		 * allocatedProjects=new AllocatedProjects(); String
		 * projectName=allocatedProjectsList.get(i).getProject_name();
		 * allocatedProjects.setProjectName(projectName);
		 * allocatedProjectNamesList.add(allocatedProjects); projectName=null; }
		 * 
		 * return allocatedProjectNamesList;
		 */

		List<AccountTO> accountTOList = new ArrayList<AccountTO>();
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		try {
			queryMap.put("portfolioId", portfolioId);

			List<Object> accountList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ACTIVE_ACCOUNTS_FOR_PORTFOLIO, queryMap);
			ProjectMaster prm;
			UserProjectMapping userProjectMapping;

			if (accountList == null) {
				return Response.status(Response.Status.NOT_FOUND).entity(accountList).build();
			}

			for (int i = 0; i < accountList.size(); i++) {
				AccountTO accountTO = new AccountTO();
				Object[] acc = (Object[]) accountList.get(i);
				accountTO.setAccountId((int) acc[0]);
				accountTO.setAccountName((String) acc[1]);
				accountTO.setAccountHead((String) acc[2]);
				accountTO.setAccountLogo((String) acc[3]);

				accountTOList.add(accountTO);
			}

		} catch (Exception e) {
			accountTOList = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(accountTOList).build();
		}

		return Response.status(Response.Status.OK).entity(accountTOList).build();

	}

	@POST
	@Path(value = "create/{portfolioId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(AccountMaster accountMaster, @PathParam("portfolioId") int portfolioId) {

		/*
		 * JSONParser jsnParser=new JSONParser(); jsonObj = (JSONObject)
		 * jsnParser.parse(createAccountString); String accountName=(String)
		 * jsonObj.get("accountName"); String accountHead=(String)
		 * jsonObj.get("accountHead"); String accountLogo=(String)
		 * jsonObj.get("accountLogo"); String backgroundImage=(String)
		 * jsonObj.get("backgroundImage"); String accountStatus=(String)
		 * jsonObj.get("accountStatus"); String portFolioId=(String)
		 * jsonObj.get("portFolioId");
		 */

		try {
			JSONObject jsonObj = new JSONObject();
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("portfolioId", portfolioId);

			PortfolioMaster portfolioMaster = (PortfolioMaster) GenericDaoSingleton.getGenericDao()
					.findUniqueByQuery(ConstantQueries.GET_PARTICULAR_PORTFOLIO, queryMap);

			accountMaster.setPortfolioMaster(portfolioMaster);
			/* accountMaster.setAccount_status("Active"); */

			AccountMaster accountMst = (AccountMaster) GenericDaoSingleton.getGenericDao()
					.createOrUpdate(accountMaster);

			// sending json object
			/*
			 * if(accountMst!=null){ jsonObj.put("accountDeleteMessage",
			 * "Account created Successfully"); }else{
			 * jsonObj.put("accountDeleteMessage", "Failed to create Account");
			 * }
			 */

			if (accountMst != null) {
				return Response.status(Response.Status.CREATED).entity("Account created Successfully").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity(accountMst).build();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create Account").build();
		}

	}

	@DELETE
	@Path(value = "delete/{accountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAccount(@PathParam("accountId") int accountId) {

		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
		
		try {

			GenericDaoSingleton.getGenericDao().delete(AccountMaster.class, accountId);
			return Response.status(Response.Status.OK).entity("Account deleted Successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete Account").build();
		}

	}
}
