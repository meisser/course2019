package com.agentecon.exercise3;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.BasicEconomyConfiguration;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.util.MovingAverage;

public class DiscountingConsumer extends MortalConsumer {

	public static final double DISCOUNT_RATE = 1.0 / BasicEconomyConfiguration.LIFE_EXPECTANCY;

	private double capitalBuffer = 0.92;
	private MovingAverage interestRateEstimate;
	private MovingAverage dailySpendings;

	/**
	 * Constructs a new consumer. Note that internally, it is known in advance how long the consumer will live, namely
	 * 'maxAge' days. However, you are not allowed to make use of that variable. Your consumer must act as if 'maxAge' was
	 * unknown. The consumer can only consider the daily probability of death, but not maxAge.
	 */
	public DiscountingConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.dailySpendings = new MovingAverage(0.95);
		this.interestRateEstimate = new MovingAverage(0.3); // you might want to change this
	}

	@Override
	public void receiveInterest(IStock wallet, double amount) {
		if (getMoney().hasSome()) {
			double interestRate = amount / getMoney().getAmount();
			interestRateEstimate.add(interestRate);
		}
		super.receiveInterest(wallet, amount);
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		// We hide a relative amount of what is left as a buffer
		this.capitalBuffer = Math.max(0.5, Math.min(calculateBuffer(), 0.99));
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), getCapitalBuffer());
		super.trade(reducedInv, market);
	}

	public double getCapitalBuffer() {
		return capitalBuffer;
	}

	private double calculateBuffer() {
		// take into account the DISCOUNT_RATE as well as the interestRateEstimate to adjust the buffer
		double oldCapitalBuffer = capitalBuffer;

		// do something adaptive here
		// (you are not allowed to use the labour share of the production function here)
		boolean incrase = getAge() % 2 == 1; // just a stupid example idea: increase on odd days, decrease otherwise
		if (incrase) {
			return oldCapitalBuffer + 0.003;
		} else {
			return oldCapitalBuffer - 0.003;
		}
	}

	@Override
	protected void notifySpent(double spendings) {
		dailySpendings.add(spendings);
	}

	public double getDailySpendings() {
		return dailySpendings.getAverage();
	}

}
