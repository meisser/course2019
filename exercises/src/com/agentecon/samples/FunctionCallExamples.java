package com.agentecon.samples;

import java.util.ArrayList;
import java.util.Collection;

public class FunctionCallExamples {

	private String name;
	private int callCount;

	public FunctionCallExamples(String name) {
		this.name = name;
		this.callCount = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
		this.callCount++;
	}
	
	public void demonstrateOutsideEffect1() {
		int x = 7;
		callByValue(x);
		System.out.println(x);
	}

	// primitive types like int or double are copied when calling a function
	public void callByValue(int x) {
		// this has no influence on the outside
		x = x + 1;
	}
	
	public void demonstrateOutsideEffect2() {
		ArrayList<String> newList = new ArrayList<String>();
		newList.add("Hello");
		callByReference(newList);
		for (String s: newList) {
			System.out.print(s + " ");
		}
		System.out.println();
	}
	
	public void callByReference(Collection<String> listOfStrings) {
		// doing something with the list is visible outside
		listOfStrings.add("World");
	}
	
	public void demonstrateOutsideEffect3(FunctionCallExamples other) {
		other.name = "Third"; // outside effect
		other = new FunctionCallExamples("Forth"); // no outside effect
		other.name = "Fifth"; // no outside effect either, we are working on 'forth'
	}

	public static void main(String[] args) {
		FunctionCallExamples instanceOne = new FunctionCallExamples("First");
		FunctionCallExamples instanceTwo = new FunctionCallExamples("Second");
		System.out.println(instanceOne.getName()); // printing the name of the first instance
		System.out.println(instanceTwo.getName()); // printing the name of the second instance
		System.out.println(instanceTwo.name); // direct access to field "name" is possible, but considered bad style
		instanceOne.demonstrateOutsideEffect1();
		instanceOne.demonstrateOutsideEffect2();
		instanceOne.demonstrateOutsideEffect3(instanceTwo);
		System.out.println(instanceTwo.getName()); // Name has changed
	}

}
