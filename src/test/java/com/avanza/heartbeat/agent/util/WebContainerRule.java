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
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class WebContainerRule implements TestRule {
    
    private static final WebRequestHandler DO_NOTHING_HANDLER = new WebRequestHandler() {
        
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response) {
            
        }
    };

    private EmbeddedWebContainer container;
    
    private WebRequestHandler handler = DO_NOTHING_HANDLER;
    
    /**
     * @param handler handler to be called upon each request.
     */
    public WebContainerRule(WebRequestHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    public WebContainerRule() {
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    container = new EmbeddedWebContainer() {

                        @Override
                        protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
                                throws IOException, ServletException {
                            handler.handle(request, response);
                        }
                        
                    };
                    container.start();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to start web container", e);
                }
                try {
                    base.evaluate();
                } finally {
                    container.stop();
                }

            }
        };
    }
    
    public int getPort() {
        if (container == null) {
            throw new IllegalStateException("container not started");
        }
        return container.getPort();
    }

    
}
