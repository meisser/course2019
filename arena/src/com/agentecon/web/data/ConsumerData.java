/**
 * Created by Luzius Meisser on Jun 13, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class ConsumerData extends AgentData {
	
	public double latestUtility;
	public double averageUtility;
	public double varianceOfUtility;
	
	public ConsumerData(IConsumer agent) {
		super(agent);
		IUtility util = agent.getUtilityFunction();
		this.latestUtility = util.getLatestExperiencedUtility();
		this.averageUtility = util.getStatistics().getAverage();
		this.varianceOfUtility = util.getStatistics().getVariance();
	}

}
