package com.thread.dump.parser;

import java.io.IOException;

import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez
 */
public class App {
	public static void main(final String[] args) throws IOException {
		
		final ThreadDumpReader threadsReader = new ThreadDumpReader(args[0]);
		
		threadsReader.read().
			stream().
			filter(thread -> thread.getStackTrace().isPresent()).forEach(thread -> {
				System.out.println(thread.getName());
				System.out.println(thread.getId());
				System.out.println(thread.getNativeId());
				System.out.println(thread.getStackTrace().get());
				if (thread.getStackTrace().isPresent()) {
				    // ...
                }

			});
		
	}
}
