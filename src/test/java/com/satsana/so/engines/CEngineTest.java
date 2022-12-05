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
import com.satsana.so.engine.visitors.CVisitor;
import com.satsana.so.engines.TestsUtil.ProcessOutput;

/* Requires Visual Studio to be installed */
@SpringBootTest
class CEngineTest {
	private static String VS_VERSION = "14.34.31933",
			ENV_BUILD_VARS = "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Auxiliary\\Build\\vcvars64.bat",
			COMPILER_DIR = "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Tools\\MSVC\\" + VS_VERSION + "\\bin\\Hostx64\\x64";
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
	void testC() throws IOException, InterruptedException, ExecutionException {
		// Check valid compiler location
		ProcessOutput po = TestsUtil.run(executor, "\"%s\\cl\"", COMPILER_DIR);
		assertTrue(po.getError().toLowerCase().startsWith("microsoft"));
		
		// Create polymorphic engine and C/C++ target generator
		PolymorphicEngine engine = new PolymorphicEngine(10, 10, 16);
		CVisitor visitor = new CVisitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically compile and run C/C++ code to test
			String filename = "main";
			String generated = createMainFile("#include <stdio.h>;", sb.toString());
			Path path = Paths.get(filename+".cpp");
			try {
				Files.writeString(path, generated);
				TestsUtil.run(executor, "call \"%s\" && \"%s\\cl\" \"%s.cpp\"", ENV_BUILD_VARS, COMPILER_DIR, filename);
				po = TestsUtil.run(executor, ".\\%s.exe", filename);
				assertEquals(message, po.getOutput(), sb.toString());
			} finally {
				Files.deleteIfExists(Paths.get(filename+".cpp"));
				Files.deleteIfExists(Paths.get(filename+".exe"));
				Files.deleteIfExists(Paths.get(filename+".obj"));
			}
		}
	}

	/* C & C++ compilation helper methods */
	
	public static String createMainFile(String imports, String body) {
		return new StringBuilder()
				.append(imports).append("\n\n")
				.append("int main(int argc, char**argv) {\n\t")
				.append(body.replaceAll("\n", "\n\t"))
				.append("\n\treturn 0;\n}\n")
				.toString();
	}
}
