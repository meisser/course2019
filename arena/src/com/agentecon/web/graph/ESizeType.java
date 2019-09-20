/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.agentecon.ISimulation;
import com.agentecon.web.query.AgentQuery;

public enum ESizeType {

	UTILITY(true, false), CASH(true, true), WEALTH(true, true), PROFITS(false, true), MARKET_CAP(false, true);

	public static final ESizeType DEFAULT_CONSUMER_TYPE = UTILITY;
	public static final ESizeType DEFAULT_FIRM_TYPE = WEALTH;

	private boolean consumer;
	private boolean firm;

	private ESizeType(boolean consumer, boolean firm) {
		this.consumer = consumer;
		this.firm = firm;
	}

	public static Collection<String> getConsumerTypes() {
		return createList(t -> t.consumer);
	}

	public static Collection<String> getFirmTypes() {
		return createList(t -> t.firm);
	}

	public static Collection<String> createList(Predicate<ESizeType> pred) {
		List<String> list = Arrays.asList(ESizeType.values()).stream().filter(pred).map(t -> t.name()).collect(Collectors.toList());
		Collections.sort(list);
		return list;
	}

	public AgentSize createQuery(AgentQuery selection, ISimulation agents) {
		switch (this) {
		case UTILITY:
			return new ConsumerMeter(selection, agents);
		case WEALTH:
			return new WealthMeter();
		default:
			throw new RuntimeException("Not implemented");
		}
	}

}
