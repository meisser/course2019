// Created by Luzius on May 15, 2015

package com.agentecon.sim;

import java.util.Random;

public abstract class Event implements Comparable<Event> {

	private int nextExecution;
	
	private int interval;
	protected int cardinality; // -1 = all

	public Event(int day) {
		this(day, -1);
	}

	public Event(int day, int cardinality) {
		this(day, 0, cardinality);
	}

	public Event(int day, int interval, int cardinality) {
		this.nextExecution = day;
		this.interval = interval;
		this.cardinality = cardinality;
	}
	
	public void init(Random rand) {
	}

	public int getCardinality() {
		return cardinality;
	}

	public String getCardinalityString() {
		if (cardinality == -1) {
			return "All";
		} else {
			return Integer.toString(cardinality);
		}
	}
	
	public boolean reschedule(){
		if (interval == 0){
			return false;
		} else {
			nextExecution += interval;
			return true;
		}
	}
	
	public int getDay() {
		return nextExecution;
	}
	
	public int getInterval(){
		return interval;
	}

	public String getType() {
		return getClass().getSimpleName();
	}

	public final int compareTo(Event o) {
		return Integer.compare(nextExecution, o.nextExecution);
	}

}
