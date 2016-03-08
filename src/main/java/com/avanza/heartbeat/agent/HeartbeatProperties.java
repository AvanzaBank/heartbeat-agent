package com.avanza.heartbeat.agent;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class HeartbeatProperties {

	private final String applicationName;
	private final int pid;
	private final String version;
	private final Integer jmxPort;
	private final URL url;

	public HeartbeatProperties(URL url, String applicationName, int pid, String version, int jmxPort) {
		this.url = Objects.requireNonNull(url);
		this.applicationName = Objects.requireNonNull(applicationName);
		validatePid(pid);
		this.pid = pid;
		this.version = Objects.requireNonNull(version);
		validateJmxPort(jmxPort);
		this.jmxPort = jmxPort;
	}

	public HeartbeatProperties(URL url, String applicationName, int pid, String version) {
		this.url = Objects.requireNonNull(url);
		this.applicationName = Objects.requireNonNull(applicationName);
		validatePid(pid);
		this.pid = pid;
		this.version = Objects.requireNonNull(version);
		this.jmxPort = null;
	}

	private void validateJmxPort(int jmxPort) {
		if (jmxPort < 1 || jmxPort > 65536) {
			throw new IllegalArgumentException("Illegal jmx port: " + jmxPort);
		}
	}

	private void validatePid(int pid) {
		if (pid < 0) {
			throw new IllegalArgumentException("Pid must be >= 0, was: " + pid);
		}
	}

	public URL getUrl() {
		return url;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public int getPid() {
		return pid;
	}

	public String getVersion() {
		return version;
	}

	public Optional<Integer> getJmxPort() {
		return Optional.ofNullable(jmxPort);
	}
	
	public String toString() {
		return "HeartbeatProperties [applicationName=" + applicationName + ", pid=" + pid + ", version=" + version
				+ ", jmxPort=" + jmxPort + ", url=" + url + "]";
	}

}
