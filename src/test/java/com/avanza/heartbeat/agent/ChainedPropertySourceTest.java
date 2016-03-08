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
