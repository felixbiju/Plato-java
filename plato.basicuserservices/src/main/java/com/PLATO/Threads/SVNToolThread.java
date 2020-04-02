package com.PLATO.Threads;

import java.util.concurrent.Callable;

public class SVNToolThread implements Callable<String>
{
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	public SVNToolThread(String toolName,String jobName,int nextBuildNumber)
	{
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
	}
	
	@Override
	public String call()
	{
		try
		{
			System.out.println("SVNToolThread :"+threadName);
			for(int i=0;i<1000;i++)
			{
				System.out.println("SVN Tool Thread :"+i+" : "+threadName);
			}
		return "Success :"+threadName;
		}
		catch(Exception e)
		{
			return "Failure :"+threadName;
		}
	}

}
