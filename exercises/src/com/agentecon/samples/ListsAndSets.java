package com.agentecon.samples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

public class ListsAndSets {
	
	// [] denotes an array, which is the most primitive type of "list".
	public static final String[] TERMS = new String[] {"alpha", "beta", "gamma", "gamma", "delta", "epsilon"};
	
	private ArrayList<String> list; // insertion order is preserved
	private HashSet<String> set; // cannot contain duplicates, no defined order
	private TreeSet<String> orderedSet; // ordered set
	
	public ListsAndSets() {
		this.set = new HashSet<String>();
		fill(set);
		
		this.list = new ArrayList<String>();
		fill(list);
		
		this.orderedSet = new TreeSet<String>();
		fill(orderedSet);
	}
	
	// Method works with lists and with sets
	private void fill(Collection<String> collection) {
		for (String term: TERMS) {
			collection.add(term);
		}
	}
	
	public void printAll() {
		System.out.print("List: ");
		print(this.list);
		System.out.print("Set: ");
		print(this.set);
		System.out.print("Ordered Set: ");
		print(this.orderedSet);
	}
	
	private void print(Collection<String> collection) {
		for (String s: collection) {
			System.out.print(s + " ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		ListsAndSets instance = new ListsAndSets();
		instance.printAll();
	}

}
