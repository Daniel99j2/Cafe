package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.daniel99j.cafe.site.SiteGenerator;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class AccountApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.loginOrNull(exchange);
            if(user == null) {
                String response = SiteGenerator.login();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }
            JsonObject response = new JsonObject();
            response.addProperty("name", user.name);
            response.addProperty("balance", user.balance);
            response.addProperty("discordId", user.discordId);

            exchange.sendResponseHeaders(200, response.toString().getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.toString().getBytes());
            }
        } catch (Exception e) {
            String response = "Bad Request";
            exchange.sendResponseHeaders(500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
