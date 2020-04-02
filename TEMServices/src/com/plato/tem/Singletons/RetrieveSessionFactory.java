package com.plato.tem.Singletons;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class RetrieveSessionFactory {

	private static final Logger logger=Logger.getLogger(RetrieveSessionFactory.class);
	
	private static SessionFactory factory;
	
	private RetrieveSessionFactory(){
		
	}
	public static SessionFactory getSessionFactory(){
		
	//	factory = new AnnotationConfiguration().configure().buildSessionFactory();
		
		
		if(factory==null){
			
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
					configuration.getProperties()).build();
			factory = configuration.buildSessionFactory(serviceRegistry);
			return factory;
		}else{
			return factory;
		}
		
	}
}
