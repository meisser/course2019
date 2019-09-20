package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.market.Bid;
import com.agentecon.market.Price;

public class BidFin extends Bid {
	
	public BidFin(IAgent owner, IStock wallet, Position stock, double price, double amount) {
		super(owner, wallet, stock, new Price(stock.getTicker(), price), amount);
	}

	public BidFin(IAgent owner, IStock wallet, Position stock, Price price, double amount) {
		super(owner, wallet, stock, price, amount);
	}
	
	protected Position getStock(){
		return (Position)stock;
	}
	
	public double accept(IAgent acceptor, IStock seller, Position target, double shares) {
		return super.accept(acceptor, seller, target, new Quantity(target.getGood(), Math.min(shares, target.getAmount())));
	}

	public Ticker getTicker() {
		return (Ticker) getGood();
	}

}
