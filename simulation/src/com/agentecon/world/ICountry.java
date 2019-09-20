package com.agentecon.world;

import java.util.Random;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.goods.Good;


public interface ICountry extends IAgentIdGenerator {
	
	public Random getRand();

	public int getDay();

	public Agents getAgents();
	
	public void add(IAgent agent);

	public Good getMoney();
	
}
