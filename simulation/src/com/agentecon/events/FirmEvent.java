package com.agentecon.events;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.finance.Firm;
import com.agentecon.firm.OldProducer;
import com.agentecon.production.IProductionFunction;
import com.agentecon.world.ICountry;

public class FirmEvent extends SimEvent {

	private String type;
	protected Endowment end;
	protected IProductionFunction prodFun;

	public FirmEvent(int card, Endowment end, IProductionFunction prodFun) {
		this(card, null, end, prodFun);
	}

	public FirmEvent(int card, String type, Endowment end, IProductionFunction prodFun) {
		super(0, card);
		this.end = end;
		this.type = type;
		this.prodFun = prodFun;
	}

	@Override
	public void execute(int day, ICountry sim) {
		for (int i = 0; i < getCardinality(); i++) {
			sim.add(createFirm(sim, end, prodFun));
		}
	}

	protected Firm createFirm(ICountry sim, Endowment end, IProductionFunction prodFun) {
		return new OldProducer(sim, end, prodFun) {

			@Override
			protected String inferType(Class<? extends Agent> clazz) {
				if (type == null) {
					return super.inferType(clazz);
				} else {
					return type;
				}
			}

		};
	}

	@Override
	public String toString() {
		return getCardinalityString() + " firms";
	}

}
