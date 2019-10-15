package com.agentecon.exercise2;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.BasicEconomyConfiguration;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.bank.EqualDistribution;
import com.agentecon.finance.bank.IDistributionPolicy;
import com.agentecon.finance.bank.InterestDistribution;
import com.agentecon.goods.Inventory;
import com.agentecon.util.Average;
import com.agentecon.world.ICountry;

public class Configuration extends BasicEconomyConfiguration {

	private static final int RANDOM_SEED = 299; // seed for the deterministic random number generator
	private static final boolean COMPETITIVE = false; // switch to false to make every life exactly 100 days long

	private int testConsumerId = -1;
	private Average generalUtility;
	private Average testConsumerUtility;
	private IDistributionPolicy distributionPolicy; // defines how to distribute profits

	public Configuration() throws SocketTimeoutException, IOException {
		this(RANDOM_SEED);
	}

	public Configuration(int seed) throws SocketTimeoutException, IOException {
		super(seed, COMPETITIVE);
		this.generalUtility = new Average();
		this.testConsumerUtility = new Average();
//		this.distributionPolicy = new InterestDistribution(); // proportional to wealth
		this.distributionPolicy = new EqualDistribution(); // equal distribution
		enableLuckyEventForDummy();
	}

	private void enableLuckyEventForDummy() {
		addEvent(new SimEvent(220) {
			public void execute(int day, ICountry sim) {
				IAgent testconsumer = sim.getAgents().getAgent(testConsumerId);
				testconsumer.getMoney().add(200);
			}
		});
	}

	@Override
	protected IDistributionPolicy getDistributionPolicy() {
		return distributionPolicy;
	}

	@Override
	protected IConsumer createGeneralConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility util) {
		return new GeneralConsumer(id, maxAge, end, util) {
			@Override
			protected double consume(Inventory inv) {
				double utility = super.consume(inv);
				generalUtility.add(utility);
				return utility;
			}
		};
	}

	@Override
	// Call on day 200 in non-competitive mode to create a single consumer for testing purposes
	protected IConsumer createIndividualTestConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility util) {
		TestConsumer consumer = new TestConsumer(id, maxAge, end, util) {
			@Override
			protected double consume(Inventory inv) {
				double utility = super.consume(inv);
				testConsumerUtility.add(utility);
				return utility;
			}
		};
		testConsumerId = consumer.getAgentId();
		return consumer;
	}

	private void printResults() {
		System.out.println("General consumer utility: " + generalUtility.toFullString());
		System.out.println("Test consumer utility: " + testConsumerUtility.toFullString());
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		Average overallTest = new Average();
		Average overallGeneral = new Average();
		int runs = 5;
		for (int i = 0; i < runs; i++) {
			Configuration config = new Configuration(RANDOM_SEED + i);
			Simulation sim = new Simulation(config);
			sim.forwardTo(300);
//			System.out.println(config.distributionPolicy.toString()); // to print out interest information
			sim.run();
//			config.printResults(); // to print out individual results
			overallTest.add(config.testConsumerUtility.getAverage());
			overallGeneral.add(config.generalUtility.getAverage());
		}
		System.out.println("Test consumer average over " + runs + " runs: " + overallTest.toFullString());
		System.out.println("General consumer average over " + runs + " runs: " + overallGeneral.toFullString());
	}

}
