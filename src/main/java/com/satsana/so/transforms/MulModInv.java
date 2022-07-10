package com.satsana.so.transforms;

import com.satsana.so.transforms.model.Transformation;

/* long c;
 	forward = (a*c)%m;
	reverse = (inv*forward)%m == c
 */
public class MulModInv extends MulMod {
	private long initial;
	
	public MulModInv(long value, long modulo, int maxBits) {
		super(modInverse(value, modulo), modulo, maxBits);
		initial = value;
	}
	
	@Override
	public Transformation reversed() {
		return new MulMod(initial, getModulo(), maxBits());
	}
}
