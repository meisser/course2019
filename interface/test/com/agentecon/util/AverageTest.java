package com.agentecon.util;

import java.util.Random;

import org.junit.Test;

public class AverageTest {

	@Test
	public void test() {
		Random rand = new Random(13);
		AccumulatingAverage wma = new AccumulatingAverage(0.95);
		for (int i = 0; i < 10000; i++) {
			wma.add(rand.nextDouble() + 0.1, 17.3);
			if (i % 7 == 0) {
				wma.flush();
			}
		}
		assert Numbers.equals(wma.getWrapped().getAverage(), 17.3);
	}

	@Test
	public void test2() {
		double r2 = calc(new MovingAverage(0.9999));
		assert r2 <= 0.05;
	}
	
	public static double calc(IAverage ma){
		Random rand = new Random(13);
		for (int i = 0; i < 100000; i++) {
			ma.add(rand.nextGaussian());
		}
		return Math.abs(1.0 - ma.getVariance());
	}
	
}
