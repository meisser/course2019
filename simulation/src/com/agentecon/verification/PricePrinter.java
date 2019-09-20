package com.agentecon.verification;

import java.util.HashMap;
import java.util.Map.Entry;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class PricePrinter extends SimulationListenerAdapter implements IMarketListener {

	private HashMap<Good, Average> prices;

	private int startRecordingDate;
	private int endRecordingDate;

	public PricePrinter(int startRecordingDate) {
		this(startRecordingDate, Integer.MAX_VALUE);
	}

	public PricePrinter(int start, int end) {
		this.startRecordingDate = start;
		this.endRecordingDate = end;
		this.prices = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		this.prices.get(good).add(quantity, payment / quantity);
	}

	@Override
	public void notifyTradesCancelled() {
		this.prices.clear();
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		this.prices.clear();
		market.addMarketListener(this);
	}
	
	@Override
	public void notifyDayEnded(int day) {
		if (day >= startRecordingDate && day < endRecordingDate) {
			String line = Integer.toString(day);
			for (Entry<Good, Average> e : prices.entrySet()) {
				Average avg = e.getValue();
				line += "\t" + e.getKey() + "\t" + avg.getAverage() + "\t" + avg.getTotWeight();
			}
			System.out.println(line);
		}
	}

}
