package com.agentecon.research;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IFirm;
import com.agentecon.market.IStatistics;

public interface IFounder {
	
	/**
	 * Every morning, before trading takes place, all agents that implement IFounder
	 * are asked to consider creating a firm.
	 * 
	 * For firms that require a production function, a production function can be
	 * obtained through the research object.
	 * 
	 * Founders should equip their firms with a basic amount of money and man-hours
	 * in order to kick-start them.
	 * @param id TODO
	 */
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id);
	
}
