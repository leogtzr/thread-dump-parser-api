package com.thread.dump.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;

import com.thread.dump.parser.util.ParsingConstants;
import com.thread.dump.parser.util.PatternConstants;

/**
 * @author Leo Gutiérrez
 */
public class ThreadDumpReader {
	
	private final String threadDumpFilePath;
	
	public ThreadDumpReader(final String threadDumpFilePath) {
		this.threadDumpFilePath = threadDumpFilePath;
	}
	
	public void parse() throws IOException {
		try {
			try (final BufferedReader br = new BufferedReader(new FileReader(threadDumpFilePath))) {
				// Read thread timestamp ...
				final Date threadDumpTimesTamp = PatternConstants.THREAD_DUMP_TIMESTAMP_FORMAT.parse(br.readLine());
				System.out.println(threadDumpTimesTamp);
				
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					// System.out.println(line);
					if (line.startsWith(ParsingConstants.THREAD_INFORMATION_BEGIN)) {
						System.out.println("Threa beginning: " + line);
					}
				}
				
			}
		} catch (IOException | ParseException ex) {
			throw new IOException("Unable to generate thread dump information.", ex);
		}
	}
	
	private void parseThreadNameInfo(final String threadNameLine) {
		final Matcher matcher = PatternConstants.THREAD_NAME.matcher(threadNameLine);
		if (matcher.find()) {
			if (matcher.groupCount() !=  3) {
				// ... 
			}
		}
	}
	
}
