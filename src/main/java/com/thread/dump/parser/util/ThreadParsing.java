package com.thread.dump.parser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

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
	
	public static Optional<String> extractThreadStackTrace(final BufferedReader br) throws IOException {
		
		final StringBuilder sb = new StringBuilder();
		for (String line = br.readLine(); line != null && StringUtils.isNotBlank(line); line = br.readLine()) {
			sb.append(line).append(ParsingConstants.NEW_LINE);
		}
		
		if (StringUtils.isNotEmpty(sb.toString())) {
			return Optional.<String>of(sb.toString());
		} else {
			return Optional.<String>empty();
		}
	}
	
	public static boolean isThreadWaitingToAcquireLock(final String stackTrace) {
		return PatternConstants.WAITING_TO_LOCK.matcher(stackTrace).find();
	}
	
	public static boolean isThreadHoldingLock(final String stackTrace) {
		return PatternConstants.THREAD_LOCKED.matcher(stackTrace).find();
	}
	
	public static void retrieveLockingThreads(final String stackTrace) {
		final Matcher threadHoldingMatcher = PatternConstants.THREAD_LOCKED.matcher(stackTrace);
		
		while (threadHoldingMatcher.find()) {
			System.out.println(threadHoldingMatcher.group(1));
			
		}
		
	}

}
