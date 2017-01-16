package eu.heads.apama.test;

import org.junit.Test;

import eu.heads.apama.ApamaReceiver;

public class ApamaReceiverTest {

	@Test
	public final void testStart() throws InterruptedException {
		ApamaReceiver receiver = new ApamaReceiver();
		receiver.setChannel("com.apama.input.headsChannel");
		receiver.start();
		Thread.sleep(10000L);
		receiver.stop();
	}

}

