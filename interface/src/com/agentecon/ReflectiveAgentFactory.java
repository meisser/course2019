package com.agentecon;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.production.IProductionFunction;

public class ReflectiveAgentFactory implements IAgentFactory {

	public static final String LOCAL = "local";

	private String classname;
	private ClassLoader loader;

	public ReflectiveAgentFactory(RemoteLoader parent, String team, String name) throws IOException {
		this(parent.obtainChildLoader(team.equals(LOCAL) ? new LocalSimulationHandle(false) : new GitSimulationHandle("meisser", team, false)), name);
	}

	public ReflectiveAgentFactory(ClassLoader loader, String name) {
		this.classname = name;
		this.loader = loader;
	}

	private Class<?> getAgentClass() throws ClassNotFoundException {
		return loader.loadClass(classname);
	}

	public IFirm createFirm(IAgentIdGenerator id, Endowment end) {
		try {
			@SuppressWarnings("unchecked")
			Constructor<? extends IFirm> con = (Constructor<? extends IFirm>) getAgentClass().getConstructor(IAgentIdGenerator.class, Endowment.class);
			return (IFirm) con.newInstance(id, end);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
			System.err.println("Could not create " + classname + " due to " + e);
			return createDefaultFirm(id, end, null, e);
		}
	}

	public IFirm createFirm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		if (prodFun == null) {
			return createFirm(id, end);
		} else {
			try {
				@SuppressWarnings("unchecked")
				Constructor<? extends IFirm> con = (Constructor<? extends IFirm>) getAgentClass().getConstructor(IAgentIdGenerator.class, Endowment.class, IProductionFunction.class);
				return (IFirm) con.newInstance(id, end, prodFun);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
				System.err.println("Could not create " + classname + " due to " + e);
				return createDefaultFirm(id, end, prodFun, e);
			}
		}
	}

	protected IFirm createDefaultFirm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun, Exception e) {
		throw new RuntimeException(e);
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility utilityFunction) {
		try {
			@SuppressWarnings("unchecked")
			Constructor<? extends IConsumer> con = (Constructor<? extends IConsumer>) getAgentClass().getConstructor(IAgentIdGenerator.class, Endowment.class, IUtility.class);
			return (IConsumer) con.newInstance(id, end, utilityFunction);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
			System.err.println("Could not create " + classname + " due to " + e);
			return createDefaultConsumer(id, Integer.MAX_VALUE, end, utilityFunction, e);
		}
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		try {
			@SuppressWarnings("unchecked")
			Constructor<? extends IConsumer> con = (Constructor<? extends IConsumer>) getAgentClass().getConstructor(IAgentIdGenerator.class, int.class, Endowment.class, IUtility.class);
			return (IConsumer) con.newInstance(id, maxAge, endowment, utilityFunction);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
			System.err.println("Could not create " + classname + " due to " + e);
			return createDefaultConsumer(id, maxAge, endowment, utilityFunction, e);
		}
	}

	protected IConsumer createDefaultConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction, Exception e) {
		throw new RuntimeException(e);
	}

}
