package com.satsana.so.engine;

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

import com.satsana.so.model.Context;
import com.satsana.so.model.PolymorphicEngine;
import com.satsana.so.visitors.Masm64Visitor;

/* Requires Visual Studio to be installed with the Windows SDK */
@SpringBootTest
class Masm64EngineTest {
	private static final String ASSEMBLER = "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Tools\\MSVC\\14.32.31326\\bin\\Hostx64\\x64\\ml64.exe",
		LINKER = "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Tools\\MSVC\\14.32.31326\\bin\\Hostx64\\x64\\link.exe",
		LIB_PATH = "C:\\Program Files (x86)\\Windows Kits\\10\\Lib\\10.0.19041.0\\um\\x64",
		ASSEMBLE = "\""+ASSEMBLER+"\" /c /nologo /Zi /Fo\"build\\main.obj\" /W3 /errorReport:prompt /Ta \"build\\main.asm\"",
		LINK = "\""+LINKER+"\" /SUBSYSTEM:CONSOLE \"build\\main.obj\" /DYNAMICBASE \"kernel32.lib\" \"user32.lib\" \"gdi32.lib\" \"winspool.lib\" \"comdlg32.lib\" \"advapi32.lib\" \"shell32.lib\" \"ole32.lib\" \"oleaut32.lib\" \"uuid.lib\" \"odbc32.lib\" \"odbccp32.lib\" "
			+ "/DEBUG /MACHINE:X64 /ENTRY:\"main\" /MANIFEST /NXCOMPAT /OUT:\"build\\main.exe\" /INCREMENTAL "
			+ "/LIBPATH:\""+LIB_PATH+"\" /MANIFESTUAC:\"level='asInvoker' uiAccess='false'\" /ManifestFile:\"build\\main.exe.intermediate.manifest\" /ERRORREPORT:PROMPT /ILK:\"build\\main.ilk\" /NOLOGO /TLBID:1";
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
	void testMASM64() throws IOException, InterruptedException, ExecutionException {
		// Check if assembler and linker exists
		Path assembler = Paths.get(ASSEMBLER);
		assertTrue(Files.exists(assembler), "masm assembler not found");
		
		// Create polymorphic engine and masm target generator
		PolymorphicEngine engine = new PolymorphicEngine(5, 10, 16);
		Masm64Visitor visitor = new Masm64Visitor();
		String message = "Hello World!";
		
		// Test multiple decryption routines
		int decryptionRoutines = 100;
		for (int i=0; i<decryptionRoutines; i++) {
			// Generate new polymorphic code
			Context ctx = engine.transform(message);
			StringBuilder sb = visitor.visit(ctx);
			
			// Automatically assemble, link and run to test
			try {
				Path asm = Paths.get("build/main.asm");
				Files.writeString(asm, sb.toString());
				TestsUtil.run(executor, "\"%s\"", ASSEMBLE);
				TestsUtil.run(executor, "\"%s\"", LINK);
				TestsUtil.run(executor,  "\"%s\"", ".\\build\\main.exe > output.txt");
				String output = Files.readString(Paths.get("output.txt"), StandardCharsets.UTF_16LE);
				assertEquals(message, output, sb.toString());
			} finally {
				Files.deleteIfExists(Paths.get("build/main.asm"));
				Files.deleteIfExists(Paths.get("build/main.pdb"));
				Files.deleteIfExists(Paths.get("build/main.obj"));
				Files.deleteIfExists(Paths.get("build/main.ilk"));
				Files.deleteIfExists(Paths.get("build/main.exe.intermediate.manifest"));
				Files.deleteIfExists(Paths.get("build/main.exe"));
				Files.deleteIfExists(Paths.get("output.txt"));
			}
		}		
	}
}
