package com.satsana.so.model;

public class Context {
	private final int maxBits;
	private final long[] bytes;
	private final long mask;
	private final TransformationChain forward, reverse;
	
	public Context(int maxBits, long[] bytes, long mask, TransformationChain forward, TransformationChain reverse) {
		this.maxBits = maxBits;
		this.bytes = bytes;
		this.mask = mask;
		this.forward = forward;
		this.reverse = reverse;
	}

	public int getMaxBits() {
		return maxBits;
	}
	
	public long getMask() {
		return mask;
	}
	
	public long[] getBytes() {
		return bytes;
	}

	public TransformationChain getForward() {
		return forward;
	}

	public TransformationChain getReverse() {
		return reverse;
	}
}
