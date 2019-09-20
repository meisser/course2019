/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.agentecon.ISimulation;
import com.agentecon.metric.EMetrics;
import com.agentecon.metric.NoInterestingTimeSeriesFoundException;
import com.agentecon.metric.SimStats;
import com.agentecon.runner.SimulationStepper;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class DownloadCSVMethod extends SimSpecificMethod {

	private static final String KEY_PREFIX = "CSV-";

	public static final String CHOICE_PARAMETER = "metric";

	public DownloadCSVMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		String superSample = super.createExamplePath();
		int dayIndex = superSample.indexOf(Parameters.DAY);
		if (dayIndex >= 0) {
			superSample = superSample.substring(0, dayIndex);
		}
		return superSample + CHOICE_PARAMETER + "=" + EMetrics.PRODUCTION.getName();
	}

	private String getCacheKey(EMetrics metric) {
		return KEY_PREFIX + metric;
	}

	@Override
	public Response execute(IHTTPSession session, Parameters params) throws IOException {
		EMetrics metric = EMetrics.parse(params.getParam(CHOICE_PARAMETER));
		SimulationStepper stepper = getSimulation(params);
		byte[] rawData = (byte[]) stepper.getCachedItem(getCacheKey(metric));
		if (rawData == null) {
			ISimulation sim = stepper.getSimulation(0).getItem();
			SimStats stats = metric.createAndRegister(sim, params.getSelection(), true);
			sim.run();
			ByteArrayOutputStream csvData = new ByteArrayOutputStream();
			try (PrintStream writer = new PrintStream(csvData)) {
				try {
					stats.print(writer, ", ");
				} catch (NoInterestingTimeSeriesFoundException e) {
					// send empty file
					writer.println("No interesting data found.");
					writer.println("Maybe the chosen metric is not relevant in this simulation, e.g. asking for stock market statistics in a simulation without stock market?");
					writer.println("Or the simulation might be disfunctional, e.g. no trade taking place.");
				}
			}
			rawData = csvData.toByteArray();
			stepper.putCached(getCacheKey(metric), rawData);
		}
		Response resp = NanoHTTPD.newFixedLengthResponse(Status.OK, "text/csv", new ByteArrayInputStream(rawData), rawData.length);
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String simName = params.getSimulation();
		resp.addHeader("Content-Disposition", "inline; filename=\"" + dateString + " " + simName + " " + metric.getName() + ".csv\"");
		return resp;
	}

}
