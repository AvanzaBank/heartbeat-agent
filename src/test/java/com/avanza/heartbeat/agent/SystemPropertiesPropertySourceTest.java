package com.avanza.heartbeat.agent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class SystemPropertiesPropertySourceTest {

	@Test
	public void getProperties() throws Exception {
		System.setProperty("test.property.foo", "foobar");
		System.setProperty("test.property.bar", "baz");
		SystemPropertiesPropertySource source = new SystemPropertiesPropertySource();
		Properties properties = source.getProperties();
		assertThat(properties.getProperty("test.property.foo"), equalTo("foobar"));
		assertThat(properties.getProperty("test.property.bar"), equalTo("baz"));
	}
}
