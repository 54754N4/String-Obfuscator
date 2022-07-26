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

import com.satsana.so.engine.model.Context;
import com.satsana.so.engine.model.PolymorphicEngine;
import com.satsana.so.engine.visitors.BashVisitor;
import com.satsana.so.engines.TestsUtil.ProcessOutput;

/* Requires WSL and a linux distro installed to run */
@SpringBootTest
class BashEngineTest {
	private static String SHEBANG = "#!/bin/bash\n\n";
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
	void testBash() throws IOException, InterruptedException, ExecutionException {
		// Check valid compiler location
		ProcessOutput po = TestsUtil.run(executor, "bash --help");
		assertTrue(po.getOutput().toLowerCase().startsWith("gnu bash"));
		
		// Create polymorphic engine and bash target generator
		PolymorphicEngine engine = new PolymorphicEngine(5, 10, 16);
		BashVisitor visitor = new BashVisitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically compile and run bash code to test
			String filename = "main.sh";
			Path windowsPath = Paths.get(filename).toAbsolutePath();
			try {
				Files.writeString(windowsPath, SHEBANG + sb.toString());
				po = TestsUtil.run(executor, "wsl wslpath -a \"%s\"", windowsPath);
				assertTrue(po.getOutput().startsWith("/mnt"));
				String linuxPath = po.getOutput();
				po = TestsUtil.run(executor, "wsl \"%s\"", linuxPath);
				assertEquals(message, po.getOutput(), sb.toString());
			} finally {
				Files.deleteIfExists(windowsPath);
			}
		}
	}
}
