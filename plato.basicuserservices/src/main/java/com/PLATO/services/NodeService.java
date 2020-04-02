package com.PLATO.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.GlobalConstants;
import com.PLATO.daoimpl.GenericDaoImpl;
import com.PLATO.entities.ModuleSubJobsJenkins;
import com.PLATO.entities.NodeMaster;
import com.PLATO.entities.ProjectToolMapping;
import com.PLATO.entities.SubjobCheckpointDetails;
import com.PLATO.entities.ToolMaster;
import com.PLATO.userTO.NodeTO;
import com.PLATO.utilities.GenericDBUtil;
import com.PLATO.utilities.XMLUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author 10643380(Rahul Bhardwaj) this service is used to get all nodes from
 *         the database
 * @author 10650463( Harsh Mathur ) delete service 
 *  
 */

@Path("NodeService")

public class NodeService {

	private static final Logger logger = Logger.getLogger(NodeService.class);
	private static GenericDBUtil genericDBUtil = new GenericDBUtil();

	@GET
	@Path("fetchAllNodes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fectchAllNodes() throws IOException {
		System.out.println("fetching all nodes:");
		List<NodeTO> nodeTOList = new ArrayList<NodeTO>(); 
		List<Object> nodeMasterList;
		try {
			nodeMasterList = GenericDaoSingleton.getGenericDao().findAll(NodeMaster.class);
			if (nodeMasterList == null) {
				return Response.status(Response.Status.NOT_FOUND).entity(nodeTOList).build();
			}
			Iterator<Object> it = nodeMasterList.iterator();
			while (it.hasNext()) {
				NodeTO nodeTo = new NodeTO();
				NodeMaster node = (NodeMaster) it.next();
				nodeTo.setNode_id(node.getNode_id());
				nodeTo.setNode_name(node.getNode_name());
				nodeTOList.add(nodeTo);
			}
			return Response.status(Response.Status.OK).entity(nodeTOList).build();

		} catch (Exception e) {
			System.out.println("Error while fetching NodeMaster list");
			e.printStackTrace();
			nodeTOList = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(nodeTOList).build();
		}

	}

	
	@GET
	@Path("fetchAllNodesWithStatus")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fectchAllNodesWithStatus() throws IOException {

		logger.info("fetching all nodes with status");

		List<NodeTO> nodeTOList = new ArrayList<NodeTO>(); 
		List<Object> nodeMasterList;
		HashMap<String, String> queryMap = new HashMap<String, String>();

		try {
			nodeMasterList = GenericDaoSingleton.getGenericDao().findAll(NodeMaster.class);
			if (nodeMasterList == null) {
				logger.error("error in fetching all nodes");

				return Response.status(Response.Status.NOT_FOUND).entity(nodeTOList).build();
			}
			String inputLine;
			URL url = new URL(
					GlobalConstants.JENKINS_URL + ":" + GlobalConstants.JENKINS_PORT + "/jenkins/computer/api/xml");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("GET");

			Thread.sleep(5000);
			System.out.println("Response Code : " + con.getResponseCode());
			if (con.getResponseCode() != 200) {
				logger.info("Response code: " + con.getResponseCode());

				return null;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String output = response.toString();
			if (output != null) {
				Document doc = XMLUtilities.convertStringToDocument(output);
				Node computerSet = doc.getElementsByTagName("computerSet").item(0);
				NodeList computers = computerSet.getChildNodes();

				for (int count = 0; count < computers.getLength(); count++) {

					Node computer = doc.getElementsByTagName("computer").item(count);
					if (computer != null) {
						Element eElement = (Element) computer;
						Node displayName = eElement.getElementsByTagName("displayName").item(0);

						Node offineStatus = eElement.getElementsByTagName("offline").item(0);
						String offline = offineStatus.getTextContent();
						String online;

						if (offline.equalsIgnoreCase("true")) {
							online = "Offline";
						} else {
							online = "Online";
						}
						queryMap.put(displayName.getTextContent(), online);
					} else {
						continue;
					}
				}
				Iterator<Object> it = nodeMasterList.iterator();
				while (it.hasNext()) {
					NodeTO nodeTo = new NodeTO();
					NodeMaster node = (NodeMaster) it.next();
					nodeTo.setNode_id(node.getNode_id());
					nodeTo.setNode_name(node.getNode_name());

					String nname = node.getNode_name();

					if (nname.equalsIgnoreCase("master")) {
						nname = "master";
					}

					if (queryMap != null) {
						if (queryMap.containsKey(nname)) {
							System.out.println(queryMap.get(nname));
							nodeTo.setStatus(queryMap.get(nname));
						} else {
							nodeTo.setStatus("Node Not Created");
						}
					} else {
						nodeTo.setStatus("Node Not Created");
					}

					nodeTOList.add(nodeTo);
				}
				logger.info("Nodes with status fetching successfully");
				return Response.status(Response.Status.OK).entity(nodeTOList).build();
			} else {
				logger.error("Jenkins is not up and running");
				System.out.println("jenkins is not up and running");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(nodeTOList).build();

			}

		} catch (Exception e) {
			logger.error("Error while fetching NodeMaster list");

			System.out.println("Error while fetching NodeMaster list");
			e.printStackTrace();
			nodeTOList = null;
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(nodeTOList).build();
		}

	}

	public List<NodeTO> fectchAllNodesWithStatusForJobExecution() throws IOException {

		logger.info("fetching all nodes with status");

		List<NodeTO> nodeTOList = new ArrayList<NodeTO>(); 
		List<Object> nodeMasterList;
		HashMap<String, String> queryMap = new HashMap<String, String>();

		try {
			nodeMasterList = GenericDaoSingleton.getGenericDao().findAll(NodeMaster.class);
			if (nodeMasterList == null) {
				logger.error("error in fetching all nodes");
				nodeTOList = null;
				return nodeTOList;
			}
			String inputLine;
			URL url = new URL(
					GlobalConstants.JENKINS_URL + ":" + GlobalConstants.JENKINS_PORT + "/jenkins/computer/api/xml");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("GET");

			Thread.sleep(5000);
			System.out.println("Response Code : " + con.getResponseCode());
			if (con.getResponseCode() != 200) {
				logger.info("Response code: " + con.getResponseCode());

				return null;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String output = response.toString();
			if (output != null) {
				Document doc = XMLUtilities.convertStringToDocument(output);
				Node computerSet = doc.getElementsByTagName("computerSet").item(0);
				NodeList computers = computerSet.getChildNodes();

				for (int count = 0; count < computers.getLength(); count++) {

					Node computer = doc.getElementsByTagName("computer").item(count);
					if (computer != null) {
						Element eElement = (Element) computer;
						Node displayName = eElement.getElementsByTagName("displayName").item(0);

						Node offineStatus = eElement.getElementsByTagName("offline").item(0);
						String offline = offineStatus.getTextContent();
						String online;

						if (offline.equalsIgnoreCase("true")) {
							online = "Offline";
						} else {
							online = "Online";
						}
						queryMap.put(displayName.getTextContent(), online);
					} else {
						continue;
					}
				}
				Iterator<Object> it = nodeMasterList.iterator();
				while (it.hasNext()) {
					NodeTO nodeTo = new NodeTO();
					NodeMaster node = (NodeMaster) it.next();
					nodeTo.setNode_id(node.getNode_id());
					nodeTo.setNode_name(node.getNode_name());

					String nname = node.getNode_name();

					if (nname.equalsIgnoreCase("master")) {
						nname = "master";
					}

					if (queryMap != null) {
						if (queryMap.containsKey(nname)) {
							System.out.println(queryMap.get(nname));
							nodeTo.setStatus(queryMap.get(nname));
						} else {
							nodeTo.setStatus("Node Not Created");
						}
					} else {
						nodeTo.setStatus("Node Not Created");
					}

					nodeTOList.add(nodeTo);
				}
				logger.info("Nodes with status fetching successfully");
				return nodeTOList;
			} else {
				logger.error("Jenkins is not up and running");
				System.out.println("jenkins is not up and running");
				return nodeTOList;

			}

		} catch (Exception e) {
			logger.error("Error while fetching NodeMaster list");

			System.out.println("Error while fetching NodeMaster list");
			e.printStackTrace();
			nodeTOList = null;
			return nodeTOList;
		}

	}

	@POST
	@Path(value = "create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNode(NodeMaster nodeMaster) {
		System.out.println("creating node");
		try {
			NodeMaster nodeMst = new NodeMaster();
			File dir = new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name());
			boolean successful = dir.mkdir();
			if (successful) {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				ClassLoader classLoader = this.getClass().getClassLoader();
				File file = new File(classLoader.getResource("xmlFiles/nodeConfig.xml").getFile());
				Document doc = docBuilder.parse(file);
				Element name = (Element) doc.getElementsByTagName("name").item(0);
				name.appendChild(doc.createTextNode(nodeMaster.getNode_name()));
				StringWriter sw = new StringWriter();
				javax.xml.transform.Transformer serializer = TransformerFactory.newInstance().newTransformer();
				serializer.transform(new DOMSource(doc), new StreamResult(sw));
				String st = sw.toString();
				try {
					File fileXml = new File(
							GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name() + "/config.xml");
					FileWriter fileWriter = new FileWriter(fileXml);
					fileWriter.write(st);
					fileWriter.flush();
					fileWriter.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				nodeMst = (NodeMaster) GenericDaoSingleton.getGenericDao().createOrUpdate(nodeMaster);
			} else {
				System.out.println("could not create node in jenkins");
			}
			reloadJenkins();
			if (nodeMst != null) {
				return Response.status(Response.Status.OK).entity("Node created Successfully").build();
			}

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create Node").build();
		}
		return Response.status(Response.Status.OK).entity("created successfuly").build();

	}

	@PUT
	@Path(value = "update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateNode(NodeMaster nodeMaster) {

		
		try {
			String queryString = "from NodeMaster WHERE node_id=:node_id";
			HashMap<String, Object> keyvalueMapFind = new HashMap<String, Object>();
			keyvalueMapFind.put("node_id", nodeMaster.getNode_id());
			NodeMaster nodeMasterExisting = (NodeMaster) GenericDaoSingleton.getGenericDao()
					.findUniqueByQuery(queryString, keyvalueMapFind);

			
			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("node_name", nodeMaster.getNode_name());
			queryMap.put("node_id", nodeMaster.getNode_id());
			File dir = new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMasterExisting.getNode_name());
			File file = new File(
					GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMasterExisting.getNode_name() + "/config.xml");
			file.delete();
			dir.renameTo(new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name()));
			file = new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name() + "/config.xml");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			ClassLoader classLoader = this.getClass().getClassLoader();
			Document doc = docBuilder.parse(new File(classLoader.getResource("xmlFiles/nodeConfig.xml").getFile()));
			Element name = (Element) doc.getElementsByTagName("name").item(0);
			name.appendChild(doc.createTextNode(nodeMaster.getNode_name()));
			StringWriter sw = new StringWriter();
			javax.xml.transform.Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.transform(new DOMSource(doc), new StreamResult(sw));
			String st = sw.toString();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(st);
			fileWriter.flush();
			fileWriter.close();

			String query = "update NodeMaster set node_name=:node_name where node_id=:node_id";
			int returnValue = GenericDaoSingleton.getGenericDao().updateQuery(query, queryMap);
			if (returnValue == 0) {
				return Response.status(Response.Status.NOT_FOUND).entity("could not update").build();
			}
			reloadJenkins();
			return Response.status(Response.Status.OK).entity("updated successfully").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Update Node").build();
		}

	}

	@SuppressWarnings("unchecked")
	@DELETE
	@Path(value = "delete/{nodeId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("nodeId") int nodeId) {
		try {
			String queryString = "from NodeMaster WHERE node_id=:node_id";
			HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
			keyvalueMap.put("node_id", nodeId);
			NodeMaster nodeMaster = (NodeMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(queryString,
					keyvalueMap);
			if (nodeMaster == null) {
				System.out.println("could not find NodeMaster");
				return Response.status(Response.Status.NOT_FOUND).entity("Could not find").build();
			}
			try {
				File dir = new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name());
				File file = new File(
						GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name() + "/config.xml");
				file.delete();
				dir.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				List<NodeMaster> nodeMasterReplace = (List<NodeMaster>) genericDBUtil
						.fetchResultFromDB(NodeMaster.class, "node_name", "master", null);
				NodeMaster nodeMasterRepl = null;
				if (null != nodeMasterReplace && !nodeMasterReplace.isEmpty()) {
					nodeMasterRepl = nodeMasterReplace.get(0);
				}

				List<ProjectToolMapping> projectToolMappings = (List<ProjectToolMapping>) genericDBUtil
						.fetchResultFromDB(ProjectToolMapping.class, "nodeMaster.node_name", nodeMaster.getNode_name(),
								null);
				if (null != projectToolMappings && !projectToolMappings.isEmpty()) {
					for (ProjectToolMapping projectToolMapping : projectToolMappings) {
						projectToolMapping.setNodeMaster(nodeMasterRepl);
						genericDBUtil.updateEntity(projectToolMapping);
					}
				}

				List<ModuleSubJobsJenkins> moduleSubjobJenkinMasters = (List<ModuleSubJobsJenkins>) genericDBUtil
						.fetchResultFromDB(ModuleSubJobsJenkins.class, "nodeMaster.node_name",
								nodeMaster.getNode_name(), null);
				if (null != moduleSubjobJenkinMasters && !moduleSubjobJenkinMasters.isEmpty()) {
					for (ModuleSubJobsJenkins moduleSubjobJenkinMaster : moduleSubjobJenkinMasters) {
						moduleSubjobJenkinMaster.setNodeMaster(nodeMasterRepl);
						genericDBUtil.updateEntity(moduleSubjobJenkinMaster);
					}
				}

				List<ToolMaster> toolMasters = (List<ToolMaster>) genericDBUtil.fetchResultFromDB(ToolMaster.class,
						"nodeMaster.node_name", nodeMaster.getNode_name(), null);
				if (null != toolMasters && !toolMasters.isEmpty()) {
					for (ToolMaster toolMaster : toolMasters) {
						toolMaster.setNodeMaster(nodeMasterRepl);
						genericDBUtil.updateEntity(toolMaster);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				GenericDaoSingleton.getGenericDao().delete(NodeMaster.class, nodeMaster.getNode_name());
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.OK).entity("Error while deleting node").build();
			}

			reloadJenkins();
			return Response.status(Response.Status.OK).entity("Node deleted Successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete Node").build();
		}

	}

	@DELETE
	@Path(value = "deleteNodeByName/{nodeName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNodeByName(@PathParam("nodeName") String nodeName) {
		// logger.info("Deleting account ");
		try {
			System.out.println(" node name is " + nodeName);
			String queryString = "from NodeMaster WHERE node_Name=:nodeName";
			HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
			keyvalueMap.put("nodeName", nodeName);
			NodeMaster nodeMaster = (NodeMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(queryString,
					keyvalueMap);
			if (nodeMaster == null) {
				System.out.println("could not find NodeMaster");
				return Response.status(Response.Status.NOT_FOUND).entity("Could not find").build();
			}

			try {
				File dir = new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name());
				File file = new File(
						GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name() + "/config.xml");
				file.delete();
				dir.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}

			GenericDaoSingleton.getGenericDao().delete(NodeMaster.class, nodeName);
			reloadJenkins();
			return Response.status(Response.Status.OK).entity("Node deleted Successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete Node").build();
		}

	}

	@SuppressWarnings("unchecked")
	@DELETE
	@Path(value = "deleteByNodeId/{nodeId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteByNodeId(@PathParam("nodeId") int nodeId) {
		// logger.info("Deleting account ");
		try {
			String queryString = "from NodeMaster WHERE node_id=:node_id";
			HashMap<String, Object> keyvalueMap = new HashMap<String, Object>();
			keyvalueMap.put("node_id", nodeId);
			NodeMaster nodeMaster = (NodeMaster) GenericDaoSingleton.getGenericDao().findUniqueByQuery(queryString,
					keyvalueMap);
			if (nodeMaster == null) {
				System.out.println("could not find Node");
				return Response.status(Response.Status.NOT_FOUND).entity("Could not find node").build();
			}
			try {
				File dir = new File(GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name());
				File file = new File(
						GlobalConstants.JENKINS_HOME + "/nodes/" + nodeMaster.getNode_name() + "/config.xml");
				file.delete();
				dir.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				List<NodeMaster> nodeMasterReplace = (List<NodeMaster>) genericDBUtil
						.fetchResultFromDB(NodeMaster.class, "node_name", "master", null);
				NodeMaster nodeMasterRepl = null;
				if (null != nodeMasterReplace && !nodeMasterReplace.isEmpty()) {
					nodeMasterRepl = nodeMasterReplace.get(0);
				}

				List<ProjectToolMapping> projectToolMappings = (List<ProjectToolMapping>) genericDBUtil
						.fetchResultFromDB(ProjectToolMapping.class, "nodeMaster.node_name", nodeMaster.getNode_name(),
								null);
				if (null != projectToolMappings && !projectToolMappings.isEmpty()) {
					for (ProjectToolMapping projectToolMapping : projectToolMappings) {
						projectToolMapping.setNodeMaster(nodeMasterRepl);
						genericDBUtil.updateEntity(projectToolMapping);
					}
				}

				List<ModuleSubJobsJenkins> moduleSubjobJenkinMasters = (List<ModuleSubJobsJenkins>) genericDBUtil
						.fetchResultFromDB(ModuleSubJobsJenkins.class, "nodeMaster.node_name",
								nodeMaster.getNode_name(), null);
				if (null != moduleSubjobJenkinMasters && !moduleSubjobJenkinMasters.isEmpty()) {
					for (ModuleSubJobsJenkins moduleSubjobJenkinMaster : moduleSubjobJenkinMasters) {
						moduleSubjobJenkinMaster.setNodeMaster(nodeMasterRepl);
						genericDBUtil.updateEntity(moduleSubjobJenkinMaster);
					}
				}

				List<ToolMaster> toolMasters = (List<ToolMaster>) genericDBUtil.fetchResultFromDB(ToolMaster.class,
						"nodeMaster.node_name", nodeMaster.getNode_name(), null);
				if (null != toolMasters && !toolMasters.isEmpty()) {
					for (ToolMaster toolMaster : toolMasters) {
						toolMaster.setNodeMaster(nodeMasterRepl);
						genericDBUtil.updateEntity(toolMaster);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				GenericDaoSingleton.getGenericDao().delete(NodeMaster.class, nodeMaster.getNode_name());
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.OK).entity("Error while deleting node").build();
			}

			reloadJenkins();
			return Response.status(Response.Status.OK).entity("Node deleted Successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete Node").build();
		}

	}

	// function to reload config files from jenkins workspace
	public void reloadJenkins() throws Exception {
		try {

			URL reloadUrl = new URL(
					GlobalConstants.JENKINS_URL + ":" + GlobalConstants.JENKINS_PORT + "/jenkins/reload");
			System.out.println(reloadUrl);
			HttpURLConnection con = (HttpURLConnection) reloadUrl.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Accept", "text/xml");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write("Reload");
			wr.flush();
			wr.close();

			// reading the response
			/*
			 * InputStreamReader reader = new InputStreamReader(
			 * con.getInputStream() ); StringBuilder buf = new StringBuilder();
			 * char[] cbuf = new char[ 2048 ]; int num; while ( -1 !=
			 * (num=reader.read( cbuf ))) { buf.append( cbuf, 0, num ); } String
			 * result = buf.toString();
			 */

			StringBuilder sb = new StringBuilder();
			System.out.println(con.getResponseCode());
			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				System.out.println("Received Response :" + sb.toString());
			}
		} catch (Exception e) {
			System.out.println("Reload exception");
		}
	}

}
