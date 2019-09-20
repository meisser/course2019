package com.agentecon.metric.series;

public interface IAgentType {

	public String getIndividualKey();

	public String[] getTypeKeys();

	public boolean isConsumer();

	public boolean isFirm();

}
