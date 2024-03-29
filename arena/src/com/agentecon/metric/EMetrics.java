/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric;

import java.util.ArrayList;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.metric.variants.CashStats;
import com.agentecon.metric.variants.Demographics;
import com.agentecon.metric.variants.DividendStats;
import com.agentecon.metric.variants.Equality;
import com.agentecon.metric.variants.InterestStats;
import com.agentecon.metric.variants.InventoryStats;
import com.agentecon.metric.variants.MarketMakerStats;
import com.agentecon.metric.variants.MarketStats;
import com.agentecon.metric.variants.MonetaryStats;
import com.agentecon.metric.variants.OwnershipStats;
import com.agentecon.metric.variants.ProductionDetailStats;
import com.agentecon.metric.variants.ProductionStats;
import com.agentecon.metric.variants.ShareholderValueStats;
import com.agentecon.metric.variants.StockMarketStats;
import com.agentecon.metric.variants.SynchronizedUtility;
import com.agentecon.metric.variants.TypeStatistics;
import com.agentecon.metric.variants.UtilityRanking;
import com.agentecon.metric.variants.UtilityStats;
import com.agentecon.metric.variants.WealthStats;
import com.agentecon.web.query.AgentQuery;

public enum EMetrics {
	
	CASH, DEMOGRAPHICS, TOTAL_DIVIDENDS, AVERAGE_DIVIDENDS, SHAREHOLDER_GAINS, INTEREST, EQUALITY, INVENTORY, MARKET, MARKETMAKER, MONETARY, OWNERSHIP, STOCKMARKET, PRODUCTION, PRODUCTION_DETAILS, RANKING_CONSUMERS, UTILITY, UTILITY_SYNCHRONIZED, WEALTH_AGG, WEALTH_AVG, TYPE;
	
	public static final EMetrics[] ENABLED_METRICS = new EMetrics[] {CASH, DEMOGRAPHICS, TOTAL_DIVIDENDS, AVERAGE_DIVIDENDS, SHAREHOLDER_GAINS, MARKET, MARKETMAKER, MONETARY, OWNERSHIP, STOCKMARKET, PRODUCTION, WEALTH_AGG, WEALTH_AVG, TYPE};

	public SimStats createAndRegister(ISimulation sim, List<String> list, boolean details) {
		ArrayList<AgentQuery> queries = new ArrayList<>();
		for (String query : list) {
			queries.add(new AgentQuery(query));
		}
		SimStats stats = instantiate(sim, queries, details);
		sim.addListener(stats);
		return stats;
	}

	public String getDescription() {
		switch (this) {
		case CASH:
			return "Nightly cash holdings, aggregate per type as well as average per type.";
		case WEALTH_AGG:
			return "Aggregate net worth at market prices for each agent type. Related: cash statistics.";
		case WEALTH_AVG:
			return "Average net worth at market prices for each agent type. Related: cash statistics.";
		case DEMOGRAPHICS:
			return "The size of the population and related figures.";
		case TOTAL_DIVIDENDS:
			return "Total dividends paid out to free float shareholders (no dividend is paid to firm itself).";
		case AVERAGE_DIVIDENDS:
			return "Average dividends paid out to free float shareholders (no dividend is paid to firm itself).";
		case SHAREHOLDER_GAINS:
			return "Daily dividends received and capital gains made by consumer shareholders.";
		case INTEREST:
			return "Interest rate on money holdings (for simulations with a bank paying interest to consumers).";	
		case EQUALITY:
			return "Gini co-efficient for various cohorts over time. A low value implies more equality.";
		case RANKING_CONSUMERS:
			return "The average over the exponentially moving average of daily utility. The 'all' variant is used for the ranking.";
		case INVENTORY:
			return "Average amount of goods held by firms and consumers after trading (before consumption and before production).";
		case MARKET:
			return "Average market price and trading volume of all goods, including a volume-weighted price index.";
		case MARKETMAKER:
			return "The bid and ask price beliefs of one selected market maker.";
		case MONETARY:
			return "Money supply, money velocity; prices and trade volume on the goods market.";
		case OWNERSHIP:
			return "Some general statistics on firm ownership.";
		case PRODUCTION:
			return "Overall production statistics.";
		case PRODUCTION_DETAILS:
			return "Production volume of individual firms.";
		case STOCKMARKET:
			return "Various stock market statistics: average prices, trading volumes, inflows, outflows, etc.";
		case UTILITY_SYNCHRONIZED:
			return "Average daily utility of each consumer type over the course of their life span. (x-axis is age and not time)";
		case UTILITY:
			return "Daily average utility, as well as the minimum and maximum experienced by an agent.";
		case TYPE:
			return "How many agents of each type there are in the simulation at each point in time.";
//		case VALUATION:
		default:
			return "no description available";
		}
	}

	private SimStats instantiate(ISimulation sim, ArrayList<AgentQuery> agents, boolean details) {
		switch (this) {
		case DEMOGRAPHICS:
			return new Demographics(sim);
		case TOTAL_DIVIDENDS:
			return new DividendStats(sim, false, false, details);
		case AVERAGE_DIVIDENDS:
			return new DividendStats(sim, false, true, details);
		case SHAREHOLDER_GAINS:
			return new ShareholderValueStats(sim);
		case INTEREST:
			return new InterestStats(sim);
		case EQUALITY:
			return new Equality(sim);
		case INVENTORY:
			return new InventoryStats(sim, details);
		case MARKET:
			return new MarketStats(sim, true);
		case MARKETMAKER:
			return new MarketMakerStats(sim);
		case CASH:
			return new CashStats(sim, details);
		case WEALTH_AGG:
			return new WealthStats(sim, true, details);
		case WEALTH_AVG:
			return new WealthStats(sim, false, details);
		case MONETARY:
			return new MonetaryStats(sim);
		case OWNERSHIP:
			return new OwnershipStats(sim, details);
		case PRODUCTION:
			return new ProductionStats(sim, details);
		case PRODUCTION_DETAILS:
			return new ProductionDetailStats(sim);
		case STOCKMARKET:
			return new StockMarketStats(sim, true, details);
		case RANKING_CONSUMERS:
			return new UtilityRanking(sim, true);
		case UTILITY:
			return new UtilityStats(sim, details);
		case UTILITY_SYNCHRONIZED:
			return new SynchronizedUtility(sim, details);
		case TYPE:
			return new TypeStatistics(sim);
		default:
			return null;
		}
	}

	public static EMetrics parse(String metric) {
		for (EMetrics candidate : EMetrics.values()) {
			if (candidate.getName().equals(metric)) {
				return candidate;
			}
		}
		return null;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

}