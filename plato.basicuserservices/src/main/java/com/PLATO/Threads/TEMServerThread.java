package com.PLATO.Threads;

import java.net.InetAddress;
import com.PLATO.constants.GlobalConstants;

public class TEMServerThread {

public TEMServerThread(){
		
	}
	
	public static String getServerStatus(String serverUrl){

		
		// Displaying the thread that is running
		/*System.out.println ("Thread " +
				Thread.currentThread().getId() +
				" is running "+" "+System.currentTimeMillis());*/
		int status = 0;
		String serverStatus;
		//proxy settings
		//System.setProperty("https.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
		System.setProperty("https.proxyHost", GlobalConstants.PROXY_URL_TEM_SERVER);
		System.setProperty("https.proxyPort", GlobalConstants.PROXY_PORT_TEM_SERVER);
		try{
			
			/*ClientConfig config = new ClientConfig();

	        Client client = ClientBuilder.newClient(config);

	       // WebTarget target = client.target(this.applicationUrl);
			
			status=client.target(serverUrl).request().get().getStatus();*/
			serverUrl=serverUrl.trim();
			serverUrl=serverUrl.toLowerCase();
			serverUrl=serverUrl.replace("http", "");
			serverUrl=serverUrl.replace("s", "");
			serverUrl=serverUrl.replace("/","");
			serverUrl=serverUrl.replace(":", "");
			System.out.println("server url is "+serverUrl);
			InetAddress pinging = InetAddress.getByName(serverUrl);
			if (pinging.isReachable(5000)) {
				status=200;
			}else {
				status=404;
			}
			//working code
			/*URL siteURL = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) siteURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            status = connection.getResponseCode();
            if(status!=200) {
            	HttpsURLConnection connectionHttps=(HttpsURLConnection) siteURL.openConnection();
            	connectionHttps.connect();
            	status=connection.getResponseCode();
            }/*
            //working code
			
		/*	Client client = Client.create();
			WebResource webResource = client.resource(this.applicationUrl);

			ClientResponse response = webResource.get(ClientResponse.class);
			status=response.getStatus();
	 */
			System.out.println("status of Server "+serverUrl+"is......"+status);


		}catch(Exception e)
		{	e.printStackTrace();
			System.out.println("Server Down");
		//	applicationUp=0;

		}
		//	}
		if(status==200){
			serverStatus="Running";
		}else{
			serverStatus="Stopped";
		}

return serverStatus;
}
}
