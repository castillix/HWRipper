package org.freegeekarkansas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpManager implements HttpHandler {
    public BuildInfo build;

    public HttpManager(BuildInfo build) {
        this.build = build;
    }

    public void servePage() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8787), 0);
        server.createContext("/", this);
        server.setExecutor(null);
        server.start();
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://localhost:8787"));
        }
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            String response = HTMLBuilder.buildHTML(this.build);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch(Exception e) {
            // do nothing
        }
    }
}
