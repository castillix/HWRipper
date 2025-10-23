package org.freegeekarkansas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;

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
        URI requestURI = t.getRequestURI();
        String path = requestURI.getPath();
        System.out.println(path);

        if(path.equals("/")) {
            try {
                String response = HTMLBuilder.buildHTML(this.build);
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                // do nothing
            }
        } else if(path.equals("/src/main/resources/FreeGeekLogo.png")) {
            try {
                File file = new File("FreeGeekLogo.png");
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                System.out.println(fileBytes.length);

                t.getResponseHeaders().set("Content-Type", "image/png");
                t.sendResponseHeaders(200, fileBytes.length);

                OutputStream os = t.getResponseBody();
                os.write(fileBytes);
                os.close();
            } catch(Exception e) {
                System.out.println("bad");
                throw new RuntimeException(e);
            }
        }
    }
}
