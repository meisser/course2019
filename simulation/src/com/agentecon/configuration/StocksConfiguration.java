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
import java.util.Random;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.SimEvent;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.MarketMaker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class StocksConfiguration extends FarmingConfiguration implements IUtilityFactory, IInnovation {

	private static final int BASIC_AGENTS = 30;

	public static final double GROWTH_RATE = 0.005;
	
	private static final int GROW_UNTIL = 400; // day at which growth stops
	
	private Random rand = new Random(1313);
	private int maxAge;
	
	public StocksConfiguration() throws SocketTimeoutException, IOException {
		this(500);
	}

	public StocksConfiguration(int maxAgeParam) throws SocketTimeoutException, IOException {
		super(new IAgentFactory() {

			private int number = 1;

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility utility) {
				int maxAge = number++ * maxAgeParam / BASIC_AGENTS;
				return new InvestingConsumer(id, maxAge, end, utility);
			}
		}, BASIC_AGENTS);
		this.maxAge = maxAgeParam;
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createBasicPopulation(workerEndowment);
		addMarketMakers();
		addEvent(new CentralBankEvent(POTATOE));
	}
	
	@Override
	public int getMaxAge() {
		return maxAge;
	}
	
	@Override
	public LogUtilWithFloor create(int number) {
		return super.create(number).wiggle(rand);
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
		addEvent(new GrowthEvent(0, GROWTH_RATE, false) {

			@Override
			protected void execute(ICountry sim) {
				if (sim.getDay() < GROW_UNTIL) {
					IConsumer cons = new InvestingConsumer(sim, getMaxAge(), workerEndowment, create(0));
					sim.add(cons);
				}
			}

		});
		addEvent(new GrowthEvent(GROW_UNTIL, 1.0d / getMaxAge(), false) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, getMaxAge(), workerEndowment, create(0));
				sim.add(cons);
			}

		});
	}

}
