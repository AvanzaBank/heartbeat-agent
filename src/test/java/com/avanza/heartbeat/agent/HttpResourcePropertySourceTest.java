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
