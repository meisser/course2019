package com.agentecon.metric.series;

public interface IAgentType {

	public String getName();

	public String[] getTypeKeys();

	public boolean isConsumer();

	public boolean isFirm();

}
