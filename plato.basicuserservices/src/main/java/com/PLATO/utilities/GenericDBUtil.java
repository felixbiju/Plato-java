package com.PLATO.utilities;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.PLATO.Singletons.RetrieveSessionFactory;


public class GenericDBUtil {
	
	private final SessionFactory factory = RetrieveSessionFactory.getSessionFactory();

	private SessionFactory sessionFactory;
	
	public Session getCurrentSession() {
		Session session = null;
		if(factory.getCurrentSession().isOpen()) {
			session = factory.getCurrentSession();
		}
		else if(!factory.getCurrentSession().isOpen()) {
			session = factory.openSession();
		}
		Transaction tx = session.beginTransaction();
		return session;
	}
	
	public void closeCurrentSession(Session session ) {
		if(session.isOpen()) {
			Transaction tx = session.getTransaction();
			tx.commit();
			if(session.isOpen()) {
				session.close();
			}
		}
	}
	
	@Transactional
	public List<?> fetchResultFromDB(Class className, String param, Object value, String projection) {
		Session session = null;
		try {
			session = getCurrentSession();
			Criteria criteria = session.createCriteria(className);
			
			if(null != param && !param.equals("") && null != value && !value.equals("")) {
				criteria.add(Restrictions.eq(param, value));
			}
			if(null != projection && !projection.equals("")) {
				criteria.setProjection(Projections.property(projection));
			}
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			List<?> results = criteria.list();
			return results;
		}
		catch (Exception e) {
			System.out.println("Error in fetching data from database :: ");
			e.printStackTrace();
		}
		finally {
			closeCurrentSession(session);
		}
		return null;
	}
	
	@Transactional
	public List<?> fetchResultFromDBFromList(Class className, String param, 
			List<Object> valueList, String projection) {
		Session session = null;
		try {
			session = getCurrentSession();
			Criteria criteria = session.createCriteria(className);
			
			if(null != param && !param.equals("") && null != valueList && !valueList.isEmpty()) {
				criteria.add(Restrictions.in(param, valueList));
			}
			if(null != projection && !projection.equals("")) {
				criteria.setProjection(Projections.property(projection));
			}
			List<?> results = criteria.list();
			return results;
		}
		catch (Exception e) {
			System.out.println("Error in fetching data from database :: ");
			e.printStackTrace();
		}
		finally {
			closeCurrentSession(session);
		}
		return null;
	}
	
	@Transactional
	public List<?> fetchResultForMultipleConditions(Class className, List<String> paramList, 
			List<Object> valueList, String projection) {
		Session session = null;
		try {
			session = getCurrentSession();
			Criteria criteria = session.createCriteria(className);
			
			if(null != paramList && !paramList.isEmpty() && null != valueList && !valueList.isEmpty()) {
				for (int i = 0; i < paramList.size(); i++) {
					criteria.add(Restrictions.eq(paramList.get(i), valueList.get(i)));
				}
			}
			if(null != projection && !projection.equals("")) {
				criteria.setProjection(Projections.property(projection));
			}
			List<?> results = criteria.list();
			return results;
		}
		catch (Exception e) {
			System.out.println("Error in fetching data from database :: ");
			e.printStackTrace();
		}
		finally {
			closeCurrentSession(session);
		}
		return null;
	}
	
	@Transactional
	public List<?> fetchResultForConditionsWithList(Class className, List<String> paramList, 
			List<Object> valueList, String listParam, List<Object> listValues, String projection) {
		Session session = null;
		try {
			session = getCurrentSession();
			Criteria criteria = session.createCriteria(className);
			
			if(null != paramList && !paramList.isEmpty() && null != valueList && !valueList.isEmpty()) {
				for (int i = 0; i < paramList.size(); i++) {
					criteria.add(Restrictions.eq(paramList.get(i), valueList.get(i)));
				}
			}
			if(null != listValues && !listValues.isEmpty()) {
				criteria.add(Restrictions.in(listParam, listValues));
			}
			if(null != projection && !projection.equals("")) {
				criteria.setProjection(Projections.property(projection));
			}
			List<?> results = criteria.list();
			return results;
		}
		catch (Exception e) {
			System.out.println("Error in fetching data from database :: ");
			e.printStackTrace();
		}
		finally {
			closeCurrentSession(session);
		}
		return null;
	}
	
	@Transactional
	public List<?> fetchResultFromDBWithList(Class className, String param, Object value, String listParam, Set<Object> listValues, String projection) {
		Session session = null;
		try{
			session = getCurrentSession();
			Criteria criteria = session.createCriteria(className);
			
			if(null != param && !param.equals("") && null != value && !value.equals("")) {
				criteria.add(Restrictions.eq(param, value));
			}
			if(null != listValues && !listValues.isEmpty()) {
				criteria.add(Restrictions.in(listParam, listValues));
			}
			if(null != projection && !projection.equals("")) {
				criteria.setProjection(Projections.property(projection));
			}
			List<?> results = criteria.list();
			return results;
		}
		catch (Exception e) {
			System.out.println("Error in fetching data from database :: ");
			e.printStackTrace();
		}
		finally {
			closeCurrentSession(session);
		}
		return null;
	}
	
	@Transactional
	public void updateEntity(Object obj) {
		Session session = null;
		try {
			session = getCurrentSession();
			session.saveOrUpdate(obj);
		}
		catch(Exception e) {
			System.out.println("Error in fetching data from database :: ");
			e.printStackTrace();
		}
		finally {
			closeCurrentSession(session);
		}
		
	}
}
