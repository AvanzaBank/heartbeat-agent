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

import java.util.Properties;

import org.junit.Test;

public class ChainedPropertySourceTest {

	@Test
	public void getProperties() throws Exception {
		Properties p1 = new Properties();
		p1.setProperty("foo", "bar");
		p1.setProperty("bar", "baz");
		Properties p2 = new Properties();
		p2.setProperty("foo", "bar2");
		p2.setProperty("wee", "eew");
		ChainedPropertySource source = new ChainedPropertySource(new StaticPropertySource(p1), new StaticPropertySource(p2));
		Properties p = source.getProperties();
		assertThat(p.getProperty("foo"), equalTo("bar2"));
		assertThat(p.getProperty("bar"), equalTo("baz"));
		assertThat(p.getProperty("wee"), equalTo("eew"));
	}
	
	
	private static class StaticPropertySource implements PropertySource {
		private final Properties p;

		public StaticPropertySource(Properties p) {
			this.p = p;
		}

		@Override
		public Properties getProperties() {
			return p;
		}
		
	}
}
