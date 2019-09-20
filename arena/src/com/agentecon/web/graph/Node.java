/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.graph;

import java.util.Set;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.market.IStatistics;
import com.agentecon.web.query.AgentQuery;
import com.agentecon.web.query.ENodeType;

public class Node implements Comparable<Node>{

	public String label;
	public String parent;
	public int children;
	public double size;
	
	private transient AgentQuery query;
	private transient AgentSize sizeQuery;
	
	public Node(String agent) {
		this.label = agent;
		this.query = new AgentQuery(agent);
	}

	public void initializeSizeQuery(ESizeType type, ISimulation agents) {
		this.sizeQuery = type.createQuery(query, agents);
	}
	
	public ENodeType getType(IAgents agents, Set<String> shownAgents) {
		return query.getType(agents, shownAgents);
	}

	public boolean contains(IAgent agent) {
		return query.matches(agent);
	}

	public void fetchData(IStatistics stats, IAgents agents) {
		this.parent = query.getParent(agents);
		this.children = query.getChildren(agents).size();
		this.size = sizeQuery.getSize(stats, query, agents);
	}

	@Override
	public int compareTo(Node o) {
		return query.compareTo(o.query);
	}
	
	@Override
	public String toString(){
		return label;
	}
	
}
