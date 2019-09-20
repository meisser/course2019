package com.agentecon.verification;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.Average;
import com.agentecon.util.IAverage;
import com.agentecon.util.InstantiatingHashMap;

public class PriceMetric extends SimulationListenerAdapter implements IMarketListener {

	private HashMap<Good, AccumulatingAverage> prices;
	private HashMap<Good, IAverage> volume;

	private boolean verbose;
	private int startRecordingDate;
	private int endRecordingDate;

	public PriceMetric(int startRecordingDate) {
		this(startRecordingDate, Integer.MAX_VALUE, false);
	}

	public PriceMetric(int start, int end, boolean verbose) {
		this.verbose = verbose;
		this.startRecordingDate = start;
		this.endRecordingDate = end;
		this.prices = new InstantiatingHashMap<Good, AccumulatingAverage>() {

			@Override
			protected AccumulatingAverage create(Good key) {
				AccumulatingAverage wma = new AccumulatingAverage();
				return wma;
			}
		};
		this.volume = new InstantiatingHashMap<Good, IAverage>() {

			@Override
			protected IAverage create(Good key) {
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
		for (AccumulatingAverage avg : prices.values()) {
			avg.reset();
		}
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}
	
	@Override
	public void notifyMarketClosed(int day) {
	}

	public boolean isStable() {
		for (AccumulatingAverage ma : prices.values()) {
			if (!ma.isStable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void notifyDayEnded(int day) {
		if (day == startRecordingDate) {
			notifyTradesCancelled();
			if (verbose) {
				System.out.print("\t");
				for (Good good : prices.keySet()) {
					System.out.print(good.toString() + "\t");
				}
				System.out.println();
			}
		} else if (day >= startRecordingDate && day < endRecordingDate) {
			String line = verbose ? Integer.toString(day) : null;
			for (Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
				AccumulatingAverage avg = e.getValue();
				volume.get(e.getKey()).add(avg.getWeight());
				double val = avg.flush();
				if (verbose) {
					line += "\t" + val;
				}
			}
			if (verbose) {
				System.out.println(line);
			}
		}
	}

	public void printResult(PrintStream ps) {
		System.out.println("Good\tPrice\tProduction\tTurnover");
		for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
			double price = e.getValue().getWrapped().getAverage();
			ps.print(e.getKey() + "\t" + price);
			if (volume.containsKey(e.getKey())) {
				double vol = volume.get(e.getKey()).getAverage();
				ps.print("\t" + vol);
				ps.print("\t" + vol * price);
			}
			System.out.println();
		}

		// if (prices.containsKey(new Good("output 0"))) {
		// AccumulatingAverage pizzaPrice = prices.get(new Good("output 0"));
		// double priceNormalization = pizzaPrice.getWrapped().getAverage();
		// System.out.println("\nNormalized prices:");
		// for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
		// ps.println(e.getKey() + " price: " +
		// e.getValue().getWrapped().normalize(priceNormalization));
		// if (volume.containsKey(e.getKey())) {
		// ps.println(e.getKey() + " production: " +
		// volume.get(e.getKey()).normalize(priceNormalization));
		// }
		// }
		// }
	}

	public Result getResult() {
		Result res = new Result();
		for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
			res.include(e.getKey(), e.getValue().getWrapped().getAverage(), volume.get(e.getKey()).getAverage());
		}
		return res;
	}

}
