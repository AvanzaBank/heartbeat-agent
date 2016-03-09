package com.avanza.heartbeat.agent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.avanza.heartbeat.agent.util.WebContainerRule;
import com.avanza.heartbeat.agent.util.WebRequestHandler;

public class HeartbeatAgentAcceptanceTest {
	
	private FakeHBServer hbServer = new FakeHBServer();

	public WebContainerRule heartbeatServerContainer = new WebContainerRule(hbServer);
	
	public WebContainerRule propertiesServerContainer = new WebContainerRule(new WebRequestHandler() {
		
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response) {
			Properties p = new Properties();
			p.put("heartbeat.agent.url", "http://localhost:" + heartbeatServerContainer.getPort() + "/beat");
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
	
	@Rule
	public RuleChain rules = RuleChain.outerRule(propertiesServerContainer).around(heartbeatServerContainer);

	@SuppressWarnings("unchecked")
	@Test
	public void testBeat() throws Exception {
		HeartbeatAgent.premain("http://localhost:" + propertiesServerContainer.getPort(), null);
		AsyncTestUtils.eventually(() -> assertThat(hbServer.getHandledRequests(),
				contains(
						// Test that two beats are received
						allOf(hasEntry("name", "acceptance-test"), hasEntry("revision", "1.2")),
						allOf(hasEntry("name", "acceptance-test"), hasEntry("revision", "1.2")))),
				8000);
	}
	
	@After
	public void stopAgent() {
		HeartbeatAgent.stop();
	}

}
