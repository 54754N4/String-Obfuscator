package com.satsana.so.engine.transforms.model;

public abstract class Transformation {
	private final int maxBits;
	private final long mask;
	
	public Transformation(int maxBits) {
		this.maxBits = maxBits;
		mask = (1l << maxBits) - 1l;	// avoid sign extension to keep 64 bits
	}
	
	public long getMask() {
		return mask;
	}
	
	public int maxBits() {
		return maxBits;
	}
	
	public long max() {
		return 1l << maxBits;
	}
	
	public abstract long transform(long i);
	public abstract Transformation reversed();
	
	/* Helper methods */
	
	public static long gcd(long a, long b) {	// steins algorithm
		if (a == 0)
	        return b;
	    if (b == 0)
	        return a;
	    int n;
	    for (n = 0; ((a | b) & 1) == 0; n++) {
	        a >>= 1;
	        b >>= 1;
	    }
	    while ((a & 1) == 0)
	        a >>= 1;
	    do {
	        while ((b & 1) == 0)
	            b >>= 1;
	        if (a > b) {
	            long temp = a;
	            a = b;
	            b = temp;
	        }
	        b = (b - a);
	    } while (b != 0);
	    return a << n;
	}
	
	public static long modInverse(long a, long m) {
		if (a%m == 0)
			throw new ArithmeticException("Modular inverse can't be calculated when a/m");
		if (gcd(a, m) != 1)
			throw new ArithmeticException("Modular inverse exists only if a and m are coprime");
		long m0 = m;
		long y = 0, x = 1;
		if (m == 1)
			return 0;
		while (a > 1) {
			long q = a / m,
				t = m;
			m = a % m;
			a = t;
			t = y;
			y = x - q * y;
			x = t;
		}
		if (x < 0)
			x += m0;
		return x;
	}
}
