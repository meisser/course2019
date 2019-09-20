package com.agentecon.events;

import java.util.Random;

import org.junit.Test;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.world.Agents;
import com.agentecon.world.ICountry;

public class SinConsumerEventTest implements ICountry {
	
	private int day;
	private int consumers;

	@Test
	public void test() {
		SinConsumerEvent e = new SinConsumerEvent(50, 7, 100, 150) {

			@Override
			protected void addConsumer(ICountry sim) {
				sim.add(null);
			}
			
		};
		for (day = e.getDay(); day<200; day++){
			e.execute(day, this);
		}
		assert consumers == 107;
	}

	@Override
	public Random getRand() {
		return null;
	}

	@Override
	public int getDay() {
		return day;
	}

	public void add(IAgent agent) {
		consumers++;
	}

	@Override
	public Agents getAgents() {
		return null;
	}

	@Override
	public Good getMoney() {
		return new Good("Taler");
	}
	
	@Override
	public int previewNextId() {
		return 1;
	}
	
	@Override
	public int createUniqueAgentId() {
		return 1;
	}

}
