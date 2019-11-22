package com.agentecon.metric.series;

import com.agentecon.firm.Ticker;

public class TickerType implements IAgentType {

	private Ticker ticker;

	public TickerType(Ticker ticker) {
		this.ticker = ticker;
	}

	@Override
	public String getName() {
		return ticker.getName();
	}

	@Override
	public String[] getTypeKeys() {
		return new String[] { ticker.getType() };
	}

	@Override
	public boolean isConsumer() {
		return false;
	}

	@Override
	public boolean isFirm() {
		return true;
	}

}
