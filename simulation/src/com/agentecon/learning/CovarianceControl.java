/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.learning;

import com.agentecon.util.MovingCovarianceAlt;

public class CovarianceControl implements IControl {

	private MovingCovarianceAlt cov;
	private IBelief belief;

	public CovarianceControl(double start, double memory) {
		this.belief = new ConstantFactorBelief(start, 0.01);
		this.cov = new MovingCovarianceAlt(memory);
	}

	public double getCurrentInput() {
		return belief.getValue();
	}

	public void reportOutput(double output) {
		this.cov.add(getCurrentInput(), output);
		boolean upwards = this.cov.getCovariance() > 0;
		this.belief.adapt(upwards);
	}

}
