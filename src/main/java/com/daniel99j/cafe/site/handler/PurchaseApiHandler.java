package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.Items;
import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.daniel99j.cafe.ordering.Order;
import com.daniel99j.cafe.ordering.OrderItem;
import com.daniel99j.cafe.ordering.OrderManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class PurchaseApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.login(exchange);

            ArrayList<OrderItem> items = new ArrayList<>();

            //JsonObject object = JsonParser.parseString(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject object = JsonParser.parseString(exchange.getRequestHeaders().getFirst("Cart")).getAsJsonObject();

            for (JsonElement cart : object.get("cart").getAsJsonArray()) {
                JsonObject item = cart.getAsJsonObject();
                int amount = item.get("quantity").getAsInt();
                if(amount <= 0) throw new IllegalArgumentException("Amount cannot be negative or zero");
                if(amount > 10) throw new IllegalArgumentException("Amount cannot be greater than 10");
                items.add(new OrderItem(Items.items.get(item.get("name").getAsString()), amount));
            }

            if(items.isEmpty()) throw new IllegalArgumentException("No items");

            User deliverer = UserLoader.getUser(object.get("deliverer").getAsString());

            Order order = new Order(items, user, deliverer, object.get("deliveryLocation").getAsString());

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
