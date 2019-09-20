package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.agentecon.agent.Agent;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.consumer.Inheritance;
import com.agentecon.firm.IBank;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Ticker;
import com.agentecon.production.IGoodsTrader;
import com.agentecon.production.IProducer;
import com.agentecon.sim.ISimulationListener;

public class Agents implements IAgents, IAgentIdGenerator {

	private static final boolean SINGLE_HEIR_MODE = true;

	private long seed;
	private Random rand;
	private int agentId;

	private Ticker bank;
	private HashMap<Integer, Agent> all;
	private ArrayList<IConsumer> consumers;
	private HashMap<Ticker, IFirm> firms;
	private ArrayList<IGoodsTrader> producers;
	// private ArrayList<Fundamentalist> fundies;
	private ArrayList<IMarketMaker> marketMakers;
	private ArrayList<IShareholder> shareholders;
	private ArrayList<Inheritance> pendingInheritances;

	private HashSet<String> consumerTypes;
	private HashSet<String> firmTypes;

	private ISimulationListener listeners;

	public Agents(ISimulationListener listeners, long seed, int newAgentId) {
		this.firms = new HashMap<>();
		this.all = new HashMap<>();
		this.consumers = new ArrayList<>();
		this.shareholders = new ArrayList<>();
		this.producers = new ArrayList<>();
		this.marketMakers = new ArrayList<>();
		this.consumerTypes = new HashSet<>();
		this.firmTypes = new HashSet<>();
		this.pendingInheritances = new ArrayList<>();
		this.listeners = listeners;
		this.seed = seed;
		this.agentId = newAgentId;
	}

	@Override
	public Collection<Inheritance> getPendingInheritances() {
		return pendingInheritances;
	}

	public IBank getBank() {
		return bank == null ? null : (IBank) firms.get(bank);
	}

	@Override
	public Collection<? extends IBank> getBanks() {
		if (bank == null) {
			return Collections.emptyList();
		} else {
			return Collections.singleton(getBank());
		}
	}

	public void addInheritance(Inheritance left) {
		this.pendingInheritances.add(left);
		this.shareholders.add(left);
	}

	public Collection<IFirm> getFirms() {
		return firms.values();
	}

	public Collection<IGoodsTrader> getRandomGoodsMarketMakers() {
		Collections.shuffle(producers, getRand());
		return producers;
	}

	public Iterable<IProducer> getProducers() {
		return producers.stream().filter(t -> t instanceof IProducer).map(t -> (IProducer) t)::iterator;
	}

	public double calculateMoneySupply() {
		double money = 0.0;
		for (IAgent agent : all.values()) {
			money += agent.getMoney().getNetAmount();
		}
		double credit = 0.0;
		for (IBank bank : getBanks()) {
			credit += bank.getOutstandingCredit();
		}
		for (Inheritance inh : pendingInheritances) {
			money += inh.getMoney().getNetAmount();
		}
		return money - credit;
	}

	public Collection<IConsumer> getConsumers() {
		return consumers;
	}

	public Collection<IMarketMaker> getRandomMarketMakers() {
		Collections.shuffle(marketMakers, getRand());
		return marketMakers;
	}

	public IFirm getCompany(Ticker ticker) {
		return firms.get(ticker);
	}

	public void add(Agent agent) {
		include(agent, true);
	}

	private void include(Agent agent, boolean newAgent) {
		Agent prev = all.put(agent.getAgentId(), agent);
		assert prev == null : "Cannot register " + agent + " because there already is another agent with that id: " + prev;
		if (agent instanceof IFirm) {
			IFirm firm = (IFirm) agent;
			IFirm prevFirm = firms.put(firm.getTicker(), firm);
			assert prevFirm == null : "firms must have unique tickers";
			firmTypes.add(firm.getType());
			if (newAgent) {
				for (IMarketMaker mm : marketMakers) {
					mm.notifyFirmCreated(firm);
				}
			}
			if (agent instanceof IBank) {
				bank = firm.getTicker();
			}
		}
		if (agent instanceof IShareholder) {
			shareholders.add((IShareholder) agent);
		}
		if (agent instanceof IMarketMaker) {
			if (newAgent) {
				IMarketMaker mm = (IMarketMaker) agent;
				for (IFirm firm : firms.values()) {
					if (firm != mm) {
						mm.notifyFirmCreated(firm);
					}
				}
			}
			marketMakers.add((IMarketMaker) agent);
		}
		if (agent instanceof IGoodsTrader) {
			producers.add((IGoodsTrader) agent);
		}
		if (agent instanceof IConsumer) {
			IConsumer consumer = (IConsumer) agent;
			consumers.add(consumer);
			consumerTypes.add(agent.getType());
			if (newAgent && !pendingInheritances.isEmpty()) {
				consumer.inherit(pendingInheritances.remove(0));
			}
		}
		if (listeners != null && newAgent) {
			listeners.notifyAgentCreated(agent);
		}
	}

	public Collection<IMarketParticipant> getRandomMarketParticipants() {
		ArrayList<IMarketParticipant> parts = new ArrayList<>(all.size());
		for (IAgent a : all.values()) {
			if (a instanceof IMarketParticipant) {
				parts.add((IMarketParticipant) a);
			}
		}
		Collections.shuffle(parts, getRand());
		return parts;
	}

	public Collection<? extends IAgent> getAgents() {
		return all.values();
	}

	@SuppressWarnings("unchecked")
	public Collection<IConsumer> getRandomConsumers(int cardinality) {
		Collections.shuffle(consumers, getRand()); // OPTIMIZABLE in case of cardinality < size
		if (cardinality == -1 || cardinality >= consumers.size()) {
			return (Collection<IConsumer>) consumers.clone();
		} else {
			return consumers.subList(0, cardinality);
		}
	}

	public IConsumer getRandomConsumer() {
		return consumers.get(getRand().nextInt(consumers.size()));
	}

	public Random getRand() {
		if (rand == null) {
			rand = new Random(seed);
		}
		return rand;
	}

	@SuppressWarnings("unchecked")
	public Collection<IShareholder> getRandomShareholders() {
		Collections.shuffle(shareholders, getRand());
		return (Collection<IShareholder>) shareholders.clone();
	}

	public Agents renew(long seed) {
		Agents copy = new Agents(listeners, seed, agentId);
		for (Agent a : all.values()) {
			if (a.isAlive()) {
				copy.include(a, false);
			} else {
				listeners.notifyAgentDied(a);
			}
		}
		for (Inheritance inh : pendingInheritances) {
			copy.distribute(inh);
		}
		return copy;
	}

	private void distribute(Inheritance inheritance) {
		if (SINGLE_HEIR_MODE) {
			this.pendingInheritances.add(inheritance);
			this.shareholders.add(inheritance);
		} else {
			double sharePerConsumer = 1.0 / consumers.size();
			for (IConsumer consumer : consumers) {
				consumer.inherit(inheritance.getFraction(sharePerConsumer));
			}
			consumers.iterator().next().inherit(inheritance);
		}
	}

	public Agents duplicate() {
		long seed = getCurrentSeed();
		assert rand == null;
		Agents duplicate = new Agents(listeners, seed, agentId);
		assert false : "TODO: implement inheritance cloning";
		duplicate.pendingInheritances = (ArrayList<Inheritance>) pendingInheritances.clone();
		for (Agent a : all.values()) {
			duplicate.include(a.clone(), false);
		}
		return duplicate;
	}

	private long getCurrentSeed() {
		if (rand != null) {
			seed = rand.nextLong();
			rand = null;
		}
		return seed;
	}

	public void refreshReferences() {
		for (Agent a : all.values()) {
			a.refreshRef();
		}
	}

	public Collection<IMarketMaker> getAllMarketMakers() {
		return marketMakers;
	}

	@Override
	public Collection<? extends IShareholder> getShareholders() {
		return shareholders;
	}

	@Override
	public IFirm getFirm(Ticker ticker) {
		return firms.get(ticker);
	}

	@Override
	public IAgent getAgent(int agentId) {
		return all.get(agentId);
	}

	@Override
	public Set<String> getFirmTypes() {
		return firmTypes;
	}

	@Override
	public Set<String> getConsumerTypes() {
		return consumerTypes;
	}

	@Override
	public Collection<IAgent> getAgents(String type) {
		if (consumerTypes.contains(type)) {
			return extract(getConsumers(), type);
		} else if (firmTypes.contains(type)) {
			return extract(getFirms(), type);
		} else {
			return Collections.emptyList();
		}
	}

	private Collection<IAgent> extract(Collection<? extends IAgent> agents, String type) {
		ArrayList<IAgent> matches = new ArrayList<>();
		for (IAgent candidate : agents) {
			if (candidate.getType().equals(type)) {
				matches.add(candidate);
			}
		}
		return matches;
	}

	@Override
	public int previewNextId() {
		return agentId;
	}

	@Override
	public int createUniqueAgentId() {
		return agentId++;
	}

	@Override
	public String toString() {
		return all.size() + " agents out of which " + consumers.size() + " are consumers and " + firms.size() + " firms.";
	}

}
