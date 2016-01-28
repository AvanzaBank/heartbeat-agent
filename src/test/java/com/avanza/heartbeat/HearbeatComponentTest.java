package com.avanza.heartbeat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;

import com.avanza.heartbeat.util.EmbeddedWebContainer;
import com.avanza.heartbeat.util.WebContainerRule;
import com.avanza.heartbeat.util.WebRequestHandler;

public class HearbeatComponentTest {
    
    FakeHBServer hbServer = new FakeHBServer();
    
    @Rule
    public WebContainerRule webRule = new WebContainerRule(hbServer);

    @Test
    public void testName() throws Exception {
    }
    
    private static class FakeHBServer implements WebRequestHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response) {
            request.getParameterMap();
            
        }
        
    }
}
