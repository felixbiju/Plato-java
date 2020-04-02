package com.PLATO.Threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.constants.GlobalConstants;
import com.PLATO.dao.GenericDao;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.ModuleSubJobsJenkinsParameters;
import com.PLATO.enums.ToolNameEnum;
import com.PLATO.services.JenkinsServices;

/* Class Description : This thread is for main module. This thread created threads for each tool/subjob and invokes
 * all the threads. When all the threads finish execution this class retrieves their response and returns appropriate
 * result
 */

public class ModuleThread implements Callable<String>
{
	private GenericDao genericDao=GenericDaoSingleton.getGenericDao();
	private static final Logger logger=Logger.getLogger(ModuleThread.class);
	private String threadName;
	private ArrayList<String> subJobOrderList;
	private int buildHistoryId;
	private HashMap<String,Integer> subJobBuildNumberList;

	//constructor of modulethread
	public ModuleThread(String threadName,ArrayList<String> subJobOrderList, int buildHistoryId,HashMap<String,Integer> subJobBuildNumberList)
	{
		logger.info("Inside Module thread constructor");
		logger.debug("Received parameters threadName :"+threadName+" subjobOrderList :"+subJobOrderList+" buildHistoryId :"+buildHistoryId+"  subJobBuildNumberList  "+subJobBuildNumberList);
		this.subJobOrderList=subJobOrderList;
		this.threadName=threadName;
		this.buildHistoryId=buildHistoryId;
		this.subJobBuildNumberList=subJobBuildNumberList;
	}

	@Override
	public String call()
	{
		logger.info("Inside moduleThread call");
		logger.info("ModuleThread :"+threadName);
		JenkinsServices jenkinsService=new JenkinsServices();
		Set<Callable<String>> callableSet=new HashSet<Callable<String>>(); 
		int nextBuildNumber=-1;
		ExecutorService execService=null;
		try
		{
			//loop through list of subjobs to be executed
			for(int i=0;i<subJobOrderList.size();i++)
			{
				HashMap<String, Object> keyvalueMap=new HashMap<String,Object>();
				logger.debug("subjobName is :"+subJobOrderList.get(i));
				keyvalueMap.put("subJobName", subJobOrderList.get(i));
				ModuleSubJobsJenkins subJobs= (ModuleSubJobsJenkins) genericDao.findUniqueByQuery(ConstantQueries.GETSUBMODULEBYNAME, keyvalueMap);
				//Important: replacing whitepace with _ because Enum cannot have spaces,to handle HP_QTP
				//We are keeping tool names in Uppercase. So convert subjob toolname to uppercase
				ToolNameEnum toolName=ToolNameEnum.valueOf(subJobs.getTool_name().replaceAll(" ", "_").toUpperCase());
				String jobName=subJobs.getSubjob_name();
				logger.debug("Current subjob :"+jobName+" Tool Name :"+toolName);
				nextBuildNumber=subJobBuildNumberList.get(jobName);
				//check the tool of subjob, create instance of thread for that tool and add it to set of threads
				switch(toolName)
				{
				case FAST:
				case BROWSERSTACK:
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					logger.debug(jobName +" Creating FAStToolThread for build number :"+nextBuildNumber);
					//FASTToolThread fastThread=new FASTToolThread(toolName.FAST.toString(),jobName,nextBuildNumber,buildHistoryId);
					FASTToolThread fastThread=new FASTToolThread(subJobs.getTool_name(),jobName,nextBuildNumber,buildHistoryId);

					callableSet.add(fastThread);
					break;

				case SVN:
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					logger.debug(jobName +" Creating SVNToolThread for build number :"+nextBuildNumber);
					SVNToolThread svnThread=new SVNToolThread(toolName.SVN.toString(),jobName,nextBuildNumber);
					callableSet.add(svnThread);
					break;

				case MAVEN:
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					logger.debug(jobName +" Creating MavenToolThread for build number :"+nextBuildNumber);
					MavenToolThread mavenThread=new MavenToolThread(toolName.MAVEN.toString(),jobName,nextBuildNumber,buildHistoryId);
					callableSet.add(mavenThread);
					break;
										
				case HP_QTP:
				case UFT:
					/*nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					int hello=0;
					logger.debug(jobName +" Creating HP_QTPToolThread for build number :"+nextBuildNumber);
					UftToolThread uftToolThread=new UftToolThread(toolName.HP_QTP.toString(),jobName,nextBuildNumber);
					callableSet.add(uftToolThread);*/
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					//String reportPath=subJobs.getReport_path();
					//here the reportPath of jenkins is taken,because all the reports are copied over there
					String reportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+subJobs.getSubjob_name()+"/";
					logger.debug(jobName+" Creating generic tool Thread for build number:"+nextBuildNumber);
					GenericCommandToolThread uftToolThread=new GenericCommandToolThread(subJobs.getTool_name(),jobName,nextBuildNumber,buildHistoryId,reportPath);
					callableSet.add(uftToolThread);
					break;
					
				case SONAR:
					logger.debug("commandToExecute "+subJobs.getCommand_to_execute());
					String commandToExecute=subJobs.getCommand_to_execute();
					commandToExecute=commandToExecute.replace("\n\r", "\n");
					commandToExecute=commandToExecute.replace("\r", "\n");
					String[] commandToExecutePair=commandToExecute.split("\n");
					String sonarKey="";
					logger.debug("commandToExecutePair length"+commandToExecutePair.length);
					for(int i1=0;i1<commandToExecutePair.length;i1++) {
						System.out.println("commandToExecutePair["+i1+"]="+commandToExecutePair[i1]);
						if(commandToExecutePair[i1].toLowerCase().contains("sonar.projectkey")) {
							String[] sonarKeyPair=commandToExecutePair[i1].split("=");
							logger.debug("sonarKetPair[0] "+sonarKeyPair[0]);
							logger.debug("sonarKetPair[1] "+sonarKeyPair[1]);
							logger.debug("sonarKeyPair length"+sonarKeyPair.length);
							sonarKey=sonarKeyPair[1].trim();
						}
					}
					reportPath=subJobs.getReport_path();
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					logger.debug(jobName+" Creating Sonar tool Thread for build number:"+nextBuildNumber);
					SonarToolThread sonarToolThread=new SonarToolThread(toolName.SONAR.toString(),jobName,nextBuildNumber,buildHistoryId,sonarKey,reportPath);
					callableSet.add(sonarToolThread);
					break;
					
				case DIAT:
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					//String reportPath=subJobs.getReport_path();
					//here the reportPath of jenkins is taken,because all the reports are copied over there
					reportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+subJobs.getSubjob_name();
					logger.debug(jobName+" Creating Diat tool Thread for build number:"+nextBuildNumber);
					DiatToolThread diatToolThread2=new DiatToolThread(toolName.DIAT.toString(),jobName,nextBuildNumber,buildHistoryId,reportPath);
					callableSet.add(diatToolThread2);
					break;
					
				/*case BROWSERSTACK:
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					//String reportPath=subJobs.getReport_path();
					reportPath=subJobs.getReport_path();
					logger.debug(jobName+" Creating BrowserStack tool Thread for build number:"+nextBuildNumber);
					MultiplePlatformToolThread multiplePlatformToolThread=new MultiplePlatformToolThread(toolName.BROWSERSTACK.toString(),jobName,nextBuildNumber,buildHistoryId);
					callableSet.add(multiplePlatformToolThread);
					break;*/
				case BUILD:
					//this is because the build gets executed first and then the reports are read.
					//Therefore lastbuildNumber will be the current build number 
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName)-1;
					logger.debug("nextBuildNumber is "+nextBuildNumber);
					logger.debug(jobName+" Creating Build tool Thread for build number:"+nextBuildNumber);
					reportPath=subJobs.getReport_path();
					BuildToolThread buildToolThread=new BuildToolThread(toolName.BUILD.toString(),jobName,nextBuildNumber,buildHistoryId,reportPath,subJobs.getCommand_to_execute());
					callableSet.add(buildToolThread);
					break;
				case IDISCOVER:
					reportPath=subJobs.getReport_path();
					System.out.println(reportPath);
					logger.debug(jobName+" Creating generic tool Thread for build number:"+nextBuildNumber);
					GenericCommandToolThread genericIdiscoverCommandToolThread=new GenericCommandToolThread(subJobs.getTool_name(),jobName,nextBuildNumber,buildHistoryId,reportPath);
					callableSet.add(genericIdiscoverCommandToolThread);
					break;
				case JIRA:
					ArrayList<ModuleSubJobsJenkinsParameters> parameterList=(ArrayList<ModuleSubJobsJenkinsParameters>) subJobs.getModuleSubJobsJenkinsParametersList();
					HashMap<String,String> parameterMap=new HashMap<String,String>();
					for(ModuleSubJobsJenkinsParameters parameter:parameterList) {
						parameterMap.put(parameter.getParameter_key(),parameter.getValue());
					}
					String typeOfDataToFetch=parameterMap.get("typeOfDataToFetch");
					String username=parameterMap.get("username");
					String password=parameterMap.get("password");
					String projectName=parameterMap.get("projectName");
					String jiraUrl=parameterMap.get("jiraUrl");
					String issueType=parameterMap.get("issueType");
					reportPath=jiraUrl+","+issueType+","+typeOfDataToFetch+","+projectName+","+username+","+password;
					GenericCommandToolThread jiraToolThread=new GenericCommandToolThread(subJobs.getTool_name(),jobName,nextBuildNumber,buildHistoryId,reportPath);
					callableSet.add(jiraToolThread);
					break;
				default:
					//nextBuildNumber=jenkinsService.fetchNextBuildNumber(jobName);
					//String reportPath=subJobs.getReport_path();
					//reportPath=subJobs.getReport_path();
					//here the reportPath of jenkins is taken,because all the reports are copied over there
					reportPath=GlobalConstants.JENKINS_HOME+"/workspace/"+subJobs.getSubjob_name()+"/";
					logger.debug(jobName+" Creating generic tool Thread for build number:"+nextBuildNumber);
					GenericCommandToolThread genericCommandToolThread=new GenericCommandToolThread(subJobs.getTool_name(),jobName,nextBuildNumber,buildHistoryId,reportPath);
					callableSet.add(genericCommandToolThread);
					break;
						
				}
			}

			//invoke all threads added to the set
		    execService=Executors.newCachedThreadPool();
			List<Future<String>> futureList=execService.invokeAll(callableSet);
			
			//result of all threads is returned in future list. Note: After invokeAll() the execution of current thread/function is blocked till all invoked threads finish execution.
			Iterator<Future<String>> itr=futureList.iterator();
			logger.info("Getting results returned by all tool threads in Module Thread");
			//if each thread returns success then return success else return failure. This response will be returned to jobExecution service 
			while(itr.hasNext())
			{
				Future<String> future=itr.next();
				String result=future.get();
				logger.debug("Result is: "+result);
				if(result.contains("Failed"))
				{
					logger.info("Returning failure");
					return result;
				}else if(result.contains("Aborted")){					
					logger.info("Returning aborted");
					return result;
				}
			}
			logger.info("Returning success");
			return "Success";

		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Exception in run of module thread :"+e);
			logger.error("Returning failure");
			return "Failure";
		}
		
		finally
		{
			logger.info("Inside finally closing executor service");
			execService.shutdown();
		}

	}

}
