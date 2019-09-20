package com.agentecon.configuration;

import java.util.ArrayList;

import com.agentecon.Simulation;
import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.goods.Good;
import com.agentecon.goods.Stock;
import com.agentecon.production.IProductionFunction;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.verification.PriceMetric;

public class CobbDougConfiguration implements IConfiguration {

	public static final Good MONEY = new Good("Taler");

	public static final int ROUNDS = 1000;
	public static final int WOBBLES = 0;
	public static final int MAX_ITERATIONS = 1;

	protected int iteration = 0;
	protected int firmsPerType;
	protected int consumersPerType;
	private int seed;

	protected Good[] inputs, outputs;

	protected ArrayList<SimEvent> constantEvents;
	protected ArrayList<EvolvingEvent> evolvingEvents;

	public CobbDougConfiguration(int seed) {
		this(5, 20, 5, 4, seed);
	}

	public CobbDougConfiguration(int firmsPerType, int consumersPerType, int consumerTypes, int firmTypes, int seed) {
		this.firmsPerType = firmsPerType;
		this.consumersPerType = consumersPerType;
		this.seed = seed;
		this.evolvingEvents = new ArrayList<>();
		this.constantEvents = new ArrayList<>();
		this.createGoods(consumerTypes, firmTypes);
		// PriceFactory.NORMALIZED_GOOD = outputs[0];
	}

	protected void createGoods(int consumerTypes, int firmTypes) {
		this.inputs = new Good[consumerTypes];
		for (int i = 0; i < consumerTypes; i++) {
			inputs[i] = new Good("input " + i, 0.0);
		}
		this.outputs = new Good[firmTypes];
		for (int i = 0; i < firmTypes; i++) {
			outputs[i] = new Good("output " + i, 1.0);
		}
	}

	public SimulationConfig createNextConfig() {
		constantEvents.clear();
		ArrayList<EvolvingEvent> onlyUsedInInitialIteration = iteration == 0 ? this.evolvingEvents : new ArrayList<EvolvingEvent>();
		addFirms(constantEvents, onlyUsedInInitialIteration, new ProductionWeights(inputs, outputs));
		addConsumers(constantEvents, onlyUsedInInitialIteration, new ConsumptionWeights(inputs, outputs));

		// constantEvents.add(new TaxEvent(TAX_EVENT, 0.2));
		// constantEvents.add(new MoneyPrintEvent(1000, 1, 63));
		//
		// constantEvents.add(new MoneyPrintEvent(2000, 3, 20));
		// for (int i=1000; i<ROUNDS; i+=2000){
		// constantEvents.add(new MoneyPrintEvent(2000, 1, 1000));
		// }
		// for (int i=5000; i<10000; i+=250){
		// constantEvents.add(new MoneyPrintEvent(i, 100, 10));
		// }
		if (iteration > 0) {
			ArrayList<EvolvingEvent> newList = new ArrayList<>();
			for (EvolvingEvent ee : evolvingEvents) {
				newList.add(ee.createNextGeneration());
			}
			evolvingEvents = newList;
		}
		SimulationConfig config = new SimulationConfig(ROUNDS, seed, WOBBLES);
		for (SimEvent event : constantEvents) {
			config.addEvent(event);
		}
		for (SimEvent event : evolvingEvents) {
			config.addEvent(event);
		}
		iteration++;
		return config;
	}

	public String getComment() {
		String c = "";
		for (EvolvingEvent ee : evolvingEvents) {
			if (c.length() > 0) {
				c += "\n";
			}
			c += ee.toString();
		}
		return c;
	}

	protected void addConsumers(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, ConsumptionWeights defaultPrefs) {
		for (int i = 0; i < inputs.length; i++) {
			Endowment end = new Endowment(MONEY, new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			final int type = i;
			config.add(new ConsumerEvent(consumersPerType, end, defaultPrefs.getFactory(i)) {
				@Override
				protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util) {
					return new Consumer(id, end, util) {
						@Override
						protected String inferType(Class<? extends Agent> clazz) {
							return "Consumer-Type-" + type;
						}
					};
				}
			});
		}
	}

	protected void addFirms(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, ProductionWeights prod) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(MONEY, new Stock[] { new Stock(MONEY, 1000), new Stock(outputs[i], 10) }, new Stock[] {});
			IProductionFunction fun = prod.createProdFun(i, 0.7);
			config.add(new FirmEvent(firmsPerType, "Firm " + i, end, fun));
			// newList.add(new EvolvingFirmEvent(firmsPerType, "Firm " + i, end, fun, new Random(rand.nextLong()), PriceFactory.SENSOR, "0.05"));
		}
	}

	public boolean shouldTryAgain() {
		return iteration < MAX_ITERATIONS;
	}

	public double getScore() {
		double tot = 0.0;
		for (EvolvingEvent ae : evolvingEvents) {
			tot += ae.getScore();
		}
		return tot;
	}

	public static void main(String[] args) {
		CobbDougConfiguration config = new CobbDougConfiguration(5, 20, 5, 4, 13);
		Simulation sim = new Simulation(config);
		PriceMetric metric = new PriceMetric(500);
		sim.addListener(metric);
		sim.run();
		metric.printResult(System.out);
	}

}
