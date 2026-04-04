package com.daniel99j.site;

import com.daniel99j.User;
import com.daniel99j.UserLoader;
import com.daniel99j.ordering.Order;
import com.daniel99j.ordering.OrderItem;
import com.daniel99j.ordering.OrderManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class PurchaseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.login(exchange);

            ArrayList<OrderItem> items = new ArrayList<>();

            JsonObject object = JsonParser.parseString(data).getAsJsonObject();

            Order order = new Order(0, items, user, UserLoader.getUser("Hugo"));

            OrderManager.addOrder(order);

            String response = "http://localhost:8080/order?id="+order.uuid.toString();

            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e) {
            String response = "Bad Request";
            exchange.sendResponseHeaders(400, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
