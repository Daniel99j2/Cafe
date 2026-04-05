package com.daniel99j.site;

import com.daniel99j.Items;
import com.daniel99j.User;
import com.daniel99j.UserLoader;
import com.daniel99j.ordering.Order;
import com.daniel99j.ordering.OrderItem;
import com.daniel99j.ordering.OrderManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
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
                if(amount > 0) items.add(new OrderItem(Items.items.get(item.get("name").getAsString()), amount));
            }

            if(items.isEmpty()) throw new IllegalArgumentException("No items");

            float cost = 0;

            for (OrderItem item : items) {
                cost += item.quantity*item.item.price;
            }

            if(cost <= 0) throw new IllegalArgumentException("Too cheap");

            Order order = new Order(cost, items, user, UserLoader.getUser("Hugo"), object.get("deliveryLocation").getAsString());

            order.currentStatusExpiry = Instant.now().plus(Duration.ofMinutes(2));

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
