package com.agentecon.sim;

import java.io.IOException;

import org.junit.Test;

import com.agentecon.Simulation;

public class SimulationTest extends SimulationListenerAdapter {
	
	@Test
	public void test() throws IOException {
		Simulation sim = new Simulation();
		sim.run();
	}
}
