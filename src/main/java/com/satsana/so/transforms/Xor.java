package com.satsana.so.transforms;

import com.satsana.so.transforms.model.Transformation;

public class Xor extends Transformation {
	private final long value;
	
	public Xor(long value, int maxBits) {
		super(maxBits);
		this.value = value;
	}

	@Override
	public long transform(long i) {
		return i ^ value;
	}

	@Override
	public Transformation reversed() {
		return this;
	}
	
	public long getValue() {
		return value;
	}
}
