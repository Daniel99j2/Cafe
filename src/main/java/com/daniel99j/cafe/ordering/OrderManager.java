package com.daniel99j.cafe.ordering;

import com.google.gson.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OrderManager {
    public static final ArrayList<Order> orders = new ArrayList<Order>();

    public static void addOrder(Order order) {
        orders.add(order);
        order.setStatus(OrderStatus.SENDING);
    }

    public static void load() {
        try {
            Path path = Paths.get("orders.json");
            String data = Files.readString(path);

            JsonObject jsonData = JsonParser.parseString(data).getAsJsonObject();
            JsonArray ordersArray = jsonData.getAsJsonArray("orders");

            orders.clear();

            for (JsonElement orderElement : ordersArray) {
                JsonObject orderJson = orderElement.getAsJsonObject();
                Order order = new Order(orderJson);
                orders.add(order);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load orders", e);
        }
    }

    public static void save() {
        try {
            JsonObject data = new JsonObject();
            JsonArray list = new JsonArray();
            for (Order order : orders) {
                JsonObject orderJson = new JsonObject();
                JsonArray items = new JsonArray();
                for (OrderItem item : order.items) {
                    JsonObject itemJson = new JsonObject();
                    itemJson.addProperty("quantity", item.quantity);
                    itemJson.addProperty("item", item.item.name);
                    items.add(itemJson);
                }
                orderJson.add("items", items);
                orderJson.addProperty("status", order.getStatus().toString());
                orderJson.addProperty("orderer", order.orderer.name);
                orderJson.addProperty("deliverer", order.deliverer.name);
                orderJson.addProperty("deliveryLocation", order.deliveryLocation);
                orderJson.addProperty("failReason", order.failReason);
                orderJson.addProperty("uuid", order.uuid.toString());
                orderJson.addProperty("discordMessageId", order.discordMessageId);
                orderJson.addProperty("currentStatusExpiry", order.currentStatusExpiry == null ? null : order.currentStatusExpiry.toString());
                orderJson.addProperty("verificationCode", order.verificationCode);
                list.add(orderJson);
            }
            data.add("orders", list);

            Path path = Paths.get("orders.json");
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.write(path, new GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(data).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
