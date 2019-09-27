package com.agentecon.samples;

import com.agentecon.util.MovingAverage;

public class MathExamples {

	public void someIntOperations(int input) {
		// (comments assume that input=1000)

		int x = 13; // integers (int) are whole numbers between about -2 billion and +2 billion

		// When doing a potentially lossy assignment, the numer must be "cast" to the
		// correct type first
		short y = (short) input; // shorts are whole numbers between about -16000 and +16000

		byte z = -13; // bytes are whole numbers between -127 and +128

		// assigning a byte to an integer is no problem, as there can't be an overflow
		x = y; // x is still an integer, but now has the value 1000

		// since z can store at most 128, we get an overflow
		z = (byte) y; // z is still a byte, but now has the value -24 (= 1000 - 4*256)
		System.out.println("x: " + x + ", z: " + z);

		int sameagain = 2 * x / (x + x); // multiplication and division
		int seven = 27 % 20; // modulo operator
	}

	public void someFloatOperations(double d) {
		float f = (float) d; // floats have lower precision than doubles, you should use doubles
		double large = 1000000.0;
		double small = 0.0000001;
		System.out.println("Large: " + large);
		System.out.println("Small: " + small);
		System.out.println("Large + small: " + (large + small));
		double verysmall = small / 10000;
		System.out.println("Large + very small: " + (large + verysmall));
		assert large + verysmall == large;

		// The Math class has many useful operations and constants
		double squareRoot = Math.sqrt(large);
		double whatver = Math.cos(squareRoot) * Math.PI;
		double one = Math.log(Math.E);
		double thousand = Math.pow(10, 3);

		// A class of mine that might be useful
		MovingAverage mov = new MovingAverage(0.9);
		for (int i = 0; i < 100; i++) {
			mov.add(0.0);
		}
		mov.add(10);
		System.out.println("Should be 1, but is a little less due to rounding errors: " + mov.getAverage());
	}

	public void increments() {
		int x = 13;
		x = x + 1; // add one
		x += 1; // like the above, but shorter
		x++; // like the above, but even shorter

		int sixteen = (x++); // the ++ operator reads the number first and then increments it
		int seventeen = x; // now x is one higher
	}
	
	public String someEqualityOperators(int x, int y) {
		if (x < y) {
			return "x is smaller than y";
		} else if (x >= y) {
			// x is smaller or equal
			if (x == y) {
				return "x is equal to y";
			} else {
				assert x > y;
				return "x must be larger than y";
			}
		} else {
			throw new RuntimeException("This can never happen");
		}
	}

	public static void main(String[] args) {
		MathExamples samples = new MathExamples();
		samples.someIntOperations(1000);
		samples.someFloatOperations(3.141);
	}

}
