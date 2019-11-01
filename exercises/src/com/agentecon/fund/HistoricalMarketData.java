package com.agentecon.fund;

import java.util.HashMap;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.util.MovingAverage;

/**
 * A stock picking strategy that uses historical data
 */
public class HistoricalMarketData {

	private HashMap<Ticker, HistoricalStockData> trackedStocks;

	public HistoricalMarketData() {
		this.trackedStocks = new HashMap<>();
	}

	public HistoricalStockData getData(Ticker t) {
		return trackedStocks.get(t);
	}

	public void update(IStockMarket market) {
		for (Ticker ticker : market.getTradedStocks()) {
			double value = market.getMarketStatistics().getLatestPrice(ticker);
			if (Double.isFinite(value)) {
				HistoricalStockData data = trackedStocks.get(ticker);
				if (data == null) {
					data = new HistoricalStockData();
					trackedStocks.put(ticker, data);
				}
				data.update(value);
			}
		}
	}

	class HistoricalStockData {

		private MovingAverage shortTermAverage;
		private MovingAverage longTermAverage;
		// feel free to add other averages or remove some

		// a time series object
		private TimeSeries series;

		public HistoricalStockData() {
			this.shortTermAverage = new MovingAverage(0.80);
			this.longTermAverage = new MovingAverage(0.98);

			// a time series that goes back 50 days
			this.series = new TimeSeries(50);
		}

		public void update(double value) {
			this.shortTermAverage.add(value);
			this.longTermAverage.add(value);
			this.series.add(value);
		}

		public double getShortTermAverage() {
			return shortTermAverage.getAverage();
		}

		public double getShortTermVariance() {
			return shortTermAverage.getVariance();
		}

		public double getLongTermAverage() {
			return longTermAverage.getAverage();
		}

		public double getLongTermVariance() {
			return longTermAverage.getVariance();
		}

		public TimeSeries getSeries() {
			return series;
		}

	}

}
