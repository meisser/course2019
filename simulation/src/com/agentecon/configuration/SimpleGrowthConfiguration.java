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
import com.agentecon.consumer.Consumer;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.exercises.ExerciseAgentLoader;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class SimpleGrowthConfiguration extends FarmingConfiguration implements IUtilityFactory, IInnovation {
	
	private static final int BASIC_AGENTS = 30;
	public static final String BASIC_AGENT = "com.agentecon.exercise3.Farmer";
	
	public static final double GROWTH_RATE = 0.001;

	@SafeVarargs
	public SimpleGrowthConfiguration(Class<? extends Consumer>... agents) {
		this(new AgentFactoryMultiplex(agents), BASIC_AGENTS);
	}
	
	public SimpleGrowthConfiguration() throws SocketTimeoutException, IOException {
		this(new ExerciseAgentLoader(BASIC_AGENT), BASIC_AGENTS);
	}
	
	public SimpleGrowthConfiguration(IAgentFactory loader, int agents) {
		super(loader, agents);
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		addEvent(new GrowthEvent(0, GROWTH_RATE){

			@Override
			protected void execute(ICountry sim) {
				sim.add(new Consumer(sim, workerEndowment, create(0)));
			}
			
		});
		addEvent(new InterestEvent(0.001, 1));
	}

}
