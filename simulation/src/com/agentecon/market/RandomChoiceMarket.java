// Created on May 24, 2015 by Luzius Meisser

package com.agentecon.market;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.agentecon.goods.Good;

public class RandomChoiceMarket extends AbstractMarket {

	private Random rand;
	private ArrayList<Ask> asks;
	private ArrayList<Bid> bids;

	public RandomChoiceMarket(Random rand, Good good) {
		super(good);
		this.rand = rand;
		this.asks = new ArrayList<>();
		this.bids = new ArrayList<>();
	}

	@Override
	public Bid getBid() {
		return getBest(bids);
	}

	@Override
	public Ask getAsk() {
		return getBest(asks);
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractOffer> T getBest(ArrayList<T> offers) {
		IOffer o1 = getRandom(offers);
		if (o1 == null) {
			return null;
		} else {
			T o2 = getRandom(offers);
			T o3 = getRandom(offers);
			return (T) o1.getBetterOne(o2).getBetterOne(o3);
		}
	}

	private <T extends AbstractOffer> T getRandom(ArrayList<T> offers) {
		int size = offers.size();
		while (size > 0){
			int pos = rand.nextInt(size);
			T offer = offers.get(pos);
			if (offer.isUsed()){
				offers.remove(pos);
				size--;
			} else{
				return offer;
			}
		}
		return null;
	}

	@Override
	protected Collection<Ask> getAsks() {
		return asks;
	}

	@Override
	protected Collection<Bid> getBids() {
		return bids;
	}

}
