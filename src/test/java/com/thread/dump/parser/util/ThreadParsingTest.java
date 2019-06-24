package com.thread.dump.parser.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.thread.dump.parser.ThreadDumpReader;
import org.junit.Test;

import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez
 */
public class ThreadParsingTest {
	
	private static final String THREAD_NAME_HEADER = 
		"\"http-nio-8080-exec-1\" #27 daemon prio=5 os_prio=0 tid=0x00007f0b8c00d000 nid=0x1d32 waiting on condition [0x00007f0bc63c3000]";
	
	private static final String THREAD_STATE = " java.lang.Thread.State: WAITING (kahsdasd)";

	private static final String DAEMON_THREAD_INFORMATION = "\"Attach Listener\" #6085 daemon prio=9 os_prio=0 tid=0x00007f90d0106000 nid=0x18a1 runnable [0x0000000000000000]" +
	"java.lang.Thread.State: RUNNABLE" +
"\n" +
"	Locked ownable synchronizers:"+
"	 - None";


	@Test
	public void shouldReturnNonEmptyThreadInfoObject() {
		final Optional<ThreadInfo> threadInfo = ThreadParsing.extractThreadInfoFromLine(THREAD_NAME_HEADER);
		assertTrue(threadInfo.isPresent());
	}
	
	@Test
	public void shouldReturnNonEmptyThreadState() {
		final Optional<Thread.State> threadState = ThreadParsing.extractThreadState(THREAD_STATE);
		assertTrue(threadState.isPresent());
	}

	@Test
	public void shouldReturnThreadState() {
		final Thread.State expectedState = Thread.State.WAITING;
		final Optional<Thread.State> state = ThreadParsing.extractThreadState(THREAD_STATE);
		assertTrue(state.isPresent());
		final Thread.State threadState = state.get();
		assertEquals(expectedState, threadState);
	}

	@Test
	public void testShouldIdentifyDaemonThread() throws IOException {
		final int expectedNumberOfThreads = 1;
		final List<ThreadInfo> threads = new ThreadDumpReader().fromString(DAEMON_THREAD_INFORMATION);
		assertEquals(threads.size(), expectedNumberOfThreads);
		assertTrue("Thread should be daemon", threads.get(0).isDaemon());
	}
	
}
