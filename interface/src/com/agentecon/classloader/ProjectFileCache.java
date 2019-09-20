package com.agentecon.classloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ProjectFileCache {

	private String project;
	private SimulationHandle parent;
	private HashMap<String, HashSet<String>> cachedTree;

	public ProjectFileCache(SimulationHandle parent, String project) {
		this.project = project;
		this.parent = parent;
		this.cachedTree = new HashMap<>();
	}

	public boolean isClassPresent(String classname) throws IOException {
		String packageName = getPackage(classname);
		if (packageName.contains("$")) {
			classname = classname.substring(0, classname.indexOf("$"));
		}
		return listSourceFiles(packageName).contains(classname);
	}
	
	private String getPackage(String classname) {
		int index = classname.lastIndexOf('.');
		return classname.substring(0, index);
	}
	
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		HashSet<String> children = cachedTree.get(packageName);
		if (children == null) {
			children = new HashSet<>();
			if (couldExist(packageName)) {
				String answer = WebUtil.readGitApi(parent.getOwner(), parent.getRepo(), "contents", project + "/" + SimulationHandle.SOURCE_FOLDER + "/" + packageName.replace('.', '/'), parent.getBranch());
				int[] pos = new int[] { 0 };
				while (true) {
					String name = WebUtil.extract(answer, "name", pos);
					if (name == null) {
						break;
					} else {
						children.add(name);
					}
				}
				cachedTree.put(packageName, children);
			}
		}
		ArrayList<String> filtered = new ArrayList<>();
		for (String candidate: children) {
			if (candidate.endsWith(SimulationHandle.JAVA_SUFFIX)) {
				candidate = candidate.substring(0, candidate.length() - SimulationHandle.JAVA_SUFFIX.length());
				filtered.add(packageName + "." + candidate);
			}

		}
		return filtered;
	}

	private boolean couldExist(String packageName) {
		while (true) {
			int dot = packageName.lastIndexOf('.');
			if (dot == -1) {
				return true;
			} else {
				String parent = packageName.substring(0, dot);
				HashSet<String> children = cachedTree.get(parent);
				if (children != null && !children.contains(packageName.substring(dot + 1))) {
					return false;
				}
				packageName = parent;
			}
		}
	}

	public String getProject() {
		return project;
	}

}
