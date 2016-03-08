package com.avanza.heartbeat.agent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;

import com.avanza.heartbeat.agent.util.WebContainerRule;
import com.avanza.heartbeat.agent.util.WebRequestHandler;

public class HttpResourcePropertySourceTest {

	public static class ReturnPropertiesHandler implements WebRequestHandler {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response) {
			try {
				PrintWriter writer = response.getWriter();
				writer.println("foo=bar");
				writer.println("#foo=baz");
				writer.println("foo.baz=foobar");
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Rule
	public WebContainerRule webContainer = new WebContainerRule(new ReturnPropertiesHandler());

	@Test
	public void getProperties() throws Exception {
		HttpResourcePropertySource propertySource = new HttpResourcePropertySource(new URL("http://localhost:" + webContainer.getPort()));
		Properties p = propertySource.getProperties();
		assertThat(p.getProperty("foo"), equalTo("bar"));
		assertThat(p.getProperty("foo.baz"), equalTo("foobar"));
	}

}
