package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collection;

public class AveragingTimeSeries {

	private TimeSeries series;
	private double tot, weight;

	public AveragingTimeSeries(String key, int max) {
		this.series = new TimeSeries(key, max);
	}

	public AveragingTimeSeries(String key, Line line, int max) {
		this.series = new TimeSeries(key, line, max);
	}
	
	public void add(double weight, double value) {
		this.tot += value;
		this.weight += 1.0;
	}

	public void add(double delta) {
		add(1.0, delta);
	}

	public void pushSum(int day) {
		if (weight > 0) {
			this.series.set(day, tot);
			reset();
		}
	}
	
	public void pushZeroIfNothing() {
		if (weight == 0.0 && series.isInteresting()) {
			add(0.0);
		}
	}

	public double push(int day) {
		if (weight > 0) {
			double value = tot / weight;
			this.series.set(day, value);
			reset();
			return value;
		} else {
			return 0.0;
		}
	}

	protected void reset() {
		this.weight = 0.0;
		this.tot = 0.0;
	}

	public TimeSeries getTimeSeries() {
		return series;
	}

	public String toString() {
		return series.toString();
	}

	public double getCurrent() {
		return weight == 0.0 ? 0.0 : tot / weight;
	}

	public static ArrayList<TimeSeries> unwrap(Collection<AveragingTimeSeries> values) {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (AveragingTimeSeries ats : values) {
			list.add(ats.getTimeSeries());
		}
		return list;
	}

}
