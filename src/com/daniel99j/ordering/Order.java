package com.daniel99j.ordering;

import com.daniel99j.User;

import java.util.ArrayList;
import java.util.UUID;

public class Order {
    public final float cost;
    public final ArrayList<OrderItem> items;
    public OrderStatus status = OrderStatus.SENDING;
    public final UUID uuid;
    public final User orderer;
    public final User deliverer;

    public Order(float cost, ArrayList<OrderItem> items, User orderer, User deliverer) {
        this.cost = cost;
        this.items = items;
        this.orderer = orderer;
        this.deliverer = deliverer;
        this.uuid = UUID.randomUUID();
    }
}
