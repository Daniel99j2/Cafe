package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.daniel99j.cafe.ordering.Order;
import com.daniel99j.cafe.ordering.OrderItem;
import com.daniel99j.cafe.ordering.OrderManager;
import com.daniel99j.cafe.ordering.OrderStatus;
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
                    o.failReason = exchange.getRequestHeaders().getFirst("FailReason");
                    o.setStatus(OrderStatus.FAILED);
                }
                else if(o.orderer == user && o.getStatus() == OrderStatus.SENDING) o.setStatus(OrderStatus.CANCELLED);
                else throw new IllegalArgumentException("Incorrect user");
            } else if(exchange.getRequestMethod().equals("PUT")) {
                if(o.getStatus() == OrderStatus.SENDING) o.setStatus(OrderStatus.PREPARING);
                else if(o.getStatus() == OrderStatus.PREPARING) o.setStatus(OrderStatus.DELIVERING);
                else throw new IllegalArgumentException("Invalid status");
            } else if(exchange.getRequestMethod().equals("POST") && o.deliverer == user && o.getStatus() == OrderStatus.DELIVERING) {
                if(exchange.getRequestHeaders().getFirst("Code").equals(o.verificationCode)) {
                    o.setStatus(OrderStatus.SOLD);
                    for (OrderItem item : o.items) {
                        o.deliverer.balance+=item.quantity*item.item.prepareValue;
                        o.deliverer.balance+=item.quantity*item.item.price;

                        o.orderer.balance-=item.quantity*item.item.price;
                    }
                }
                else throw new IllegalArgumentException("Invalid code");
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
