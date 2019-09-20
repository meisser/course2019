package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collections;

public class Line {

	protected ArrayList<Point> points = new ArrayList<>();

	public Line() {
	}

	public Line(Line line) {
		this.points.addAll(line.getPoints());
	}

	public synchronized void add(Point newpoint) {
		add(newpoint, 1);
	}

	public synchronized void add(Point newpoint, int agg) {
		points.add(newpoint);
	}

	public synchronized float[][] getData() {
		float[][] arr = new float[points.size()][];
		for (int i = 0; i < arr.length; i++) {
			Point p = points.get(i);
			arr[i] = p.getData();
		}
		return arr;
	}

	public synchronized Point getFirst() {
		return points.get(0);
	}

	public synchronized Point getLast() {
		return points.get(points.size() - 1);
	}

	public synchronized ArrayList<Point> getPoints() {
		return new ArrayList<>(points);
	}

	public synchronized float getLatest() {
		return points.isEmpty() ? 0.0f : points.get(points.size() - 1).y;
	}

	public synchronized boolean has(int pos) {
		return Collections.binarySearch(points, new Point(pos)) >= 0;
	}

	public synchronized float get(int pos) {
		int index = Collections.binarySearch(points, new Point(pos));
		if (index >= 0) {
			return points.get(index).y;
		} else if (index == -1) {
			return 0.0f; // start with 0
		} else {
			index = -index - 1;
			if (index == points.size()) {
				return 0.0f;
			} else {
				return points.get(index - 1).y; // previous value
			}
		}
	}

	public synchronized int getStart() {
		return getFirst().x;
	}

	public synchronized int getEnd() {
		if (points.size() == 0) {
			return -1;
		} else {
			return points.get(points.size() - 1).x;
		}
	}

	@Override
	public String toString() {
		return points.toString();
	}

}