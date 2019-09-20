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

import com.agentecon.metric.EMetrics;
import com.agentecon.web.data.JsonData;

public class MetricsMethod extends WebApiMethod {

	public MetricsMethod() {
		super();
	}

	@Override
	protected JsonData getJsonAnswer(Parameters params) throws IOException {
		return new Metrics(EMetrics.values());
	}

	class Metrics extends JsonData {

		public String[] metrics;
		
		public Metrics(EMetrics[] metrics){
			this.metrics = new String[metrics.length];
			for (int i=0; i<metrics.length; i++){
				this.metrics[i] = metrics[i].name().toLowerCase();
			}
		}
	}

}
