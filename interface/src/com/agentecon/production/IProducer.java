package com.agentecon.production;

import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;

public interface IProducer extends IFirm, IGoodsTrader {
	
	public Good[] getInputs();
	
	public Good getOutput();
	
	public void addProducerMonitor(IProducerListener listener);

	public void produce();

}
