package com.agentecon.verification;

import java.util.Map;

import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.market.IStatistics;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class FirmStatistics extends SimulationListenerAdapter {
	
	private Map<String, AccumulatingAverage> avg;
	
	public FirmStatistics(){
		this.avg = new InstantiatingConcurrentHashMap<String, AccumulatingAverage>() {
			
			@Override
			protected AccumulatingAverage create(String key) {
				return new AccumulatingAverage();
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		for (AccumulatingAverage a: avg.values()){
			a.flush();
		}
	}

	public void reportProfits(IFirmDecisions strategy, double amount) {
		avg.get(strategy.getClass().getSimpleName()).add(amount);
	}
	
	public String getRanking(){
		String ranking = null;
		for (Map.Entry<String, AccumulatingAverage> e: avg.entrySet()){
			String line = e.getKey() + " produced " + e.getValue().getWrapped();
			if (ranking == null){
				ranking = line;
			} else {
				ranking = ranking + "\n" + line;
			}
		}
		return ranking;
	}
	
	@Override
	public String toString(){
		return avg.keySet().toString();
	}

}
