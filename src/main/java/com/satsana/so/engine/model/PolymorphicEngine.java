package com.satsana.so.engine.model;

import java.util.concurrent.ThreadLocalRandom;

import com.satsana.so.engine.transforms.Add;
import com.satsana.so.engine.transforms.MulMod;
import com.satsana.so.engine.transforms.MulModInv;
import com.satsana.so.engine.transforms.Not;
import com.satsana.so.engine.transforms.Permutation;
import com.satsana.so.engine.transforms.RotateLeft;
import com.satsana.so.engine.transforms.RotateRight;
import com.satsana.so.engine.transforms.Substract;
import com.satsana.so.engine.transforms.Xor;
import com.satsana.so.engine.transforms.model.Transformation;

public class PolymorphicEngine implements Engine {
	private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
	private final int maxBits, minOps, maxOps;
	private final long MASK, MULTIPLICATIVE_LIMIT, ADDITIVE_LIMIT;
	
	public PolymorphicEngine(int minOps, int maxOps, int maxBits) {
		this.maxBits = maxBits;
		this.minOps = minOps;
		this.maxOps = maxOps;
		MASK = (1l << maxBits) - 1;
		MULTIPLICATIVE_LIMIT = 1l << (maxBits / 2);
		ADDITIVE_LIMIT = 1l << (maxBits - 2);
	}
	
	/* Returns long[] instead of int[] because some 32/64 bit 
	 * transformations might overflow and we want to be able to 
	 * detect them easily. It will be downcasted in the end anyways.
	 * Note: Reads all in memory since array needs to be stored
	 * before generating decryption routine either way. */
	@Override
	public Context transform(String text) {
		long[] buffer = new long[text.length()];
		TransformationChain forward, reverse;
		long check;
		retry: while (true) {
			forward = generateForward();
			reverse = forward.reverse();
			for (int pos = 0; pos < buffer.length; pos++) {
				try {
					long c = text.charAt(pos);
					buffer[pos] = forward.apply(c);
					// dynamic check in case of implicit overflows
					check = reverse.apply(buffer[pos]).longValue();
					if ((char) check != (char) c)
						continue retry;
				} catch (ArithmeticException e) { // explicit overflow/underflow
					continue retry;
				}
				// valid range sanity check
				if (buffer[pos] < 0 || buffer[pos] >= max())
					continue retry;
			}
			break; // passed all sanity checks
		}
		return new Context(maxBits, buffer, MASK, forward, reverse);
	}

	private final TransformationChain generateForward() {
		TransformationChain forward = new TransformationChain();
		int total = RANDOM.nextInt(minOps, maxOps+1);
		for (int i=0; i<total; i++)
			forward.add(generateTransformation());
		return forward;
	}
	
	private final Transformation generateTransformation() {
		switch (RANDOM.nextInt(9)) {
			case 0:
				return new Add(nextLong(ADDITIVE_LIMIT), maxBits); 
			case 1:
				return mulMod();
			case 2:
				return mulModInv();
			case 3:
				return new Not(maxBits);
			case 4:
				return permutation();
			case 5:
				return new RotateLeft(nextLong(maxBits-1)+1, maxBits);
			case 6:
				return new RotateRight(nextLong(maxBits-1)+1, maxBits);
			case 7:
				return new Substract(nextLong(ADDITIVE_LIMIT), maxBits);
			default:
				return new Xor(randomMax(), maxBits);
		}
	}

	private final Transformation permutation() {
		int pos1, pos2, bits; 
		do {
			pos1 = (int) nextLong(maxBits);
			pos2 = (int) nextLong(maxBits);
			bits = (int) nextLong(maxBits-2)+2; // shouldn't be 0 or 1
		} while ((pos1 + bits) >= maxBits 
				|| (pos2 + bits) >= maxBits);
		return new Permutation(pos1, pos2, bits, maxBits);
	}
	
	private final Transformation mulMod() {
		MulMod mm;
		while (true) {
			mm = new MulMod(randomMax(), 1l << maxBits, maxBits);
			if (mm.getValue() == 1)
				continue;
			try {
				MulModInv mmi = (MulModInv) mm.reversed();
				if (mmi.getValue() > MULTIPLICATIVE_LIMIT)
					continue;
			} catch (Exception e) { // no inverse mod
				continue;
			}
			break;
		}
		return mm;
	}
	
	private final Transformation mulModInv() {
		MulModInv mmi;
		while (true) {
			try {
				mmi = new MulModInv(randomMax(), 1l << maxBits, maxBits);
				if (mmi.getValue() == 1)
					continue;
			} catch (Exception e) { // no inverse mod
				continue;
			}
			MulMod mm = (MulMod) mmi.reversed();
			if (mm.getValue() > MULTIPLICATIVE_LIMIT)
				continue;
			break;
		}
		return mmi;
	}
	
	/* Random generator */
	
	public static long nextLong(long bound) {
		return RANDOM.nextLong(bound);
	}
	
	private long randomMax() {
		return nextLong(max());
	}
	
	private long max() {
		return 1l << maxBits;
	}
	
	/* Accessors */
	
	public int getMaxBits() {
		return maxBits;
	}
}
