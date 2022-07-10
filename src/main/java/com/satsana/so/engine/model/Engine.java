package com.satsana.so.engine.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Engine {
	Context transform(String text) throws Exception;
	
	/* Convenience methods */
	default Context transform(Path path) throws Exception {
		return transform(Files.readString(path));
	}
	
	default Context transform(File file) throws Exception {
		return transform(file.toPath());
	}
}
