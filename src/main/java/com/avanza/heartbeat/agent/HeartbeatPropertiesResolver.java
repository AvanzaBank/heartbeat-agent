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

import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class HeartbeatPropertiesResolver {
	
	private final static String NAME_PROPERTY = "heartbeat.agent.application.name";
	private final static String URL_PROPERTY = "heartbeat.agent.url";
	private final static String VERSION_PROPERTY = "heartbeat.agent.application.version";
	private final static String DEFAULT_VERSION = "UNKNOWN"; // Used of version is not set
	
	private final PropertySource propertySource;
	
	public HeartbeatPropertiesResolver(PropertySource propertySource) {
		this.propertySource = Objects.requireNonNull(propertySource);
	}

	public HeartbeatProperties resolveProperties() {
		Properties props = propertySource.getProperties();
		String version = getVersion(props);
		URL url = getUrl(props);
		String applicationName = getRequiredProperty(props, NAME_PROPERTY);
		int pid = getPid();
		Integer jmxPort = getJmxPort();
		if (jmxPort != null) {
			return new HeartbeatProperties(url, applicationName, pid, version, jmxPort);
		} else {
			return new HeartbeatProperties(url, applicationName, pid, version);
		}
	}

	private Integer getJmxPort() {
		String value = System.getProperty("com.sun.management.jmxremote.port");
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String getVersion(Properties props) {
		return Optional.ofNullable(props.getProperty(VERSION_PROPERTY)).orElse(DEFAULT_VERSION);
	}

	private String getRequiredProperty(Properties props, String propertyName) {
		String value = props.getProperty(propertyName);
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Required property " + propertyName + " is missing or empty");
		}
		return value;
	}

	private URL getUrl(Properties props) {
		try {
			String urlString = getRequiredProperty(props, URL_PROPERTY);
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed heartbeat url", e);
		}
	}
	
	private int getPid() {
		try {
			String jvmName = ManagementFactory.getRuntimeMXBean().getName();
			return Integer.valueOf(jvmName.split("@")[0]);
		} catch (Exception e) {
			return Integer.valueOf(0);
		}
	}



}
