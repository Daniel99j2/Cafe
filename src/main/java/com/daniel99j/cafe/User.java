package com.daniel99j.cafe;

import java.util.UUID;

public class User {
    public final String name;
    public final long discordId;
    private String password;
    public final UUID uuid;
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

    public String getPassword() {
        return password;
    }
}
