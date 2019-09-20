package com.agentecon.agent;

import java.util.Random;

/**
 * Needed to create new Agents.
 */
public interface IAgentIdGenerator {
	
	/**
	 * Creates a unique id for an agent.
	 * The id is unique for that simulation run.
	 * Running the same simulation twice must lead to the same
	 * ids being assigned again. Thus, using a static counter
	 * won't do.
	 */
	public int createUniqueAgentId();
	
	public Random getRand();

	public int previewNextId();

}
