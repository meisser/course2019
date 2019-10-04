/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise1.discussion;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.learning.IControl;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * A variant of the Hermit that adjusts the work amount dynamically with a primitive
 * variant of steepest descent.
 */
public class AdaptiveHermit extends Consumer implements IFounder {

	private IProductionFunction prodFun;
	private IControl control;

	public AdaptiveHermit(IAgentIdGenerator id, Endowment end, IUtility utility, IControl control) {
		super(id, end, utility);
		this.control = control;
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
		// Hermit does not trade, produces instead for himself
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
		return control.getCurrentInput();
	}

	@Override
	public double consume() {
		double utility = super.consume();
		this.control.reportOutput(utility);
		return utility;
	}

}