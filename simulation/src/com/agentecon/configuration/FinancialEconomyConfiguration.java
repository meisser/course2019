/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.SimEvent;
import com.agentecon.events.SinConsumerEvent;
import com.agentecon.exercises.ExerciseAgentLoader;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.finance.IInterest;
import com.agentecon.finance.MarketMaker;
import com.agentecon.finance.bank.CentralBank;
import com.agentecon.finance.bank.CreditBank;
import com.agentecon.finance.bank.IDistributionPolicy;
import com.agentecon.finance.bank.InterestDistribution;
import com.agentecon.firm.Farm;
import com.agentecon.firm.IBank;
import com.agentecon.firm.Ticker;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.world.ICountry;

public class FinancialEconomyConfiguration extends SimulationConfig implements IUtilityFactory {

	public static final int LIFE_EXPECTANCY = 250;
	private static final int CYCLE_LENGTH = 377;

	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good LAND = HermitConfiguration.LAND;
	public static final double START_CAPITAL = 1000;

	protected static final double PRODUCTION_MULTIPLIER = 3.0;

	private static final String FUND = "com.agentecon.fund.InvestmentFund";
	private static final String TEST_FUND = "com.agentecon.fund.TestInvestmentFund";

	private Ticker centralBank;
	private InterestDistribution policy;

	public FinancialEconomyConfiguration(int seed) throws SocketTimeoutException, IOException {
		super(3000, seed);
		this.policy = new InterestDistribution();
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment endowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createCentralBank();
		createPopulation(endowment, LIFE_EXPECTANCY);
		createFarms(7);
		addMarketMakers();
		addBank();
		boolean remote = SimulationConfig.shouldLoadRemoteTeams();
		int[] teams = ExerciseAgentLoader.COMPETITIVE_TEAMS;
		if (remote) {
			addInvestmentFunds(new ExerciseAgentLoader(FUND, teams), teams.length);
		} else {
			addInvestmentFunds(new ExerciseAgentLoader(FUND), teams.length - 1);
			addInvestmentFunds(new ExerciseAgentLoader(TEST_FUND), 1);
		}
	}

	private void createPopulation(Endowment endowment, int lifeExpectancy) {
		addEvent(new ConsumerEvent(lifeExpectancy / 2, endowment, this) {

			int count = 1;

			@Override
			protected IConsumer createConsumer(ICountry id, Endowment end, IUtility util) {
				return new InvestingConsumer(id, count++, end, util);
			}

		});
		addEvent(new SinConsumerEvent(0, 0, CYCLE_LENGTH / 2, CYCLE_LENGTH) {

			@Override
			protected void addConsumer(ICountry sim) {
				sim.add(new InvestingConsumer(sim, lifeExpectancy, endowment, create(0)));
			}

		});
	}

	protected CentralBank findCentralBank(IAgents agents) {
		return (CentralBank) agents.getFirm(centralBank);
	}

	private void createCentralBank() {
		addEvent(new SimEvent(0) {
			public void execute(int day, ICountry sim) {
				assert sim.getAgents().getAgents().size() == 0 : "Central bank must be first";
				CentralBank cb = new CentralBank(getDistributionPolicy(), sim, new Endowment(getMoney()), POTATOE);
				centralBank = cb.getTicker();
				sim.add(cb);
			}
		});
		addEvent(new WealthTaxEvent(0.001) {

			@Override
			protected void distribute(ICountry sim, IStatistics stats, Stock temp) {
				CentralBank cb = findCentralBank(sim.getAgents());
				cb.getMoney().absorb(temp);
				cb.distributeMoney(sim.getAgents().getConsumers(), stats);
			}

		});
	}

	@Override
	public IInterest getInterest() {
		return new IInterest() {

			@Override
			public double getInterestRate() {
				return policy.getImpliedInterest();
			}

			@Override
			public double getAverageDiscountRate() {
				return 1.0 / LIFE_EXPECTANCY;
			}
		};
	}

	protected IDistributionPolicy getDistributionPolicy() {
		return policy;
	}

	private void createFarms(int count) {
		Endowment end = new Endowment(getMoney(), new Stock[] { new Stock(getMoney(), 1000), new Stock(POTATOE, 10), new Stock(LAND, 100) }, new Stock[] {});
		Ticker[] ticker = new Ticker[1];
		addEvent(new FirmEvent(count, end, createProductionFunction(PRODUCTION_MULTIPLIER)) {
			@Override
			protected Firm createFirm(ICountry sim, Endowment end, IProductionFunction prodFun) {
				Firm firm = new Farm(sim, end, prodFun);
				ticker[0] = firm.getTicker();
				return firm;
			}
		});
		addEvent(new SimEvent(2000) {
			@Override
			public void execute(int day, ICountry sim) {
				Farm farm = (Farm) sim.getAgents().getAgent(ticker[0].getNumer());
				farm.updateProductionFunction(createProductionFunction(PRODUCTION_MULTIPLIER * 2));
			}
		});
	}

	public IProductionFunction createProductionFunction(double multiplier) {
		return new CobbDouglasProduction(POTATOE, multiplier, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}

	@Override
	public IUtility create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}

	private void addBank() {
		addEvent(new SimEvent(0) {

			@Override
			public void execute(int day, ICountry sim) {
				IBank bank = new CreditBank(sim, new Endowment(getMoney()));
				sim.add(bank);
			}
		});
	}

	protected void addMarketMakers() {
		addEvent(new SimEvent(0, 0, 5) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), START_CAPITAL);
					sim.add(new MarketMaker(sim, money));
				}
			}
		});
	}

	private void addInvestmentFunds(IAgentFactory factory, int number) throws IOException {
		addEvent(new SimEvent(500, 0, number) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), START_CAPITAL);
					sim.add(factory.createFirm(sim, money));
				}
			}
		});
	}

}
