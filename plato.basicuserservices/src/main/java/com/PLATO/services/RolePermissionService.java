package com.PLATO.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.entities.PermissionModuleMaster;
import com.PLATO.entities.PermissionScreenMaster;
import com.PLATO.entities.RolePermissionMapping;
import com.PLATO.entities.ScreenFunctionality;
import com.PLATO.userTO.PermissionDataComponentTO;
import com.PLATO.userTO.PermissionDataModuleTO;
import com.PLATO.userTO.PermissionDataScreenTO;
import com.PLATO.userTO.PermissionDataTO;
import com.PLATO.userTO.PermissionModuleMasterTO;
import com.PLATO.userTO.PermissionScreenMasterTO;
import com.PLATO.userTO.ScreenFunctionalityTO;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Path("rolePermissionService")
public class RolePermissionService {
	
	@Path("getAllPermissionModuleMaster")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPermissionModuleMaster() {
		String query="from PermissionModuleMaster";
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		List<Object> permissionModuleMasterList=new ArrayList<Object>();
		List<PermissionModuleMasterTO> permissionModuleMasterTOList=new ArrayList<PermissionModuleMasterTO>();
		try {
			permissionModuleMasterList=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();
		}
		if(permissionModuleMasterList==null || permissionModuleMasterList.size()<=0 || permissionModuleMasterList.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();
		}
		for(int i=0;i<permissionModuleMasterList.size();i++) {
			PermissionModuleMaster  permissionModuleMaster=(PermissionModuleMaster)permissionModuleMasterList.get(i);
			PermissionModuleMasterTO permissionModuleMasterTo=new PermissionModuleMasterTO();
			permissionModuleMasterTo.setModuleChecked(permissionModuleMaster.getModule_checked());
			permissionModuleMasterTo.setPermissionModuleId(permissionModuleMaster.getPermission_module_id());
			permissionModuleMasterTo.setPermissionModuleName(permissionModuleMaster.getPermission_module_name());
			permissionModuleMasterTOList.add(permissionModuleMasterTo);
		}
		return Response.status(Response.Status.OK).entity(permissionModuleMasterTOList).build();
		
	}
	@Path("getAllPermissionScreenMaster")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPermissionScreenMaster() {
		String query="from PermissionScreenMaster";
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		List<Object> permissionScreenMasterList=new ArrayList<Object>();
		List<PermissionScreenMasterTO> permissionScreenMasterToList=new ArrayList<PermissionScreenMasterTO>();
		try {
			permissionScreenMasterList=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
			
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();
		}
		if(permissionScreenMasterList==null || permissionScreenMasterList.size()<=0 || permissionScreenMasterList.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();
		}
		for(int i=0;i<permissionScreenMasterList.size();i++) {
			PermissionScreenMaster permissionScreenMaster=(PermissionScreenMaster)permissionScreenMasterList.get(i);
			PermissionScreenMasterTO permissionScreenMasterTo=new PermissionScreenMasterTO();
			permissionScreenMasterTo.setPermissionModuleMaster(permissionScreenMaster.getPermissionModuleMaster());
			permissionScreenMasterTo.setScreenChecked(permissionScreenMaster.getScreen_checked());
			permissionScreenMasterTo.setScreenId(permissionScreenMaster.getScreen_id());
			permissionScreenMasterTo.setScreenName(permissionScreenMaster.getScreen_name());
			permissionScreenMasterToList.add(permissionScreenMasterTo);
		}
		return Response.status(Response.Status.OK).entity(permissionScreenMasterToList).build();
	}
	
	@Path("getAllScreenFunctionality")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllScreenFunctionality() {
		String query="from ScreenFunctionality";
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		List<Object> screenFunctionalityList=new ArrayList<Object>();
		List<ScreenFunctionalityTO> screenFunctionalityToList=new ArrayList<ScreenFunctionalityTO>();
		try {
			screenFunctionalityList=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();			
		}
		if(screenFunctionalityList==null || screenFunctionalityList.size()<=0 || screenFunctionalityList.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();
		}
		for(int i=0;i<screenFunctionalityList.size();i++) {
			ScreenFunctionality screenFunctionality=(ScreenFunctionality)screenFunctionalityList.get(i);
			ScreenFunctionalityTO screenFunctionalityTo=new ScreenFunctionalityTO();
			screenFunctionalityTo.setFuncBtn(screenFunctionality.getFunc_btn());
			screenFunctionalityTo.setFuncChecked(screenFunctionality.getFunc_checked());
			screenFunctionalityTo.setFuncName(screenFunctionality.getFunc_name());
			screenFunctionalityTo.setPermissionScreenMaster(screenFunctionality.getPermissionScreenMaster());
			screenFunctionalityTo.setScreenFunctionalityId(screenFunctionality.getScreen_functionality_id());
			screenFunctionalityToList.add(screenFunctionalityTo);
		}
		return Response.status(Response.Status.OK).entity(screenFunctionalityToList).build();
	}
	
//	@Path("createRolePermission")
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response createRolePermission(ArrayList<RolePermissionMapping>rolePermissionMappingList) {
//		for(RolePermissionMapping rolePermissionMapping : rolePermissionMappingList) {
//			try {
//				GenericDaoSingleton.getGenericDao().createOrUpdate(rolePermissionMapping);
//			}catch(Exception e) {
//				e.printStackTrace();
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error in creation").build();
//			}
//			
//		}
//		return Response.status(Response.Status.OK).entity("created successfully").build();
//	}
	
	@Path("createRolePermission/{roleId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRolePermission(@PathParam("roleId") int roleId,PermissionDataTO permissionDataTO) {
		List<PermissionDataModuleTO> permissionDataModuleTOList=permissionDataTO.getPermissionData();
		for(PermissionDataModuleTO permissionDataModuleTO:permissionDataModuleTOList) {
			List<PermissionDataScreenTO>permissionDataScreenTOList=permissionDataModuleTO.getScreens();
			for(PermissionDataScreenTO permissionDataScreenTO:permissionDataScreenTOList) {
				List<PermissionDataComponentTO>permissionDataComponentTOList=permissionDataScreenTO.getComponents();
				for(PermissionDataComponentTO permissionDataComponentTO:permissionDataComponentTOList) {
					String query="from ScreenFunctionality where func_name=:func_name and permissionScreenMaster.screen_name=:screen_name";
					String func_name=permissionDataComponentTO.getName();
					String screen_name=permissionDataScreenTO.getScreenName();
					HashMap<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("func_name", func_name);
					queryMap.put("screen_name", screen_name);
					System.out.println("func_name : "+func_name+" screen_name : "+screen_name);
					ScreenFunctionality screenFunctionality=new ScreenFunctionality();
					try {
						screenFunctionality=(ScreenFunctionality)GenericDaoSingleton.getGenericDao().findUniqueByQuery(query,queryMap);
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					RolePermissionMapping rolePermissionMapping=new RolePermissionMapping();
					rolePermissionMapping.setPermission(permissionDataComponentTO.getChecked());
					rolePermissionMapping.setRole_id(roleId);
					rolePermissionMapping.setScreenFunctionality(screenFunctionality);
					try {
						GenericDaoSingleton.getGenericDao().createOrUpdate(rolePermissionMapping);
					}catch(Exception e) {

					}
				}
			}
		}
		return Response.status(Response.Status.OK).entity(permissionDataTO).build();
	}
	
	@Path("deleteAll/{roleId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRolePermissionMapping(@PathParam("roleId") int roleId) {
		String query ="from RolePermissionMapping where role_id=:roleId";
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		List<Object> rolePermissionMappingList=new ArrayList<Object>();
		queryMap.put("roleId", roleId);
		try {
			rolePermissionMappingList=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
			for(int i=0;i<rolePermissionMappingList.size();i++) {
				RolePermissionMapping rolePermissionMapping=(RolePermissionMapping)rolePermissionMappingList.get(i);
				GenericDaoSingleton.getGenericDao().delete(RolePermissionMapping.class,rolePermissionMapping.getId());
			}
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("failed: could not delete").build();
		}
		
		return Response.status(Response.Status.OK).entity("successfully deleted").build();
	}
	
	@Path("getPermissions/{roleId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPermissions(@PathParam("roleId") int roleId) {
		String query ="from RolePermissionMapping where role_id=:roleId";
		//String query="select id,roleMaster.role_id,screenFunctionality.screen_functionality_id,permission from RolePermissionMapping where roleMaster.role_id=:roleId";
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		List<Object> rolePermissionMappingList=new ArrayList<Object>();
		queryMap.put("roleId", roleId);
		PermissionDataTO permissionDataTo=new PermissionDataTO();
		try {
			rolePermissionMappingList=GenericDaoSingleton.getGenericDao().findByQuery(query,queryMap);
			for(int i=0;i<rolePermissionMappingList.size();i++) {
				RolePermissionMapping rolePermissionMapping=(RolePermissionMapping)rolePermissionMappingList.get(i);
				ScreenFunctionality screenFunctionality=rolePermissionMapping.getScreenFunctionality();
				PermissionScreenMaster permissionScreenMaster=screenFunctionality.getPermissionScreenMaster();
				PermissionModuleMaster permissionModuleMaster=permissionScreenMaster.getPermissionModuleMaster();
				List<PermissionDataModuleTO> permissionDataModuleTOList=permissionDataTo.getPermissionData();
				if(permissionDataModuleTOList==null || permissionDataModuleTOList.isEmpty() || permissionDataModuleTOList.size()<=0) {
					permissionDataTo.setPermissionData(new ArrayList<PermissionDataModuleTO>());
					addNewPermissionDataModule(rolePermissionMapping,permissionDataTo);
					}else {
					for(int j=0;j<permissionDataModuleTOList.size();j++) {
						if(permissionDataModuleTOList.get(j).getModuleName().equalsIgnoreCase(permissionModuleMaster.getPermission_module_name())) {
							List<PermissionDataScreenTO> permissionDataScreenTOList=permissionDataModuleTOList.get(j).getScreens();
							for(int k=0;k<permissionDataScreenTOList.size();k++) {
								if(permissionDataScreenTOList.get(k).getScreenName().equalsIgnoreCase(permissionScreenMaster.getScreen_name())) {
									PermissionDataScreenTO permissionDataScreenTO=permissionDataScreenTOList.get(k);
									PermissionDataComponentTO permissionDataComponentTO=new PermissionDataComponentTO();
									
									permissionDataComponentTO.setChecked(rolePermissionMapping.getPermission());
									permissionDataComponentTO.setName(screenFunctionality.getFunc_name());
									
									permissionDataScreenTO.getComponents().add(permissionDataComponentTO);
									break;
								}else if(k>=permissionDataScreenTOList.size()-1) {
									PermissionDataScreenTO permissionDataScreenTO=new PermissionDataScreenTO();
									permissionDataScreenTO.setScreenName(permissionScreenMaster.getScreen_name());
									permissionDataScreenTO.setComponents(new ArrayList<PermissionDataComponentTO>());
									List<PermissionDataComponentTO> permissionDataComponentTOList=permissionDataScreenTO.getComponents();
									PermissionDataComponentTO permissionDataComponentTO=new PermissionDataComponentTO();
									permissionDataComponentTO.setChecked(rolePermissionMapping.getPermission());
									permissionDataComponentTO.setName(screenFunctionality.getFunc_name());
									permissionDataComponentTOList.add(permissionDataComponentTO);
									
									permissionDataScreenTOList.add(permissionDataScreenTO);
									break;
								}
							}
							break;
						}else if(j>=permissionDataModuleTOList.size()-1) {
							addNewPermissionDataModule(rolePermissionMapping,permissionDataTo);
							break;
						}
					}
				}
			}
//			for(int i=0;i<rolePermissionMappingList.size();i++) {
//				RolePermissionMapping r=(RolePermissionMapping)rolePermissionMappingList.get(i);
//				Hibernate.initialize(r.getRoleMaster());
//			}
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();		
		}
		
		return Response.status(Response.Status.OK).entity(permissionDataTo).build();
		
	}
	
	@Path("getAllPermissions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPermissions() {
		PermissionDataTO permissionDataTo=new PermissionDataTO();
		permissionDataTo.setPermissionData(new ArrayList<PermissionDataModuleTO>());
		
		String query1="from PermissionModuleMaster order by permission_module_id";
		String query2="from PermissionScreenMaster order by permissionModuleMaster.permission_module_id,screen_id";
		String query3="from ScreenFunctionality order by permissionScreenMaster.permissionModuleMaster.permission_module_id,permissionScreenMaster.screen_id";
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		List<Object> screenFunctionalityList=new ArrayList<Object>();
		List<Object> permissionScreenMasterList=new ArrayList<Object>();
		List<Object> permissionModuleMasterList=new ArrayList<Object>();
		try {
			permissionModuleMasterList=GenericDaoSingleton.getGenericDao().findByQuery(query1,queryMap);
			permissionScreenMasterList=GenericDaoSingleton.getGenericDao().findByQuery(query2,queryMap);
			screenFunctionalityList=GenericDaoSingleton.getGenericDao().findByQuery(query3,queryMap);			
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity(null).build();			
		}
		System.out.println(" permissionModuleMasterList.size() "+permissionModuleMasterList.size());
		System.out.println("permissionScreenMasterList.size() "+permissionScreenMasterList.size());
		System.out.println("screenFunctionalityList.size() "+screenFunctionalityList.size());
		for(int i=0;i<permissionModuleMasterList.size();i++) {
			PermissionModuleMaster permissionModuleMaster=(PermissionModuleMaster)permissionModuleMasterList.get(i);
			System.out.println("permission "+permissionModuleMaster.getPermission_module_id()+" module name "+permissionModuleMaster.getPermission_module_name());
		}
		
		for(int i=0;i<permissionScreenMasterList.size();i++) {
			PermissionScreenMaster permissionScreenMaster=(PermissionScreenMaster)permissionScreenMasterList.get(i);
			System.out.println("screen id "+permissionScreenMaster.getScreen_id()+" module id "+permissionScreenMaster.getPermissionModuleMaster().getPermission_module_id()+
					" screen name "+permissionScreenMaster.getScreen_name());
		}
		
		for(int i=0;i<screenFunctionalityList.size();i++) {
			ScreenFunctionality screenFunctionality=(ScreenFunctionality)screenFunctionalityList.get(i);
			System.out.println("screen functionality id "+screenFunctionality.getScreen_functionality_id()+" screen id "+screenFunctionality.getPermissionScreenMaster().getScreen_id()+
					" module id "+screenFunctionality.getPermissionScreenMaster().getPermissionModuleMaster().getPermission_module_id()+" screen func name "+screenFunctionality.getFunc_name());
		}
		
		int j=0, k=0;
		for(int i=0;i<permissionModuleMasterList.size();i++) {
			PermissionModuleMaster permissionModuleMaster=(PermissionModuleMaster)permissionModuleMasterList.get(i);
			List<PermissionDataModuleTO> permissionDataModuleTOList=permissionDataTo.getPermissionData();
			PermissionDataModuleTO permissionDataModuleTO=new PermissionDataModuleTO();
			permissionDataModuleTO.setModuleName(permissionModuleMaster.getPermission_module_name());
			permissionDataModuleTO.setScreens(new ArrayList<PermissionDataScreenTO>());
			permissionDataModuleTO.setChecked(true);
			permissionDataModuleTOList.add(permissionDataModuleTO);
			
			PermissionScreenMaster permissionScreenMaster=null;
			if(j<permissionScreenMasterList.size())
				permissionScreenMaster=(PermissionScreenMaster)permissionScreenMasterList.get(j);
			while(j<permissionScreenMasterList.size() && 
					permissionScreenMaster.getPermissionModuleMaster().getPermission_module_id()==permissionModuleMaster.getPermission_module_id()) {
				List<PermissionDataScreenTO>PermissionDataScreenTOList= permissionDataModuleTO.getScreens();
				PermissionDataScreenTO permissionDataScreenTO=new  PermissionDataScreenTO();
				permissionDataScreenTO.setScreenName(permissionScreenMaster.getScreen_name());
				permissionDataScreenTO.setComponents(new ArrayList<PermissionDataComponentTO>());
				permissionDataScreenTO.setChecked(true);
				PermissionDataScreenTOList.add(permissionDataScreenTO);
				ScreenFunctionality screenFunctionality=null;
				if(k<screenFunctionalityList.size())
					screenFunctionality=(ScreenFunctionality)screenFunctionalityList.get(k);
				while(k<screenFunctionalityList.size() && screenFunctionality.getPermissionScreenMaster().getScreen_id()==permissionScreenMaster.getScreen_id()) {
					List<PermissionDataComponentTO> permissionDataComponentTOList=permissionDataScreenTO.getComponents();
					PermissionDataComponentTO permissionDataComponentTO=new PermissionDataComponentTO();
					permissionDataComponentTO.setChecked(true);
					permissionDataComponentTO.setName(screenFunctionality.getFunc_name());
					permissionDataComponentTOList.add(permissionDataComponentTO);
					k++;
					if(k<screenFunctionalityList.size())
						screenFunctionality=(ScreenFunctionality)screenFunctionalityList.get(k);
				}
				j++;
				if(j<permissionScreenMasterList.size())
					permissionScreenMaster=(PermissionScreenMaster)permissionScreenMasterList.get(j);
			}
			}
		return Response.status(Response.Status.OK).entity(permissionDataTo).build();
	}
	
	
	public void addNewPermissionDataModule(RolePermissionMapping rolePermissionMapping,PermissionDataTO permissionDataTo) {
		ScreenFunctionality screenFunctionality=rolePermissionMapping.getScreenFunctionality();
		PermissionScreenMaster permissionScreenMaster=screenFunctionality.getPermissionScreenMaster();
		PermissionModuleMaster permissionModuleMaster=permissionScreenMaster.getPermissionModuleMaster();
		
		List<PermissionDataModuleTO> permissionDataModuleTOList=permissionDataTo.getPermissionData();
		
		PermissionDataModuleTO permissionDataModuleTO=new PermissionDataModuleTO();
		permissionDataModuleTO.setModuleName(permissionModuleMaster.getPermission_module_name());
		permissionDataModuleTO.setScreens(new ArrayList<PermissionDataScreenTO>());
		permissionDataModuleTOList.add(permissionDataModuleTO);
		
		PermissionDataScreenTO permissionDataScreenTO=new PermissionDataScreenTO();
		permissionDataScreenTO.setScreenName(permissionScreenMaster.getScreen_name());
		permissionDataScreenTO.setComponents(new ArrayList<PermissionDataComponentTO>());
		
		List<PermissionDataComponentTO> permissionDataComponentTOList=permissionDataScreenTO.getComponents();
		PermissionDataComponentTO permissionDataComponentTO=new PermissionDataComponentTO();
		permissionDataComponentTO.setName(screenFunctionality.getFunc_name());
		permissionDataComponentTO.setChecked(rolePermissionMapping.getPermission());
		permissionDataComponentTOList.add(permissionDataComponentTO);
		
		List<PermissionDataScreenTO> permissionDataScreenTOList=permissionDataModuleTO.getScreens();
		permissionDataScreenTOList.add(permissionDataScreenTO);
		
		
		
	}
	
	

}
