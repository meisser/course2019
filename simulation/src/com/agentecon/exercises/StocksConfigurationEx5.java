package com.agentecon.exercises;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.CentralBankEvent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.MinPopulationGrowthEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.MarketMaker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class StocksConfigurationEx5 extends HighProductivityConfiguration implements IUtilityFactory, IInnovation {

	private static final int BASIC_AGENTS = 30;
	public static final String BASIC_AGENT = "com.agentecon.exercise5.Investor";
	public static final String REFERENCE_AGENT = "com.agentecon.exercise5.ReferenceInvestor";

	public static final double GROWTH_RATE = 0.0025;
	public static final int MAX_AGE = 500;
	private static final int GROW_UNTIL = 2000; // day at which growth stops

	public StocksConfigurationEx5() throws SocketTimeoutException, IOException {
		this(new ExerciseAgentLoader(REFERENCE_AGENT), BASIC_AGENTS);
	}

	public StocksConfigurationEx5(IAgentFactory loader, int agents) {
		super(new IAgentFactory() {

			private int number = 1;

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility utility) {
				int maxAge = number++ * MAX_AGE / agents;
				return new InvestingConsumer(id, maxAge, end, utility);
			}
		}, agents);
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createBasicPopulation(workerEndowment);
		addMarketMakers();
		addEvent(new CentralBankEvent(POTATOE));
		addCustomInvestors(loader, workerEndowment);
	}

	private void addCustomInvestors(IAgentFactory loader, Endowment end) {
		addEvent(new SimEvent(ROUNDS - MAX_AGE - 1, 0, 5) {

			private HashSet<String> types = new HashSet<>();

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < 10; i++) {
					IConsumer newConsumer = loader.createConsumer(sim, MAX_AGE, end, create(0));
					if (newConsumer != null && types.add(newConsumer.getType())) {
						sim.add(newConsumer);
					}
				}
			}
		});
	}

	protected void addMarketMakers() {
		addEvent(new SimEvent(0, 0, 10) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), 1000);
					sim.add(new MarketMaker(sim, money));
				}
			}
		});
	}

	protected void createBasicPopulation(Endowment workerEndowment) {
		addEvent(new MinPopulationGrowthEvent(0, BASIC_AGENTS) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
		addEvent(new GrowthEvent(0, GROWTH_RATE, false) {

			@Override
			protected void execute(ICountry sim) {
				if (sim.getDay() < GROW_UNTIL) {
					IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
					sim.add(cons);
				}
			}

		});
		addEvent(new GrowthEvent(GROW_UNTIL, 1.0d / MAX_AGE, false) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
	}
	
	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}

}
