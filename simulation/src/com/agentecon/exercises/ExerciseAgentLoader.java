/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercises;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.configuration.AgentFactoryMultiplex;
import com.agentecon.configuration.FundConfiguration;
import com.agentecon.configuration.InvalidAgentException;
import com.agentecon.goods.Good;
import com.agentecon.goods.Stock;
import com.agentecon.sim.SimulationConfig;

public class ExerciseAgentLoader extends AgentFactoryMultiplex {

	public static final String DEFAULT_REPO = "course2019";

	public static final Collection<String> TEAMS = createRepos(2, 1, 2, 3, 5, 7, 8, 10);

	public ExerciseAgentLoader(String classname) throws SocketTimeoutException, IOException {
		this(classname, SimulationConfig.shouldLoadRemoteTeams());
	}

	public ExerciseAgentLoader(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		super(remoteTeams ? new ExerciseAgentFactory(classname, "meisser", DEFAULT_REPO) : new ExerciseAgentFactory(classname));
		if (remoteTeams) {
			addFactories(classname, remoteTeams);
		}
	}

	private static Collection<String> createRepos(int year, int... teams) {
		ArrayList<String> repos = new ArrayList<>();
		for (int i : teams) {
			String current = Integer.toString(i);
			if (current.length() == 1) {
				current = "0" + current;
			}
			repos.add("team" + year + current);
		}
		return repos;
	}

	private void addFactories(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		Stream<IAgentFactory> stream = TEAMS.parallelStream().map(team -> {
			return createFactory(classname, team);
		}).filter(factory -> factory != null);
		for (IAgentFactory factory : stream.collect(Collectors.toList())) {
			super.addFactory(factory);
		}
	}

	protected IAgentFactory createFactory(String classname, String team) {
		try {
			ExerciseAgentFactory factory = new ExerciseAgentFactory(classname, new GitSimulationHandle("meisser", team, false));
			try {
				Class<?> clazz = factory.preload();
				check(clazz);
				return factory;
			} catch (ClassNotFoundException | InvalidAgentException e) {
				System.err.println("Could not load agent from " + factory + " due to " + e);
				System.err.println("Falling back to default factory for team " + team);
				return getDefaultFactory();
			}
		} catch (IOException e) {
			System.err.println("Could not load agent factory for team " + team + ", falling back to default factory. Reason: " + e);
			return getDefaultFactory();
		}
	}

	protected void check(Class<?> clazz) throws InvalidAgentException {
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		ExerciseAgentLoader loader = new ExerciseAgentLoader(FundConfiguration.LEV_FUND, true);
		IAgentIdGenerator id = new IAgentIdGenerator() {
			@Override
			public int previewNextId() {
				return 1;
			}

			@Override
			public int createUniqueAgentId() {
				return 1;
			}

			@Override
			public Random getRand() {
				return new Random();
			}
		};
		for (int i = 0; i < 6; i++) {
			loader.createFirm(id, new Stock(new Good("Taler")));
		}

	}

}
