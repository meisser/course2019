package com.agentecon.metric;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgents;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.sim.SimulationListenerAdapter;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class SimStats extends SimulationListenerAdapter {

	private boolean completed;
	private Throwable problem;
	private ISimulation sim;
	private int maxDay;

	public SimStats(ISimulation sim) {
		this.sim = sim;
		this.maxDay = sim.getConfig().getRounds() - 1;
	}

	protected IStatistics getStats() {
		return sim.getStatistics();
	}

	protected IAgents getAgents() {
		return sim.getAgents();
	}

	protected int getMaxDay() {
		return maxDay;
	}

	public double getCompleteness() {
		if (sim == null) {
			return 1.0;
		} else if (problem == null) {
			return ((double) sim.getDay()) / sim.getConfig().getRounds();
		} else {
			throw new RuntimeException(problem);
		}
	}

	public synchronized void abort(Throwable t) {
		this.problem = t;
		this.completed = true;
		this.notifyAll();
	}

	public synchronized double join(int patience) throws InterruptedException {
		if (!completed) {
			this.wait(patience);
		}
		return getCompleteness();
	}

	public synchronized void notifySimEnded() {
		this.sim = null;
		this.completed = true;
		this.notifyAll();
	}

	public int getDay() {
		return sim.getDay();
	}

	public Collection<? extends Chart> getCharts() {
		throw new NotImplementedException();
	}

	public void notifySimStarting(ISimulation sim) {
		sim.addListener(this);
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public abstract Collection<TimeSeries> getTimeSeries();

	public void print(PrintStream out) {
		try {
			print(out, "\t");
		} catch (NoInterestingTimeSeriesFoundException e) {
		}
	}

	public void print(PrintStream out, String separator) throws NoInterestingTimeSeriesFoundException {
		out.print("Day");
		Collection<TimeSeries> series = getTimeSeries();
		int start = Integer.MAX_VALUE;
		int end = 0;
		ArrayList<TimeSeries> ofInterest = new ArrayList<>();
		for (TimeSeries ts : series) {
			if (ts.isInteresting()) {
				out.print(separator + ts.getName());
				start = Math.min(start, ts.getStart());
				end = Math.max(end, ts.getEnd());
				ofInterest.add(ts);
			}
		}
		if (ofInterest.isEmpty()) {
			throw new NoInterestingTimeSeriesFoundException();
		}
		out.println();
		for (int day = start; day <= end; day++) {
			if (hasDay(ofInterest, day)) {
				out.print(day);
				for (TimeSeries ts : ofInterest) {
					out.print(separator + ts.get(day));
				}
				out.println();
			}
		}
	}

	private boolean hasDay(ArrayList<TimeSeries> ofInterest, int day) {
		for (TimeSeries ts: ofInterest) {
			if (ts.has(day)) {
				return true;
			}
		}
		return false;
	}

}
