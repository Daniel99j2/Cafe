package com.daniel99j;

import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;

public class UserLoader {
    public static final ArrayList<User> users = new ArrayList<>();

    public static User getUser(String name) {
        return users.stream().filter(user -> user.name.equals(name)).findFirst().orElse(null);
    }

    public static User loginOrNull(HttpExchange exchange) {
        try {
            return login(exchange);
        } catch (Exception e) {
            return null;
        }
    }

    public static User login(HttpExchange exchange) throws IllegalAccessException {
        return login(getCookieValue(exchange.getRequestHeaders().getFirst("Cookie"), "user"), getCookieValue(exchange.getRequestHeaders().getFirst("Cookie"), "password"));
    }

    public static User login(String username, String password) throws IllegalAccessException {
        User user = getUser(username);
        if(user == null) throw new IllegalAccessException("User not found");
        if(!user.hasPasswordSetup()) {
            user.setPassword(hashPassword(password));
        };
        if(!user.checkPassword(hashPassword(password))) throw new IllegalAccessException("Wrong password");
        return user;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

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

    public static String getCookieValue(String cookieHeader, String key) {
        if (cookieHeader == null) throw new IllegalStateException("Cookie not found");

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1].replace("\"", "");
            }
        }
        throw new IllegalStateException("Cookie not found");
    }
}
