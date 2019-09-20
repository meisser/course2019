package com.agentecon.events;

import org.junit.Test;

import com.agentecon.world.ICountry;

public class GrowthEventTest {

	private static final int MAX_AGE = 500;

	private int born;
	private int[] popByAge = new int[MAX_AGE];

	public GrowthEventTest() {
		born = 0;
		for (int i = 0; i < popByAge.length; i++) {
			popByAge[i] = 1;
		}
	}

	protected int getPopulation() {
		int sum = 0;
		for (int pop : popByAge) {
			sum += pop;
		}
		return sum;
	}

	@Test
	public void testConstantPopulation() {
		GrowthEvent event = new GrowthEvent(0, 0.002, false) {

			@Override
			protected int getPopulation(ICountry sim) {
				return GrowthEventTest.this.getPopulation();
			}

			@Override
			protected void execute(ICountry sim) {
				born++;
			}
		};
		for (int i = 0; i < 5000; i++) {
			event.execute(i, null);
			System.out.println(getPopulation());
			System.arraycopy(popByAge, 0, popByAge, 1, popByAge.length - 1);
			popByAge[0] = born;
			born = 0;
		}
		assert getPopulation() == 500 : "Population was " + getPopulation();
	}

}
