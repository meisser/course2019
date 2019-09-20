// Created by Luzius on May 15, 2015

package com.agentecon.sim;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.agent.Agent;
import com.agentecon.goods.Good;
import com.agentecon.market.IDiscountRate;
import com.agentecon.research.IInnovation;

public class SimulationConfig implements IDiscountRate {

	private static final Good MONEY = new Good("Taler");
	
	private static final int DEFAULT_MARKET_RETRIES = 0;

	private long seed;
	private int rounds;
	private int marketRetries;

	private ArrayList<Event> events = new ArrayList<Event>();

	public SimulationConfig(int rounds) {
		this(rounds, 23);
	}

	public SimulationConfig(int rounds, int seed) {
		this(rounds, seed, DEFAULT_MARKET_RETRIES);
	}

	public SimulationConfig(int rounds, int seed, int wobbles) {
		this.seed = seed;
		this.rounds = rounds;
		this.marketRetries = wobbles;
	}
	
	public Good getMoney(){
		return MONEY;
	}

	public int getMaxAge() {
		return Integer.MAX_VALUE;
	}
	
	public long getSeed() {
		return seed;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void addEvent(Event e) {
		events.add(e);
	}

	public int getRounds() {
		return rounds;
	}
	
	public IOptimalityIndicator[] getOptimalFirmCountIndicators() {
		return new IOptimalityIndicator[0];
	}
	
	public IOptimalityIndicator[] getOptimalProductionIndicators() {
		return new IOptimalityIndicator[0];
	}

	public int getIntradayIterations() {
		return marketRetries;
	}

	public IInnovation getInnovation() {
		return null;
	}
	
	public String getName(){
		String suffix = "Configuration";
		String simple = Agent.findType(getClass());
		if (simple.endsWith(suffix)){
			return simple.substring(0, simple.length() - suffix.length());
		} else {
			return simple;
		}
	}

	public void diagnoseResult(PrintStream out, ISimulation stats) {
	}

	@Override
	public double getCurrentDiscountRate() {
		return 1.0 / getRounds();
	}
	
	public static boolean isServerConfig() {
		return System.getProperty("agentecon.server") != null;
	}

	public static boolean shouldLoadRemoteTeams() {
		return isServerConfig() || System.getProperty("agentecon.remoteteams") != null;
	}

}
