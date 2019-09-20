package com.agentecon.sim;

import com.agentecon.goods.Good;
import com.agentecon.market.IMarketStatistics;

public interface IOptimalityIndicator {
	
	public Good getOutputGood();
	
	public double getOptimum(IMarketStatistics stats);

}
