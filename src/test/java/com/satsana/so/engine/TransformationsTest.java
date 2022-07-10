package com.satsana.so.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.satsana.so.transforms.MulMod;
import com.satsana.so.transforms.Not;
import com.satsana.so.transforms.Permutation;
import com.satsana.so.transforms.RotateLeft;
import com.satsana.so.transforms.model.Rotation;
import com.satsana.so.transforms.model.Transformation;

class TransformationsTest {
	@Test
	void testPermutation() {
		Transformation perm = new Permutation(0, 3, 2, 16),
				reverse = perm.reversed();
		for (long x=30; x<100; x++) {
			long temp = perm.transform(x);
			temp = reverse.transform(temp);
			assertEquals(x, temp, x + " & "+temp + "are not equal");
		}
	}
	
	@Test
	void testNeg() {
		Transformation not = new Not(32),
				rev = not.reversed();
		for (long x=0; x<1000; x++) {
			long temp = not.transform(x);
			temp = rev.transform(temp);
			assertEquals(x, temp, x + " & "+temp + "are not equal");
		}
	}
	
	@Test
	void testMulMod() {
		MulMod mm = new MulMod(3, 11, 16); 
		Transformation mmi = mm.reversed();
		long c = 5, 
			temp = mm.transform(c);
		temp = mmi.transform(temp);
		assertEquals(c, temp, c + " & "+temp + "are not equal");
	}
	
	@Test
	void testRotations() {
		Rotation r = new RotateLeft(1, 16);
		long v = 10, 
			temp = r.transform(v);
		Rotation rr = (Rotation) r.reversed();
		temp = r.reversed().transform(temp);
		assertEquals(r.lhs(), rr.lhs());
		assertEquals(r.rhs(), rr.rhs());
		assertEquals(v, temp);
	}
}
