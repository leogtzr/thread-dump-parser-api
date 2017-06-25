package com.thread.dump.parser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.thread.dump.parser.bean.StackTraceLock;
import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez
 */
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
	
	public static Map<StackTraceLock, Map<String, ThreadInfo>> lockingInfo(final List<ThreadInfo> threads) {
		
		final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace = new HashMap<>();
		initializeStackTrace(stackTrace);
		
		threads.stream().filter(thread -> thread.getStackTrace() != null).forEach(thread -> {
			for (final String stackTraceLine : thread.getStackTrace().split(ParsingConstants.NEW_LINE)) {
				if (stackTraceLine.contains(PatternConstants.LOCKED_TEXT)) {
					extractLocked(stackTraceLine, thread, stackTrace);
				} else if (stackTraceLine.contains(PatternConstants.PARKING_TO_WAIT_FOR_TEXT)) {
					extractParkingToWaitFor(stackTraceLine, thread, stackTrace);
				} else if (stackTraceLine.contains(PatternConstants.WAITING_ON_TEXT)) {
					extractWaitingOn(stackTraceLine, thread, stackTrace);
				} else if (stackTraceLine.contains(PatternConstants.WAITING_TO_LOCK_TEXT)) {
					extractWaitingToLock(stackTraceLine, thread, stackTrace);
				}
			}
		});
		
		return stackTrace;
		
	}
	
	private static void initializeStackTrace(final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		for (final StackTraceLock stackTraceLock : StackTraceLock.values()) {
			if (stackTrace.get(stackTraceLock) == null) {
				final Map<String, ThreadInfo> threadLockInformation = new HashMap<>();
				stackTrace.put(stackTraceLock, threadLockInformation);
			}
		}
	}
	
	private static void extractLocked(
			final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher threadLockedMatcher = PatternConstants.THREAD_LOCKED.matcher(stackTraceLine);
		if (threadLockedMatcher.find()) {
			final Map<String, ThreadInfo> lockeds = stackTrace.get(StackTraceLock.LOCKED);
			final String lockedId = threadLockedMatcher.group(1);
			lockeds.put(lockedId, threadInfo);
		}
		
	}
	
	private static void extractWaitingToLock(final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher waitingToLockMatcher = PatternConstants.WAITING_TO_LOCK.matcher(stackTraceLine);
		if (waitingToLockMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.WAITING_TO_LOCK);
			final String lockedId = waitingToLockMatcher.group(1);
			waitingToLock.put(lockedId, threadInfo);
		}
	}
	
	private static void extractParkingToWaitFor(final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher parkingToWaitForMatcher = PatternConstants.LOCK_WAIT.matcher(stackTraceLine);
		if (parkingToWaitForMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.PARKING_TO_WAITT_FOR);
			final String lockedId = parkingToWaitForMatcher.group(1);
			waitingToLock.put(lockedId, threadInfo);
		}
	}
	
	private static void extractWaitingOn(final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher waitingOnMatcher = PatternConstants.WAITING_ON.matcher(stackTraceLine);
		if (waitingOnMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.WAITING_ON);
			final String lockedId = waitingOnMatcher.group(1);
			waitingToLock.put(lockedId, threadInfo);
		}
	}
	
}
