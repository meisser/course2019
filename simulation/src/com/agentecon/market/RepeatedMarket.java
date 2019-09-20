package com.agentecon.market;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.goods.Good;
import com.agentecon.production.IGoodsTrader;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.world.Country;

public class RepeatedMarket {

	private final Country world;
	private final SimulationListeners listeners;
	private final MarketStatistics stats;

	public RepeatedMarket(Country world, SimulationListeners listeners, MarketStatistics stats) {
		this.world = world;
		this.listeners = listeners;
		this.stats = stats;
	}

	public void iterate(int day, int iterations) {
		if (iterations == 0) {
			Market market = new Market(world.getRand());
			trade(market);
			market.close(day);
		} else {
			doIterate(day, iterations);
		}
	}

	public void doIterate(int day, int iterations) {
		MarketObserver observer = new MarketObserver(iterations);
		while (true) {
			world.startTransaction();
			Market market = new Market(world.getRand());
			market.addMarketListener(observer);
			trade(market);
			if (observer.shouldTryAgain()) {
				market.cancel();
				world.abortTransaction();
			} else {
				market.close(day);
				world.commitTransaction();
				break;
			}
		}
	}

	protected void trade(Market market) {
		Collection<IGoodsTrader> firms = world.getAgents().getRandomGoodsMarketMakers();
		Collection<IMarketParticipant> cons = world.getAgents().getRandomMarketParticipants();
		market.addMarketListener(stats);
		listeners.notifyGoodsMarketOpened(market);
		for (IGoodsTrader firm : firms) {
			firm.offer(market);
		}
		for (IMarketParticipant c : cons) {
			c.tradeGoods(market);
		}
		for (IGoodsTrader firm : firms) {
			firm.adaptPrices();
		}
	}

	static class MarketObserver implements IMarketListener {

		static int count = 0;

		private int iters;
		private double sensitivity;
		private HashMap<Good, Average> current;
		private HashMap<Good, Average> prev;

		public MarketObserver(int maxIters) {
			this.iters = maxIters;
			this.sensitivity = 0.001;
			next();
		}

		protected void next() {
			this.prev = current;
			this.current = new InstantiatingHashMap<Good, Average>() {

				@Override
				protected Average create(Good key) {
					return new Average();
				}
			};
		}

		@Override
		public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
			current.get(good).add(quantity, payment / quantity);
		}

		@Override
		public void notifyTradesCancelled() {
			current.clear();
		}

		public boolean shouldTryAgain() {
			if (iters-- <= 0) {
				return false;
			} else if (prev == null) {
				next();
				return true;
			} else {
				Average change = new Average();
				for (Map.Entry<Good, Average> e : current.entrySet()) {
					double p1 = e.getValue().getAverage();
					double p2 = prev.get(e.getKey()).getAverage();
					double diff = Math.abs(p1 - p2) / p1;
					change.add(diff);
				}
				next();
				// sensitivity *= 1.3;
				// System.out.println(count++);
				return change.getAverage() > sensitivity;
			}
		}

	}

}
