package com.satsana.so.transforms;

import com.satsana.so.transforms.model.Transformation;

public class Permutation extends Transformation {
	private final long pos1, pos2, bits;
	
	public Permutation(int pos1, int pos2, int bits, int maxBits) {
		super(maxBits);
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.bits = bits;
		if (Math.max(pos1, pos2) + bits > maxBits)
			throw new ArithmeticException("Invalid ranges");
	}

	@Override
	public long transform(long i) {
		long xor = ((i >> pos1) ^ (i >> pos2)) & ((1l << bits) - 1l);
		return i ^ ((xor << pos1) | (xor << pos2));
	}

	@Override
	public Transformation reversed() {
		return this;
	}
	
	public long getPosition1() {
		return pos1;
	}
	
	public long getPosition2() {
		return pos2;
	}
	
	public long getBits() {
		return bits;
	}
}
