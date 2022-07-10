package com.satsana.so.engine;

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

import com.satsana.so.engine.TestsUtil.ProcessOutput;
import com.satsana.so.model.Context;
import com.satsana.so.model.PolymorphicEngine;
import com.satsana.so.visitors.PowerShellVisitor;

@SpringBootTest
class PowerShellEngineTest {
	private static String CMD_FORMAT = "powershell -Command \"& '%s'\"";
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
		// Check valid compiler
		assertTrue(TestsUtil.isWindows, "This test case cannot run on linux");
		
		// Create polymorphic engine and PowerShell target generator
		PolymorphicEngine engine = new PolymorphicEngine(5, 10, 16);
		PowerShellVisitor visitor = new PowerShellVisitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically compile and run PowerShell code to test
			String filename = "main.ps1";
			Path path = Paths.get(filename).toAbsolutePath();
			try {
				Files.writeString(path, sb.toString());
				ProcessOutput po = TestsUtil.run(executor, CMD_FORMAT, path);
				assertEquals(message, po.getOutput(), sb.toString());
			} finally {
				Files.deleteIfExists(path);
			}
		}
	}
}
