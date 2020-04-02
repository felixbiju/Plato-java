package com.mongo.singletons;

import com.mongo.dao.GenericDao;
import com.mongo.dao.GenericDaoImpl;


public class GenericDaoSingleton {

	
private static GenericDao genericDao;
	
	private GenericDaoSingleton(){
		
	}
	public static GenericDao getGenericDao(){
		
	//	factory = new AnnotationConfiguration().configure().buildSessionFactory();
		
		
		if(genericDao==null){
			genericDao= new GenericDaoImpl();
			return genericDao;
		}else{
			return genericDao;
		}
		
	}
}
