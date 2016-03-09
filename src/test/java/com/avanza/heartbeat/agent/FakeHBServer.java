package com.avanza.heartbeat.agent;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.avanza.heartbeat.agent.util.WebRequestHandler;

class FakeHBServer implements WebRequestHandler {

	private List<Map<String, String>> handledRequests = new ArrayList<>();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Set<Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
		Map<String, String> params = entrySet.stream()
				.collect(toMap(entry -> entry.getKey(), this::getValueOrThrowIfMultiple));
		handledRequests.add(params);
		try {
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getValueOrThrowIfMultiple(Entry<String, String[]> entry) {
		if (entry.getValue().length > 1) {
			throw new IllegalArgumentException("Multi value parameter received");
		}
		return entry.getValue()[0];
	}

	public List<Map<String, String>> getHandledRequests() {
		return handledRequests;
	}

}