package com.daniel99j.ordering;

import com.daniel99j.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Order {
    public final float cost;
    public final ArrayList<OrderItem> items;
    public OrderStatus status = OrderStatus.SENDING;
    public String failReason = "";
    public final String deliveryLocation;
    public final UUID uuid;
    public final User orderer;
    public final User deliverer;
    public Instant currentStatusExpiry;

    public Order(float cost, ArrayList<OrderItem> items, User orderer, User deliverer, String deliveryLocation) {
        this.cost = cost;
        this.items = items;
        this.orderer = orderer;
        this.deliverer = deliverer;
        this.uuid = UUID.randomUUID();
        this.deliveryLocation = deliveryLocation;
    }
}
