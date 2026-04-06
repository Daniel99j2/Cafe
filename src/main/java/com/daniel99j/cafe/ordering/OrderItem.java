package com.daniel99j.cafe.ordering;

import com.daniel99j.cafe.Item;

public class OrderItem {
    public final Item item;
    public final int quantity;

    public OrderItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}
