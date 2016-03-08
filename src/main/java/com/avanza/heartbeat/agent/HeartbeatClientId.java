package com.avanza.heartbeat.agent;

import java.util.Objects;
import java.util.UUID;

public class HeartbeatClientId {

	private final String id;
	
	private HeartbeatClientId(String id) {
		this.id = id;
	}
	
	public static HeartbeatClientId newId() {
		return new HeartbeatClientId(UUID.randomUUID().toString());
	}
	
	static HeartbeatClientId fromString(String id) {
		return new HeartbeatClientId(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeartbeatClientId other = (HeartbeatClientId) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return id;
	}
	
}
