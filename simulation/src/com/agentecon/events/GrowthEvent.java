package com.agentecon.events;

import java.util.Random;

import com.agentecon.world.ICountry;

public abstract class GrowthEvent extends RandomEvent {

	private Random rand = new Random(13);
	
	private double births;
	private double probPerConsumer;
	private boolean probabilistic;
	
	public GrowthEvent(int start, double probPerConsumer) {
		this(start, probPerConsumer, true);
	}

	public GrowthEvent(int start, double probPerConsumer, boolean probabilistic) {
		super(start, 1);
		this.probPerConsumer = probPerConsumer;
		this.births = 0;
		this.probabilistic = probabilistic;
	}
	
	protected int getPopulation(ICountry sim) {
		return sim.getAgents().getConsumers().size();
	}

	@Override
	public void execute(int day, ICountry sim) {
		int population = getPopulation(sim);
		if (probabilistic) {
			for (int i=0; i<population; i++) {
				double random = rand.nextDouble();
				if (random <= probPerConsumer) {
					execute(sim);
				}
			}
		} else {
			this.births += population * probPerConsumer;
			while (births >= 0.5) {
				execute(sim);
				births -= 1.0;
			}
		}
	}

	protected abstract void execute(ICountry sim);

}
