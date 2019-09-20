package com.agentecon.web;

import java.io.IOException;
import java.util.Collection;
import java.util.StringTokenizer;

import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.web.methods.AgentsMethod;
import com.agentecon.web.methods.ChartMethod;
import com.agentecon.web.methods.ChildrenMethod;
import com.agentecon.web.methods.DownloadCSVMethod;
import com.agentecon.web.methods.FirmRankingMethod;
import com.agentecon.web.methods.GithubeventMethod;
import com.agentecon.web.methods.InfoMethod;
import com.agentecon.web.methods.ListMethod;
import com.agentecon.web.methods.MethodsMethod;
import com.agentecon.web.methods.MetricsMethod;
import com.agentecon.web.methods.MiniChartMethod;
import com.agentecon.web.methods.Parameters;
import com.agentecon.web.methods.RankingMethod;
import com.agentecon.web.methods.SizeTypesMethod;
import com.agentecon.web.methods.TradeGraphMethod;
import com.agentecon.web.methods.WebApiMethod;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class SimulationServer extends VisServer {

	private MethodsMethod methods;
	private ListMethod simulations;
	private Refresher refresher;

	public SimulationServer(int port) throws IOException, InterruptedException {
		super(port);

		this.refresher = new Refresher(20);
		this.simulations = new ListMethod(refresher);
		if (SimulationConfig.isServerConfig()) {
			try {
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "master", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex1-hermit-3", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex2-farmer-3", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex3-money-basic-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex3-money-interest-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex3-money-helicopter-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex3-money-buffer-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex4-growth-2", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex5-stocks-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex5-stocks-discount0995-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex5-stocks-discount099-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex6-flow-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex7-equality-1", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex9-fund-6", true));
				this.simulations.add(new GitSimulationHandle("meisser", "course2018", "ex9-fund-lowinterest-8", true));
			} catch (IOException e) {
				System.out.println("Disabled remote repositories. " + e.getMessage());
			}
		} else {
			this.simulations.add(new LocalSimulationHandle());
			if (SimulationConfig.shouldLoadRemoteTeams()) {
				// only start local reloader if we don't have remote teams in order to save github api calls
				// note that we cannot refresh the main simulation alone, as the classes loaded by the sub class
				// loaders still refer to classes loaded by the main one
			} else {
				new LocalSimulationUpdater(this.simulations).start();
			}
		}

		this.methods = new MethodsMethod();
		this.methods.add(this.simulations);
		this.methods.add(new SizeTypesMethod());
		this.methods.add(new MetricsMethod());
		this.methods.add(new GithubeventMethod(this.simulations));
		this.methods.add(new InfoMethod(this.simulations));
		this.methods.add(new AgentsMethod(this.simulations));
		this.methods.add(new TradeGraphMethod(this.simulations));
		this.methods.add(new ChildrenMethod(this.simulations));
		this.methods.add(new RankingMethod(this.simulations));
		this.methods.add(new FirmRankingMethod(this.simulations));
		this.methods.add(new ChartMethod(this.simulations));
		this.methods.add(new DownloadCSVMethod(this.simulations));
		this.methods.add(new MiniChartMethod(this.simulations));
	}

	@Override
	public Response serve(IHTTPSession session) {
		// Method method = session.getMethod();
		// assert method == Method.GET : "Received a " + method;
		try {
			String uri = session.getUri();
			Response res = createResponse(session, uri);
			res.addHeader("Access-Control-Allow-Origin", "*");
			return res;
		} catch (InterruptedException e) {
			return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), "Operation Interrupted");
		}
	}

	private Response createResponse(IHTTPSession session, String uri) throws InterruptedException {
		StringTokenizer tok = new StringTokenizer(uri, "\\/");
		if (tok.hasMoreTokens()) {
			try {
				String methodName = tok.nextToken();
				WebApiMethod calledMethod = methods.getMethod(methodName);
				if (calledMethod != null) {
					refresher.notifyCalled(calledMethod, new Parameters(session));
					return calledMethod.execute(session);
				} else {
					return super.serve(session);
				}
			} catch (RuntimeException e) {
				String msg = "Failed to handle call due to " + e.toString();
				e.printStackTrace();
				System.out.println(msg);
				return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), msg);
			} catch (IOException e) {
				String msg = "Failed to handle call due to " + e.toString();
				System.out.println(msg);
				return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), msg);
			}
		} else {
			return super.serve(session);
		}
	}

	@Override
	protected String getStartPath() {
		Collection<String> sims = simulations.getSimulations();
		if (sims.size() == 1) {
			return "/vis/simulation?sim=" + sims.iterator().next();
		} else {
			return super.getStartPath();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		SimulationServer server = new SimulationServer(8080);
		server.run();
	}

}
