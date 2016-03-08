package com.avanza.heartbeat.agent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

public class HeartbeatClient {

	private static final int READ_TIMEOUT = 2000;
	private static final int CONNECT_TIMEOUT = 1000;
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
		URL url = buildUrl();
		HttpURLConnection connection = createConnection(url);
		connectAndGetResponseCode(connection);
	}

	private int connectAndGetResponseCode(HttpURLConnection connection) {
		try {
			connection.connect();
			return connection.getResponseCode();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpURLConnection createConnection(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			return connection;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private URL buildUrl() {
		StringBuilder query = new StringBuilder()
				.append("name=")
				.append(encode(props.getApplicationName()))
				.append("&pid=")
				.append(encode(props.getPid()))
				.append("&uid=")
				.append(encode(id.toString()))
				.append("&revision=")
				.append(encode(props.getVersion()));
		props.getJmxPort().ifPresent(jmxPort -> query.append("&jmx=").append(jmxPort));
		String url = props.getUrl() + "?" + query.toString();
		return createUrl(url);
	}

	private String encode(int n) {
		return encode("" + n);
	}

	private String encode(String queryString) {
		try {
			return URLEncoder.encode(queryString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private URL createUrl(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
