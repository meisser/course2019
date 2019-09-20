/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class LimitingAgentFactory implements IAgentFactory {
	
	private IAgentFactory wrapped;
	private int maxNumberOfAgents;

	public LimitingAgentFactory(int maxNumberOfAgents, IAgentFactory wrapped){
		this.wrapped = wrapped;
		this.maxNumberOfAgents = maxNumberOfAgents;
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		if (maxNumberOfAgents-- > 0){
			return wrapped.createConsumer(id, endowment, utilityFunction);
		} else {
			return null;
		}
	}
	
	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		if (maxNumberOfAgents-- > 0){
			return wrapped.createConsumer(id, maxAge, endowment, utilityFunction);
		} else {
			return null;
		}
	}

}
