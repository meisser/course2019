package com.agentecon.configuration;

import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.goods.Good;

public class ConsumptionWeights {

	public static final double TIME_WEIGHT = 14.0;
	public static final double[] WEIGHTS = new double[] { 3.0, 1.0, 6.0 };
	private static final int MAX_CONSUMPTION_GOODS = 3;

	private Weight[] inputs;
	private Weight[] outputs;

	public ConsumptionWeights(Good[] inputs, Good[] outputs) {
		this(inputs, outputs, WEIGHTS);
	}

	public ConsumptionWeights(Good[] inputs, Good[] outputs, double... weights) {
		this.inputs = new Weight[inputs.length];
		this.outputs = new Weight[outputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputs[i] = new Weight(inputs[i], TIME_WEIGHT);
		}
		for (int i = 0; i < outputs.length; i++) {
			this.outputs[i] = new Weight(outputs[i], weights[i % weights.length]);
		}
	}

	public LogUtilWithFloor createUtilFun(int type) {
		int count = Math.min(MAX_CONSUMPTION_GOODS, outputs.length);
		Weight[] prefs = new Weight[count + 1];
		for (int i = 0; i < count; i++) {
			prefs[i] = outputs[(i + type) % outputs.length];
		}
		prefs[prefs.length - 1] = inputs[type];
		return new LogUtilWithFloor(prefs);
	}

	public LogUtilWithFloor createDeviation(LogUtilWithFloor basis, Good changedGood, double newWeight) {
		if (basis.isValued(changedGood)) {
			Good[] goods = basis.getGoods();
			double[] weights = basis.getWeights();
			Weight[] newWeights = new Weight[goods.length];
			for (int i = 0; i < goods.length; i++) {
				double w = goods[i] == changedGood ? newWeight : weights[i];
				newWeights[i] = new Weight(goods[i], w);
			}
			return new LogUtilWithFloor(newWeights);
		} else {
			return basis;
		}
	}

	public IUtilityFactory getFactory(final int type) {
		return new IUtilityFactory() {

			@Override
			public IUtility create(int number) {
				return createUtilFun(type);
			}
		};
	}

}
