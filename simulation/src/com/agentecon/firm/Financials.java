package com.agentecon.firm;

import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.goods.IStock;

public abstract class Financials implements IFinancials {
	
	private IStock money;
	private OutputFactor output;
	private InputFactor[] inputs;

	public Financials(IStock money, OutputFactor output, InputFactor... inputs) {
		this.money = money;
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public double getCash() {
		return money.getAmount();
	}

	@Override
	public double getLatestCogs() {
		double cogs = 0.0;
		for (InputFactor input: inputs){
			cogs += input.getVolume();
		}
		return cogs;
	}

	@Override
	public double getLatestRevenue() {
		return output.getVolume();
	}

	@Override
	public double getExpectedRevenue() {
		return output.getPrice() * output.getStock().getAmount();
	}
	
	@Override
	public double getProfits() {
		return getExpectedRevenue() - getLatestCogs();
	}
	
	@Override
	public String toString(){
		return "spent " + getLatestCogs() + " to generate expected revenues of " + getExpectedRevenue() + " at cash level " + getCash();
	}

}
