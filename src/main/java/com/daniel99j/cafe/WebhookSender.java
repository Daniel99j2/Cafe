package com.daniel99j.cafe;

import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class WebhookSender {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1489959259474235452/meBVlpGG8Kz7TsX2-5OtmDHWyARk0XNo7yNtxzOKzu6FCh7QJhLC8AgRkwXgjw-H5B9F";

    public static long sendMessage(String message) {
        try {
            message = message.replace("\n", "\\n").replace("\"", "\\\"");

            String jsonPayload = """
                {
                    "tts": false,
                    "avatar_url": "https://emojiisland.com/cdn/shop/products/23_grande.png?v=1571606116",
                    "content": "%message%",
                    "username": "Mr Nom Nom"
                }
                """.replace("%message%", message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(WEBHOOK_URL + "?wait=true"))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Java-DiscordWebhook-BY-Gelox_")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return JsonParser.parseString(response.body())
                    .getAsJsonObject()
                    .get("id")
                    .getAsLong();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void editMessage(String message, long id) {
        try {
            message = message.replace("\n", "\\n").replace("\"", "\\\"");

            String jsonPayload = """
                {
                    "content": "%message%"
                }
                """.replace("%message%", message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(WEBHOOK_URL + "/messages/" + id))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Java-DiscordWebhook-BY-Gelox_")
                    .method("PATCH", BodyPublishers.ofString(jsonPayload))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void deleteMessage(long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(WEBHOOK_URL + "/messages/" + id))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Java-DiscordWebhook-BY-Gelox_")
                    .method("DELETE", BodyPublishers.noBody())
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}