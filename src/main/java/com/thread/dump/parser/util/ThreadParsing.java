package com.thread.dump.parser.util;

import static com.thread.dump.parser.util.PatternConstants.LOCKED_RGX;
import static com.thread.dump.parser.util.PatternConstants.ThreadNameFieldsIndex.ID;
import static com.thread.dump.parser.util.PatternConstants.ThreadNameFieldsIndex.NAME;
import static com.thread.dump.parser.util.PatternConstants.ThreadNameFieldsIndex.NATIVE_ID;

import static com.thread.dump.parser.util.PatternConstants.StateFieldsIndex.STATE;
import static com.thread.dump.parser.util.PatternConstants.ThreadLockedFieldsIndex.LOCKED_ID;
import static com.thread.dump.parser.util.PatternConstants.ThreadWaitingToLockFieldsIndex.WAITING_TO_LOCK;
import static com.thread.dump.parser.util.PatternConstants.ThreadParkingToWaitFor.WAITING_FOR_ID;
import static com.thread.dump.parser.util.PatternConstants.ThreadWaitingOn.WAITING_ON_ID;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;

import com.thread.dump.parser.bean.Locked;
import org.apache.commons.lang3.StringUtils;

import com.thread.dump.parser.bean.StackTraceLock;
import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez
 */
public class ThreadParsing {
	
	public static Optional<ThreadInfo> extractThreadInfoFromLine(final String threadHeaderLine) {
		
		final Matcher matcher = PatternConstants.THREAD_NAME.matcher(threadHeaderLine);
		
		if (matcher.find() && matcher.groupCount() == ParsingConstants.THREAD_NAME_FIELD_COUNT) {
			
			final ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName(matcher.group(NAME.get()));
			threadInfo.setId(matcher.group(ID.get()));
			threadInfo.setNativeId(matcher.group(NATIVE_ID.get()));

			if (threadHeaderLine.contains(" daemon ")) {
				threadInfo.setDaemon(true);
			}
			
			return Optional.<ThreadInfo>of(threadInfo);
		}
		
		return Optional.<ThreadInfo>empty();
		
	}
	
	public static Optional<Thread.State> extractThreadState(final String line) {
		
		final Matcher threadStateMatcher = PatternConstants.STATE.matcher(line);
		
		if (threadStateMatcher.find()) {
			final String[] stateFields = threadStateMatcher.group(STATE.get()).split(" ");
			return Optional.<Thread.State>of(Thread.State.valueOf(stateFields[0]));
		}
		
		return Optional.<Thread.State>empty();
	}
	
	public static Optional<String> extractThreadStackTrace(final BufferedReader br) throws IOException {
		
		final StringBuilder sb = new StringBuilder();
		for (String line = br.readLine(); line != null && StringUtils.isNotBlank(line); line = br.readLine()) {
			sb.append(line.trim()).append(ParsingConstants.NEW_LINE);
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
		
		threads.stream().
			filter(thread -> thread.getStackTrace().isPresent()).forEach(thread -> {
			for (final String stackTraceLine : thread.getStackTrace().get().split(ParsingConstants.NEW_LINE)) {
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

	public static Map<ThreadInfo, List<Locked>> holds(final List<ThreadInfo> threads){
		final Map<ThreadInfo, List<Locked>> holds = new HashMap<>();

		threads.stream()
				.filter(th -> th.getStackTrace().isPresent())
				.filter(th -> th.getStackTrace().get().contains(" locked "))
				.forEach(th -> {
			final String stackTrace = th.getStackTrace().get();

			Arrays.stream(stackTrace.split("\n")).map(stackLine -> LOCKED_RGX.matcher(stackLine.trim()))
					.filter(Matcher::matches)
					.forEach(match -> {
						if (!holds.containsKey(th)) {
							final List<Locked> locks = new ArrayList<>();
							holds.put(th, locks);
						}
						final List<Locked> locks = holds.get(th);
						final Locked lockToAdd = new Locked(match.group(1), match.group(2));
						locks.add(lockToAdd);
						holds.put(th, locks);
					});
		});

		return holds;
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
			final String lockedId = threadLockedMatcher.group(LOCKED_ID.get());
			lockeds.put(lockedId, threadInfo);
		}
		
	}
	
	private static void extractWaitingToLock(
			final String stackTraceLine,
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher waitingToLockMatcher = PatternConstants.WAITING_TO_LOCK.matcher(stackTraceLine);
		if (waitingToLockMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.WAITING_TO_LOCK);
			final String lockedId = waitingToLockMatcher.group(WAITING_TO_LOCK.get());
			waitingToLock.put(lockedId, threadInfo);
		}
	}
	
	private static void extractParkingToWaitFor(
			final String stackTraceLine,
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher parkingToWaitForMatcher = PatternConstants.PARKING_TO_WAIT_FOR.matcher(stackTraceLine);
		if (parkingToWaitForMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.PARKING_TO_WAITT_FOR);
			final String lockedId = parkingToWaitForMatcher.group(WAITING_FOR_ID.get());
			waitingToLock.put(lockedId, threadInfo);
		}
	}
	
	private static void extractWaitingOn(final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher waitingOnMatcher = PatternConstants.WAITING_ON.matcher(stackTraceLine);
		if (waitingOnMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.WAITING_ON);
			final String lockedId = waitingOnMatcher.group(WAITING_ON_ID.get());
			waitingToLock.put(lockedId, threadInfo);
		}
	}

	private ThreadParsing() {}
	
}
