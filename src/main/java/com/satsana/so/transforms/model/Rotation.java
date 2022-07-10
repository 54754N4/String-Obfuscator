package com.satsana.so.transforms.model;

public abstract class Rotation extends Transformation {
	private final long value;
	
	public Rotation(long value, int maxBits) {
		super(maxBits);
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	/* Both rotations use the same left/right hand sides */
	
	public long lhs() {
		return ((long) maxBits()) - getValue();
	}
	
	public long rhs() {
		return getValue();
	}
}
