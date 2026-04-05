package com.daniel99j;

import java.util.HashMap;
import java.util.Map;

public class Items {
    public static final Map<String, Item> items = new HashMap<String, Item>();
    public static final Item MILO = item(new Item("Milo", 2.50, "Stir it", 2, 1, Map.of("Scoops", Integer.class, "Heating", HeatStatus.class)));
    public static final Item JATZ = item(new Item("jatz", 1, "Stir it", 2, 5, Map.of("Topping", String.class)));
    public static final Item GOLD_BAR = item(new Item("1kg Gold Bar", 222000, "I dont even know.", -1, 99999999, Map.of("Topping", String.class)));
    public static final Item YUZU_DRINK = item(new Item("Yuzu drink", 3, "Just get it", 1, 1, Map.of("Topping", String.class)));

    public static Item item(Item item) {
        items.put(item.name, item);
        return item;
    }
}
