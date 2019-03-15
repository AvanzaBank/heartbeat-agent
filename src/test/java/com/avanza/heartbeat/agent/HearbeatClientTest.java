/**
 * The MIT License
 * Copyright (c) 2016 Avanza Bank AB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.avanza.heartbeat.agent;

import static com.avanza.heartbeat.agent.AsyncTestUtils.eventually;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.avanza.heartbeat.agent.util.WebContainerRule;

public class HearbeatClientTest {
    
    private final FakeHBServer hbServer = new FakeHBServer();
    
    @Rule
    public WebContainerRule webRule = new WebContainerRule(hbServer);

	private HeartbeatClient client;

	@After
	public void stopClient() {
		if (client != null) {
			client.stop();
		}
	}
	
    @Test
    public void allProperties() throws Exception {
    	HeartbeatProperties props = new HeartbeatProperties(true, new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 0, 5000, 11223);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"));
    	client.start();
    	Map<String, String> expected = new HashMap<>();
    	expected.put("name", "my-app");
    	expected.put("pid", "123");
    	expected.put("uid", "abc123123abc");
    	expected.put("revision", "1.0.0");
    	expected.put("jmx", "11223");
    	eventually(() -> assertThat(hbServer.getHandledRequests(), hasItem(expected)), 1000);
    }

    @Test
    public void optionalJmxPort() throws Exception {
    	HeartbeatProperties props = new HeartbeatProperties(true, new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 0, 5000);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"));
    	client.start();
    	Map<String, String> expected = new HashMap<>();
    	expected.put("name", "my-app");
    	expected.put("pid", "123");
    	expected.put("uid", "abc123123abc");
    	expected.put("revision", "1.0.0");
    	eventually(() -> assertThat(hbServer.getHandledRequests(), hasItem(expected)), 1000);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void moreThanOneHeartbeatIsMade() throws Exception {
    	HeartbeatProperties props = new HeartbeatProperties(true, new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 0, 200, 11223);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"));
    	client.start();
    	Map<String, String> expected = new HashMap<>();
    	expected.put("name", "my-app");
    	expected.put("pid", "123");
    	expected.put("uid", "abc123123abc");
    	expected.put("revision", "1.0.0");
    	expected.put("jmx", "11223");
    	eventually(() -> assertThat(hbServer.getHandledRequests(), contains(expected, expected)), 2000);
    }
    
	@Test
    public void handleInitialDelay() throws Exception {
    	HeartbeatProperties props = new HeartbeatProperties(true, new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 2000, 11223, 200);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"));
    	client.start();
    	Thread.sleep(1000);
    	assertThat(hbServer.getHandledRequests(), empty());
    }

	@Test
    public void handleDisabled() throws Exception {
    	HeartbeatProperties props = new HeartbeatProperties(false, new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 0, 10_000, 200);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"));
    	client.start();
    	Thread.sleep(1000);
    	assertThat(hbServer.getHandledRequests(), empty());
    }

}
