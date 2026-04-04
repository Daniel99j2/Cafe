package com.daniel99j.site;

import com.daniel99j.Item;
import com.daniel99j.Items;

import java.util.concurrent.atomic.AtomicReference;

public class ItemElement extends ElementParser {
    public ItemElement() {
        super("item");
    }

    @Override
    public String parse(String data) {
        Item item = Items.items.get(data);
        String name = "_cartItem_"+item.name;
        String out = "<div class=\"item\" id=\""+name+"\" data-cost="+item.price+">";
        out += "<p>"+item.name+"</p>";
        out += "<input name=\"test\" type=\"number\" max=\"10\" min=\"0\" value=\"0\" id=\""+name+"_Input\">";
        out += "</div>";
        out += "<addCartData>"+item.name+"</addCartData>";
        return out;
    }
}
