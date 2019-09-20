package com.agentecon.events;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.OldProducer;
import com.agentecon.firm.production.LogProdFun;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.util.Average;
import com.agentecon.world.ICountry;

public class EvolvingFirmEvent extends EvolvingEvent {

	private IAgentIdGenerator id;
	private Endowment end;
	private FirstDayProduction prod;
	private LogProdFun prodFun;
	private ArrayList<OldProducer> firms;

	public EvolvingFirmEvent(IAgentIdGenerator id, int firmsPerType, String type, Endowment end, LogProdFun fun, Random rand) {
		super(0, firmsPerType);
		this.end = end;
		this.prodFun = fun;
		this.firms = new ArrayList<>();
		for (int i = 0; i < getCardinality(); i++) {
			firms.add(new OldProducer(id, end, fun));
		}
		this.id = id;
		initListener();
	}

	private void initListener() {
		this.prod = new FirstDayProduction(firms.size());
		for (OldProducer firm : firms) {
			firm.addProducerMonitor(prod);
		}
	}

	private EvolvingFirmEvent(int cardinality, Endowment end, LogProdFun prodFun, ArrayList<OldProducer> firms) {
		super(0, cardinality);
		this.end = end;
		this.prodFun = prodFun;
		this.firms = firms;
		initListener();

	}

	@Override
	public EvolvingEvent createNextGeneration() {
		ArrayList<OldProducer> newFirms = new ArrayList<>();
		adaptEndowment();
		for (OldProducer firm : firms) {
			newFirms.add(firm.createNextGeneration(id, end, prodFun));
		}
		return new EvolvingFirmEvent(getCardinality(), end, prodFun, newFirms);
	}

	private void adaptEndowment() {
		Inventory inv = end.getInitialInventory();
		IStock stock = inv.getStock(prod.getGood());
		double diff = prod.getAmount() - stock.getAmount();
		if (diff > 0) {
			stock.add(diff);
		} else {
			stock.remove(-diff);
		}
		end = new Endowment(inv.getMoney().getGood(), inv.getAll().toArray(new IStock[] {}), end.getDaily());
	}

	@Override
	public double getScore() {
		Average avg = new Average();
		for (OldProducer firm : firms) {
			avg.add(firm.getOutputPrice());
		}
		return avg.getAverage();
	}

	@Override
	public void execute(int day, ICountry sim) {
		for (OldProducer firm : firms) {
			sim.add(firm);
		}
	}

	public String toString() {
		return "Firms with average price " + getScore();
	}

}
