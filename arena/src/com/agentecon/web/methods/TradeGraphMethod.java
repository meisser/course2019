/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.agentecon.ISimulation;
import com.agentecon.runner.Recyclable;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.graph.TradeGraph;

public class TradeGraphMethod extends SimSpecificMethod {

	public TradeGraphMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "&selection=consumers,firms&step=1";
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException {
		int day = params.getDay();
		int stepSize = params.getIntParam("step");
		if (stepSize <= 0) {
			stepSize = 1;
		}
		Recyclable<ISimulation> simulation = getSimulation(params, Math.max(0, day - stepSize));
		try {
			Set<String> agents = new HashSet<>(params.getSelection());
			TradeGraph graph = new TradeGraph(simulation.getItem(), agents);
			simulation.getItem().forwardTo(day);
			return graph.fetchData();
		} finally {
			simulation.recycle();
		}
	}

}
