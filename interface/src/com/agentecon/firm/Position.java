package com.agentecon.firm;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;

public class Position extends Stock {
	
	private Stock dividends;
	private boolean consumer;
	private boolean disposed;
	private IRegister register;
	
	@Deprecated
	public Position(IRegister register, Ticker ticker, Good currency, double shares) {
		this(register, ticker, currency, shares, true);
	}
	
	public Position(IRegister register, Ticker ticker, Good currency, double shares, boolean consumer) {
		super(ticker, shares);
		this.consumer = consumer;
		this.disposed = false;
		this.register = register;
		this.dividends = new Stock(currency);
	}
	
	public Good getCurrency(){
		return dividends.getGood();
	}
	
	public double getUncollectedDividends() {
		return dividends.getAmount();
	}
	
	@Override
	public void transfer(IStock source, double amount) {
		super.transfer(source, amount);
	}
	
	@Deprecated
	public Position createNewPosition(){
		return createNewPosition(true);
	}
	
	public Position createNewPosition(boolean consumer){
		assert !disposed;
		double prevAmount = getAmount();
		Position pos = register.createPosition(consumer);
		assert pos.getAmount() + getAmount() == prevAmount;
		return pos;
	}
	
	@Override
	public void absorb(IStock s) {
		dividends.absorb(((Position)s).dividends);
		super.absorb(s);
	}
	
	public Ticker getTicker(){
		return (Ticker) super.getGood();
	}
	
	public void receiveDividend(IStock firmWallet, double perShare){
		assert !disposed;
		double myShare = perShare * getAmount();
		dividends.transfer(firmWallet, myShare);
	}
	
	public void collectDividend(IStock holderWallet){
		assert !disposed;
		holderWallet.absorb(dividends);
	}
	
	public double getOwnershipShare(){
		return getAmount() / IRegister.SHARES_PER_COMPANY;
	}
	
	public boolean isDisposed(){
		return disposed;
	}
	
	public void dispose(Position target){
		assert !disposed;
		target.absorb(this);
		dispose();
	}
	
	public void dispose(){
		assert !disposed;
		assert isEmpty();
		this.disposed = true;
	}
	
	@Override
	public IStock hide(double amount) {
		throw new RuntimeException();
	}
	
	@Override
	public IStock hideRelative(double fraction) {
		throw new RuntimeException();
	}
	
	@Override
	public Position duplicate() {
		assert dividends.isEmpty();
		return this;
	}

	public boolean isConsumerPosition() {
		return consumer;
	}
	
}
