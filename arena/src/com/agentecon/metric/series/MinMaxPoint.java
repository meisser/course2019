package com.agentecon.metric.series;

import java.nio.ByteBuffer;

public class MinMaxPoint extends Point {
	
	protected float max;

	public MinMaxPoint(int x, float min, float max) {
		super(x, min);
		this.max = max;
	}
	
	@Override
	public boolean isBetween(Point newpoint, Point prevprev, int agg) {
		return super.isBetween(newpoint, prevprev, agg) && areClose(max, ((MinMaxPoint)newpoint).max, ((MinMaxPoint)prevprev).max, agg);
	}
	
	@Override
	protected float getEpsilon() {
		return super.getEpsilon() * 5;
	}
	
	@Override
	public float[] getData() {
		return new float[]{x, y, max};
	}
	
	@Override
	public void writeTo(ByteBuffer buffer) {
		super.writeTo(buffer);
		buffer.putFloat(max);
	}
	
	public static float[] read(ByteBuffer buffer){
		return new float[]{buffer.getInt(), buffer.getFloat(), buffer.getFloat()};
	}

}
