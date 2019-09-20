package com.agentecon.classloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class GitSimulationHandle extends SimulationHandle {

	private final String branch;
	private HashMap<String, ProjectFileCache> cache;
	
	@Deprecated
	public GitSimulationHandle(String owner, String repo) throws IOException {
		this(owner, repo, false);
	}

	public GitSimulationHandle(String owner, String repo, boolean simulation) throws IOException {
		this(owner, repo, "master", simulation);
	}
	
	@Deprecated
	public GitSimulationHandle(String owner, String repo, String branch) throws IOException {
		this(owner, repo, branch, false);
	}

	public GitSimulationHandle(String owner, String repo, String branch, boolean simulation) throws IOException {
		super(owner, repo, simulation);
		this.branch = branch;
		this.cache = new HashMap<>();
		for (String project: getProjects()) {
			this.cache.put(project, new ProjectFileCache(this, project));
		}
		WebUtil.checkAuthorizationCode();
	}
	
	public SimulationHandle copy(boolean simulation) throws IOException {
		return new GitSimulationHandle(getOwner(), getRepo(), getBranch(), simulation);
	}

	public String getPath() {
		return super.getOwner() + "/" + getRepo() + "/" + branch;
	}

	@Override
	public String getBranch() {
		return branch;
	}
	
	@Override
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		HashSet<String> all = new HashSet<>();
		for (ProjectFileCache project: cache.values()){
			all.addAll(project.listSourceFiles(packageName));
		}
		return all;
	}
	
	public String findPath(String classname) throws IOException {
		for (ProjectFileCache project: cache.values()) {
			if (project.isClassPresent(classname)) {
				return project.getProject() + "/" + toFilePath(classname);
			}
		}
		throw new FileNotFoundException(classname);
	}

	public URL getBrowsableURL(String classname) {
		try {
			try {
				return new URL("https://github.com/" + getOwner() + "/" + getRepo() + "/blob/" + branch + "/" + findPath(classname));
			} catch (IOException e) {
				return new URL("https://github.com/" + getOwner() + "/" + getRepo() + "/blob/" + branch);
			}
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	private URLConnection openContentConnection(String path) throws IOException {
		try {
			URL url = new URL(WebUtil.addSecret("https://api.github.com/repos/" + getOwner() + "/" + getRepo() + "/contents/" + path + "?ref=" + branch));
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept", "application/vnd.github.VERSION.raw");
			return conn;
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public boolean isClassPresent(String classname) throws IOException {
		for (ProjectFileCache project: cache.values()) {
			if (project.isClassPresent(classname)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public InputStream openInputStream(String classname) throws IOException {
		for (ProjectFileCache project: cache.values()) {
			if (project.isClassPresent(classname)) {
				URLConnection url = openContentConnection(project.getProject() + "/" + toFilePath(classname));
				return url.getInputStream();
			}
		}
		throw new FileNotFoundException(classname);
	}

	@Override
	public String getVersion() throws IOException {
		String commitUrl = "https://api.github.com/repos/" + getOwner() + "/" + getRepo() + "/commits/" + branch;
		String commitDesc = WebUtil.readHttp(commitUrl);
		// String hash = WebUtil.extract(commitDesc, "sha", new int[] { 0 });
		String name = WebUtil.extract(commitDesc, "name", new int[] { 0 });
		String date = WebUtil.extract(commitDesc, "date", new int[] { 0 });
		// String email = WebUtil.extract(commitDesc, "email", new int[]{0});
		return name + " on " + date;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GitSimulationHandle) {
			return super.equals(o) && ((GitSimulationHandle) o).branch.equals(branch);
		} else {
			return false;
		}
	}

}
