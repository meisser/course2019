package com.agentecon.metric.series;

import java.util.List;

public class TimeSeriesData {

	private String name;
	private float[] xs;
	private float[] ys;

	public TimeSeriesData(String name, List<Point> points, int maxX) {
		this.name = name;
		int size = points.size();
		int last = points.get(points.size() - 1).x;
		size += Math.min(2, maxX - last);
		this.xs = new float[size];
		this.ys = new float[size];
		int pos = 0;
		for (Point p : points) {
			xs[pos] = p.x;
			ys[pos] = p.y;
			pos++;
		}
		if (pos < xs.length) {
			xs[xs.length - 1] = maxX;
			ys[xs.length - 1] = 0.0f;
			pos++;
			if (pos < xs.length) {
				xs[xs.length - 2] = last + 1;
				ys[xs.length - 2] = 0.0f;
			}
		}
	}

	public String getName() {
		return name;
	}

	public float getLastY() {
		return ys[ys.length - 1];
	}

}
