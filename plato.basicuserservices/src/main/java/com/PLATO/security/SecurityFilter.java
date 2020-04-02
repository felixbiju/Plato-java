package com.PLATO.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.PLATO.constants.GlobalConstants;

@Provider
public class SecurityFilter implements Filter
{

	/*
	 * Description : Service to validate Authentication key for each request
	 * Author: Harsh Mathur
	 * 
	 * /


	@Context
	private ResourceInfo resourceInfo;
	private static final String AUTHORIZATION_PROPERTY = "Authorization";	
	private static final String HEADER_PREFIX="Bearer";

	private static final Logger logger=Logger.getLogger(SecurityFilter.class);

//	@Override
//	public void filter(ContainerRequestContext requestContext)
//	{
//		logger.info("Inside Security");
//		String jwt=null;
//		//int found=0;
//
//		Method method = resourceInfo.getResourceMethod();
//
//		if( !method.isAnnotationPresent( PermitAll.class ) )
//		{
//			// nobody can access
//			if( method.isAnnotationPresent( DenyAll.class ) ) 
//			{	          
//				requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(GlobalConstants.FORBIDDEN).build());
//				return;
//			}
//
//			final MultivaluedMap<String, String> headers = requestContext.getHeaders();
//
//			//Fetch authorization header
//			final List<String> authProperty = headers.get(AUTHORIZATION_PROPERTY);
//
//			// block access if no authorization information is provided
//			if( authProperty == null || authProperty.isEmpty() )
//			{
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				return;
//
//			}
//
//
//			jwt = authProperty.get(0).trim();
//
//			if (jwt.length() < HEADER_PREFIX.length()) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				return;
//			}
//
//			jwt = jwt.substring(HEADER_PREFIX.length(), jwt.length()).trim();
//
//			try 
//			{
//				logger.info("Validating Token");
//				Claims claims = Jwts.parser()
//						.setSigningKey(GlobalConstants.SECRET)
//						.parseClaimsJws(jwt)
//						.getBody();
//
//				logger.debug("User Email :"+claims.get("email"));
//				//System.out.println("Roles permitted :"+claims.get("roles"));
//
//				//requestContext.getHeaders().add("roles",claims.get("roles").toString());
//				requestContext.getHeaders().add("email",claims.get("email").toString());
//
//				//List<String> allowedRoleList =(List<String>) claims.get("roles");
//
//			/*	if( method.isAnnotationPresent( RolesAllowed.class ) )
//				{
//					// get annotated roles
//					RolesAllowed rolesAnnotation = method.getAnnotation( RolesAllowed.class );
//					List<String> tokenRolesList = Arrays.asList( rolesAnnotation.value());
//
//					for(String role:allowedRoleList)
//					{
//						if(tokenRolesList.contains(role))
//						{
//							found=1;
//							break;
//						}
//					}
//
//					if(found==0)
//					{
//						requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.LOGIN_REQUIRED).build());            
//						return;
//					}
//				}*/
//
//
//			} 
//			catch (ExpiredJwtException e) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.LOGIN_REQUIRED).build());            
//				e.printStackTrace();
//				return;
//
//			} catch (UnsupportedJwtException e) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				e.printStackTrace();
//				return;
//
//			} catch (MalformedJwtException e) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				e.printStackTrace();
//				return;
//
//			} catch (SignatureException e) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				e.printStackTrace();
//				return;
//
//			} catch (IllegalArgumentException e) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				e.printStackTrace();
//				return;
//
//			} catch (Exception e) {
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(GlobalConstants.UNAUTHORIZED).build());
//				e.printStackTrace();
//				logger.error("Exception in parsing jwt");
//				return;
//			}
//
//		}

//	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
			throws IOException, ServletException {
		
//		 Claims claims = Jwts.parser().setSigningKey(GlobalConstants.SecretAuthKey).parseClaimsJws(GlobalConstants.AuthToken).getBody();
//		
//		 System.out.println(claims.get("plato_basicuserservices"));
		// pass the request along the filter chain
		
		next.doFilter(request, response);
		HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
      
        httpServletResponse.addHeader("Referrer-Policy", "same-origin");
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {		
	}

}
