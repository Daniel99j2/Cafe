package com.daniel99j.cafe;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Items {
    public static final LinkedHashMap<String, Item> items = new LinkedHashMap<>();
    public static final Item MILO = item(new Item("Milo", 2.50, "Stir it", 2, 1, 0, 0, 0.1));
    public static final Item HOT_MILO = item(new Item("Hot Milo", "Stir it then heat", MILO));
    public static final Item JATZ = item(new Item("Jatz", 0.1, "Stir it", 0, 0.1, 0.5, 1, 0.5));
    public static final Item GOLD_BAR = item(new Item("1kg Gold Bar", 222000, "I dont even know.", -1, 99999999, 999999, 99999, 99999));
    public static final Item YUZU_DRINK = item(new Item("Yuzu drink", 3, "Just get it", 1, 1, 0, 0, 0));
    public static final Item CHOCOLATE_MILK = item(new Item("Chocolate Milk", 2, "Just get it", 1, 1, 0, 0, 0));
    public static final Item BREAD = item(new Item("Chocolate Milk", 2, "Just get it", 1, 1, 0, 0,0));

    public static Item item(Item item) {
        items.put(item.name, item);
        return item;
    }

    public static Item getItem(String itemName) {
        return items.get(itemName);
    }
}
