// Created by Luzius on May 15, 2015

package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.agentecon.util.Average;
import com.agentecon.util.Numbers;

public class TimeSeries implements Comparable<TimeSeries> {

	private int max;
	private String name;
	protected Line line;

	public TimeSeries(String name, int max) {
		this(name, new Line(), max);
	}

	public TimeSeries(String name, Line line, int max) {
		this.max = max;
		this.name = name;
		this.line = line;
	}

	public TimeSeries compact() {
		return new TimeSeries(name, new CompactLine(line), max);
	}

	public TimeSeries prefix(String prefix) {
		return rename(prefix + " " + name);
	}

	public TimeSeries rename(String name) {
		return new TimeSeries(name, line, max);
	}

	public boolean has(int pos) {
		return line.has(pos);
	}

	public float get(int pos) {
		return line.get(pos);
	}

	public void set(int pos, double average) {
		assert !Double.isNaN(average);
		assert !Double.isInfinite(average);
		assert pos > line.getEnd() : "Adding points in the middle is not yet supported";
		line.add(new Point(pos, (float) average));
	}

	public float getLatest() {
		return line.getLatest();
	}

	public String getName() {
		return name;
	}

	public float[][] getValues() {
		return line.getData();
	}

	public TimeSeriesData getRawData() {
		return new TimeSeriesData(name, line.getPoints(), max);
	}

	public boolean isInteresting() {
		return line.getPoints().size() > 2;
	}

	@Override
	public String toString() {
		return name + " " + Numbers.toString(line.getLatest());
	}

	public static ArrayList<TimeSeries> prefix(String string, Collection<? extends TimeSeries> unwrap) {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (TimeSeries ts : unwrap) {
			list.add(ts.prefix(string));
		}
		return list;
	}

	public static ArrayList<TimeSeries> logReturns(ArrayList<TimeSeries> in) {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (TimeSeries ts : in) {
			list.add(ts.getLogReturns());
		}
		return list;
	}

	public static Collection<? extends TimeSeries> absolute(ArrayList<? extends TimeSeries> in) {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (TimeSeries ts : in) {
			list.add(ts.absolute());
		}
		return list;
	}

	public double correlate(TimeSeries ts2, int start) {
		if (ts2 == this) {
			return 1.0;
		} else {
			ArrayList<Point> p1 = line.getPoints();
			ArrayList<Point> p2 = ts2.line.getPoints();
			Average avg1 = new Average();
			Average avg2 = new Average();
			{// averages
				int pos1 = getStart(p1, start);
				int pos2 = getStart(p2, start);
				while (pos1 < p1.size() && pos2 < p2.size()) {
					if (p1.get(pos1).x == p2.get(pos2).x) {
						avg1.add(p1.get(pos1++).y);
						avg2.add(p2.get(pos2++).y);
					} else if (p1.get(pos1).x < p2.get(pos2).x) {
						pos1++;
					} else {
						pos2++;
					}
				}
			}
			Average cov = new Average();
			{ // correlations
				int pos1 = getStart(p1, start);
				int pos2 = getStart(p2, start);
				while (pos1 < p1.size() && pos2 < p2.size()) {
					if (p1.get(pos1).x == p2.get(pos2).x) {
						cov.add((p1.get(pos1++).y - avg1.getAverage()) * (p2.get(pos2++).y - avg2.getAverage()));
					} else if (p1.get(pos1).x < p2.get(pos2).x) {
						pos1++;
					} else {
						pos2++;
					}
				}
			}
			double covariance = cov.getAverage();
			return covariance / Math.sqrt(avg1.getVariance() * avg2.getVariance());
		}
	}

	public int getEnd() {
		return line.getEnd();
	}

	public int getStart() {
		return line.getStart();
	}

	private int getStart(ArrayList<Point> p1, int start) {
		int pos = 0;
		while (pos < p1.size() && p1.get(pos).x < start) {
			pos++;
		}
		return pos;
	}

	public TimeSeries absolute() {
		TimeSeries ts = new TimeSeries("Absolute " + getName(), max);
		if (isInteresting()) {
			for (Point p : line.getPoints()) {
				ts.set(p.x, Math.abs(p.y));
			}
		}
		return ts;
	}

	public Average getAverage(int start) {
		Average avg = new Average();
		ArrayList<Point> p1 = line.getPoints();
		for (int i = start; i < p1.size(); i++) {
			avg.add(p1.get(i).y);
		}
		return avg;
	}

	public TimeSeries divideBy(TimeSeries referencePrice) {
		TimeSeries ts = new TimeSeries(getName() + "/" + referencePrice.getName(), max);
		ArrayList<Point> p1 = line.getPoints();
		ArrayList<Point> p2 = referencePrice.line.getPoints();
		int pos1 = 0, pos2 = 0;
		while (pos1 < p1.size() && pos2 < p2.size()) {
			if (p1.get(pos1).x == p2.get(pos2).x && p2.get(pos2).y != 0.0) {
				ts.set(pos1, p1.get(pos1++).y / p2.get(pos2++).y);
			} else if (p1.get(pos1).x < p2.get(pos2).x) {
				pos1++;
			} else {
				pos2++;
			}
		}
		return ts;
	}

	public TimeSeries add(TimeSeries other) {
		TimeSeries ts = new TimeSeries(getName() + " + " + other.getName(), max);
		if (isInteresting() || other.isInteresting()) {
			int start = Math.min(getStart(), other.getStart());
			int end = Math.max(getEnd(), other.getEnd());
			for (int i = start; i <= end; i++) {
				ts.set(i, get(i) + other.get(i));
			}
		}
		return ts;
	}

	public TimeSeries getReturns() {
		TimeSeries ts = new TimeSeries("Returns of " + getName(), max);
		if (isInteresting()) {
			Iterator<Point> iter = line.getPoints().iterator();
			Point prev = iter.next();
			while (iter.hasNext()) {
				Point current = iter.next();
				double currentReturn = current.y / prev.y;
				ts.set(current.x, currentReturn);
				prev = current;
			}
		}
		return ts;
	}

	public TimeSeries getLogReturns() {
		TimeSeries ts = new TimeSeries("log return of " + getName(), max);
		if (isInteresting()) {
			Iterator<Point> iter = line.getPoints().iterator();
			Point prev = iter.next();
			double logPrev = Math.log(prev.y);
			while (iter.hasNext()) {
				Point current = iter.next();
				double logCurrent = Math.log(current.y);
				double logReturn = (logCurrent - logPrev) / (current.x - prev.x);
				ts.set(current.x, logReturn);
				prev = current;
				logPrev = logCurrent;
			}
		}
		return ts;
	}

	public TimeSeries buildMovingAverage(int days) {
		int additionalDays = days - 1;
		assert additionalDays > 0;
		TimeSeries ts = new TimeSeries(getName(), max);
		if (isInteresting()) {
			int start = line.getFirst().x;
			int pos = start;
			int end = line.getLast().x + 1;
			double value = 0.0;
			for (int i = 0; i < additionalDays; i++) {
				value += line.get(pos++);
			}
			while (pos < end) {
				value += line.get(pos);
				ts.set(start++, value / days);
				value -= line.get(pos - additionalDays);
				pos++;
			}
		}
		return ts;
	}

	@Override
	public int compareTo(TimeSeries o) {
		StringTokenizer mine = new StringTokenizer(name);
		StringTokenizer other = new StringTokenizer(o.name);
		while (true) {
			if (mine.hasMoreTokens() && other.hasMoreTokens()) {
				String myNext = mine.nextToken();
				String otherNext = other.nextToken();
				int diff = findDiff(myNext, otherNext);
				if (diff != 0) {
					return diff;
				}
			} else if (mine.hasMoreTokens()) {
				return 1;
			} else if (other.hasMoreTokens()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private int findDiff(String myNext, String otherNext) {
		if (isNumber(myNext) && isNumber(otherNext)) {
			int myNum = Integer.parseInt(myNext);
			int otNum = Integer.parseInt(otherNext);
			return Integer.compare(myNum, otNum);
		} else {
			return myNext.compareTo(otherNext);
		}
	}

	private boolean isNumber(String myNext) {
		for (char ch : myNext.toCharArray()) {
			if (!Character.isDigit(ch)) {
				return false;
			}
		}
		return true;
	}

	public static List<TimeSeries> sort(Collection<TimeSeries> series) {
		ArrayList<TimeSeries> list = new ArrayList<>(series);
		Collections.sort(list);
		return list;
	}

	public static void main(String[] args) {
		TimeSeries ts = new TimeSeries("test", 1000);
		for (int i = 0; i < 100; i++) {
			ts.set(i, i);
		}
		assert ts.get(0) == 0.0f;
		assert Numbers.equals(ts.buildMovingAverage(1).get(0), 0.5f);
		assert Numbers.equals(ts.buildMovingAverage(5).get(0), (1 + 2 + 3 + 4 + 5) / 6.0f);
		assert Numbers.equals(ts.buildMovingAverage(5).get(10), (1 + 2 + 3 + 4 + 5) / 6.0f + 10);
	}

}
