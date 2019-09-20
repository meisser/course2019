package com.agentecon.web.graph;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.agent.Agent;
import com.agentecon.agent.IAgent;

public class Child {

	public String label;
	public String author;
	public String type;
	
	public Child(String label, IAgent agent) {
		this.label = label;
		this.author = Agent.findAuthor(agent.getClass());
		this.type = Agent.findType(agent.getClass());
	}

	public static Collection<Child> wrap(Collection<? extends IAgent> agents) {
		HashMap<String, Child> children = new HashMap<>();
		for (IAgent agent: agents){
			String type = agent.getType();
			if (!children.containsKey(type)){
				children.put(type, new Child(type, agent));
			}
		}
		return children.values();
	}

}
