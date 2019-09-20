/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.Weight;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.MinPopulationGrowthEvent;
import com.agentecon.exercises.ExerciseAgentLoader;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class GrowthConfiguration extends FarmingConfiguration implements IUtilityFactory, IInnovation {

	private static final int BASIC_AGENTS = 5 * ExerciseAgentLoader.TEAMS.size();
	public static final String FARMER = "com.agentecon.exercise4.Farmer";

	public static final double GROWTH_RATE = 0.0023;
	public static final int MAX_AGE = 500;

	@SafeVarargs
	public GrowthConfiguration(Class<? extends Consumer>... agents) {
		this(new AgentFactoryMultiplex(agents), BASIC_AGENTS);
	}

	public GrowthConfiguration(IAgentFactory loader, int agents) {
		super(new IAgentFactory() {

			private int number = 1;

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
				int maxAge = number++ * MAX_AGE / agents;
				return loader.createConsumer(id, maxAge, endowment, utilityFunction);
			}
		}, agents);
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		IAgentFactory growthLoader = loader;
		addEvent(new MinPopulationGrowthEvent(0, BASIC_AGENTS) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = growthLoader.createConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
		addEvent(new GrowthEvent(0, GROWTH_RATE, false) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = growthLoader.createConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
		addEvent(new CentralBankEvent(POTATOE));
	}

	@Override
	public CobbDouglasProductionWithFixedCost createProductionFunction(Good desiredOutput) {
		assert desiredOutput.equals(POTATOE);
		return new CobbDouglasProductionWithFixedCost(POTATOE, 3.0, FIXED_COSTS, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}

	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}

	public static void main(String[] args) {
		System.out.println("asdasd");
	}

}
