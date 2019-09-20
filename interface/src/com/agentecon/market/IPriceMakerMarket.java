// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

public interface IPriceMakerMarket extends IMarket {
	
	public void offer(Bid offer);
	
	public void offer(Ask offer);
	
}
