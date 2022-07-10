package com.satsana.so.engine.transforms;

import com.satsana.so.engine.transforms.model.Rotation;
import com.satsana.so.engine.transforms.model.Transformation;

public class RotateLeft extends Rotation {
	public RotateLeft(long value, int maxBits) {
		super(value, maxBits);
	}

	@Override
	public long transform(long i) {
		return (((i & getMask()) >> lhs()) | (i << rhs())) & getMask();
	}

	@Override
	public Transformation reversed() {
		return new RotateRight(getValue(), maxBits());
	}
}
