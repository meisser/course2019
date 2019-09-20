package com.agentecon.exercises;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.classloader.CompilingClassLoader;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;

public class ExerciseAgentFactory implements IAgentFactory {

	private String classname;
	private RemoteLoader loader;

	public ExerciseAgentFactory(String classname, String owner, String repo) throws SocketTimeoutException, IOException {
		this(classname, owner, repo, "master");
	}

	public ExerciseAgentFactory(String classname, String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		this(classname, new GitSimulationHandle(owner, repo, branch, false));
	}

	public ExerciseAgentFactory(String classname) throws SocketTimeoutException, IOException {
		this(classname, new LocalSimulationHandle(false));
	}

	public ExerciseAgentFactory(String classname, SimulationHandle handle) throws SocketTimeoutException, IOException {
		this.classname = classname;
		RemoteLoader parent = getSimulationLoader();
		if (parent == null) {
			this.loader = new CompilingClassLoader(handle);
		} else {
			this.loader = parent.obtainChildLoader(handle);
		}
	}

	private RemoteLoader getSimulationLoader() {
		ClassLoader loader = getClass().getClassLoader();
		return loader instanceof RemoteLoader ? (RemoteLoader) loader : null;
	}

	public Class<?> preload() throws ClassNotFoundException {
		return loader.loadClass(classname);
	}
	
	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		try {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends IConsumer> clazz = (Class<? extends IConsumer>) loader.loadClass(classname);
				Constructor<? extends IConsumer> constructor = clazz.getConstructor(IAgentIdGenerator.class, int.class, Endowment.class, IUtility.class);
				assert clazz.getClassLoader() == loader;
				return constructor.newInstance(id, maxAge, endowment, utilityFunction);
			} catch (ClassNotFoundException e) {
				System.err.println("Could not load " + classname + " from " + loader + " due to " + e);
				return null;
			} catch (NoSuchMethodException e) {
				return null;
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} catch (RuntimeException e) {
			System.out.println("Could not load consumer from " + this);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		try {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends IConsumer> clazz = (Class<? extends IConsumer>) loader.loadClass(classname);
				Constructor<? extends IConsumer> constructor = clazz.getConstructor(IAgentIdGenerator.class, Endowment.class, IUtility.class);
				assert clazz.getClassLoader() == loader;
				return constructor.newInstance(id, endowment, utilityFunction);
			} catch (ClassNotFoundException e) {
				System.err.println("Could not load " + classname + " from " + loader + " due to " + e);
				return null;
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} catch (RuntimeException e) {
			System.out.println("Could not load consumer from " + this);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public IFirm createFirm(IAgentIdGenerator id, Endowment end) {
		try {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends IFirm> clazz = (Class<? extends IFirm>) loader.loadClass(classname);
				Constructor<? extends IFirm> constructor = clazz.getConstructor(IAgentIdGenerator.class, Endowment.class);
				assert clazz.getClassLoader() == loader;
				return constructor.newInstance(id, end);
			} catch (ClassNotFoundException e) {
				System.err.println("Could not load " + classname + " from " + loader + " due to " + e);
				return null;
			} catch (NoSuchMethodException e) {
				return null;
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} catch (RuntimeException e) {
			System.out.println("Could not load consumer from " + this);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString(){
		return "Agent factory for " + classname + " from " + loader;
	}

}
