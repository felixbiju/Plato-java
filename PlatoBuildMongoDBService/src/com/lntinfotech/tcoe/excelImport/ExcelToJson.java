package com.lntinfotech.tcoe.excelImport;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ExcelToJson {
	public String getImpactData(String path)
	{
	
		 String[][] sourceFileArray = null;
	     ArrayList<String> colnames=new ArrayList();		
	     colnames.add("Quotes"); /*colnames.add("NB/Issue"); colnames.add("Endorsement"); colnames.add("Cancellation"); colnames.add("Renewal");*/
	     
	     ArrayList<String> colnamesid=new ArrayList();		
	     colnamesid.add("5"); /*colnamesid.add("7"); colnamesid.add("8"); colnamesid.add("9"); colnamesid.add("18");*/
	 	JSONObject main=new JSONObject();
	    try {
			
	    	Workbook w=ExcelUtils.INSTANCE.createWorkbook(path);
			
			sourceFileArray =ExcelUtils.INSTANCE.readExcelToArray(w,2);
			int nocol=ExcelUtils.INSTANCE.readNoOfColumns(w,2);
			
			main.put("name","flare");
			main.put("description","flare");
			
			JSONArray firstlevel =new JSONArray();
			
			for(int i=0;i<colnames.size();i++)
			{	
				String policyTestCase="";
			    HashMap<String,HashMap<String,String>>  screen=new HashMap<String, HashMap<String, String>>();
			   
			    
				
				for(int sourceRow = 1; sourceRow <sourceFileArray.length; sourceRow++ )
				{
					
			 		//if(sourceFileArray[0][])
					if(sourceFileArray[sourceRow][Integer.parseInt(colnamesid.get(i))] != "")
					{	
						policyTestCase+=sourceFileArray[sourceRow][Integer.parseInt(colnamesid.get(i))]+",";
						
						// screen
						if(!screen.containsKey(sourceFileArray[sourceRow][0]))
						{
							 HashMap<String,String>  field=new HashMap<String, String>();
							field.put(sourceFileArray[sourceRow][2], sourceFileArray[sourceRow][Integer.parseInt(colnamesid.get(i))]);
							screen.put(sourceFileArray[sourceRow][0], field);
						
						}
						else
						{
						  HashMap<String,String>  tempfield=screen.get(sourceFileArray[sourceRow][0]);
						  
						  if(!tempfield.containsKey(sourceFileArray[sourceRow][2]))
							{
							  tempfield.put(sourceFileArray[sourceRow][2], sourceFileArray[sourceRow][Integer.parseInt(colnamesid.get(i))]);
								
							}
						  else
						  {
							  String temp=tempfield.get(sourceFileArray[sourceRow][2]);
							  tempfield.put(sourceFileArray[sourceRow][2], temp+","+sourceFileArray[sourceRow][Integer.parseInt(colnamesid.get(i))]);
								
						  } 
						  screen.put(sourceFileArray[sourceRow][0], tempfield);
									
						}	
					}
				}
				
				JSONObject policyobj= new JSONObject();
				policyobj.put("name",colnames.get(i));
				policyobj.put("description",policyTestCase);
				JSONArray screenarray=new JSONArray();
				
				for (String key : screen.keySet()) 
				{
				   System.out.println("Key = " + key + " - " + screen.get(key));
				    JSONObject screenobj=new JSONObject();
				    screenobj.put("name", key);
				    
				    JSONArray fieldarray=new JSONArray();
				    String screenTestcase="";
				    HashMap<String,String>  fieldtemp=screen.get(key);
				    for (String keyfield : fieldtemp.keySet()) 
					{
				    	
				    	   System.out.println("keyfield = " + keyfield + " - " + fieldtemp.get(keyfield));
				    	    JSONObject fieldobj=new JSONObject();
				    	    fieldobj.put("name", keyfield);
				    	    fieldobj.put("description", fieldtemp.get(keyfield));
				    	    fieldobj.put("size", fieldtemp.get(keyfield).split(",").length);
						    screenTestcase+=fieldtemp.get(keyfield).toString()+",";
						    fieldarray.add(fieldobj);
					}
				    screenobj.put("description", screenTestcase);
				    screenobj.put("children", fieldarray);
				    screenarray.add(screenobj);
				}
				policyobj.put("children", screenarray);
				firstlevel.add(policyobj);
			}
			
			main.put("children", firstlevel);
			
			  /* String pathNew= System.getProperty("catalina.base")+"/wtpwebapps/Plato/WEB-INF/web-resources/chart";
			   
			   ServletContext context = getContext();
			   String fullPath = context.getRealPath("/data");
				
			   File file = new File(pathNew+"/flare-labeled.json");
			
			    PrintWriter writer = new PrintWriter(file, "UTF-8");
			    writer.println(main);
			    writer.close();
			    */
			    
			System.out.println(""+main);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	 	
	    return main.toString();
	}


}
