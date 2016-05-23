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
