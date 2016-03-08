package com.avanza.heartbeat.agent;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Optional;

/**
 * Java agent for sending HTTP heartbeats to a server.
 * 
 * Launch the agent by adding the JVM argument {@code -javaagent:<path to jar>=<optional http url to properties file>}.
 * The following properties are required:
 * <ul>
 * <li>{@code heartbeat.agent.application.name=<application name>}
 * <li>{@code heartbeat.agent.url=<URL to where heartbeats should be sent>}
 * </ul>
 * <p>In addition an optional {@code heartbeat.agent.application.version} property is support for send the application's
 * version number in the heartbeat.
 * 
 * <p>Properties will first be looked up in the properties file specified as a HTTP url in the agent argument. If no file
 * is specified or if a property cannot be found there, System properties will be checked.
 * 
 * @author Kristoffer Erlandsson
 */
public class HeartbeatAgent {

	private static final Logger log = new Logger(HeartbeatAgent.class);

	public static void premain(String args, Instrumentation inst) {
		try {
			HeartbeatClient client = configureClient(args);
			client.start();
		} catch (Exception e) {
			log.warn("Failed to start heart beat client, heartbeats will be disabled");
			log.logStacktrace(e);
		}
	}

	private static HeartbeatClient configureClient(String propertyUrl) {
		PropertySource propertySource = createPropertySource(propertyUrl);
		log.info("Will use the following property source: " + propertySource);
		HeartbeatPropertiesResolver propertiesResolver = new HeartbeatPropertiesResolver(propertySource);
		HeartbeatProperties heartbeatProperties = propertiesResolver.resolveProperties();
		HeartbeatClient client = new HeartbeatClient(heartbeatProperties);
		return client;
	}

	private static PropertySource createPropertySource(String propertyUrl) {
		Optional<HttpResourcePropertySource> httpSource = createHttpPropertySource(propertyUrl);
		return httpSource
				.map(source -> (PropertySource) new ChainedPropertySource(new SystemPropertiesPropertySource(), source))
				.orElseGet(() -> new SystemPropertiesPropertySource());
	}

	private static Optional<HttpResourcePropertySource> createHttpPropertySource(String propertyUrl) {
		if (propertyUrl == null || propertyUrl.trim().isEmpty()) {
			return Optional.empty();
		}
		try {
			URL url = new URL(propertyUrl);
			HttpResourcePropertySource httpSource = new HttpResourcePropertySource(url);
			return Optional.of(httpSource);
		} catch (Exception e) {
			log.warn("HTTP property source specified but could not be created, will not create one");
			log.logStacktrace(e);
			return Optional.empty();
		}
	}
}
