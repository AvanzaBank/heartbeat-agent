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

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Optional;

/**
 * Java agent for sending HTTP heartbeats to a server.
 * 
 * Launch the agent by adding the JVM argument {@code -javaagent:<path to jar>=<optional url to properties file>}.
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
	
	private static HeartbeatClient client;

	public static void premain(String args, Instrumentation inst) {
		try {
			log.info("Heartbeat Agent started with premain method, args: " + args);
			client = configureClient(args);
			client.start();
		} catch (Exception e) {
			log.warn("Failed to start heart beat client, heartbeats will be disabled");
			log.logStacktrace(e);
		}
	}
	
	public static void stop() {
		if (client != null) {
			log.info("Stopping heartbeat client...");
			client.stop();
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
		Optional<UrlResourcePropertySource> urlSource = createUrlPropertySource(propertyUrl);
		return urlSource
				.map(source -> (PropertySource) new ChainedPropertySource(source, new SystemPropertiesPropertySource()))
				.orElseGet(() -> new SystemPropertiesPropertySource());
	}

	private static Optional<UrlResourcePropertySource> createUrlPropertySource(String propertyUrl) {
		if (propertyUrl == null || propertyUrl.trim().isEmpty()) {
			return Optional.empty();
		}
		try {
			URL url = new URL(propertyUrl);
			UrlResourcePropertySource urlSource = new UrlResourcePropertySource(url);
			return Optional.of(urlSource);
		} catch (Exception e) {
			log.warn("URL property source specified but could not be created, will not create one");
			log.logStacktrace(e);
			return Optional.empty();
		}
	}
}
