package com.agentecon.firm.production;

import java.util.Arrays;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.production.IProductionFunction;

public abstract class AbstractProductionFunction implements IProductionFunction {

	protected final Good output;
	protected final Weight[] inputs;
	
	private double totWeight;
	private double totConsumedWeight;

	public AbstractProductionFunction(Good output, Weight... weights) {
		assert output != null;
		this.output = output;
		this.inputs = weights;
		for (Weight w: weights){
			totWeight += w.weight;
			if (!w.capital){
				totConsumedWeight += w.weight;
			}
		}
	}
	
	public Weight[] getInputWeigths() {
		return inputs;
	}
	
	@Override
	public double getFixedCost(Good good) {
		return 0;
	}
	
	@Override
	public final Quantity produce(Inventory inventory) {
		double production = useInputs(inventory);
		inventory.getStock(getOutput()).addFreshlyProduced(production);
		return new Quantity(output, production);
	}
	
	@Override
	public final Quantity calculateOutput(Quantity... inputs) {
		Inventory inv = new Inventory(inputs);
		return new Quantity(output, useInputs(inv));
	}
	
	protected abstract double useInputs(Inventory inventory);

	public double getTotalWeight() {
		return totWeight;
	}
	
	public double getTotalConsumedWeight(){
		return totConsumedWeight;
	}

	public double getTotalCapitalWeight(){
		return getTotalWeight() - getTotalConsumedWeight();
	}
	
	@Override
	public Good[] getInputs() {
		Good[] goods = new Good[inputs.length];
		for (int i=0; i<goods.length; i++){
			goods[i] = inputs[i].good;
		}
		return goods;
	}
	
	@Override
	public Good getOutput() {
		return output;
	}

	@Override
	public Weight getWeight(Good input) {
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].good.equals(input)) {
				return inputs[i];
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + " with weights " + Arrays.toString(inputs) + " and output " + output;
	}
	
}
