package com.thread.dump.parser.util;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez
 */
public class ThreadParsingTest {
	
	private static final String THREAD_NAME_HEADER = 
		"\"http-nio-8080-exec-1\" #27 daemon prio=5 os_prio=0 tid=0x00007f0b8c00d000 nid=0x1d32 waiting on condition [0x00007f0bc63c3000]";
	
	private static final String THREAD_STATE = " java.lang.Thread.State: WAITING (kahsdasd)";
	
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
	
}
