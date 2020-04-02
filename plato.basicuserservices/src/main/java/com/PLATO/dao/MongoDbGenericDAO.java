package com.PLATO.dao;

import java.util.ArrayList;

import org.bson.Document;

public interface MongoDbGenericDAO
{
	//Morphia
	/*public List<?> getAllDocuments(Class<?> entityToFetch);
	public Object insertDocument(Object objectToInsert);*/
	
	public Document getBySingleCondition(String fieldName, String value);
	public ArrayList<Document> getAllBySingleCondition(String fieldName,String value);
	public Document getBySingleCondition(String fieldName, int value);
}
