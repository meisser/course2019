package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Factor;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.learning.IBelief;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Ask;
import com.agentecon.market.Price;

public class CeilingFactor extends Factor {

	public CeilingFactor(IStock stock, IBelief price) {
		super(stock, price);
	}

	public void adapt(double min) {
		if (prevOffer != null) {
			price.adaptWithFloor(shouldIncrease(), min);
		} else if (price.getValue() < min) {
			price.adaptWithFloor(true, min);
		}
	}

	@Override
	protected AbstractOffer newOffer(IAgent owner, IStock money, double p, double amount) {
		Price price = new Price(getGood(), p);
		if (stock instanceof Position) {
			return new AskFin(owner, money, (Position) stock, price, amount);
		} else {
			return new Ask(owner, money, stock, price, amount);
		}
	}

}
