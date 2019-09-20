package com.agentecon.util;

import java.util.Random;

import org.junit.Test;

public class CovarianceTest {
	
	@Test
	public void test(){
		double memory = 0.999;
		MovingCovariance movcov = new MovingCovariance(memory);
		MovingCovarianceAlt movcov2 = new MovingCovarianceAlt(memory);
		Random rand = new Random();
		int len = 10000;
		double[] xs = new double[len];
		double[] ys = new double[len];
		for (int i=0; i<len; i++){
			xs[i] = rand.nextDouble();
			ys[i] = rand.nextDouble() * 100;
			movcov.add(xs[i], ys[i]);
			movcov2.add(xs[i], ys[i]);
		}
		double cov = cov(xs, ys, memory);
		System.out.println(cov/Math.sqrt(cov(xs, xs, memory) * cov(ys, ys, memory)));
		System.out.println(movcov.getCorrelation());
		System.out.println(movcov2.getCorrelation());
	}

	private double avg(double[] xs, double memory) {
		double sum = 0.0;
		for (double x: xs){
			sum = memory * sum + x * (1.0 - memory);
		}
		return sum;
	}
	
	private double cov(double[] xs, double[] ys, double memory){
		double avgx = avg(xs, memory);
		double avgy = avg(ys, memory);
		double cov = 0.0;
		for (int i=0; i<xs.length; i++){
			cov = memory * cov + (xs[i] - avgx)*(ys[i] - avgy) * (1.0 - memory);
		}
		return cov;
	}

}
