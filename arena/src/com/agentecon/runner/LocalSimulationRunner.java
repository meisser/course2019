/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.runner;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.metric.NoInterestingTimeSeriesFoundException;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.export.ExcelWriter;
import com.agentecon.metric.variants.MarketMakerStats;
import com.agentecon.metric.variants.UtilityRanking;
import com.agentecon.metric.variants.WealthStats;
import com.agentecon.production.PriceUnknownException;

public class LocalSimulationRunner {

	private ISimulation sim;
	private ExcelWriter writer;

	public LocalSimulationRunner(SimulationHandle handle) throws SocketTimeoutException, IOException {
		sim = new SimulationLoader(handle).loadSimulation();
		writer = new ExcelWriter(new File("data"));
	}

	private void run() throws IOException {
		UtilityRanking ranking = new UtilityRanking(sim, false);
//		SimStats prodStats = new ProductionStats(sim, false);
//		SimStats stats = new DividendStats(sim, new ArrayList<>());
		SimStats prices = new MarketMakerStats(sim);
		sim.addListener(prices);
//		sim.addListener(stats);
		sim.addListener(ranking);
//		sim.addListener(prodStats);
		sim.run();
		// stats.print(System.out);
		ranking.print(System.out);
//
//		System.out.println();
//		try {
//			writer.export(prices);
//			System.out.println();
//		} catch (NoInterestingTimeSeriesFoundException e) {
//			System.out.println("Not creating an excel file for " + prices.getName() + " as there is no interesting data");
//		}
//		sim.getConfig().diagnoseResult(System.out, sim);
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException, PriceUnknownException {
		LocalSimulationRunner runner = new LocalSimulationRunner(new LocalSimulationHandle());
		runner.run();
	}

}
