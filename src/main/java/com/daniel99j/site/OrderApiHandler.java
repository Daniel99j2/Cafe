package com.daniel99j.site;

import com.daniel99j.User;
import com.daniel99j.UserLoader;
import com.daniel99j.ordering.Order;
import com.daniel99j.ordering.OrderManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class OrderApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.loginOrNull(exchange);
            UUID id = UUID.fromString(exchange.getRequestURI().toString().replace("/api/order?id=", ""));
            Order o = OrderManager.orders.stream().filter((order -> order.uuid.equals(id))).findFirst().orElse(null);
            String response = o.status.toString() + ":" + (o.orderer == user ? "AUTHORIZED" : "DENIED") + ":" + o.failReason;

            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
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
