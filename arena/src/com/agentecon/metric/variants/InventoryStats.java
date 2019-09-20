package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class InventoryStats extends SimStats {

	private Map<Good, TimeSeriesCollector> inventoriesByGoods;

	public InventoryStats(ISimulation agents, boolean details) {
		super(agents);
		this.inventoriesByGoods = new InstantiatingConcurrentHashMap<Good, TimeSeriesCollector>() {

			@Override
			protected TimeSeriesCollector create(Good key) {
				return new TimeSeriesCollector(details, getMaxDay());
			}
		};
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(new IMarketListener() {

			@Override
			public void notifyTradesCancelled() {
			}

			@Override
			public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
			}

			@Override
			public void notifyMarketClosed(int day) {
				InventoryStats.this.notifyMarketClosed(day);
			}
		});
	}

	public void notifyMarketClosed(int day) {
		IAgents agents = getAgents();
		for (IAgent ag : agents.getAgents()) {
			Inventory inv = ag.getInventory();
			for (IStock stock : inv.getAll()) {
				TimeSeriesCollector collector = inventoriesByGoods.get(stock.getGood());
				collector.record(day, ag, stock.getAmount());
			}
		}
		for (TimeSeriesCollector c : inventoriesByGoods.values()) {
			c.flushDay(day, false);
		}
	}

	@Override
	public ArrayList<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		inventoriesByGoods.entrySet().forEach(new Consumer<Entry<Good, TimeSeriesCollector>>() {

			@Override
			public void accept(Entry<Good, TimeSeriesCollector> t) {
				list.addAll(TimeSeries.prefix(t.getKey() + " held by ", t.getValue().getTimeSeries()));
			}
		});
		return list;
	}

}
