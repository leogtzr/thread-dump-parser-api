package com.thread.dump.parser.util;

import static com.thread.dump.parser.util.ParsingConstants.NEW_LINE;
import static com.thread.dump.parser.util.PatternConstants.*;
import static com.thread.dump.parser.util.PatternConstants.STATE;
import static com.thread.dump.parser.util.PatternConstants.ThreadNameFieldsIndex.ID;
import static com.thread.dump.parser.util.PatternConstants.ThreadNameFieldsIndex.NAME;
import static com.thread.dump.parser.util.PatternConstants.ThreadNameFieldsIndex.NATIVE_ID;

import static com.thread.dump.parser.util.PatternConstants.ThreadLockedFieldsIndex.LOCKED_ID;
import static com.thread.dump.parser.util.PatternConstants.ThreadWaitingToLockFieldsIndex.WAITING_TO_LOCK;
import static com.thread.dump.parser.util.PatternConstants.ThreadParkingToWaitFor.WAITING_FOR_ID;
import static com.thread.dump.parser.util.PatternConstants.ThreadWaitingOn.WAITING_ON_ID;

import java.io.BufferedReader;
import java.io.IOException;
// import java.util.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.thread.dump.parser.bean.Locked;
import org.apache.commons.lang3.StringUtils;

import com.thread.dump.parser.bean.StackTraceLock;
import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez
 */
public class ThreadParsing {
	
	public static Optional<ThreadInfo> extractThreadInfoFromLine(final String threadHeaderLine) {
		
		final Matcher matcher = THREAD_NAME.matcher(threadHeaderLine);
		
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
		
		final Matcher threadStateMatcher = STATE.matcher(line);
		
		if (threadStateMatcher.find()) {
			final String[] stateFields = threadStateMatcher.group(StateFieldsIndex.STATE.get()).split(" ");
			return Optional.of(Thread.State.valueOf(stateFields[0]));
		}
		
		return Optional.empty();
	}
	
	public static Optional<String> extractThreadStackTrace(final BufferedReader br) throws IOException {
		
		final StringBuilder sb = new StringBuilder();
		for (String line = br.readLine(); line != null && StringUtils.isNotBlank(line); line = br.readLine()) {
			sb.append(line.trim()).append(NEW_LINE);
		}
		
		if (StringUtils.isNotEmpty(sb.toString())) {
			return Optional.of(sb.toString());
		} else {
			return Optional.empty();
		}
	}
	
	public static Map<StackTraceLock, Map<String, ThreadInfo>> lockingInfo(final List<ThreadInfo> threads) {
		
		final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace = new HashMap<>();
		initializeStackTrace(stackTrace);
		
		threads.stream().
			filter(thread -> thread.getStackTrace().isPresent()).forEach(thread -> {
			for (final String stackTraceLine : thread.getStackTrace().get().split(NEW_LINE)) {
				if (stackTraceLine.contains(LOCKED_TEXT)) {
					extractLocked(stackTraceLine, thread, stackTrace);
				} else if (stackTraceLine.contains(PARKING_TO_WAIT_FOR_TEXT)) {
					extractParkingToWaitFor(stackTraceLine, thread, stackTrace);
				} else if (stackTraceLine.contains(WAITING_ON_TEXT)) {
					extractWaitingOn(stackTraceLine, thread, stackTrace);
				} else if (stackTraceLine.contains(WAITING_TO_LOCK_TEXT)) {
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
						final Locked lockToAdd = new Locked(match.group(LockedIndex.ID.get()), match.group(LockedIndex.CLASS.get()));
						locks.add(lockToAdd);
						holds.put(th, locks);
					});
		});
		return holds;
	}

	public static List<Locked> holdsForThread(final ThreadInfo thread) {
		if (!thread.getStackTrace().isPresent() || !thread.getStackTrace().get().contains(" locked ")) {
			return new ArrayList<>();
		}

		final String stackTrace = thread.getStackTrace().get();
		final List<Locked> holds = Arrays.stream(stackTrace.split("\n"))
				.map(String::trim)
				.map(stackLine -> LOCKED_RGX.matcher(stackLine.trim()))
				.filter(Matcher::matches)
				.map(match -> new Locked(match.group(LockedIndex.ID.get()), match.group(LockedIndex.CLASS.get())))
				.collect(Collectors.toList());
		return holds;
	}
	
	private static void initializeStackTrace(final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		Arrays.stream(StackTraceLock.values())
				.filter(stackTraceLock -> stackTrace.get(stackTraceLock) == null)
				.forEach(stackTraceLock -> {
					final Map<String, ThreadInfo> threadLockInformation = new HashMap<>();
					stackTrace.put(stackTraceLock, threadLockInformation);
				});
	}
	
	private static void extractLocked(
			final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher threadLockedMatcher = THREAD_LOCKED.matcher(stackTraceLine);
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
		
		final Matcher parkingToWaitForMatcher = PARKING_TO_WAIT_FOR.matcher(stackTraceLine);
		if (parkingToWaitForMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.PARKING_TO_WAITT_FOR);
			final String lockedId = parkingToWaitForMatcher.group(WAITING_FOR_ID.get());
			waitingToLock.put(lockedId, threadInfo);
		}
	}
	
	private static void extractWaitingOn(final String stackTraceLine, 
			final ThreadInfo threadInfo,
			final Map<StackTraceLock, Map<String, ThreadInfo>> stackTrace) {
		
		final Matcher waitingOnMatcher = WAITING_ON.matcher(stackTraceLine);
		if (waitingOnMatcher.find()) {
			final Map<String, ThreadInfo> waitingToLock = stackTrace.get(StackTraceLock.WAITING_ON);
			final String lockedId = waitingOnMatcher.group(WAITING_ON_ID.get());
			waitingToLock.put(lockedId, threadInfo);
		}
	}

	private static List<String> uniqueStackTrace(final List<String> threadStackTrace) {
		final List<String> u = new ArrayList<>(threadStackTrace.size());
		final Map<String, Boolean> m = new HashMap<>();

		for (final String val : threadStackTrace) {
			if (!m.containsKey(val.trim())) {
				m.put(val, true);
				u.add(val);
			}
		}

		return u;
	}

	private static String joinFieldsFromStackTraceMethod(final String[] fields) {
		if (fields.length == 2) {
			return fields[1];
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 1; i < fields.length; i++) {
			sb.append(fields[i]);
			sb.append(" ");
		}
		return sb.toString();
	}

	private static String extractJavaMethodNameFromStackTraceLine(final String stacktraceLine) {
		final Matcher matcher = STACKTRACE_RGX_METHOD_NAME.matcher(stacktraceLine);
		if (matcher.matches()) {
			final String[] fields = stacktraceLine.split("\\s+");
			String s = joinFieldsFromStackTraceMethod(fields);
			return s.trim();
		}
		return "";
	}

	public static Map<String, Integer> mostUsedMethods(final List<ThreadInfo> threads) {
		final Map<String, Integer> mostUsedMethodsGeneral = new HashMap<>();

		for (final ThreadInfo th : threads) {
			if (!th.getStackTrace().isPresent()) {
				continue;
			}
			final List<String> stackTraceLines = uniqueStackTrace(Arrays.asList(th.getStackTrace().get().split("\n")));
			for (String stackTraceLine : stackTraceLines) {
				stackTraceLine = extractJavaMethodNameFromStackTraceLine(stackTraceLine.trim());
				if (stackTraceLine.isEmpty()) {
					continue;
				}
				if (mostUsedMethodsGeneral.containsKey(stackTraceLine)) {
					final int count = mostUsedMethodsGeneral.get(stackTraceLine);
					mostUsedMethodsGeneral.put(stackTraceLine, count + 1);
				} else {
					mostUsedMethodsGeneral.put(stackTraceLine, 1);
				}
			}
		}
		return mostUsedMethodsGeneral;
	}

	private ThreadParsing() {}
	
}
