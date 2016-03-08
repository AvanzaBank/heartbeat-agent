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
