package com.agentecon.web.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.metric.EMetrics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Point;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesData;
import com.agentecon.runner.IFactory;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;

public class ChartMethod extends SimSpecificMethod {

	private static final String KEY_PREFIX = "Chart-";

	public static final String CHOICE_PARAMETER = DownloadCSVMethod.CHOICE_PARAMETER;
	public static final String ROW = "row";

	public ChartMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		String superSample = super.createExamplePath();
		int dayIndex = superSample.indexOf(Parameters.DAY);
		if (dayIndex >= 0) {
			superSample = superSample.substring(0, dayIndex);
		}
		return superSample + DownloadCSVMethod.CHOICE_PARAMETER + "=" + EMetrics.PRODUCTION.getName();
	}

	private String getCacheKey(EMetrics metric) {
		return KEY_PREFIX + metric.getName();
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException, InterruptedException {
		EMetrics metric = EMetrics.parse(params.getParam(DownloadCSVMethod.CHOICE_PARAMETER));
		if (metric == null) {
			return new ChartData();
		} else {
			SimulationStepper stepper = getSimulation(params);
			SimStats stats = (SimStats) stepper.getOrCreate(getCacheKey(metric), new IFactory<Object>() {

				@Override
				public Object create() throws IOException {
					ISimulation sim = stepper.getSimulation(0).getItem();
					SimStats stats = metric.createAndRegister(sim, params.getSelection(), false);
					Thread t = new Thread() {
						public void run() {
							try {
								sim.run();
								stats.notifySimEnded();
							} catch (RuntimeException | Error t) {
								stats.abort(t);
							}
						}
					};
					t.setName("Calculating " + stats.getName() + " for " + sim.toString());
					t.setDaemon(true);
					t.start();
					return stats;
				}
			});
			double completeness = stats.join(100);
			return new ChartData(metric.getDescription(), stats.getTimeSeries(), completeness, Arrays.asList(params.getParam(ROW).split(",")));
		}
	}

	class ChartData extends JsonData {

		private static final int MAX_OPTIONS = 16;

		private boolean complete;
		private String description;
		private TimeSeriesData[] data;
		private ArrayList<String> options;

		public ChartData() {
			this("No valid metric selected", Collections.emptyList(), 1.0, Collections.emptyList());
		}

		public ChartData(String description, Collection<TimeSeries> series, double completeness, List<String> selected) {
			this.complete = completeness >= 1.0;
			this.description = description;
			if (!complete) {
				this.description += " (" + ((int) (completeness * 100)) + "%)";
			}
			this.options = new ArrayList<>();
			for (TimeSeries ts : series) {
				if (ts.isInteresting()) {
					options.add(ts.getName());
				}
			}
			List<String> validSelection = new ArrayList<>();
			for (String sel : selected) {
				if (options.contains(sel) && !validSelection.contains(sel)) {
					validSelection.add(sel);
				}
			}
			if (validSelection.isEmpty() && options.isEmpty()) {
				data = new TimeSeriesData[] { new TimeSeriesData("No data", Arrays.asList(new Point(0, 1.0f), new Point(1000, 1.0f)), 1000) };
			} else {
				if (validSelection.isEmpty()) {
					validSelection = Arrays.asList(options.get(0));
				}
				if (options.size() > MAX_OPTIONS) {
					trimOptions(validSelection.get(validSelection.size() - 1));
				}
				ArrayList<TimeSeriesData> data = new ArrayList<>();
				// make sure that time series have same order as selection
				for (String sel : validSelection) {
					for (TimeSeries ts : series) {
						if (sel.equals(ts.getName())) {
							TimeSeriesData raw = ts.getRawData();
							data.add(raw);
							break;
						}
					}
				}
				this.data = data.toArray(new TimeSeriesData[data.size()]);
			}
		}

		protected void trimOptions(String lastSelected) {
			int start = Math.max(0, options.indexOf(lastSelected) - MAX_OPTIONS / 2);
			while (start > 0) {
				options.remove(0);
				start--;
			}
			while (options.size() > MAX_OPTIONS) {
				options.remove(options.size() - 1);
			}
		}
	}

}
