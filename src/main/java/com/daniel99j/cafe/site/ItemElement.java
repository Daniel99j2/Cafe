package com.daniel99j.cafe.site;

import com.daniel99j.cafe.Item;
import com.daniel99j.cafe.Items;

public class ItemElement extends ElementParser {
    public ItemElement() {
        super("item");
    }

    @Override
    public String parse(String data) {
        Item item = Items.items.get(data);
        String name = "_cartItem_"+item.name;
        String input = name + "_Input";
        String out = "\n<div class=\"item\" id=\""+name+"\" data-cost="+item.price+">";
        String itemNameDisplay = item.parent == null ? item.name : "↳ " + item.name;
        out += "<p>"+itemNameDisplay+"</p>";
        out += "<button class=\"moreless\" type=\"button\" onmousedown=\"startHold('"+input+"', -1)\" onmouseup=\"stopHold('"+input+"')\" onmouseleave=\"stopHold('"+input+"')\">-</button>";
        out += "<input class=\"itemamount\" name=\""+item.name.toLowerCase().replace(" ", "_")+"\" type=\"number\" max=\"10\" min=\"0\" value=\"0\" id=\""+name+"_Input\">";
        out += "<button class=\"moreless\" type=\"button\" onmousedown=\"startHold('"+input+"', 1)\" onmouseup=\"stopHold('"+input+"')\" onmouseleave=\"stopHold('"+input+"')\">+</button>";
        out += "</div>";
        out += "<addCartData>"+item.name+"</addCartData>";
        return out;
    }
}
