package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class PersistentProductionFunction implements IProductionFunction {

	private double previousOutput;
	private Quantity[] previousInputs;
	private IProductionFunction function;

	public PersistentProductionFunction(IProductionFunction function) {
		this.function = function;
		this.previousOutput = 0.0;
		Good[] inputs = function.getInputs();
		this.previousInputs = new Quantity[inputs.length];
		for (int i = 0; i < previousInputs.length; i++) {
			this.previousInputs[i] = new Quantity(inputs[i], 0);
		}
	}

	@Override
	public Good[] getInputs() {
		return function.getInputs();
	}

	@Override
	public Weight getWeight(Good good) {
		return function.getWeight(good);
	}

	@Override
	public double getFixedCost(Good good) {
		return function.getFixedCost(good) - getPreviousInput(good).getAmount();
	}

	private Quantity getPreviousInput(Good good) {
		for (Quantity prev : previousInputs) {
			if (prev.getGood().equals(good)) {
				return prev;
			}
		}
		return null;
	}

	@Override
	public double getFixedCosts(IPriceProvider prices) throws PriceUnknownException {
		return 0.0;
	}

	@Override
	public Good getOutput() {
		return function.getOutput();
	}

	@Override
	public Quantity produce(Inventory inventory) {
		Quantity[] total = new Quantity[previousInputs.length];
		for (int i=0; i<total.length; i++) {
			Good good = previousInputs[i].getGood();
			total[i] = new Quantity(good, previousInputs[i].getAmount() + inventory.getStock(good).consume());
		}
		this.previousInputs = total;
		Quantity output = function.calculateOutput(total);
		Quantity actualOutput = new Quantity(output.getGood(), output.getAmount() - previousOutput);
		this.previousOutput = output.getAmount();
		inventory.getStock(actualOutput.getGood()).add(actualOutput.getAmount());;
		return actualOutput;
	}

	@Override
	public Quantity calculateOutput(Quantity... inputs) {
		Quantity[] quants = new Quantity[inputs.length];
		for (int i=0; i<quants.length; i++) {
			Good good = inputs[i].getGood();
			quants[i] = new Quantity(good, inputs[i].getAmount() + getPreviousInput(good).getAmount());
		}
		double output = function.calculateOutput(quants).getAmount();
		return new Quantity(getOutput(), output - previousOutput);
	}

	@Override
	public double getCostOfMaximumProfit(Inventory inv, IPriceProvider prices) throws PriceUnknownException {
		throw new RuntimeException("Not implemented for persistent production");
	}

	@Override
	public double getExpenses(Good good, IPriceProvider prices, double totalSpendings) throws PriceUnknownException {
		throw new RuntimeException("Not implemented for persistent production");
	}

}
