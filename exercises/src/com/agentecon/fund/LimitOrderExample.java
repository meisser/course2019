package com.agentecon.fund;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.AskFin;
import com.agentecon.finance.BidFin;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.market.IPriceMakerMarket;

// Let your LeveragedInvestmentFund implement IMarketMaker and copy the methods below
// into your LeveragedInvestmentFund class
public class LimitOrderExample extends LeveragedInvestmentFund implements IMarketMaker {
	
	public LimitOrderExample(IAgentIdGenerator world, Endowment end) {
		super(world, end);
	}
	
	// this method is call before trading starts to post limit orders to the market
	// this is also when the market makers post their offers
	@Override
	public void postOffers(IPriceMakerMarket market) {
		// Example code to show how to post limit offers
		
		Portfolio port = getPortfolio();
		// go through all positions
		for (Position p: port.getPositions()) {
			// Bid 150 for 1.5 shares
			BidFin bid = new BidFin(this, port.getWallet(), p, 100, 1.5);
			market.offer(bid);
		}
		
		// go through all positions
		for (Position p: port.getPositions()) {
			// offer 5% of this position for 200$ per share
			AskFin ask = new AskFin(this, port.getWallet(), p, 200, p.getAmount() * 0.05);
			market.offer(ask);
		}
	}

	// this method must be present in order to make sure that the portfolio spans the whole investable universe
	@Override
	public void notifyFirmCreated(IFirm firm) {
		// make sure that there is a position for every firm in our portfolio, even if it is empty
		// note that this does not include this firm itself
		getPortfolio().addPosition(firm.getShareRegister().createPosition(false));
	}

}
