package com.satsana.so.transformations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiFunction;

import org.apache.logging.log4j.util.TriConsumer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.satsana.so.engine.transforms.MulMod;
import com.satsana.so.engine.transforms.Not;
import com.satsana.so.engine.transforms.Permutation;
import com.satsana.so.engine.transforms.RotateLeft;
import com.satsana.so.engine.transforms.model.Rotation;
import com.satsana.so.engine.transforms.model.Transformation;

class TransformationsTest {
	private Logger logger = LoggerFactory.getLogger(TransformationsTest.class);
	
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
	
	@Test
	void testCoprimeGeneration() {
		/* All pair of positive coprime numbers (m,n) (with m > n) can be arranged 
		 * in two disjoint complete ternary trees starting from (2,1) and (3,1).
		 * The three branches are as follow : (2m-n, m), (2m+n, m), (m+2n, n)
		 * Ref: https://en.wikipedia.org/wiki/Coprime_integers
		 */
		final long limit = 1000;
		BiFunction<Long, Long, Long> gcd = Transformation::gcd,
				modInv = Transformation::modInverse;
		TriConsumer<String, Long, Long> tester = (s, m, n) -> {
			long inv = modInv.apply(n, m);
			assertTrue(m > n, m +" is not bigger than "+n);
			assertTrue(gcd.apply(m, n) == 1);
			assertDoesNotThrow(() -> inv);
			logger.debug("{} ({}, {}) mod inv of {} [{}] = {}", s, m, n, n, m, inv);
		};
		TriConsumer<String,Long, Long> seededTest = (s, m, n) -> {
			long im = m, in = n,
				pm, pn;
			// Branch 1
			while (m < limit) {
				pm = m; pn = n;
				tester.accept(s+"Branch 1",m, n);
				m = (pm << 1) - pn;	// 2m-n
				n = pm;	// m
			}
			m = im; n = in;
			// Branch 2
			while (m < limit) {
				pm = m; pn = n;
				tester.accept(s+"Branch 2",m, n);
				m = (pm << 1) + pn;	// 2m+n
				n = pm;	// m
			}
			m = im; n = in;
			// Branch 3
			while (m < limit) {
				pm = m; pn = n;
				tester.accept(s+"Branch 3",m, n);
				m = pm + (pn << 1); // m+2n
				n = pn;	// n
			}
		};
		// Even-odd/ood-even pairs starting with (2,1)
		seededTest.accept("Even-odds ", 2l,1l);
		// Odd-odd pairs starting with (3,1)
		seededTest.accept("Odd-odds ", 3l,1l);
	}
}
