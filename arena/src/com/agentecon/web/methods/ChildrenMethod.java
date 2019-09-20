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
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.runner.Recyclable;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.graph.Child;
import com.agentecon.web.query.AgentQuery;

public class ChildrenMethod extends SimSpecificMethod {

	public ChildrenMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "&" + AgentQuery.getExample();
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException {
		Recyclable<ISimulation> sim = getSimulation(params, params.getDay());
		try {
			AgentQuery query = new AgentQuery(params.getSingleSelection());
			return new Children(query.getChildren(sim.getItem().getAgents()));
		} finally {
			sim.recycle();
		}
	}

	class Children extends JsonData {

		Collection<Child> children;

		public Children(Collection<Child> children) {
			this.children = children;
		}

	}

}
