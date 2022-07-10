package com.satsana.so.transforms;

import com.satsana.so.transforms.model.Transformation;

public class Substract extends Transformation {
	private final long value;
	
	public Substract(long value, int maxBits) {
		super(maxBits);
		this.value = value;
	}

	@Override
	public long transform(long i) {
		if (i < value)
			throw new ArithmeticException("Substraction underflow");
		return i - value;
	}

	@Override
	public Transformation reversed() {
		return new Add(value, maxBits());
	}

	public long getValue() {
		return value;
	}
}
