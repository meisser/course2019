package com.agentecon.market;

import com.agentecon.util.Average;
import com.agentecon.util.MovingAverage;
import com.agentecon.util.Numbers;

public class GoodStats {

	private Average current;
	private Average yesterday;
	private MovingAverage moving;

	public GoodStats() {
		this.current = new Average();
		this.yesterday = new Average();
		this.moving = new MovingAverage(0.95);
	}

	public MovingAverage getMovingAverage() {
		return moving;
	}

	public Average getYesterday() {
		return yesterday;
	}

	void resetCurrent() {
		this.current = new Average();
	}

	void commitCurrent() {
		if (current.hasValue()) {
			this.moving.add(current.getAverage());
		}
		this.yesterday = current;
		this.current = new Average();
	}

	void notifyTraded(double quantity, double price) {
		this.current.add(quantity, price);
	}

	@Override
	public String toString() {
		return Numbers.toShortString(yesterday.getTotWeight()) + " units traded for " + Numbers.toShortString(yesterday.getAverage()) + "$ each";
	}

	public String toTabString() {
		return Numbers.toShortString(yesterday.getAverage()) + "\t" + Numbers.toShortString(yesterday.getTotWeight());
	}

}
