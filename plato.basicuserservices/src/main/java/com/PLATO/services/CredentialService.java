package com.PLATO.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import com.PLATO.constants.GlobalConstants;
import com.PLATO.userTO.CredentialsTO;
import com.PLATO.utilities.XMLUtilities;

@Path("CredentialService")
public class CredentialService {
	private static final Logger logger=Logger.getLogger(CredentialService.class);
	@GET
	@Path("getCredentials")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response getCredentials() throws IOException{
		try {
			String inputLine;
			URL url=new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/credentials/store/system/domain/_/api/xml?depth=1");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/json");
			con.setRequestProperty("Accept", "text/json");
			con.setRequestMethod("GET");
			System.out.println("Hello is anybody out there");
			System.out.println("Response Code : " + con.getResponseCode());
			if(con.getResponseCode()!=200)
			{
				return Response.status(Response.Status.OK).entity(" ").build();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println(response.toString());
			String output=response.toString();
			
			DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
			Document doc=XMLUtilities.convertStringToDocument(output);
			int n =doc.getElementsByTagName("credential").getLength()+1;
			List<CredentialsTO> credentialsTOList=new ArrayList<CredentialsTO>();
			for(int i=0;i<n-1;i++) {
				Element credential=(Element)doc.getElementsByTagName("credential").item(i);
				CredentialsTO credentialsTO=new CredentialsTO();
				credentialsTO.setDescription(credential.getElementsByTagName("description").item(0).getTextContent());
				credentialsTO.setDisplayName(credential.getElementsByTagName("displayName").item(0).getTextContent());
				credentialsTO.setId(credential.getElementsByTagName("id").item(0).getTextContent());
				credentialsTO.setTypeName(credential.getElementsByTagName("typeName").item(0).getTextContent());
				credentialsTOList.add(credentialsTO);
				System.out.println("descritption "+credentialsTO.getDescription()+" displayName "+credentialsTO.getDisplayName()+" id "+credentialsTO.getId());
			}
			
			return Response.status(Response.Status.OK).entity(credentialsTOList).build();
			
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(" ").build();
		}
	}
	
	@POST
	@Path(value="createCredentials/{description}/{userName}/{password}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static void createCredentials(@PathParam("description") String description,@PathParam("userName") String userName, @PathParam("password") String password) {
		try {
			System.out.println("welcome to the machine");
			Runtime rt=Runtime.getRuntime();
			Process p=rt.exec("cmd /c start  C:\\\\Users\\\\10643380\\\\jenkinsCredential.bat "+description+" "+userName+" "+password);
			//p.waitFor(20000, TimeUnit.MILLISECONDS);
			//rt.exec("cmd /c start cmd.exe /K java -jar jenkins-cli.jar -s http://localhost:8080/jenkins/ -remoting groovy C:\\Users\\10643380\\createCredentials.GROOVY "+description+" "+userName+" "+password);
			//ProcessBuilder builder=new ProcessBuilder("cmd.exe /C start","k/ jenkinsCredential.bat "+description+" "+userName+" "+password);
			//ProcessBuilder builder=new ProcessBuilder("start C:\\Users\\10643380\\jenkinsCredential.bat",description,userName,password);
			//builder.directory(new File("C:\\Users\\10643380"));
			//Process p=builder.start();
			//p.destroy();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*try {
			System.out.println("hello");
			String st="import com.cloudbees.plugins.credentials.impl.*;\r\n" + 
					"import com.cloudbees.plugins.credentials.*;\r\n" + 
					"import com.cloudbees.plugins.credentials.domains.*;\r\n" + 
					"\r\n" + 
					"String keyfile = \"/tmp/key\"\r\n" + 
					"\r\n" + 
					"Credentials c = (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,java.util.UUID.randomUUID().toString(), \"description\", \"user123\", \"password234\")\r\n" + 
					"SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), c)";
			URL url=new URL(GlobalConstants.JENKINS_URL+":"+GlobalConstants.JENKINS_PORT+"/jenkins/scriptText?"+st);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(st);
			wr.flush();
			wr.close();
			// reading the response
			InputStreamReader reader = new InputStreamReader( con.getInputStream() );
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[ 2048 ];
			int num;
			while ( -1 != (num=reader.read( cbuf )))
			{
				buf.append( cbuf, 0, num );
			}
			String result = buf.toString();
			System.err.println( "\nResponse from server after POST:\n" + result );
			//reloadJenkins();
		}catch(Exception e) {
			e.printStackTrace();
		}*/
	}
}
