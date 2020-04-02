package com.PLATO.security;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;


public class CorsFilterSpecification implements ContainerResponseFilter{

	@Override
	public void filter(ContainerRequestContext requestContext,ContainerResponseContext responseContext) throws IOException
	{
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.add("Access-Control-Allow-Origin", "172.25.*.*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");			
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, origin, accept, authorization");
		
		MultivaluedMap<String, String> reqHeaders = requestContext.getHeaders();
		headers.add("Referrer-Policy", "same-origin");
	}
}
