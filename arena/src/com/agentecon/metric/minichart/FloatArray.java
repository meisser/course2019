package com.agentecon.metric.minichart;

public class FloatArray {

	private static final float NO_VALUE = Float.NaN;
	
	private int start;
	private int increment;
	private float[] data;

	public FloatArray(int minLen) {
		this.start = -1;
		this.increment = minLen;
		this.data = new float[minLen];
		for (int i = 0; i < data.length; i++) {
			this.data[i] = NO_VALUE;
		}
	}
	
	public boolean has(int current) {
		int index = current - start;
		if (index < 0){
			return false;
		} else if (index >= data.length){
			return false;
		} else {
			return !Float.isNaN(data[index]);
		}
	}
	
	public float get(int pos) {
		return data[pos - start];
	}

	public void set(int pos, float value) {
		if (!Float.isFinite(value)){
			value = 0.0f; // Gson does not support NaN
		}
		if (start == -1) {
			start = pos;
		}
		int index = pos - start;
		if (data.length <= index) {
			float[] newArray = new float[index + increment];
			System.arraycopy(this.data, 0, newArray, 0, this.data.length);
			for (int i=this.data.length; i<newArray.length; i++){
				newArray[i] = NO_VALUE;
			}
			this.data = newArray;
		} else if (index < 0) {
			int newStart = pos;
			int newEnd = start + data.length;
			float[] newArray = new float[newEnd - newStart];
			int emptyPart = newArray.length - data.length;
			for (int i=0; i<emptyPart; i++){
				newArray[i] = NO_VALUE;
			}
			System.arraycopy(this.data, 0, newArray, emptyPart, this.data.length);
			this.start = newStart;
			this.data = newArray;
			index = pos - start;
		}
		data[index] = value;
	}
	
}
