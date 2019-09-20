/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.graph;

import java.util.function.Consumer;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IStatistics;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.util.Numbers;
import com.agentecon.web.query.AgentQuery;

public class ConsumerMeter extends AgentSize implements IConsumerListener {
	
	private Average utility;
	
	public ConsumerMeter(AgentQuery query, ISimulation sim){
		this.utility = new Average();
		query.forEach(sim.getAgents(), new Consumer<IAgent>() {
			
			@Override
			public void accept(IAgent t) {
				t.addListener(ConsumerMeter.this);
			}
		});
		sim.addListener(new SimulationListenerAdapter(){
			@Override
			public void notifyConsumerCreated(IConsumer consumer){
				consumer.addListener(ConsumerMeter.this);
			}
		});
	}

	@Override
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
		this.utility.add(utility);
	}

	@Override
	public void notifyRetiring(IConsumer inst, int age) {
	}

	@Override
	public void notifyInvested(IConsumer inst, double amount) {
	}

	@Override
	public void notifyDivested(IConsumer inst, double amount) {
	}

	@Override
	public double getSize(IStatistics stats, AgentQuery query, IAgents agents) {
		return Numbers.normalize(utility.getAverage());
	}

}
