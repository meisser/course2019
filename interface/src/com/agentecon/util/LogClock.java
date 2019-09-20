package com.agentecon.util;

public class LogClock {
	
	private long nanoTime;
	
	public LogClock(){
		this.nanoTime = System.nanoTime();
	}
	
	public void time(String comment){
		long now = System.nanoTime();
		long milliPeriod = (now - nanoTime) / 1000000;
		System.out.println(milliPeriod + "ms " + comment);
		this.nanoTime = now;
	}

}
