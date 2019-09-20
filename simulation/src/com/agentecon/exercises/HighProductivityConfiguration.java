package com.agentecon.exercises;

import com.agentecon.IAgentFactory;
import com.agentecon.consumer.Weight;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;

public class HighProductivityConfiguration extends FarmingConfiguration {
	
	public HighProductivityConfiguration(IAgentFactory factory, int agents) {
		super(factory, agents);
	}
	
	@Override
	public CobbDouglasProductionWithFixedCost createProductionFunction(Good desiredOutput) {
		assert desiredOutput.equals(POTATOE);
		return new CobbDouglasProductionWithFixedCost(POTATOE, 3.0, FIXED_COSTS, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}

}
