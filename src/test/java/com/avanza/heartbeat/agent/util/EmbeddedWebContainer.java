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
package com.avanza.heartbeat.agent.util;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Embedded web container that can be used in tests. Extend this class and implement the handleRequest() method.
 * 
 * @author Kristoffer Erlandsson (krierl), kristoffer.erlandsson@avanzabank.se
 */
public class EmbeddedWebContainer {

    private static final int NUM_TRIES_ON_RANDOM_PORT = 4;
    private volatile int port;
    private boolean randomGeneratePort = true;
    private Server server;
    private boolean started = false;

    public EmbeddedWebContainer(int port) {
        this.port = port;
        randomGeneratePort = false;
    }

    /**
     * Creates an embedded web container that will use a random generated available port
     */
    public EmbeddedWebContainer() {
    }

    /**
     * Starts this web container.
     * 
     * @throws Exception
     */
    public final synchronized void start() throws Exception {
        verifyNotStarted();
        int startRetries = randomGeneratePort ? NUM_TRIES_ON_RANDOM_PORT : 1;
        BindException lastException = null;
        // We retry on new ports a couple of times in case we have a race condition and the chosen port is busy.
        for (int i = 0; i < startRetries; i++) {
            if (randomGeneratePort) {
                generateNewPort();
            }
            try {
                startJettyServer();
                started = true;
                break;
            } catch (BindException e) {
                e.printStackTrace();
                lastException = e;
            }
        }
        if (!started) {
            throw new WebContainerBindException("Failed to start, last exception chained", lastException);
        }
    }

    private void verifyNotStarted() {
        if (started) {
            throw new IllegalStateException("Server is already running");
        }
    }

    private void startJettyServer() throws Exception {
        server = new Server();
        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        // We set reuse address to false, otherwise multiple instances of jetty can start on the same port
        // with weird behavior.
        connector.setReuseAddress(false);
        connector.setPort(this.port);
        server.setConnectors(new Connector[]{connector});
        server.setHandler(new AbstractHandler() {

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                    HttpServletResponse response) throws IOException, ServletException {
                handleRequest(request, response);
            }

        });
        server.start();
    }

    /**
     * Override this method to provide your own behavior.
     */
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {

    }

    private void generateNewPort() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        serverSocket.close();
    }

    /**
     * Stops this web container.
     * 
     * @throws Exception
     */
    public final synchronized void stop() throws Exception {
        if (!started) {
            return;
        }
        server.stop();
        started = false;
    }

    public final int getPort() {
        if (!started) {
            throw new IllegalStateException("Server must be started to get the port");
        }
        return port;
    }

    public final class WebContainerBindException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public WebContainerBindException(String msg, BindException exception) {
            super(msg, exception);
        }

    }

}

