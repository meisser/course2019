package com.agentecon.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;

public class SourceFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private SimulationHandle handle;
	private HashMap<String, ByteArrayOutputStream> byteCode;
	private RemoteLoader parent;

	public SourceFileManager(JavaFileManager standard, RemoteLoader parent, SimulationHandle handle) {
		super(standard);
		this.handle = handle;
		this.parent = parent;
		this.byteCode = new HashMap<>();
	}

	@Override
	public int isSupportedOption(String option) {
		return -1;
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		return super.getClassLoader(location);
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
			throws IOException {
		Iterable<JavaFileObject> objects = super.list(location, packageName, kinds, recurse);
		if (packageName.startsWith("com.agentecon")) {
			assert !recurse; // not supported yet and does not seem necessary
			if (location.equals(StandardLocation.SOURCE_PATH) && kinds.contains(Kind.SOURCE)) {
				ArrayList<JavaFileObject> list = copyList(objects);
				Collection<String> classNames = handle.listSourceFiles(packageName);
				for (String name : classNames) {
					list.add(getJavaSourceFile(name));
				}
				return list;
			} else if (location.equals(StandardLocation.CLASS_OUTPUT) && kinds.contains(Kind.CLASS)) {
				ArrayList<JavaFileObject> list = copyList(objects);
				byteCode.forEach(new BiConsumer<String, ByteArrayOutputStream>() {

					@Override
					public void accept(String name, ByteArrayOutputStream content) {
						list.add(getClassFile(name, content.toByteArray()));
					}
				});
				return list;
			} else if (location.equals(StandardLocation.CLASS_PATH) && kinds.contains(Kind.CLASS) && parent != null) {
				ArrayList<JavaFileObject> list = copyList(objects);
				addParentClasses(packageName, list);
				return list;
			} else {
				return objects;
			}
		} else {
			return objects;
		}
	}

	private void addParentClasses(String packageName, ArrayList<JavaFileObject> list) throws IOException {
		parent.forEach(packageName, new BiConsumer<String, ByteCodeSource>() {

			@Override
			public void accept(String name, ByteCodeSource byteCode) {
				list.add(getClassFile(name, byteCode));
			}
		});
	}

	private SimpleJavaFileObject getJavaSourceFile(String name) {
		assert !name.endsWith(".java");
		if (name.contentEquals("module-info")) {
			return null;
		} else {
			return new SimpleJavaFileObject(URI.create(name.replace(".", "/") + ".java"), Kind.SOURCE) {
				@Override
				public InputStream openInputStream() throws IOException {
					return handle.openInputStream(name);
				}

				@Override
				public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
					try (InputStream source = openInputStream()) {
						return new String(readData(source));
					}
				}
			};
		}
	}

	public static byte[] readData(InputStream source) throws IOException {
		int ava = source.available();
		int size = ava == 0 ? 1000 : ava;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		byte[] buffer = new byte[size];
		while (true) {
			int read = source.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.write(buffer, 0, read);
			} else {
				assert read == -1;
				break;
			}
		}
		return out.toByteArray();
	}

	private ArrayList<JavaFileObject> copyList(Iterable<JavaFileObject> objects) {
		ArrayList<JavaFileObject> list = new ArrayList<JavaFileObject>();
		for (JavaFileObject existing : objects) {
			list.add(existing);
		}
		return list;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof SimpleJavaFileObject) {
			String name = file.getName();
			String extension = file.getKind().extension;
			return name.substring(0, name.length() - extension.length()).replace('/', '.');
		} else {
			return super.inferBinaryName(location, file);
		}
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean handleOption(String current, Iterator<String> remaining) {
		return true;
	}

	@Override
	public boolean hasLocation(Location location) {
		if (location == StandardLocation.CLASS_PATH) {
			return true;
		} else if (location == StandardLocation.CLASS_OUTPUT) {
			return true;
		} else if (location == StandardLocation.SOURCE_PATH) {
			return true;
		} else {
			return super.hasLocation(location);
		}
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
		if (location.getName().startsWith(StandardLocation.SYSTEM_MODULES.getName())) {
			return super.getJavaFileForInput(location, className, kind);
		} else if (location.equals(StandardLocation.SOURCE_PATH) && kind == Kind.SOURCE) {
			return getJavaSourceFile(className);
		} else if (byteCode.containsKey(className) && kind == Kind.CLASS) {
			return getClassFile(className, byteCode.get(className).toByteArray());
		} else if (parent != null) {
			return getClassFile(className, parent.getByteCodeSource(className));
		} else {
			return null;
		}
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, final String className, Kind kind, FileObject sibling)
			throws IOException {
		assert kind == Kind.CLASS;
		return getClassFileOutput(className);
	}

	public JavaFileObject getClassFile(final String className, byte[] data) {
		return getClassFile(className, new ByteCodeSource(className, data));
	}

	public JavaFileObject getClassFile(final String className, ByteCodeSource data) {
		return new SimpleJavaFileObject(URI.create(className.replace('.', '/') + ".class"), Kind.CLASS) {
			@Override
			public InputStream openInputStream() throws IOException {
				try {
					return data.openStream();
				} catch (ClassNotFoundException e) {
					throw new IOException(e);
				}
			}
		};
	}

	public JavaFileObject getClassFileOutput(final String className) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		if (!byteCode.containsKey(className)) {
			byteCode.put(className, outStream);
		}
		return new SimpleJavaFileObject(URI.create(className.replace('.', '/') + ".class"), Kind.CLASS) {
			@Override
			public ByteArrayOutputStream openOutputStream() {
				return outStream;
			}
		};
	}

	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
		throw new RuntimeException("not implemented");
	}

	public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling)
			throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	public byte[] getByteCode(String name) {
		ByteArrayOutputStream stream = byteCode.get(name);
		return stream == null ? null : stream.toByteArray();
	}

}
