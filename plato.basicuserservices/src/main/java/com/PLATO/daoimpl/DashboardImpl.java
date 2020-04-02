package com.PLATO.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.PLATO.Singletons.RetrieveSessionFactory;
import com.PLATO.dao.DashboardDao;
import com.PLATO.entities.ProjectMaster;
import com.PLATO.entities.UserProjectMapping;

public class DashboardImpl implements DashboardDao {

	
	//private SessionFactory factory; 
	
	@Override
	public List<ProjectMaster> getAllocatedProjectsForUser(String username) {
		// TODO Auto-generated method stub
		List<ProjectMaster> allocatedProjects = new ArrayList<ProjectMaster>();
		List<UserProjectMapping>userPrjMappingList;
	//	factory = new AnnotationConfiguration().configure().buildSessionFactory();
		Session session = RetrieveSessionFactory.getSessionFactory().openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		try{
			Query query = session.createQuery("FROM UserProjectMapping where user_id=:user_id");

			query.setParameter("user_id", username);
			userPrjMappingList= query.list();
			
			
			ProjectMaster prjMaster;
			UserProjectMapping userPrjMapping;
			for(int i=0;i<userPrjMappingList.size();i++){
			
				userPrjMapping=userPrjMappingList.get(i);
				prjMaster=userPrjMapping.getProjectMaster();			
				allocatedProjects.add(prjMaster);			
			}
	
			tx.commit();
			session.close();

		}catch(Exception e){
			e.printStackTrace();
		}
		return allocatedProjects;
		
		
	}

}
