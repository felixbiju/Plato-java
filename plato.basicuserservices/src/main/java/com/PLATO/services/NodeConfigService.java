package com.PLATO.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.PLATO.Singletons.GenericDaoSingleton;
import com.PLATO.constants.ConstantQueries;
import com.PLATO.entities.NodeMaster;
import com.PLATO.userTO.NodeTO;

@Path("NodeConfigService")
public class NodeConfigService {
	

	/*
	 * Description : Service to fetch all nodes on node page
	 * Author: Sueanne Alphonso
	 * 
	 * */

	private static final Logger logger=Logger.getLogger(NodeConfigService.class);

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllNodes() {
		
		logger.info("Fetching all Nodes");

		List<NodeTO> nodeMasterList = new ArrayList<NodeTO>();
		NodeMaster nodeMaster;
		NodeTO nodeTO = null;

		try {

			HashMap<String, Object> queryMap = new HashMap<String, Object>();
			List<Object> nodeList = GenericDaoSingleton.getGenericDao()
					.findByQuery(ConstantQueries.GET_ALL_NODES, queryMap);

			if (nodeList == null || nodeList.size() == 0
					|| nodeList.isEmpty() == true) {
				logger.error("Nodes not found");
				//System.out.println("Nodes not found");
				nodeTO = null;
				return Response.status(Response.Status.NOT_FOUND)
						.entity(nodeTO).build();
			}

			for (int i = 0; i < nodeList.size(); i++) {
				nodeTO = new NodeTO();
				nodeMaster = (NodeMaster) nodeList.get(i);
				nodeTO.setNode_id(nodeMaster.getNode_id());
				nodeTO.setNode_name(nodeMaster.getNode_name());

				nodeMasterList.add(nodeTO);
			}

			logger.info("Nodes fetched successfully");
			return Response.status(Response.Status.OK).entity(nodeMasterList).build();

		} catch (Exception e) {
			nodeTO = null;
			logger.error("Error while getting nodes in Node Config service");

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(nodeTO).build();
		}

	}

}
