package com.PLATO.Threads;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.PLATO.constants.GlobalConstants;
import com.PLATO.userTO.TEMApplicationTO;


public class TEMApplicationThread// extends Thread{
{
	public static List<TEMApplicationTO>temAppToList=new ArrayList<TEMApplicationTO>();

	private String applicationUrl;
	private String applicationName;
	private String monitoringStatus;
	public TEMApplicationThread(String applicationUrl,String applicationName,String monitoringStatus){
		this.applicationUrl=applicationUrl;
		this.applicationName=applicationName;
		this.monitoringStatus=monitoringStatus;
	}
	
	public TEMApplicationThread(){
		
	}
	
	//@Override
	public void run()
	{
		try
		{
			// Displaying the thread that is running
			System.out.println ("Thread " +
					Thread.currentThread().getId() +
					" is running "+" "+System.currentTimeMillis());
			int status = 0;
			try{
				
				ClientConfig config = new ClientConfig();

		        Client client = ClientBuilder.newClient(config);

		       // WebTarget target = client.target(this.applicationUrl);
				
				status=client.target(this.applicationUrl).request().get().getStatus();
				
		/*		Client client = Client.create();
				WebResource webResource = client.resource(this.applicationUrl);

				ClientResponse response = webResource.get(ClientResponse.class);
				status=response.getStatus();
		 */
				System.out.println("status of application "+this.applicationUrl+"is......"+status);


			}catch(Exception e)
			{
				System.out.println("application Down");
			//	applicationUp=0;

			}
			//	}
			if(status==200){
				this.monitoringStatus="Running";
			}else{
				this.monitoringStatus="Stopped";
			}

			
			TEMApplicationTO temAppTo=new TEMApplicationTO();
			temAppTo.setApplicationName(this.applicationName);
			temAppTo.setApplicationStatus(this.monitoringStatus);
			temAppTo.setApplicationUrl(this.applicationUrl);
			temAppToList.add(temAppTo);
			
		}
		catch (Exception e)
		{
			// Throwing an exception
			System.out.println ("Exception is caught");
		}
	}
	
	
	public static String getApplicationStatus(String appUrl){

	
			// Displaying the thread that is running
			/*System.out.println ("Thread " +
					Thread.currentThread().getId() +
					" is running "+" "+System.currentTimeMillis());*/
			int status = 0;
			String appStatus;
			//proxy settings
    		//System.setProperty("https.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
			System.setProperty("https.proxyHost", GlobalConstants.PROXY_URL_TEM_APP);
    		System.setProperty("https.proxyPort", GlobalConstants.PROXY_PORT_TEM_APP);
			try{
				
				/*System.getProperties().put("https.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
				  System.getProperties().put("https.proxyPort", "8080");
				  System.getProperties().put("https.proxyUser", "pwiodc\10621560");
				  System.getProperties().put("https.proxyPassword", "Sanu@2017");
				  
				  System.getProperties().put("http.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
				  System.getProperties().put("http.proxyPort", "8080");
				  System.getProperties().put("http.proxyUser", "pwiodc\10621560");
				  System.getProperties().put("http.proxyPassword", "Sanu@2017");*/
				  
				  /*System.setProperty("http.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
				  System.setProperty("http.proxyPort", "8080");
				  System.setProperty("http.proxyUser", "pwiodc\10621560");
				  System.setProperty("http.proxyPassword", "Sanu@2017");
				  
				  System.setProperty("https.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
				  System.setProperty("https.proxyPort", "8080");
				  System.setProperty("https.proxyUser", "pwiodc\10621560");
				  System.setProperty("https.proxyPassword", "Sanu@2017");*/
				  
				
								
				
				/*ClientConfig config = new ClientConfig();

		        Client client = ClientBuilder.newClient(config);

		        WebTarget target = client.target(appUrl);
				
		        Builder request = target.request();
		        
		        Response response = request.get();
		        status=response.getStatus();*/
				
				/*HttpClient httpclient = new DefaultHttpClient() ;
				HttpHost proxy = new HttpHost( "172.25.32.148", 8080 ) ;
				httpclient.getParams().setParameter( ConnRoutePNames.LOCAL_ADDRESS, proxy ) ;
				String localhost = InetAddress.getLocalHost().getHostName() ;
				NTCredentials credentials = new NTCredentials( "10621560", "Sanu@2017", localhost, "pwiodc" )  ;
				((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials( AuthScope.ANY, credentials) ;*/
				
				
				CookieHandler.setDefault(new CookieManager(null,CookiePolicy.ACCEPT_ALL));				
				URL siteURL = new URL(appUrl);
	            HttpURLConnection connection = (HttpURLConnection) siteURL
	                    .openConnection();
	          //  connection.setRequestMethod("GET");
	            connection.connect();
	            status = connection.getResponseCode();
	            if(status!=200) {
	            	HttpsURLConnection connectionHttps=(HttpsURLConnection) siteURL.openConnection();
	            	connectionHttps.connect();
	            	status=connection.getResponseCode();
	            }
				
		/*		Client client = Client.create();
				WebResource webResource = client.resource(this.applicationUrl);

				ClientResponse response = webResource.get(ClientResponse.class);
				status=response.getStatus();
		 */
				System.out.println("status of application "+appUrl+"is......"+status);


			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("application Down");
			//	applicationUp=0;

			}
			//	}
			if(status==200){
				appStatus="Running";
			}else{
				appStatus="Stopped";
			}

	return appStatus;
	}

}
