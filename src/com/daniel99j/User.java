package com.daniel99j;

import com.daniel99j.ordering.Order;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    public final String name;
    public final long discordId;
    private String password;
    public final UUID uuid;
    public final ArrayList<Order> orders = new ArrayList<>();
    public float balance = 0;

    public User(String name, long discordId, UUID uuid) {
        this.name = name;
        this.discordId = discordId;
        this.uuid = uuid;
    }

    public boolean hasPasswordSetup() {
        return password != null && !password.isEmpty();
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void setPassword(String s) {
        this.password = s;
    }
}
