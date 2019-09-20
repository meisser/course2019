package com.agentecon.metric.minichart;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.agentecon.goods.Good;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.query.AgentQuery;

public abstract class MiniChart {

	public static final int LENGTH = 100;

	private FloatArray data;

	public MiniChart() {
		this.data = new FloatArray(LENGTH);
	}

	public MiniChartData getData(int day, SimulationStepper stepper, int height) throws IOException {
		int start = Math.max(1, day - LENGTH + 1);
		for (int current = start; current <= day; current++) {

			if (!data.has(current)) {
				data.set(current, getData(stepper, current));
			}
		}
		return new MiniChartData(getName(), start, Math.min(LENGTH, day), data, height);
	}

	protected abstract float getData(SimulationStepper stepper, int day) throws IOException;

	protected abstract String getName();

	class MiniChartData extends JsonData {

		public String name;
		public float min, max;
		public short[] data;

		public MiniChartData(String name, int start, int length, FloatArray data, int height) {
			this.name = name;
			this.min = Float.MAX_VALUE;
			this.max = 0.0f;
			float[] temp = new float[length];
			for (int i = 0; i < length; i++) {
				float value = data.get(start + i);
				this.min = (float) Math.min(min, Math.floor(value));
				this.max = (float) Math.max(max, Math.ceil(value));
				temp[i] = value;
			}
			this.data = new short[length];
			float range = max - min;
			for (int i = 0; i < length; i++) {
				this.data[i] = (short) ((temp[i] - min) / range * height);
				assert this.data[i] >= 0 && this.data[i] <= height;
			}
		}
		
		@Override
		public boolean equals(Object o){
			MiniChartData other = (MiniChartData)o;
			if (name.equals(other.name) && min == other.min && max == other.max){
				return Arrays.equals(data, other.data);
			} else {
				return false;
			}
		}

	}

	public static MiniChart create(List<String> set) {
		if (set.size() == 1) {
			return new NodeMiniChart(new AgentQuery(set.iterator().next()));
		} else if (set.size() == 3){
			AgentQuery source = new AgentQuery(set.get(0));
			AgentQuery dest = new AgentQuery(set.get(1));
			Good good = new Good(set.get(2));
			return new TradeMiniChart(source, dest, good);
		} else {
			throw new RuntimeException("Minichart needs either one node or a tupel like selection=source,dest,good as argument, provided was: " + set);
		}
	}

}
