package com.agentecon.exercise1.discussion;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

public class AnalyticHermit extends Consumer implements IFounder {
	
	private IProductionFunction prodFun;

	public AnalyticHermit(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
	}
	
	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		if (this.prodFun == null) {
			this.prodFun = research.createProductionFunction(HermitConfiguration.POTATOE);
		}
		return null;
	}
	
	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		produce(getInventory());
	}
	
	private void produce(Inventory inventory) {
		IStock currentManhours = inventory.getStock(HermitConfiguration.MAN_HOUR);
		double plannedLeisureTime = currentManhours.getAmount() - calculateWorkAmount(currentManhours);

		// The hide function creates allows to hide parts of the inventory from the
		// production function, preserving it for later consumption.
		Inventory productionInventory = inventory.hide(HermitConfiguration.MAN_HOUR, plannedLeisureTime);
		prodFun.produce(productionInventory);
	}

	protected double calculateWorkAmount(IStock currentManhours) {
		double weight = prodFun.getWeight(currentManhours.getGood()).weight;
		double fixedCost = prodFun.getFixedCost(currentManhours.getGood());
		return (currentManhours.getAmount() * weight + fixedCost) / (1 + weight);
	}

}
