package com.PLATO.Singletons;

import com.PLATO.dao.GenericDao;
import com.PLATO.daoimpl.GenericDaoImpl;

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
