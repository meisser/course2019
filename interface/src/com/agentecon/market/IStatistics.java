/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.market;

import java.util.Random;

import com.agentecon.goods.Good;
/**
 * Provides access to daily goods and stock market statistics.
 */
import com.agentecon.util.Average;

public interface IStatistics {
	
	public int getDay();
	
	public Random getRandomNumberGenerator();
	
	public Average getAverageUtility();
	
	public Good getMoney();
	
	public IMarketStatistics getGoodsMarketStats();
	
	public IMarketStatistics getStockMarketStats();

	public IDiscountRate getDiscountRate();

}
