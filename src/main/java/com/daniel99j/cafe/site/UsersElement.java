package com.daniel99j.cafe.site;

import com.daniel99j.cafe.Item;
import com.daniel99j.cafe.Items;
import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;

public class UsersElement extends ElementParser {
    public UsersElement() {
        super("users");
    }

    @Override
    public String parse(String data) {
        StringBuilder out = new StringBuilder("""
                <label for="users">Deliverer:</label>
                <select id="users" name="Users" required>
                    <option value="null" disabled selected>--Select a delivery option--</option>
                """);

        for (User user : UserLoader.userList.users) {
            out.append("<option value=\"");
            out.append(user.name);
            out.append("\">");
            out.append(user.name);
            out.append("</option>\n");
        }

        out.append("</select>");

        return out.toString();
    }
}
