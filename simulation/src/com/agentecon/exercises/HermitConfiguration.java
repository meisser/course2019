/**
 * Created by Luzius Meisser on Jun 18, 2017
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
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IInnovation;
import com.agentecon.research.IResearchProject;
import com.agentecon.sim.SimulationConfig;

public class HermitConfiguration extends SimulationConfig implements IInnovation, IUtilityFactory {

	public static final String AGENT_CLASS_NAME = "com.agentecon.exercise1.Hermit";

	public static final Good POTATOE = new Good("Potatoe", 0.99);
	public static final Good MAN_HOUR = new Good("Man-hour", 0.0);
	public static final int DAILY_ENDOWMENT = 24;

	public static final Good LAND = new Good("Land", 1.0);

	private static final int ROUNDS = 1000;

	public static final Quantity FIXED_COSTS = new Quantity(MAN_HOUR, 6.0);

	public HermitConfiguration() throws IOException {
		this(new ExerciseAgentLoader(AGENT_CLASS_NAME), 16);
	}

	public HermitConfiguration(IAgentFactory factory, int agents) {
		super(ROUNDS);
		Endowment end = createEndowment();
		addEvent(new ConsumerEvent(agents, end, this) {
			@Override
			protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util) {
				return factory.createConsumer(id, end, util);
			}
		});
	}

	public Endowment createEndowment() {
		IStock[] initialEndowment = new IStock[] { new Stock(LAND, 100) };
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, DAILY_ENDOWMENT) };
		Endowment end = new Endowment(getMoney(), initialEndowment, dailyEndowment);
		return end;
	}
	
	@Override
	public IUtility create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}

	@Override
	public IInnovation getInnovation() {
		return this;
	}

	@Override
	public IProductionFunction createProductionFunction(Good desiredOutput) {
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, FIXED_COSTS, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}

	@Override
	public void diagnoseResult(PrintStream out, ISimulation stats) {
		out.println("Achieved an average utility of " + stats.getStatistics().getAverageUtility());

		IProductionFunction prodFun = createProductionFunction(POTATOE);
		double weight = prodFun.getWeight(MAN_HOUR).weight;
		double fixedCost = prodFun.getFixedCost(MAN_HOUR);
		double optimalWorkAmount = (DAILY_ENDOWMENT * weight + fixedCost) / (1 + weight);
		out.println("Optimal work amount would be " + optimalWorkAmount);
	}

	@Override
	public IResearchProject createResearchProject(Good desiredOutput) {
		throw new RuntimeException("not implemented");
	}

//	private static IAgentFactory createFactory() throws SocketTimeoutException, IOException {
//		IAgentFactory defaultFactory = new CompilingAgentFactory(AGENT_CLASS_NAME, new File("../exercises/src")); // this factory loads agents from the local disk
//		IAgentFactory adaptiveFactory = new CompilingAgentFactory("com.agentecon.exercise1.CovarianceHermit", new File("../exercises/src"));
//		IAgentFactory leastSquareFactory = new CompilingAgentFactory("com.agentecon.exercise1.LeastSquaresHermit", new File("../exercises/src"));
//		IAgentFactory factory = new AgentFactoryMultiplex(defaultFactory, new LimitingAgentFactory(1, adaptiveFactory), new LimitingAgentFactory(1, leastSquareFactory));
//		return factory;
//	}
//
//	public static void main(String[] args) throws SocketTimeoutException, IOException {
////		IAgentFactory factory = createFactory();
//		HermitConfiguration config = new HermitConfiguration(); // Create the configuration
//		Simulation sim = new Simulation(config); // Create the simulation
//		ConsumerRanking ranking = new ConsumerRanking(); // Create a ranking
//		sim.addListener(ranking); // register the ranking as a listener interested in what is going on
//		sim.run(); // run the simulation
//		ranking.print(System.out); // print the resulting ranking
//	}

}
