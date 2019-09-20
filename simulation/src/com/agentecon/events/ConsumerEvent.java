package com.agentecon.events;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.world.ICountry;

public class ConsumerEvent extends SimEvent {

	protected int count;
	protected Endowment end;
	protected IUtilityFactory utilFun;

	public ConsumerEvent(int card, Endowment end, IUtilityFactory utility) {
		this(0, card, 0, end, utility);
	}

	public ConsumerEvent(int time, int card, int interval, Endowment end, IUtilityFactory utility) {
		super(time, interval, card);
		this.end = end;
		this.count = 0;
		this.utilFun = utility;
	}

	@Override
	public void execute(int day, ICountry sim) {
		for (int i = 0; i < cardinality; i++) {
			sim.add((Agent) createConsumer(sim, end, utilFun.create(count++)));
		}
	}

	protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util){
		return new Consumer(id, end, util);
	}

	public String toString() {
		return "Add " + getCardinalityString() + " consumers";
	}

}
