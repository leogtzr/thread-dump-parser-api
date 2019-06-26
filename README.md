# thread-dump-parser-api

This is a small Java library to parse some information from a Java Thread Dump.

## To simply parse a thread dump file:

```java
ThreadDumpReader threadsReader = new ThreadDumpReader();

threadsReader.fromFile("threaddump.txt").
	stream().
	filter(thread -> thread.getStackTrace().isPresent()).forEach(thread -> {
		System.out.println(thread.getName());
		System.out.println(thread.getId());
		System.out.println(thread.getNativeId());
		System.out.println(thread.getStackTrace().get());
		System.out.println(thread.isDaemon());
		if (thread.getStackTrace().isPresent()) {
			// ... 	
		}
	});
```

Or you could parse the thread information from a String:

```java
ThreadDumpReader threadsReader = new ThreadDumpReader();
List<ThreadInfo> threads = threadsReader.fromString(THREAD_DUMP_STRING);

```

