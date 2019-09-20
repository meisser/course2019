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

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.market.IStatistics;
import com.agentecon.util.Average;
import com.agentecon.util.Numbers;
import com.agentecon.web.query.AgentQuery;

public class WealthMeter extends AgentSize {
	
	public WealthMeter() {
	}

	@Override
	public double getSize(final IStatistics stats, AgentQuery query, IAgents agents) {
		final Average average = new Average();
		query.forEach(agents, new Consumer<IAgent>() {
			
			@Override
			public void accept(IAgent t) {
				double wealth = t.getWealth(stats);
				average.add(wealth);
			}
		});
//		System.out.println("Calculated wealth " + average.getAverage() + ", log: " + Math.log(average.getAverage()));
		return Numbers.normalize(Math.log(average.getAverage()));
	}

}
