package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.Items;
import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.daniel99j.cafe.ordering.OrderItem;
import com.daniel99j.cafe.site.SiteGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class CartApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = UserLoader.login(exchange);
            ArrayList<OrderItem> items = new ArrayList<>();
            JsonObject object = JsonParser.parseString(new String(exchange.getRequestBody().readAllBytes())).getAsJsonObject();

            for (JsonElement cart : object.get("cart").getAsJsonArray()) {
                JsonObject item = cart.getAsJsonObject();
                int amount = item.get("quantity").getAsInt();
                if(amount > 0) items.add(new OrderItem(Items.items.get(item.get("name").getAsString()), amount));
            }

            float cost = 0;
            float time = 0;

            for (OrderItem item : items) {
                cost += item.item.priceSingle;
                cost += item.quantity*item.item.price;
                time += item.item.maxPrepareTimeMinsSingle;
                time += item.quantity*item.item.maxPrepareTimeMins;
            }

            JsonObject out = new JsonObject();
            out.addProperty("cost", cost);
            out.addProperty("estimatedTime", time);
            out.addProperty("tooExpensive", cost > user.balance);

            exchange.sendResponseHeaders(200, out.toString().getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(out.toString().getBytes());
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
