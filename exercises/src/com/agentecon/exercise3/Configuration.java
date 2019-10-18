package com.agentecon.exercise3;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.InterestEconomyConfiguration;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.consumer.IUtility;
import com.agentecon.util.Average;

public class Configuration extends InterestEconomyConfiguration {

	private static final int RANDOM_SEED = 299; // seed for the deterministic random number generator
	private static final boolean COMPETITIVE = false; // switch to false to make every life exactly 100 days long

	private Average buffer;
	
	public Configuration() throws SocketTimeoutException, IOException {
		super(RANDOM_SEED, COMPETITIVE);
		this.buffer = new Average();
	}
	
	@Override
	protected double getLabourShare() {
		return 0.8; // change this number to see if your agent finds an ok buffer size also for other settings
	}
	
	@Override
	protected IConsumer createGeneralConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		final DiscountingConsumer consumer = new DiscountingConsumer(id, maxAge, end, utility);
		consumer.addListener(new IConsumerListener() {
			
			@Override
			public void notifyDied(IConsumer inst) {
				double reserve = consumer.getCapitalBuffer();
				buffer.add(reserve);
			}
		});
		return consumer;
	}
	
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		Configuration config = new Configuration();
		Simulation sim = new Simulation(config);
		sim.run();
		System.out.println("Your agents found the following average buffer size " + config.buffer.toFullString());
		double discountRate = DiscountingConsumer.DISCOUNT_RATE;
		double profitShare = 1.0 - config.getLabourShare();
		double bufferEstimate = 1.0 - discountRate / profitShare;
		System.out.println("Ideally, it should be roughly " + bufferEstimate);
	}

}
