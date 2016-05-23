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