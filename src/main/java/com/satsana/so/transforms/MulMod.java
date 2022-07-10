package com.satsana.so.transforms;

import com.satsana.so.transforms.model.Modulus;
import com.satsana.so.transforms.model.Transformation;

public class MulMod extends Modulus {
	public MulMod(long value, long modulo, int maxBits) {
		super(value, modulo, maxBits);
	}

	@Override
	public long transform(long i) {
		if (i != (i*getValue())/getValue() || i * getValue() >= max())
			throw new ArithmeticException("Multiplicative overflow");
		return (i*getValue()) % getModulo();
	}

	@Override
	public Transformation reversed() {
		return new MulModInv(getValue(), getModulo(), maxBits());
	}
}
