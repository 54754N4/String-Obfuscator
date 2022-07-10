package com.satsana.so.engine.transforms;

import com.satsana.so.engine.transforms.model.Transformation;

public class Not extends Transformation {
	public Not(int maxBits) {
		super(maxBits);
	}
	
	@Override
	public long transform(long i) {
		return ~i & ((1l << maxBits()) - 1);
	}

	@Override
	public Transformation reversed() {
		return this;
	}
}
