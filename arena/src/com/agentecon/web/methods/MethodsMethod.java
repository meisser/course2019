/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.agentecon.web.data.JsonData;

public class MethodsMethod extends WebApiMethod {
	
	private transient HashMap<String, WebApiMethod> methods;
	
	public MethodsMethod(){
		this.methods = new HashMap<>();
		this.add(this);
	}

	public void add(WebApiMethod method) {
		this.methods.put(method.getName(), method);
	}

	public WebApiMethod getMethod(String name) {
		return methods.get(name);
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) {
		return new MethodList();
	}
	
	class MethodList extends JsonData {
		public Collection<Descriptor> methods = MethodsMethod.this.methods.values().stream().map(m -> m.getDescriptor()).collect(Collectors.toList());
	}

}
