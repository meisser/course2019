/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.finance;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.ProducerListeners;

public abstract class Producer extends Firm implements IProducer {

	private IProductionFunction production;
	private ProducerListeners listeners;

	public Producer(IAgentIdGenerator id, IShareholder owner, IProductionFunction prodFun, Good money) {
		super(id, owner, new Endowment(money));
		this.production = prodFun;
		this.listeners = new ProducerListeners();
	}
	
	public Producer(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		super(id, end);
		this.production = prodFun;
		this.listeners = new ProducerListeners();
	}
	
	protected IProductionFunction getProductionFunction() {
		return production;
	}

	@Override
	public void addProducerMonitor(IProducerListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public Good[] getInputs() {
		return production.getInputs();
	}

	@Override
	public Good getOutput() {
		return production.getOutput();
	}

	@Override
	public void produce() {
		Quantity[] inputs = getInventory().getQuantities(getInputs());
		Quantity produced = production.produce(getInventory());
		listeners.notifyProduced(this, inputs, produced);
	}

}
