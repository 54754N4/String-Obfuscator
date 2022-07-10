package com.satsana.so.engine.transforms.model;

public abstract class Modulus extends Transformation {
	private final long value, modulo;
	
	public Modulus(long value, long modulo, int maxBits) {
		super(maxBits);
		this.value = value;
		this.modulo = modulo;
	}
	
	public long getValue() {
		return value;
	}
	
	public long getModulo() {
		return modulo;
	}
}
