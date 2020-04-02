package com.PLATO.services;

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

import org.apache.log4j.Logger;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.RoleMaster;
import com.PLATO.userTO.RoleTO;

@Path("RoleConfigService")
public class RoleConfigService {
	

	private static final Logger logger=Logger.getLogger(RoleConfigService.class);
	
	//*********************************get all roles 18/12/17*************************
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllRoles() {

		List<RoleTO> roleMasterList = new ArrayList<RoleTO>();
		RoleMaster roleMaster;
		RoleTO roleTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> roleList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_ROLES, queryMap);

			if (roleList == null || roleList.size() == 0
					|| roleList.isEmpty() == true) {
				System.out.println("Roles not found");
				roleTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(roleTO).build();
			}

			for (int i = 0; i < roleList.size(); i++) {
				roleTO = new RoleTO();
				roleMaster =  (RoleMaster) roleList.get(i);
				roleTO.setRoleId(roleMaster.getRole_id());
				roleTO.setRoleName(roleMaster.getRole_name());
				

				roleMasterList.add(roleTO);
			}

			return Response.status(Response.Status.OK).entity(roleMasterList).build();

		} catch (Exception e) {
			roleTO = null;
			//System.out.println("Error while getting users");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(roleTO).build();
		}

	}
	
	
	
	
	//***************************************Create Roles 18/12/17**********************************************

		@POST
		@Path(value = "create/")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response createUser(RoleMaster roleMaster) throws Exception {

			try {
				RoleMaster rolemaster = (RoleMaster) GenericDaoSingleton
					.getGenericDao().createOrUpdate(roleMaster);

			if (rolemaster != null) {
				return Response.status(Response.Status.OK)
						.entity("Role Created Successfully").build();
			} else {
				return Response.status(Response.Status.OK)
						.entity("Role Creation Failed").build();
			}
			}catch (Exception e) {			
				
				System.out.println(e.getCause());
				System.out.println(e.getMessage());
				if(e.getMessage().equals("Duplicate Row in table"))
				{
					return Response.status(Response.Status.CONFLICT).entity("Role Already Exists").build();
				}
				
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Creating Role").build();

			}

		}
		
		
		//***********************Delete Role********************

		/*@DELETE
		@Path(value = "/delete/{roleId}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteUser(@PathParam("roleId") String roleId) {
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("roleId", roleId);

			try {
				
				
				System.out.println("inside update deletRole");
							

				RoleMaster roleMaster = (RoleMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(
								ConstantQueries.GET_PARTICULAR_ROLE, queryMap);
				RoleMaster roleMaster = (RoleMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(
								ConstantQueries.GET_PARTICULAR_ROLE, queryMap);
				
				
				
				
				//RoleMaster roleMaster = (RoleMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(ConstantQueries.GET_PARTICULAR_ROLE, queryMap);

				if (roleMaster == null) {
					System.out.println("Role Does Not Exists");
					roleMaster = null;
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Role Does Not Exists").build();
				}

				GenericDaoSingleton.getGenericDao().delete(UserMaster.class,
						roleMaster.getRole_id());

				return Response.status(Response.Status.OK)
						.entity("Role Deleted Successfully").build();
			} catch (Exception e) {

				System.out
						.println("Error while getting role in Role Config service");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Role Deletion Failed").build();
			}

		}
*/
		
		
		
		
		@DELETE
		@Path(value = "/delete/{roleId}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteRolee(@PathParam("roleId") int roleId) {
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("roleId", roleId);

			try {

				System.out.println("inside update deleteroleId");
				RoleMaster roleMaster = (RoleMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(
								ConstantQueries.GET_PARTICULAR_ROLE, queryMap);

				if (roleMaster == null) {
					System.out.println("Role Does Not Exists");
					roleMaster = null;
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Project Does Not Exists").build();
				}

				GenericDaoSingleton.getGenericDao().delete(RoleMaster.class,
						roleMaster.getRole_id());

				return Response.status(Response.Status.OK)
						.entity("Project Deleted Successfully").build();
			} catch (Exception e) {

				System.out
						.println("Error while getting projects in Project Config service");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Project Deletion Failed").build();
			}

		}
		
		
		
		

		//***********************Update Role*****************

		@PUT
		@Path(value = "update/")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response updateRole(RoleMaster roleMaster) {
			
			logger.debug("Updating Role "+roleMaster.getRole_id());
			try {
				HashMap<String, Object> queryMap = new HashMap<String, Object>();
				queryMap.put("roleId", roleMaster.getRole_id());
				RoleMaster existingRoleMaster = (RoleMaster) GenericDaoSingleton
						.getGenericDao().findUniqueByQuery(
								ConstantQueries.GET_PARTICULAR_ROLE, queryMap);

				if (existingRoleMaster == null) {
					
					logger.error("Role Does Not Exists");
					//System.out.println("User Does Not Exists");
					existingRoleMaster = null;
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Role Does Not Exists").build();
				}

				existingRoleMaster.setRole_id(roleMaster.getRole_id());
				existingRoleMaster.setRole_name(roleMaster.getRole_name());
				
				

				RoleMaster roleMstr = (RoleMaster) GenericDaoSingleton
						.getGenericDao().createOrUpdate(existingRoleMaster);

				if (roleMstr != null) {
					logger.info("Role updated Successfully");
					return Response.status(Response.Status.OK)
							.entity("Role updated Successfully").build();
				} else {
					return Response.status(Response.Status.BAD_REQUEST)
							.entity(roleMstr).build();

				}
			} catch (Exception e) {
				logger.error("Failed to update Role");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Failed to update Role").build();
			}

		}
		
		
		
		
}
