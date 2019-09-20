package com.agentecon.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public abstract class SimulationHandle {

	protected static final String SOURCE_FOLDER = "src";

	public static final String JAVA_SUFFIX = ".java";

	private final String owner, name;
	private String[] projects;

	public SimulationHandle(String owner, String repo, boolean simulation) {
		this.owner = owner;
		this.name = repo;
		this.projects = simulation ? new String[] { "simulation" } : new String[] { "exercises" };
	}
	
	public abstract SimulationHandle copy(boolean simulation) throws IOException;

	public String getRepo() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	protected String[] getProjects() {
		return projects;
	}
	
	public static String toIdentifier(String branchOrTag) {
		int dash = branchOrTag.lastIndexOf('-');
		if (dash >= 0 && branchOrTag.length() > dash + 1) {
			for (int pos = dash + 1; pos < branchOrTag.length(); pos++) {
				if (!Character.isDigit(branchOrTag.charAt(pos))) {
					return branchOrTag;
				}
			}
			return branchOrTag.substring(0, dash);
		} else {
			return branchOrTag;
		}
	}

	public String getIdentifier() {
		return toIdentifier(getBranch());
	}

	public abstract String getBranch();

	public abstract boolean isClassPresent(String path) throws IOException;

	protected String toFilePath(String classname) {
		int usdIndex = classname.indexOf('$');
		if (usdIndex >= 0) {
			classname = classname.substring(0, usdIndex);
		}
		return SOURCE_FOLDER + "/" + classname.replace('.', '/') + ".java";
	}

	public abstract String getPath();

	public abstract URL getBrowsableURL(String classname);

	@Override
	public String toString() {
		return name;
	}

	public abstract InputStream openInputStream(String classname) throws IOException;

	public abstract Collection<String> listSourceFiles(String packageName) throws IOException;

	public abstract String getVersion() throws IOException;

	@Override
	public int hashCode() {
		return owner.hashCode() ^ name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		SimulationHandle other = (SimulationHandle) o;
		return other.owner.equals(owner) && other.name.equals(name);
	}

}
