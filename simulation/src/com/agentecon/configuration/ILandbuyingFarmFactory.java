package com.agentecon.configuration;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.Farm;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.market.IStatistics;

public interface ILandbuyingFarmFactory {
	
	public Farm considerCreatingNewFarm(IAgentIdGenerator id, Endowment end, CobbDouglasProductionWithFixedCost prodFun, IStatistics stats);

}
