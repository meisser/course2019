package com.agentecon.events;

public abstract class EvolvingEvent extends SimEvent {

	public EvolvingEvent(int step, int card) {
		super(step, card);
	}
	
	public abstract EvolvingEvent createNextGeneration();

	public abstract double getScore();

}
