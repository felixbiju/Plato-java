package com.plato.errorHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONObject;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */
public class ErrorHandler {
	public void handleError(Exception e) {
		
	}
	public void handleNullPointerException(Exception e,JSONObject temp) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		e.printStackTrace();
		String stackTraceStr=sw.toString();
		JSONObject errorBody=new JSONObject();
		JSONObject traceObj=new JSONObject();
//		traceObj.put("stackTrace", "hello");
//		errorBody.put("stack trace",stackTraceStr);
		errorBody.put("code", 1);
//		errorBody.put("type", e.getMessage());
		errorBody.put("type", e.getClass().getCanonicalName());
		errorBody.put("name", "nullPointerException");
		errorBody.put("error_message", "the file or folder wasn't present or there was an exception in the report format");
//		System.out.println("errorBody.get(message) "+errorBody.get("message"));
		temp.put("error_body",errorBody);
//		JSONObject ob=(JSONObject)temp.get("error_body");
//		System.out.println("ob.get(code)"+ob.get("code"));
//		System.out.println("ob.get(type)"+ob.get("type"));
//		System.out.println("ob.get(name)"+ob.get("name"));
//		System.out.println("ob.get(message)"+ob.get("message"));
		temp.put("stackTrace",stackTraceStr);
	}
	
	public void handleSecurityException(Exception e,JSONObject temp) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		e.printStackTrace();
		String stackTraceStr=sw.toString();
		JSONObject errorBody=new JSONObject();
		errorBody.put("code", 2);
//		errorBody.put("type", e.getMessage());
		errorBody.put("type", e.getClass().getCanonicalName());
		errorBody.put("name", "fileAndFolderException");
		errorBody.put("error_message", "the file or folder may not be present");
		System.out.println("stack trace "+errorBody.get("stack trace"));
		temp.put("error_body",errorBody);
		temp.put("stackTrace",stackTraceStr);
	}
	public void handleNumberFormatException(Exception e,JSONObject temp) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		e.printStackTrace();
		String stackTraceStr=sw.toString();
		JSONObject errorBody=new JSONObject();
		errorBody.put("code", 3);
//		errorBody.put("type", e.getMessage());
		errorBody.put("type", e.getClass().getCanonicalName());
		errorBody.put("name", "FileFormatException");
		errorBody.put("error_message", "there may be an error in report format");
		System.out.println("stack trace "+errorBody.get("stack trace"));
		temp.put("error_body",errorBody);
		temp.put("stackTrace",stackTraceStr);
	}
	public void handleUnanticipatedException(Exception e,JSONObject temp) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		e.printStackTrace();
		String stackTraceStr=sw.toString();
		JSONObject errorBody=new JSONObject();
		errorBody.put("code", 4);
//		errorBody.put("type", e.getMessage());
		errorBody.put("type", e.getClass().getCanonicalName());
		errorBody.put("name", "UnanticipatedException");
		errorBody.put("error_message", "there was an unanticipated exception");
		System.out.println("stack trace "+errorBody.get("stack trace"));
		temp.put("error_body",errorBody);	
		temp.put("stackTrace",stackTraceStr);
	}
}
