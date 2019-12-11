// Created on 25.10.2015 by Luzius Meisser

package com.agentecon.agent;

public class AgentRef {

	private IAgent agent;
	
	public AgentRef(IAgent agent){
		this.agent = agent;
	}
	
	public void set(IAgent agent) {
		assert this.agent.getAgentId() == agent.getAgentId();
		this.agent = agent;
	}
	
	public IAgent get(){
		return agent;
	}
	
	@Override
	public int hashCode() {
		return agent.getAgentId();
	}
	
	@Override
	public boolean equals(Object o) {
		return agent.getAgentId() == ((AgentRef)o).agent.getAgentId();
	}

}
