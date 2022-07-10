package com.satsana.so.visitors;

import java.util.concurrent.ThreadLocalRandom;

public abstract class LanguageVisitor implements Visitor<StringBuilder> {
	private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
	private static final int NAME_MIN = 4, NAME_MAX = 10;
	private static final String DEFAULT_ALPHABET;
	
	static {
		StringBuilder sb = new StringBuilder();
		sb.append("_");
		for (char c = 'a'; c <= 'z'; c++)
			sb.append(c).append((char) (c ^ 32)); // lowercase and uppercase
		DEFAULT_ALPHABET = sb.toString();
	}
	
	public static String generateName(String alphabet) {
		int size = RANDOM.nextInt(NAME_MIN, NAME_MAX+1);
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<size; i++)
			sb.append(DEFAULT_ALPHABET.charAt(RANDOM.nextInt(DEFAULT_ALPHABET.length())));
		return sb.toString();
	}
	
	public static String generateName() {
		return generateName(DEFAULT_ALPHABET);
	}
	
	public String hex(long l) {
		return "0x"+Long.toHexString(l);
	}
	
	public static int nextInt(int origin, int bound) {
		return RANDOM.nextInt(origin, bound);
	}
}
