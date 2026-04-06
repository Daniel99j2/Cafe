package com.daniel99j.cafe.ordering;

import com.daniel99j.cafe.*;
import com.daniel99j.djutil.NumberUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Order {
    public final float cost;
    public final ArrayList<OrderItem> items;
    private OrderStatus status = OrderStatus.SENDING;
    public String failReason = "";
    public final String deliveryLocation;
    public final UUID uuid;
    public final User orderer;
    public final User deliverer;
    public Instant currentStatusExpiry = null;
    public long discordMessageId;
    public final String verificationCode;

    public Order(ArrayList<OrderItem> items, User orderer, User deliverer, String deliveryLocation) {
        float cost = 0;

        for (OrderItem item : items) {
            cost += item.quantity*item.item.price;
        }

        if(cost <= 0) throw new IllegalArgumentException("Too cheap");

        this.cost = cost;
        this.items = items;
        this.orderer = orderer;
        this.deliverer = deliverer;
        this.uuid = UUID.randomUUID();
        this.deliveryLocation = deliveryLocation;
        StringBuilder verify = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            verify.append(NumberUtils.getRandomInt(0, 9));
        }
        this.verificationCode = verify.toString();
    }

    public Order(JsonObject orderJson) {
        this.uuid = UUID.fromString(orderJson.get("uuid").getAsString());
        this.deliveryLocation = orderJson.get("deliveryLocation").getAsString();
        this.failReason = orderJson.get("failReason").getAsString();
        String ordererName = orderJson.get("orderer").getAsString();
        String delivererName = orderJson.get("deliverer").getAsString();
        this.discordMessageId = orderJson.get("discordMessageId").getAsLong();
        if(!orderJson.get("currentStatusExpiry").isJsonNull()) this.currentStatusExpiry = Instant.parse(orderJson.get("currentStatusExpiry").getAsString());
        this.verificationCode = orderJson.get("verificationCode").getAsString();

        JsonArray itemsArray = orderJson.getAsJsonArray("items");
        this.items = new ArrayList<>();
        for (JsonElement itemElement : itemsArray) {
            JsonObject itemJson = itemElement.getAsJsonObject();
            int quantity = itemJson.get("quantity").getAsInt();
            String itemName = itemJson.get("item").getAsString();
            // Assuming you have a method to find the Item object by name
            Item item = Items.getItem(itemName);
            if(item == null) {
                System.err.println("Item " + itemName + " not found");
            } else this.items.add(new OrderItem(item, quantity));
        }

        float cost = 0;

        for (OrderItem item : items) {
            cost += item.quantity*item.item.price;
        }

        if(cost <= 0) throw new IllegalArgumentException("Too cheap");

        this.cost = cost;

        this.orderer = UserLoader.getUser(ordererName);
        this.deliverer = UserLoader.getUser(delivererName);

        //dont setStatus as it breaks messages
        this.status = OrderStatus.valueOf(orderJson.get("status").getAsString());
    }

    public void setStatus(OrderStatus status) {
        this.status = status;

        StringBuilder stringifiedItems = new StringBuilder();
        for (OrderItem item : this.items) {
            stringifiedItems.append(item.quantity);
            stringifiedItems.append("x ");
            stringifiedItems.append(item.item.name);
            stringifiedItems.append("\n");
        }

        if(status == OrderStatus.SENDING) {
            this.currentStatusExpiry = Instant.now().plus(Duration.ofMinutes(2));
            long discordTimestamp = this.currentStatusExpiry.getEpochSecond();
            this.discordMessageId = WebhookSender.sendMessage("""
                <@discordId>
                Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/vieworder?id=uuid)
                Current status: SENDING
                This expires <t:time:R>"""
                    .replace("uuid", this.uuid.toString())
                    .replace("orderer", this.orderer.name)
                    .replace("discordId", String.valueOf(this.deliverer.discordId))
                    .replace("time", String.valueOf(discordTimestamp))
                    .replace("place", this.deliveryLocation)
                    .replace("items", stringifiedItems.toString()));
        } else if(status == OrderStatus.PREPARING) {
            float time = 0;
            for (OrderItem item : this.items) {
                time += item.quantity*item.item.maxPrepareTimeMins;
            }
            this.currentStatusExpiry = Instant.now().plus(Duration.ofSeconds((long) (time*60))).plus(Duration.ofMinutes(1));
            long discordTimestamp = this.currentStatusExpiry.getEpochSecond();
            WebhookSender.editMessage("""
                <@discordId>
                Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/vieworder?id=uuid)
                Current status: PREPARING
                This expires <t:time:R>"""
                    .replace("uuid", this.uuid.toString())
                    .replace("orderer", this.orderer.name)
                    .replace("discordId", String.valueOf(this.deliverer.discordId))
                    .replace("time", String.valueOf(discordTimestamp))
                    .replace("place", this.deliveryLocation)
                    .replace("items", stringifiedItems.toString()), this.discordMessageId);
        } else if(status == OrderStatus.DELIVERING) {
            this.currentStatusExpiry = Instant.now().plus(Duration.ofMinutes(2));
            long discordTimestamp = this.currentStatusExpiry.getEpochSecond();
            WebhookSender.editMessage("""
                <@discordId>
                Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/vieworder?id=uuid)
                Current status: DELIVERING
                This expires <t:time:R>"""
                    .replace("uuid", this.uuid.toString())
                    .replace("orderer", this.orderer.name)
                    .replace("discordId", String.valueOf(this.deliverer.discordId))
                    .replace("time", String.valueOf(discordTimestamp))
                    .replace("place", this.deliveryLocation)
                    .replace("items", stringifiedItems.toString()), this.discordMessageId);
        } else if(status == OrderStatus.SOLD) {
            this.currentStatusExpiry = null;
            WebhookSender.editMessage("""
                <@discordId>
                Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/vieworder?id=uuid)
                Current status: SOLD
                """
                    .replace("uuid", this.uuid.toString())
                    .replace("orderer", this.orderer.name)
                    .replace("discordId", String.valueOf(this.deliverer.discordId))
                    .replace("place", this.deliveryLocation)
                    .replace("items", stringifiedItems.toString()), this.discordMessageId);
        } else if(status == OrderStatus.FAILED) {
            this.currentStatusExpiry = null;
            WebhookSender.editMessage("""
                <@discordId>
                ~~Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/vieworder?id=uuid)~~
                Order was cancelled by deliverer with reason "failReason\""""
                    .replace("uuid", this.uuid.toString())
                    .replace("orderer", this.orderer.name)
                    .replace("discordId", String.valueOf(this.deliverer.discordId))
                    .replace("place", this.deliveryLocation)
                    .replace("failReason", this.failReason)
                    .replace("items", stringifiedItems.toString()), this.discordMessageId);

            WebhookSender.deleteMessage(WebhookSender.sendMessage("<@discordId> Your order has failed!".replace("discordId", String.valueOf(this.orderer.discordId))));
        } else if(status == OrderStatus.CANCELLED) {
            this.currentStatusExpiry = null;
            WebhookSender.editMessage("""
                ~~<@discordId>
                Order requested by orderer.
                Location: "place"
                Cart:
                ```
                items
                ```
                [View order](http://localhost:8080/vieworder?id=uuid)~~
                Order has been cancelled by orderer.
                """
                    .replace("uuid", this.uuid.toString())
                    .replace("orderer", this.orderer.name)
                    .replace("discordId", String.valueOf(this.deliverer.discordId))
                    .replace("place", this.deliveryLocation)
                    .replace("items", stringifiedItems.toString()), this.discordMessageId);
        }
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void tick() {
        if(this.currentStatusExpiry != null && Instant.now().isAfter(this.currentStatusExpiry)) {
            this.failReason = "The deliverer did not complete the order in time";
            this.setStatus(OrderStatus.FAILED);
        }
    }
}
