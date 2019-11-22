package com.agentecon.metric.series;

public class AggregatingTimeSeries extends AveragingTimeSeries {

	public AggregatingTimeSeries(String key, int max) {
		super(key, max);
	}

	@Override
	protected void reset() {
		// don't reset
	}
}
