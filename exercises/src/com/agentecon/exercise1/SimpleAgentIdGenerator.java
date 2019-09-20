package com.agentecon.exercise1;

import java.util.Random;

import com.agentecon.agent.IAgentIdGenerator;

public class SimpleAgentIdGenerator implements IAgentIdGenerator {

	@Override
	public int previewNextId() {
		return 1;
	}
	
	@Override
	public int createUniqueAgentId() {
		return 1;
	}

	@Override
	public Random getRand() {
		return new Random();
	}

}
