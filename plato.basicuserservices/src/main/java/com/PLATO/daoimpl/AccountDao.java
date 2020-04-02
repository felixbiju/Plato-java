package com.PLATO.daoimpl;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import com.PLATO.mongodbentities.Account;
import com.mongodb.MongoClient;

public class AccountDao extends BasicDAO<Account, String> {
	
	  public AccountDao(Morphia morphia, MongoClient mongo, String dbName) {       
	        super(mongo, morphia, dbName);   
	    }

}
