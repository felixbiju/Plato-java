package com.adapters.utilities;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mongo.constants.GlobalConstants;


/**
 * @author 10643331(Sueanne Alphonso)
 **/
public class PropertiesFileUtility
{
	private static final Logger logger=Logger.getLogger(PropertiesFileUtility.class);
	//function to read a given value of a property in properties file
	public static String getProperty(String name)
	{
		try
		{
			logger.debug("Getting property from properties file for Name :"+name);
			String value="";
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Properties props = new Properties();
			try(InputStream resourceStream = loader.getResourceAsStream(GlobalConstants.PROPERTIES_FILE_PATH)) {
				props.load(resourceStream);
				value=props.getProperty(name);
			}
			return value;
		}
		catch(Exception e)
		{
			logger.error("Exception in reading from Constants.properties :"+e);
			return null;
		}
	}
}
