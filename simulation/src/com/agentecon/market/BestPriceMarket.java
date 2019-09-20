// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import java.util.Collection;
import java.util.PriorityQueue;

import com.agentecon.goods.Good;

public class BestPriceMarket extends AbstractMarket {

	private static final boolean REQUEUE_TO_END = false;

	private PriorityQueue<Bid> bids;
	private PriorityQueue<Ask> asks;

	public BestPriceMarket(Good good) {
		super(good);
		this.bids = new PriorityQueue<Bid>();
		this.asks = new PriorityQueue<Ask>();
	}

	public final boolean hasOffers(PriorityQueue<? extends AbstractOffer> bids) {
		return getBest(bids) != null;
	}

	@Override
	protected final Collection<Ask> getAsks() {
		return asks;
	}

	@Override
	protected final Collection<Bid> getBids() {
		return bids;
	}

	@Override
	public final Bid getBid() {
		return getBest(bids);
	}

	@Override
	public final Ask getAsk() {
		return getBest(asks);
	}

	private final <T extends AbstractOffer> T getBest(PriorityQueue<T> bids) {
		if (REQUEUE_TO_END) {
			T offer = bids.poll();
			while (offer != null && offer.isUsed()) {
				offer = bids.poll();
			}
			if (offer != null) {
				bids.add(offer);
			}
			return offer;
		} else {
			T offer = bids.peek();
			while (offer != null && offer.isUsed()) {
				bids.poll();
				offer = bids.peek();
			}
			return offer;
		}
	}
}
