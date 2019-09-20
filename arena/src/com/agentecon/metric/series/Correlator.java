package com.agentecon.metric.series;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.PriorityQueue;

import com.agentecon.util.Numbers;

public class Correlator {

	private int start;
	private ArrayList<TimeSeries> series;

	public Correlator(ArrayList<TimeSeries> series) {
		this(series, series.size() > 0 ? findStart(series.iterator().next()) : 0);
	}

	public Correlator(ArrayList<TimeSeries> series, int start) {
		this.series = series;
		this.start = start;
	}
	
	public Correlator buildMovingAverage(int days){
		ArrayList<TimeSeries> averages = new ArrayList<>();
		for (TimeSeries ts: series){
			averages.add(ts.buildMovingAverage(days));
		}
		return new Correlator(averages, start);
	}

	private static int findStart(TimeSeries next) {
		float[][] vals = next.getValues();
		return (int) (vals[vals.length - 1][0] / 10);
	}

	public String getCorrelationMatrix() {
		String everything = "";
		for (TimeSeries ts : series) {
			everything += "\t" + ts.getName();
		}
		everything += "\n";
		for (TimeSeries ts : series) {
			String line = ts.getName();
			for (TimeSeries ts2 : series) {
				line += "\t" + ts.correlate(ts2, start);
			}
			everything += line + "\n";
		}
		return everything;
	}

	public String getTopCorrelations(boolean positive) {
		PriorityQueue<Correlation> queue = new PriorityQueue<>();
		for (int i = 0; i < series.size(); i++) {
			for (int j = i + 1; j < series.size(); j++) {
				Correlation cor = new Correlation(series.get(i), series.get(j), positive);
				if (Double.isNaN(cor.correlation)){
					// skip
				} else {
					queue.add(cor);
				}
			}
		}
		int count = series.size();
		String list = "Correlation List";
		while (queue.size() > 0 && count-- > 0) {
			list += "\n" + queue.poll().toString();
		}
		return list;
	}
	
	class Correlation implements Comparable<Correlation> {

		private String name;
		private int positive;
		private double correlation;

		public Correlation(TimeSeries timeSeries, TimeSeries timeSeries2, boolean positive) {
			this.name = "Corr(" + timeSeries.getName() + ", " + timeSeries2.getName() + ")";
			this.correlation = timeSeries.correlate(timeSeries2, start);
			this.positive = positive ? -1 : 1;
		}

		@Override
		public int compareTo(Correlation o) {
			return Double.compare(correlation, o.correlation) * positive;
		}

		public String toString() {
			return name + "\t" + Numbers.toString(correlation);
		}

	}

	@Override
	public String toString() {
		return "correlator from " + start + " on " + series.size() + " series";
	}

	public void printFullTable(Writer writer, int start) throws IOException {
		String labels = "Day";
		for (TimeSeries ts : series) {
			labels += "\t" + ts.getName();
		}
		int day = start;
		writer.write(labels);
		while (true) {
			boolean found = false;
			String line = Integer.toString(day);
			for (TimeSeries ts : series) {
				line += "\t";
				if (ts.has(day)) {
					line += ts.get(day);
					found = true;
				}
			}
			if (found) {
				writer.write("\n" + line);
				day++;
			} else {
				break;
			}
		}
	}

}
