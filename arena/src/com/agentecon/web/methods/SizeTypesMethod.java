/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.util.Collection;

import com.agentecon.web.data.JsonData;
import com.agentecon.web.graph.ESizeType;

public class SizeTypesMethod extends WebApiMethod {

	@Override
	protected JsonData getJsonAnswer(Parameters params) throws IOException {
		return new SizeTypes();
	}
	
	class SizeTypes extends JsonData {
		public Collection<String> consumer = ESizeType.getConsumerTypes();
		public Collection<String> firm = ESizeType.getFirmTypes();
	}

}


