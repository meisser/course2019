// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Stock;
import com.agentecon.util.Numbers;

public class LogUtilTest {
	
	public static final Good MONEY = new Good("Taler");
	public static final Good PIZZA = new Good("Pizza", 1.0);
	public static final Good FONDUE = new Good("Fondue", 1.0);
	public static final Good SWISSTIME = new Good("Swiss man-hours", 0.0);
	public static final Good ITALTIME = new Good("Italian man-hours", 0.0);
	
	@Test
	public void testLogUtil(){
		IUtility util = new LogUtilWithAdjustment(new Weight(PIZZA, 8.0), new Weight(FONDUE, 2.0), new Weight(SWISSTIME, 14.0));
		assert util.getUtility(Collections.<IStock>emptyList()) == 0.0;
		Stock s1 = new Stock(SWISSTIME, 10);
		Stock s2 = new Stock(PIZZA, 10);
		assert util.getUtility(Arrays.<IStock>asList(s1, s2)) > 0.0;
		assert util.getUtility(Arrays.<IStock>asList(s1, s2)) == util.getUtility(Arrays.<IStock>asList(s1)) + util.getUtility(Arrays.<IStock>asList(s2));
	}
	
	@Test
	public void testLogUtil2(){
		IUtility util = new LogUtilWithAdjustment(new Weight(PIZZA, 8.0), new Weight(SWISSTIME, 14.0));
		assert util.getUtility(Collections.<IStock>emptyList()) == 0.0;
		Stock s1 = new Stock(SWISSTIME, 24.0 - 2.27272727273);
		Stock s2 = new Stock(PIZZA, 1.99997508071);
		assert Numbers.equals(util.getUtility(Arrays.<IStock>asList(s1, s2)), 52.51875088854);
	}
	
	@Test
	public void testEquilibrium(){
		IUtility utilFun = new LogUtilWithAdjustment(new Weight(FONDUE, 10), new Weight(SWISSTIME, 14));
		Stock s1 = new Stock(SWISSTIME, 24 - 3.03683);
		Stock s2 = new Stock(FONDUE, 3.5458);
		double utility = utilFun.getUtility(Arrays.<IStock>asList(s1, s2));
		assert Numbers.equals(utility, 58.39317473172329);
		
		Endowment end = ConsumerTest.createEndowment();
		Inventory inv = end.getInitialInventory();
		inv.receive(end.getDaily());
		double[] alloc = utilFun.getOptimalAllocation(inv, Arrays.asList(ConsumerTest.createAsk(), ConsumerTest.createBid()));
		assert Numbers.equals(alloc[0], 3.61564375); 
		assert Numbers.equals(alloc[1], 20.7362388); 
	}
}
