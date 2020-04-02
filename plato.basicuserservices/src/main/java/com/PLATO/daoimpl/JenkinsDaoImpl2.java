package com.PLATO.daoimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.PLATO.Singletons.RetrieveSessionFactory;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.dao.JenkinsDao;
import com.PLATO.entities.ModuleJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.services.JenkinsServices;

public class JenkinsDaoImpl2 implements JenkinsDao
{


	private final SessionFactory factory=RetrieveSessionFactory.getSessionFactory();
	private static final Logger logger=Logger.getLogger(JenkinsServices.class);


	public Query setParams(Query query, HashMap<String, Object> keyvalueMap){
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
		return query;
	}




	@Override
	public int updateQuery(String queryString,
			HashMap<String, Object> keyvalueMap) throws Exception {
		int result=0;
		Transaction tx=null;
		Session session = factory.openSession();
		tx=session.beginTransaction();
		try{
			Query query = session.createQuery(queryString);
			query = setParams(query, keyvalueMap);	
			result=query.executeUpdate();
			tx.commit();
			return result;
		}catch(Exception e){
			if(tx!=null){
				tx.rollback();
			}
			throw new Exception(e);
		}
		finally{	
			session.close();
		}

	}

	//function to create module in jenkins
	@Override
	public ModuleJobsJenkins createModule(ModuleJobsJenkins module) throws Exception {
		logger.info("Inside function createModule");
		logger.debug("Received parameter ModuleJobsJenkins with name as :"+module.getJenkins_job_name());
		Transaction tx=null;
		JenkinsServices jenkinsService=new JenkinsServices();
		Session session = factory.openSession();
		tx=session.beginTransaction();
		try{
			//create job in database
			session.saveOrUpdate(module);
			//create job in jenkins
			//jenkinsService.createJob(module.getDescription(),module.getJenkins_job_name());
			session.flush();
			//commit the transaction if creation of job successful in both database and jenkins
			tx.commit();
			logger.info("Create job in both database and jenkins and Committed the transaction");
		}
		//roll back transaction if there is exception. Thus entry is not created in database if there is error while creating job in jenkins
		catch(Exception e)
		{
			logger.error("Error while creating job :"+e.getMessage());
			e.printStackTrace();
			if(tx!=null)
			{
				tx.rollback();	
				logger.info("Rolled back transaction");
			}
			if(e.getCause().toString().contains("Duplicate")||e.getMessage().equalsIgnoreCase("Duplicate Module"))
			{
				logger.error("Throwing exception");
				throw new Exception("Duplicate Module");
			}			
			module=null;
			logger.info("Throwing exception");
			throw new Exception(e);
		}
		finally
		{	
			session.close();
		}
		// entityManager.refresh(entity);
		return module;
	}	



	//function to create jobs in jenkins under main job(module) and create submodules in database
	@Override
	public void createSubModule(ModuleJobsJenkins module) throws Exception {
		logger.info("Inside function createSubModule");
		logger.debug("Received parameter ModuleJobsJenkins with name as :"+module.getJenkins_job_name());
		Transaction tx=null;
		ArrayList<ModuleSubJobsJenkins> subJobList=null;
		JenkinsServices jenkinsService=new JenkinsServices();
		Session session = factory.openSession();
		//begin transaction
		tx=session.beginTransaction();
		int subModulecnt=0;
		try{

			Set<ModuleSubJobsJenkins> subJobSet=module.getModuleSubJobsJenkins();
			subJobList=new ArrayList<ModuleSubJobsJenkins>(subJobSet);
			//loop to go through each subjob to be created
			for(ModuleSubJobsJenkins subJob:subJobList)
			{
				//save subjob in database
				session.saveOrUpdate(subJob);
				//check the tool for subjob and call appropriate function to create subjob accordingly
				if(subJob.getTool_name().equalsIgnoreCase("FAST"))
				{
					jenkinsService.createFASTSubJob(subJob);
				}else if(subJob.getTool_name().equalsIgnoreCase("SVN")) {
					jenkinsService.createSVNSubJob(subJob);
				}else if(subJob.getTool_name().equalsIgnoreCase("MAVEN")) {
					jenkinsService.createMavenSubJob(subJob);
				}else if(subJob.getTool_name().equalsIgnoreCase("Selenium")){
					jenkinsService.createSeleniumSubJob(subJob);
				}else if(subJob.getTool_name().equalsIgnoreCase("UFT") || subJob.getTool_name().equalsIgnoreCase("HP QTP")) {
					jenkinsService.createUftSubJob(subJob);
				}else if(subJob.getTool_name().equalsIgnoreCase("SONAR")) {
					jenkinsService.createSonarJob(subJob);
				}else if(subJob.getTool_name().equalsIgnoreCase("MavenSVN")) {
					//jenkinsService.createMavenSvnSubJob(subJob);;
				}else if(subJob.getTool_name().equalsIgnoreCase("Diat")){
					jenkinsService.createDiatSubJob(subJob);

				}
				//increase the count to keep track of number of submodules successfully created
				subModulecnt++;
			}		



			/* NOTE : We are keeping the order of subjobs (as a comma seperated string) in the table of Main module. This order is used for displaying
			 * the subjobs on UI. So whenever we add new subjobs under a module, we need to update the order of 
			 * subjobs. 
			 */
			//update the order of subjobs in the database after all subjobs are created successfully. 
			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleSubJobsOrder", module.getModule_subjobs_order());
			keyvalueMap.put("moduleId", module.getJenkins_job_id());

			Query query=session.createQuery(ConstantQueries.UPDATEMODULESUBJOBORDER);
			query = setParams(query, keyvalueMap);	
			query.executeUpdate();		
			session.flush();

			//jenkinsService.setChildProjectsInMainJob(subJobSet,module.getJenkins_job_name());

			//set the created subjobs as downstream projects in config of main job(main module)
			jenkinsService.setChildProjectsInMainJob(module.getModule_subjobs_order(),module.getJenkins_job_name());

			//commit transaction after all above operations are successful in database as well as jenkins
			tx.commit();
			logger.info("Created subjobs successfully in both jenkins and databas. Committed the transaction");
		}
		catch(Exception e)
		{
			logger.error("Error in creating subjobs :"+e.getMessage());
			e.printStackTrace();
			/* rollback transaction if creation of subjob in jenkins fails. Thus if creation in jenkins fails,
			 * then we undo changes in database. 
			 */
			if(tx!=null)
			{
				tx.rollback();		
				logger.info("Rolled back transaction");
			}

			/* NOTE :In case of exception we are deleting the created subjobs from jenkins. 
			 * subModulecnt is used to keep track of number of subjobs created. Arraylist has all submodule objects 
			 * in order. 
			 */
			if(e.getCause().toString().contains("Duplicate")||e.getMessage().equalsIgnoreCase("Duplicate Module"))
			{	
				//deleting the created jobs from jenkins
				for(int i=0;i<subModulecnt;i++)
				{
					ModuleSubJobsJenkins subJob= subJobList.get(i);
					String result=jenkinsService.deleteJob(subJob.getSubjob_name());
				}
				logger.error("Throwing duplicate module exception");
				throw new Exception("Duplicate Module");
			}	

			//deleting created subjobs from jenkins
			for(int i=0;i<subModulecnt;i++)
			{
				ModuleSubJobsJenkins subJob= subJobList.get(i);
				String result=jenkinsService.deleteJob(subJob.getSubjob_name());
			}
			module=null;	
			logger.info("Throwing exception");
			throw new Exception(e);
		}
		finally
		{	
			session.close();
		}
		// entityManager.refresh(entity);

	}	


	/*@Override
	public void createSubModule(ModuleSubJobsJenkins subModule, String moduleSubJobOrder, int moduleId) throws Exception {
		Transaction tx=null;

		JenkinsServices jenkinsService=new JenkinsServices();
		Session session = factory.openSession();
		tx=session.beginTransaction();
		try
		{
			session.saveOrUpdate(subModule);
			if(subModule.getTool_name().equalsIgnoreCase("FAST"))
			{
				jenkinsService.createFASTSubJob(subModule);
			}

			if(moduleSubJobOrder==null||moduleSubJobOrder.isEmpty())
			{
				moduleSubJobOrder=subModule.getSubjob_name();
			}
			else
			{
				moduleSubJobOrder+=","+subModule.getSubjob_name();
			}

			HashMap<String,Object> keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleSubJobsOrder", moduleSubJobOrder);
			keyvalueMap.put("moduleId", moduleId);

			Query query=session.createQuery(ConstantQueries.UPDATEMODULESUBJOBORDER);
			query = setParams(query, keyvalueMap);	
			query.executeUpdate();		
			session.flush();

			jenkinsService.setChildProjectsInMainJob(moduleSubJobOrder,subModule.getModuleJobsJenkins().getJenkins_job_name());

			tx.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(tx!=null)
			{
				tx.rollback();		
			}
			if(e.getCause().toString().contains("Duplicate")||e.getMessage().equalsIgnoreCase("Duplicate Module"))
			{	
				throw new Exception("Duplicate Module");
			}		
			throw new Exception(e);
		}
		finally
		{	
			session.close();
		}

	}*/
	public void createSubModule(ModuleJobsJenkins module,Set<ModuleSubJobsJenkins> moduleSetForPostBuild)throws Exception{
		
	}

	//function to update subjob in both database and jenkins
	@Override
	public void updateSubModule(ModuleSubJobsJenkins subModule) throws Exception
	{
		logger.info("Inside function updateSubModule");
		logger.debug("Received object ModuleSubJobsJenkins as parameter with subjob name as :"+subModule.getSubjob_name());
		Transaction tx=null;
		JenkinsServices jenkinsService=new JenkinsServices();
		Session session = factory.openSession();
		//begin transaction
		tx=session.beginTransaction();

		try
		{
			//update row in database
			session.saveOrUpdate(subModule);
			session.flush();
			//according to tool of subjob, call function to update jenkins subjob
			if(subModule.getTool_name().equalsIgnoreCase("FAST"))
			{
				jenkinsService.updateFASTSubJob(subModule);
			}else if(subModule.getTool_name().equalsIgnoreCase("MAVEN")) {
				jenkinsService.updateMavenSubJob(subModule);
			}else if(subModule.getTool_name().equalsIgnoreCase("UFT")|| subModule.getTool_name().equalsIgnoreCase("HP QTP")) {
				jenkinsService.updateUftSubJob(subModule);
			}else if(subModule.getTool_name().equalsIgnoreCase("SVN")) {
				jenkinsService.updateSvnSubJob(subModule);
			}else if(subModule.getTool_name().equalsIgnoreCase("MAVENSVN") ) {
				//jenkinsService.updateMavenSvnSubJob(subModule);
			}

			//commit transaction when updates in database and jenkins are successful
			tx.commit();
			logger.info("Successfully update submodule and committed transaction");
		}
		catch(Exception e)
		{
			logger.error("Exception in updating subjob");
			e.printStackTrace();
			
			/*rollback transaction if updation of job in jenkins fails. Thus if update in jenkins fails,
			 * then we undo changes in database
			 */
			if(tx!=null)
			{
				tx.rollback();		
			}
			if(e.getMessage().equalsIgnoreCase("Module Does Not Exist"))
			{
				throw new Exception("Module Does Not Exist");
			}			
			subModule=null;
			logger.info("Throwing exception");
			throw new Exception(e);

		}
		finally
		{	
			session.close();
		}
	}

	//function to delete job from jenkins
	@Override
	public void delete(Class<?> type, Object id,String jobName) throws Exception
	{
		JenkinsServices jenkinsService=new JenkinsServices();
		Transaction tx=null;
		Session session = factory.openSession();
		tx=session.beginTransaction();
		try
		{
			//delete job from database
			Object entity = (Object) session.get(type, (Serializable) id);
			if (null != entity)
			{
				session.delete(entity);
				session.flush();
			}
			else
			{
				throw new Exception("Job does not exist");
			}
			//delete job from jenkins
			jenkinsService.deleteJob(jobName);
			//commit transaction if deletion from database and jenkins is successful
			tx.commit();
		}
		catch(Exception e)
		{
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

	//function to delete subjob from database and jenkins
	@Override
	public void deleteSubModuleByQuery(String subModuleName) throws Exception
	{	
		JenkinsServices jenkinsService=new JenkinsServices();
		Transaction tx=null;
		Session session = factory.openSession();
		//begin transaction
		tx=session.beginTransaction();

		try
		{		
			//get moduleId of main module for given submodule 
			HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("subJobName", subModuleName);
			Query query = session.createQuery(ConstantQueries.GETMAINMODULEID);
			query = setParams(query, keyvalueMap);	
			List<Object> moduleIDList=query.list();
			int moduleId=(int) moduleIDList.get(0);

			//delete submodule from database
			query = session.createQuery(ConstantQueries.DELETESUBMODULEBYNAME);
			query = setParams(query, keyvalueMap);	
			query.executeUpdate();

			//get subjob order for main module from database, remove it from list and update it in database
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleId", moduleId);
			query= session.createQuery(ConstantQueries.GETMODULESUBJOBORDER);
			query = setParams(query, keyvalueMap);	
			List<Object> moduleSubJobOrder=query.list();


			String subJobOrder=(String) moduleSubJobOrder.get(0);
			LinkedList<String> subJobOrderList=new LinkedList<String>(Arrays.asList(subJobOrder.split(",")));
			if(subJobOrderList.size()==1||subJobOrderList.size()==0)
			{
				subJobOrder=null;
			}
			else
			{
				subJobOrderList.remove(subModuleName);
				subJobOrder=String.join(",",subJobOrderList);
			}
			keyvalueMap=new HashMap<String,Object>();
			keyvalueMap.put("moduleSubJobsOrder", subJobOrder);
			keyvalueMap.put("moduleId", moduleId);
			query=session.createQuery(ConstantQueries.UPDATEMODULESUBJOBORDER);
			query=setParams(query, keyvalueMap);
			query.executeUpdate();

			//delete subjob from jenkins
			String result=jenkinsService.deleteJob(subModuleName);
			if(result.equalsIgnoreCase("Job does not exist"))
			{
				throw new Exception("Job does not exist");
			}
			//commit transaction if deletion from database and jenkins is successful 
			tx.commit();
		}
		catch(Exception e)
		{
			//rollback transaction if any operation fails
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

	//function to delete module
	@Override
	public void deleteModule(ModuleJobsJenkins module) throws Exception
	{
		logger.info("Inside function deleteModule");
		logger.debug("Received parameter ModuleJobsJenkins with module name :"+module.getJenkins_job_name());
		JenkinsServices jenkinsService=new JenkinsServices();
		Transaction tx=null;
		Session session = factory.openSession();
		tx=session.beginTransaction();

		try
		{
			//delete module from database
			session.delete(module);
			String subJobOrder=module.getModule_subjobs_order();
			//update subjob order by removing deleted subjob from list
			if(subJobOrder!=null)
			{
				String[] subJobs=subJobOrder.split(",");
				ArrayList<String> subJobOrderList=new ArrayList<String>(Arrays.asList(subJobs));
				for(String subJob:subJobOrderList)
				{
					String result=jenkinsService.deleteJob(subJob);
				}
			}
			
			//delete job from jenkins
			jenkinsService.deleteJob(module.getJenkins_job_name());
			//commit transaction after deletion is successful in database as well as jenkins
			tx.commit();
		}
		catch(Exception e)
		{
			/*rollback transaction in case of exception. Thus if deletion is not successful in Jenkins then 
			 * we do not delete from database 
			 */
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




	@Override
	public ModuleJobsJenkins updateModule(ModuleJobsJenkins module) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
