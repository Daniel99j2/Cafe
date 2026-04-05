package com.daniel99j.site;

import com.daniel99j.Items;

import java.util.concurrent.atomic.AtomicReference;

public class ItemsElement extends ElementParser {
    public ItemsElement() {
        super("items");
    }

    @Override
    public String parse(String data) {
        AtomicReference<String> additions = new AtomicReference<>("<div class=\"cart\" id=\"Cart\">");
        Items.items.forEach((name, item) -> {
            additions.set(additions + "<item>" + name + "</item>");
        });
        return additions.get()+"</div>";
    }
}
