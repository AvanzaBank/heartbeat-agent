package com.avanza.heartbeat.agent;

import java.util.Objects;

public class HeartbeatClient {
	
	private final HeartbeatClientId id;
	private final HeartbeatProperties props;

	public HeartbeatClient(HeartbeatProperties props) {
		this.props = Objects.requireNonNull(props);
		this.id = HeartbeatClientId.newId();
	}

	HeartbeatClient(HeartbeatProperties props, HeartbeatClientId id) {
		this.props = Objects.requireNonNull(props);
		this.id = id;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
