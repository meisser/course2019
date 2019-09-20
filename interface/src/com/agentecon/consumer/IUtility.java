// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.util.Average;


public interface IUtility {
	
	public double getUtility(Collection<IStock> quantities);
	
	public double consume(Collection<IStock> goods);

	public double[] getOptimalAllocation(Inventory inv, Collection<IOffer> offers);

	public void updateWeight(Weight weight);

	public boolean isValued(Good good);

	public Good[] getGoods();

	public double[] getWeights();
	
	public Average getStatistics();
	
	public double getLatestExperiencedUtility();

}
