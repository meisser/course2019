package com.agentecon.web.data;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.util.Numbers;

public class AgentData extends JsonData {

	public String agentName;
	public String[][] inventory;
	
	public SourceData source;

	public AgentData(IAgent agent) {
		this.source = new SourceData(agent);
		
		this.inventory = toStringArray(agent.getInventory().getAll());
		this.agentName = agent.getName();
	}

	public static String[][] toStringArray(Collection<IStock> inv) {
		String[][] inventory = new String[inv.size()][2];
		int i=0;
		for (IStock stock: inv){
			inventory[i++] = new String[]{stock.getGood().toString(), Numbers.toString(stock.getAmount())}; 
		}
		return inventory;
	}

}
