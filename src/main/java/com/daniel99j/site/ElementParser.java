package com.daniel99j.site;

import java.util.ArrayList;

public class ElementParser {
    public final String elementName;
    public static final ArrayList<ElementParser> elementParsers = new ArrayList<ElementParser>();

    public ElementParser(String elementName) {
        this.elementName = elementName;
        elementParsers.add(this);
    }

    public String parseFile(String file) {
        while (true) {
            String startName = "<"+elementName+">";
            String endName = "</"+elementName+">";
            if(!file.contains(startName)) break;
            int start = file.indexOf(startName);
            int end = file.indexOf(endName)+endName.length();
            String between = file.substring(start+startName.length(), end-endName.length());

            file = file.replace(startName+between+endName, parse(between));
        }
        return file;
    }

    public String parse(String data) {
        return "";
    }
}
