package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;

public class BufferingMortalConsumer extends MortalConsumer {
	
	private static final double CAPITAL_BUFFER = 0.80;
	public static final double MINIMUM_WORKING_HOURS = 5;

	public BufferingMortalConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
	}
	
	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		// We hide a relative amount of what is left as a buffer
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		super.trade(reducedInv, market);
	}

}
