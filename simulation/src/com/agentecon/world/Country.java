package com.agentecon.world;

import java.util.Random;

import com.agentecon.agent.Agent;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.Inheritance;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IStatistics;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.util.Average;

public class Country implements ICountry {

	private int day;
	private Good money;
	private Random rand;
	private Average utility;
	private Agents agents, backup;
	private long randomBaseSeed;
	private IInnovation innovation;
	private SimulationListeners listeners;

	public Country(SimulationConfig config, SimulationListeners listeners) {
		this.money = config.getMoney();
		this.listeners = listeners;
		this.utility = new Average();
		this.randomBaseSeed = config.getSeed() + 123123453;
		this.rand = new Random(config.getSeed());
		this.agents = new Agents(listeners, rand.nextLong(), 1);
		this.innovation = config.getInnovation();
	}

	public Good getMoney() {
		return money;
	}

	public void handoutEndowments() {
		for (IConsumer c : agents.getConsumers()) {
			c.collectDailyEndowment();
		}
	}

	public void prepareDay(IStatistics stats) {
		this.day = stats.getDay();
		// reset random every day to get more consistent results on small
		// changes
		this.rand = new Random(day ^ randomBaseSeed);
		this.agents = this.agents.renew(rand.nextLong());
		this.handoutEndowments();
		this.createFirms(stats);
		this.listeners.notifyDayStarted(day);
	}

	private void dismantleFirms(IStatistics stats) {
		for (IFirm firm : agents.getFirms()) {
			if (firm.considerBankruptcy(stats)) {
				Inventory inv = new Inventory(getMoney());
				Portfolio port = new Portfolio(inv.getMoney(), false);
				double totalshares = firm.dispose(inv, port);
				IShareholder last = null;
				for (IShareholder shareholder : agents.getShareholders()) {
					double shares = shareholder.notifyFirmClosed(firm.getTicker());
					if (shares > 0.0) {
						last = shareholder;
						double ratio = Math.min(shares / totalshares, 1.0);
						shareholder.getInventory().absorb(ratio, inv);
						shareholder.getPortfolio().absorbPositions(ratio, port);
						totalshares -= shares;
					}
				}
				if (last != null) {
					last.getInventory().absorb(inv);
					last.getPortfolio().absorb(port);
				} else {
					// TEMP
				}
				port.dispose();
			}
		}
	}

	private void createFirms(IStatistics stats) {
		for (IShareholder shareholder : agents.getShareholders()) {
			if (shareholder instanceof IFounder) {
				IFounder founder = (IFounder) shareholder;
				IFirm firm = founder.considerCreatingFirm(stats, innovation, this);
				if (firm != null) {
					add(firm);
				}
			}
		}
	}

	@Override
	public Random getRand() {
		return rand;
	}

	@Override
	public int getDay() {
		return day;
	}

	public void finishDay(IStatistics stats) {
		consume();

		dismantleFirms(stats);

		handleDeath();

		listeners.notifyDayEnded(stats);
	}

	protected void consume() {
		utility = new Average();
		for (IConsumer c : agents.getConsumers()) {
			assert c.isAlive();
			double util = c.consume();
			utility.add(util);
		}
	}

	protected void handleDeath() {
		for (IConsumer c : agents.getConsumers()) {
			assert c.isAlive();
			Inheritance left = c.considerDeath();
			if (left != null) {
				agents.addInheritance(left);
			}
		}
	}

	// protected void handleDeath() {
	// IStock inheritedMoney = new Stock(money);
	// Portfolio inheritance = new Portfolio(inheritedMoney);
	// Collection<IConsumer> consumers = agents.getConsumers();
	// Iterator<IConsumer> iter = consumers.iterator();
	// utility = new Average();
	// while (iter.hasNext()) {
	// IConsumer c = iter.next();
	// assert c.isAlive();
	// double util = c.consume();
	// utility.add(util);
	// c.considerDeath(inheritance);
	// }
	// for (Position pos : inheritance.getPositions()) {
	// agents.getCompany(pos.getTicker()).inherit(pos);
	// }
	// if (inheritedMoney.getAmount() > 0) {
	// agents.getRandomConsumer().getMoney().absorb(inheritedMoney);
	// }
	// }

	public void startTransaction() {
		this.backup = agents.duplicate();
	}

	public void commitTransaction() {
		this.backup = null;
	}

	public void abortTransaction() {
		assert backup != null;
		this.agents = backup;
		this.agents.refreshReferences();
	}

	public Agents getAgents() {
		return agents;
	}

	public void add(IAgent agent) {
		agents.add((Agent) agent);
	}

	public String toString() {
		return "World at day " + day + " with " + agents.getAgents().size() + " agents";
	}

	@Override
	public int previewNextId() {
		return agents.previewNextId();
	}
	
	@Override
	public int createUniqueAgentId() {
		return agents.createUniqueAgentId();
	}

	public Average getAverageUtility() {
		return utility;
	}

}
