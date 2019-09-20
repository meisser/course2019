/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteCodeSource {
	
	private String name;
	private byte[] data;
	
	public ByteCodeSource(String name){
		this.name = name;
	}

	public ByteCodeSource(String name, byte[] data) {
		this(name);
		this.data = data;
	}

	public synchronized byte[] getData() throws ClassNotFoundException {
		if (data == null){
			data = loadData();
		}
		return data;
	}

	public InputStream openStream() throws ClassNotFoundException {
		return new ByteArrayInputStream(getData());
	}

	protected byte[] loadData() throws ClassNotFoundException {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public String toString(){
		return "bytecode of " + name;
	}

}
