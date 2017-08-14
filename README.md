# thread-dump-parser-api

```java
final ThreadDumpReader threadsReader = new ThreadDumpReader(threadDumpFilePath);

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
```
