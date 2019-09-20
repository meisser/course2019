package com.agentecon.research;

import com.agentecon.goods.Good;
import com.agentecon.production.IProductionFunction;

public interface IInnovation {
	
	/**
	 * Returns a simple production function to produce the desired output
	 * or null if no such production function is configured.
	 */
	public IProductionFunction createProductionFunction(Good desiredOutput);
	
	/**
	 * Returns a research project to find a new production function.
	 * The research project requires man-hours to complete.
	 * 
	 * Can return null if the configuration does support research projects
	 * for the desired output good.
	 */
	public IResearchProject createResearchProject(Good desiredOutput);
	
}
