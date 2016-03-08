package com.avanza.heartbeat.agent;

import java.util.Objects;

public class HeartbeatProperties {

	private final String applicationName;
	private final int pid;
	private final String version;
	private final Integer jmxPort;

	public HeartbeatProperties(String applicationName, int pid, String version, int jmxPort) {
		this.applicationName = Objects.requireNonNull(applicationName);
		validatePid(pid);
		this.pid = pid;
		this.version = Objects.requireNonNull(version);
		validateJmxPort(jmxPort);
		this.jmxPort = jmxPort;
	}

	public HeartbeatProperties(String applicationName, int pid, String version) {
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
		if (pid < 1) {
			throw new IllegalArgumentException("Pid must be > 0, was: " + pid);
		}
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

	public Integer getJmxPort() {
		return jmxPort;
	}
	
}
