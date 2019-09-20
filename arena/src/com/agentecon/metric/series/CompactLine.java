package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CompactLine extends Line {

	int MAX_LEN = 5000;

	public CompactLine() {
	}

	public CompactLine(Line line) {
		for (Point p: line.getPoints()) {
			add(p);
		}
	}

	@Override
	public void add(Point newpoint, int agg) {
		int count = points.size();
		if (count >= 2) {
			assert newpoint.x > points.get(points.size() - 1).x;
			Point prev = points.get(count - 1);
			Point prevprev = points.get(count - 2);
			if (prev.isBetween(newpoint, prevprev, agg)) {
				points.set(count - 1, newpoint); // overwrite prev
			} else {
				points.add(newpoint);
			}
		} else {
			points.add(newpoint);
		}
	}

	@Override
	public ArrayList<Point> getPoints() {
		ArrayList<Point> points = this.points;
		int agg = 1;
		while (points.size() > MAX_LEN) {
			agg++;
			CompactLine line = new CompactLine();
			for (Point p : points) {
				line.add(p, agg);
			}
			points = line.points;
		}
		return points;
	}

	public float getValue(int x) {
		Point key = new Point(x, 0.0f);
		int pos = Collections.binarySearch(points, key, new Comparator<Point>() {

			@Override
			public int compare(Point o1, Point o2) {
				return Integer.compare(o1.x, o2.x);
			}
		});
		if (pos >= 0) {
			return points.get(pos).y;
		} else {
			pos = -(pos + 1) - 1;
			if (pos == -1) {
				return 0.0f;
			} else {
				return points.get(pos).y;
			}
		}
	}

}
