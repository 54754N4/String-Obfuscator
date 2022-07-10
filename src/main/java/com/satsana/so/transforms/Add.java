package com.satsana.so.transforms;

import com.satsana.so.transforms.model.Transformation;

public class Add extends Transformation {
	private final long value;
	
	public Add(long value, int maxBits) {
		super(maxBits);
		this.value = value;
	}

	@Override
	public long transform(long i) {
		if (i > max() - value)
			throw new ArithmeticException("Additive overflow");
		return i+value;
	}

	@Override
	public Transformation reversed() {
		return new Substract(value, maxBits());
	}
	
	public long getValue() {
		return value;
	}
}
