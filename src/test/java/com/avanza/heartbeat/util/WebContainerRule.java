package com.avanza.heartbeat.util;



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
