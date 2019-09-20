package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.IStock;
import com.agentecon.production.IProductionFunction;

public interface IAgentFactory {

	public default IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		throw new RuntimeException("Not implemented");
	}

	public default IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		return createConsumer(id, endowment, utilityFunction);
	}

	public default IFirm createFirm(IAgentIdGenerator id, IStock money) {
		return createFirm(id, new Endowment(money));
	}

	public default IFirm createFirm(IAgentIdGenerator id, Endowment end) {
		throw new RuntimeException("Not implemented");
	}

	public default IFirm createFirm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		throw new RuntimeException("Not implemented");
	}

}
