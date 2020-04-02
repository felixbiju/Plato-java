package com.adapters;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import com.mongo.constants.ConstantQueries;
import com.mongo.entities.ModuleSubJobsJenkins;
import com.mongo.entities.ModuleSubJobsJenkinsParameters;
import com.mongo.singletons.GenericDaoSingleton;
import com.mongo.constants.GlobalConstants;
import com.mongo.dao.GenericDao;

public class GenericURLApdapter {
	GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	
	private static final Logger logger=Logger.getLogger( GenericURLApdapter.class);
	/*FileFilter htmlFilter = new FileFilter() { 
		public boolean accept(File file) 
        { 
            return file.getName().endsWith("html"); 
            
        } 
    }; */
   
	

	@SuppressWarnings("unchecked")
	public JSONArray URLAdapter(String reportPath,String jobName,int nextBuildNumber) throws Exception
	{
		final FilenameFilter FILE_FILTER = new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				HashMap keyvalueMap=new HashMap();
				String report_pattern=new String("");;
				keyvalueMap.put("subModuleName", jobName);//check db for correct name
				ModuleSubJobsJenkins dbModule=new ModuleSubJobsJenkins();
				try {
					dbModule = (ModuleSubJobsJenkins) genericDao.findUniqueByQuery(ConstantQueries.GET_SUBMODULE_BY_NAME,keyvalueMap);
					
					List<ModuleSubJobsJenkinsParameters> list1=dbModule.getModuleSubJobsJenkinsParametersList();
					for(ModuleSubJobsJenkinsParameters parameter:list1 )
					{
						if(parameter.getParameter_key().equalsIgnoreCase("report_pattern"))
							{
								report_pattern=parameter.getValue();
							}
					}
						
					
				}catch(NullPointerException e1)
				{
					logger.debug("Job does not exist in db:"+dbModule.getSubjob_name());
					System.out.println("Job does not exist in db:"+dbModule.getSubjob_name());
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				String report_pattern_Array[]=report_pattern.split(",");
				String extensionsRegex=new String(".*\\.(");
				Boolean extensionPresentFlag= false;
				ArrayList<String> fileNames = new ArrayList<String>();
				int i=0;
				for(String str:report_pattern_Array)
				{
					if(!str.contains("."))
					{
						extensionPresentFlag=true;
						if((!extensionsRegex.isEmpty())&&(!extensionsRegex.equals("")))
							extensionsRegex=extensionsRegex+"|"+str;
						else
						{
							extensionsRegex=extensionsRegex+str;
						}
					}
					else{
						fileNames.add(str);
					}
				}
			
				if(extensionPresentFlag)
				{
					extensionsRegex=extensionsRegex+")$";
				
				//if (name.matches(".*\\.(htm|html|json|css|svg|png)$")) {
					if (name.matches(extensionsRegex)||(fileNames.contains(name))) 
					{
						return (true);
					}
				}
				
				
				return false;
					
			}
		};
		
		//logger.info("inside URLAdapter: reportPath is " +reportPath);
		System.out.println("***************inside url adapter******************");
		String reportUrl=new String();
		reportUrl ="";
		File file=new File(reportPath);
		
		
			try{
				
					 File[] files = file.listFiles(FILE_FILTER); 
					// logger.info("no of html files found is "+htmlFiles.length);
					 System.out.println("no of files found is "+files.length);
					 for(File f : files){
						 reportUrl=reportUrl+GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/";
						 reportUrl=reportUrl+f.getName()+",,,";
							System.out.println(f.getName());
					 }
					 if((reportUrl!=null)&&(reportUrl!=""))
						 reportUrl=reportUrl.substring(0,reportUrl.length()-3);
					 
					
			}
			catch(NullPointerException e)
			{
				System.out.println(" NO file present ");
				
				e.printStackTrace();
			}
			 
		
		
		JSONArray report= new JSONArray();
		JSONObject tool = new JSONObject();
		//Document doc = Jsoup.parse(file, null);
		JSONArray chart_labels = new JSONArray();
		JSONArray chart_values = new JSONArray();
		JSONArray tabular_data = new JSONArray();
		String pass = "0";
		String fail = "0";
		String chartName = "reportURL";
		String total_scenario_executed = "NA";
		String total_execution_time = "NA";
		
		tool.put("chart_name", chartName);
		chart_labels.add("passed");
		chart_labels.add("failed");
		tool.put("chart_labels", chart_labels);
		chart_values.add(pass);
		chart_values.add(fail);
		tool.put("chart_values", chart_values);
		tool.put("tabular_data", tabular_data);
		
	
		
		
		//automation_specific
		tool.put("total_scenario_executed", total_scenario_executed);
		tool.put("total_execution_time", total_execution_time);
		
		//security->default 
		//performance->default
		
		//data_testing_specific
		tool.put("level", "NA");
		
		
		tool.put("report_url", reportUrl);
		report.add(tool);
		if((reportUrl==null)||(reportUrl==""))
		{
			throw new NullPointerException();
		}
		
		return report;
	}
}
