// Created by Luzius on Apr 22, 2014

package com.agentecon;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import com.agentecon.agent.IAgents;
import com.agentecon.configuration.FundConfiguration;
import com.agentecon.configuration.IConfiguration;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.StockMarket;
import com.agentecon.goods.Good;
import com.agentecon.market.IDiscountRate;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IStatistics;
import com.agentecon.market.MarketStatistics;
import com.agentecon.market.RepeatedMarket;
import com.agentecon.production.IProducer;
import com.agentecon.sim.Event;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.util.Average;
import com.agentecon.util.Numbers;
import com.agentecon.world.Country;

// The world
public class Simulation implements ISimulation, IStatistics, IIteratedSimulation {

	private IConfiguration metaConfig;

	private SimulationConfig config;

	private int day;
	private Queue<SimEvent> events;
	private SimulationListeners listeners;
	private Country world;
	private StockMarket stocks;

	private MarketStatistics goodsMarketStats;

	public Simulation() throws IOException {
		this(new FundConfiguration());
	}
	
	public Simulation(IConfiguration metaConfig) {
		this(metaConfig.createNextConfig());
		this.metaConfig = metaConfig;
		System.out.println("Instantiated simulation from " + metaConfig.getClass().getSimpleName());
	}

	public Simulation(SimulationConfig config) {
		this.config = config;
		this.listeners = new SimulationListeners();
		this.world = new Country(config, listeners);
		this.events = createEventQueue(config.getEvents());
		this.stocks = new StockMarket(world, listeners);
		this.goodsMarketStats = new MarketStatistics();
		this.day = 0;
	}

	private PriorityBlockingQueue<SimEvent> createEventQueue(Collection<Event> collection) {
		PriorityBlockingQueue<SimEvent> queue = new PriorityBlockingQueue<>();
		for (Event e : collection) {
			e.init(getRandomNumberGenerator());
			queue.add((SimEvent) e);
		}
		return queue;
	}

	@Override
	public boolean hasNext() {
		return metaConfig != null && metaConfig.shouldTryAgain();
	}

	public ISimulation getNext() {
		if (hasNext()) {
			return new Simulation(metaConfig);
		} else {
			return null;
		}
	}

	public String getComment() {
		return metaConfig == null ? null : metaConfig.getComment();
	}

	public void run() {
		forwardTo(config.getRounds());
	}

	@Override
	public void forwardTo(int targetDay) {
		targetDay = Math.min(targetDay, getConfig().getRounds());
		for (; day < targetDay; day++) {
			processEvents(day); // must happen before daily endowments
			world.prepareDay(getStatistics());
			stocks.trade(day, getStatistics());
			RepeatedMarket market = new RepeatedMarket(world, listeners, goodsMarketStats);
			goodsMarketStats.notifyMarketOpened();
			market.iterate(day, config.getIntradayIterations());
			for (IProducer firm : world.getAgents().getProducers()) {
				firm.produce();
			}
			world.finishDay(getStatistics());
		}
	}

	public IStatistics getStatistics() {
		return this;
	}

	public int getDay() {
		return day;
	}

	@Override
	public boolean isFinished() {
		return day >= config.getRounds();
	}

	private void processEvents(int day) {
		while (!events.isEmpty() && events.peek().getDay() <= day) {
			SimEvent event = events.poll();
			event.execute(day, world, getStatistics());
			listeners.notifyEvent(event);
			if (event.reschedule()) {
				events.add(event);
			}
		}
	}

	@Override
	public SimulationConfig getConfig() {
		return config;
	}

	@Override
	public void addListener(ISimulationListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(ISimulationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public IAgents getAgents() {
		return world.getAgents();
	}

	@Override
	public String toString() {
		return "Simulation at day " + day + " with " + world.getAgents().getAgents().size() + " agents";
	}

	@Override
	public IMarketStatistics getGoodsMarketStats() {
		return goodsMarketStats;
	}

	@Override
	public IMarketStatistics getStockMarketStats() {
		return stocks.getStats();
	}

	@Override
	public Random getRandomNumberGenerator() {
		return world.getRand();
	}

	@Override
	public Average getAverageUtility() {
		return world.getAverageUtility();
	}

	@Override
	public IDiscountRate getDiscountRate() {
		return config;
	}

	@Override
	public Good getMoney() {
		return config.getMoney();
	}
	
	public static void main(String[] args) throws Exception {
		Simulation sim = new Simulation();
		long t0 = System.nanoTime();
		sim.run();
		long t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1000000);
	}

}
