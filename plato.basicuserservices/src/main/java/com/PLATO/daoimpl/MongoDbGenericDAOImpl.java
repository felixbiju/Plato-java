package com.PLATO.daoimpl;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.PLATO.constants.GlobalConstants;
import com.PLATO.dao.MongoDbGenericDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDbGenericDAOImpl implements MongoDbGenericDAO
{
	private static final Logger logger=Logger.getLogger(MongoDbGenericDAOImpl.class);
	//Datastore datastore=null;

	//Constructor to connect to mongodb when object of dao is created
	public MongoDbGenericDAOImpl()
	{
		/*try
		{
			MongoClient mongo = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
			Morphia morphia = new Morphia();

			morphia.mapPackage(GlobalConstants.ENTITY_PACKAGE);

			datastore = morphia.createDatastore(mongo, GlobalConstants.MONGODB_DATABASE_NAME); 
			// Creating Credentials
			System.out.println("Connected successfully to Mongodb Host :"+GlobalConstants.MONGODB_URL+" Port:"+GlobalConstants.MongoDB_PORT);
			datastore.ensureIndexes();
		}
		catch(Exception e)
		{
			System.out.println("Could not connect to Mongodb Host :"+GlobalConstants.MONGODB_URL+" Port:"+GlobalConstants.MongoDB_PORT);
		    e.printStackTrace();
		}*/


	}

	/*  Morphia
	 * @Override
	public List<?> getAllDocuments(Class<?> entityToFetch)
	{
		 Query query = datastore.createQuery(entityToFetch);
		 List<Object> dataList = query.asList();
		 return dataList;
	}

	@Override
	public Object insertDocument(Object objectToInsert)
	{
		 Key<Object> savedDocument = datastore.save(objectToInsert);   
	     System.out.println(savedDocument.getId());
	     return savedDocument.getId();
	}*/

	@Override
	public Document getBySingleCondition(String fieldName, String value)
	{
		logger.info("Inside function getBySingleCondition");
		logger.debug("Received parameters fieldName :"+fieldName+" String value :"+value);
		
		Document document=null;
		MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
		try{					
		MongoDatabase database = mongoClient.getDatabase(GlobalConstants.MONGODB_DATABASE_NAME);
		MongoCollection<Document> collection=database.getCollection(GlobalConstants.MONGODB_COLLECTION);

		BasicDBObject searchObj=new BasicDBObject();
		searchObj.put(fieldName, value);

		MongoCursor<Document> cursor = collection.find(searchObj).iterator();
		while(cursor.hasNext()) {
			document=cursor.next();
			logger.debug("Returning the document retrieved in getBySingleCondition");
			return document;
		}
		}finally {
		mongoClient.close();
		}
		logger.info("No document found with buildHistoryId :"+value);
		return null;
	}
	
	@Override
	public ArrayList<Document> getAllBySingleCondition(String fieldName, String value)
	{
		logger.info("Inside function getBySingleCondition");
		logger.debug("Received parameters fieldName :"+fieldName+" String value :"+value);
		
		Document document=null;
		ArrayList<Document> documentList=new ArrayList<Document>();
		MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
		try{					
		MongoDatabase database = mongoClient.getDatabase(GlobalConstants.MONGODB_DATABASE_NAME);
		MongoCollection<Document> collection=database.getCollection(GlobalConstants.MONGODB_COLLECTION);

		BasicDBObject searchObj=new BasicDBObject();
		searchObj.put(fieldName, value);

		MongoCursor<Document> cursor = collection.find(searchObj).iterator();
		while(cursor.hasNext()) {
			document=cursor.next();
			logger.debug("Returning the document retrieved in getBySingleCondition");
			documentList.add(document);
			//return document;
		}
		}finally {
		mongoClient.close();
		}
		logger.info("No document found with buildHistoryId :"+value);
		return documentList;
	}
	
	@Override
	public Document getBySingleCondition(String fieldName, int value)
	{
		logger.info("Inside function getBySingleCondition");
		logger.debug("Received parameters fieldName :"+fieldName+" int value :"+value);
		Document document=null;
		MongoClient mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
		MongoDatabase database = mongoClient.getDatabase(GlobalConstants.MONGODB_DATABASE_NAME);
		MongoCollection<Document> collection=database.getCollection(GlobalConstants.MONGODB_COLLECTION);

	/*	BasicDBObject searchObj=new BasicDBObject();
		searchObj.put(fieldName, value);

		MongoCursor<Document> cursor = collection.find(searchObj).iterator();
		while(cursor.hasNext()) {
			document=cursor.next();
		}*/
		
		FindIterable<Document> iterDoc = collection.find(); 

		Iterator<Document> it = iterDoc.iterator();
		while (it.hasNext()) {  
			document=(Document) it.next();
			String platoJson=document.toString();
			if(platoJson.contains("build_history_id="+value)){
				platoJson=document.toJson();
				break;

			}
		}
		mongoClient.close();
		logger.info("Returning the document retrieved in getBySingleCondition");
		return document;
	}

}
