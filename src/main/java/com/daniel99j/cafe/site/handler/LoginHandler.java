package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("POST")) {
                UserLoader.login(exchange);

                String response = "Logged in";


                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else if (exchange.getRequestMethod().equals("PUT")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                User user = UserLoader.login(json.get("username").getAsString(), json.get("password").getAsString());
                exchange.getResponseHeaders().add("Set-Cookie", "Token=" + user.getPassword());
                exchange.getResponseHeaders().add("Set-Cookie", "Username=" + json.get("username").getAsString());

                String response = "Logged in";

                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else throw new Exception("Invalid request");
        } catch (Exception e) {
            String response = "Bad Request";
            exchange.sendResponseHeaders(e instanceof IllegalAccessException ? 401 : 500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
