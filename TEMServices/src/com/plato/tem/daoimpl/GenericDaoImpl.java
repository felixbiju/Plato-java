package com.plato.tem.daoimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.plato.tem.Singletons.RetrieveSessionFactory;
import com.plato.tem.dao.GenericDao;

/*Description: This class has all generic queries for database operrations using Hibernate
 * 
 * 
 * */


public class GenericDaoImpl implements GenericDao
{
	private final SessionFactory factory=RetrieveSessionFactory.getSessionFactory();
	private static final Logger logger=Logger.getLogger(GenericDaoImpl.class);


	@Override	
	public Object findByID(Class<?> type, Object id) throws Exception
	{
		logger.info("Inside function findById");
		Object entity = new Object();
		Session session=factory.openSession();
		try{			 
			entity = (Object)session.get(type, (Serializable) id);
		}
		catch(Exception e)
		{
			logger.error("Error in finding Object by Id");
			throw new Exception(e);
		}
		finally{
			session.close();
		}
		logger.info("Returning entity retrieved by Id");
		return entity;
	}



	@Override
	public Object findUniqueByQuery(String queryString,HashMap<String, Object> keyvalueMap) throws Exception
	{
		logger.info("Inside function findUniqueByQuery");
		Object entity = new Object();
		Session session = factory.openSession();
		try
		{
			Query query = session.createQuery(queryString);
			query = setParams(query, keyvalueMap);	
			entity= query.uniqueResult();
		}
		catch(Exception e)
		{
			logger.error("Error in finding Unique Object by Query");	
			throw new Exception(e);
		}
		finally
		{
			session.close();
		}
		logger.info("Returning entity retrieved in findUniqueByQuery");
		return entity;
	}


	@Override
	public List<Object> findByQuery(String queryString,	HashMap<String, Object> keyvalueMap) throws Exception {
		logger.info("Inside function findByQuery");
		Session session = factory.openSession();
		List<Object> list=new ArrayList<Object>();
		try{

			Query query = session.createQuery(queryString);
			query = setParams(query, keyvalueMap);	
			list= query.list();

		}
		catch(HibernateException hbe){
			logger.error("Error in finding by query");
			hbe.printStackTrace();
			throw new Exception(hbe);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		finally{
			session.close();
		}
		logger.info("Returning entities retrieved in findByQuery");
		return list;
	}


	@Override
	public List<Object> findAll(Class<?> type) throws Exception {
		logger.info("Inside function findAll");
		List<Object> list= new ArrayList<Object>();
		Session session=factory.openSession();
		try{
			//Session session=factory.openSession();
			list= session.createQuery("Select list from " + type.getName() + " list").list();
			//session.close();
		}
		catch(HibernateException hbe){
			logger.error("Error in findAll");
			hbe.printStackTrace();
			list=null;
			throw new Exception(hbe);
		}
		catch (Exception e) {
			e.printStackTrace();
			list=null;
			throw new Exception(e);
		}finally {
			session.close();
		}
		logger.info("Returning entities retrieved in findAll");
		return list;
	}


	public Query setParams(Query query, HashMap<String, Object> keyvalueMap){
		logger.info("Inside function setParams");
		if (keyvalueMap!=null) {
			if (keyvalueMap.size() != 0) {
				Iterator<Entry<String, Object>> keyValuePairs = keyvalueMap.entrySet().iterator();
				for (int i = 0; i < keyvalueMap.size(); i++) {
					@SuppressWarnings("rawtypes")
					Map.Entry entry = (Map.Entry) keyValuePairs.next();
					if(entry.getKey().toString().endsWith("LIST")){
						query.setParameterList(entry.getKey().toString(), (Object[])entry.getValue());
					}
					else{
						query.setParameter(entry.getKey().toString(), entry.getValue());
					}
				}//for
			}// if not empty
		}//if not null
		logger.info("Returning query after setting params");
		return query;
	}




	@Override
	public int updateQuery(String queryString,
			HashMap<String, Object> keyvalueMap) throws Exception {
		logger.info("Inside function updateQuery");
		int result=0;
		Transaction tx=null;
		Session session = factory.openSession();
		tx=session.beginTransaction();
		try{
			Query query = session.createQuery(queryString);
			query = setParams(query, keyvalueMap);	
			result=query.executeUpdate();
			tx.commit();
			logger.info("Returning after updating the entity");
			return result;
		}catch(Exception e){
			logger.error("Error in updateQuery");
			if(tx!=null){
				tx.rollback();
			}
			throw new Exception(e);
		}
		finally{	
			session.close();
		}

	}

	@Override
	public List<Object> findBySQLQuery(String queryString,
			HashMap<String, Object> keyvalueMap) {
		logger.info("Inside function findBySQLQuery");
		Session session=null;
			Query query = null;
			List<Object>emptyList=new ArrayList<Object>();
			try{

				session = factory.openSession();			
				query=session.createSQLQuery(queryString);
				query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
				query = setParams(query, keyvalueMap);	
				//session.close();
				logger.info("Returning entities retrieved in findBySQLQuery");
				return query.list();
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Error in findBySQLQuery");
				session.close();
			}
			finally{
				session.close();
			}
			logger.info("Returning entities retrieved in emptyList");
			return emptyList;
	}


	@Override
	public Object createOrUpdate(Object entity) throws Exception {
		logger.info("Inside function createOrUpdate");
		Transaction tx=null;
		Session session = factory.openSession();
		tx=session.beginTransaction();
		try{
			session.saveOrUpdate(entity);
			session.flush();
			tx.commit();
		}
		catch(Exception e)
		{
			logger.error("Error in createOrUpdate");
			e.printStackTrace();
			if(tx!=null){
				tx.rollback();		
			}
			if(e.getCause().toString().contains("Duplicate"))
			{
				throw new Exception("Duplicate Row in table");
			}
			entity=null;
			throw new Exception(e);
		}
		finally
		{	
			session.close();
		}
		// entityManager.refresh(entity);
		logger.info("Returning successfully after creation of entity");
		return entity;
	}

	@Override
	public void delete(Class<?> type, Object id) throws Exception
	{
		logger.info("Inside function delete");
		Transaction tx=null;
		Session session = factory.openSession();
		tx=session.beginTransaction();
		Object entity = (Object) session.get(type, (Serializable) id);
		try
		{
			if (null != entity)
			{
				session.delete(entity);
				session.flush();
			}
			tx.commit();
			logger.info("Deletion sucessful");
		}
		catch(Exception e)
		{
			logger.error("Error in delete");
			if(tx!=null)
			{
				tx.rollback();
			}
			throw new Exception(e);
		}
		finally
		{
			session.close();

		}
	}





	///
	/*@Override
public List<Object> findAndKillBySQLQuery(String queryString,
		HashMap<String, Object> keyvalueMap) {

	    Session session=null;
		Query query = null;
		List<Object>emptyList=new ArrayList<Object>();
		queryString="select * from information_schema.PROCESSLIST where Command='Sleep' and HOST not like '%com%'";

      try{

		 session = factory.openSession();			
		 query=session.createSQLQuery(queryString);
		//query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
	//	query = setParams(query, keyvalueMap);	
		//session.close();
		 List<Object>emptyList1= query.list();

		 for(int i=0;i<emptyList1.size();i++){
			// int pid=emptyList1.get(index);
			 query=session.createSQLQuery("KILL ");
		 }

      }catch(Exception e){
    	  session.close();
      }
      finally{
    	  session.close();
      }
      return emptyList;
      }
	 */}
