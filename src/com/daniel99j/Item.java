package com.daniel99j;

import java.util.Map;

public class Item {
    public final String name;
    public final float price;
    public final String instructions;
    public final float value;
    public final float maxPrepareTime;
    public final Map<String, Class<?>> options;

    public Item(String name, double price, String instructions, double value, double maxPrepareTime, Map<String, Class<?>> options) {
        this.name = name;
        this.price = (float) price;
        this.instructions = instructions;
        this.value = (float) value;
        this.maxPrepareTime = (float) maxPrepareTime;
        this.options = options;
    }
}
