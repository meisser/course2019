/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.learning;

import java.util.Random;

/**
 * Based on y = a*x*x + b*x + c
 * 
 * Should be equivalent to recursive least squares, which is described here: https://en.wikipedia.org/wiki/Online_machine_learning
 */
public class RecursiveLeastSquares {

	private int step;
	private double memory;
	protected Matrix weights;
	private Matrix covarianceMatrix;

	public RecursiveLeastSquares(double memory, int parameters) {
		this.covarianceMatrix = new Matrix(parameters, true);
		this.weights = new Matrix(1, parameters);
		this.memory = memory;
		this.step = 1;
	}

	public void update(Matrix x, double y) {
		// Algorithm taken from "Recursive Least Squares with Forgetting for Online Estimation of Vehicle Mass and Road Grade:
		// Theory and Experiments", https://pdfs.semanticscholar.org/80eb/236ec16f66e4ce167b2bb0c9804385b03c7f.pdf, page 9
		
		double memory = Math.min(this.memory, Math.max(0.1, 1.0 - 1.0/step++));
		
		Matrix xT = x.transpose();
		
//		System.out.println(xT + "\t" + weights.transpose() + "\t" + xT.multiply(weights).getSingleValue() + "\t" + y);
		
		Matrix numerator = this.covarianceMatrix.multiply(x);
		double divisor = memory + xT.multiply(this.covarianceMatrix).multiply(x).getSingleValue();
		Matrix gain = numerator.multiply(1/divisor);
		
		double error = y - xT.multiply(weights).getSingleValue();
		Matrix adjustment = gain.multiply(error);
		this.weights = this.weights.add(adjustment);
		
		Matrix identityMatrix = new Matrix(gain.getHeight(), true);
		this.covarianceMatrix = identityMatrix.subtract(gain.multiply(xT)).multiply(this.covarianceMatrix).multiply(1/memory);
	}
	
	public static void main(String[] args) {
		RecursiveLeastSquares rls = new RecursiveLeastSquares(0.99, 3);
		Random rand = new Random(1313);
		for (int i=0; i<1000; i++){
			double latest = rand.nextDouble() * 10;
			double output = latest * latest + 3*latest + 10;
			rls.update(new Matrix(1.0, latest, latest*latest), output);
		}
		
	}

}
