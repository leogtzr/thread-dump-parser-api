package com.thread.dump.parser;

import java.io.IOException;

/**
 * @author Leo Gutiérrez
 */
public class App {
	public static void main(final String[] args) throws IOException {
		final ThreadDumpReader threadDumpReader = new ThreadDumpReader("../td");
		threadDumpReader.parse();
	}
}
