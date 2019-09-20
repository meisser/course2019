package com.agentecon.metric.series;

import java.nio.ByteBuffer;

public class Point implements Comparable<Point> {

	public static final int LENGTH = 8;
	
	public int x;
	public float y;
	
	public Point(int x){
		this(x, 0.0f);
	}

	public Point(int x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean isBetween(Point newpoint, Point prevprev, int agg) {
		if (areClose(y, newpoint.y, prevprev.y, agg)) {
			return true;
		} else if (isOnLineBetween(prevprev, newpoint, agg)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isOnLineBetween(Point prevprev, Point newpoint, int agg) {
		double diff = newpoint.y - prevprev.y;
		int distance = newpoint.x - prevprev.x;
		double slope = diff / distance;
		double myInterpolatedPos = (x - prevprev.x) * slope + prevprev.y;
		double relDeviation = (y - myInterpolatedPos) / y;
		return relDeviation * relDeviation < getEpsilon() * agg * agg * getEpsilon();
	}

	protected boolean areClose(float ref, float before, float after, int agg) {
		if (ref > 1) {
			float factor = 1 / ref;
			ref *= factor;
			before *= factor;
			after *= factor;
		}
		return areClose(ref, before, agg) && areClose(ref, after, agg) && areClose(after, before, agg);
	}

	protected boolean areClose(float after, float before, int agg) {
		return Math.abs(before - after) <= getEpsilon() * agg;
	}

	protected float getEpsilon() {
		return 0.001f;
	}

	public float[] getData() {
		return new float[] { x, y };
	}

	public void writeTo(ByteBuffer buffer) {
		buffer.putInt(x);
		buffer.putFloat(y);
	}

	public static float[] read(ByteBuffer buffer) {
		return new float[] { buffer.getInt(), buffer.getFloat() };
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int compareTo(Point o) {
		return Integer.compare(x, o.x);
	}

}
