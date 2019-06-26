# thread-dump-parser-api

This is a small Java library to parse some information from a Java Thread Dump.

## Parse a thread dump file:

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

## Holds/Locks

Having this thread:

```
"Thread-36" daemon prio=10 tid=0x00002aaac0c80800 nid=0x4da7 runnable [0x0000000050c81000]
   java.lang.Thread.State: RUNNABLE
        at sun.nio.ch.EPollArrayWrapper.epollWait(Native Method)
        at sun.nio.ch.EPollArrayWrapper.poll(EPollArrayWrapper.java:210)
        at sun.nio.ch.EPollSelectorImpl.doSelect(EPollSelectorImpl.java:65)
        at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:69)
        - locked <0x000000076cac33d8> (a sun.nio.ch.Util$2)
        - locked <0x000000076cac33f0> (a java.util.Collections$UnmodifiableSet)
        - locked <0x000000076cce4250> (a sun.nio.ch.EPollSelectorImpl)
        at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:80)
        at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:84)
        at com.endeca.infront.publish.AbstractNioTcpClient.run(AbstractNioTcpClient.java:234)
        at com.endeca.infront.publish.WorkbenchContentClient.run(WorkbenchContentClient.java:28)
        at java.lang.Thread.run(Thread.java:682)

   Locked ownable synchronizers:
        - None
```
We can get the locking information for this thread (_holds_), with the following code:

```java
List<ThreadInfo> threads = new ThreadDumpReader().fromString(THREAD_INFO_WITH_LOCKS_STRING);
Map<ThreadInfo, List<Locked>> holds = ThreadParsing.holds(threads);
```
It will get you a map with the Thread as the key and list of locks as the value, in this especific case:
```
[Thread Id: '0x00002aaac2330800' (daemon), Name: 'Thread-35', State: 'RUNNABLE']
[
 [Locked{lockID='0x000000076ce9f878', lockedObjectName='sun.nio.ch.Util$2'}
 , Locked{lockID='0x000000076ce9f890', lockedObjectName='java.util.Collections$UnmodifiableSet'}
 , Locked{lockID='0x000000076ce5dc68', lockedObjectName='sun.nio.ch.EPollSelectorImpl'}]
]
```
