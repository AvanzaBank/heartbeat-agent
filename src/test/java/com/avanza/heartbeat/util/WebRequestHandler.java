package com.avanza.heartbeat.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface WebRequestHandler {

    void handle(HttpServletRequest request, HttpServletResponse response);

}
