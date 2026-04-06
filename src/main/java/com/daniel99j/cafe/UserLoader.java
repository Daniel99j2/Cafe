package com.daniel99j.cafe;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class UserLoader {
    public static UserList userList = new UserList();

    public static User getUser(String name) {
        return userList.users.stream().filter(user -> user.name.equals(name)).findFirst().orElse(null);
    }

    public static User loginOrNull(HttpExchange exchange) {
        try {
            return login(exchange);
        } catch (Exception e) {
            return null;
        }
    }

    public static User login(HttpExchange exchange) throws IllegalAccessException {
        return loginToken(getCookieValue(exchange.getRequestHeaders().getFirst("Cookie"), "Username"), getCookieValue(exchange.getRequestHeaders().getFirst("Cookie"), "Token"));
    }

    public static User loginToken(String username, String token) throws IllegalAccessException {
        User user = getUser(username);
        if(user == null) throw new IllegalAccessException("User not found");
        if(!user.hasPasswordSetup()) {
            System.out.println("User " + user.name + " has no password setup. Use set-password {user} {password}");
        };
        if(!user.checkPassword(token)) throw new IllegalAccessException("Wrong password");
        return user;
    }

    public static User login(String username, String password) throws IllegalAccessException {
        User user = getUser(username);
        if(user == null) throw new IllegalAccessException("User not found");
        if(!user.hasPasswordSetup()) {
            System.out.println("User " + user.name + " has no password setup. Use set-password {user} {password}");
        };
        if(!user.checkPassword(hashPassword(username, password))) throw new IllegalAccessException("Wrong password");
        return user;
    }

    public static String hashPassword(String extra, String password) {
        String combined = extra + password;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCookieValue(String cookieHeader, String key) throws IllegalAccessException {
        if (cookieHeader == null) throw new IllegalAccessException("Cookie header not found");

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1].replace("\"", "");
            }
        }
        throw new IllegalAccessException("Cookie not found");
    }

    public static void load() {
        try {
            Path path = Paths.get("users.json");
            String data = Files.readString(path);
            userList = new GsonBuilder().create().fromJson(data, UserList.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void save() {
        try {
            Path path = Paths.get("users.json");
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.write(path, new GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(userList).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
