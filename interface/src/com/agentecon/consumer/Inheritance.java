package com.agentecon.consumer;

import java.util.ArrayList;

import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;

public class Inheritance implements IShareholder {

	private Inventory inventory;
	private Portfolio portfolio;
	
	public Inheritance(Inheritance parent, double ratio, boolean consumer) {
		this.inventory = new Inventory(parent.inventory.getMoney().getGood());
		this.inventory.absorb(ratio, parent.inventory);
		this.portfolio = new Portfolio(this.inventory.getMoney(), consumer);
		this.portfolio.absorbPositions(ratio, parent.portfolio);
	}

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

	public boolean hasSomething() {
		return inventory.hasSomething() || portfolio.hasSomething();
	}

	public Inheritance clone() {
		throw new RuntimeException("Not yet implemented");
	}

	public void distributeEvenly(ArrayList<IConsumer> consumers) {
		int size = consumers.size();
		for (int i = 0; i < size - 1; i++) {
			double ratio = 1.0 / (size - i);
			Inheritance temp = new Inheritance(this, ratio, false);
			IConsumer current = consumers.get(i);
			current.inherit(temp);
		}
		consumers.get(consumers.size() - 1).inherit(this);
	}
	
	@Override
	public String toString() {
		return inventory.toString() + " " + portfolio.toString();
	}

}
