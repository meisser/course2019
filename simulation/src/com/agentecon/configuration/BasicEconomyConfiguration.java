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
import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.BufferingMortalConsumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.SimEvent;
import com.agentecon.exercises.ExerciseAgentLoader;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.finance.bank.CentralBank;
import com.agentecon.finance.bank.IDistributionPolicy;
import com.agentecon.finance.bank.InterestDistribution;
import com.agentecon.firm.Farm;
import com.agentecon.firm.Ticker;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.world.ICountry;

public class BasicEconomyConfiguration extends SimulationConfig implements IUtilityFactory {

	public static final int LIFE_EXPECTANCY = 100;
	public static final int STUDENTS_START = 100;
	public static final int BIRTH_PER_DAY = 1;
	public static final int STUDENT_AGENTS = 500;
	public static final int FARMS = 5;

	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good LAND = HermitConfiguration.LAND;
	public static final Quantity FIXED_COSTS = HermitConfiguration.FIXED_COSTS;

	private Ticker centralBank;
	private boolean randomizedDemographics;
	private IAgentFactory factory;

	public BasicEconomyConfiguration(int seed, boolean randomizedDemographics) throws SocketTimeoutException, IOException {
		super(randomizedDemographics ? calculateRounds(seed) : STUDENTS_START + STUDENT_AGENTS / BIRTH_PER_DAY + LIFE_EXPECTANCY, seed + 133);
		this.randomizedDemographics = randomizedDemographics;
		this.factory = new ExerciseAgentLoader("com.agentecon.exercise2.GeneralConsumer");
		int expectedPopulation = LIFE_EXPECTANCY * BIRTH_PER_DAY;
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment endowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createCentralBank();
		createPopulation(endowment, expectedPopulation, seed);
		createFarms(((double) expectedPopulation) / FARMS, FARMS);
	}

	private static int calculateRounds(int seed) {
		LifeClock clock = new LifeClock(LIFE_EXPECTANCY, seed);
		assert BIRTH_PER_DAY == 1;
		int lastDay = STUDENTS_START;
		for (int day = STUDENTS_START; day < STUDENTS_START + STUDENT_AGENTS; day++) {
			lastDay = Math.max(lastDay, day + clock.getRandomLifeLength());
		}
		return lastDay + 5;
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
		addEvent(new SimEvent(1, 1, 1) {
			public void execute(int day, ICountry sim, IStatistics stats) {
				CentralBank bank = findCentralBank(sim.getAgents());
				bank.distributeMoney(sim.getAgents().getConsumers(), stats);
			}
		});
	}

	protected IDistributionPolicy getDistributionPolicy() {
		return new InterestDistribution();
	}

	private void createPopulation(Endowment e, int initialAgents, int seed) {
		LifeClock normalConsumerClock = new LifeClock(LIFE_EXPECTANCY, seed + 123123, randomizedDemographics);
		addEvent(new ConsumerEvent(initialAgents, e, this) {

			private int maxAge = 1;

			@Override
			protected IConsumer createConsumer(ICountry id, Endowment end, IUtility util) {
				if (randomizedDemographics) {
					return new BufferingMortalConsumer(id, normalConsumerClock.getRandomLifeLength(), end, util);
				} else {
					return new BufferingMortalConsumer(id, maxAge++, end, util);
				}
			}
		});
		addEvent(new ConsumerEvent(1, BIRTH_PER_DAY, 1, e, this) {

			private int count = 0;
			private LifeClock clock = new LifeClock(LIFE_EXPECTANCY, seed, randomizedDemographics);

			@Override
			protected IConsumer createConsumer(ICountry id, Endowment end, IUtility util) {
				if (id.getDay() >= STUDENTS_START && count++ < STUDENT_AGENTS) {
					return createGeneralConsumer(id, clock.getRandomLifeLength(), end, util);
				} else {
					return new BufferingMortalConsumer(id, normalConsumerClock.getRandomLifeLength(), end, util);
				}
			}
		});
//		if (!randomizedDemographics) {
//			addEvent(new ConsumerEvent(STUDENTS_START + LIFE_EXPECTANCY + 73, 1, 0, e, this) {
//				@Override
//				protected IConsumer createConsumer(ICountry id, Endowment end, IUtility util) {
//					return createIndividualTestConsumer(id, LIFE_EXPECTANCY, end, util);
//				}
//			});
//		}
	}
	
	protected IConsumer createGeneralConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility util) {
		return factory.createConsumer(id, maxAge, end, util);
	}

	protected IConsumer createIndividualTestConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility util) {
		return new BufferingMortalConsumer(id, maxAge, end, util) {
			@Override
			protected String inferType(Class<? extends Agent> clazz) {
				return "Dummy";
			}
		};
	}

	private void createFarms(double productivityMultiplier, int count) {
		Endowment end = new Endowment(getMoney(), new Stock[] { new Stock(getMoney(), 1000), new Stock(POTATOE, 10), new Stock(LAND, 100) }, new Stock[] {});
		addEvent(new FirmEvent(count, end, createProductionFunction(productivityMultiplier)) {
			@Override
			protected Firm createFirm(ICountry sim, Endowment end, IProductionFunction prodFun) {
				CentralBank cb = findCentralBank(sim.getAgents());
				assert cb != null;
				return new Farm(sim, cb, prodFun, end, null);
			}
		});
	}

	public IProductionFunction createProductionFunction(double multiplier) {
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, multiplier, FIXED_COSTS, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}

	@Override
	public int getMaxAge() {
		// only for statistics
		return LIFE_EXPECTANCY * 4; // almost no agents should get that old :)
	}

	@Override
	public IUtility create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}

//	@Override
//	public void diagnoseResult(PrintStream out, ISimulation sim) {
//		try {
//			IAgents agents = sim.getAgents();
//			System.out.println();
//			System.out.println("On the last day, there were " + agents);
//			Average avg = new Average();
//			for (IConsumer c : agents.getConsumers()) {
//				avg.add(c.getUtilityFunction().getLatestExperiencedUtility());
//			}
//			System.out.println("The average utility experienced by the consumer on the last day was: " + avg.toFullString());
//
//			IStatistics stats = sim.getStatistics();
//			CobbDouglasProductionWithFixedCost prodFun = (CobbDouglasProductionWithFixedCost) createProductionFunction(LIFE_EXPECTANCY*BIRTH_PER_DAY/FARMS);
//			GoodStats manhours = stats.getGoodsMarketStats().getStats(MAN_HOUR);
//			double laborShare = prodFun.getWeight(MAN_HOUR).weight;
//			double optimalNumberOfFirms = (manhours.getYesterday().getTotWeight() / prodFun.getInputMultiplier()) / FIXED_COSTS.getAmount() * (1 - laborShare);
//			int numberOfFirms = sim.getAgents().getFirms().size();
//			System.out.println();
//			System.out.println("On the last simulation day, " + manhours + " were sold from consumers to firms.");
//			System.out.println("This implies optimal number of firms k=" + optimalNumberOfFirms + ". The actual number of firms is " + numberOfFirms + " (some of which might not be active).");
//
//			Inventory inv = new Inventory(getMoney(), new Stock(LAND, 100));
//			double optimalCost = prodFun.getCostOfMaximumProfit(inv, stats.getGoodsMarketStats());
//			double optimalManhours = optimalCost / stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
////			double fixedCosts = prodFun.getFixedCost(MAN_HOUR) * stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
//			inv.getStock(MAN_HOUR).add(optimalManhours);
//			Quantity prod = prodFun.produce(inv);
//			double profits = prod.getAmount() * stats.getGoodsMarketStats().getPriceBelief(POTATOE) - optimalCost;
//
////			double profitShare = 1.0 - laborShare;
////			double profits2 = (optimalCost - fixedCosts) / laborShare * profitShare - fixedCosts;
//			System.out.println("Given current market prices, a firm should use " + optimalManhours + " " + MAN_HOUR + " to produce " + prod + " and yield a profit of " + profits + ".");
//			double altInput = 12;
//			System.out.println("Using " + altInput + " man-hours would yield a profit of " + getProfits(prodFun, stats, altInput) + ".");
//
//			System.out.println();
//			System.out.println("Market statistics for the last day:");
//			System.out.println(stats.getGoodsMarketStats());
//			System.out.println("Price ratio: " + stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR) / stats.getGoodsMarketStats().getPriceBelief(POTATOE));
//
//			double totalInput = manhours.getYesterday().getTotWeight();
//			double perFirm = totalInput / optimalNumberOfFirms;
//			if (perFirm > 0.0) {
//				inv.getStock(MAN_HOUR).add(perFirm);
//				double output = prodFun.produce(inv).getAmount() * optimalNumberOfFirms;
//				System.out.println("With " + optimalNumberOfFirms + " firms the " + totalInput + " manhours could have produced " + output + " instead of "
//						+ stats.getGoodsMarketStats().getStats(POTATOE).getYesterday().getTotWeight());
//			}
//		} catch (PriceUnknownException e) {
//			e.printStackTrace(out);
//		}
//	}
//
//	private double getProfits(IProductionFunction prodFun, IStatistics sim, double inputAmount) throws PriceUnknownException {
//		Inventory inv = new Inventory(getMoney(), new Stock(LAND, 100));
//		double costs = inputAmount * sim.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
//		inv.getStock(MAN_HOUR).add(inputAmount);
//		Quantity prod = prodFun.produce(inv);
//		return prod.getAmount() * sim.getGoodsMarketStats().getPriceBelief(POTATOE) - costs;
//	}

}
