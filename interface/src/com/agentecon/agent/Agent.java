package com.agentecon.agent;

import java.net.URL;

import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;

public abstract class Agent implements IAgent, Cloneable {

	private final int number;
	private final String type;
	private final Endowment end;
	private Inventory inv;
	private int age;

	private AgentRef ref;

	public Agent(IAgentIdGenerator agentRegistry, Endowment end) {
		this.inv = end.getInitialInventory();
		this.number = agentRegistry.createUniqueAgentId();
		this.type = inferType(getClass());
		this.end = end;
		this.age = 1;
		this.ref = new AgentRef(this);
		assert type != null;
	}

	protected String inferType(Class<? extends Agent> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		if (loader instanceof RemoteLoader) {
			return ((RemoteLoader) loader).getOwner() + "-" + findType(clazz);
		} else {
			return findType(clazz);
		}
	}
	
	public String getVersion() {
		ClassLoader loader = getClass().getClassLoader();
		if (loader instanceof RemoteLoader) {
			return ((RemoteLoader) loader).getVersionString();
		} else {
			return "Local";
		}
	}

	public URL getSourceUrl() {
		ClassLoader loader = getClass().getClassLoader();
		if (loader instanceof RemoteLoader) {
			SimulationHandle source = ((RemoteLoader) loader).getSource();
			return source.getBrowsableURL(getClass().getName());
		} else {
			return null;
		}
	}

	public static String findAuthor(Class<? extends IAgent> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		if (loader instanceof RemoteLoader) {
			return ((RemoteLoader) loader).getOwner();
		} else {
			return "Local";
		}
	}

	public static String findType(Class<?> clazz) {
		String name = clazz.getSimpleName();
		while (name.length() == 0) {
			clazz = clazz.getSuperclass();
			name = clazz.getSimpleName();
		}
		return name;
	}

	public void addListener(Object listener) {
		if (listener instanceof IConsumerListener && this instanceof IConsumer) {
			((IConsumer) this).addListener((IConsumerListener) listener);
		} else {
			((IFirm) this).addFirmMonitor((IFirmListener) listener);
			if (listener instanceof IProducerListener && this instanceof IProducer) {
				((IProducer) this).addProducerMonitor((IProducerListener) listener);
			}
		}
	}

	public AgentRef getReference() {
		return ref;
	}

	public boolean isAlive() {
		return age > 0;
	}

	public void age() {
		this.age++;
	}

	public int getAge() {
		if (age < 0) {
			return -age;
		} else {
			return age;
		}
	}

	public String getName() {
		return getType() + " " + number;
	}

	public String getType() {
		return type;
	}

	public final Inventory getInventory() {
		return inv;
	}

	public int getAgentId() {
		assert this.number > 0;
		return number;
	}

	public Inventory dispose() {
		assert isAlive();
		assert age > 0;
		age = -age;
		Inventory old = this.inv;
		Inventory newInventory = new Inventory(old.getMoney().getGood());
		newInventory.absorb(old);
		return newInventory;
	}

	protected final IStock getStock(Good good) {
		return inv.getStock(good);
	}

	public final IStock getMoney() {
		return inv.getMoney();
	}

	public final double getDailyEndowment(Good good) {
		for (IStock stock : end.getDaily()) {
			if (stock.getGood().equals(good)) {
				return stock.getAmount();
			}
		}
		return 0.0;
	}

	public final void collectDailyEndowment() {
		assert isAlive();
		inv.deprecate();
		inv.receive(end.getDaily());
	}

	@Override
	public int hashCode() {
		return number;
	}

	@Override
	public boolean equals(Object o) {
		return ((Agent) o).number == number;
	}

	@Override
	public Agent clone() {
		try {
			Agent klon = (Agent) super.clone();
			klon.inv = inv.duplicate();
			return klon;
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public void refreshRef() {
		this.ref.set(this);
	}

	@Override
	public String toString() {
		return getType() + " with " + inv;
	}

}
