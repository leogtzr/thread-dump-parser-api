package com.thread.dump.parser.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * @author Leo Gutiérrez
 */
public class PatternConstants {
	
	public static final Pattern THREAD_NAME = Pattern.compile("^\"(.*)\".*prio=[0-9]+ tid=(\\w*) nid=(\\w*)\\s\\w*");
	
	public static enum ThreadNameFieldsIndex {
		NAME(1),
		ID(2),
		NATIVE_ID(3)
		;
		
		private final int index;
		ThreadNameFieldsIndex(final int index) {
			this.index = index;
		}
		
		public int get() {
			return index;
		}
	}
	
	public static final Pattern STATE = Pattern.compile("\\s+java.lang.Thread.State: (.*)");
	
	public static enum StateFieldsIndex {
		STATE(1)
		;
		
		private final int index;
		StateFieldsIndex(final int index) {
			this.index = index;
		}
		
		public int get() {
			return index;
		}
	}
	
	public static final Pattern PARKING_TO_WAIT_FOR = Pattern.compile("\\s+- parking to wait for\\s+<(.*)>\\s+\\(.*\\)");
	
	public static enum ThreadParkingToWaitFor {
		WAITING_FOR_ID(1)
		;
		private final int index;
		ThreadParkingToWaitFor(final int index) {
			this.index = index;
		}
		
		public int get() {
			return index;
		}
	}
	
	public static final Pattern THREAD_LOCKED = Pattern.compile("\\s+- locked\\s+<(.*)>\\s+\\(.*\\)");
	
	public static enum ThreadLockedFieldsIndex {
		LOCKED_ID(1)
		;
		private final int index;
		ThreadLockedFieldsIndex(final int index) {
			this.index = index;
		}
		public int get() {
			return index;
		}
	}
	
	public static final Pattern WAITING_TO_LOCK = Pattern.compile("- waiting to lock\\s+<(.*)>");
	
	public static enum ThreadWaitingToLockFieldsIndex {
		WAITING_TO_LOCK(1)
		;
		private final int index;
		ThreadWaitingToLockFieldsIndex(final int index) {
			this.index = index;
		}
		public int get() {
			return index;
		}
	}
	
	public static final Pattern WAITING_ON = Pattern.compile("- waiting on\\s+<(.*)>");
	
	public static enum ThreadWaitingOn {
		WAITING_ON_ID(1)
		;
		private final int index;
		ThreadWaitingOn(final int index) {
			this.index = index;
		}
		public int get() {
			return index;
		}
	}
	
	public static final DateFormat THREAD_DUMP_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
	
	public static final String LOCKED_TEXT = "- locked <";
	public static final String PARKING_TO_WAIT_FOR_TEXT = "- parking to wait for";
	public static final String WAITING_ON_TEXT = "- waiting on <";
	public static final String WAITING_TO_LOCK_TEXT = "- waiting to lock <";

	public static final Pattern LOCKED_RGX = Pattern.compile("\\s*\\- locked\\s*<(.*)>\\s*\\(a\\s(.*)\\)");

	public static enum LockedIndex {
		ID(1),
		CLASS(2)
		;
		private final int index;
		LockedIndex(final int index) {
			this.index = index;
		}
		public int get() {
			return index;
		}
	}

	public static final Pattern STACKTRACE_RGX_METHOD_NAME = Pattern.compile("at\\s+(.*)$");
	
	private PatternConstants() {}
	
}
