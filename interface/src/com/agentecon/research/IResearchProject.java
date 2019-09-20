package com.agentecon.research;

import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.production.IProductionFunction;

public interface IResearchProject {
	
	public Quantity getHoursLeft();
	
	public void invest(IStock hours, double amount);
	
	public IProductionFunction complete();

}
