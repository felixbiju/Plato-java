package com.PLATO.daoimpl;

import java.net.UnknownHostException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import com.PLATO.mongodbentities.Account;
import com.PLATO.mongodbentities.Project;
import com.PLATO.mongodbentities.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;



public class MongodbDao 
{
	
	public void connectDB(Account acc) throws UnknownHostException{
		// Creating a Mongo client 
		MongoClient mongo = new MongoClient( "localhost" , 27017 );

		// Creating Credentials 
		
		System.out.println("Connected to the database successfully");  

		// Accessing the database 
		DB db = mongo.getDB("mysampledb"); 
		//System.out.println("Credentials ::"+ credential);  
		
		DBCollection collection = db.getCollection("accountscoll");
		
		BasicDBObject document = new BasicDBObject();
		//document.put("account",acc );
		
		//collection.insert(document);
		
		//collection.save(acc);
		
		DBCursor cursorDoc = collection.find();
		while (cursorDoc.hasNext()) {
			System.out.println(cursorDoc.next());
		}	
	}
	
	public Account getAccountFromDb(int id) throws UnknownHostException
	{
		// Creating a Mongo client 
		Account acc = new Account();
		BasicDBObject dbObj=null;
		MongoClient mongo = new MongoClient( "localhost" , 27017 );

		// Creating Credentials 
		
		System.out.println("Connected to the database successfully");  

		// Accessing the database 
		//DB db = mongo.getDB("mysampledb"); 
		//System.out.println("Credentials ::"+ credential);  
		
		DB db = mongo.getDB("mysampledb"); 
		
		DBCollection collection = db.getCollection("accountscoll");
	
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("acc_id", ""+id);
        //collection.setObjectClass(Account.class);
		DBCursor cursor = collection.find(searchQuery);

		while (cursor.hasNext()) {
			dbObj=(BasicDBObject)cursor.next();
			
		}
		
		//dbObj.remove("_id");
	//	acc=(Account) dbObj;
		
		/*acc.setAcc_id(dbObj.getString("acc_id"));
		acc.setAcc_name(dbObj.getString("acc_name"));
		
	
		Set<Project> projects=new HashSet<Project>();
		
		
		 BasicDBList projectList = (BasicDBList) dbObj.get("projects");
		 
		 BasicDBObject proj = new BasicDBObject();
		 proj.put("project_name","Project3");
		 proj.put("project_id","3");
		 projectList.add(proj);
		 
		 dbObj.put("projects",projectList);
		 collection.save(dbObj);*/
		 
		 
		    /*for (int i = 0; i < projectList.size(); i++)
		    {
		        BasicDBObject projObj = (BasicDBObject) projectList.get(i);
		       
		        
		    }  */
		
		
		return acc;
	}
	
	
	public Account saveAccToDb(Account acc) throws UnknownHostException{
		// Creating a Mongo client 
		
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
        String dbName="PLATODB";
		 Morphia morphia = new Morphia();
		 
		 morphia.mapPackage("com.PLATO.entities");
		 
	     Datastore datastore = morphia.createDatastore(mongo, dbName); 
		// Creating Credentials
	     datastore.ensureIndexes();
	   
		
		System.out.println("Connected to the database successfully");  
		
		Query query=datastore.createQuery(User.class).field("username").equal("gaurava.kulkarni@lntinfotech.com");		
	     User usr = (User) query.asList().get(0);
	     System.out.println(usr.getId());
	     String dd=usr.getId().toString();
	     query=datastore.createQuery(Project.class).field("users").equal("1");
	     //.equal(usr.getId());		
	     Project prj= (Project) query.asList().get(0);
	     
         System.out.println(prj.getProject_name());
		// Accessing the database 
		//DB db = mongo.getDB("mysampledb"); 
		//System.out.println("Credentials ::"+ credential);  
		
		//DBCollection collection = db.getCollection("accountscoll");
		
		// Key<Account> savedAccount = datastore.save(acc);   
	    // System.out.println(savedAccount.getId());
	     
	    
	    /* Query query = datastore.createQuery(Account.class).field("account_name").equal("JCI");
	     Account acc1=(Account) query.asList().get(0);*/
		 //Query query1=datastore.createQuery(User.class).field("username").equal("gaurava.kulkarni@lntinfotech.com");
	    // User usr1 = (User) query1.asList().get(0);
	     
	    /* query1=datastore.createQuery(User.class).field("username").equal("nilesh.dandage@lntinfotech.com");
	     User usr2 = (User) query1.asList().get(0);
	     
	    
	    // acc1.setAcc_name("Account11");
	     
	     
	     
	     Set<User> usrSet=new HashSet<User>();
	     usrSet.add(usr1);
	     usrSet.add(usr2);
	     prj.setUsers(usrSet);
	     datastore.save(prj);*/
	     //Add new project to account
	    /* Project p=new Project();
	     p.setProject_id(3);
	     p.setProject_name("PRJ1");
	     Set<Project> prj=acc1.getProjects();
	     prj.add(p);
	     acc1.setProjects(prj);
	     
	     Key<Account> savedAccount = datastore.save(acc1);   
		 System.out.println(savedAccount.getId());*/
	     
	     //replace exisiting prjects with new projects
	    
	  //   Set<Project> prj=acc.getProjects();
	   //  acc1.setProjects(prj);
	     
	  //   Key<Account> savedAccount = datastore.save(acc1);   
		// System.out.println(savedAccount.getId());
	     
		   
	     return null;
	     
	
	}
}
