// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.util;

public class PrimitiveDoubleList {

	private double[] data;
	
	public PrimitiveDoubleList(){
		this(10);
	}

	public PrimitiveDoubleList(int size) {
		this.data = new double[size * 2];
	}

	public void set(int pos, double quantity) {
		if (pos >= data.length){
			double[] newData = new double[Math.max(pos, data.length) * 2];
			System.arraycopy(data, 0, newData, 0, data.length);
			this.data = newData;
		}
		data[pos] = quantity;
	}

	public double[] getData(int size) {
		double[] cut = new double[size];
		System.arraycopy(data, 0, cut, 0, Math.min(size, data.length));
		return cut;
	}
	
}
