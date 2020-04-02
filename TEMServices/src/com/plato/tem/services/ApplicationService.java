package com.plato.tem.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 *
 * @author 10643331 Sueanne
 * @author 10643380 (Rahul Bhardwaj)
 */
@Path("ApplicationService")
public class ApplicationService {
	
	//private static final Logger logger=Logger.getLogger(ApplicationService.class);
	final Sigar sigar = new Sigar();
	final long myPid = sigar.getPid();
	/**
	 * This service is used to check if a server is running or stopped
	 * @return String 
	 */
	@Path("checkUp/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@QueryParam("ip") String ip) {
		System.out.println("in getALL");
		int x=0;
		
		try {
			System.out.println("checking server up");
			x=checkServerForUp(1,ip);
			
			System.out.println("server is up  "+x);
			if(x==0) {
				return Response.status(Response.Status.OK).entity("Server down").build();
			}
			return Response.status(Response.Status.OK).entity("Server up").build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Not working").build();
		}
	}

	
	
	@Path("checkCpuUsage")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCpuUsage() {
		
		double x=0;
		
		try {
			//x=calcCpuUsage();
			x=calcCpuUsage();
			
			
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Response.Status.OK).entity("CPU USAGE IS: "+x).build();

		
		
		
		
	}
	
	@Path("checkProcessCpuAndMemoryUsage/{pId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkProcessCpuAndMemoryUsage(@PathParam("pId") String pId) {
		JSONArray arr=null;
		try {
			arr=calcCpuAndMemory(pId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.OK).entity(arr).build();
	}
	
	JSONArray calcCpuAndMemory(String pId) throws SigarException,InterruptedException {
		JSONArray cpuMemUsageArr=new JSONArray();
		ProcCpu cpu=sigar.getProcCpu("Exe.Name.ct="+pId);
		ProcMem memory=sigar.getProcMem("Exe.Name.ct="+pId);	
		for(int i=0;i<20;i++) {
			TimeUnit.SECONDS.sleep(1);
			JSONObject cpuMemUsage=new JSONObject();
//			double cpuUsage=sigar.getCpuPerc().getCombined()*100;
//			double memoryUsage=sigar.getMem().getUsedPercent();
			Date date= new Date();
			Timestamp ts = new Timestamp(date.getTime());
			System.out.println("cpu per : "+cpu.getPercent()*100);
			System.out.println("mem usage : "+Sigar.formatSize(memory.getRss()));
			cpuMemUsage.put("cpu_usage", cpu.getPercent()*100);
			cpuMemUsage.put("memory_usage", Sigar.formatSize(memory.getRss()));
			cpuMemUsage.put("timeStamp",ts);
			cpuMemUsage.put("process_name", pId);
			cpuMemUsageArr.add(cpuMemUsage);
		}
		return cpuMemUsageArr;
	}
	@Path("checkCpuAndMemoryUsage")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkCpuAndMemoryUsage() {
		JSONArray arr=null;
		try {
			arr=calcCpuAndMemory();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.OK).entity(arr).build();
	}

	JSONArray calcCpuAndMemory() throws SigarException,InterruptedException{
		JSONArray cpuMemUsageArr=new JSONArray();
//		ProcCpu cp=sigar.getProcCpu("Exe.Name.ct=eclipse.exe");
//		ProcMem me=sigar.getProcMem("Exe.Name.ct=java.exe");
		//System.out.println("eclipse mem "+sigar.formatSize(me.getSize())+" Rss "+sigar.formatSize(me.getRss())+" share "+sigar.formatSize(me.getShare())+" vsize "+sigar.formatSize(me.getVsize()));
		for(int i=0;i<20;i++) {
			TimeUnit.SECONDS.sleep(1);
			JSONObject cpuMemUsage=new JSONObject();
			double cpuUsage=sigar.getCpuPerc().getCombined()*100;
			String memoryUsage=sigar.formatSize(sigar.getMem().getActualUsed());
			String freeMemory=sigar.formatSize(sigar.getMem().getActualFree());
			Date date= new Date();
			Timestamp ts = new Timestamp(date.getTime());
			System.out.println("cpu per : "+cpuUsage);
			System.out.println("mem per : "+memoryUsage);
			cpuMemUsage.put("cpu_usage", cpuUsage);
			cpuMemUsage.put("memory_usage", memoryUsage);
			cpuMemUsage.put("free_memory", freeMemory);
			cpuMemUsage.put("timeStamp",ts);
			cpuMemUsageArr.add(cpuMemUsage);
		}
		return cpuMemUsageArr;
	}
	
	
	int checkServerForUp(int serverId,String url) throws UnknownHostException, IOException{

		
		int serverUp = 0;
		Client client;
		
		String ur[]=url.split("\\//");
		
		//if(url.contains("https")){
			/*ClientConfig config = new DefaultClientConfig();
			 client = new Client(new URLConnectionClientHandler(
			        new HttpURLConnectionFactory() {
			    Proxy p = null;
			    @Override
			    public HttpURLConnection getHttpURLConnection(URL url)
			            throws IOException {
			        if (p == null) {
			            if (System.getProperties().containsKey("http.proxyHost")) {
			                p = new Proxy(Proxy.Type.HTTP,
			                        new InetSocketAddress(
			                        System.getProperty("http.proxyHost"),
			                        Integer.getInteger("http.proxyPort", 80)));
			            } else {
			                p = Proxy.NO_PROXY;
			            }
			        }
			        return (HttpURLConnection) url.openConnection(p);
			    }
			}), config);*/
			
			/*TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
			    public X509Certificate[] getAcceptedIssuers(){return null;}
			    public void checkClientTrusted(X509Certificate[] certs, String authType){}
			    public void checkServerTrusted(X509Certificate[] certs, String authType){}
			}};
			
			
			
			try {
			    SSLContext sc = SSLContext.getInstance("TLS");
			    sc.init(null, trustAllCerts, new SecureRandom());
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
			    ;
			}*/
			
			
			 
	/*		
			DefaultHttpClient httpclient = new DefaultHttpClient();

		    SSLContext sslContext;
		    try {
		        sslContext = SSLContext.getInstance("SSL");

		        // set up a TrustManager that trusts everything
		        try {
		            sslContext.init(null,
		                    new TrustManager[] { new X509TrustManager() {
		                        public X509Certificate[] getAcceptedIssuers() {
		                          //  log.debug("getAcceptedIssuers =============");
		                            return null;
		                        }

		                        public void checkClientTrusted(
		                                X509Certificate[] certs, String authType) {
		                         //   log.debug("checkClientTrusted =============");
		                        }

		                        public void checkServerTrusted(
		                                X509Certificate[] certs, String authType) {
		                           // log.debug("checkServerTrusted =============");
		                        }
		                    } }, new SecureRandom());
		        } catch (KeyManagementException e) {
		        }
		         SSLSocketFactory ssf = new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		         ClientConnectionManager ccm = this.httpclient.getConnectionManager();
		         SchemeRegistry sr = ccm.getSchemeRegistry();
		         sr.register(new Scheme("https", 443, ssf));            
		    } catch (Exception e) {
		      //  log.error(e.getMessage(),e);
		    }*/
			
			
			
			
			
			
			
			
			
			
			
			/*System.setProperty("http.proxyHost", "poincoproxy01.pwiodc.lntinfotech.com");
			System.setProperty("http.proxyPort", "8080");
			System.setProperty("http.proxyUser", "10621560");
			System.setProperty("http.proxyPassword", "Welcome@11111");*/

		//}
		
		//client = Client.create();
	
		//WebResource webResource = client.resource(url);
		
		//ClientResponse response = webResource.get(ClientResponse.class);
		
		
		/*System.setProperty("http.proxyHost", GlobalClass.proxyurl);
		System.setProperty("http.proxyPort", GlobalClass.port);
		System.setProperty("http.proxyUser", GlobalClass.username);
		System.setProperty("http.proxyPassword", GlobalClass.password);*/
		
		boolean a=InetAddress.getByName(ur[1]).isReachable(2000);
		
		int status = 0;
		if(a==true){
			status=200;
		}else{

			
			
			/*System.setProperty("http.proxyHost", GlobalClass.proxyurl);
			System.setProperty("http.proxyPort", GlobalClass.port);
			System.setProperty("http.proxyUser", GlobalClass.username);
			System.setProperty("http.proxyPassword", GlobalClass.password);	*/
		try{
		client = Client.create();
		WebResource webResource = client.resource(url);
		
		ClientResponse response = webResource.get(ClientResponse.class);
		
	
		 status=response.getStatus();
		
		System.out.println("status of application is......"+status);
		
		
		}catch(ClientHandlerException e)
		{
			System.out.println("Server Down");
			serverUp=0;
		
		}
		
		}
		try{
		//int status=response.getStatus();
		
		System.out.println("status of server is......"+status);
		
		if(status==200){
			serverUp=1;
		}
		}catch(ClientHandlerException e)
		{
			System.out.println("Server Down");
			serverUp=0;
		
		}
		
		return serverUp;
		
	
		
		
		
	}

	double calcCpuUsage() throws SigarException,InterruptedException{
		
		

//		logger.fine("PID of current process is: " + myPid);
		ProcCpu cpu = sigar.getProcCpu(myPid);
		System.out.println("PID of current process is: " + myPid);
		//TimeUnit.SECONDS.sleep(5);
		for(int i=0;i<20;i++) {
			TimeUnit.SECONDS.sleep(1);
			System.out.println("cpu per : "+sigar.getCpuPerc().getCombined()*100);
		}
			
		
		
		
		/*com.sun.management.OperatingSystemMXBean operatingSystemMXBean = 
		         (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		    int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
		    long prevUpTime = runtimeMXBean.getUptime();
		    long prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
		    double cpuUsage;
		    try 
		    {
		        Thread.sleep(500);
		    } 
		    catch (Exception ignored) { }

		    operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		    long upTime = runtimeMXBean.getUptime();
		    long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
		    long elapsedCpu = processCpuTime - prevProcessCpuTime;
		    long elapsedTime = upTime - prevUpTime;

		    cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
		    System.out.println("Java CPU: " + cpuUsage);*/
		
		/*String name = ManagementFactory.getRuntimeMXBean().getName();

		StringBuffer pid = new StringBuffer();
		    for (int i = 0, l = name.length(); i < l; i++) {
		    if (Character.isDigit(name.charAt(i))) {
		        pid.append(name.charAt(i));
		    } else if (pid.length() > 0) {
		        break;
		    }
		    }
		    try {
		    return Integer.parseInt(pid.toString());
		    } catch (NumberFormatException e) {
		    return 0;
		    }*/

		return (int) (cpu.getPercent()*100);
}

}
