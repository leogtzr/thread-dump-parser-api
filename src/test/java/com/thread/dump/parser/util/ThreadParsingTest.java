package com.thread.dump.parser.util;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.thread.dump.parser.bean.ThreadInfo;

public class ThreadParsingTest {
	
	private static final String THREAD_NAME_HEADER = 
		"\"http-nio-8080-exec-1\" #27 daemon prio=5 os_prio=0 tid=0x00007f0b8c00d000 nid=0x1d32 waiting on condition [0x00007f0bc63c3000]";
	
	private static final String THREAD_STATE = " java.lang.Thread.State: WAITING (kahsdasd)";
	
	private static final String STACKTRACE = "java.lang.Thread.State: BLOCKED (on object monitor)" + 
			"at atg.adapter.gsa.GSATransaction.removeItemStateFromTransaction(GSATransaction.java:579)" +
			"- waiting to lock <0x00000007663f3518> (a atg.adapter.gsa.GSATransaction)" + 
			"at atg.adapter.gsa.GSAItem.removeItemTransactionState(GSAItem.java:1162)" +
			" - locked <0x0000000735109af8> (a atg.adapter.gsa.GSAContentItem)" + 
			"at atg.adapter.gsa.ItemTransactionState.commitItemState(ItemTransactionState.java:1033)" + 
			"at atg.adapter.gsa.GSATransaction.afterCompletion(GSATransaction.java:546)" +
			"at com.arjuna.ats.internal.jta.resources.arjunacore.SynchronizationImple.afterCompletion(SynchronizationImple.java:126)" +
			"at com.arjuna.ats.arjuna.coordinator.TwoPhaseCoordinator.afterCompletion(TwoPhaseCoordinator.java:389)" +
			"- locked <0x000000075bcdacf0> (a java.lang.Object)" +
			"at com.arjuna.ats.arjuna.coordinator.TwoPhaseCoordinator.cancel(TwoPhaseCoordinator.java:116)";
	
	@Test
	public void shouldReturnNonEmptyThreadInfoObject() {
		final Optional<ThreadInfo> threadInfo = ThreadParsing.extractThreadInfoFromLine(THREAD_NAME_HEADER);
		System.out.println(threadInfo);
		assertTrue(threadInfo.isPresent());
	}
	
	@Test
	public void shouldReturnNonEmptyThreadState() {
		final Optional<Thread.State> threadState = ThreadParsing.extractThreadState(THREAD_STATE);
		assertTrue(threadState.isPresent());
	}
	
	@Test
	public void shouldIdentifyStackTraceWaitingToAcquireLock() {
		assertTrue(ThreadParsing.isThreadWaitingToAcquireLock(STACKTRACE));
	}
	
	@Test
	public void shouldIdentifyStackTraceHolding() {
		assertTrue(ThreadParsing.isThreadHoldingLock(STACKTRACE));
	}
	
}
