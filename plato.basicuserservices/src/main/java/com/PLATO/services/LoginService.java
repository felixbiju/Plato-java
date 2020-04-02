package com.PLATO.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.constants.GlobalConstants;
import com.PLATO.dao.GenericDao;
import com.PLATO.entities.UserMaster;
import com.PLATO.userTO.ValidateUserTO;


@Path("LoginService")
public class LoginService 
{

	/*
	 * Description : Service to Validate user and return token on successful authentication
	 * Author: Gaurav Kulkarni.
	 * */

	private static final Logger logger=Logger.getLogger(LoginService.class);
	private GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	
	
	@POST
	@Path("Login")
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//When user submits login this service is invoked. This service validates the user credentials
	public Response validateUserWithoutDefaultProject(UserMaster user)
	{  	
		logger.info("Inside function validateUser");
		logger.debug("Received UserMaster object as parameter with username as :"+user.getUser_id());
		JSONObject tokenJson=new JSONObject();
		String jwt = null;
		long currentTime=System.currentTimeMillis(); 
		HashMap<String,Object> queryMap=new HashMap<String,Object >();
		/*List<String> roleList=new ArrayList<String>();
		roleList.add("admin");
		roleList.add("user");*/

		try
		{
			queryMap.put("user_id",user.getUser_id());
			queryMap.put("password",user.getPassword());
			//queryMap.put("default_project","Y");

			//UserMaster fetchedUser=(UserMaster) genericDao.findUniqueByQuery(ConstantQueries.VALIDATEUSERQUERY, queryMap);
			List<Object> fetchedUsers= genericDao.findByQuery(ConstantQueries.VALIDATEUSER, queryMap);
			
			
	
			if(fetchedUsers==null||fetchedUsers.isEmpty())
			{
				logger.error("Login failed");
				return Response.status(Response.Status.NOT_FOUND).entity(tokenJson).build();
			}
			else
			{
				for(int i=0;i<fetchedUsers.size();i++){
					Object[] defaultProjectUser=(Object[]) fetchedUsers.get(i);
					
					if(!defaultProjectUser[7].toString().equals("")){
						
					if(defaultProjectUser[7].toString().equalsIgnoreCase("Y")){
						
						logger.info("Login Successful");
						
						//fetchedUser=(Object[]) fetchedUserSet.get(0);
						ValidateUserTO validateUserTo=new ValidateUserTO();			
						String userName=defaultProjectUser[0].toString();	
						validateUserTo.setProject_id(Integer.parseInt(defaultProjectUser[1].toString()));
						validateUserTo.setProject_name(defaultProjectUser[2].toString());
						validateUserTo.setAccount_id(Integer.parseInt(defaultProjectUser[3].toString()));
						validateUserTo.setAccount_name(defaultProjectUser[4].toString());
						validateUserTo.setPortfolio_id(Integer.parseInt(defaultProjectUser[5].toString()));
						validateUserTo.setPortfolio_name(defaultProjectUser[6].toString());

						jwt=Jwts.builder()
								.setIssuer("LTI PLATO")
								.setSubject("Authentication Token for "+userName)
								.setIssuedAt(new Date(currentTime))
								.setExpiration(new Date(currentTime+12000000))
								.claim("name", userName)
								.claim("email", user.getUser_id())
								.signWith(SignatureAlgorithm.HS256, GlobalConstants.SECRET)
								.compact();
						logger.debug("JWT is :"+jwt);
						tokenJson.put("success","true");
						tokenJson.put("JWT", jwt);
						tokenJson.put("userName",userName);
						tokenJson.put("defaultProjectDetails", validateUserTo);
					
						logger.info("Returning jwt after successful login");
						return Response.status(Response.Status.OK).entity(tokenJson).build();
						
						
					
					}
					
				}else{

					
					logger.info("Login Successful");
					
					//fetchedUser=(Object[]) fetchedUserSet.get(0);
					ValidateUserTO validateUserTo=new ValidateUserTO();	
					Object[] fetUser=(Object[]) fetchedUsers.get(0);
					String userName=fetUser[0].toString();	
					validateUserTo.setProject_id(Integer.parseInt(fetUser[1].toString()));
					validateUserTo.setProject_name(fetUser[2].toString());
					validateUserTo.setAccount_id(Integer.parseInt(fetUser[3].toString()));
					validateUserTo.setAccount_name(fetUser[4].toString());
					validateUserTo.setPortfolio_id(Integer.parseInt(fetUser[5].toString()));
					validateUserTo.setPortfolio_name(fetUser[6].toString());

					jwt=Jwts.builder()
							.setIssuer("LTI PLATO")
							.setSubject("Authentication Token for "+userName)
							.setIssuedAt(new Date(currentTime))
							.setExpiration(new Date(currentTime+12000000))
							.claim("name", userName)
							.claim("email", user.getUser_id())
							.signWith(SignatureAlgorithm.HS256, GlobalConstants.SECRET)
							.compact();
					logger.debug("JWT is :"+jwt);
					tokenJson.put("success","true");
					tokenJson.put("JWT", jwt);
					tokenJson.put("userName",userName);
					tokenJson.put("defaultProjectDetails", validateUserTo);
					
					logger.info("Returning jwt after successful login");
					return Response.status(Response.Status.OK).entity(tokenJson).build();
					
					
					
					
				
					
					
				}
					
				}//end of for loop
				
				logger.error("Login Failed");
				tokenJson.put("success","false");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(tokenJson).build();
			
			}
			
		
		}catch(Exception e)
		{
			logger.error("Login Failed with exception :"+e);
			tokenJson.put("success","false");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(tokenJson).build();
		}


		
		
	}
}
