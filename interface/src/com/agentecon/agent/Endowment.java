package com.agentecon.agent;

import java.util.Arrays;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;

public class Endowment {

	public static final int HOURS_PER_DAY = 24;
	
	private Good money;
	private IStock[] initial;
	private IStock[] daily;
	
	public Endowment(IStock wallet) {
		this(wallet.getGood(), new IStock[]{wallet}, new IStock[]{});
	}
	
	public Endowment(Good money, IStock... daily) {
		this(money, new IStock[]{}, daily);
	}
		
	public Endowment(Good money, IStock[] initial, IStock[] daily) {
		this.initial = initial;
		this.daily = daily;
		this.money = money;
	}

	public Inventory getInitialInventory() {
		return new Inventory(money, clone(initial));
	}
	
	public IStock[] getDaily(){
		return clone(daily);
	}
	
	public static IStock[] clone(IStock[] daily) {
		IStock[] copy = new IStock[daily.length];
		for (int i=0; i<copy.length; i++){
			copy[i] = daily[i].duplicate();
		}
		return copy;
	}
	
	@Override
	public String toString() {
		return "Initial: " + Arrays.toString(initial) + ", daily: " + Arrays.toString(daily);
	}

}
