package com.thread.dump.parser.util;

import java.util.Optional;
import java.util.regex.Matcher;

import com.thread.dump.parser.bean.ThreadInfo;

public class ThreadParsing {
	
	private ThreadParsing() {}
	
	public static Optional<ThreadInfo> extractThreadInfoFromLine(final String threadHeaderLine) {
		
		final Matcher matcher = PatternConstants.THREAD_NAME.matcher(threadHeaderLine);
		
		if (matcher.find() && matcher.groupCount() == ParsingConstants.THREAD_NAME_FIELD_COUNT) {
			
			final ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName(matcher.group(1));
			threadInfo.setId(matcher.group(2));
			threadInfo.setNativeId(matcher.group(3));
			
			return Optional.<ThreadInfo>of(threadInfo);
		}
		
		return Optional.<ThreadInfo>empty();
		
	}
	
	public static Optional<Thread.State> extractThreadState(final String line) {
		
		final Matcher threadStateMatcher = PatternConstants.STATE.matcher(line);
		
		if (threadStateMatcher.find()) {
			final String[] stateFields = threadStateMatcher.group(1).split(" ");
			return Optional.<Thread.State>of(Thread.State.valueOf(stateFields[0]));
		}
		
		return Optional.<Thread.State>empty();
	}

}
