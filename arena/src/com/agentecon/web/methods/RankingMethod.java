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
import java.util.HashMap;

import com.agentecon.ISimulation;
import com.agentecon.metric.variants.Rank;
import com.agentecon.metric.variants.UtilityRanking;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.web.data.JsonData;

public class RankingMethod extends SimSpecificMethod {

	private transient HashMap<SimulationStepper, Object> locks;

	private static final String CACHE_KEY = "Ranking";

	public RankingMethod(ListMethod listing) {
		super(listing);
		this.locks = new InstantiatingHashMap<SimulationStepper, Object>() {

			@Override
			protected Object create(SimulationStepper key) {
				return new Object();
			}
		};
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException {
		SimulationStepper simulation = getSimulation(params);
		synchronized (locks.get(simulation)) {
			UtilityRanking ranking = (UtilityRanking) simulation.getCachedItem(CACHE_KEY);
			if (ranking == null) {
				ISimulation sim = simulation.getSimulation().getItem();
				ranking = new UtilityRanking(sim, false);
				sim.addListener(ranking);
				sim.run();
				simulation.putCached(CACHE_KEY, ranking);
			}
			return new Ranking(ranking.getRanking());
		}
	}

	class Ranking extends JsonData {

		Collection<Rank> list;

		public Ranking(Collection<Rank> children) {
			this.list = children;
		}

	}

}
