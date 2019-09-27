package com.agentecon.samples;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class InheritanceExample extends ArrayList<String> { // inherit all the functionality from ArrayList
	
	@Override // The override tag signals that we are 
    public int size() {
		// The old method is still accessible using super
        return super.size() + 1; // Let's lie about the size and always return one more :)
    }
	
	public static void main(String[] args) {
		ArrayList<String> list = new InheritanceExample(); // InheritanceExample is an ArrayList
		list.add("first entry");
		// Call to size() executes the method defined in InheritanceExample as the list actually is of that type
		// From the outside, it is not possible anymore to access the original "size()" method
		System.out.println("What's you size? " + list.size() + "!");
	}
	
}
