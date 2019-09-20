// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.verification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingConcurrentHashMap;
import com.agentecon.util.InstantiatingHashMap;

public class MarketStats extends SimulationListenerAdapter implements IMarketListener {

	public static boolean PRINT_TICKER = true;

	private IAgents world;
	private Good index = new Good("Index");
	private Map<Good, Average> averages;

	public MarketStats(IAgents world) {
		this.world = world;
		this.averages = new InstantiatingConcurrentHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
	}

	public double getPrice(Good ticker) {
		return averages.get(ticker).getAverage();
	}
	
	@Override
	public void notifyDayStarted(int day) {
		assert averages.isEmpty();
	}
	
	@Override
	public void notifyStockMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		averages.get(good).add(quantity, payment / quantity);
	}

	@Override
	public void notifyTradesCancelled() {
		averages.clear();
	}
	
	@Override
	public void notifyMarketClosed(int day) {
		Average indexPoints = new Average();
		HashMap<Good, Average> sectorIndices = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		Average indexRatio = new Average();
		HashMap<Good, Average> sectorRatios = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		for (Entry<Good, Average> e : averages.entrySet()) {
			Average avgPrice = e.getValue();
			indexPoints.add(avgPrice);
			if (e.getKey() instanceof Ticker) {
				Ticker firm = (Ticker)e.getKey();
				Good sector = new Good(firm.getType());
				sectorIndices.get(sector).add(avgPrice);
				double dividends = world.getFirm(firm).getShareRegister().getAverageDividend();
				if (dividends > 1) {
					double peratio = avgPrice.getAverage() / dividends;
					indexRatio.add(peratio);
					sectorRatios.get(sector).add(peratio);
				}
			}
		}
		sectorIndices.put(index, indexPoints);
		HashMap<Good, Average> all = new HashMap<>();
		all.putAll(averages);
		all.putAll(sectorIndices);
		printTicker(all, day);
		averages.clear();
	}

	private ArrayList<Good> toPrint = new ArrayList<>();

	protected void printTicker(Map<Good, Average> map, int day) {
		if (PRINT_TICKER) {
			if (day == 1000) {
				toPrint.addAll(map.keySet());
				Collections.sort(toPrint);
				printLabels();
			} else if (day > 1000 && toPrint.size() < map.size()) {
				for (Good t : map.keySet()) {
					if (!toPrint.contains(t)) {
						toPrint.add(t);
					}
				}
				printLabels();
			}
			if (toPrint.size() > 0) {
				String line = Integer.toString(day);
				for (Good g : toPrint) {
					Average avg = map.get(g);
					if (avg == null) {
						line += "\t";
					} else {
						line += "\t" + avg.getAverage();
					}
				}
				System.out.println(line);
			}
		}
	}

	protected void printLabels() {
		String labels = "";
		for (Good g : toPrint) {
			labels += "\t" + g;
		}
		System.out.println(labels);
	}

	@Override
	public String toString() {
		return "Prices of " + averages.size() + " stocks";
	}

}
