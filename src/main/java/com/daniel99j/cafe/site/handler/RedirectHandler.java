package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.site.SiteGenerator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RedirectHandler implements HttpHandler {
    private final String url;

    public RedirectHandler(String url) {
        this.url = url;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = SiteGenerator.redirect(url);
        exchange.sendResponseHeaders(200, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
