/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric.variants;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.Agent;
import com.agentecon.firm.IFirm;
import com.agentecon.metric.series.TimeSeries;

public class FirmRanking extends ShareholderValueStats {

	private ArrayList<Rank> ranking;
	private HashMap<String, Rank> types;

	public FirmRanking(ISimulation sim, boolean enableTimeSeries) {
		super(sim, true, true);
		this.ranking = null;
		this.types = new HashMap<String, Rank>();
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		types.put(firm.getType(), new Rank(firm.getType(), (Agent) firm));
		super.notifyFirmCreated(firm);
	}

	@Override
	public void print(PrintStream out) {
		List<Rank> list = getRanking();
		Collections.sort(list);
		int rank = 1;
		System.out.println("Rank\tType\tId\tDividends");
		for (Rank r : list) {
			out.println(rank++ + "\t" + r);
		}
	}

	public List<Rank> getRanking() {
		if (ranking == null) {
			ranking = new ArrayList<Rank>();
			Collection<? extends TimeSeries> series = collector.createTypeAveragesFromIndividualSeries();
			for (TimeSeries ts : series) {
				Rank rank = types.get(ts.getName());
				rank.add(ts.getLatest(), false);
				ranking.add(rank);
			}
			Collections.sort(ranking);
			for (Rank rank : ranking) {
				rank.roundScore();
			}
		}
		return ranking;
	}

}
