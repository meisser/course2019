/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.graph;

import com.agentecon.agent.IAgents;
import com.agentecon.market.IStatistics;
import com.agentecon.web.query.AgentQuery;

public abstract class AgentSize {

	public AgentSize() {
	}
	
	public void initialize(AgentQuery query, IAgents agents){
	}
	
	public abstract double getSize(final IStatistics stats, AgentQuery query, IAgents agents);

}
