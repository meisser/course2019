package com.agentecon.events;

import com.agentecon.world.ICountry;

public abstract class MinPopulationGrowthEvent extends SimEvent {
	
	private int minPopulation;

	public MinPopulationGrowthEvent(int start, int minPopulation) {
		super(start, 1, 1);
		this.minPopulation = minPopulation;
	}

	@Override
	public void execute(int day, ICountry sim) {
		while (sim.getAgents().getConsumers().size() < minPopulation){
			execute(sim);
		}
	}

	protected abstract void execute(ICountry sim);

}
