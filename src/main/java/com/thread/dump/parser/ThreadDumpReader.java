package com.thread.dump.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.thread.dump.parser.bean.StackTraceLock;
import com.thread.dump.parser.bean.ThreadInfo;
import com.thread.dump.parser.util.ParsingConstants;
import com.thread.dump.parser.util.ThreadParsing;

/**
 * @author Leo Gutiérrez
 */
public class ThreadDumpReader {

	private List<ThreadInfo> read(final Reader reader) throws IOException {
		final List<ThreadInfo> threads = new ArrayList<>();

		try (final BufferedReader br = new BufferedReader(reader)) {

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
							thread.setStackTrace(stacktrace.get());
						}

						threads.add(thread);
					}
				}
			}

		} catch (final IOException ex) {
			throw new IOException("Unable to generate thread dump information.", ex);
		}


		return threads;
	}


	
	public  List<ThreadInfo> fromFile(final String threadDumpFilePath) throws IOException {
		final List<ThreadInfo> threads = read(new FileReader(threadDumpFilePath));
		return threads;
	}

	public  List<ThreadInfo> fromString(final String content) throws IOException {
		final List<ThreadInfo> threads = read(new StringReader(content));
		return threads;
	}
	
	private static void printLockingThreadInformation(
			final Map<StackTraceLock, Map<String, ThreadInfo>> lockingInfo, final StackTraceLock stackTraceLock) {
		
		lockingInfo.get(stackTraceLock).forEach((k, v) -> {
			System.out.println(String.format("id: %s, thread: '%s'", k, v));
			if (lockingInfo.get(StackTraceLock.LOCKED).containsKey(k)) {
				// @PENDING
			}
		});
		
	}
	
}
