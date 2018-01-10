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

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class HeartbeatProperties {

	private final String applicationName;
	private final int pid;
	private final String version;
	private final Integer jmxPort;
	private final URL url;
	private final int initialDelayMs;

	public HeartbeatProperties(URL url, String applicationName, int pid, String version, int initialDelayMs, int jmxPort) {
		this.initialDelayMs = initialDelayMs;
		this.url = Objects.requireNonNull(url);
		this.applicationName = Objects.requireNonNull(applicationName);
		validatePid(pid);
		this.pid = pid;
		this.version = Objects.requireNonNull(version);
		validateJmxPort(jmxPort);
		this.jmxPort = jmxPort;
	}

	public HeartbeatProperties(URL url, String applicationName, int pid, String version, int initialDelayMs) {
		this.initialDelayMs = initialDelayMs;
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
	
	public int getInitialDelayMs() {
		return initialDelayMs;
	}
	
	@Override
	public String toString() {
		return "HeartbeatProperties [applicationName=" + applicationName + ", pid=" + pid + ", version=" + version
				+ ", jmxPort=" + jmxPort + ", url=" + url + ", initialDelayMs=" + initialDelayMs + "]";
	}

}
