//package com.lntinfotech.tcoe.excelImport;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Date;
//import java.io.BufferedInputStream;
//import net.sf.jxls.reader.ReaderBuilder;
//import net.sf.jxls.reader.XLSReadStatus;
//import net.sf.jxls.reader.XLSReader;
//import net.sf.jxls.transformer.Workbook;
//
//import org.apache.commons.lang.time.DateUtils;
//import org.apache.poi.ss.usermodel.DateUtil;
//
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.jfree.date.DateUtilities;
//import org.xml.sax.SAXException;
//import com.lntinfotech.virtuoso.common.dto.CustomizedERPJukeBoxSuite;
//import com.lntinfotech.virtuoso.common.dto.CustomRequirement;
//import com.lntinfotech.virtuoso.common.dto.CustomizedJukeBoxSuite;
//import com.lntinfotech.virtuoso.common.dto.ManualTestCaseSuiteTO;
//import com.lntinfotech.virtuoso.common.dto.CustomizedSuite;
//import com.lntinfotech.virtuoso.common.dto.CustomDefect;
//import com.lntinfotech.virtuoso.common.dto.ModuleTO;
//import com.lntinfotech.virtuoso.common.dto.ProjectTO;
//import com.lntinfotech.virtuoso.entities.KDTData;
//import com.lntinfotech.virtuoso.entities.Keywords;
//import com.lntinfotech.virtuoso.service.rmi.ControlFileData;
//
//
//
//public class JxlsReader {
//	@SuppressWarnings("unchecked")
//	public HashMap excelJxlsReader(File file,String template,String path){
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//   	    HashMap beans = null;
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			
//			InputStream inputXLS =new FileInputStream(xlsFile);
//		    CustomizedSuite ss = null;
//		    ModuleTO moduleTO = new ModuleTO();
//		    ProjectTO projectTO = new ProjectTO();
//		    List<CustomizedSuite> ssList = new ArrayList();
//		    beans = new HashMap();  
//		    beans.put("CustomizedSuite", ssList);
//		    beans.put("project", projectTO);		   
//		    beans.put("module", moduleTO);		            
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    System.out.println("read");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public HashMap BFSJxlsReader(File file,String template,String path) throws IOException, SAXException{
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//		HashMap beans = null;
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			InputStream inputXLS = new FileInputStream(xlsFile);
//		    CustomizedJukeBoxSuite ss = null;
//		    List<CustomizedJukeBoxSuite> ssList = new ArrayList();
//		    beans = new HashMap();
//		    beans.put("CustomizedSuite", ssList);
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    System.out.println("read");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//	
//	public HashMap DefectFileJxlsReader(File file,String template,String path){
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		//System.out.println("********* mappingFile= "+mappingFile);
//		//System.out.println("********* xlsFile= "+xlsFile);
//		InputStream inputXML;
//		HashMap beans = null;
//		List<CustomDefect> tempList = new ArrayList();
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			
//			InputStream inputXLS =new FileInputStream(xlsFile);
//			KDTData ss = null;		    
//		    List<CustomDefect> ssList = new ArrayList();
//		    ProjectTO projectTO = new ProjectTO();
//		    beans = new HashMap();  
//		    beans.put("defects", ssList);
//		    beans.put("project", projectTO);	
//		 
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//				
//			Set entries = beans.entrySet();
//	        Iterator it = entries.iterator();
//	        while (it.hasNext()) {
//	          Map.Entry entry = (Map.Entry) it.next();
//	        // System.out.println(entry.getKey() + "-->" + entry.getValue());
//	          if(entry.getKey().equals("defects")){
//	        	//  System.out.println(entry.getValue());
//	        	  tempList = (List<CustomDefect>) entry.getValue();
//	          }
//	        }
//	        } catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//	
//	
//	@SuppressWarnings("unchecked")
//	public HashMap ControlFileJxlsReader(File file,String template,String path){
//		HashMap cfMap = null;
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//		try {
//			inputXML = new FileInputStream(mappingFile);
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			InputStream inputXLS = new FileInputStream(xlsFile);
//		    List<ControlFileData> cdList = new ArrayList();
//		    cfMap = new HashMap();
//		    cfMap.put("ControlFileData", cdList);
//		    mainReader.read( inputXLS, cfMap);
//		    
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		return cfMap;
//		
//		
//	}
//		
//	
//	public HashMap RequirementJxlsReader(File file,String template,String path){
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//   	    HashMap beans = null;
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			
//			InputStream inputXLS =new FileInputStream(xlsFile);
//		    CustomRequirement ss = null;
//		    ProjectTO projectTO = new ProjectTO();
//		    ModuleTO moduleTO = new ModuleTO();
//		    List<CustomRequirement> ssList = new ArrayList();
//		    beans = new HashMap();  
//		    beans.put("requirement", ssList);
//		    beans.put("project", projectTO);	
//		    beans.put("module", moduleTO);	
//		  	            
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    System.out.println("read");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//
//	//added by sripad
//	@SuppressWarnings("unchecked")
//	public HashMap ERPJxlsReader(File file,String template,String path) throws IOException, SAXException{
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		//System.out.println("********* mappingFile= "+mappingFile);
//		//System.out.println("********* xlsFile= "+xlsFile);
//		InputStream inputXML;
//		HashMap beans = null;
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			InputStream inputXLS = new FileInputStream(xlsFile);
//			CustomizedERPJukeBoxSuite ss = null;
//		    List<CustomizedERPJukeBoxSuite> ssList = new ArrayList();
//		   
//		    beans = new HashMap();
//		    beans.put("CustomizedSuite", ssList);
//		  
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//	
//	
//	
//	public HashMap excelKDTJxlsReader(File file,String template,String path ){
//		//file.toString();
//		
//		
//		String strBeanput="";
//		HashMap beans = null;
//		
//			
//			 strBeanput="ControlFileData";
//			 String mappingFile =path + "\\" +  template.trim();// "D:\\data\\KDTConfig.xml";
//				String xlsFile = file.toString();//"D:\\Priyada\\Template\\KDT template\\PeopleSoft2.xls";
//				 List<ControlFileData> tempList = new ArrayList();
//				
//				InputStream inputXML;
//		   	  
//				try {
//					inputXML = new FileInputStream(mappingFile);   
//					XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//					
//					InputStream inputXLS =new FileInputStream(xlsFile);
//					ControlFileData ss = null;		    
//				    List<ControlFileData> ssList = new ArrayList();
//				    beans = new HashMap();  
//				    beans.put(strBeanput, ssList);
//				    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//						
//					Set entries = beans.entrySet();
//			        Iterator it = entries.iterator();
//			        while (it.hasNext()) {
//			          Map.Entry entry = (Map.Entry) it.next();
//			       //  System.out.println(entry.getKey() + "-->" + entry.getValue());
//			          if(entry.getKey().equals(strBeanput)){
//			        //	  System.out.println(entry.getValue());
//			        	  tempList = (List<ControlFileData>) entry.getValue();
//			        	  		        	  
//			          }
//			          
//			         
//			        }
//				    
//				  
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SAXException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvalidFormatException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//		
//		
//		
//		return beans;
//	}
//
//	public HashMap excelSuiteKDTJxlsReader(File file, String template,String path) {
//		
//		
//		HashMap beans = null;
//		 String strBeanput="kdtdata";
//		 String mappingFile =path + "\\" +  template.trim();// "D:\\data\\KDTConfig.xml";
//			String xlsFile = file.toString();//"D:\\Priyada\\Template\\KDT template\\PeopleSoft2.xls";
//			 List<KDTData> tempList = new ArrayList();
//			
//			InputStream inputXML;
//	   	   
//			try {
//				inputXML = new FileInputStream(mappingFile);   
//				XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//				
//				InputStream inputXLS =new FileInputStream(xlsFile);
//				KDTData ss = null;		    
//			    List<KDTData> ssList = new ArrayList();
//			    beans = new HashMap();  
//			    beans.put(strBeanput, ssList);
//			    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//					
//				Set entries = beans.entrySet();
//		        Iterator it = entries.iterator();
//		        while (it.hasNext()) {
//		          Map.Entry entry = (Map.Entry) it.next();
//		        // System.out.println(entry.getKey() + "-->" + entry.getValue());
//		          if(entry.getKey().equals(strBeanput)){
//		        	//  System.out.println(entry.getValue());
//		        	  tempList = (List<KDTData>) entry.getValue();
//		          }
//		        }
//		        	  
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (SAXException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InvalidFormatException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		        	  		        	  
//		return beans;
//	}
//
//	public HashMap manualSuiteJxlsReader(File file, String template, String path) {
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//   	    HashMap beans = null;
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			
//			InputStream inputXLS =new FileInputStream(xlsFile);
//		
//		    ProjectTO projectTO = new ProjectTO();
//		    ModuleTO moduleTO = new ModuleTO();
//		    List<CustomizedSuite> ssList = new ArrayList();
//		    beans = new HashMap();  
//		    beans.put("mtsData", ssList);
//		    beans.put("project", projectTO);	
//		    beans.put("module", moduleTO);	
//		  	            
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//	
//	public static void main(String[] args) {
//		File file = new File("D:\\New Folder\\Order Management.xls");
//		String template = "ERPConfig.xml";
//		String path = "D:\\New Folder";
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//		
//   	    HashMap beans = null;
//		try {
//			
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//
//			InputStream inputXLS =new FileInputStream(xlsFile);
//			CustomizedERPJukeBoxSuite ss = null;
//		    ModuleTO moduleTO = new ModuleTO();
//		    ProjectTO projectTO = new ProjectTO();
//		    List<CustomizedERPJukeBoxSuite> ssList = new ArrayList();
//		    beans = new HashMap();  
//		    beans.put("CustomizedSuite", ssList);
//		    beans.put("project", projectTO);		   
//		    beans.put("module", moduleTO);	
//		    
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    System.out.println("read "+ssList.get(0).getLastModificationDate());
//		    //String t = ssList.get(0).getLastModificationDate();
//		    String t = "41066";
//		    Date dt = DateUtil.getJavaDate(Double.parseDouble(t));
//		    System.out.println("dt ==> "+dt);
//		    System.out.println("day == "+dt.getDay());
//		    System.out.println("month == "+dt.getMonth());
//		    System.out.println("year == "+dt.getYear());
//		    System.out.println("date == "+dt.getDate());
//		    
//		    String formats[]={"dd-MM-yyyy","d-MMM-yyyy","dd/MM/yyyy","EEE, MMM d,yyyy","dd-MM-yyyy HH:mm:ss"};        
//		    SimpleDateFormat sdf=new SimpleDateFormat(formats[1]);  
//		    String d=sdf.format(dt);        
//		    System.out.println(d);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public HashMap QC_MTCJxlsReader(File file, String template, String path) {
//		String mappingFile = path + "\\" +  template.trim();
//		String xlsFile = file.toString();
//		InputStream inputXML;
//   	    HashMap beans = null;
//		try {
//			inputXML = new FileInputStream(mappingFile);   
//			XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
//			
//			InputStream inputXLS =new FileInputStream(xlsFile);
//		
//		    List<CustomizedSuite> ssList = new ArrayList();
//		    beans = new HashMap();  
//		    beans.put("QCData", ssList);
//		    	            
//		    XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
//		    
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return beans;
//	}
//
//}
//	
