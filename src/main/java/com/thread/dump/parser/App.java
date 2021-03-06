package com.thread.dump.parser;

import java.io.IOException;

import com.thread.dump.parser.bean.ThreadInfo;

/**
 * @author Leo Gutiérrez <leogutierrezramirez@gmail.com>
 */
public class App {
	public static void main(final String[] args) throws IOException {
		
		final ThreadDumpReader threadsReader = new ThreadDumpReader();
		
		threadsReader.fromFile("tdump.sample").
			stream().
			filter(thread -> thread.getStackTrace()
					.isPresent()).forEach(thread -> {
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
