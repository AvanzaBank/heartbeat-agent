package com.avanza.heartbeat.agent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import org.junit.Test;

public class HeartbeatPropertiesResolverTest {

	@Test
	public void resolveProperties() throws Exception {
		Properties p = new Properties();
		p.setProperty("heartbeat.agent.application.name", "my-app");
		p.setProperty("heartbeat.agent.url", "http://my.heartbeat.server/beat");
		p.setProperty("heartbeat.agent.application.version", "1.0");
		System.setProperty("com.sun.management.jmxremote.port", "123");
		HeartbeatPropertiesResolver resolver = new HeartbeatPropertiesResolver(() -> p);
		HeartbeatProperties props = resolver.resolveProperties();
		assertThat(props.getApplicationName(), equalTo("my-app"));
		assertThat(props.getUrl(), equalTo(new URL("http://my.heartbeat.server/beat")));
		assertThat(props.getVersion(), equalTo("1.0"));
		assertThat(props.getPid(), greaterThan(0));
		assertThat(props.getJmxPort(), not(equalTo(Optional.empty())));
	}

}
