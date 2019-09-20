package com.agentecon.firm;

import com.agentecon.goods.Good;

// Immutable
public class Ticker extends Good {
	
	private final int number;
	private final String type;
	
	public Ticker(String type, int number) {
		super(generateSymbol(type) + number);
		this.type = type;
		this.number = number;
	}
	
	public int getNumer() {
		return number;
	}

	protected static String generateSymbol(String type) {
		int index = type.indexOf('-');
		if (index > 3){
			return type.substring(0, 3) + type.substring(index, index + 4);
		} else {
			return type.substring(0, 3);
		}
	}
	
	public String getType(){
		return type;
	}
	
	@Override
	public boolean equals(Object o){
		return number == ((Ticker)o).number;
	}
	
	@Override
	public int hashCode(){
		return number;
	}
	
}
