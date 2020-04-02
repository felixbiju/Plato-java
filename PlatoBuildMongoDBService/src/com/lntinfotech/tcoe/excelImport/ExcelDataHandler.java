//package com.lntinfotech.tcoe.excelImport;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import net.sf.jxls.transformer.XLSTransformer;
//
//import org.apache.log4j.Logger;
//import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.xml.sax.SAXException;
//
//import com.lntinfotech.virtuoso.common.dto.AutoTestCaseTO;
//import com.lntinfotech.virtuoso.common.dto.BaseTO;
//import com.lntinfotech.virtuoso.common.dto.CustomDefect;
//import com.lntinfotech.virtuoso.common.dto.CustomRequirement;
//import com.lntinfotech.virtuoso.common.dto.CustomizedJukeBoxSuite;
//import com.lntinfotech.virtuoso.common.dto.CustomizedERPJukeBoxSuite;
//import com.lntinfotech.virtuoso.common.dto.CustomizedSuite;
//import com.lntinfotech.virtuoso.common.dto.DefectTO;
//import com.lntinfotech.virtuoso.common.dto.JukeboxDwbiLevel1TO;
//import com.lntinfotech.virtuoso.common.dto.JukeboxInsLevel1TO;
//import com.lntinfotech.virtuoso.common.dto.JukeboxLevel1TO;
//import com.lntinfotech.virtuoso.common.dto.ManualTestCaseImportTO;
//import com.lntinfotech.virtuoso.common.dto.ManualTestCaseSuiteTO;
//import com.lntinfotech.virtuoso.common.dto.ManualTestCaseTO;
//import com.lntinfotech.virtuoso.common.dto.ModuleTO;
//import com.lntinfotech.virtuoso.common.dto.ProjectTO;
//import com.lntinfotech.virtuoso.common.dto.RequirementTO;
//import com.lntinfotech.virtuoso.common.exceptions.BaseException;
//import com.lntinfotech.virtuoso.db.DAOFactory;
//import com.lntinfotech.virtuoso.db.DBNamedQueryConstants;
//import com.lntinfotech.virtuoso.db.interfaces.ApplicationDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ApplnDatabaseDAO;
//import com.lntinfotech.virtuoso.db.interfaces.AutoExecDAO;
//import com.lntinfotech.virtuoso.db.interfaces.AutoTestCaseDAO;
//import com.lntinfotech.virtuoso.db.interfaces.BaseDAO;
//
//import com.lntinfotech.virtuoso.db.interfaces.BusinessProcessDAO;
//import com.lntinfotech.virtuoso.db.interfaces.DTFDatabaseDAO;
//import com.lntinfotech.virtuoso.db.interfaces.DTTMapDAO;
//import com.lntinfotech.virtuoso.db.interfaces.DttjoinDAO;
//import com.lntinfotech.virtuoso.db.interfaces.EnvironmentDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ExecScheduleDAO;
//import com.lntinfotech.virtuoso.db.interfaces.LoginDAO;
//import com.lntinfotech.virtuoso.db.interfaces.MachineDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ManualExecDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ManualTestCaseDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ManualTestCaseSuiteDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ProjectDAO;
//import com.lntinfotech.virtuoso.db.interfaces.QueryLogDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ReleaseTaskDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ReleasesDAO;
//import com.lntinfotech.virtuoso.db.interfaces.ReqManualAutoTestCaseDAO;
//import com.lntinfotech.virtuoso.db.interfaces.RequirementDAO;
//import com.lntinfotech.virtuoso.db.interfaces.RoleAllocationDAO;
//import com.lntinfotech.virtuoso.db.interfaces.SprintPlanDAO;
//import com.lntinfotech.virtuoso.db.interfaces.SuiteDAO;
//import com.lntinfotech.virtuoso.db.interfaces.TemplateMgmtDAO;
//import com.lntinfotech.virtuoso.db.interfaces.TestPlanDao;
//import com.lntinfotech.virtuoso.db.interfaces.ToolDAO;
//import com.lntinfotech.virtuoso.db.interfaces.UploadFileDAO;
//
//import com.lntinfotech.virtuoso.entities.KDTData;
//import com.lntinfotech.virtuoso.entities.Keywords;
//
//import com.lntinfotech.virtuoso.entities.TemplateFormat;
//import com.lntinfotech.virtuoso.service.RequirementServiceImpl;
//import com.lntinfotech.virtuoso.service.externalinterface.excel.JxlsReader;
//import com.lntinfotech.virtuoso.service.rmi.ControlFileData;
//
//
//public class ExcelDataHandler {
//
//
//
//	public ExcelDataHandler() {
//
//	}
//
//	@Override
//	public BaseTO customizeDataOnImport(BaseTO baseTO) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Workbook exportData(String oper,List<?> list,String file,String template,String type) throws BaseException {
//		// TODO Auto-generated method stub
//		HashMap fileData = new HashMap();
//		String modulename = null;
//		String relname = null;
//		String applName = null;
//		String domainName = null;
//		//msuite export
//		if((template.equals("ManualSuite"))&& (type.equals("Export"))){
//		List<ManualTestCaseSuiteTO> manualTestCaseSuiteTOList = null;
//		manualTestCaseSuiteTOList = (List<ManualTestCaseSuiteTO>) list;
//		if (manualTestCaseSuiteTOList != null && !manualTestCaseSuiteTOList.isEmpty()) {
//			ManualTestCaseSuiteTO manualTestCaseSuiteTO = manualTestCaseSuiteTOList.get(0);
//			if (manualTestCaseSuiteTO.getBusinessProcess() == null) {
//				fileData.put("ProjectTO", manualTestCaseSuiteTO.getManualTestCase().getProjectApplication().getProject());
//			} else {
//				fileData.put("ProjectTO", manualTestCaseSuiteTO.getBusinessProcess().getProject());
//			}
//		}
//		
//		fileData.put("ManualTestCaseSuiteToList", manualTestCaseSuiteTOList);
//		}
//		//req export 
//		if((template.equals("Requirement"))&& (type.equals("Export"))){
//			List<RequirementTO> requirementTOList = (List<RequirementTO>) list;
//			if(requirementTOList!=null && !requirementTOList.isEmpty()){
//				RequirementTO requirementTO = requirementTOList.get(0);
//				fileData.put("ProjectName", requirementTO.getProjName());
//				fileData.put("ProjectCode", requirementTO.getExprojCode());
//			
//				modulename = requirementTO.getModule().getModuleName();
//			}
//			fileData.put("RequirementToList", requirementTOList);
//		}
//		//mtc export
//		if((template.equals("ManualTestCase"))&& (type.equals("Export"))){
//			List<ManualTestCaseTO> mtcTOList = (List<ManualTestCaseTO>) list;
//			if(mtcTOList!=null && !mtcTOList.isEmpty()){
//				ManualTestCaseTO manualTestCaseTO = mtcTOList.get(0);
//				fileData.put("ProjectName", manualTestCaseTO.getProjName());
//				fileData.put("ProjectCode", manualTestCaseTO.getExprojCode());
//				modulename = manualTestCaseTO.getModule().getModuleName();
//			}
//			fileData.put("mtcToList", mtcTOList);
//		}
//		
//		//atc export
//		if((template.equals("AutoTestCase"))&& (type.equals("Export"))){
//			List<AutoTestCaseTO> atcTOList = (List<AutoTestCaseTO>) list;
//			if(atcTOList!=null && !atcTOList.isEmpty()){
//				AutoTestCaseTO autoTestCaseTO = atcTOList.get(0);
//				fileData.put("ProjectName", autoTestCaseTO.getProjName());
//				fileData.put("ProjectCode", autoTestCaseTO.getExprojCode());
//				applName = "KDT_"+autoTestCaseTO.getProjectApplication().getApplication().getApplnName();
//			}
//			fileData.put("atcToList", atcTOList);
//		}
//		//defect export
//		if((template.equals("Defect"))&& (type.equals("Export"))){
//			List<DefectTO> defList = (List<DefectTO>) list;
//			if(defList!=null && !defList.isEmpty()){
//				DefectTO defectTO  = defList.get(0);
//				fileData.put("ProjectName", defectTO.getProjName());
//				fileData.put("ProjectCode", defectTO.getExprojCode());
//				relname = defectTO.getQaRelease().getReleaseName();
//			}
//			fileData.put("defectToList", defList);
//		}
//		//bfs jb export
//		if((template.equals("BFS"))&& (type.equals("Export"))){
//			List<JukeboxLevel1TO> jukeboxLevel1TOs = (List<JukeboxLevel1TO>) list;
//			if(jukeboxLevel1TOs!=null && !jukeboxLevel1TOs.isEmpty()){
//				JukeboxLevel1TO  jukeboxLevel1TO = jukeboxLevel1TOs.get(0);
//				domainName = jukeboxLevel1TO.getDomain().getDomainName();
//				fileData.put("JukeboxToList", jukeboxLevel1TOs);
//			}
//		}
//		//added by sripad for ERP jukebox export on 19-Jul-2012
//		if((template.equals("ERP"))&& (type.equals("Export"))){
//			List<CustomizedERPJukeBoxSuite> jukeboxLevel1TOs = (List<CustomizedERPJukeBoxSuite>) list;
//			if(jukeboxLevel1TOs!=null && !jukeboxLevel1TOs.isEmpty()){
//				CustomizedERPJukeBoxSuite  suit = jukeboxLevel1TOs.get(0);
//				domainName = suit.getDomain();
//				fileData.put("JukeboxToList", jukeboxLevel1TOs);
//			}
//		}
//		if((template.equals("ERPScenario"))&& (type.equals("Export"))){
//			List<CustomizedERPJukeBoxSuite> jukeboxScenario = (List<CustomizedERPJukeBoxSuite>) list;
//			if(jukeboxScenario!=null && !jukeboxScenario.isEmpty()){
//				CustomizedERPJukeBoxSuite  suit = jukeboxScenario.get(0);
//				domainName = suit.getDomain();
//				fileData.put("JukeboxToList", jukeboxScenario);
//			}
//		}
//		if((template.equals("INS"))&& (type.equals("Export"))){
//			List<JukeboxInsLevel1TO> jukebox = (List<JukeboxInsLevel1TO>) list;
//			if(jukebox!=null && !jukebox.isEmpty()){
//				JukeboxInsLevel1TO  suit = jukebox.get(0);
//				domainName = suit.getDomain().getDomainName();
//				//domainName = "INS Export";
//				fileData.put("JukeboxToList", jukebox);
//			}
//			
//		}
//		if((template.equals("DWBI"))&& (type.equals("Export"))){
//			List<JukeboxDwbiLevel1TO> jukebox = (List<JukeboxDwbiLevel1TO>) list;
//			if(jukebox!=null && !jukebox.isEmpty()){
//				JukeboxDwbiLevel1TO  suit = jukebox.get(0);
//				domainName = suit.getDomain().getDomainName();
//				fileData.put("JukeboxToList", jukebox);
//			}
//			
//		}
//		if((template.equals("Mob"))&& (type.equals("Export"))){
//			List<CustomizedERPJukeBoxSuite> jukeboxLevel1TOs = (List<CustomizedERPJukeBoxSuite>) list;
//			if(jukeboxLevel1TOs!=null && !jukeboxLevel1TOs.isEmpty()){
//				CustomizedERPJukeBoxSuite  suit = jukeboxLevel1TOs.get(0);
//				domainName = suit.getDomain();
//				fileData.put("JukeboxToList", jukeboxLevel1TOs);
//			}
//		}
//		//end by sripad
//		 XLSTransformer transformer = new XLSTransformer();
//		 FileInputStream fileInputStream;
//		 Workbook workbook = null;
//	        try {
//	        	fileInputStream = new FileInputStream(file);
//	        	workbook = transformer.transformXLS(fileInputStream, fileData);
//	        	if(oper.equals("BFS")){
//	        		workbook.setSheetName(0, domainName);
//	        	}
//	        	
//	        	if(oper.equals("mod")){
//		        	workbook.setSheetName(0, modulename);
//		        	}
//	        	if(oper.equals("rel")){
//		        	workbook.setSheetName(0, relname);
//		        	}
//	        	if(oper.equals("appl")){
//		        	workbook.setSheetName(0, applName);
//		        	}
//	        	if(oper.equals("INS")){
//		        	workbook.setSheetName(0, domainName);
//		        	}
//	        	if(oper.equals("DWBI")){
//		        	workbook.setSheetName(0, domainName);
//		        	}
//	        	if(oper.equals("ERP")){
//		        	workbook.setSheetName(0, domainName);
//		        	}
//	        	if(oper.equals("Mob")){
//		        	workbook.setSheetName(0, domainName);
//		        	}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				throw new BaseException("Error");
//			}
//		
//		return workbook;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public BaseTO importFileData(File file,String template,String path, String type) {
//		// TODO Auto-generated method stub
//		BaseTO baseTO = new BaseTO();
//		JxlsReader jxlsReader = new JxlsReader();
//		HashMap datamap;
//		if(type.equals("Suite_Excel")){
//			datamap = jxlsReader.excelJxlsReader(file, template ,path);
//			ModuleTO moduleTO = new ModuleTO();
//		    ProjectTO projectTO = new ProjectTO();
//		    List<CustomizedSuite> tempList = null;
//			Set entries = datamap.entrySet();
//	        Iterator it = entries.iterator();
//	        while (it.hasNext()) {
//	          Map.Entry entry = (Map.Entry) it.next();
//	        //  System.out.println(entry.getKey() + "-->" + entry.getValue());
//	          if(entry.getKey().equals("CustomizedSuite")){
//	        	  //System.out.println(entry.getValue());
//	        	  tempList = (List<CustomizedSuite>) entry.getValue();
//	        	 // System.out.println("******* "+tempList.get(0).getApplication());		        	  
//	          }
//	          if(entry.getKey().equals("project")){
//	        	  //System.out.println(entry.getValue());
//	        	  projectTO =  (ProjectTO) entry.getValue();
//	        	  		        	  
//	          }
//	          if(entry.getKey().equals("module")){
//	        	  //System.out.println(entry.getValue());
//	        	  moduleTO =  (ModuleTO) entry.getValue();
//	        	  		        	  
//	          }
//	        }
//	        baseTO.setProjImportTO(projectTO);
//	        baseTO.setModuleTO(moduleTO);
//	        baseTO.setCustomizedSuites(tempList);
//		}
//		if(type.equals("BFS")){
//			List<CustomizedJukeBoxSuite> tempList = null;
//				try {
//					datamap = jxlsReader.BFSJxlsReader(file, template, path);
//					 Set entries = datamap.entrySet();
//				     Iterator it = entries.iterator();
//				     while (it.hasNext()) {
//				          Map.Entry entry = (Map.Entry) it.next();
//				         System.out.println(entry.getKey() + "-->" + entry.getValue());
//				          if(entry.getKey().equals("CustomizedSuite")){
//				        	  //System.out.println(entry.getValue());
//				        	  tempList = (List<CustomizedJukeBoxSuite>) entry.getValue();
//				        	  		        	  
//				          }
//				        }
//				      
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SAXException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				baseTO.setCustomizedJukeBoxSuites(tempList);
//		}
//		
//		if(type.equals("CF")){
//			List<ControlFileData> cfdatas= null;
//			datamap = jxlsReader.ControlFileJxlsReader(file, template, path);
//			 Set entries = datamap.entrySet();
//		     Iterator it = entries.iterator();
//		     while (it.hasNext()) {
//		          Map.Entry entry = (Map.Entry) it.next();
//		         System.out.println(entry.getKey() + "-->" + entry.getValue());
//		          if(entry.getKey().equals("ControlFileData")){
//		        	  //System.out.println(entry.getValue());
//		        	  cfdatas = (List<ControlFileData>) entry.getValue();
//		        	  		        	  
//		          }
//		        }
//		     baseTO.setControlFileDatas(cfdatas);
//		}
//		
//
//		if(type.equals("Defect")){
//			List<CustomDefect> customDefects = null;
//			try{
//			datamap = jxlsReader.DefectFileJxlsReader(file, template, path);
//			Set entries = datamap.entrySet();
//	        Iterator it = entries.iterator();
//	        ProjectTO projectTO = new ProjectTO();
//	       
//	        while (it.hasNext()) {
//	          Map.Entry entry = (Map.Entry) it.next();
//	         System.out.println(entry.getKey() + "-->" + entry.getValue());
//	          if(entry.getKey().equals("defects")){
//	        	  //System.out.println(entry.getValue());
//	        	  customDefects = (List<CustomDefect>) entry.getValue();
//	        	  		        	  
//	          }
//	          if(entry.getKey().equals("project")){
//	        	  //System.out.println(entry.getValue());
//	        	  projectTO =  (ProjectTO) entry.getValue();
//	        	  		        	  
//	          }
//	        }
//	        baseTO.setProjImportTO(projectTO);
//	        baseTO.setCustomDefects(customDefects);
//		}catch(Exception e)
//		{
//			baseTO.setErrorMessage("Im065");
//		}
//	        
//	 }
//		
//		
//		
//		
//		if(type.equals("Requirement")){
//			
//			List<CustomRequirement> customRequirements = null;
//			ProjectTO projectTO = new ProjectTO();
//	        ModuleTO moduleTO = new ModuleTO();
//			try{
//			datamap = jxlsReader.RequirementJxlsReader(file, template, path);
//			
//			
//			Set entries = datamap.entrySet();
//	        Iterator it = entries.iterator();
//	        
//	        while (it.hasNext()) {
//	          Map.Entry entry = (Map.Entry) it.next();
//	        //  System.out.println(entry.getKey() + "-->" + entry.getValue());
//	          if(entry.getKey().equals("requirement")){
//	        	  //System.out.println(entry.getValue());
//	        	  customRequirements = (List<CustomRequirement>) entry.getValue();
//	        	  		        	  
//	          }
//	          if(entry.getKey().equals("project")){
//	        	  //System.out.println(entry.getValue());
//	        	  projectTO =  (ProjectTO) entry.getValue();
//	        	  		        	  
//	          }
//	          if(entry.getKey().equals("module")){
//	        	  //System.out.println(entry.getValue());
//	        	  moduleTO =  (ModuleTO) entry.getValue();
//	        	  		        	  
//	          }
//	          
//	        }
//			}
//	        catch(Exception e){
//				baseTO.setErrorMessage("Invalid File");
//				
//			}
//	        baseTO.setProjImportTO(projectTO);
//	        baseTO.setModuleTO(moduleTO);
//	        baseTO.setCustomRequirements(customRequirements);
//		}
//
//		//added by sripad for ERP jukebox import
//		if(type.equals("ERP")){
//			List<CustomizedERPJukeBoxSuite> tempList = null;
//				try {
//					
//					datamap = jxlsReader.ERPJxlsReader(file, template, path);
//					 Set entries = datamap.entrySet();
//				     Iterator it = entries.iterator();
//				     while (it.hasNext()) {
//				          Map.Entry entry = (Map.Entry) it.next();
//				         System.out.println(entry.getKey() + "-->" + entry.getValue());
//				          if(entry.getKey().equals("CustomizedSuite")){
//				        	  System.out.println(entry.getValue());
//				        	  tempList = (List<CustomizedERPJukeBoxSuite>) entry.getValue();
//				        	 // System.out.println("-- "+tempList.isEmpty());		        	  
//				          }
//				        }
//				      
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SAXException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				baseTO.setCustomizedERPJukeBoxSuites(tempList);
//		}
//		if(type.equals("MTC_Excel")){
//			datamap = jxlsReader.excelJxlsReader(file, template ,path);
//			ModuleTO moduleTO = new ModuleTO();
//		    ProjectTO projectTO = new ProjectTO();
//		    List<ManualTestCaseImportTO> tempList = null;
//			Set entries = datamap.entrySet();
//	        Iterator it = entries.iterator();
//	        while (it.hasNext()) {
//	          Map.Entry entry = (Map.Entry) it.next();
//	        //  System.out.println(entry.getKey() + "-->" + entry.getValue());
//	          if(entry.getKey().equals("CustomizedSuite")){
//	        	  //System.out.println(entry.getValue());
//	        	  tempList = (List<ManualTestCaseImportTO>) entry.getValue();
//	        	  		        	  
//	          }
//	          if(entry.getKey().equals("project")){
//	        	  //System.out.println(entry.getValue());
//	        	  projectTO =  (ProjectTO) entry.getValue();
//	        	  		        	  
//	          }
//	          if(entry.getKey().equals("module")){
//	        	  //System.out.println(entry.getValue());
//	        	  moduleTO =  (ModuleTO) entry.getValue();
//	        	  		        	  
//	          }
//	        }
//	        baseTO.setProjImportTO(projectTO);
//	        baseTO.setModuleTO(moduleTO);
//	        baseTO.setManualTestCaseImportSuites(tempList);
//		}
//
//		
//			if(type.equals("AutoTestCase")){
//				try{
//				datamap = jxlsReader.excelSuiteKDTJxlsReader(file,template,path);
//				Set entries = datamap.entrySet();
//		        Iterator it = entries.iterator();
//		        List<KDTData> tempList = new ArrayList();
//		        while (it.hasNext()) {
//			          Map.Entry entry = (Map.Entry) it.next();
//			          //System.out.println(entry.getKey() + "-->" + entry.getValue());
//			          if(entry.getKey().equals("kdtdata")){
//			        		  tempList = (List<KDTData>) entry.getValue();}        
//		        }
//		        baseTO.setKdtDatas(tempList);		
//			}
//			 catch(Exception e){
//			    baseTO.setErrorMessage("Im065");
//			}
//		 }
//				
//			if(type.equals("ManualSuite")){
//				
//				List<CustomizedSuite> customSuite = null;
//				ProjectTO projectTO = new ProjectTO();
//		        ModuleTO moduleTO = new ModuleTO();
//				try{
//				datamap = jxlsReader.manualSuiteJxlsReader(file, template, path);
//				
//				
//				Set entries = datamap.entrySet();
//		        Iterator it = entries.iterator();
//		        
//		        while (it.hasNext()) {
//		          Map.Entry entry = (Map.Entry) it.next();
//		        //  System.out.println(entry.getKey() + "-->" + entry.getValue());
//		          if(entry.getKey().equals("mtsData")){
//		        	  //System.out.println(entry.getValue());
//		        	  customSuite = (List<CustomizedSuite>) entry.getValue();
//		        	  		        	  
//		          }
//		          if(entry.getKey().equals("project")){
//		        	  //System.out.println(entry.getValue());
//		        	  projectTO =  (ProjectTO) entry.getValue();
//		        	  		        	  
//		          }
//		          if(entry.getKey().equals("module")){
//		        	  //System.out.println(entry.getValue());
//		        	  moduleTO =  (ModuleTO) entry.getValue();
//		        	  		        	  
//		          }
//		          
//		        }
//				}
//		        catch(Exception e){
//					baseTO.setErrorMessage("Invalid File");
//					
//				}
//		        baseTO.setProjImportTO(projectTO);
//		        baseTO.setModuleTO(moduleTO);
//		        baseTO.setCustomizedSuites(customSuite);
//			}
//			
//			// ----------- following code is for QC Import reading file------------------	
//			if(type.equals("ManualTestCase")){
//				List<CustomizedSuite> customMTC= null;
//				try{
//				datamap = jxlsReader.QC_MTCJxlsReader(file, template, path);
//				Set entries = datamap.entrySet();
//		        Iterator it = entries.iterator();
//		        while (it.hasNext()) {
//		          Map.Entry entry = (Map.Entry) it.next();
//		         System.out.println(entry.getKey() + "-->" + entry.getValue());
//		          if(entry.getKey().equals("QCData")){
//		        	  //System.out.println(entry.getValue());
//		        	  customMTC = (List<CustomizedSuite>) entry.getValue();
//		        	  		        	  
//		          }
//		         
//		        }
//		       if(customMTC.size()==0)
//		    	   baseTO.setErrorMessage("Im065");
//		       else
//		        baseTO.setCustomizedSuites(customMTC);
//		        
//			}catch(Exception e)
//			{
//				baseTO.setErrorMessage("Im065");
//			}
//		        
//		 }
//		
//			if(type.equals("Mob")){
//				List<CustomizedERPJukeBoxSuite> tempList = null;
//					try {
//						
//						datamap = jxlsReader.ERPJxlsReader(file, template, path);
//						 Set entries = datamap.entrySet();
//					     Iterator it = entries.iterator();
//					     while (it.hasNext()) {
//					          Map.Entry entry = (Map.Entry) it.next();
//					         System.out.println(entry.getKey() + "-->" + entry.getValue());
//					          if(entry.getKey().equals("CustomizedSuite")){
//					        	  System.out.println(entry.getValue());
//					        	  tempList = (List<CustomizedERPJukeBoxSuite>) entry.getValue();
//					        	 // System.out.println("-- "+tempList.isEmpty());		        	  
//					          }
//					        }
//					      
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (SAXException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					baseTO.setCustomizedERPJukeBoxSuites(tempList);
//			}	
//		// ----------- code for QC Import reading file Ends------------------	
//			
//			return baseTO;
//		
//		
//
//	}
//	@Override
//	public String readConfigurationFile(String templateName,
//			String templateType, String path, String modulename,DAOFactory daoFactory) {
//		// TODO Auto-generated method stub
//		baseDAO = daoFactory.getBaseDAO();
//		String result = new String();
//		
//		 /**********read file from database*********/
//		 HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();	
//		 keyvalueMap.put("tempName", templateName);
//		 keyvalueMap.put("tempType", templateType);
//		 keyvalueMap.put("modName", modulename);
//		 
//		 List<?> fileList = baseDAO.findObjectByKeyValue(DBNamedQueryConstants.FETCH_TEMPLATE_FILE_QUERY_NAME, keyvalueMap);
//		 
//		  if(!fileList.isEmpty()){
//		   TemplateFormat templateFormat = new TemplateFormat();
//		   templateFormat = (TemplateFormat) fileList.get(0);
//
//		  // System.out.println(templateFormat.getTemplateName());
//
//		   ByteArrayInputStream bis = new ByteArrayInputStream(templateFormat.getTemplateFile());
//		   String filePath=path + "\\" + templateFormat.getTemplateName();
//		   File f=new File(filePath);
//		  
//		      FileOutputStream fop;
//			
//				try {
//					fop = new FileOutputStream(f);
//				
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = bis.read(buf)) > 0){
//					fop.write(buf, 0, len);
//				}
//				bis.close();
//				fop.flush();
//				
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				result = "Success";
//		  }
//		  else
//		  {
//			  // if template not in database
//			  result = "Template Not Found";
//			  logger.info(result);
//			  
//		  }
//		
//		return result;
//	}
//
//	//added by sripad for fetching jukebox template from DB
//	public String fetchConfigurationFile(String domain,String templateType,String path,String fileName,DAOFactory daoFactory){
//		baseDAO = daoFactory.getBaseDAO();
//		String result = new String();
//		HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();	
//		 keyvalueMap.put("tempType", templateType);
//		 keyvalueMap.put("Domain", domain);
//		 keyvalueMap.put("templateName", fileName);
//		 
//		 List<?> fileList = baseDAO.findObjectByKeyValue(DBNamedQueryConstants.FETCH_JUKEBOX_TEMPLATE_FILE_QUERY_NAME, keyvalueMap);
//		 
//
//		  if(!fileList.isEmpty()){
//		   TemplateFormat templateFormat = new TemplateFormat();
//		   if(templateType.equalsIgnoreCase("Import")){
//			   for(int i=0;i<fileList.size();i++){
//				   if(((TemplateFormat) fileList.get(i)).getTemplateName().equalsIgnoreCase(fileName))
//					   templateFormat = (TemplateFormat) fileList.get(i);   
//				   
//			   }
//			      
//		   }else{
//			   for(int i=0;i<fileList.size();i++){
//				   TemplateFormat tempTemplateFormat = (TemplateFormat) fileList.get(i);
//				   if(tempTemplateFormat.getTemplateName().equalsIgnoreCase(fileName)){
//					   templateFormat = (TemplateFormat) fileList.get(i);
//				   }
//			   }   
//		   }
//		   
//		   
//		   templateFormat = (TemplateFormat) fileList.get(0);
//		   //System.out.println(templateFormat.getTemplateName());
//		   ByteArrayInputStream bis = new ByteArrayInputStream(templateFormat.getTemplateFile());
//		   String filePath=path + "\\" + templateFormat.getTemplateName();
//		   File f=new File(filePath);
//		  
//		      FileOutputStream fop;
//			
//				try {
//					fop = new FileOutputStream(f);
//				
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = bis.read(buf)) > 0){
//					fop.write(buf, 0, len);
//				}
//				bis.close();
//				fop.flush();
//				
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				result = "success";
//		  }
//		  else
//		  {
//			// if template not in database
//			  result = "Template Not Found";
//			  logger.info(result);
//			  
//			  
//		  }
//		
//		return result;
//	}
//	
//	public void setBaseDAO(BaseDAO baseDAO) {
//		this.baseDAO = baseDAO;
//	}
//
//	public BaseDAO getBaseDAO() {
//		return baseDAO;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Workbook customexportData(String entity,List<?> list, String file,
//			 List<String> sheetname,HashMap projData) throws BaseException {
//		// TODO Auto-generated method stub
//		
//		
//		/*for(int iList=0; iList<list.size();iList++){
//			List<?> lst=(List<?>) list.get(iList);
//				for(int iLst=0; iLst<lst.size();iLst++){
//						RequirementTO reqTO=(RequirementTO) lst.get(0);
//						String strDt= reqTO.getReqCreatedDate().toString();
//						// dd/MM/yyyy format for example "14/09/2011", String to date format conversion
//						try {
//							
//							DateFormat  formatter = new SimpleDateFormat("dd/MM/yyyy");
//							Date convertedDate = (Date) formatter.parse(strDt);
//							reqTO.setReqCreatedDate(convertedDate);
//		
//						} catch (ParseException e) {
//							logger.info("Error in parsing date");
//						}					
//			
//				}
//		}*/
//		
//		 XLSTransformer transformer = new XLSTransformer();
//		 FileInputStream fileInputStream;
//		 Workbook workbook = null;
//	        try {
//	        	fileInputStream = new FileInputStream(file);
//	        	if(entity.equals("ManualTestCase")){
//	        	workbook = transformer.transformMultipleSheetsList(fileInputStream, list, sheetname, "mtcToList", projData, 0);
//	        	}
//	        	if(entity.equals("Requirement")){
//		        	workbook = transformer.transformMultipleSheetsList(fileInputStream, list, sheetname, "RequirementToList", projData, 0);
//		        	}
//	        	if(entity.equals("Defect")){
//		        	workbook = transformer.transformMultipleSheetsList(fileInputStream, list, sheetname, "defectToList", projData, 0);
//		        	}
//	        	if(entity.equals("AutoTestCase")){
//		        	workbook = transformer.transformMultipleSheetsList(fileInputStream, list, sheetname, "atcToList", projData, 0);
//		        	}
//	        	
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				throw new BaseException("Error");
//			}
//		
//		return workbook;
//	}
//
//
//}
