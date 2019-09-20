/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric.variants;

import java.net.URL;

import com.agentecon.agent.Agent;

public class Rank implements Comparable<Rank> {

	private String type;
	private String version;
	private String url;

	@Deprecated
	private double averageUtility;

	private double score;
	private transient int instances;

	public Rank(String type, Agent agent) {
		this.type = type;
		this.version = agent.getVersion();
		URL source = agent.getSourceUrl();
		if (source == null) {
			url = "local";
		} else {
			url = source.toExternalForm();
		}
	}
	
	public void roundScore() {
		this.score = Math.round(score);
	}

	public String getType() {
		return type;
	}

	public void add(double score, boolean average) {
		if (average) {
			this.score *= instances++;
			this.score += score;
			this.score /= instances;
			this.averageUtility = score;
		} else {
			this.score += score;
		}
	}

	@Override
	public int compareTo(Rank o) {
		return -Double.compare(score, o.score);
	}

	@Override
	public String toString() {
		return getType() + "\t" + score;
	}

}