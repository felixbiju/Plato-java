package com.plato.tem.dao;


import java.util.HashMap;
import java.util.List;


public interface GenericDao
{
	public Object findUniqueByQuery(String queryString, HashMap<String, Object> keyvalueMap) throws Exception;
	public List<Object> findByQuery(String queryString,	HashMap<String, Object> keyvalueMap) throws Exception;
	public Object findByID(Class<?> type, Object id) throws Exception;
	public List<Object> findAll(Class<?> type) throws Exception;
	
	public int updateQuery(String queryString, HashMap<String, Object> keyvalueMap) throws Exception;
	
	public Object createOrUpdate(Object entity) throws Exception;
	public void delete(Class<?> type, Object id) throws Exception;	
	
	public List<Object> findBySQLQuery(String queryString,	HashMap<String, Object> keyvalueMap);
	/*List<Object> findAndKillBySQLQuery(String queryString,
			HashMap<String, Object> keyvalueMap);*/
}
