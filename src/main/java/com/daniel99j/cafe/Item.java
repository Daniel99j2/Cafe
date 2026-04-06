package com.daniel99j.cafe;

public class Item {
    public final String name;
    public final float price;
    public final String instructions;
    public final float prepareValue;
    public final float maxPrepareTimeMins;
    public final float maxPrepareTimeMinsSingle;
    public final Item parent;
    public final float priceSingle;
    public final float prepareValueSingle;

    public Item(String name, double price, String instructions, double prepareValue, double maxPrepareTimeMins, double priceSingle, double prepareValueSingle, double maxPrepareTimeMinsSingle) {
        this(name, price, instructions, prepareValue, maxPrepareTimeMins, maxPrepareTimeMinsSingle, priceSingle, prepareValueSingle, null);
    }

    public Item(String name, String instructions, Item parent) {
        this(name, parent.price, instructions, parent.prepareValue, parent.maxPrepareTimeMins, parent.maxPrepareTimeMinsSingle, parent.priceSingle, parent.prepareValueSingle, parent);
    }

    public Item(String name, double price, String instructions, double prepareValue, double maxPrepareTimeMins, double maxPrepareTimeMinsSingle, double priceSingle, double prepareValueSingle, Item parent) {
        this.name = name;
        this.price = (float) price;
        this.instructions = instructions;
        this.prepareValue = (float) prepareValue;
        this.maxPrepareTimeMins = (float) maxPrepareTimeMins;
        this.maxPrepareTimeMinsSingle = (float) maxPrepareTimeMinsSingle;
        this.parent = parent;
        this.priceSingle = (float) priceSingle;
        this.prepareValueSingle = (float) prepareValueSingle;
    }
}
