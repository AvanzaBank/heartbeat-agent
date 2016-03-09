package com.avanza.heartbeat.agent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatClient {

	private static final int READ_TIMEOUT = 2000;
	private static final int CONNECT_TIMEOUT = 1000;
	private static final long DEFAULT_HEARTBEAT_INTERVAL = 5000;
	private static final String USER_AGENT = "Heartbeat Client";
	private final HeartbeatClientId id;
	private final HeartbeatProperties props;
	private final URL heartbeatUrl;
	private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(HeartbeatClient::createDaemonThread);
	private final Logger log = new Logger(HeartbeatClient.class);
	private long heartbeatInterval;
	
	private static Thread createDaemonThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName("Heartbeat Thread");
		t.setDaemon(true);
		return t;
	}

	public HeartbeatClient(HeartbeatProperties props) {
		this(props, HeartbeatClientId.newId(), DEFAULT_HEARTBEAT_INTERVAL);
	}

	HeartbeatClient(HeartbeatProperties props, HeartbeatClientId id, long heartbeatInterval) {
		this.props = Objects.requireNonNull(props);
		this.id = id;
		this.heartbeatUrl = buildUrl();
		this.heartbeatInterval = heartbeatInterval;
	}

	public void start() {
		log.info("Starting heartbeat client with properties: " + props);
		scheduledExecutor.scheduleAtFixedRate(this::doBeat, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
	}
	
	public void stop() {
		log.info("Stopping heartbeat client with properties: " + props);
		scheduledExecutor.shutdown();
	}
	
	private void doBeat() {
		try {
			tryBeat();
		} catch (Exception e) {
			log.warn("Error when sending heartbeat");
			log.logStacktrace(e);
		}
	}

	private void tryBeat() {
		HttpURLConnection connection = createConnection(heartbeatUrl);
		int responseCode = connectAndGetResponseCode(connection);
		if (responseCode != HttpURLConnection.HTTP_OK) {
			log.warn("Error response from heartbeat server - response code: " + responseCode);
		}
	}

	private int connectAndGetResponseCode(HttpURLConnection connection) {
		try {
			connection.connect();
			int responseCode = connection.getResponseCode();
			connection.disconnect();
			return responseCode;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpURLConnection createConnection(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.addRequestProperty("beat-interval", Long.toString(heartbeatInterval));
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
