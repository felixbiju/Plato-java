package com.PLATO.Singletons;

import com.PLATO.dao.JenkinsDao;
import com.PLATO.daoimpl.JenkinsDaoImpl;

public class JenkinsDaoSingleton {

	
private static JenkinsDao jenkinsDao;
	
	private JenkinsDaoSingleton(){
		
	}
	public static JenkinsDao getJenkinsDao(){
		
	//	factory = new AnnotationConfiguration().configure().buildSessionFactory();
		
		
		if(jenkinsDao==null){
			jenkinsDao= new JenkinsDaoImpl();
			return jenkinsDao;
		}else{
			return jenkinsDao;
		}
		
	}
}
