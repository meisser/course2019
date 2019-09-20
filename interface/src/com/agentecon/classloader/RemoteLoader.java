/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;

public abstract class RemoteLoader extends ClassLoader {

	private String version;
	private HashMap<String, ByteCodeSource> bytecode;

	protected SimulationHandle source;
	private HashMap<SimulationHandle, RemoteLoader> subLoaderCache;

	public RemoteLoader(ClassLoader parent, SimulationHandle source) throws IOException {
		super(parent);
		this.version = source.getVersion();
		this.source = source;
		this.bytecode = new HashMap<>();
		this.subLoaderCache = new HashMap<>();
	}

	public RemoteLoader obtainChildLoader(SimulationHandle source) throws IOException {
		synchronized (subLoaderCache) {
			RemoteLoader existing = subLoaderCache.get(source);
			if (existing == null) {
				CompilingClassLoader loader = new CompilingClassLoader(this, source, true);
				RemoteLoader prev = subLoaderCache.put(source, loader);
				assert prev == null;
				return loader;
			} else {
				return existing;
			}
		}
	}

	@Deprecated
	public void registerSubloader(RemoteLoader loader) {
		synchronized (subLoaderCache) {
			RemoteLoader prev = subLoaderCache.put(loader.getSource(), loader);
			assert prev == null || prev == loader;
		}
	}

	protected byte[] loadBytecode(String classname) throws ClassNotFoundException {
		throw new ClassNotFoundException(classname);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = getByteCodeSource(name).getData();
		return super.defineClass(name, data, 0, data.length);
	}

	public boolean usesRepository(String repo) {
		if (source.getRepo().equals(repo)) {
			return true;
		} else {
			synchronized (subLoaderCache) {
				for (SimulationHandle handle : subLoaderCache.keySet()) {
					if (handle.getRepo().equals(repo)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public boolean refreshSubloaders() throws IOException {
		synchronized (subLoaderCache) {
			Iterator<RemoteLoader> iter = subLoaderCache.values().iterator();
			boolean changed = false;
			while (iter.hasNext()) {
				if (!iter.next().isUptoDate()) {
					iter.remove();
					changed = true;
				}
			}
			return changed;
		}
	}

	public Collection<RemoteLoader> getCachedSubloaders() {
		synchronized (subLoaderCache) {
			return new ArrayList<RemoteLoader>(subLoaderCache.values());
		}
	}

	public RemoteLoader getSubloader(SimulationHandle handle) {
		synchronized (subLoaderCache) {
			return this.subLoaderCache.get(handle);
		}
	}

	public String getVersionString() {
		return version;
	}

	public boolean isUptoDate() throws IOException {
		return source.getVersion().equals(version);
	}

	public String getOwner() {
		return source.getRepo();
	}

	public SimulationHandle getSource() {
		return source;
	}

	public synchronized ByteCodeSource getByteCodeSource(String name) {
		ByteCodeSource data = this.bytecode.get(name);
		if (data == null) {
			data = new ByteCodeSource(name) {
				@Override
				protected byte[] loadData() throws ClassNotFoundException {
					return RemoteLoader.this.loadBytecode(name);
				}
			};
			this.bytecode.put(name, data);
		}
		return data;
	}

	public void forEach(String packageName, BiConsumer<String, ByteCodeSource> biConsumer) throws IOException {
		bytecode.forEach(new BiConsumer<String, ByteCodeSource>() {

			private final boolean recurse = false;

			@Override
			public void accept(String name, ByteCodeSource u) {
				if (name.startsWith(packageName)) {
					if (recurse || name.substring(packageName.length() + 1).indexOf('.') == -1) {
						biConsumer.accept(name, u);
					}
				}
			}
		});
	}

	@Override
	public String toString() {
		return "Class loader that loads from " + source;
	}

}
