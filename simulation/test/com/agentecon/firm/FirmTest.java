// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.firm;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Weight;
import com.agentecon.firm.decisions.DifferentialDividend;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.LogProdFun;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.learning.HardcodedBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Market;
import com.agentecon.market.Price;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.IProductionFunction;
import com.agentecon.util.Numbers;

public class FirmTest implements IAgentIdGenerator {
	
	public static final Good MONEY = new Good("Taler");
	public static final Good PIZZA = new Good("Pizza", 1.0);
	public static final Good FONDUE = new Good("Fondue", 1.0);
	public static final Good SWISSTIME = new Good("Swiss man-hours", 0.0);
	public static final Good ITALTIME = new Good("Italian man-hours", 0.0);

	private Endowment end;
	private Random rand;

	@Before
	public void setUp() throws Exception {
		this.rand = new Random(23);
		this.end = new Endowment(MONEY, new Stock[] { new Stock(MONEY, 1000) }, new Stock[] {});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPriceFinding() {
		TestConsumer tc = new TestConsumer(MONEY, new Price(PIZZA, 30), new Price(SWISSTIME, 10), new Price(ITALTIME, 15));
		OldProducer firm = new OldProducer(this, end, new LogProdFun(PIZZA, new Weight(ITALTIME, 5.0), new Weight(SWISSTIME, 5.0)), new DifferentialDividend());
		for (int i = 0; i < 100; i++) {
			Market market = new Market(rand);
			firm.offer(market);
			System.out.println(tc.getPriceSquareError(market));
			tc.buyAndSell(market);
			firm.produce();
		}
		for (int i = 0; i < 1000; i++) {
			Market market = new Market(rand);
			firm.offer(market);
			System.out.println(tc.getPriceSquareError(market));
			tc.buyAndSell(market);
			firm.adaptPrices();
			firm.produce();
		}
		Market market = new Market(rand);
		firm.offer(market);
		assert tc.checkPrices(market, 0.10);
	}
	
	@Test
	public void testOptimalProduction(){
		final double hourPrice = 2.972868529894414d;
		this.end = new Endowment(MONEY, new Stock[] { new Stock(MONEY, 1000), new Stock(FONDUE, 36.156428643107d) }, new Stock[] {});
		OldProducer firm = new OldProducer(this, end, new LogProdFun(FONDUE, new Weight(SWISSTIME, 10.0)), new DifferentialDividend()){
			
			@Override
			protected IBelief createPriceBelief(Good good){
				if (good.equals(SWISSTIME)){
					return new HardcodedBelief(hourPrice);
				} else {
					return new HardcodedBelief(10.0);
				}
			}
			
		};
		firm.offer(new IPriceMakerMarket() {
			
			@Override
			public void offer(Ask offer) {
				assert offer.getPrice().getPrice() == 10.0;
				offer.accept(null, new Stock(MONEY, 100000), new Stock(offer.getGood()), offer.getQuantity());
			}
			
			@Override
			public void offer(Bid offer) {
				assert offer.getPrice().getPrice() == hourPrice;
				assert Math.abs(offer.getAmount() - 32.63754535204813) < 0.0001 : "Firm does not seek optimal input amount";
				offer.accept(null, new Stock(MONEY), new Stock(offer.getGood(), offer.getAmount()), offer.getQuantity());
			}

			@Override
			public void addMarketListener(IMarketListener listener) {
			}
			
		});
		firm.adaptPrices();
		double production = produce(firm);
		assert Math.abs(production - 36.1564) < 0.001;
//		double profits = firm.getLatestProfits();
//		assert Math.abs(profits - 264.537) < 0.001;
	}
	
	@Test
	public void testOptimalProductionCobbDouglas1(){
		final double hourPrice1 = 2.0;
		final double fonduePrice = 10.0;
		this.end = new Endowment(MONEY, new Stock[] { new Stock(MONEY, 1000) }, new Stock[] {});
		double alpha = 0.5;
		IProductionFunction prodFun = new CobbDouglasProduction(FONDUE, 1.0, new Weight(SWISSTIME, alpha));
		OldProducer firm = new OldProducer(this, end, prodFun, new DifferentialDividend()){

			@Override
			public IBelief createPriceBelief(Good good) {
				if (good.equals(SWISSTIME)){
					return new HardcodedBelief(hourPrice1);
				} else {
					return new HardcodedBelief(fonduePrice);
				}
			}
			
		};
		firm.offer(new IPriceMakerMarket() {
			
			@Override
			public void offer(Ask offer) {
				assert offer.getPrice().getPrice() == fonduePrice;
				offer.accept(null, new Stock(MONEY, 100000), new Stock(offer.getGood()), offer.getQuantity());
			}
			
			@Override
			public void offer(Bid offer) {
				assert offer.getPrice().getPrice() == hourPrice1;
				offer.accept(null, new Stock(MONEY), new Stock(offer.getGood(), offer.getAmount()), offer.getQuantity());
			}

			@Override
			public void addMarketListener(IMarketListener listener) {
			}

		});
		double prod = produce(firm);
		firm.produce();
		System.out.println("Produced " + prod);
		double x1 = 6.25;
		Quantity production2 = prodFun.produce(new Inventory(MONEY, new Stock(SWISSTIME, x1)));
		System.out.println(production2);
		assert Numbers.equals(prod, production2.getAmount());
	}

	private double produce(OldProducer firm) {
		final double[] production = new double[1];
		firm.addProducerMonitor(new IProducerListener() {
			
			@Override
			public void reportResults(IProducer inst, double revenue, double cogs, double expectedProfits) {
			}
			
			@Override
			public void notifyProduced(IProducer inst, Quantity[] inputs, Quantity output) {
				production[0] = output.getAmount();
			}
		});
		firm.produce();
		double prod = production[0];
		return prod;
	}
	
	@Test
	public void testOptimalProductionCobbDouglas2(){
		final double hourPrice1 = 5.0;
		final double hourPrice2 = 4.0;
		final double fonduePrice = 10.0;
		this.end = new Endowment(MONEY, new Stock[] { new Stock(MONEY, 1000) }, new Stock[] {});
		double alpha = 0.45;
		double beta = 0.25;
		double factor = 2.0;
		IProductionFunction prodFun = new CobbDouglasProduction(FONDUE, factor, new Weight(SWISSTIME, alpha), new Weight(ITALTIME, beta));
		OldProducer firm = new OldProducer(this, end, prodFun, new DifferentialDividend()){

			@Override
			public IBelief createPriceBelief(Good good) {
				if (good.equals(SWISSTIME)){
					return new HardcodedBelief(hourPrice1);
				} else if (good.equals(ITALTIME)){
					return new HardcodedBelief(hourPrice2);
				} else {
					return new HardcodedBelief(fonduePrice);
				}
			}
			
		};
		firm.offer(new IPriceMakerMarket() {
			
			@Override
			public void offer(Ask offer) {
				assert offer.getPrice().getPrice() == fonduePrice;
				offer.accept(null, new Stock(MONEY, 100000), new Stock(offer.getGood()), offer.getQuantity());
			}
			
			@Override
			public void offer(Bid offer) {
				assert offer.getPrice().getPrice() == hourPrice1 || offer.getPrice().getPrice() == hourPrice2;
				offer.accept(null, new Stock(MONEY), new Stock(offer.getGood(), offer.getAmount()), offer.getQuantity());
			}

			@Override
			public void addMarketListener(IMarketListener listener) {
			}

		});
		double production = produce(firm);
		System.out.println("Produced " + production);
		double x1 = Math.pow(factor * fonduePrice*Math.pow(alpha / hourPrice1, 1 - beta)*Math.pow(beta / hourPrice2, beta), 1/(1 - alpha - beta));
		double x2 = hourPrice1 / hourPrice2 * beta / alpha * x1;
		Quantity production2 = prodFun.produce(new Inventory(MONEY, new Stock(SWISSTIME, x1), new Stock(ITALTIME, x2)));
		System.out.println(production2);
		assert Numbers.equals(production, production2.getAmount());
	}
	
	@Override
	public int previewNextId() {
		return 1;
	}

	@Override
	public int createUniqueAgentId() {
		return 1;
	}

	@Override
	public Random getRand() {
		throw new RuntimeException("Not implemented");
	}
	
}
