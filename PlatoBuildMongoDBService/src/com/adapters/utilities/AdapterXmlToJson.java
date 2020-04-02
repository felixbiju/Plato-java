package com.adapters.utilities;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
/**
 * @author 10643380(Rahul Bhardwaj)
 * */

public class AdapterXmlToJson {
	public JSONObject convertXmlToJson(Document doc){
		StringWriter sw = new StringWriter();
		JSONObject jsonObj=new JSONObject();
		try {
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
			String reportString=sw.toString();
			JSONParser parser = new JSONParser();
			return jsonObj=XML.toJSONObject(reportString);
			
		}catch(Exception e) {
			e.printStackTrace();
			return jsonObj;
		}

	}
}
