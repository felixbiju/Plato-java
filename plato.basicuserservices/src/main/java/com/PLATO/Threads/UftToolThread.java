package com.PLATO.Threads;

import java.util.concurrent.Callable;

public class UftToolThread implements Callable<String>
{
	private String threadName;
	private String toolName;
	private int nextBuildNumber;
	public UftToolThread(String toolName,String jobName,int nextBuildNumber)
	{
		System.out.println("Inside HP QTP thread constructor");
		this.threadName=jobName;
		this.toolName=toolName;
		this.nextBuildNumber=nextBuildNumber;
	}

	@Override
	public String call()
	{System.out.println("Inside HP QTP thread");
		try
		{
			System.out.println("HP QTP ToolThread :"+threadName);
			for(int i=0;i<500;i++)
			{
				System.out.println("HP QTP Tool Thread :"+i+" : "+threadName);
			}
			return "Success :"+threadName;
		}
		catch(Exception e)
		{
			return "Failure :"+threadName;
		}
	}

}
