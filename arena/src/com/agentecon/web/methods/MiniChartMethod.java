package com.agentecon.web.methods;

import java.io.IOException;

import com.agentecon.metric.minichart.MiniChart;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.query.AgentQuery;

public class MiniChartMethod extends SimSpecificMethod {

	private static final String KEY_PREFIX = "MiniChart-";
	private static final String HEIGHT_PARAMETER = "height";

	public MiniChartMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "&" + AgentQuery.getExample() + "&" + HEIGHT_PARAMETER + "=300";
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException {
		SimulationStepper stepper = getSimulation(params);
		String cacheKey = KEY_PREFIX + params.getSelectionString();
		MiniChart cached = (MiniChart) stepper.getCachedItem(cacheKey);
		if (cached == null) {
			MiniChart created = MiniChart.create(params.getSelection());
			JsonData data = created.getData(params.getDay(), stepper, params.getIntParam(HEIGHT_PARAMETER, 200));
			stepper.putCached(cacheKey, created);
			return data;
		} else {
			JsonData cachedData = cached.getData(params.getDay(), stepper, params.getIntParam(HEIGHT_PARAMETER, 200));
//			JsonData createdData = created.getData(params.getDay(), stepper, params.getIntParam(HEIGHT_PARAMETER, 200));
//			assert cachedData.equals(createdData);
			return cachedData;
		}
	}

}
