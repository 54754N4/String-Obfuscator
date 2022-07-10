package com.satsana.so.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public interface TestsUtil {
	static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	
	static String addInterpreter(String input) {
		if (isWindows)
			return String.format("cmd.exe /c %s", input);
		return String.format("sh -c %s", input);
	}
	
	/* Runs system commands and captures their stdout/stderr */
	static ProcessOutput run(ExecutorService executor, String format, Object...args) throws IOException, InterruptedException, ExecutionException {
		// No need for process builder for this use case
		Process p = Runtime.getRuntime().exec(addInterpreter(String.format(format, args)));
		return new ProcessOutput(p, executor);
	}
	
	static class ProcessOutput {
		private String out, err;
		
		public ProcessOutput(Process process, ExecutorService executor) throws InterruptedException, ExecutionException {
			out = executor.submit(new StreamConsumer(process.getInputStream())).get();
			err = executor.submit(new StreamConsumer(process.getErrorStream())).get();
		}
		
		public String getOutput() {
			return out;
		}
		
		public String getError() {
			return err;
		}
		
		private static class StreamConsumer implements Callable<String> {
			private InputStream is;
			
			public StreamConsumer(InputStream is) {
				this.is = is;
			}
			
			@Override
			public String call() throws IOException {
				StringBuilder sb = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
					reader.lines().forEachOrdered(line -> sb.append(line).append(System.lineSeparator()));
				}
				return sb.toString().trim();
			}
		}
	}
}
