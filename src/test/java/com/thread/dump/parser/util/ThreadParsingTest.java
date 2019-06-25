package com.thread.dump.parser.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.thread.dump.parser.ThreadDumpReader;
import com.thread.dump.parser.bean.Locked;
import org.junit.Test;

import com.thread.dump.parser.bean.ThreadInfo;

import static org.junit.Assert.*;

/**
 * @author Leo Gutiérrez
 */
public class ThreadParsingTest {
	
	private static final String THREAD_NAME_HEADER = 
		"\"http-nio-8080-exec-1\" #27 daemon prio=5 os_prio=0 tid=0x00007f0b8c00d000 nid=0x1d32 waiting on condition [0x00007f0bc63c3000]";
	
	private static final String THREAD_STATE = " java.lang.Thread.State: WAITING (kahsdasd)";

	private static final String DAEMON_THREAD_INFORMATION = "\"Attach Listener\" #6085 daemon prio=9 os_prio=0 tid=0x00007f90d0106000 nid=0x18a1 runnable [0x0000000000000000]" +
	"java.lang.Thread.State: RUNNABLE" +
"\n" +
"	Locked ownable synchronizers:"+
"	 - None";

	private static final String THREAD_INFO_WITH_LOCKS = "\"default task-23\" #349 prio=5 os_prio=0 tid=0x00007f8fe400c800 nid=0x72fa waiting for monitor entry [0x00007f8f7228e000]\n" +
			"\tjava.lang.Thread.State: BLOCKED (on object monitor)\n" +
			"\t at java.security.Provider.getService(Provider.java:1039)\n" +
			"\t - locked <0x0000000682e5f948> (a sun.security.provider.Sun)\n" +
			"\t at sun.security.jca.ProviderList.getService(ProviderList.java:332)\n" +
			"\t at sun.security.jca.GetInstance.getInstance(GetInstance.java:157)\n" +
			"\t at java.security.Security.getImpl(Security.java:695)\n" +
			"\t at java.security.MessageDigest.getInstance(MessageDigest.java:167)\n" +
			"\t at sun.security.rsa.RSASignature.<init>(RSASignature.java:79)\n" +
			"\t at sun.security.rsa.RSASignature$SHA512withRSA.<init>(RSASignature.java:305)\n" +
			"\t at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)\n" +
			"\t at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)\n" +
			"\t at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)\n" +
			"\t at java.lang.reflect.Constructor.newInstance(Constructor.java:423)\n" +
			"\t at java.security.Provider$Service.newInstance(Provider.java:1595)\n" +
			"\t at java.security.Signature$Delegate.newInstance(Signature.java:1020)\n" +
			"\t at java.security.Signature$Delegate.chooseProvider(Signature.java:1114)\n" +
			"\t - locked <0x00000007bc531138> (a java.lang.Object)\n" +
			"\t at java.security.Signature$Delegate.engineInitSign(Signature.java:1188)\n" +
			"\t at java.security.Signature.initSign(Signature.java:553)\n" +
			"\t at sun.security.ssl.HandshakeMessage$ECDH_ServerKeyExchange.<init>(HandshakeMessage.java:1031)\n" +
			"\t at sun.security.ssl.ServerHandshaker.clientHello(ServerHandshaker.java:971)\n" +
			"\t at sun.security.ssl.ServerHandshaker.processMessage(ServerHandshaker.java:228)\n" +
			"\t at sun.security.ssl.Handshaker.processLoop(Handshaker.java:1052)\n" +
			"\t at sun.security.ssl.Handshaker$1.run(Handshaker.java:992)\n" +
			"\t at sun.security.ssl.Handshaker$1.run(Handshaker.java:989)\n" +
			"\t at java.security.AccessController.doPrivileged(Native Method)\n" +
			"\t at sun.security.ssl.Handshaker$DelegatedTask.run(Handshaker.java:1467)\n" +
			"\t - locked <0x00000007bbbac500> (a sun.security.ssl.SSLEngineImpl)\n" +
			"\t at io.undertow.protocols.ssl.SslConduit$5.run(SslConduit.java:1021)\n" +
			"\t at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n" +
			"\t at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
			"\t at java.lang.Thread.run(Thread.java:748)\n" +
			" \n" +
			"\tLocked ownable synchronizers:\n" +
			"\t - <0x00000006a43d5c08> (a java.util.concurrent.ThreadPoolExecutor$Worker)";


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

	@Test
	public void shouldReturnThreadState() {
		final Thread.State expectedState = Thread.State.WAITING;
		final Optional<Thread.State> state = ThreadParsing.extractThreadState(THREAD_STATE);
		assertTrue(state.isPresent());
		final Thread.State threadState = state.get();
		assertEquals(expectedState, threadState);
	}

	@Test
	public void testShouldIdentifyDaemonThread() throws IOException {
		final int expectedNumberOfThreads = 1;
		final List<ThreadInfo> threads = new ThreadDumpReader().fromString(DAEMON_THREAD_INFORMATION);
		assertEquals(threads.size(), expectedNumberOfThreads);
		assertTrue("Thread should be daemon", threads.get(0).isDaemon());
	}

	@Test
	public void shouldTagCorrectlyDaemonThread() {
		String expectedThreadStringInfo = "Thread Id: '0x00007f90d0106000' (daemon), Name: 'Attach Listener', State: 'RUNNABLE'";

		final ThreadInfo th = new ThreadInfo();
		th.setId("0x00007f90d0106000");
		th.setName("Attach Listener");
		th.setState("RUNNABLE");
		th.setDaemon(true);

		assertEquals(expectedThreadStringInfo, th.toString());
		th.setDaemon(false);
		expectedThreadStringInfo = "Thread Id: '0x00007f90d0106000', Name: 'Attach Listener', State: 'RUNNABLE'";
		assertEquals(expectedThreadStringInfo, th.toString());
	}

	@Test
	public void TestHolds() throws IOException {
		final int expectedNumberOfThreads = 1;
		final int expectedNumberOfLocksInThread = 3;

		final Locked[] expectedLocks = {
			new Locked("0x0000000682e5f948", "sun.security.provider.Sun"),
			new Locked("0x00000007bc531138", "java.lang.Object"),
			new Locked("0x00000007bbbac500", "sun.security.ssl.SSLEngineImpl")
		};

		final List<ThreadInfo> threads = new ThreadDumpReader().fromString(THREAD_INFO_WITH_LOCKS);
		assertFalse(threads.isEmpty());

		final Map<ThreadInfo, List<Locked>> holds = ThreadParsing.holds(threads);
		assertEquals(expectedNumberOfThreads, holds.size());

		holds.forEach((thread, locks) -> {
			assertEquals(expectedNumberOfLocksInThread, locks.size());
			for (int i = 0; i < locks.size(); i++) {
				assertEquals(expectedLocks[i], locks.get(i));
			}
		});

	}
	
}
