package com.PLATO.utilities;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

//utility class to perform operations on xml 
public class XMLUtilities
{
	// function to return xml file and return it in form of document
	public static Document getDocumentFromXML(String filePath) throws Exception
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		File configFile = new File(filePath);
		Document doc = docBuilder.parse(configFile);	
		return doc;
	}

	//function to set text inside given tag of a document
	public static Document setTagText(Document doc, String tagName, String text) 
	{
		Node descriptionNode=doc.getElementsByTagName(tagName).item(0);
		if(!(descriptionNode.hasChildNodes()))
		{
			descriptionNode.appendChild(doc.createTextNode(text));
		}
		else
		{
			descriptionNode.getFirstChild().setNodeValue(text);
		}
		return doc;
	}

	//function to check if xml file exists
	public static Boolean fileExistsCheck(String filePath)
	{
		File file = new File(filePath);
		return file.exists();
	}

	//function to convert xml string to document
	public static Document convertStringToDocument(String docStr) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder;  
		builder = factory.newDocumentBuilder();  
		Document doc = builder.parse( new InputSource( new StringReader( docStr ) ) ); 
		return doc;
	}
}
