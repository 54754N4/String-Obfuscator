package com.satsana.so.engine.transforms;

import com.satsana.so.engine.transforms.model.Rotation;
import com.satsana.so.engine.transforms.model.Transformation;

public class RotateRight extends Rotation {
	public RotateRight(long value, int maxBits) {
		super(value, maxBits);
	}

	@Override
	public long transform(long i) {
		return (((i & getMask()) << lhs()) | (i >> rhs())) & getMask();
	}

	@Override
	public Transformation reversed() {
		return new RotateLeft(getValue(), maxBits());
	}
}