package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.learning.IBelief;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Ask;
import com.agentecon.market.Price;

public class OutputFactor extends Factor {

	public OutputFactor(IStock stock, IBelief price) {
		super(stock, price);
	}

	protected AbstractOffer newOffer(IAgent owner, IStock money, double p, double amount) {
		return new Ask(owner, money, getStock(), new Price(getGood(), p), amount);
	}

	public OutputFactor duplicate(IStock stock) {
		return new OutputFactor(stock, price);
	}

}
