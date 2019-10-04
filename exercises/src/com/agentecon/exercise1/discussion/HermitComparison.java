package com.agentecon.exercise1.discussion;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.exercise1.SimpleAgentIdGenerator;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.learning.CovarianceControl;
import com.agentecon.learning.GoldenRatioSearch;
import com.agentecon.research.IFounder;

public class HermitComparison {
	
	// The "static void main" method is executed when running a class
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		HermitConfiguration config = new HermitConfiguration(null, 0);
		IAgentIdGenerator id = new SimpleAgentIdGenerator();

		// Comparing different solutions
		Consumer analytic = new AnalyticHermit(id, config.createEndowment(), config.create(0));
		Consumer covariance = new AdaptiveHermit(id, config.createEndowment(), config.create(0), new CovarianceControl(10, 0.8));
		Consumer goldenRatio = new AdaptiveHermit(id, config.createEndowment(), config.create(0), new GoldenRatioSearch(0.0, 24));

		Consumer[] all = new Consumer[] { analytic, covariance, goldenRatio };
		int endOfTime = 100; // let world end after 100 days
		System.out.println("Day\tAnalytic\t\tCovariance\t\tGolden ratio");
		for (int t = 0; t < endOfTime; t++) {
			System.out.print(t);
			for (Consumer current : all) {
				current.collectDailyEndowment();
				((IFounder)current).considerCreatingFirm(null, config, null);
				current.tradeGoods(null);
				double leisure = current.getInventory().getStock(HermitConfiguration.MAN_HOUR).getAmount();
				double utility = current.consume();
				System.out.print("\t" + leisure + "\t" + utility);
			}
			System.out.println();
		}
	}

}
