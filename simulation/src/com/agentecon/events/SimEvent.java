// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.events;

import com.agentecon.market.IStatistics;
import com.agentecon.sim.Event;
import com.agentecon.world.ICountry;

public abstract class SimEvent extends Event {
	
	public SimEvent(int step) {
		this(step, 1);
	}
	
    public SimEvent(int step, int card) {
    	super(step, card);
	}

	public SimEvent(int step, int interval, int card) {
		super(step, interval, card);
	}
	
	public void execute(int day, ICountry sim) {
	}

	public void execute(int day, ICountry sim, IStatistics stats) {
		execute(day, sim);
	}

}
