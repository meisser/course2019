/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.production.IProductionFunction;

public class AgentFactoryMultiplex implements IAgentFactory {

	private int current;
	private IAgentFactory defaultFactory;
	private ArrayList<IAgentFactory> factories;

	public AgentFactoryMultiplex(IAgentFactory defaultFactory) {
		assert defaultFactory != null;
		this.defaultFactory = defaultFactory;
		this.current = 0;
		this.factories = new ArrayList<>();
	}

	protected IAgentFactory getDefaultFactory() {
		return defaultFactory;
	}

	public AgentFactoryMultiplex(Class<? extends IConsumer>[] agents) {
		this(new IAgentFactory() {

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
				return new Consumer(id, endowment, utilityFunction);
			}
		});
		for (int i = 0; i < agents.length; i++) {
			final Class<? extends IConsumer> current = agents[i];
			addFactory(new IAgentFactory() {

				@Override
				public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
					try {
						Constructor<? extends IConsumer> constructor = current.getConstructor(IAgentIdGenerator.class, int.class, Endowment.class, IUtility.class);
						return constructor.newInstance(id, maxAge, endowment, utilityFunction);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						System.err.println("Could not instantiate agent " + current + " due to " + e);
						return null;
					}
				}

				@Override
				public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
					try {
						return current.getConstructor(IAgentIdGenerator.class, Endowment.class, IUtility.class).newInstance(id, endowment, utilityFunction);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						System.err.println("Could not instantiate agent " + current + " due to " + e);
						return null;
					}
				}
			});
		}
	}

	protected void addFactory(IAgentFactory factory) {
		factories.add(factory);
	}

	private IAgentFactory getCurrent() {
		if (factories.isEmpty()) {
			return getDefaultFactory();
		} else {
			return factories.get(current++ % factories.size());
		}
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		IAgentFactory current = getCurrent();
		IConsumer consumer = current.createConsumer(id, maxAge, endowment, utilityFunction);
		return consumer == null ? getDefaultFactory().createConsumer(id, maxAge, endowment, utilityFunction) : consumer;
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		IAgentFactory current = getCurrent();
		IConsumer consumer = current.createConsumer(id, endowment, utilityFunction);
		return consumer == null ? getDefaultFactory().createConsumer(id, endowment, utilityFunction) : consumer;
	}

	@Override
	public IFirm createFirm(IAgentIdGenerator id, Endowment end) {
		IAgentFactory current = getCurrent();
		IFirm firm = current.createFirm(id, end);
		return firm == null ? getDefaultFactory().createFirm(id, end) : firm;
	}

	@Override
	public IFirm createFirm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		IAgentFactory current = getCurrent();
		IFirm firm = current.createFirm(id, end, prodFun);
		return firm == null ? getDefaultFactory().createFirm(id, end, prodFun) : firm;
	}

}
