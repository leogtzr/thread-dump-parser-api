package com.thread.dump.parser.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * @author Leo Gutiérrez
 */
public class PatternConstants {
	
	public static final Pattern THREAD_NAME = Pattern.compile("^\"(.*)\".*prio=[0-9]+ tid=(\\w*) nid=(\\w*)\\s\\w*");		    
	public static final Pattern STATE = Pattern.compile("\\s+java.lang.Thread.State: (.*)");
	public static final Pattern LOCK_WAIT = Pattern.compile("\\s+- parking to wait for\\s+<(.*)>\\s+\\(.*\\)");
	public static final Pattern THREAD_LOCKED = Pattern.compile("\\s+- locked\\s+<(.*)>\\s+\\(.*\\)");
	
	public static final DateFormat THREAD_DUMP_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
	
	private PatternConstants() {}
	
}
