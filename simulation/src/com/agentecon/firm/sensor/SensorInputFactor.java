// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm.sensor;

import com.agentecon.agent.IAgent;

import com.agentecon.firm.InputFactor;
import com.agentecon.goods.IStock;
import com.agentecon.learning.IBelief;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;

/**
 * Implements sensor pricing for an input factor as described "An Agent-Based Simulation of the Stolper-Samuelson Effect", Journal of Computational Economics
 * 
 * See also the illustration in SensorIllusrtation.pdf as well as my blog post: http://meissereconomics.com/2016/08/09/StolperSamuelson.html#main
 */
public class SensorInputFactor extends InputFactor {

	private Bid prevRealBid;
	private SensorAccuracy acc;

	public SensorInputFactor(IStock stock, IBelief price) {
		this(stock, price, new SensorAccuracy());
	}

	public SensorInputFactor(IStock stock, IBelief price, double accuracy) {
		this(stock, price, new SensorAccuracy(accuracy));
	}

	public SensorInputFactor(IStock stock, IBelief price, SensorAccuracy accuracy) {
		super(stock, price);
		this.acc = accuracy;
	}

	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealBid == null ? 0.0 : prevRealBid.getTransactionVolume());
	}

	@Override
	public double getQuantity() {
		return super.getQuantity() + (prevRealBid == null ? 0.0 : prevRealBid.getTransactionVolume() / prevRealBid.getPrice().getPrice());
	}

	@Override
	public void createOffers(IPriceMakerMarket market, IAgent owner, IStock money, double moneySpentOnBid) {
		double sensorSize = acc.getOfferSize();
		double sensorAmount = sensorSize * moneySpentOnBid;
		super.createOffers(market, owner, money, sensorAmount);
		double left = moneySpentOnBid - sensorAmount;
		double safePrice = getSafePrice();
		double planned = left / safePrice;
		if (planned > 0) {
			prevRealBid = new Bid(owner, money, getStock(), new Price(getGood(), safePrice), planned);
			market.offer(prevRealBid);
		} else {
			prevRealBid = null;
		}
	}

	@Override
	public void adaptPrice() {
		super.adaptPrice();
		if (prevRealBid != null) {
			if (prevRealBid.isUsed()) {
				acc.moreAccurate();
			} else {
				acc.lessAccurate();
			}
			prevRealBid = null;
		}
	}

	private double getSafePrice() {
		return super.getPrice() * (1 + acc.getAccuracy());
	}

	public InputFactor duplicate(IStock stock) {
		return new SensorInputFactor(stock, price, acc);
	}

	@Override
	public double getPrice() {
		double sensor = super.getPrice();
		double most = getSafePrice();
		double accuracy = acc.getOfferSize();
		return accuracy * sensor + (1 - accuracy) * most;
	}

}
