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
	private HashMap<String, String> nameTypeMap;

	public FirmRanking(ISimulation sim) {
		super(sim);
		this.ranking = null;
		this.types = new HashMap<String, Rank>();
		this.nameTypeMap = new HashMap<String, String>();
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		Rank rank = types.get(firm.getType());
		if (rank == null) {
			rank = new Rank(firm.getType(), (Agent) firm);
			types.put(firm.getType(), rank);
		}
		nameTypeMap.put(firm.getName(), firm.getType());
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
			Collection<? extends TimeSeries> series = collector.getIndividualTimeSeries();
			for (TimeSeries ts : series) {
				String type = nameTypeMap.get(ts.getName());
				Rank rank = types.get(type);
				rank.add(ts.getSum(), true);
			}
			ranking = new ArrayList<Rank>(types.values());
			Collections.sort(ranking);
			for (Rank rank : ranking) {
				rank.roundScore();
			}
		}
		return ranking;
	}

}
