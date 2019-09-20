package com.agentecon.configuration;

import com.agentecon.consumer.Weight;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.Good;
import com.agentecon.production.IProductionFunction;

public class ProductionWeights {

	public static final int INPUTS = 3;
	public static final double[] WEIGHTS = new double[]{6.0, 2.0, 4.0};
	
	private Good[] inputs;
	private Good[] outputs;

	public ProductionWeights(Good[] inputs, Good[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	public IProductionFunction createProdFun(int type, double retToScale){
		Weight[] prefs = new Weight[Math.min(inputs.length, INPUTS)];
		for (int i=0; i<prefs.length; i++){
			Good good = inputs[(i + type) % inputs.length];
			prefs[i] = new Weight(good, WEIGHTS[i]);
		}
		return new CobbDouglasProduction(outputs[type], prefs).scale(retToScale);
	}

}
