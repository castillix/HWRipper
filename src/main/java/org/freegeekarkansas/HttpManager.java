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
    public static void servePage(BuildInfo build) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8787), 0);
        server.createContext("/", new HttpManager());
        server.setExecutor(null);
        server.start();
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://localhost:8787"));
        }
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "Get http payload";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
