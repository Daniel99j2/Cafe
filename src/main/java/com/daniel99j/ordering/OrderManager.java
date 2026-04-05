package com.daniel99j.ordering;

import com.daniel99j.WebhookSender;

import java.util.ArrayList;

public class OrderManager {
    public static final ArrayList<Order> orders = new ArrayList<Order>();

    public static void addOrder(Order order) {
        orders.add(order);
        StringBuilder items = new StringBuilder();
        for (OrderItem item : order.items) {
            items.append(item.quantity);
            items.append("x ");
            items.append(item.item.name);
            items.append("\n");
        }
        long discordTimestamp = order.currentStatusExpiry.getEpochSecond();
        WebhookSender.sendMessage("""
                <@discordId>
                Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/deliver?id=uuid)
                This expires <t:time:R>"""
                .replace("uuid", order.uuid.toString())
                .replace("orderer", order.orderer.name)
                .replace("discordId", String.valueOf(order.deliverer.discordId))
                .replace("time", String.valueOf(discordTimestamp))
                .replace("place", order.deliveryLocation)
                .replace("items", items.toString()));
    }
}
