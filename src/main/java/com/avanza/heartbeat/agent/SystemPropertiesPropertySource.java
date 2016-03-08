package com.avanza.heartbeat.agent;

import java.util.Properties;

public class SystemPropertiesPropertySource implements PropertySource {

	@Override
	public Properties getProperties() {
		return System.getProperties();
	}

}
