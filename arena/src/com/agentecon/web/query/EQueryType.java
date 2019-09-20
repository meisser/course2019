/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.query;

public enum EQueryType {

	FIRMS, CONSUMERS, TYPE, ID;

	public static final String CONSUMERS_QUERY = "consumers";
	public static final String FIRMS_QUERY = "firms";
	public static final String UNKNOWN = "unknown";

	public static EQueryType derive(String agent) {
		switch (agent) {
		case FIRMS_QUERY:
			return EQueryType.FIRMS;
		case CONSUMERS_QUERY:
			return CONSUMERS;
		default:
			if (Character.isDigit(agent.charAt(0))) {
				return ID;
			} else {
				return TYPE;
			}
		}
	}

}