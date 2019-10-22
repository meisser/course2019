package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.util.Average;

public class AverageSeries {

	private String name;
	private ArrayList<Average> data;

	public AverageSeries(String name) {
		this.name = name;
		this.data = new ArrayList<Average>();
	}

	public void add(int time, double value) {
		if (time >= data.size()) {
			assert time == data.size();
			data.add(new Average());
		}
		data.get(time).add(value);
	}

	public TimeSeries consolidate() {
		TimeSeries ts = new TimeSeries(name, data.size());
		int pos = 0;
		for (Average avg : data) {
			ts.set(pos++, avg.getAverage());
		}
		return ts;
	}

	public static Collection<TimeSeries> unwrap(Collection<AverageSeries> values) {
		ArrayList<TimeSeries> series = new ArrayList<TimeSeries>();
		for (AverageSeries avg: values) {
			series.add(avg.consolidate());
		}
		return series;
	}

}
