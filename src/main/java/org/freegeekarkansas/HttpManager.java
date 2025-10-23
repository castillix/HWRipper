package org.freegeekarkansas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                System.out.println("test");
                InputStream logo = this.getClass().getClassLoader().getResourceAsStream("FreeGeekLogo.png");

                byte[] fileBytes = IOUtils.toByteArray(logo);

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
        System.out.println(path);
    }
}
