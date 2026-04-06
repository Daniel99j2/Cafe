package com.daniel99j.cafe.site;

import java.util.ArrayList;

public class CartDataElement extends ElementParser {
    private static final ArrayList<String> additions = new ArrayList<>();
    public CartDataElement() {
        super("addCartData");
    }

    @Override
    public String parseFile(String file) {
        additions.clear();
        file = super.parseFile(file);

        StringBuilder values = new StringBuilder();

        for (String addition : additions) {
            values.append(addition).append("\n");
        }
        file = file.replace("<cartData></cartData>", "<p id=\"_cartItems\" hidden=\"hidden\">"+values+"</p>");
        return file;
    }

    @Override
    public String parse(String data) {
        additions.add(data);
        return "";
    }
}
