package com.satsana.so.engines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.satsana.so.engine.visitors.PythonVisitor;
import com.satsana.so.engines.TestsUtil.ProcessOutput;
import com.satsana.so.model.Context;
import com.satsana.so.model.PolymorphicEngine;

/* Requires python to be installed */
@SpringBootTest
class PythonEngineTest {
	private static String COMPILER = "D:\\Program Files (x86)\\Python38-32\\python.exe";
	private static ExecutorService executor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		executor = Executors.newCachedThreadPool();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		executor.shutdownNow();
	}

	@Test
	void testPython() throws IOException, InterruptedException, ExecutionException {
		// Check valid compiler location
		ProcessOutput po = TestsUtil.run(executor, "\"%s\" --help", COMPILER);
		assertTrue(po.getOutput().toLowerCase().startsWith("usage:"));
		
		// Create polymorphic engine and python target generator
		PolymorphicEngine engine = new PolymorphicEngine(5, 10, 16);
		PythonVisitor visitor = new PythonVisitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically compile and run python code to test
			String filename = "main.py";
			Path path = Paths.get(filename);
			try {
				Files.writeString(path, sb.toString());
				po = TestsUtil.run(executor, "\"%s\" %s", COMPILER, filename);
				assertEquals(message, po.getOutput(), sb.toString());
			} finally {
				Files.deleteIfExists(path);
			}
		}
	}
}