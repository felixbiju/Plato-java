package com.PLATO.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;


import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.dao.GenericDao;
import com.PLATO.entities.AccountMaster;
import com.PLATO.entities.PortfolioMaster;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.RoleMaster;
import com.PLATO.entities.UserMaster;
import com.PLATO.entities.UserProjectMapping;
import com.PLATO.userTO.ProjectTO;
import com.PLATO.userTO.RoleTO;
import com.PLATO.userTO.UserProjectMappingTO;
import com.PLATO.userTO.UserTO;
import com.PLATO.userTO.UserWithRoleArrayTO;
/**
 * 
 * @author 10643331 Sueanne
 *
 */
@Path("UserConfigService")
public class UserConfigService {

	/*
	 * Description : Service to perform various operation on users page Author:
	 * Sueanne Alphonso
	 */

	private static final Logger logger = Logger
			.getLogger(UserConfigService.class);

	// ********************To Fetch All Users from UserMaster************
	/**
	 * This service is used to fetch users
	 * @return all Users from UserMaster Table
	 */
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsers() {

		logger.info("Fetching all Users");
		List<UserTO> userMasterList = new ArrayList<UserTO>();
		Object userMaster[];
		UserTO userTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> userList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS, queryMap);

			if (userList == null || userList.size() == 0
					|| userList.isEmpty() == true) {
				logger.error("Users not found");
				userTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userTO).build();
			}

			//setting the details fetched in TO object
			for (int i = 0; i < userList.size(); i++) {
				userTO = new UserTO();
				userMaster = (Object[]) userList.get(i);
				userTO.setUser_id((String) userMaster[0]);
				userTO.setName((String) userMaster[1]);
				userTO.setProject_name((String) userMaster[2]);
				userTO.setRole_name((String) userMaster[3]);
				userMasterList.add(userTO);
			}

			logger.info("Users fetched successfully");
			return Response.status(Response.Status.OK).entity(userMasterList)
					.build();

		} catch (Exception e) {
			userTO = null;
			logger.error("Error while getting users");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userTO).build();
		}

	}

	// **************************************FETCHING PROJECTS FOR EACH USER
	// with project and role Added on 6/12/2017***********

	/**
	 * This service is used to fetch users
	 * @return All users from UserProjectMapping and UserMaster
	 */
	
	@GET
	@Path("userList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserList() {

		logger.info("Fetching Users Project and Roles");

		Map<String, List<UserTO>> userProjectMap = new HashMap<String, List<UserTO>>();
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
		List<UserTO> projectList = null;
		try {
			List<Object> userProjectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS, keyvalueMap);
			if (userProjectList.size() == 0 || userProjectList == null) {
				userProjectMap = null;
				logger.error("Users Not Found");

				return Response.status(Response.Status.NOT_FOUND)
						.entity(userProjectMap).build();
			}

			Iterator<Object> it = userProjectList.iterator();
			while (it.hasNext()) {
				UserTO userTO = new UserTO();

				Object[] userDetails = (Object[]) it.next();
				String name = userDetails[1].toString();
				userTO.setUser_id(userDetails[0].toString());
				userTO.setProject_name(userDetails[2].toString());
				userTO.setRole_name(userDetails[3].toString());

				if (userProjectMap.containsKey(name)) {
					projectList = userProjectMap.get(name);
				} else {
					projectList = new ArrayList<UserTO>();
				}
				projectList.add(userTO);
				userProjectMap.put(name, projectList);
			}
		} catch (Exception e) {
			System.out.println("Exception in getting project List");
			userProjectMap = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userProjectMap).build();
		}
		logger.info("Users Details fetched successfully");
		return Response.status(Response.Status.OK).entity(userProjectMap)
				.build();

	}

	// *******************************************************

	// ***********************Fetching with many roles 8/12/17*********
			/**
			 * This service is to fetch users with many roles
			 * @return All users with many roles too.
			 */
	@GET
	@Path("userProjectRoleList")
	@Produces(MediaType.APPLICATION_JSON)
	// Returns the list of all users
	public Response getUserListwithManyRoles() {

		logger.info("Fetching All User Details");
		Map<String, Map<String, List<UserTO>>> userProjectMap = new HashMap<String, Map<String, List<UserTO>>>();

		// Map<String,List<UserTO>> projectRoleMap=new
		// HashMap<String,List<UserTO>>();

		Map<String, List<UserTO>> projectList = new HashMap<String, List<UserTO>>();
		List<UserTO> roleList = null;
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();

		// Map<String, List<UserTO>> projectList;

		try {
			List<Object> userProjectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS, keyvalueMap);
			if (userProjectList.size() == 0 || userProjectList == null) {
				userProjectMap = null;
				// System.out.println("Not Found");
				logger.error("Users Not Found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userProjectMap).build();
			}

			Iterator<Object> it = userProjectList.iterator();
			while (it.hasNext()) {
				UserTO userTO = new UserTO();

				Object[] userDetails = (Object[]) it.next();
				// String user_id=userDetails[0].toString();
				String name = userDetails[1].toString();
				String pname = userDetails[2].toString();

				// userTO.setName(name);
				// userTO.setProject_name(pname);

				userTO.setRole_name(userDetails[3].toString());
				// int category_id=Integer.parseInt(userDetails[0].toString());

				// String category_name=userDetails[1].toString();

				// toolTO.setCategory_id(category_id);
				// userTO.setUser_id(user_id);
				userTO.setUser_id(userDetails[0].toString());

				// userTO.setProject_name(userDetails[2].toString());

				// userTO.setRole_name(userDetails[3].toString());

				if (userProjectMap.containsKey(name)) {
					projectList = userProjectMap.get(name);
				} else {
					projectList = new HashMap<String, List<UserTO>>();
				}

				if (projectList.containsKey(pname)) {
					roleList = projectList.get(pname);
				} else {
					roleList = new ArrayList<UserTO>();
				}

				roleList.add(userTO);

				projectList.put(pname, roleList);
				userProjectMap.put(name, projectList);

				// if(userRoleMap.containsKey(role))

			}
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("Exception in getting project List");
			userProjectMap = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userProjectMap).build();
		}

		logger.info("Users Details fetched successfully");
		return Response.status(Response.Status.OK).entity(userProjectMap)
				.build();

	}

	// *******************************************************

	// **************************************FETCHING PROJECTS FOR EACH USER
				// with project and role Added on 6/12/2017***********
		/**
		 * This service is user to get info of a particular user
		 * @param user_id Email ID used to login
		 * @return Details of the user.
		 */
				@GET
				@Path("userDetailsList/{user_id}")
				@Produces(MediaType.APPLICATION_JSON)
				public Response getUserDetailsList(@PathParam("user_id") String user_id) {

					logger.info("Fetching Users Project and Roles");

					Map<String, List<UserTO>> userProjectMap = new HashMap<String, List<UserTO>>();
					HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
					keyvalueMap.put("user_id", user_id);
					List<UserTO> projectList = null;
					try {
						List<Object> userProjectList = GenericDaoSingleton.getGenericDao()
								.findByQuery(ConstantQueries.GET_PARTICULAR_USERS_DETAILS, keyvalueMap);
						if (userProjectList.size() == 0 || userProjectList == null) {
							userProjectMap = null;
							logger.error("Users Not Found");

							return Response.status(Response.Status.NOT_FOUND)
									.entity(userProjectMap).build();
						}

						Iterator<Object> it = userProjectList.iterator();
						while (it.hasNext()) {
							UserTO userTO = new UserTO();

							Object[] userDetails = (Object[]) it.next();
							String name = userDetails[1].toString();
							userTO.setUser_id(userDetails[0].toString());
							userTO.setProject_name(userDetails[2].toString());
							userTO.setRole_name(userDetails[3].toString());

							if (userProjectMap.containsKey(name)) {
								projectList = userProjectMap.get(name);
							} else {
								projectList = new ArrayList<UserTO>();
							}
							projectList.add(userTO);
							userProjectMap.put(name, projectList);
						}
					} catch (Exception e) {
						System.out.println("Exception in getting project List");
						userProjectMap = null;
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity(userProjectMap).build();
					}
					logger.info("Users Details fetched successfully");
					return Response.status(Response.Status.OK).entity(userProjectMap)
							.build();

				}

				// *******************************************************
	
	
	// ***********************Fetching with many roles 8/12/17*********

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	
	@GET
	@Path("userProjectRoleList2")
	@Produces(MediaType.APPLICATION_JSON)
	// Returns the list of all users
	public Response getUserListwithManyRoless() {

		logger.info("Fetching Users Project and Roles");

		Map<String, Map<String, Map<String, ArrayList<String>>>> userRoleProjectMap = new HashMap<String, Map<String, Map<String, ArrayList<String>>>>();

		Map<String, Map<String, ArrayList<String>>> userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();

		Map<String, ArrayList<String>> projectList = new HashMap<String, ArrayList<String>>();
		ArrayList<String> roleList = null;
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();

		try {
			List<Object> userProjectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS, keyvalueMap);
			if (userProjectList.size() == 0 || userProjectList == null) {
				userProjectMap = null;
				logger.error("Users Not Found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userProjectMap).build();
			}

			Iterator<Object> it = userProjectList.iterator();
			while (it.hasNext()) {
				Object[] userDetails = (Object[]) it.next();
				String user_id = userDetails[0].toString();
				String name = userDetails[1].toString();
				String pname = userDetails[2].toString();
				String rname = userDetails[3].toString();

				if (userRoleProjectMap.containsKey(user_id)) {
					userProjectMap = userRoleProjectMap.get(user_id);
				} else {
					userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();
				}

				if (userProjectMap.containsKey(name)) {
					projectList = userProjectMap.get(name);
				} else {
					projectList = new HashMap<String, ArrayList<String>>();
				}

				if (projectList.containsKey(pname)) {
					roleList = projectList.get(pname);

				} else {
					roleList = new ArrayList<String>();
				}

				roleList.add(rname);
				String roles = String.join(", ", roleList);
				roleList.clear();
				roleList.add(roles);

				projectList.put(pname, roleList);
				userProjectMap.put(name, projectList);
				userRoleProjectMap.put(user_id, userProjectMap);
			}

			List<Object> userMasterList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.ALL_USERS, keyvalueMap);

			if (userMasterList == null || userMasterList.size() == 0
					|| userMasterList.isEmpty() == true) {
				System.out.println("Users not found");

			} else {

				Object user[];
				String uid;
				String namee;

				for (int i = 0; i < userMasterList.size(); i++) {
					user = (Object[]) userMasterList.get(i);
					uid = user[0].toString();
					namee = user[1].toString();

					if (userRoleProjectMap.containsKey(uid)) {
						System.out.println("already present");
					} else {

						userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();
						projectList = new HashMap<String, ArrayList<String>>();
						roleList = new ArrayList<String>();

						String roles = "NA";
						String pnamee = "NA";
						roleList.add(roles);
						projectList.put(pnamee, roleList);
						userProjectMap.put(namee, projectList);
						userRoleProjectMap.put(uid, userProjectMap);

					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception is getting Users Details");

			userProjectMap = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userProjectMap).build();
		}

		logger.info("Users Details fetched successfully");
		return Response.status(Response.Status.OK).entity(userRoleProjectMap)
				.build();

	}

	// **************************************FETCHING Different ROLES for a USER
	// given project id, user id. 12/12/2017***********

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	
	@GET
	@Path("getRoles/{userId}/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserRoles(@PathParam("userId") String userId,
			@PathParam("projectId") Integer projectId) {

		logger.debug("Fetching Roles for userId: " + userId + "in projectId: "
				+ projectId);

		List<RoleTO> roleMasterList = new ArrayList<RoleTO>();
		Object roleMaster[];
		RoleTO roleTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();

			queryMap.put("userId", userId);
			queryMap.put("projectId", projectId);

			List<Object> roleList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ROLES_FOR_PROJECT,
							queryMap);

			if (roleList == null || roleList.size() == 0
					|| roleList.isEmpty() == true) {
				logger.error("Roles Not Found");

				roleTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(roleTO).build();
			}

			for (int i = 0; i < roleList.size(); i++) {
				roleTO = new RoleTO();
				roleMaster = (Object[]) roleList.get(i);
				roleTO.setRoleId((int) roleMaster[0]);
				roleTO.setRoleName(roleMaster[1].toString());

				roleMasterList.add(roleTO);
			}

			logger.info("Roles fetched successfully");
			return Response.status(Response.Status.OK).entity(roleMasterList)
					.build();

		} catch (Exception e) {
			roleTO = null;
			logger.error("Error while getting Roles for Particular User");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(roleTO).build();
		}

	}

	// ***********************Fetch Users in a Project ************
	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path("allUsersInProject/{projectId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsersInProject(
			@PathParam("projectId") Integer projectId) {

		logger.debug("Fetching All Users in Project : " + projectId);

		List<UserTO> userMasterList = new ArrayList<UserTO>();
		Object userMaster[];
		UserTO userTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();

			queryMap.put("projectId", projectId);

			List<Object> userList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS_in_PROJECT,
							queryMap);

			if (userList == null || userList.size() == 0
					|| userList.isEmpty() == true) {
				// System.out.println("Users not found");

				logger.error("Users not Found");
				userTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userTO).build();
			}

			for (int i = 0; i < userList.size(); i++) {
				userTO = new UserTO();
				userMaster = (Object[]) userList.get(i);
				userTO.setUser_id((String) userMaster[0]);
				userTO.setName((String) userMaster[1]);
				userTO.setPassword((String) userMaster[2]);

				userMasterList.add(userTO);
			}

			logger.info("Users fetched successfully");

			return Response.status(Response.Status.OK).entity(userMasterList)
					.build();

		} catch (Exception e) {
			userTO = null;
			// System.out.println("Error while getting users");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userTO).build();
		}

	}

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	
	@GET
	@Path("allUsersInProjectInAccountInPortfolio/{projectId}/{accountId}/{portfolioId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsersInProjectInAccountInPortfolio(
			@PathParam("projectId") Integer projectId,
			@PathParam("accountId") Integer accountId,
			@PathParam("portfolioId") Integer portfolioId) {

		logger.debug("Fetching all users in PortfolioId: " + portfolioId
				+ ", AccountId: " + accountId + " and ProjectId: " + projectId);

		Map<String, Map<String, Map<String, ArrayList<String>>>> userRoleProjectMap = new HashMap<String, Map<String, Map<String, ArrayList<String>>>>();

		Map<String, Map<String, ArrayList<String>>> userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();

		Map<String, ArrayList<String>> projectList = new HashMap<String, ArrayList<String>>();
		ArrayList<String> roleList = null;
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
		keyvalueMap.put("portfolioId", portfolioId);
		keyvalueMap.put("accountId", accountId);
		keyvalueMap.put("projectId", projectId);

		try {
			List<Object> userProjectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(
							ConstantQueries.GET_ALL_USERS_in_selected_PROJECT,
							keyvalueMap);
			if (userProjectList.size() == 0 || userProjectList == null) {
				userProjectMap = null;
				logger.error("Users not Found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userProjectMap).build();
			}

			Iterator<Object> it = userProjectList.iterator();
			while (it.hasNext()) {
				Object[] userDetails = (Object[]) it.next();
				String user_id = userDetails[0].toString();
				String name = userDetails[1].toString();
				String pname = userDetails[2].toString();
				String rname = userDetails[3].toString();

				if (userRoleProjectMap.containsKey(user_id)) {
					userProjectMap = userRoleProjectMap.get(user_id);

				} else {
					userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();
				}

				if (userProjectMap.containsKey(name)) {
					projectList = userProjectMap.get(name);
				} else {
					projectList = new HashMap<String, ArrayList<String>>();
				}

				if (projectList.containsKey(pname)) {
					roleList = projectList.get(pname);

				} else {
					roleList = new ArrayList<String>();
				}

				roleList.add(rname);
				String roles = String.join(", ", roleList);
				roleList.clear();
				roleList.add(roles);

				projectList.put(pname, roleList);
				userProjectMap.put(name, projectList);
				userRoleProjectMap.put(user_id, userProjectMap);

			}

		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("Exception in getting users in Portfolio List");
			userProjectMap = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userProjectMap).build();
		}
		logger.info("Users fetched successfully");
		return Response.status(Response.Status.OK).entity(userRoleProjectMap)
				.build();

	}

	// ***********************Fetch Users in an Account ************
	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path("allUsersInAccount/{accountId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsersInAccount(
			@PathParam("accountId") Integer accountId) {

		logger.debug("Fetching Users in AcocuntId: " + accountId);

		List<UserTO> userMasterList = new ArrayList<UserTO>();
		Object userMaster[];
		UserTO userTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("accountId", accountId);

			List<Object> userList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS_in_ACCOUNT,
							queryMap);

			if (userList == null || userList.size() == 0
					|| userList.isEmpty() == true) {

				logger.error("Users not Found");
				userTO = null;

				return Response.status(Response.Status.NOT_FOUND)
						.entity(userTO).build();
			}

			for (int i = 0; i < userList.size(); i++) {
				userTO = new UserTO();
				userMaster = (Object[]) userList.get(i);
				userTO.setUser_id((String) userMaster[0]);
				userTO.setName((String) userMaster[1]);
				userTO.setPassword((String) userMaster[2]);

				userMasterList.add(userTO);
			}
			logger.info("Users fetched successfully");

			return Response.status(Response.Status.OK).entity(userMasterList)
					.build();

		} catch (Exception e) {
			userTO = null;
			// System.out.println("Error while getting users");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userTO).build();
		}

	}

	// ***********************Fetch Users in a Account in a particular portfolio
	// 22/12/17************
	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	
	@GET
	@Path("allUsersInAccountInPortfolio/{accountId}/{portfolioId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsersInAccountInPortfolio(
			@PathParam("accountId") Integer accountId,
			@PathParam("portfolioId") Integer portfolioId) {

		logger.debug("Fetching Users in PortfolioId: " + portfolioId
				+ " and AccountId: " + accountId);
		Map<String, Map<String, Map<String, ArrayList<String>>>> userRoleProjectMap = new HashMap<String, Map<String, Map<String, ArrayList<String>>>>();

		Map<String, Map<String, ArrayList<String>>> userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();

		Map<String, ArrayList<String>> projectList = new HashMap<String, ArrayList<String>>();
		ArrayList<String> roleList = null;
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
		keyvalueMap.put("portfolioId", portfolioId);
		keyvalueMap.put("accountId", accountId);

		try {
			List<Object> userProjectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(
							ConstantQueries.GET_ALL_USERS_in_selected_ACCOUNT,
							keyvalueMap);
			if (userProjectList.size() == 0 || userProjectList == null) {
				userProjectMap = null;
				logger.error("Users Not Found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userProjectMap).build();
			}

			Iterator<Object> it = userProjectList.iterator();
			while (it.hasNext()) {
				Object[] userDetails = (Object[]) it.next();
				String user_id = userDetails[0].toString();
				String name = userDetails[1].toString();
				String pname = userDetails[2].toString();
				String rname = userDetails[3].toString();

				if (userRoleProjectMap.containsKey(user_id)) {
					userProjectMap = userRoleProjectMap.get(user_id);

				} else {
					userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();
				}

				if (userProjectMap.containsKey(name)) {
					projectList = userProjectMap.get(name);
				} else {
					projectList = new HashMap<String, ArrayList<String>>();
				}

				if (projectList.containsKey(pname)) {
					roleList = projectList.get(pname);

				} else {
					roleList = new ArrayList<String>();
				}

				roleList.add(rname);
				String roles = String.join(",", roleList);
				roleList.clear();
				roleList.add(roles);

				projectList.put(pname, roleList);
				userProjectMap.put(name, projectList);
				userRoleProjectMap.put(user_id, userProjectMap);
			}
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("Exception in getting users in Portfolio List");
			userProjectMap = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userProjectMap).build();
		}
		logger.info("Users Fetched Successfully");
		return Response.status(Response.Status.OK).entity(userRoleProjectMap)
				.build();

	}

	// ***********************Fetch Users in a Portfolio ************
	
	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path("allUsersInPortfolio/{portfolioId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsersInPortfolio(
			@PathParam("portfolioId") Integer portfolioId) {

		logger.debug("Fetching Users in PortfolioId : " + portfolioId);
		List<UserTO> userMasterList = new ArrayList<UserTO>();
		Object userMaster[];
		UserTO userTO = null;

		try {
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("portfolioId", portfolioId);

			List<Object> userList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_USERS_in_PORTFOLIO,
							queryMap);

			if (userList == null || userList.size() == 0
					|| userList.isEmpty() == true) {

				logger.error("Users Not Found");
				userTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userTO).build();
			}

			for (int i = 0; i < userList.size(); i++) {
				userTO = new UserTO();
				userMaster = (Object[]) userList.get(i);
				userTO.setUser_id((String) userMaster[0]);
				userTO.setName((String) userMaster[1]);
				// userTO.setPassword((String) userMaster[2]);

				userMasterList.add(userTO);
			}

			logger.info("Users Fetched Successfully");
			return Response.status(Response.Status.OK).entity(userMasterList)
					.build();

		} catch (Exception e) {
			userTO = null;
			// System.out.println("Error while getting users");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userTO).build();
		}

	}

	// ***********************Fetch Users in Portfolio with project details
	// 22/12/17 ************
	
	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path("allUsersInPortfoliowithProjectDetails/{portfolioId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllUsersInPortfoliowithProjectDetails(
			@PathParam("portfolioId") Integer portfolioId) {

		logger.debug("Fecthing users details for PortfolioId : " + portfolioId);
		Map<String, Map<String, Map<String, ArrayList<String>>>> userRoleProjectMap = new HashMap<String, Map<String, Map<String, ArrayList<String>>>>();

		Map<String, Map<String, ArrayList<String>>> userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();

		// Map<String,List<UserTO>> projectRoleMap=new
		// HashMap<String,List<UserTO>>();

		Map<String, ArrayList<String>> projectList = new HashMap<String, ArrayList<String>>();
		ArrayList<String> roleList = null;
		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
		keyvalueMap.put("portfolioId", portfolioId);

		try {

			List<Object> userProjectList = GenericDaoSingleton
					.getGenericDao()
					.findByQuery(
							ConstantQueries.GET_ALL_USERS_in_Selected_PORTFOLIO,
							keyvalueMap);
			if (userProjectList.size() == 0 || userProjectList == null) {
				userProjectMap = null;
				// System.out.println("Not Found");
				logger.info("Users Not Found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(userProjectMap).build();
			}

			Iterator<Object> it = userProjectList.iterator();
			while (it.hasNext()) {
				Object[] userDetails = (Object[]) it.next();
				String user_id = userDetails[0].toString();
				String name = userDetails[1].toString();
				String pname = userDetails[2].toString();
				String rname = userDetails[3].toString();

				if (userRoleProjectMap.containsKey(user_id)) {
					userProjectMap = userRoleProjectMap.get(user_id);

				} else {
					userProjectMap = new HashMap<String, Map<String, ArrayList<String>>>();
				}

				if (userProjectMap.containsKey(name)) {
					projectList = userProjectMap.get(name);
				} else {
					projectList = new HashMap<String, ArrayList<String>>();
				}

				if (projectList.containsKey(pname)) {
					roleList = projectList.get(pname);

				} else {
					roleList = new ArrayList<String>();
				}

				roleList.add(rname);
				String roles = String.join(",", roleList);
				roleList.clear();
				roleList.add(roles);

				projectList.put(pname, roleList);
				userProjectMap.put(name, projectList);
				userRoleProjectMap.put(user_id, userProjectMap);

			}
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("Exception in getting users in Portfolio List");
			userProjectMap = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userProjectMap).build();
		}
		logger.info("Users Found Successfully");
		return Response.status(Response.Status.OK).entity(userRoleProjectMap)
				.build();
	}

	// ***************************************Create Users**********************************************

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	
	@POST
	@Path(value = "create/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(UserMaster userMaster) throws Exception {

		try {
			UserMaster usermaster = (UserMaster) GenericDaoSingleton
					.getGenericDao().createOrUpdate(userMaster);

			if (usermaster != null) {
				logger.info("Users Created");
				return Response.status(Response.Status.OK)
						.entity("User Created Successfully").build();
			} else {
				logger.info("Users Creation Failed");
				return Response.status(Response.Status.OK)
						.entity("User Creation Failed").build();
			}
		} catch (Exception e) {

			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			if (e.getMessage().equals("Duplicate Row in table")) {
				logger.error("Users Already Exists");
				return Response.status(Response.Status.CONFLICT)
						.entity("User Already Exists").build();
			}

			logger.error("Could Not Create User");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error Creating User").build();

		}

	}

	// ***********************Create Users Along with Project Mapping ****************
	/**
	 * @author 10643380(Rahul Bhardwaj)
	 * */
	@PUT
	@Path("allocateMultipleRolesForMultipleUsers/{projectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response allocateMultipleRolesForMultipleUsers(JSONObject roleUserSet,@PathParam("projectId") Integer projectId) throws Exception{
		
		String defaultProject; 
		ArrayList<HashMap<Object,Object>> roleList=new ArrayList<HashMap<Object,Object>>();
		ArrayList<HashMap<Object,Object>> userList=new ArrayList<HashMap<Object,Object>>();
		try {
//			JSONArray roleArray=new JSONArray();
//			JSONArray userArray=new JSONArray();
			roleList=(ArrayList<HashMap<Object,Object>>)roleUserSet.get("roleList");
			userList=(ArrayList<HashMap<Object,Object>>)roleUserSet.get("userList");
			logger.debug(roleList.toString());
			logger.debug(userList.toString());

		}catch(Exception e){
			e.printStackTrace();
		}
			HashMap<String, Object> queryMapProject = new HashMap<String, Object>();
			queryMapProject.put("projectId", projectId);
			ProjectMaster projectMaster=(ProjectMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_PROJECT_BY_ID,queryMapProject);
			
			PortfolioMaster portfolioMaster = (PortfolioMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_PORTFOLIO_FROM_PROJECT,queryMapProject);

			AccountMaster accountMaster = (AccountMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_ACCOUNT_FROM_PROJECT,queryMapProject);

			if (projectMaster == null) {
				logger.error("Could Not Find Projet");
				return Response.status(Response.Status.OK)
						.entity("Could Not Find Projet").build();
			}
			
			for(HashMap<Object,Object> userHashMap:userList) {
				HashMap<String, Object> queryMapUser = new HashMap<String, Object>();
				queryMapUser.put("userId",(String)userHashMap.get("user_id"));
				UserMaster userMaster=(UserMaster)GenericDaoSingleton.getGenericDao().findUniqueByQuery(
						ConstantQueries.GET_PARTICULAR_USER,queryMapUser);
				for(HashMap<Object,Object> roleHashMap:roleList) {
					HashMap<String, Object> queryMapRole = new HashMap<String, Object>();
					BigDecimal big;
					big=(BigDecimal)roleHashMap.get("roleId");
					queryMapRole.put("roleId",big.intValue());

					RoleMaster roleMaster = (RoleMaster) GenericDaoSingleton.getGenericDao()
							.findUniqueByQuery(ConstantQueries.GET_PARTICULAR_ROLE_BY_ID,queryMapRole);
					
					UserProjectMapping userProjectMapping = new UserProjectMapping();
					userProjectMapping.setProjectMaster(projectMaster);
					userProjectMapping.setRoleMaster(roleMaster);
					userProjectMapping.setUserMaster(userMaster);
					userProjectMapping.setAccountMaster(accountMaster);
					userProjectMapping.setPortfolioMaster(portfolioMaster);
					
					List<Object> u=GenericDaoSingleton.getGenericDao()
							.findByQuery(ConstantQueries.GET_DEFAULT_PROJECT_FOR_USER,queryMapUser);
					
					
					if(u==null || u.isEmpty()){
						defaultProject="Y";
						
					}else{
					int proId=(int) u.get(0);
					if(projectMaster.getProject_id()==proId){
						defaultProject="Y";
					}else{
						defaultProject="N";
					}
					
					}
					userProjectMapping.setDefault_project(defaultProject);		
					try {
						UserProjectMapping userProMapping = (UserProjectMapping) GenericDaoSingleton
								.getGenericDao().createOrUpdate(userProjectMapping);

						if (userProMapping != null) {
							logger.info("User Mapped Successfully");
//							return Response.status(Response.Status.OK)
//									.entity("User Mapping Created Successfully").build();
						} else {
							logger.error("User Mapping Failed");
							return Response.status(Response.Status.OK)
									.entity("User Mapping Creation Failed").build();
						}
					} catch (Exception e) {

						System.out.println(e.getCause());
						System.out.println(e.getMessage());
						if (e.getMessage().equals("Duplicate Row in table")) {
							logger.error("User Already Exists");
							return Response.status(Response.Status.CONFLICT)
									.entity("User Already Exists").build();
						}

						logger.error("Error Creating User");

						return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity("Error Creating User").build();

					}
				}

				
			}

		return Response.status(Response.Status.OK)
				.entity("Allocated users").build();
	}

	// ***********************Create Users Along with Project Mapping (Completed on 5/12/17)****************
	
	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	
	@PUT
	@Path(value = "create/{projectId}/{roleId}/{defaultProject}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUserwithProjRoleMapping(UserMaster userMaster,
			@PathParam("projectId") Integer projectId,
			@PathParam("roleId") Integer roleId,
			@PathParam("defaultProject") String defaultProject)
			throws Exception {

		// /{defaultProject}
		// ,@PathParam("defaultProject") String defaultProject
		logger.debug("Mapping User to projectId " + projectId
				+ " with RoleId. " + roleId + " Default project : "
				+ defaultProject);
		HashMap<String, Object> queryMapProject = new HashMap<String, Object>();
		queryMapProject.put("projectId", projectId);

		ProjectMaster projectMaster = (ProjectMaster) GenericDaoSingleton
				.getGenericDao().findUniqueByQuery(
						ConstantQueries.GET_PARTICULAR_PROJECT_BY_ID,
						queryMapProject);

		if (projectMaster == null) {
			logger.error("Could Not Find Projet");
			return Response.status(Response.Status.OK)
					.entity("Could Not Find Projet").build();
		}

		HashMap<String, Object> queryMapRole = new HashMap<String, Object>();
		queryMapRole.put("roleId", roleId);

		RoleMaster roleMaster = (RoleMaster) GenericDaoSingleton
				.getGenericDao()
				.findUniqueByQuery(ConstantQueries.GET_PARTICULAR_ROLE_BY_ID,
						queryMapRole);

		if (roleMaster == null) {
			logger.error("Could Not Find Role");
			return Response.status(Response.Status.OK)
					.entity("Could Not Find Role").build();
		}

		PortfolioMaster portfolioMaster = (PortfolioMaster) GenericDaoSingleton
				.getGenericDao().findUniqueByQuery(
						ConstantQueries.GET_PARTICULAR_PORTFOLIO_FROM_PROJECT,
						queryMapProject);

		AccountMaster accountMaster = (AccountMaster) GenericDaoSingleton
				.getGenericDao().findUniqueByQuery(
						ConstantQueries.GET_PARTICULAR_ACCOUNT_FROM_PROJECT,
						queryMapProject);

		UserProjectMapping userProjectMapping = new UserProjectMapping();

		userProjectMapping.setProjectMaster(projectMaster);
		userProjectMapping.setRoleMaster(roleMaster);
		userProjectMapping.setUserMaster(userMaster);
		userProjectMapping.setAccountMaster(accountMaster);
		userProjectMapping.setPortfolioMaster(portfolioMaster);
		userProjectMapping.setDefault_project(defaultProject);

		try {
			UserProjectMapping userProMapping = (UserProjectMapping) GenericDaoSingleton
					.getGenericDao().createOrUpdate(userProjectMapping);

			if (userProMapping != null) {
				logger.info("User Mapped Successfully");
				return Response.status(Response.Status.OK)
						.entity("User Mapping Created Successfully").build();
			} else {
				logger.error("User Mapping Failed");
				return Response.status(Response.Status.OK)
						.entity("User Mapping Creation Failed").build();
			}
		} catch (Exception e) {

			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			if (e.getMessage().equals("Duplicate Row in table")) {
				logger.error("User Already Exists");
				return Response.status(Response.Status.CONFLICT)
						.entity("User Already Exists").build();
			}

			logger.error("Error Creating User");

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error Creating User").build();

		}

	}

	// ***********************Delete User********************

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@PUT
	@Path(value = "delete/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("userId") String userId) {
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);

		logger.debug("Deleting User " + userId);

		try {

			List<Object> userMappingsList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_MAPPINGS, queryMap);

			if (userMappingsList == null || userMappingsList.size() == 0
					|| userMappingsList.isEmpty() == true) {
				logger.info("User has no Mappings");

				System.out.println("User has no Mappings");

			} else {

				int users;
				UserProjectMappingTO uPMTO = null;
				for (int i = 0; i < userMappingsList.size(); i++) {

					uPMTO = new UserProjectMappingTO();

					users = (int) userMappingsList.get(i);

					uPMTO.setId(users);

					GenericDaoSingleton.getGenericDao().delete(
							UserProjectMapping.class, uPMTO.getId());

				}

			}

			UserMaster userMaster = (UserMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_USER, queryMap);

			if (userMaster == null) {
				logger.error("User Does Not Exists");

				// System.out.println("User Does Not Exists");
				userMaster = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity("User Does Not Exists").build();
			}

			GenericDaoSingleton.getGenericDao().delete(UserMaster.class,
					userMaster.getUser_id());

			logger.info("User Deleted Successfully");

			return Response.status(Response.Status.OK)
					.entity("User Deleted Successfully").build();
		} catch (Exception e) {

			logger.error("Exception in Deletion of User: User Deletion Failed");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("User Deletion Failed").build();
		}

	}

	// ***********************Delete Particular Users Particular Project 23/1/18 *******************

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path(value = "delete/{userId}/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUsersProject(@PathParam("userId") String userId,
			@PathParam("projectId") int projectId) {
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("projectId", projectId);

		logger.debug("Deleting Project: " + projectId + " of User " + userId);

		try {
			List<Object> userMappingsList = GenericDaoSingleton.getGenericDao()
					.findByQuery(
							ConstantQueries.GET_USERS_MAPPING_WITH_PROJECT,
							queryMap);

			if (userMappingsList == null || userMappingsList.size() == 0
					|| userMappingsList.isEmpty() == true) {
				logger.info("User has no Mappings with Particular Project");

				System.out
						.println("User has no Mappings with Particular Project");

			} else {
				int users;
				UserProjectMappingTO uPMTO = null;
				for (int i = 0; i < userMappingsList.size(); i++) {

					uPMTO = new UserProjectMappingTO();

					users = (int) userMappingsList.get(i);

					uPMTO.setId(users);

					GenericDaoSingleton.getGenericDao().delete(
							UserProjectMapping.class, uPMTO.getId());

				}

			}

			logger.info("Users Project Deleted Successfully");

			return Response.status(Response.Status.OK)
					.entity("Users Project Deleted Successfully").build();
		} catch (Exception e) {

			logger.error("Users Project Deletion Failed");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Users Project Deletion Failed").build();
		}

	}

	// ***********************Delete Particular Users PartiParticular Roles- 23/1/18 ********************

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path(value = "delete/{userId}/{projectId}/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUsersProjectsRole(@PathParam("userId") String userId,
			@PathParam("projectId") int projectId,
			@PathParam("roleId") int roleId) {
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("projectId", projectId);
		queryMap.put("roleId", roleId);

		logger.debug("Deleting Role " + roleId + " of Project: " + projectId
				+ " of User " + userId);

		try {

			UserProjectMapping userMappingsList = (UserProjectMapping) GenericDaoSingleton
					.getGenericDao()
					.findUniqueByQuery(
							ConstantQueries.GET_USERS_MAPPING_WITH_PROJECT_WITH_ROLE,
							queryMap);

			if (userMappingsList == null) {
				logger.info("User has no Mappings with Particular Project and Role");

				System.out
						.println("User has no Mappings with Particular Project and Role");

			} else {
				GenericDaoSingleton.getGenericDao().delete(
						UserProjectMapping.class, userMappingsList.getId());
			}

			logger.info("Users Role Deleted Successfully");
			return Response.status(Response.Status.OK)
					.entity("Users Role Deleted Successfully").build();
		} catch (Exception e) {

			logger.error("Users Role Deletion Failed");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Users Role Deletion Failed").build();
		}

	}

	// ***********************Update User*****************

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@PUT
	@Path(value = "update/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(UserMaster userMaster) {

		logger.debug("Updating User " + userMaster.getUser_id());
		try {
			HashMap<String, Object> queryMapUser = new HashMap<String, Object>();
			queryMapUser.put("userId", userMaster.getUser_id());
			UserMaster existingUserMaster = (UserMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_USER, queryMapUser);

			if (existingUserMaster == null) {

				logger.error("User Does Not Exists");
				// System.out.println("User Does Not Exists");
				existingUserMaster = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity("User Does Not Exists").build();
			}

			existingUserMaster.setUser_id(userMaster.getUser_id());
			existingUserMaster.setName(userMaster.getName());
			existingUserMaster.setPassword(userMaster.getPassword());

			UserMaster userMstr = (UserMaster) GenericDaoSingleton
					.getGenericDao().createOrUpdate(existingUserMaster);

			if (userMstr != null) {
				logger.info("User updated Successfully");
				return Response.status(Response.Status.OK)
						.entity("User updated Successfully").build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(userMstr).build();

			}
		} catch (Exception e) {
			logger.error("Failed to update User");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to update User").build();
		}

	}

	
	// ***********************Update User*****************

		/**
		 * @author 10643331(Sueanne Alphonso)
		 * */
		@PUT
		@Path(value = "updateUPM/")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(UserWithRoleArrayTO upmTO) {

		logger.debug("Updating User " + upmTO.getUser_id());
		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("userId", upmTO.getUser_id());

			List<Object> upmList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_UPMS, queryMap);

			if (upmList.isEmpty() || upmList == null) {

				logger.error("User Does Not Exists");
				// System.out.println("User Does Not Exists");
				upmList = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity("User Does Not Exists").build();
			}

			queryMap.clear();
			queryMap.put("projectId", upmTO.getProject_id());

			ProjectMaster pm = (ProjectMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_PROJECT_BY_ID,
							queryMap);

			queryMap.clear();
			queryMap.put("accountId", pm.getAccountMaster().getAccount_id());

			AccountMaster am = (AccountMaster) GenericDaoSingleton
					.getGenericDao().findUniqueByQuery(
							ConstantQueries.GET_PARTICULAR_ACCOUNT_BY_ID,
							queryMap);

			UserProjectMapping existingUPMs;

			for (int j = 0; j < upmList.size(); j++) {
				existingUPMs = (UserProjectMapping) upmList.get(j);

				existingUPMs.setDefault_project("N");

				UserProjectMapping userPM = (UserProjectMapping) GenericDaoSingleton
						.getGenericDao().createOrUpdate(existingUPMs);

				if (userPM != null) {
					logger.info("Updated default project to 'N' for "
							+ existingUPMs.getUserMaster().getUser_id());

				} else {
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity("Couldnt update default project to 'N' for "
									+ existingUPMs.getUserMaster().getUser_id())
							.build();

				}

			}

			queryMap.clear();
			queryMap.put("userId", upmTO.getUser_id());
			queryMap.put("project_id", upmTO.getProject_id());
			queryMap.put("account_id", am.getAccount_id());
			queryMap.put("portfolio_id", am.getPortfolioMaster()
					.getPortfolio_id());

			List<Object> upms = (List<Object>) GenericDaoSingleton
					.getGenericDao().findByQuery(
							ConstantQueries.GET_PARTICULAR_USER_FROM_UPM,
							queryMap);

			for (int i = 0; i < upms.size(); i++) {

				UserProjectMapping existingUPM = (UserProjectMapping) upms
						.get(i);

				existingUPM.setDefault_project("Y");

				UserProjectMapping userPM = (UserProjectMapping) GenericDaoSingleton
						.getGenericDao().createOrUpdate(existingUPM);
				if (userPM != null) {
					logger.info("User updated Successfully");

				} else {
					return Response.status(Response.Status.BAD_REQUEST)
							.entity(userPM).build();
				}

			}

			if (upms.size() == 0) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Error in Updating default project").build();
			} else {
				return Response.status(Response.Status.OK)
						.entity("Successfully Updated").build();

			}

		} catch (Exception e) {
			logger.error("Failed to update User");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to update User").build();
		}

	}

		
	// ***********************To get particular USERr*****************

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path("fetchParticular/{userId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchParticular(@PathParam("userId") String userId) {

		logger.info("Fetching Particular User " + userId);

		UserMaster userMaster;
		UserTO userTO = null;

		try {

			HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
			keyvalueMap.put("userId", userId);
			userMaster = (UserMaster) GenericDaoSingleton.getGenericDao()
					.findUniqueByQuery(ConstantQueries.GET_PARTICULAR_USER,
							keyvalueMap);

			if (userMaster == null) {
				logger.error("User doesnt exist");

				return Response.status(Response.Status.NOT_FOUND)
						.entity("User doesnt exist").build();
			}

			userTO = new UserTO();
			// projectMaster = (Object[]) projectList.get(i);
			userTO.setUser_id(userMaster.getUser_id());
			userTO.setName(userMaster.getName());
			userTO.setPassword(userMaster.getPassword());
//			userTO.setRole_name("");
//			userTO.setProject_name("");

			logger.info("Particular user fetched successfully");
			return Response.status(Response.Status.OK).entity(userTO).build();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in getting users in Portfolio List");
			userTO = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(userTO).build();
		}

	}

	// ********FETCHING USERS ALLOCATEED PROJECTS 23/1/18 ***********

	/**
	 * @author 10643331(Sueanne Alphonso)
	 * */
	@GET
	@Path("getAllocatedProjects/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllocatedProjects(@PathParam("userId") String userId) {

		logger.debug("Fetching Allocated Projects for userId: " + userId);

		List<ProjectTO> projectMasterList = new ArrayList<ProjectTO>();
		Object projectMaster[];
		ProjectTO projectTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();

			queryMap.put("userId", userId);

			List<Object> projectList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALLOCATED_PROJECTS,
							queryMap);

			if (projectList == null || projectList.size() == 0
					|| projectList.isEmpty() == true) {
				// System.out.println("Role not found");

				logger.error("Projects Not Found");

				projectTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(projectTO).build();
			}

			for (int i = 0; i < projectList.size(); i++) {
				projectTO = new ProjectTO();
				projectMaster = (Object[]) projectList.get(i);
				projectTO.setProject_id((int) projectMaster[0]);
				projectTO.setProject_name((String) projectMaster[1]);
				// roleTO.setRoleName(roleMaster[1].toString());

				projectMasterList.add(projectTO);
			}

			logger.info("Projects Allocated fetched successfully");
			return Response.status(Response.Status.OK)
					.entity(projectMasterList).build();

		} catch (Exception e) {
			projectTO = null;
			// System.out.println("Error while getting users");

			logger.error("Error while getting Allocated Projects for Particular User");

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(projectTO).build();
		}

	}
	
	/**
	 * @author 10643380( Rahul Bhardwaj)
	 * */
	@GET
	@Path("getAllocatedProjectsAndRoles/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllocatedProjectsAndRoles(@PathParam("userId") String userId) {
		String query="select p.project_name,u.userMaster.user_id,r.role_name from UserProjectMapping u,RoleMaster r,ProjectMaster p " + 
					  "where u.userMaster.user_id=:userId and p.project_id=u.projectMaster.project_id and r.role_id=u.roleMaster.role_id";
		
		List<UserTO> userTOList=new ArrayList<UserTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		GenericDao genericDao=GenericDaoSingleton.getGenericDao();
		try {
			keyvalueMap.put("userId", userId);
			List <Object> userProjectsRolesList=genericDao.findByQuery(query,keyvalueMap);
			if(userProjectsRolesList==null||userProjectsRolesList.size()==0||userProjectsRolesList.isEmpty()==true) {
				logger.debug("no user with project or roles found");
				userTOList=null;
				return Response.status(Response.Status.NOT_FOUND).entity(userTOList).build();
			}
			
			Iterator<Object> it=userProjectsRolesList.iterator();
			while(it.hasNext()) {
				UserTO userTO=new UserTO();
				Object[] userProjectsRoles=(Object[]) it.next();
				userTO.setProject_name(String.valueOf(userProjectsRoles[0]));
				userTO.setUser_id(String.valueOf(userProjectsRoles[1]));
				userTO.setRole_name(String.valueOf(userProjectsRoles[2]));
				userTOList.add(userTO);
			}
			return Response.status(Response.Status.OK).entity(userTOList).build();
			
		}catch(Exception e){
			e.printStackTrace();
			userTOList=null;
			return Response.status(Response.Status.NOT_FOUND).entity(userTOList).build();
		}
	}
	
	/**
	 * @author 10643380( Rahul Bhardwaj)
	 * */
	@GET
	@Path("getAllocatedProjectsAndRolesArray/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllocatedProjectsAndRolesArray(@PathParam("userId") String userId) {
		String query="select p.project_name,u.userMaster.user_id,r.role_name,p.project_id,u.default_project from UserProjectMapping u,RoleMaster r,ProjectMaster p " + 
					  "where u.userMaster.user_id=:userId and p.project_id=u.projectMaster.project_id and r.role_id=u.roleMaster.role_id";
		
		List<UserWithRoleArrayTO> userWithRoleArrayTOList=new ArrayList<UserWithRoleArrayTO>();
		List<UserTO> userTOList=new ArrayList<UserTO>();
		HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
		GenericDao genericDao=GenericDaoSingleton.getGenericDao();
		try {
			keyvalueMap.put("userId", userId);
			List <Object> userProjectsRolesList=genericDao.findByQuery(query,keyvalueMap);
			if(userProjectsRolesList==null||userProjectsRolesList.size()==0||userProjectsRolesList.isEmpty()==true) {
				logger.debug("no user with project or roles found");
				userTOList=null;
				userWithRoleArrayTOList=null;
				return Response.status(Response.Status.NOT_FOUND).entity(userWithRoleArrayTOList).build();
			}
			Set projectSet=new HashSet<String>();
			ArrayList<Integer> projectIdList=new ArrayList<Integer>();
			ArrayList<String> defaultProjectList=new ArrayList<String>();
			Iterator<Object> it=userProjectsRolesList.iterator();
			logger.debug("userProjectsRolesList size is "+userProjectsRolesList.size());
			while(it.hasNext()) {
				UserTO userTO=new UserTO();
				Object[] userProjectsRoles=(Object[]) it.next();
				userTO.setProject_name(String.valueOf(userProjectsRoles[0]));
				userTO.setUser_id(String.valueOf(userProjectsRoles[1]));
				userTO.setRole_name(String.valueOf(userProjectsRoles[2]));
				System.out.println("userTO is "+userTO.getUser_id()+" "+userTO.getProject_name()+" "+userTO.getRole_name());
				userTOList.add(userTO);
				projectSet.add(userTO.getProject_name());
				projectIdList.add(Integer.parseInt(String.valueOf(userProjectsRoles[3])));
				defaultProjectList.add(String.valueOf(userProjectsRoles[4]));
			}
			logger.debug("UserTO list "+userTOList.toString());
			Iterator<String>projectSetItr=projectSet.iterator();
			while(projectSetItr.hasNext()) {
				String projectName=projectSetItr.next();
				UserWithRoleArrayTO userWithRoleArrayTO=new UserWithRoleArrayTO();
				userWithRoleArrayTO.setRole_name(new HashSet<String>());
				int i=0;
				for(UserTO userTo:userTOList) {
					Integer projectId=projectIdList.get(i);
					String defaultProject=defaultProjectList.get(i);
					if(userTo.getProject_name().equalsIgnoreCase(projectName)) {
						System.out.println("userTO is "+userTo.getUser_id()+" "+userTo.getProject_name()+" "+userTo.getRole_name());
						userWithRoleArrayTO.setProject_name(projectName);
						userWithRoleArrayTO.setUser_id(userId);
						userWithRoleArrayTO.setProject_id(projectId);
						userWithRoleArrayTO.setDefault_project(defaultProject);
						Set<String> temp=userWithRoleArrayTO.getRole_name();
						temp.add(userTo.getRole_name());
					}
					i++;
				}
				userWithRoleArrayTOList.add(userWithRoleArrayTO);
			}
			return Response.status(Response.Status.OK).entity(userWithRoleArrayTOList).build();
			
		}catch(Exception e){
			e.printStackTrace();
			userTOList=null;
			return Response.status(Response.Status.NOT_FOUND).entity(userWithRoleArrayTOList).build();
		}
	}

	
	// ******************De-allocate roles of particular users****************

		@PUT
		@Path(value = "deallocate/{projectId}")
		@Produces(MediaType.TEXT_PLAIN)
		public Response deallocateRoles(Set<String> users,
				@PathParam("projectId") Integer projectId)
				throws Exception {

			if(users==null) {
				return Response.status(Response.Status.NO_CONTENT).entity("User set is null").build();
			}
			
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			
			for(String userId:users){
				queryMap.clear();
				queryMap.put("userId", userId);
				queryMap.put("projectId", projectId);
				try{
		
					List<Object> userList = GenericDaoSingleton.getGenericDao()
							.findByQuery(ConstantQueries.GET_USERS_TO_DEALLOCATE,
									queryMap);

					if (userList == null || userList.size() == 0
							|| userList.isEmpty() == true) {
						// System.out.println("Role not found");

						logger.error("User has no roles in particular project");

						//projectTO = null;
						return Response.status(Response.Status.OK)
								.entity("User has no roles").build();
					}

					for (int i = 0; i < userList.size(); i++) {
					
						int upm_id= (int) userList.get(i);
						
						GenericDaoSingleton.getGenericDao().delete(UserProjectMapping.class, upm_id);
						
					}
					

				}catch(Exception e){
					e.printStackTrace();
					logger.error("Error in Deallocating Roles");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Error Deallocating Roles").build();
				}
				
				
			}
			
			logger.info("Successfully Deallocated Roles for Particular Users");
			return Response.status(Response.Status.OK).entity("Successfully Deallocated Roles").build();
		

		}
		
	
	
}
