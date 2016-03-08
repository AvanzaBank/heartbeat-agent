package com.avanza.heartbeat.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class HttpResourcePropertySource implements PropertySource {

	private final URL url;

	public HttpResourcePropertySource(URL url) {
		this.url = url;
	}

	@Override
	public Properties getProperties() {
		try {
			return tryGetProperties();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Properties tryGetProperties() throws IOException {
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(1000);
		connection.setReadTimeout(3000);
		connection.connect();
		Properties p = new Properties();
		try (InputStream stream = connection.getInputStream()) {
			p.load(stream);
		}
		return p;
	}

}
