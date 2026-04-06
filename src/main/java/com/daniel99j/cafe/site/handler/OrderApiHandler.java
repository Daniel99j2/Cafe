package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.daniel99j.cafe.ordering.Order;
import com.daniel99j.cafe.ordering.OrderManager;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class OrderApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.loginOrNull(exchange);
            UUID id = UUID.fromString(exchange.getRequestURI().toString().replace("/api/order?id=", ""));
            Order o = OrderManager.orders.stream().filter((order -> order.uuid.equals(id))).findFirst().orElse(null);
            JsonObject response = new JsonObject();
            response.addProperty("status", o.getStatus().toString());
            response.addProperty("authorization", o.orderer == user);
            response.addProperty("deliverer", o.deliverer == user);
            response.addProperty("failReason", o.failReason);
            response.addProperty("deliveryLocation", o.deliverer == user ? o.deliveryLocation : "HIDDEN");
            response.addProperty("verificationCode", o.orderer == user ? o.verificationCode : "HIDDEN");
            response.addProperty("currentStatusExpiry", o.currentStatusExpiry != null ? String.valueOf(Instant.now().until(o.currentStatusExpiry, ChronoUnit.SECONDS)) : null);

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
