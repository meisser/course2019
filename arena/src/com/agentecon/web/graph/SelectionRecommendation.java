package com.agentecon.web.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.web.query.AgentQuery;
import com.agentecon.web.query.EQueryType;

public class SelectionRecommendation extends SimulationListenerAdapter {

	private HashSet<String> knownTypes;
	private HashSet<String> individualSuggestions;

	public SelectionRecommendation(ISimulation simulation, Set<String> agents) {
		this.knownTypes = new HashSet<>(simulation.getAgents().getFirmTypes());
		this.knownTypes.addAll(simulation.getAgents().getConsumerTypes());
		this.knownTypes.addAll(agents);
		this.individualSuggestions = new HashSet<>();
//		if (containsIndividual(agents)) {
//			simulation.addListener(this);
//		}
	}

	private boolean containsIndividual(Set<String> agents) {
		for (String selection : agents) {
			AgentQuery query = new AgentQuery(selection);
			if (query.getType() == EQueryType.ID) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void notifyAgentCreated(IAgent agent) {
		if (knownTypes.contains(agent.getType())) {
			individualSuggestions.add(Integer.toString(agent.getAgentId()));
		}
	}

	public Collection<String> getNewNodeSuggestions(ISimulation simulation) {
//		simulation.removeListener(this);
		
		ArrayList<String> newTypes = new ArrayList<>(individualSuggestions);
		for (String type : simulation.getAgents().getConsumerTypes()) {
			if (!knownTypes.contains(type)) {
				newTypes.add(type);
			}
		}
		for (String type : simulation.getAgents().getFirmTypes()) {
			if (!knownTypes.contains(type)) {
				newTypes.add(type);
			}
		}
		return newTypes;
	}

}
