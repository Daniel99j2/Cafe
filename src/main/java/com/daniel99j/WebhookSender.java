package com.daniel99j;

public class WebhookSender {
    public static void sendMessage(String message) {
        try {
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1489959259474235452/meBVlpGG8Kz7TsX2-5OtmDHWyARk0XNo7yNtxzOKzu6FCh7QJhLC8AgRkwXgjw-H5B9F");
            webhook.setContent(message.replace("\n", "\\n").replace("\"", "\\\""));
            webhook.setAvatarUrl("https://emojiisland.com/cdn/shop/products/23_grande.png?v=1571606116");
            webhook.setUsername("Mr Nom Nom");
            webhook.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
