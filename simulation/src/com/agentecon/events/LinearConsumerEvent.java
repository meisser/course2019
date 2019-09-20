package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.world.ICountry;

public class LinearConsumerEvent extends ConsumerEvent {

	private int maxAge;
	private int initialPopulation;

	public LinearConsumerEvent(int initialPopulation, int birthPerInterval, int maxAge, int interval, String name, Endowment end, IUtilityFactory utility) {
		super(0, birthPerInterval, interval, end, utility);
		this.maxAge = maxAge;
		this.initialPopulation = initialPopulation;
	}

	public LinearConsumerEvent(int initialPopulation, int birthPerInterval, int maxAge, int interval, String name, Endowment end, IUtility logUtil) {
		this(initialPopulation, birthPerInterval, maxAge, interval, name, end, new IUtilityFactory() {

			@Override
			public IUtility create(int number) {
				return new LogUtilWithFloor();
			}
		});
	}

	@Override
	public void execute(int day, ICountry sim) {
		if (initialPopulation > 0) {
			int step = maxAge / initialPopulation;
			for (; initialPopulation > 0; initialPopulation--) {
				int maxAge = Math.max(this.maxAge - initialPopulation * step, step);
				sim.add(new MortalConsumer(sim, maxAge, end, utilFun.create(count++)));
			}
		}
		super.execute(day, sim);
	}
	
	@Override
	protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util){
		return new MortalConsumer(id, this.maxAge, end, util);
	}

}
