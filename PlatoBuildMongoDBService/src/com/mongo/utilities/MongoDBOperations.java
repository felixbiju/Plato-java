package com.mongo.utilities;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;

import com.mongo.constants.GlobalConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public class MongoDBOperations {

	private static final Logger logger=Logger.getLogger(MongoDBOperations.class);

	public static void UpdateJsonInMongoDB(JSONObject finalPlatoJsonObj, int buildHistoryId) {/*
		// TODO Auto-generated method stub

		String finalPlatoJson=finalPlatoJsonObj.toJSONString();

		MongoClient mongoClient = new MongoClient( "172.25.9.140" , 27011 );
		MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
		MongoCollection collection = database.getCollection("BuildHistoryDetails");
		Document doc = Document.parse(finalPlatoJson);




		FindIterable<Document> iterDoc = collection.find(); 

		Iterator it = iterDoc.iterator(); 

		if(it.hasNext()==false){
			collection.insertOne(doc); 
		}else{
			while (it.hasNext()) {  
				Document query=(Document) it.next();
	    		String platoJson=query.toString();
	    		if(platoJson.contains("build_history_id="+buildHistoryId)){
	    			//Bson filter = new Document("build_history_id", buildHistoryId);
	    			//collection.replaceOne(filter, doc);
	    			//collection.deleteOne(Filters.eq("build_history_id", buildHistoryId)); 
	    			//collection.insertOne(doc); 


	    			BasicDBObject query1 = new BasicDBObject();

	    			    query.append("build_history_id", buildHistoryId);

	    			    UpdateResult result = collection.replaceOne(query1, doc,

	    			        (new UpdateOptions()).upsert(true));


	    		}else{
	    			collection.insertOne(doc); 
	    		}





			//Bson filter = new Document("build_history_id", buildHistoryId);
			//Bson updateOperationDocument = new Document("$set", doc);
		//	collection.updateOne(Filters.eq("build_history_id", buildHistoryId), updateOperationDocument);
			//collection.updateOne(Filters.eq("build_history_id", buildHistoryId), updateOperationDocument);
		//}
		}

	    while (it.hasNext()) {  
	    	if(it.next()==null){
	    		collection.insertOne(doc); 
	    	}else{

	    		Bson filter = new Document("build_history_id", "3");
	    		Bson updateOperationDocument = new Document("$set", doc);
	    		collection.updateOne(filter, updateOperationDocument);
	    	}

	    }

	 */



		// TODO Auto-generated method stub

		boolean flag=false;

		String finalPlatoJson=null;
		MongoClient mongoClient =null;
		try{
			finalPlatoJson=finalPlatoJsonObj.toJSONString();
			System.out.println("mongoDBOperations finalPlatoJson "+finalPlatoJson);
			mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
			MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
			MongoCollection<Document> collection=database.getCollection("BuildHistoryDetails");

			Document doc = Document.parse(finalPlatoJson);
			System.out.println("mongoDBOperations doc "+doc.toString());
			/*Document query;
		String platoJson="";*/

			/*FindIterable<Document> iterDoc = collection.find(); 

		Iterator it = iterDoc.iterator(); 

			if(it.hasNext()==false){
			query=(Document) it.next();
			platoJson=query.toString();
			if(platoJson.contains("build_history_id="+buildHistoryId)){
				platoJson=query.toJson();
				break;

			collection.insertOne(doc); 
		}else{
		Document query = null;
		String platoJson;
		while (it.hasNext()) {  
			query=(Document) it.next();
			platoJson=query.toString();
			if(platoJson.contains("build_history_id="+String.valueOf(buildHistoryId))){
				//Bson filter = new Document("build_history_id", buildHistoryId);
				//collection.replaceOne(filter, doc);
				//collection.deleteOne(Filters.eq("build_history_id", buildHistoryId)); 
				//collection.insertOne(doc); 

				flag=true;
				break;				

			}
		}	
			 */


			BasicDBObject query1 = new BasicDBObject();
			query1.append("_id", doc.get("_id"));

			// BasicDBObject doc = BasicDBObject.parse(jsonString);

			Bson newDocument = new Document("$set", doc);
			System.out.println("mongoDBOperations newDocument "+newDocument.toString());
			UpdateResult result = collection.updateOne(query1, newDocument,(new UpdateOptions()).upsert(true));


			logger.debug("UpdateResult is :"+result);
			System.out.println("MongoDBOPerations UpdateResult is :"+result);
			logger.debug("Updated Document : "+doc);
			System.out.println("MongoDBOPerations Updated Document : "+doc);
		}finally{
			mongoClient.close();
		}

		/*if(flag){
			logger.debug("Updating the Existing Document : "+query);
			logger.debug("Replacing the document with : "+doc);
			Object id=query.get("_id");
					logger.debug("id is :"+query.get("_id"));
					collection.deleteOne(query);

					BasicDBObject query1 = new BasicDBObject();

					//query1.append("build_history_id",buildHistoryId);
					query1.append("_id",id);

					UpdateResult result = collection.replaceOne(query1, doc,

							(new UpdateOptions()).upsert(true));

			BasicDBObject query1 = new BasicDBObject();
			query1.append("_id", doc.get("_id"));

			// BasicDBObject doc = BasicDBObject.parse(jsonString);

			Bson newDocument = new Document("$set", doc);

			UpdateResult result = collection.updateOne(query1, newDocument,(new UpdateOptions()).upsert(true));


			logger.debug("UpdateResult is :"+result);
			logger.debug("Updated Document : "+doc);
		}else{
			logger.debug("Document not found: In Else, so inserting new document :"+doc);
			collection.insertOne(doc); 
		}
		 */




		//Bson filter = new Document("build_history_id", buildHistoryId);
		//Bson updateOperationDocument = new Document("$set", doc);
		//	collection.updateOne(Filters.eq("build_history_id", buildHistoryId), updateOperationDocument);
		//collection.updateOne(Filters.eq("build_history_id", buildHistoryId), updateOperationDocument);
		//}
		//}

		/*    while (it.hasNext()) {  
		if(it.next()==null){
			collection.insertOne(doc); 
		}else{

			Bson filter = new Document("build_history_id", "3");
			Bson updateOperationDocument = new Document("$set", doc);
			collection.updateOne(filter, updateOperationDocument);
		}

	}*/




	}



	public synchronized static String fetchJsonFromMongoDB(int buildHistoryId) throws Exception {
		// TODO Auto-generated method stub

		//String finalPlatoJson=finalPlatoJsonObj.toJSONString();

		//String platoJson="";


		/*	MongoClient mongoClient = new MongoClient( "172.25.9.140" , 27011 );
		MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
		MongoCollection collection = database.getCollection("BuildHistoryDetails");*/
		//Document doc = Document.parse(finalPlatoJson);
		MongoClient mongoClient=null;
		try{
			mongoClient = new MongoClient(GlobalConstants.MONGODB_URL , GlobalConstants.MongoDB_PORT );
			MongoDatabase database = mongoClient.getDatabase("BuildHistoryJson");
			MongoCollection<Document> collection=database.getCollection("BuildHistoryDetails");

			Document query;
			logger.debug("Fetching data from MongoDB with build history id :"+buildHistoryId);
			System.out.println("Fetching data from MongoDB with build history id :"+buildHistoryId);
			BasicDBObject fields = new BasicDBObject();
			fields.put("BuildHistory.build_history_id",String.valueOf(buildHistoryId));
			//fields.append("readCount",String.valueOf(0));
			//DBCursor cursor = (DBCursor) collection.find(fields);
			MongoCursor<Document> cursor = collection.find(fields).iterator();
			int i=0;
			while(cursor.hasNext())
			{
				logger.info("inside while.. reading template");
				System.out.println("inside while.. reading template");
				Document platoJsonObj=cursor.next();
				if(platoJsonObj.get("readCount").toString().equalsIgnoreCase("0"))
				{
					System.out.println("Read count is 0.. Updating read count to 1");
					logger.info("Read count is 0.. Updating read count to 1");
					//-----------added for read count

					BasicDBObject newDocument = new BasicDBObject();
					newDocument.append("$set", new BasicDBObject().append("readCount",String.valueOf(1)));
					BasicDBObject searchQuery = new BasicDBObject().append("BuildHistory.build_history_id",String.valueOf(buildHistoryId));

					UpdateResult updateResult=collection.updateOne(searchQuery, newDocument);
					logger.debug("Update result is :"+updateResult);
					System.out.println("Update result is :"+updateResult);
					//---------------------------
					logger.debug("PLATO JSON TEMPLATE IS : "+platoJsonObj.toJson());
					System.out.println("PLATO JSON TEMPLATE IS : "+platoJsonObj.toJson());

					return platoJsonObj.toJson();
				}
				i++;
			}
			if(i==0)
			{
				throw new Exception("No data found");
			}


			/*	Iterator it = iterDoc.iterator();
		while (it.hasNext()) {  
	    		query=(Document) it.next();
	    		platoJson=query.toString();
	    		if(platoJson.contains("build_history_id="+String.valueOf(buildHistoryId))&&platoJson.contains("readCount="+String.valueOf(0))){
	    			System.out.println("mongoDb contains build history id");
	    			platoJson=query.toJson();
	    			return platoJson;

	    	}*/

			//query.put("build_history_id", String.valueOf(buildHistoryId));

			/*BasicDBObject getQuery=new BasicDBObject();
		getQuery.put("build_history_id", new BasicDBObject("$eq",buildHistoryId));

		DBCursor cursor=(DBCursor) collection.find(getQuery);

		while(cursor.hasNext()){
			System.out.println(cursor.next());
		}*/

			//Iterator it = iterDoc.iterator(); 

			//if(it.hasNext()==false){
			//	platoJson="";
			//		}else{
			//platoJson=((Document)it.next()).toString();
			//		}

			/*if(iterDoc==null){
		}

			//for(Document dc:iterDoc){

			platoJson=(iterDoc.first()).toString();*/
			//}

			/*    while (it.hasNext()) {  
	    	if(it.next()==null){
	    		collection.insertOne(doc); 
	    	}else{

	    		Bson filter = new Document("build_history_id", "3");
	    		Bson updateOperationDocument = new Document("$set", doc);
	    		collection.updateOne(filter, updateOperationDocument);
	    	}

	    }*/
			logger.info("returning null as document does not exist or read count is 1");
			System.out.println("returning null as document does not exist or read count is 1");
			return null;

		}finally{
			mongoClient.close();
		}
	}
}
