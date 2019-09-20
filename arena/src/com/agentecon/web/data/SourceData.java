/**
 * Created by Luzius Meisser on Jun 14, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import java.io.File;
import java.net.URL;

import com.agentecon.agent.IAgent;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;

public class SourceData {

	// public long date;
	public String owner;
	public String codeLink;

	public SourceData(IAgent agent) {
		this(agent.getClass());
	}

	public SourceData(Class<? extends IAgent> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		if (loader instanceof RemoteLoader) {
			RemoteLoader agentLoader = (RemoteLoader) loader;
			SimulationHandle handle = agentLoader.getSource();
//			this.date = agentLoader.getDate();
			this.owner = agentLoader.getOwner();
			this.codeLink = handle.getBrowsableURL(clazz.getName()).toExternalForm();
		} else {
			URL url = loader.getResource(clazz.getName().replace('.', File.separatorChar) + ".class");
			File sourceFile = new File(url.toExternalForm().substring("file://".length()).replace(".class", ".java").replace("/bin/", "/src/"));
//			this.date = sourceFile.lastModified();
			this.owner = System.getProperty("user.name");
			this.codeLink = "file://" + sourceFile;
		}
	}

}
