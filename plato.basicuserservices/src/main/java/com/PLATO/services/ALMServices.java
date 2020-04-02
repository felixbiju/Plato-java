package com.PLATO.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.PLATO.alm.Infrastucture.AlmConnector;
import com.PLATO.alm.Infrastucture.Constants;
import com.PLATO.alm.Infrastucture.RestConnector;
import com.PLATO.userTO.AlmInfoTO;

/**
 * @author 10643380(Rahul Bhardwaj)
 * */
@Path("ALMServices")
public class ALMServices {
	private static final Logger logger = Logger.getLogger(ALMServices.class);
	RestConnector restConnector;
	@POST
	@Path("getAllTests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTests(AlmInfoTO almInfoTO) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			com.PLATO.alm.Infrastucture.Response response=null;
			if(almInfoTO.isAbove12()) {
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/tests";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
			}else {
				
			}
			alm.logout();
			return Response.status(Response.Status.OK).entity(response.getResponseData()).build();	
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();	
		}
	}
	private String getAllTests2(AlmInfoTO almInfoTO) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			com.PLATO.alm.Infrastucture.Response response=null;
			if(almInfoTO.isAbove12()) {
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/tests";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
			}else {
				
			}
			alm.logout();
			return new String(response.getResponseData());	
		}catch(Exception e) {
			e.printStackTrace();
			return null;	
		}		
	} 
	
	private String getAllTestInstances(AlmInfoTO almInfoTO) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=false;
			com.PLATO.alm.Infrastucture.Response response=null;
			if(almInfoTO.isAbove12()) {
				loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout();
			}else {
				loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();				
			}
			return new String(response.getResponseData());	
		}catch(Exception e) {
			e.printStackTrace();
			return null;	
		}		
	} 
	
	private String getTest(AlmInfoTO almInfoTO,String id) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			com.PLATO.alm.Infrastucture.Response response=null;
			if(almInfoTO.isAbove12()) {
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/tests/"+id;
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
			}else {
				
			}
			alm.logout();
			return new String(response.getResponseData());	
		}catch(Exception e) {
			e.printStackTrace();
			return null;	
		}				
	}
	
	private String getTestInstanceById(AlmInfoTO almInfoTO,String id) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=false;
			com.PLATO.alm.Infrastucture.Response response=null;
			if(almInfoTO.isAbove12()) {
				loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances/"+id;
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout();
			}else {
				loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances/"+id;
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();
			}

			return new String(response.getResponseData());	
		}catch(Exception e) {
			e.printStackTrace();
			return null;	
		}				
	}
	
	private boolean setTest(AlmInfoTO almInfoTO,String id,String doc) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
			com.PLATO.alm.Infrastucture.Response response=null;
			System.out.println("setTest doc");
			System.out.println(doc);
			if(almInfoTO.isAbove12()) {
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/tests/"+id;
				url=restConnector.buildUrlForQC12(url);
				System.out.println("setTest url");
				System.out.println(url);
				Map<String, String> map = new HashMap<String, String>();
				map.put("Accept", "application/xml");
				map.put("Content-Type","application/xml");
				response=restConnector.httpPut(url, doc.getBytes(), map);
				System.out.println("setTest Response code "+response.getStatusCode());
				System.out.println("setTest "+response.toString());
			}else {
				
			}
			alm.logout();
			return true;	
		}catch(Exception e) {
			e.printStackTrace();
			return false;	
		}			
	}
	
	private boolean setTestInstance(AlmInfoTO almInfoTO,String id,String doc) {
		AlmConnector alm = new AlmConnector();
		try {
			restConnector= RestConnector.getInstance();
			restConnector.init(new HashMap<String, String>(), Constants.HOST,
					Constants.DOMAIN, Constants.PROJECT);
			boolean loginResponse=false;
			com.PLATO.alm.Infrastucture.Response response=null;
			System.out.println("setTest doc");
			System.out.println(doc);
			if(almInfoTO.isAbove12()) {
				loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances/"+id;
				url=restConnector.buildUrlForQC12(url);
				System.out.println("setTest url");
				System.out.println(url);
				Map<String, String> map = new HashMap<String, String>();
				map.put("Accept", "application/xml");
				map.put("Content-Type","application/xml");
				response=restConnector.httpPut(url, doc.getBytes(), map);
				System.out.println("setTest Response code "+response.getStatusCode());
				System.out.println("setTest "+response.toString());
				alm.logout();
			}else {
				loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances/"+id;
				url=restConnector.buildUrlForQC12(url);
				System.out.println("setTest url");
				System.out.println(url);
				Map<String, String> map = new HashMap<String, String>();
				map.put("Accept", "application/xml");
				map.put("Content-Type","application/xml");
				response=restConnector.httpPut(url, doc.getBytes(), map);
				System.out.println("setTest Response code "+response.getStatusCode());
				System.out.println("setTest "+response.toString());
				alm.logout11();				
			}
			return true;	
		}catch(Exception e) {
			e.printStackTrace();
			return false;	
		}			
	}
	
	@POST
	@Path("findAndUpdate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAndUpdate(AlmInfoTO almInfoTO){
		try {
			almInfoTO.setDomain(Constants.DOMAIN);
			almInfoTO.setProject(Constants.PROJECT);
			almInfoTO.setAbove12(Boolean.parseBoolean(Constants.ABOVE12));
			String testsXml=getAllTests2(almInfoTO);
			String requiredTestXml=null;
			String id=null;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(testsXml)));
			NodeList entities=doc.getElementsByTagName("Entity");
			for(int i=0;i<entities.getLength();i++) {
				Element entity=(Element)entities.item(i);
				Element fields=(Element)entity.getElementsByTagName("Fields").item(0);
				Element nameValue=null;
				Element idValue=null;
				Element parentId=null;
				NodeList fieldList=entity.getElementsByTagName("Field");
				System.out.println("fieldList.length "+fieldList.getLength());
				for(int j=0;j<fieldList.getLength();j++) {
					Element field=(Element)fieldList.item(j);
//					System.out.println("field.getAttribute(Name) "+field.getAttribute("Name"));
					if(field.getAttribute("Name").equals("name")) {
						nameValue=(Element)field.getElementsByTagName("Value").item(0);
//						System.out.println("nameValue "+nameValue.getTextContent());
					}else if(field.getAttribute("Name").equals("id")) {
						idValue=(Element)field.getElementsByTagName("Value").item(0);
//						System.out.println("idValue "+idValue.getTextContent());
					}else if(field.getAttribute("Name").equals("parent-id")) {
						parentId=(Element)field.getElementsByTagName("Value").item(0);
					}
				}
				if(nameValue!=null &&nameValue.getTextContent().equals(almInfoTO.getTestName())&&Integer.parseInt(parentId.getTextContent())>=0) {
					if(idValue!=null) {
						System.out.println("oh yeah id "+idValue.getTextContent());
						id=idValue.getTextContent();
						requiredTestXml=getTest(almInfoTO,id);
					}
					break;
				}
//				System.out.println(entity.getAttribute("Type"));
			}
			//changing doc to the single xml Value that is required
			System.out.println("requiredTestXml "+requiredTestXml);
			doc=docBuilder.parse(new InputSource(new StringReader(requiredTestXml)));
			NodeList fields=doc.getElementsByTagName("Field");
			for(int i=0;i<fields.getLength();i++) {
				Element field=(Element)fields.item(i);
				if(field.getAttribute("Name").equals("exec-status")) {
					Element value=(Element)field.getElementsByTagName("Value").item(0);
					value.setTextContent(almInfoTO.getExecStatus());
					System.out.println("doc to string"+doc.toString());
					StringWriter sw = new StringWriter(); 
					Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
					serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
					String st=sw.toString(); 
					setTest(almInfoTO,id,st);
					break;
				}
			}
			return Response.status(Response.Status.OK).entity(getTest(almInfoTO,id)).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();			
		}

	}
	
	@POST
	@Path("findAndUpdateTestInstance")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAndUpdateTestInstance(AlmInfoTO almInfoTO){
		try {
			almInfoTO.setDomain(Constants.DOMAIN);
			almInfoTO.setProject(Constants.PROJECT);
			almInfoTO.setAbove12(Boolean.parseBoolean( Constants.ABOVE12));
			String testsXml=getAllTestInstances(almInfoTO);
			String requiredTestXml=null;
			String id=null;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(testsXml)));
			NodeList entities=doc.getElementsByTagName("Entity");
			for(int i=0;i<entities.getLength();i++) {
				Element entity=(Element)entities.item(i);
				Element fields=(Element)entity.getElementsByTagName("Fields").item(0);
				Element nameValue=null;
				Element idValue=null;
				Element parentId=null;
				NodeList fieldList=entity.getElementsByTagName("Field");
				System.out.println("fieldList.length "+fieldList.getLength());
				for(int j=0;j<fieldList.getLength();j++) {
					Element field=(Element)fieldList.item(j);
//					System.out.println("field.getAttribute(Name) "+field.getAttribute("Name"));
					if(field.getAttribute("Name").equals("name")) {
						nameValue=(Element)field.getElementsByTagName("Value").item(0);
//						System.out.println("nameValue "+nameValue.getTextContent());
					}else if(field.getAttribute("Name").equals("id")) {
						idValue=(Element)field.getElementsByTagName("Value").item(0);
//						System.out.println("idValue "+idValue.getTextContent());
					}
				}
				if(nameValue!=null &&nameValue.getTextContent().equals(almInfoTO.getTestName())) {
					if(idValue!=null) {
						System.out.println("oh yeah id "+idValue.getTextContent());
						id=idValue.getTextContent();
						requiredTestXml=getTestInstanceById(almInfoTO,id);
					}
					break;
				}
//				System.out.println(entity.getAttribute("Type"));
			}
			//changing doc to the single xml Value that is required
			System.out.println("requiredTestXml "+requiredTestXml);
			doc=docBuilder.parse(new InputSource(new StringReader(requiredTestXml)));
			NodeList fields=doc.getElementsByTagName("Field");
			for(int i=0;i<fields.getLength();i++) {
				Element field=(Element)fields.item(i);
				if(field.getAttribute("Name").equals("status")) {
					Element value=(Element)field.getElementsByTagName("Value").item(0);
					value.setTextContent(almInfoTO.getExecStatus());
					System.out.println("doc to string"+doc.toString());
					StringWriter sw = new StringWriter(); 
					Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
					serializer.transform(new DOMSource(doc), new StreamResult(sw)); 
					String st=sw.toString(); 
					setTestInstance(almInfoTO,id,st);
					break;
				}
			}
			return Response.status(Response.Status.OK).entity(getTestInstanceById(almInfoTO,id)).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();			
		}

	}
	
	@GET
	@Path("getAlmInfoTO")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlmInfoTO() {
		AlmInfoTO almInfoTO=new AlmInfoTO();
		almInfoTO.setDomain("hello");
		almInfoTO.setProject("hello");
		almInfoTO.setExecStatus("exec");
		almInfoTO.setTestName("hsisi");
		almInfoTO.setReleaseName("R115");
		almInfoTO.setSubFolderNames(new ArrayList<String>());
		almInfoTO.getSubFolderNames().add("hello");
		almInfoTO.setTestCaseStatusMap(new HashMap<String,String>());
		almInfoTO.getTestCaseStatusMap().put("y2k", "successful");
		return Response.status(Response.Status.OK).entity(almInfoTO).build();
	}
	
	@POST
	@Path("updateTestInstanceForRSAServices")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTestInstanceForRSAServices(String resultJSON) {
		JSONParser parser=new JSONParser();
		JSONObject result=new JSONObject();
		try {
			result=(JSONObject)parser.parse(resultJSON);
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error in parsing json").build();
		}
		JSONObject buildHistory=(JSONObject)result.get("BuildHistory");
		JSONObject liveBuildConsole=(JSONObject)buildHistory.get("LiveBuildConsole");
		JSONArray toolsLiveBuildConsole=(JSONArray)liveBuildConsole.get("tools");
		for(int i=0;i<toolsLiveBuildConsole.size();i++) {
			JSONObject obj=(JSONObject)toolsLiveBuildConsole.get(i);
			boolean isAlmChecked=(boolean)obj.get("isAlmChecked");
			System.out.println("isAlmChecked "+isAlmChecked);
			logger.debug("isAlmChecked "+isAlmChecked);
			if(isAlmChecked==true) {
				String toolName=(String)obj.get("toolName");
				toolName=toolName.replaceAll(" ", "_");
				String subJobName=(String)obj.get("tool_name");
				String status=(String)obj.get("tool_status");
				if(toolName.equalsIgnoreCase("junit")||toolName.equalsIgnoreCase("testng")) {
					System.out.println("junit or testng");
					logger.info("junit or testng");
					//if tool comes under build
//					buildAlm(buildHistory,subJobName,status);
					
				}else if(toolName.equalsIgnoreCase("uipath") || toolName.equalsIgnoreCase("hp_qtp")||toolName.equalsIgnoreCase("selenium")||toolName.equalsIgnoreCase("x_check")||toolName.equalsIgnoreCase("selenium_framework")||toolName.equalsIgnoreCase("SELENIUM_FRAMEWORK")||toolName.equalsIgnoreCase("hp qtp")) { 
					//if tool comes under automation
					logger.info("automation tool");
					automationAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("dtf") || toolName.equalsIgnoreCase("pdf") || toolName.equalsIgnoreCase("pdf_diff")) {
					System.out.println("dtf job :D");
					//if tool comes under data testing
//					dataTestingAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("jmeter")) {
					//if tool comes under performance
//					performanceAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("owasp_zap")) {
					//if tool comes under security
//					securityAlm(buildHistory,subJobName,status);
				}else if(toolName.equalsIgnoreCase("ibm_app_scan")){
//					securityAlm(buildHistory,subJobName,status);
				}
				obj.put("almNotification", "success");
			}
		}
		return Response.status(Response.Status.OK).entity("hi").build();
	}
	//old method
	/*void automationAlm(JSONObject buildHistory,String subJobName,String status){
		JSONObject automationTesting=(JSONObject)buildHistory.get("AutomationTesting");
		JSONArray tools=(JSONArray)automationTesting.get("Tools");
		AlmInfoTO almInfoTO=new AlmInfoTO();
		HashMap testCaseStatusMap=new HashMap<String,String>();
//		String releaseName="";
		ArrayList<String> subFolderNames=new ArrayList<String>();
		System.out.println("automation testing");
		logger.debug("subJobName "+subJobName);
		for(int i=0;i<tools.size();i++) {
			JSONObject temp=(JSONObject)tools.get(i);
			String tempJobName=(String)temp.get("tool_name");
			JSONArray toolReport=(JSONArray)temp.get("ToolReport");
			if(tempJobName.equals(subJobName)) {
				for(int j=0;j<toolReport.size();j++) {
					JSONObject toolReportObj=(JSONObject)toolReport.get(j);
					String chartName=(String)toolReportObj.get("chart_name");
					JSONArray tabularData=(JSONArray)toolReportObj.get("tabular_data");
					String qcPath=(String)toolReportObj.get("qcPath");
					String[] qcPathArr=qcPath.split("/");
					almInfoTO.setReleaseName(qcPathArr[0]);
					for(int k=1;k<qcPathArr.length;k++) {
						subFolderNames.add(qcPathArr[k]);
					}
					for(int k=0;k<tabularData.size();k++) {
						JSONObject tabularDataObj=(JSONObject)tabularData.get(k);
						JSONArray components=(JSONArray)tabularDataObj.get("components");
						for(int k1=0;k1<components.size();k1++) {
							JSONObject componentsObject=(JSONObject)components.get(k1);
							String componentId=(String)componentsObject.get("componentid");   //testCaseName
							String overAllStatus=(String)componentsObject.get("overallStatus"); //test case status
							logger.debug("componentID "+componentId+" overallStatus "+overAllStatus);
							if(overAllStatus.equalsIgnoreCase("pass")) {
								overAllStatus="Passed";
							}else if(overAllStatus.equalsIgnoreCase("fail")) {
								overAllStatus="Failed";
							}
							testCaseStatusMap.put(componentId,overAllStatus);
						}
					}
				}
				almInfoTO.setSubFolderNames(subFolderNames);
				almInfoTO.setTestCaseStatusMap(testCaseStatusMap);
				almInfoTO.setDomain(Constants.DOMAIN);
				almInfoTO.setProject(Constants.PROJECT);
				System.out.println("Constants.DOMAIN "+Constants.DOMAIN+" Constants.PROJECT "+Constants.PROJECT);
				almInfoTO.setAbove12(Boolean.parseBoolean(Constants.ABOVE12));
				System.out.println("512 almInfoTO.getDomain() "+almInfoTO.getDomain()+" almInfoTO.getProject() "+almInfoTO.getProject());
				updateTestInstanceForRsaNoService(almInfoTO);
				break;
			}
		}
	}*/
	//new method
	void automationAlm(JSONObject buildHistory,String subJobName,String status){
		JSONObject automationTesting=(JSONObject)buildHistory.get("AutomationTesting");
		JSONArray tools=(JSONArray)automationTesting.get("Tools");
		AlmInfoTO almInfoTO=new AlmInfoTO();
		HashMap testCaseStatusMap=new HashMap<String,String>();
//		String releaseName="";
		ArrayList<String> subFolderNames=new ArrayList<String>();
		System.out.println("automation testing");
		logger.debug("subJobName "+subJobName);
		for(int i=0;i<tools.size();i++) {
			JSONObject temp=(JSONObject)tools.get(i);
			String tempJobName=(String)temp.get("tool_name");
			JSONArray toolReport=(JSONArray)temp.get("ToolReport");
			if(tempJobName.equals(subJobName)) {
				for(int j=0;j<toolReport.size();j++) {
					JSONObject toolReportObj=(JSONObject)toolReport.get(j);
					String chartName=(String)toolReportObj.get("chart_name");
					JSONArray tabularData=(JSONArray)toolReportObj.get("tabular_data");

					for(int k=0;k<tabularData.size();k++) {
						HashMap<String,HashMap<String,String>> testCaseTestStepMap=new HashMap<String,HashMap<String,String>>();
						testCaseStatusMap=new HashMap<String,String>();
						subFolderNames=new ArrayList<String>();
						JSONObject tabularDataObj=(JSONObject)tabularData.get(k);
						
						String qcPath=(String)tabularDataObj.get("qcPath");
						String[] qcPathArr=qcPath.split("/");
						almInfoTO.setReleaseName(qcPathArr[0]);
						for(int k1=1;k1<qcPathArr.length;k1++) {
							subFolderNames.add(qcPathArr[k1]);
						}
						
						JSONArray components=(JSONArray)tabularDataObj.get("components");
						for(int k1=0;k1<components.size();k1++) {
							JSONObject componentsObject=(JSONObject)components.get(k1);
							String componentId=(String)componentsObject.get("componentid");   //testCaseName
							String overAllStatus=(String)componentsObject.get("overallStatus"); //test case status
							logger.debug("componentID "+componentId+" overallStatus "+overAllStatus);
							if(overAllStatus.equalsIgnoreCase("pass")) {
								overAllStatus="Passed";
							}else if(overAllStatus.equalsIgnoreCase("fail")) {
								overAllStatus="Failed";
							}
							JSONArray componentArray=(JSONArray)componentsObject.get("componentsArray");
							HashMap<String,String> tempHashMap=new HashMap<String,String>();
							for(int k2=0;k2<componentArray.size();k2++) {
								JSONArray componentArrayArray=(JSONArray)componentArray.get(k2);
								String stepDescription="";
								String stepStatus="";
								for(int k3=0;k3<componentArrayArray.size();k3++) {
									JSONObject componentArrayObj=(JSONObject)componentArrayArray.get(k3);
									if(componentArrayObj.containsKey("step_description")) {
										stepDescription=(String)componentArrayObj.get("step_description");
										System.out.println("step description "+stepDescription);
									}else if(componentArrayObj.containsKey("status")) {
										stepStatus=(String)componentArrayObj.get("status");
									}
								}
								tempHashMap.put(stepDescription, stepStatus);
							}
							testCaseStatusMap.put(componentId,overAllStatus);
							testCaseTestStepMap.put(componentId, tempHashMap);
						}
						almInfoTO.setSubFolderNames(subFolderNames);
						almInfoTO.setTestCaseStatusMap(testCaseStatusMap);
						almInfoTO.setDomain(Constants.DOMAIN);
						almInfoTO.setProject(Constants.PROJECT);
						System.out.println("Constants.DOMAIN "+Constants.DOMAIN+" Constants.PROJECT "+Constants.PROJECT);
						almInfoTO.setAbove12(Boolean.parseBoolean(Constants.ABOVE12));
						System.out.println("512 almInfoTO.getDomain() "+almInfoTO.getDomain()+" almInfoTO.getProject() "+almInfoTO.getProject());
						updateTestInstanceForRsaNoService(almInfoTO,testCaseTestStepMap);
					}
				}

			}
		}
	}
	
	@POST
	@Path("updateTestInstanceForRSA")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTestInstanceForRSA(AlmInfoTO almInfoTO) {
//		String releaseName="r415";
//		ArrayList<String> subFolderNames=new ArrayList<String>();
//		subFolderNames.add("jl_pet");
//		subFolderNames.add("small");
//		subFolderNames.add("medium");
//		
//		HashMap<String,String> testCaseStatusMap=new HashMap<String,String>();
//		testCaseStatusMap.put("TC01_AC_Correct_Info","Failed");
//		testCaseStatusMap.put("TC05_AC_Foreign_Currency","Failed");
//		testCaseStatusMap.put("TC04_AC_Same_Data","Failed");
//		testCaseStatusMap.put("TC03_AC_All_Missing_Info","Failed");
//		testCaseStatusMap.put("TC02_AC_Some_Missing_Info","Failed");
		try {
			System.out.println("getAllTestInstancesForRelease");
//			setAllTestInstancesForRelease(almInfoTO,almInfoTO.getReleaseName(),almInfoTO.getSubFolderNames(),almInfoTO.getTestCaseStatusMap());			
		}catch(Exception e) {
			e.printStackTrace();
		}

		return Response.status(Response.Status.OK).entity("hi").build();
	}
	
	public Response updateTestInstanceForRsaNoService(AlmInfoTO almInfoTO,HashMap<String,HashMap<String,String>>testCaseTestStepMap) {
//		String releaseName="r415";
//		ArrayList<String> subFolderNames=new ArrayList<String>();
//		subFolderNames.add("jl_pet");
//		subFolderNames.add("small");
//		subFolderNames.add("medium");
//		
//		HashMap<String,String> testCaseStatusMap=new HashMap<String,String>();
//		testCaseStatusMap.put("TC01_AC_Correct_Info","Failed");
//		testCaseStatusMap.put("TC05_AC_Foreign_Currency","Failed");
//		testCaseStatusMap.put("TC04_AC_Same_Data","Failed");
//		testCaseStatusMap.put("TC03_AC_All_Missing_Info","Failed");
//		testCaseStatusMap.put("TC02_AC_Some_Missing_Info","Failed");
		try {
			System.out.println("getAllTestInstancesForRelease");
			System.out.println("almInfoTO.getDomain() "+almInfoTO.getDomain()+" almInfoTO.getProject() "+almInfoTO.getProject());
			setAllTestInstancesForRelease(almInfoTO,almInfoTO.getReleaseName(),almInfoTO.getSubFolderNames(),almInfoTO.getTestCaseStatusMap(),testCaseTestStepMap);			
		}catch(Exception e) {
			e.printStackTrace();
		}

		return Response.status(Response.Status.OK).entity("hi").build();
	}
	private void setAllTestInstancesForRelease(AlmInfoTO almInfoTO,String releaseName,HashMap<String,String> testCaseStatusMap) {
		
	}
	private String getAllTestSetsForRelease() {
		return new String();
	}
	
	private void setAllTestInstancesForRelease(AlmInfoTO almInfoTO,String releaseName,ArrayList<String> subFolderNames,HashMap<String,String> testCaseStatusMap,HashMap<String,HashMap<String,String>>testCaseTestStepMap) throws IOException,SAXException,ParserConfigurationException,TransformerException  {
		System.out.println("getAllTestInstancesForRelease");
		System.out.println("getTestSetFolderByName");
		System.out.println("576 almInfoTO.getDomain() "+almInfoTO.getDomain()+" almInfoTO.getProject() "+almInfoTO.getProject());
		String xmlResultString=getTestSetFolderByName(almInfoTO,releaseName);
		System.out.println("xmlResultString "+xmlResultString);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xmlResultString)));
		System.out.println("xmlResultString completed");
		NodeList entities=doc.getElementsByTagName("Entity");
		String releaseId="";
		for(int i=0;i<entities.getLength();i++) {
			Element entity=(Element)entities.item(i);
//			Element fields=(Element)entity.getElementsByTagName("Fields").item(0);
			NodeList fieldList=entity.getElementsByTagName("Field");
			Element nameValue=null;
			Element idValue=null;
			for(int j=0;j<fieldList.getLength();j++) {
				Element field=(Element)fieldList.item(j);
				if(field.getAttribute("Name").equals("name")) {
					nameValue=(Element)field.getElementsByTagName("Value").item(0);
				}else if(field.getAttribute("Name").equals("id")) {
					idValue=(Element)field.getElementsByTagName("Value").item(0);
				}
			}
			if(nameValue!=null && nameValue.getTextContent().equals(releaseName)) {
				releaseId=idValue.getTextContent();
				break;
			}
		}
		System.out.println("getAllTestSetsForRelease");
		String testSetsXmlString=getAllTestSetsForRelease(almInfoTO,releaseId,subFolderNames,0);
		System.out.println("testSetsXmlString "+testSetsXmlString);
		doc = docBuilder.parse(new InputSource(new StringReader(testSetsXmlString)));
		entities=doc.getElementsByTagName("Entity");
		System.out.println("entities.getLength() "+entities.getLength());
		for(int i=0;i<entities.getLength();i++) {
			Element entity=(Element)entities.item(i);
			NodeList fieldList=entity.getElementsByTagName("Field");
			Element idValue=null;
			for(int j=0;j<fieldList.getLength();j++) {
				Element field=(Element)fieldList.item(j);
				if(field.getAttribute("Name").equals("id")) {
					idValue=(Element)field.getElementsByTagName("Value").item(0);
					break;
				}
			}
			String testInstancesXmlString=getTestInstancesByCycleId(almInfoTO,idValue.getTextContent());
			System.out.println("testInstancesXmlString "+testInstancesXmlString);
			Document testInstanceDoc=docBuilder.parse(new InputSource(new StringReader(testInstancesXmlString)));
			NodeList testInstanceEntities=testInstanceDoc.getElementsByTagName("Entity");
			System.out.println("testInstanceEntities.getLength() "+testInstanceEntities.getLength());
			for(int i1=0;i1<testInstanceEntities.getLength();i1++) {
				System.out.println("i1 "+i1);
				Element testInstance=(Element)testInstanceEntities.item(i1);
				Element testId=null;
				Element testInstanceId=null;
				NodeList testInstanceFieldList=testInstance.getElementsByTagName("Field");
				for(int j1=0;j1<testInstanceFieldList.getLength();j1++) {
					Element testInstanceField=(Element)testInstanceFieldList.item(j1);
					if(testInstanceField.getAttribute("Name").equals("id")) {
						testInstanceId=(Element)testInstanceField.getElementsByTagName("Value").item(0);
					}else if(testInstanceField.getAttribute("Name").equals("test-id")) {
						testId=(Element)testInstanceField.getElementsByTagName("Value").item(0);
					}
				}
				String singleTestInstanceXmlString=getTestInstanceById(almInfoTO,testInstanceId.getTextContent());
				System.out.println("singleTestInstanceXmlString "+singleTestInstanceXmlString);
				String testXmlString=getTestsById(almInfoTO,testId.getTextContent());
				Document testDoc=docBuilder.parse(new InputSource(new StringReader(testXmlString)));
				NodeList testDocFields=testDoc.getElementsByTagName("Field");
				Element testName=null;
				String status=null;
				Document singleTestInstanceDoc=docBuilder.parse(new InputSource(new StringReader(singleTestInstanceXmlString)));
				for(int i2=0;i2<testDocFields.getLength();i2++) {
					Element testDocField=(Element)testDocFields.item(i2);
					if(testDocField.getAttribute("Name").equals("name")) {
						testName=(Element)testDocField.getElementsByTagName("Value").item(0);
						System.out.println("tesDocField.name "+testName.getTextContent());
						break;
					}
				}
				if(testCaseStatusMap.containsKey(testName.getTextContent())) {
					System.out.println("contains key "+testName.getTextContent());
					if(testCaseStatusMap.get(testName.getTextContent())!=null) {
						status=testCaseStatusMap.get(testName.getTextContent());
					}
				}
				if(status!=null) {
					NodeList singleTestInstanceDocFields=singleTestInstanceDoc.getElementsByTagName("Field");
					for(int i3=0;i3<singleTestInstanceDocFields.getLength();i3++) {
						Element singleTestInstanceField=(Element)singleTestInstanceDocFields.item(i3);
						if(singleTestInstanceField.getAttribute("Name").equals("status")) {
							Element value=(Element)singleTestInstanceField.getElementsByTagName("Value").item(0);
							value.setTextContent(status);
							StringWriter sw = new StringWriter(); 
							Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
							serializer.transform(new DOMSource(singleTestInstanceDoc), new StreamResult(sw)); 
							String st=sw.toString();
							setTestInstance(almInfoTO,testInstanceId.getTextContent(),st);
						}
					}
				}
				try {
					updateTestRunsForTest(almInfoTO,Integer.parseInt(testId.getTextContent()),testCaseTestStepMap.get(testName.getTextContent()));
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
	private String getAllTestSetsForRelease(AlmInfoTO almInfoTO,String id,ArrayList<String> subFolderNames,int index) throws IOException,SAXException,ParserConfigurationException {
		System.out.println("index "+index+" id "+id);
		if(index==subFolderNames.size()){
			System.out.println("at end id "+id+" subFolderName "+subFolderNames.get(index-1));
			return getTestSetByParentId(almInfoTO,id);
		}else {
			String subFolderName=subFolderNames.get(index);
			index++;
			String parentId="";
			String xmlResultString=getTestSetFoldersByParentId(almInfoTO,id);
			System.out.println("541 xmlResultString "+xmlResultString);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(xmlResultString)));
			NodeList entities=doc.getElementsByTagName("Entity");
			for(int i=0;i<entities.getLength();i++) {
				Element entity=(Element)entities.item(i);
//				Element fields=(Element)entity.getElementsByTagName("Fields").item(0);
				NodeList fieldList=entity.getElementsByTagName("Field");
				Element nameValue=null;
				Element idValue=null;
				for(int j=0;j<fieldList.getLength();j++) {
					Element field=(Element)fieldList.item(j);
					if(field.getAttribute("Name").equals("name")) {
						nameValue=(Element)field.getElementsByTagName("Value").item(0);
					}else if(field.getAttribute("Name").equals("id")) {
						idValue=(Element)field.getElementsByTagName("Value").item(0);
					}
				}
				System.out.println("nameValue "+nameValue.getTextContent()+" subFolderName "+subFolderName);
				if(nameValue!=null && nameValue.getTextContent().equals(subFolderName)) {
					parentId=idValue.getTextContent();
					break;
				}
			}
			return getAllTestSetsForRelease(almInfoTO,parentId,subFolderNames,index);
		}
	}
	
	private String getTestSetFolderByName(AlmInfoTO almInfoTO,String name) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		try {
			if(almInfoTO.isAbove12()) {
				System.out.println("inside getTestSetFolderByName isAbove12");
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				System.out.println("login done");
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-set-folders?query={name["+name+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				System.out.println("got Response");
				alm.logout();
				System.out.println("logout");
			}else {
				System.out.println("739 almInfoTO.getDomain() "+almInfoTO.getDomain()+" almInfoTO.getProject() "+almInfoTO.getProject());
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-set-folders?query={name["+name+"]}";
				url=restConnector.buildUrlForQC12(url);
				System.out.println("url "+url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();				
			}

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return new String(response.getResponseData());
	}
		
	private String getTestSetFoldersByParentId(AlmInfoTO almInfoTO,String parentId) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-set-folders?query={parent-id["+parentId+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout();						
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-set-folders?query={parent-id["+parentId+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();				
			}

		}catch(Exception e) {
			return null;
		}
		return new String(response.getResponseData());		
	}
	
	private String getTestSetByParentId(AlmInfoTO almInfoTO,String parentId) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-sets?query={parent-id["+parentId+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout();					
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-sets?query={parent-id["+parentId+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();				
			}

		}catch(Exception e) {
			return null;
		}
		return new String(response.getResponseData());				
	}
	
	private String getTestInstancesByCycleId(AlmInfoTO almInfoTO,String cycleId) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances?query={cycle-id["+cycleId+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout();				
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/test-instances?query={cycle-id["+cycleId+"]}";
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();				
			}

		}catch(Exception e) {
			return null;
		}
		return new String(response.getResponseData());			
	}
	
	private String getTestsById(AlmInfoTO almInfoTO,String id) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/tests/"+id;
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout();					
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/tests/"+id;
				url=restConnector.buildUrlForQC12(url);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();				
			}

		}catch(Exception e) {
			return null;
		}
		return new String(response.getResponseData());	
	}
	private void updateTestRunsForTest(AlmInfoTO almInfoTO,int testId,HashMap<String,String> runStepsStatusMap) throws Exception{
		String latestEntity=getLastestRunForTestId(almInfoTO,testId);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		System.out.println("latestEntity "+latestEntity);
		Document doc = docBuilder.parse(new InputSource(new StringReader(latestEntity)));
		NodeList fieldList=doc.getElementsByTagName("Field");
		Element idValue=null;
		for(int j=0;j<fieldList.getLength();j++) {
			Element field=(Element)fieldList.item(j);
			if(field.getAttribute("Name").equals("id")) {
				idValue=(Element)field.getElementsByTagName("Value").item(0);
				break;
			}
		}
		int runId=Integer.parseInt(idValue.getTextContent());
		String allRunSteps=getAllRunSteps(almInfoTO,runId);
		System.out.println("allRunSteps "+allRunSteps);
		doc=docBuilder.parse(new InputSource(new StringReader(allRunSteps)));
		NodeList entities=doc.getElementsByTagName("Entity");
		int runStepId=0;
		for(int j=0;j<entities.getLength();j++) {
			Element entity=(Element)entities.item(j);
			//change this
			NodeList runStepsNL=entity.getElementsByTagName("Field");
			String runStepName="";
			for(int k=0;k<runStepsNL.getLength();k++) {
				Element field=(Element)runStepsNL.item(k);
				if(field.getAttribute("Name").equals("id")) {
					runStepId=Integer.parseInt(field.getElementsByTagName("Value").item(0).getTextContent());
					System.out.println("runStepId "+runStepId);
					
				}else if(field.getAttribute("Name").equals("name")) {
					runStepName=field.getElementsByTagName("Value").item(0).getTextContent();
					System.out.println("runStepName "+runStepName);
				}
			}
			String runStep=getRunStep(almInfoTO, runId,runStepId);
			System.out.println("runStep "+runStep);
			//put code over here
			Document docRunStep = docBuilder.parse(new InputSource(new StringReader(runStep)));
			System.out.println("docRunStep "+docRunStep);
			runStepsNL=docRunStep.getElementsByTagName("Field");
			for(int k=0;k<runStepsNL.getLength();k++) {
				Element field=(Element)runStepsNL.item(k);
				if(field.getAttribute("Name").equals("status")) {
					if(runStepsStatusMap.containsKey(runStepName)) {
						System.out.println("runStepsStatusMap.get(runStepName) "+runStepsStatusMap.get(runStepName));
						if(runStepsStatusMap.get(runStepName).toLowerCase().contains("pass")) {
							field.getElementsByTagName("Value").item(0).setTextContent("Passed");	
						}else if(runStepsStatusMap.get(runStepName).toLowerCase().contains("fail")) {
							field.getElementsByTagName("Value").item(0).setTextContent("Failed");	
						}					
					}
					break;
				}
			}
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(docRunStep), new StreamResult(sw)); 
			runStep=sw.toString();
			System.out.println("new Runstep "+runStep);
			setRunStep(almInfoTO, runId,runStepId,runStep);
		}
		
	}
	
	//for getting latest run for a testId 
	private String getLastestRunForTestId(AlmInfoTO almInfoTO,int testId) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		String entityString="";
		
		String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/runs?query={test-id["+testId+"]}&order-by={id[DESC]}";
		url=restConnector.buildUrlForQC12(url);
		System.out.println("url "+url);
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpGet(url, null, null);
				alm.logout();
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();
			}
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(new String(response.getResponseData()))));
			Element entity=(Element)doc.getElementsByTagName("Entity").item(0);
			System.out.println("1033response.getResponseData() "+response.getResponseData());
			StringWriter sw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(entity), new StreamResult(sw)); 
			entityString=sw.toString();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new String(entityString);
	}
	
	//for getting all run-steps for a particular run
	private String getAllRunSteps(AlmInfoTO almInfoTO,int runId) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		
		String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/runs/"+runId+"/run-steps";
		url=restConnector.buildUrlForQC12(url);
		System.out.println("url "+url);
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpGet(url, null, null);
				alm.logout();
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return new String(response.getResponseData());	
	}
	
	//for getting a particular run-step for a particular run
	private String getRunStep(AlmInfoTO almInfoTO,int runId,int runStepId) {
		System.out.println("runId "+runId+" runStepId "+runStepId);
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		
		String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/runs/"+runId+"/run-steps/"+runStepId;
		url=restConnector.buildUrlForQC12(url);
		System.out.println("url "+url);
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpGet(url, null, null);
				alm.logout();
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpGet(url, null, null);
				alm.logout11();
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return new String(response.getResponseData());	
	}
	
	//for setting runSteps of a particular runId
	private void setRunStep(AlmInfoTO almInfoTO,int runId,int runStepId,String doc) {
		AlmConnector alm = new AlmConnector();
		restConnector= RestConnector.getInstance();
		restConnector.init(new HashMap<String, String>(), Constants.HOST,
				Constants.DOMAIN, Constants.PROJECT);
		com.PLATO.alm.Infrastucture.Response response=null;
		
		String url="qcbin/rest/domains/"+almInfoTO.getDomain()+"/projects/"+almInfoTO.getProject()+"/runs/"+runId+"/run-steps/"+runStepId;
		url=restConnector.buildUrlForQC12(url);
		System.out.println("url "+url);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("Accept", "application/xml");
		map.put("Content-Type","application/xml");
		try {
			if(almInfoTO.isAbove12()) {
				boolean loginResponse=alm.login(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpPut(url, doc.getBytes(), map);
				alm.logout();
			}else {
				boolean loginResponse=alm.login11(Constants.USERNAME, Constants.PASSWORD);
				response=restConnector.httpPut(url, doc.getBytes(), map);
				alm.logout11();
			}		
		}catch(Exception e) {
			e.printStackTrace();
		}

		System.out.println("setTest Response code "+response.getStatusCode());
		System.out.println("setTest "+response.toString());
	}
	

}
