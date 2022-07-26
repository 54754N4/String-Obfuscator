package com.satsana.so.engines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.satsana.so.engine.model.Context;
import com.satsana.so.engine.model.PolymorphicEngine;
import com.satsana.so.engine.visitors.CSharpVisitor;
import com.satsana.so.engines.TestsUtil.ProcessOutput;

/* Requires .NET and Visual Studio installed (code tested on .NET v6.0) */
@SpringBootTest
class CSharpEngineTest {
	private static ExecutorService executor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		executor = Executors.newCachedThreadPool();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		executor.shutdownNow();
	}

	/**
	 * Add .\build folder as exception in Antivirus to avoid it delaying the test
	 */
	@Test
	void testCSharp() throws IOException, InterruptedException, ExecutionException {
		// Check C# in env path
		ProcessOutput po = TestsUtil.run(executor, "dotnet --info");
		assertTrue(po.getOutput().toLowerCase().startsWith(".net"));
		
		// Create polymorphic engine and C# target generator
		PolymorphicEngine engine = new PolymorphicEngine(5, 10, 16);
		CSharpVisitor visitor = new CSharpVisitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically compile and run C# code to test
			String filename = "build/CSharpTest";
			Path build = Paths.get("build");
			Path csproj = Paths.get(filename+".csproj");
			Path cs = Paths.get(filename+".cs");
			try {
				if (!Files.exists(build))
					Files.createDirectory(build);
				Files.writeString(csproj, CSPROJ);
				Files.writeString(cs, sb.toString());
				po = TestsUtil.run(executor, "dotnet run --project %s", csproj);
				assertEquals(message, po.getOutput(), sb.toString());
			} finally {
				Files.deleteIfExists(csproj);
				Files.deleteIfExists(cs);
				if (Files.exists(Paths.get("build/bin"))) {
					deleteDir(Paths.get("build/bin"));
					deleteDir(Paths.get("build/obj"));
				}
			}
		}
	}

	
	/* C# compilation helper methods */
	
	public static void deleteDir(Path directory) throws IOException {
		Files.walk(directory)
	      .sorted(Comparator.reverseOrder())
	      .map(Path::toFile)
	      .forEach(File::delete);
	}
	
	private static final String CSPROJ = "<Project Sdk=\"Microsoft.NET.Sdk\">\r\n"
			+ "\r\n"
			+ "  <PropertyGroup>\r\n"
			+ "    <OutputType>Exe</OutputType>\r\n"
			+ "    <TargetFramework>net6.0</TargetFramework>\r\n"
			+ "    <ImplicitUsings>enable</ImplicitUsings>\r\n"
			+ "    <Nullable>enable</Nullable>\r\n"
			+ "  </PropertyGroup>\r\n"
			+ "\r\n"
			+ "</Project>\r\n"
			+ "";
}
