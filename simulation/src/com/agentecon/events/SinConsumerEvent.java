package com.agentecon.events;

import com.agentecon.world.ICountry;

public abstract class SinConsumerEvent extends SimEvent {
	
	private static final double FLATNESS = 2;
	
	private int start;
	private int cycle;
	private double births;

	public SinConsumerEvent(int start, int initialPopulation, int birthsPerCycle, int cycle) {
		super(start, 1, birthsPerCycle);
		this.start = start;
		this.cycle = cycle;
		this.births = initialPopulation;
		assert FLATNESS >= 1.0;
	}
	
	@Override
	public void execute(int today, ICountry sim) {
		int day = today - start;
		assert day >= 0;
		double period = (day % cycle) * 2 * Math.PI / cycle;
		this.births += (Math.sin(period) + FLATNESS) * getCardinality() / cycle / FLATNESS;
		while (births >= 1.0){
			births -= 1.0;
			addConsumer(sim);
		}
	}
	
	protected abstract void addConsumer(ICountry sim);
	
}
