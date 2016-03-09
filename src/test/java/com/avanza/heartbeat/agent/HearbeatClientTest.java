package com.avanza.heartbeat.agent;

import static com.avanza.heartbeat.agent.AsyncTestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.avanza.heartbeat.agent.util.WebContainerRule;

public class HearbeatClientTest {
    
    private FakeHBServer hbServer = new FakeHBServer();
    
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
    	HeartbeatProperties props = new HeartbeatProperties(new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 11223);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"), 5000);
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
    	HeartbeatProperties props = new HeartbeatProperties(new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0");
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"), 5000);
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
    	HeartbeatProperties props = new HeartbeatProperties(new URL("http://localhost:" + webRule.getPort()), "my-app", 123, "1.0.0", 11223);
    	client = new HeartbeatClient(props, HeartbeatClientId.fromString("abc123123abc"), 200);
    	client.start();
    	Map<String, String> expected = new HashMap<>();
    	expected.put("name", "my-app");
    	expected.put("pid", "123");
    	expected.put("uid", "abc123123abc");
    	expected.put("revision", "1.0.0");
    	expected.put("jmx", "11223");
    	eventually(() -> assertThat(hbServer.getHandledRequests(), contains(expected, expected)), 2000);
    }
    

}
