package com.avanza.heartbeat.agent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.avanza.heartbeat.agent.util.WebContainerRule;
import com.avanza.heartbeat.agent.util.WebRequestHandler;

/**
 * @author Kristoffer Erlandsson
 */
public class HeartbeatAgentAcceptanceTest {

	private FakeHBServer hbServer = new FakeHBServer();

	public WebContainerRule heartbeatServerContainer = new WebContainerRule(hbServer);

	public WebContainerRule propertiesServerContainer = new WebContainerRule(new WebRequestHandler() {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response) {
			Properties p = new Properties();
			p.put("heartbeat.agent.url", "http://localhost:" + heartbeatServerContainer.getPort());
			p.put("heartbeat.agent.application.name", "acceptance-test");
			p.put("heartbeat.agent.application.version", "1.2");
			try {
				p.store(response.getOutputStream(), "");
				response.flushBuffer();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	});

	private Collection<String> systemPropertiesToClear = new ArrayList<>();

	@Rule
	public RuleChain rules = RuleChain.outerRule(propertiesServerContainer).around(heartbeatServerContainer);

	@SuppressWarnings("unchecked")
	@Test
	public void testWithHttpPropertiesSource() throws Exception {
		HeartbeatAgent.premain("http://localhost:" + propertiesServerContainer.getPort(), null);
		AsyncTestUtils.eventually(() -> assertThat(hbServer.getHandledRequests(),
				contains(
						// Test that two beats are received
						allOf(hasEntry("name", "acceptance-test"), hasEntry("revision", "1.2")),
						allOf(hasEntry("name", "acceptance-test"), hasEntry("revision", "1.2")))),
				8000);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithSystemPropertiesOnly() throws Exception {
		setSystemProperty("heartbeat.agent.url", "http://localhost:" + heartbeatServerContainer.getPort());
		setSystemProperty("heartbeat.agent.application.name", "acceptance-test-system-properties");
		setSystemProperty("heartbeat.agent.application.version", "1.3");
		HeartbeatAgent.premain("", null);
		AsyncTestUtils.eventually(() -> assertThat(hbServer.getHandledRequests(),
				contains(
						// Test that two beats are received
						allOf(hasEntry("name", "acceptance-test-system-properties"), hasEntry("revision", "1.3")),
						allOf(hasEntry("name", "acceptance-test-system-properties"), hasEntry("revision", "1.3")))),
				8000);
	}

	@After
	public void stopAgent() {
		HeartbeatAgent.stop();
	}

	@After
	public void clearSystemProperties() {
		systemPropertiesToClear.stream().forEach(key -> System.clearProperty(key));
	}

	private void setSystemProperty(String key, String value) {
		systemPropertiesToClear.add(key);
		System.setProperty(key, value);
	}

}
