package com.thread.dump.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.thread.dump.parser.bean.ThreadInfo;
import com.thread.dump.parser.util.ParsingConstants;
import com.thread.dump.parser.util.PatternConstants;
import com.thread.dump.parser.util.ThreadParsing;

/**
 * @author Leo Gutiérrez
 */
public class ThreadDumpReader {
	
	private final String threadDumpFilePath;
	
	public ThreadDumpReader(final String threadDumpFilePath) {
		this.threadDumpFilePath = threadDumpFilePath;
	}
	
	public void parse() throws IOException {
		
		final List<ThreadInfo> threads = new ArrayList<>();
		
		try {
			try (final BufferedReader br = new BufferedReader(new FileReader(threadDumpFilePath))) {
				
				// Read thread's timestamp ...
				final Date threadDumpTimesTamp = PatternConstants.THREAD_DUMP_TIMESTAMP_FORMAT.parse(br.readLine());

				for (String line = br.readLine(); line != null; line = br.readLine()) {
					if (line.startsWith(ParsingConstants.THREAD_INFORMATION_BEGIN)) {
						
						final Optional<ThreadInfo> threadInfo = ThreadParsing.extractThreadInfoFromLine(line);
						if (threadInfo.isPresent()) {
							final ThreadInfo thread = threadInfo.get();
							
							final Optional<Thread.State> state = ThreadParsing.extractThreadState(br.readLine());
							if (state.isPresent()) {
								thread.setState(state.get().toString());
							}
							
							final Optional<String> stacktrace = ThreadParsing.extractThreadStackTrace(br);
							if (stacktrace.isPresent()) {
								thread.setRawData(stacktrace.get());
							}
							
							threads.add(thread);
						}
					}
				}
				
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				
				threads.stream().filter(thread -> ThreadParsing.isThreadHoldingLock(thread.getRawData())).
					forEach(thread -> {
						System.out.println("Thread: " + thread.getName());
					});
				
				System.out.println("Analyzing locking ... ");
				threads.stream().forEach(thread -> ThreadParsing.retrieveLockingThreads(thread.getRawData()));
				
			}
			
		} catch (IOException | ParseException ex) {
			throw new IOException("Unable to generate thread dump information.", ex);
		}
		
		// printThreadsInformation(threads);
		
	}
	
	private void printThreadsInformation(final List<ThreadInfo> threads) {
		threads.stream().forEach(System.out::println);
	}
	
}
