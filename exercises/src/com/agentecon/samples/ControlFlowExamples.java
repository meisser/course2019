package com.agentecon.samples;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ControlFlowExamples {

	// if then else structure
	public String getWord1(int x) {
		if (x == 1) {
			return "one";
		} else if (x == 2) {
			return "two";
		} else {
			return "many";
		}
	}

	// switch statement (rarely used)
	public String getWord2(int x) {
		switch (x) {
		case 1:
			return "one";
		case 2:
			return "two";
		default:
			return "many";
		}
	}

	public boolean containsShort(Collection<String> list, String keyword) {
		for (String element : list) {
			if (element.equals(keyword)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsClassic(List<String> list, String keyword) {
		// for loop has three parts: for (initialization, condition, action)
		// see example below for equivalent loop with "while"
		for (int i = 0; i < list.size(); i++) {
			String element = list.get(i);
			if (element.equals(keyword)) {
				return true;
			}
		}
		return false;
	}

	// same as above
	public boolean containsWhile(List<String> list, String keyword) {
		int i = 0;
		while (i < list.size()) {
			String element = list.get(i);
			if (element.equals(keyword)) {
				return true;
			}
			i++;
		}
		return false;
	}

	public boolean containsWithIterator(Collection<String> list, String keyword) {
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) { // while loop is repeated until condition not valid any more
			String element = iter.next();
			if (element.equals(keyword)) {
				return true;
			}
		}
		return false;
	}

	public String exceptionExample(int x) {
		try {
			while (true) { // loop forever
				check(x++);
			}
		} catch (Exception e) {
			System.out.println("We caught an exception");
			return "x was " + x;
		} finally {
			System.out.println("This is always executed, but it does not affect the return value any more");
			x = 42;
		}
	}

	private void check(int i) throws Exception {
		if (i == 7) {
			/**
			 * Exceptions are used to gracefully handle exceptional conditions.
			 * For example, when reading a file and it suddenly ends or when accidentally dividing by zero.
			 * They are an advanced feature and you won't need to use them. But you should also not be
			 * scared when encountering them.
			 */
			throw new Exception("Seven reached!");
		}
	}
	
	public static void main(String[] args) {
		ControlFlowExamples examples = new ControlFlowExamples();
		System.out.println(examples.getWord2(2));
		System.out.println(examples.exceptionExample(-5));
	}

}
