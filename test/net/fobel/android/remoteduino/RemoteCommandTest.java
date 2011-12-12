package net.fobel.android.remoteduino;

import static org.junit.Assert.fail;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class RemoteCommandTest {

	@Test
	public void testRemoteCommandStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoteCommandStringString() throws IOException {
		RemoteCommand command = new RemoteCommand("TestLabel", "<h1>Please press button on remote...</h1>\r\n" + 
				"<p>Received unknown code, saving as raw</p><p>\r\n" + 
				" m3350 s1800 m350 s550 m350 s1400 m350 s500 m350 s550 m300 s550 m350 s500 m350 s550 m350 s500 m350 s500 m350 s550 m350 s500 m350 s550 m350 s500 m350 s1400 m350 s500 m350 s550 m350 s500 m350 s550 m300 s550 m350 s500 m350 s550 m350 s500 m350 s550 m300 s1400 m350 s550 m350 s500 m350 s550 m300 s550 m350 s500 m350 s550 m350 s500 m350 s500 m350 s550 m350 s500 m350 s550 m300 s550 m350</p>\r\n" + 
				"<h1>OK</h1>\r\n" + 
				"");
		
		int [] expectedRaw = {3350, 1800, 350, 550, 350, 1400, 350};
		Assert.assertEquals("", command.code);
		Assert.assertEquals("0", command.protocol);
//		Assert.assertEquals(75, command.raw.size());  -- Test the first few for now
		for (int i = 0; i < expectedRaw.length; i++){
			expectedRaw[i] = command.raw.get(i);
		}
		
		
		command = new RemoteCommand("TestLabel", "<h1>Please press button on remote...</h1>\r\n" + 
		"<p>Received NEC: \r\n" + 
		"<h2>\r\n" + 
		"4BB6A05F\r\n" + 
		"</h2>\r\n" + 
		"<h1>OK</h1>\r\n" + 
		"");
		
		Assert.assertEquals("4BB6A05F", command.code);
		Assert.assertEquals("1", command.protocol);
		Assert.assertNull(command.raw);
	}

}
