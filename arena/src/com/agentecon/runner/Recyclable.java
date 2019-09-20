package com.agentecon.runner;

public abstract class Recyclable<T> {
	
	private T item;
	
	public Recyclable(T item){
		this.item = item;
	}
	
	public T getItem(){
		return item;
	}
	
	public final void recycle(){
		recycle(item);
	}

	protected abstract void recycle(T item);

}
