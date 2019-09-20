package com.agentecon.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class LocalSimulationHandle extends SimulationHandle {

	public static final String REPO_NAME = "local";

	private File base;
	private long version;
	private HashSet<String> touchedFiles;

	public LocalSimulationHandle() {
		this(true);
	}

	public LocalSimulationHandle(boolean simulation) {
		super(System.getProperty("user.name").toLowerCase(), REPO_NAME, simulation);
		this.base = new File("..").getAbsoluteFile();
		this.touchedFiles = new HashSet<>();
		this.version = System.currentTimeMillis();
		assert base.isDirectory() : base.getAbsolutePath() + " is not a folder";
	}
	
	public SimulationHandle copy(boolean simulation) {
		return new LocalSimulationHandle(simulation);
	}

	public String getDescription() {
		return "Simulation loader from local file system";
	}

	public String getBranch() {
		return "local";
	}

	public String getAuthor() {
		return getOwner();
	}

	@Override
	public boolean isClassPresent(String classname) {
		try {
			return find(toFilePath(classname)) != null;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	@Override
	protected String toFilePath(String classname) {
		return super.toFilePath(classname).replace('/', File.separatorChar);
	}

	private File find(String path) throws FileNotFoundException {
		for (String project : getProjects()) {
			File candidate = new File(new File(base, project), path);
			if (candidate.exists()) {
				return candidate;
			}
		}
		throw new FileNotFoundException(path + " not found in " + this);
	}

	@Override
	public String getPath() {
		return getOwner() + "/local/local";
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			try {
				return new URL("file://" + find(toFilePath(classname)));
			} catch (FileNotFoundException e) {
				return new URL("file://" + base.getAbsolutePath());
			}
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	private void notifyTouched(File file) {
		touchedFiles.add(file.getPath());
		version = Math.max(version, file.lastModified());
	}

	@Override
	public InputStream openInputStream(String classname) throws IOException {
		File file = find(toFilePath(classname));
		notifyTouched(file);
		return new FileInputStream(file);
	}

	@Override
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		ArrayList<String> names = new ArrayList<>();
		for (String project : getProjects()) {
			File file = new File(base, project + File.separator + SOURCE_FOLDER + File.separator + packageName.replace('.', '/'));
			File[] children = file.listFiles();
			if (children != null) {
				for (File f : children) {
					String name = f.getName();
					if (name.endsWith(JAVA_SUFFIX)) {
						name = name.substring(0, name.length() - JAVA_SUFFIX.length());
						names.add(packageName + "." + name);
					}
				}
			}
		}
		return names;
	}

	@Override
	public String getVersion() {
		for (String file : touchedFiles) {
			long mod = new File(file).lastModified();
			if (mod > version) {
				version = mod;
			}
		}
		return "local version " + new Date(version);
	}

}
