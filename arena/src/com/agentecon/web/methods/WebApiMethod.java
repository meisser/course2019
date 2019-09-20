/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;

import com.agentecon.util.LogClock;
import com.agentecon.web.data.JsonData;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public abstract class WebApiMethod {

	public final static String PLACE_HOLDER = "#NAME";

	public String name;
	public String examplePath;

	public WebApiMethod() {
		this.name = deriveName();
		this.examplePath = createExamplePath();
	}
	
	public Descriptor getDescriptor() {
		return new Descriptor(name, examplePath);
	}

	protected String deriveName() {
		String className = getClass().getSimpleName();
		return className.substring(0, className.length() - "Method".length()).toLowerCase();
	}

	protected String createExamplePath() {
		return getName();
	}

	public final String getName() {
		return name;
	}

	public final Response execute(IHTTPSession session) throws IOException, InterruptedException {
		LogClock clock = new LogClock();
		Parameters params = new Parameters(session);
		try {
			return execute(session, params);
		} finally {
			clock.time("Executed " + session.getUri() + "?" + session.getQueryParameterString());
		}
	}

	public Response execute(IHTTPSession session, Parameters params) throws IOException, InterruptedException {
		JsonData answer = getJsonAnswer(params);
		return NanoHTTPD.newFixedLengthResponse(Status.OK, "application/json", answer.getJson());
	}
	
	/**
	 * Dummy execution of this method to refresh the cache
	 * @throws InterruptedException 
	 */
	public void refreshCache(Parameters params) throws InterruptedException {
		try {
			getJsonAnswer(params);
		} catch (IOException e) {
		}
	}

	protected JsonData getJsonAnswer(Parameters params) throws IOException, InterruptedException {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
