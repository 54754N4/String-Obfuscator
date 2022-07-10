package com.satsana.so.services;

import com.satsana.so.model.GenerationTarget;

public interface ObfuscationService {
	String generate(String message, int minOps, int maxOps, GenerationTarget target);
}
