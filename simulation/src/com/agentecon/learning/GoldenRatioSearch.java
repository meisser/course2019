package com.agentecon.learning;

public class GoldenRatioSearch implements IControl {

	private static final double GOLDEN_RATIO = (Math.sqrt(5) - 1) / 2;

	private Point min, mid, max;
	private double current;

	public GoldenRatioSearch(double min, double max) {
		this.min = new Point(min, Double.MIN_VALUE);
		this.max = new Point(max, Double.MAX_VALUE);
		this.current = max - (max - min) * GOLDEN_RATIO;
	}
	
	@Override
	public double getCurrentInput() {
		return current;
	}

	@Override
	public void reportOutput(double result) {
		Point newPoint = new Point(current, result);
		if (mid == null) {
			this.mid = newPoint;
			this.current = min.x + (max.x - min.x) * GOLDEN_RATIO;
		} else {
			boolean currentIsLeft = newPoint.x < mid.x;
			Point midLeft = currentIsLeft ? newPoint : mid;
			Point midRight = currentIsLeft ? mid : newPoint;
			if (midLeft.y > midRight.y) {
				max = midRight;
				mid = midLeft;
				current = max.x - (max.x - min.x) * GOLDEN_RATIO;
			} else {
				min = midLeft;
				mid = midRight;
				current = min.x + (max.x - min.x) * GOLDEN_RATIO;
			}
		}
	}

	class Point {

		double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

}
