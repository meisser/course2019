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

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.production.IProductionFunction;
import com.agentecon.sim.SimulationConfig;

public class BasicEconomyConfiguration extends SimulationConfig implements IUtilityFactory {

	private static final int LIFE_EXPECTANCY = 100;
	private static final int STUDENTS_START = 100;
	private static final int BIRTH_PER_DAY = 1;
	private static final int STUDENT_AGENTS = 200;
	private static final int FARMS = 5;
	
	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good LAND = HermitConfiguration.LAND;
	public static final Quantity FIXED_COSTS = HermitConfiguration.FIXED_COSTS;

	public BasicEconomyConfiguration(int seed) throws SocketTimeoutException, IOException {
		super(calculateRounds(seed));
		int expectedPopulation = LIFE_EXPECTANCY * BIRTH_PER_DAY;
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment endowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createPopulation(endowment, expectedPopulation);
		createFarms(((double)expectedPopulation) / FARMS, FARMS);
		addEvent(new CentralBankEvent(POTATOE));
	}
	
	private static int calculateRounds(int seed) {
		LifeClock clock = new LifeClock(LIFE_EXPECTANCY, seed);
		assert BIRTH_PER_DAY == 1;
		int lastDay = STUDENTS_START;
		for (int day = STUDENTS_START; day < STUDENTS_START + STUDENT_AGENTS; day++) {
			lastDay = Math.max(lastDay, day + clock.getRandomLifeLength());
		}
		return lastDay + 1;
	}

	private void createPopulation(Endowment e, int number) {
		addEvent(new ConsumerEvent(number, e, this) {
			protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util){
				return new Consumer(id, end, util);
			}
		});
		addEvent(new ConsumerEvent(0, BIRTH_PER_DAY, 1, e, this) {
			protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util){
				return new Consumer(id, end, util);
			}
		});
	}
	
	private void createFarms(double productivityMultiplier, int count) {
		
	}

	public IProductionFunction createProductionFunction(double multiplier) {
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, FIXED_COSTS, new Weight(LAND, 0.2, true), new Weight(MAN_HOUR, 0.6));
	}
	
	@Override
	public int getMaxAge() {
		return LIFE_EXPECTANCY * 4; // almost no agents should get that old :)
	}

	@Override
	public IUtility create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}

}
