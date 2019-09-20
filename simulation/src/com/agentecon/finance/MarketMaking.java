/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.learning.AdjustableBelief;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;

/**
 * A class to do market making for a single stock.
 */
public class MarketMaking {

	private static final double SPENDING_FRACTION = 0.2;

	private static final double MAX_PRICE = AdjustableBelief.MAX;
	
	private IStock position;
	private IStock wallet;

	private IBelief bidSizeInShares;
	private IBelief askSizeInMoney;

	private IOffer prevBid;
	private IOffer prevAsk;

	public MarketMaking(IStock wallet, IStock shares) {
		this.wallet = wallet;
		this.position = shares;
		this.bidSizeInShares = new ExpSearchBelief();
		this.askSizeInMoney = new ExpSearchBelief();
	}
	
	public void trade(IPriceMakerMarket market, IAgent owner) {
		if (prevAsk != null) {
			adjustAskSize(prevAsk.isUsed());
		}
		if (prevBid != null) {
			adjustBidSize(prevBid.isUsed());
		}
		ensurePositiveSpread();
		double bid = getBidPrice(); // use the value before placing the ask, as the ask might lead to a change
		prevAsk = placeAsk(market, owner, getAskSizeInShares(), getAskPrice());
		prevBid = placeBid(market, owner, getBidSizeInShares(), bid);
	}
	
	protected double getBidSizeInShares() {
		return bidSizeInShares.getValue();
	}

	protected double getAskSizeInShares() {
		return position.getAmount() * SPENDING_FRACTION;
	}

	protected void adjustAskSize(boolean upwards) {
		askSizeInMoney.adapt(upwards);
	}

	protected void adjustBidSize(boolean upwards) {
		bidSizeInShares.adaptWithFloor(upwards, 0.01);
	}

	private void ensurePositiveSpread() {
		double bid = getBidPrice();
		double ask = getAskPrice();
		while (bid > ask) {
			increaseSpread();
			ask = getAskPrice();
			bid = getBidPrice();
		}
	}

	protected void increaseSpread() {
		adjustAskSize(true);
		adjustBidSize(true);
	}

	public double getBidPrice() {
		return wallet.getAmount() * SPENDING_FRACTION / getBidSizeInShares();
	}

	public double getAskPrice() {
		if (position.isEmpty()) {
			return MAX_PRICE;
		} else {
			return askSizeInMoney.getValue() / getAskSizeInShares();
		}
	}
	
	protected IOffer placeBid(IPriceMakerMarket dsm, IAgent owner, double sharesToBuy, double price) {
		if (sharesToBuy > 0.0) {
			Bid bid = createBid(owner, sharesToBuy, price);
			dsm.offer(bid);
			return bid;
		} else {
			return null;
		}
	}

	protected Bid createBid(IAgent owner, double sharesToBuy, double price) {
		if (position instanceof Position) {
			return new BidFin(owner, wallet, (Position) position, new Price(position.getGood(), price), sharesToBuy);
		} else {
			return new Bid(owner, wallet, position, price, sharesToBuy);
		}
	}

	protected IOffer placeAsk(IPriceMakerMarket dsm, IAgent owner, double sharesToOffer) {
		return placeAsk(dsm, owner, sharesToOffer, getAskPrice());
	}

	protected IOffer placeAsk(IPriceMakerMarket dsm, IAgent owner, double sharesToOffer, double price) {
		if (sharesToOffer > 0.0) {
			Ask ask = createAsk(owner, sharesToOffer, price);
			dsm.offer(ask);
			return ask;
		} else {
			return null;
		}
	}

	protected Ask createAsk(IAgent owner, double sharesToOffer, double price) {
		if (position instanceof Position) {
			return new AskFin(owner, wallet, (Position) position, new Price(position.getGood(), price), sharesToOffer);
		} else {
			return new Ask(owner, wallet, position, new Price(position.getGood(), price), sharesToOffer);
		}
	}

	public double getPrice() {
		return (getBidPrice() + getAskPrice()) / 2;
	}

	public double getSpread() {
		return getAskPrice() - getBidPrice();
	}
	
	public double getBoundCash() {
		return wallet.getAmount();
	}

	@Override
	public String toString() {
		return position.getGood() + " from " + getBidPrice() + " to " + getAskPrice();
	}

}
