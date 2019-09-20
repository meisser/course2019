package com.agentecon.consumer;

import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;

public class Inheritance implements IShareholder {
	
	private Inventory inventory;
	private Portfolio portfolio;
	
	public Inheritance(Inventory inventory, Portfolio portfolio) {
		this.inventory = inventory;
		this.portfolio = portfolio;
	}

	public IStock getMoney() {
		return inventory.getMoney();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public void absorb(Inheritance left) {
		this.inventory.absorb(left.inventory);
		this.portfolio.absorb(left.portfolio);
	}
	
	public Inheritance getFraction(double share) {
		Inventory inv = new Inventory(inventory.getMoney().getGood());
		inv.absorb(share, this.inventory);
		Portfolio port = new Portfolio(inv.getMoney(), false);
		port.absorbPositions(share, portfolio);
		return new Inheritance(inv, port);
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}
	
	public Inheritance clone() {
		throw new RuntimeException("Not yet implemented");
	}

}
