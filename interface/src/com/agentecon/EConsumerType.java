/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon;

public enum EConsumerType {

	HERMIT, FARMER;

	public String getConsumerClassName() {
		return getExercisePath(ordinal() + 1) + getMixedCaseName();
	}

	private String getMixedCaseName() {
		String name = name();
		return name.substring(0, 1) + name.substring(1).toLowerCase();
	}

	private String getExercisePath(int i) {
		return "com.agentecon.exercise" + i + ".";
	}
	
}
