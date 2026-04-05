package com.daniel99j.site;

import com.daniel99j.User;
import com.daniel99j.UserLoader;
import com.daniel99j.ordering.Order;
import com.daniel99j.ordering.OrderManager;
import com.daniel99j.ordering.OrderStatus;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class DeliverApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.login(exchange);
            UUID id = UUID.fromString(exchange.getRequestURI().toString().replace("/api/deliver?id=", ""));
            Order o = OrderManager.orders.stream().filter((order -> order.uuid.equals(id))).findFirst().orElse(null);
            String response = "Done";

            if(exchange.getRequestMethod().equals("DELETE")) {
                if(o.deliverer == user) {
                    o.status = OrderStatus.FAILED;
                    o.failReason = exchange.getRequestHeaders().getFirst("FailReason");
                }
                else if(o.orderer == user) o.status = OrderStatus.CANCELLED;
                else throw new IllegalArgumentException("Incorrect user");
            } else if(exchange.getRequestMethod().equals("PUT")) {
                if(o.status == OrderStatus.SENDING) o.status = OrderStatus.PREPARING;
                else if(o.status == OrderStatus.PREPARING) o.status = OrderStatus.DELIVERING;
                else if(o.status == OrderStatus.DELIVERING) o.status = OrderStatus.SOLD;
                else throw new IllegalArgumentException("Invalid status");
            } else throw new IllegalArgumentException("Invalid request");

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
