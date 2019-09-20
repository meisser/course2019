package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.market.Ask;
import com.agentecon.market.Price;

public class AskFin extends Ask {

	public AskFin(IAgent owner, IStock wallet, Position stock, double price, double amount) {
		super(owner, wallet, stock, new Price(stock.getTicker(), price), amount);
	}
	
	public AskFin(IAgent owner, IStock wallet, Position stock, Price price, double amount) {
		super(owner, wallet, stock, price, amount);
	}
	
	protected Position getStock(){
		return (Position)stock;
	}
	
	public Position accept(IAgent acceptor, IStock payer, Position target, double budget) {
		if (target == null){
			target = getStock().createNewPosition(acceptor instanceof IConsumer);
		}
		super.accept(acceptor, payer, target, new Quantity(target.getGood(), budget / getPrice().getPrice()));
		return target;
	}

	public Ticker getTicker() {
		return (Ticker) getGood();
	}

}
