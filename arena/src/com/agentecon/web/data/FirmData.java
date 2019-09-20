/**
 * Created by Luzius Meisser on Jun 13, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import com.agentecon.firm.IFirm;
import com.agentecon.firm.IRegister;
import com.agentecon.goods.Good;
import com.agentecon.production.IProducer;

public class FirmData extends AgentData {

	public String ticker;
	public double dividend;
	public int shareholders;
	public String[] inputs;
	public String output;
	
	public FirmData(IFirm agent) {
		super(agent);
		IRegister shares = agent.getShareRegister();
		this.dividend = shares.getAverageDividend();
		this.ticker = agent.getTicker().getName();
		this.shareholders = shares.getShareholderCount();
		if (agent instanceof IProducer){
			IProducer prod = (IProducer)agent;
			Good[] goods = prod.getInputs();
			this.inputs = new String[goods.length];
			for (int i=0; i<goods.length; i++){
				this.inputs[i] = goods[i].getName();
			}
			this.output = prod.getOutput().getName();
		}
	}

}
