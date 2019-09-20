// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.classloader;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.function.BiConsumer;

public class CompilingClassLoader extends RemoteLoader {

	private AgentCompiler compiler;
	
	public CompilingClassLoader(SimulationHandle source) throws SocketTimeoutException, IOException {
		super(CompilingClassLoader.class.getClassLoader(), source);
		assert !(getParent() instanceof RemoteLoader) : "If parent is a remote loader, you should call the other constructor";
		this.compiler = new AgentCompiler(null, source);
	}

	CompilingClassLoader(RemoteLoader parent, SimulationHandle source, boolean dummy) throws SocketTimeoutException, IOException {
		super(parent, source);
		this.compiler = new AgentCompiler(parent, source);
	}

	@Override
	public void forEach(String packageName, BiConsumer<String, ByteCodeSource> biConsumer) throws IOException {
		Collection<String> files = source.listSourceFiles(packageName);
		for (String f : files) {
			biConsumer.accept(f, getByteCodeSource(f));
		}
	}

	@Override
	protected byte[] loadBytecode(String name) throws ClassNotFoundException {
		try {
			if (compiler.alreadyCompiledClass(name) || source.isClassPresent(name)) {
				byte[] data = this.compiler.findClass(name);
				assert data != null;
				return data;
			} else {
				throw new ClassNotFoundException(name + " could not be found on " + getSource());
			}
		} catch (IOException e) {
			throw new ClassNotFoundException(name + " not found", e);
		}
	}

}
