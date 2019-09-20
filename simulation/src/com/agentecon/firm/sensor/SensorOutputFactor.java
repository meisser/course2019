// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm.sensor;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.OutputFactor;
import com.agentecon.goods.IStock;
import com.agentecon.learning.AdjustableBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;

/**
 * Implements sensor pricing for an input factor as described "An Agent-Based Simulation of the Stolper-Samuelson Effect", Journal of Computational Economics
 * 
 * See also the illustration in SensorIllusrtation.pdf as well as my blog post: http://meissereconomics.com/2016/08/09/StolperSamuelson.html#main
 */
public class SensorOutputFactor extends OutputFactor {

	private Ask prevRealAsk;
	private SensorAccuracy accuracy;

	public SensorOutputFactor(IStock stock, IBelief price) {
		this(stock, price, new SensorAccuracy());
	}

	public SensorOutputFactor(IStock stock, IBelief price, double accuracy) {
		this(stock, price, new SensorAccuracy(accuracy));
	}

	public SensorOutputFactor(IStock stock, IBelief price, SensorAccuracy accuracy) {
		super(stock, price);
		this.accuracy = accuracy;
	}

	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealAsk == null ? 0.0 : prevRealAsk.getTransactionVolume());
	}

	@Override
	public void createOffers(IPriceMakerMarket market, IAgent owner, IStock money, double amount) {
		double sensorSize = accuracy.getOfferSize() * amount;
		super.createOffers(market, owner, money, sensorSize);
		if (amount > 0) {
			prevRealAsk = new Ask(owner, money, getStock(), new Price(getGood(), getSafePrice()), amount - sensorSize);
			market.offer(prevRealAsk);
		} else {
			prevRealAsk = null;
		}
	}

	@Override
	public void adaptPrice() {
		super.adaptPrice();
		if (prevRealAsk != null) {
			if (prevRealAsk.isUsed()) {
				accuracy.moreAccurate();
			} else {
				accuracy.lessAccurate();
			}
			prevRealAsk = null;
		}
	}

	private double getSafePrice() {
		return Math.max(AdjustableBelief.MIN, super.getPrice() / (1 + accuracy.getAccuracy()));
	}

	@Override
	public double getPrice() {
		double offerSize = accuracy.getOfferSize();
		double sensor = super.getPrice();
		double most = getSafePrice();
		return offerSize * sensor + (1 - offerSize) * most;
	}

	@Override
	public OutputFactor duplicate(IStock stock) {
		return new SensorOutputFactor(stock, price, accuracy);
	}

}
