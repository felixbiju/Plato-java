package com.PLATO.Singletons;

import com.PLATO.dao.MongoDbGenericDAO;
import com.PLATO.daoimpl.MongoDbGenericDAOImpl;

public class MongoDBGenericDaoSingleton {

	
private static MongoDbGenericDAO mongoGenericDao;
	
	private MongoDBGenericDaoSingleton(){
		
	}
	public static MongoDbGenericDAO getGenericDao(){
		
	//	factory = new AnnotationConfiguration().configure().buildSessionFactory();
		
		
		if(mongoGenericDao==null){
			mongoGenericDao= new MongoDbGenericDAOImpl();
			return mongoGenericDao;
		}else{
			return mongoGenericDao;
		}
		
	}
}
