/**
 * Created by Luzius Meisser on Jun 13, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import java.util.Collection;

import com.agentecon.consumer.IConsumer;

public class CollectiveConsumerData extends CollectiveData {
	
	public double currentUtility;
	public double averageUtility;

	public CollectiveConsumerData(Collection<? extends IConsumer> consumers) {
		super(consumers);
		for (IConsumer c: consumers){
			this.currentUtility += c.getUtilityFunction().getLatestExperiencedUtility();
			this.averageUtility += c.getUtilityFunction().getStatistics().getAverage();
		}
		this.currentUtility /= consumers.size();
		this.averageUtility /= consumers.size();
	}

}
