/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.web.data.CollectiveConsumerData;
import com.agentecon.web.data.CollectiveFirmData;
import com.agentecon.web.data.ConsumerData;
import com.agentecon.web.data.FirmData;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.graph.Child;
import com.agentecon.web.methods.Parameters;

public class AgentQuery {

	private EQueryType type;
	private String query;
	private int id;

	public AgentQuery(String query) {
		this.query = query;
		this.type = EQueryType.derive(query);
		if (this.type == EQueryType.ID) {
			this.id = Integer.parseInt(query);
		}
	}
	
	public EQueryType getType() {
		return type;
	}
	
	public static final String getExample() {
		return Parameters.SELECTION + "=" + EQueryType.FIRMS_QUERY;
	}

	public ENodeType getType(IAgents agents, Set<String> shownAgents) {
		switch (type) {
		case CONSUMERS:
			return ENodeType.CONSUMER;
		case FIRMS:
			return ENodeType.FIRM;
		default:
			return ENodeType.UNKNOWN;
		case TYPE:
			if (agents.getFirmTypes().contains(query)) {
				return ENodeType.FIRM;
			} else if (agents.getConsumerTypes().contains(query)) {
				return ENodeType.CONSUMER;
			} else {
				return ENodeType.UNKNOWN;
			}
		case ID:
			IAgent agent = agents.getAgent(id);
			if (agent == null) {
				return ENodeType.UNKNOWN;
			} else if (!shownAgents.contains(agent.getType())){
				return ENodeType.DISCONNECTED;
			} else if (agent instanceof IConsumer) {
				return ENodeType.CONSUMER;
			} else {
				assert agent instanceof IFirm;
				return ENodeType.FIRM;
			}
		}
	}

	public String getParent(IAgents agents) {
		switch (type) {
		default:
		case CONSUMERS:
		case FIRMS:
			return "";
		case TYPE:
			if (agents.getFirmTypes().contains(query)) {
				return EQueryType.FIRMS_QUERY;
			} else if (agents.getConsumerTypes().contains(query)) {
				return EQueryType.CONSUMERS_QUERY;
			} else {
				return EQueryType.UNKNOWN;
			}
		case ID:
			IAgent agent = agents.getAgent(id);
			if (agent == null) {
				return EQueryType.UNKNOWN;
			} else {
				return agent.getType();
			}
		}
	}

	public void forEach(IAgents agents, Consumer<IAgent> consumer) {
		switch (type) {
		case CONSUMERS:
			agents.getConsumers().forEach(consumer);
			break;
		case FIRMS:
			agents.getFirms().forEach(consumer);
			break;
		case TYPE:
			agents.getAgents(query).forEach(consumer);
			break;
		case ID:
			IAgent agent = agents.getAgent(id);
			if (agent != null){
				consumer.accept(agent);
			}
			break;
		default:
		}
	}

	public Collection<Child> getChildren(IAgents agents) {
		switch (type) {
		case CONSUMERS:
			return Child.wrap(agents.getConsumers());
		case FIRMS:
			return Child.wrap(agents.getFirms());
		case TYPE:
			Collection<IAgent> agentsOfType = agents.getAgents(query);
			ArrayList<Child> ids = new ArrayList<>(agentsOfType.size());
			for (IAgent agent : agentsOfType) {
				ids.add(new Child(Integer.toString(agent.getAgentId()), agent));
			}
			return ids;
		default:
			return Collections.emptyList();
		}
	}

	public boolean matches(IAgent agent) {
		switch (type) {
		case CONSUMERS:
			return agent instanceof IConsumer;
		case FIRMS:
			return agent instanceof IFirm;
		case ID:
			return agent.getAgentId() == id;
		case TYPE:
			return agent.getType().equals(query);
		default:
			return false;
		}
	}

	public JsonData getAgentData(IAgents agents) {
		switch (type) {
		case CONSUMERS:
			return new CollectiveConsumerData(agents.getConsumers());
		case FIRMS:
			return new CollectiveFirmData(agents.getFirms());
		case ID:
			IAgent agent = agents.getAgent(id);
			if (agent instanceof IConsumer) {
				return new ConsumerData((IConsumer) agent);
			} else if (agent instanceof IFirm) {
				return new FirmData((IFirm) agent);
			} else {
				assert false : "Agent " + query + " not found";
				return null;
			}
		case TYPE:
			if (agents.getConsumerTypes().contains(query)) {
				return new CollectiveConsumerData(extract(agents.getConsumers(), query));
			} else {
				assert agents.getFirmTypes().contains(query);
				return new CollectiveFirmData(extract(agents.getFirms(), query));
			}
		default:
			assert false : query + " not found";
			return null;
		}
	}

	private <T extends IAgent> Collection<T> extract(Collection<T> agents, String selection) {
		ArrayList<T> list = new ArrayList<>();
		for (T agent : agents) {
			if (agent.getType().equals(selection)) {
				list.add(agent);
			}
		}
		return list;
	}

	public int compareTo(AgentQuery query) {
		return type.ordinal() - query.type.ordinal();
	}
	
	@Override
	public String toString(){
		return query;
	}

}