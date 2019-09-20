// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Stock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IOffer;
import com.agentecon.market.Price;
import com.agentecon.util.Numbers;

public class CobbDouglasUtilTest {
	
	public static final Good MONEY = new Good("Taler");
	public static final Good PIZZA = new Good("Pizza", 1.0);
	public static final Good FONDUE = new Good("Fondue", 1.0);
	public static final Good BEER = new Good("Beer", 1.0);
	public static final Good SWISSTIME = new Good("Swiss man-hours", 0.0);
	public static final Good ITALTIME = new Good("Italian man-hours", 0.0);
	public static final Good GERTIME = new Good("German man-hours", 0.0);
	
	@Test
	public void testLogUtil(){
		CobbDouglasUtil util = new CobbDouglasUtil(new Weight(PIZZA, 8.0), new Weight(FONDUE, 2.0), new Weight(SWISSTIME, 14.0));
		assert util.getUtility(Collections.<IStock>emptyList()) == 0.0;
		Stock s1 = new Stock(SWISSTIME, 10);
		Stock s2 = new Stock(PIZZA, 10);
		assert util.getUtility(Arrays.<IStock>asList(s1, s2)) == 0.0;
		Stock s3 = new Stock(FONDUE, 10);
		assert util.getUtility(Arrays.<IStock>asList(s1, s2, s3)) == 10.0;
	}
	
	@Test
	public void testEquilibrium(){
		CobbDouglasUtil utilFun = new CobbDouglasUtil(new Weight(FONDUE, 10), new Weight(SWISSTIME, 15));
		Stock s1 = new Stock(SWISSTIME, 24 - 3.03683);
		Stock s2 = new Stock(FONDUE, 3.5458);
		double utility = utilFun.getUtility(Arrays.<IStock>asList(s1, s2));
		assert Numbers.equals(utility, 10.298165523700048);
		
		Inventory inv = new Inventory(MONEY, new Stock(SWISSTIME, 24));
		double[] alloc = utilFun.getOptimalAllocation(inv, Arrays.asList(createAsk(10), createBid(5)));
		double endow = 5*24;
		assert Numbers.equals(alloc[0], 10.0 / 25.0 * endow / 10); 
		assert Numbers.equals(alloc[1], 15.0 / 25.0 * endow / 5); 
	}
	
	public static IOffer createBid(double price) {
		return new Bid(null, new Stock(MONEY, 10000), new Stock(SWISSTIME), new Price(SWISSTIME, price), 1000);
	}

	public static IOffer createAsk(double price) {
		return new Ask(null, new Stock(MONEY), new Stock(FONDUE, 1000), new Price(FONDUE, price), 1000);
	}
}
