/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercises;

import java.io.IOException;
import java.io.PrintStream;

import com.agentecon.IAgentFactory;
import com.agentecon.ISimulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.agent.IAgents;
import com.agentecon.configuration.AgentFactoryMultiplex;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.market.GoodStats;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.research.IInnovation;
import com.agentecon.research.IResearchProject;
import com.agentecon.sim.IOptimalityIndicator;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.util.Average;

public class FarmingConfiguration extends SimulationConfig implements IInnovation, IUtilityFactory {

	private static final int AGENTS = 32;

	public static final String FARMER = "com.agentecon.exercise2.Farmer";

	public static final Good LAND = HermitConfiguration.LAND;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;

	private static final double LAND_ENDOWMENT = 100;

	public static final int ROUNDS = 10000;

	public static final Quantity FIXED_COSTS = HermitConfiguration.FIXED_COSTS;

	@SafeVarargs
	public FarmingConfiguration(Class<? extends Consumer>... agents) {
		this(new AgentFactoryMultiplex(agents), AGENTS);
	}

	public FarmingConfiguration() throws IOException {
		this(new ExerciseAgentLoader(FARMER), AGENTS);
	}

	public FarmingConfiguration(IAgentFactory factory, int agents) {
		super(ROUNDS);
		IStock[] initialEndowment = new IStock[] { new Stock(LAND, LAND_ENDOWMENT), new Stock(getMoney(), 10000) };
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment end = new Endowment(getMoney(), initialEndowment, dailyEndowment);
		addEvent(new ConsumerEvent(agents, end, this) {
			@Override
			protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util) {
				return factory.createConsumer(id, end, util);
			}
		});
	}

	@Override
	public IOptimalityIndicator[] getOptimalFirmCountIndicators() {
		return new IOptimalityIndicator[] { new OptimalFirmCountIndicator(createProductionFunction(POTATOE), MAN_HOUR) };
	}

	@Override
	public IOptimalityIndicator[] getOptimalProductionIndicators() {
		return new IOptimalityIndicator[] { new OptimalProductionIndicator(new Quantity(LAND, LAND_ENDOWMENT), createProductionFunction(POTATOE), MAN_HOUR) };
	}

	@Override
	public LogUtilWithFloor create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}

	@Override
	public IInnovation getInnovation() {
		return this;
	}

	@Override
	public CobbDouglasProductionWithFixedCost createProductionFunction(Good desiredOutput) {
		assert desiredOutput.equals(POTATOE);
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, FIXED_COSTS, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}

	@Override
	public IResearchProject createResearchProject(Good desiredOutput) {
		return null;
	}

	@Override
	public void diagnoseResult(PrintStream out, ISimulation sim) {
		try {
			IAgents agents = sim.getAgents();
			System.out.println();
			System.out.println("On the last day, there were " + agents);
			Average avg = new Average();
			for (IConsumer c : agents.getConsumers()) {
				avg.add(c.getUtilityFunction().getLatestExperiencedUtility());
			}
			System.out.println("The average utility experienced by the consumer on the last day was: " + avg.toFullString());

			IStatistics stats = sim.getStatistics();
			CobbDouglasProductionWithFixedCost prodFun = createProductionFunction(POTATOE);
			GoodStats manhours = stats.getGoodsMarketStats().getStats(MAN_HOUR);
			double laborShare = prodFun.getWeight(MAN_HOUR).weight;
			double optimalNumberOfFirms = manhours.getYesterday().getTotWeight() / FIXED_COSTS.getAmount() * (1 - laborShare);
			int numberOfFirms = sim.getAgents().getFirms().size();
			System.out.println();
			System.out.println("On the last simulation day, " + manhours + " were sold from consumers to firms.");
			System.out.println("This implies optimal number of firms k=" + optimalNumberOfFirms + ". The actual number of firms is " + numberOfFirms + " (some of which might not be active).");

			Inventory inv = new Inventory(getMoney(), new Stock(LAND, 100));
			double optimalCost = prodFun.getCostOfMaximumProfit(inv, stats.getGoodsMarketStats());
			double optimalManhours = optimalCost / stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
//			double fixedCosts = prodFun.getFixedCost(MAN_HOUR) * stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
			inv.getStock(MAN_HOUR).add(optimalManhours);
			Quantity prod = prodFun.produce(inv);
			double profits = prod.getAmount() * stats.getGoodsMarketStats().getPriceBelief(POTATOE) - optimalCost;

//			double profitShare = 1.0 - laborShare;
//			double profits2 = (optimalCost - fixedCosts) / laborShare * profitShare - fixedCosts;
			System.out.println("Given current market prices, a firm should use " + optimalManhours + " " + MAN_HOUR + " to produce " + prod + " and yield a profit of " + profits + ".");
			double altInput = 12;
			System.out.println("Using " + altInput + " man-hours would yield a profit of " + getProfits(prodFun, stats, altInput) + ".");

			System.out.println();
			System.out.println("Market statistics for the last day:");
			System.out.println(stats.getGoodsMarketStats());
			System.out.println("Price ratio: " + stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR) / stats.getGoodsMarketStats().getPriceBelief(POTATOE));

			double totalInput = manhours.getYesterday().getTotWeight();
			double perFirm = totalInput / optimalNumberOfFirms;
			if (perFirm > 0.0) {
				inv.getStock(MAN_HOUR).add(perFirm);
				double output = prodFun.produce(inv).getAmount() * optimalNumberOfFirms;
				System.out.println("With " + optimalNumberOfFirms + " firms the " + totalInput + " manhours could have produced " + output + " instead of "
						+ stats.getGoodsMarketStats().getStats(POTATOE).getYesterday().getTotWeight());
			}
		} catch (PriceUnknownException e) {
			e.printStackTrace(out);
		}
	}

	private double getProfits(IProductionFunction prodFun, IStatistics sim, double inputAmount) throws PriceUnknownException {
		Inventory inv = new Inventory(getMoney(), new Stock(LAND, 100));
		double costs = inputAmount * sim.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
		inv.getStock(MAN_HOUR).add(inputAmount);
		Quantity prod = prodFun.produce(inv);
		return prod.getAmount() * sim.getGoodsMarketStats().getPriceBelief(POTATOE) - costs;
	}

}
