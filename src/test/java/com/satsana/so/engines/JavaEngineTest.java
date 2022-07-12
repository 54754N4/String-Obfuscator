package com.satsana.so.engines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import com.satsana.so.engine.visitors.JavaVisitor;
import com.satsana.so.engines.TestsUtil.ProcessOutput;

/* Requires Java to be installed and in PATH */
@SpringBootTest
class JavaEngineTest {
	private static ExecutorService executor;
	
	@BeforeAll
	public static void setUpBeforeClass() {
		executor = Executors.newCachedThreadPool();
	}
	
	@AfterAll
	public static void tearDownAfterClass() {
		executor.shutdownNow();
	}
	
	@Test
	void testJava() throws IOException, InterruptedException, ExecutionException {
		// Check java in env path
		ProcessOutput po = TestsUtil.run(executor, "java -version");
		assertTrue(po.getError().toLowerCase().startsWith("java"));	// somehow java returns the version in stderr lol
		
		// Create polymorphic engine and java target generator
		PolymorphicEngine engine = new PolymorphicEngine(5, 10, 16);
		JavaVisitor visitor = new JavaVisitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically compile and run java code to test
			String filename = "JavaTest";
			String generated = createTestClass(filename, sb.toString());
			Path path = Paths.get(filename+".java");
			try {
				Files.writeString(path, generated, StandardCharsets.UTF_8);
				TestsUtil.run(executor, "javac %s.java", filename);
				po = TestsUtil.run(executor, "java %s", filename);
				assertEquals(message, po.getOutput(), sb.toString());
			} finally {
				Files.deleteIfExists(path);
				Files.deleteIfExists(Paths.get(filename+".class"));
			}
		}
	}

	/* Java compilation helper methods */
	
	public static String createTestClass(String classname, String body) {
		return new StringBuilder()
				.append("class "+classname+" {\n")
				.append("\tpublic static void main(String[] args) {\n")
				.append(body.replaceAll("\n", "\n\t\t"))
				.append("\n\t}\n")
				.append("}\n")
				.toString();
	}
}
