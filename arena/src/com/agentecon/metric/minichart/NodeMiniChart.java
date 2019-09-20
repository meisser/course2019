package com.agentecon.metric.minichart;

import java.io.IOException;
import java.util.function.Consumer;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.runner.Recyclable;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.util.Average;
import com.agentecon.web.query.AgentQuery;
import com.agentecon.web.query.ENodeType;

public class NodeMiniChart extends MiniChart {

	private AgentQuery agent;
	private ENodeType type = ENodeType.UNKNOWN;

	public NodeMiniChart(AgentQuery agentQuery) {
		this.agent = agentQuery;
	}

	protected String getName() {
		return type == ENodeType.CONSUMER ? "Utility of " + agent : "Dividends of " + agent;
	}

	@Override
	protected float getData(SimulationStepper stepper, int day) throws IOException {
		final Average average = new Average();
		Recyclable<ISimulation> simulation = stepper.getSimulation(day);
		try {
			agent.forEach(simulation.getItem().getAgents(), new Consumer<IAgent>() {

				@Override
				public void accept(IAgent t) {
					if (t instanceof IConsumer) {
						average.add(((IConsumer) t).getUtilityFunction().getLatestExperiencedUtility());
						type = ENodeType.CONSUMER;
					} else if (t instanceof IFirm){
						average.add(((IFirm) t).getShareRegister().getAverageDividend());
						type = ENodeType.FIRM;
					}
				}
			});
			return (float) average.getAverage();
		} finally {
			simulation.recycle();
		}
	}

}
