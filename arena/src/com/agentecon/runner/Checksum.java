// Created on Jun 24, 2015 by Luzius Meisser

package com.agentecon.runner;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Checksum {

	private byte[] checksum;
	
	public Checksum(byte[] data){
		long t0 = System.nanoTime();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			this.checksum = md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new java.lang.RuntimeException(e);
		} finally {
			long t1 = System.nanoTime();
			long ys = (t1 - t0) / 1000;
			System.out.println("Generated checksum for " + data.length + " bytes in " + ys + " ys");
		}
	}
	
	@Override
	public boolean equals(Object o){
		Checksum ot = (Checksum)o;
		if (ot == null){
			return false;
		} else {
			return Arrays.equals(checksum, ot.checksum);
		}
	}
	
	public long generateId(){
		ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
		for (int i=0; i<checksum.length; i++){
			buf.put(i%Long.BYTES, checksum[i]);
		}
		return buf.getLong();
	}
	
	@Override
	public String toString(){
		BigInteger bi = new BigInteger(1, checksum);
	    return String.format("%0" + (checksum.length << 1) + "x", bi);
	}
	
}
