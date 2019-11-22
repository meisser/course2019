package com.agentecon.metric.series;

import com.agentecon.firm.Ticker;

public class TickerType implements IAgentType {

	private String name;
	private Ticker ticker;

	public TickerType(String name, Ticker ticker) {
		this.name = name;
		this.ticker = ticker;
	}

	@Override
	public String getName() {
		return name;
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
