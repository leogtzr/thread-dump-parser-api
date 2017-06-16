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
				System.out.println(threadDumpTimesTamp);

				for (String line = br.readLine(); line != null; line = br.readLine()) {
					if (line.startsWith(ParsingConstants.THREAD_INFORMATION_BEGIN)) {
						final Optional<ThreadInfo> threadInfo = ThreadParsing.extractThreadInfoFromLine(line);
						if (threadInfo.isPresent()) {
							final ThreadInfo thread = threadInfo.get();
							
							final Optional<Thread.State> state = ThreadParsing.extractThreadState(br.readLine());
							if (state.isPresent()) {
								thread.setState(state.get().toString());
							}
							
							threads.add(thread);
						}
					}
				}
				
			}
		} catch (IOException | ParseException ex) {
			throw new IOException("Unable to generate thread dump information.", ex);
		}
		
		System.out.println(threads);
		
	}
	
}
