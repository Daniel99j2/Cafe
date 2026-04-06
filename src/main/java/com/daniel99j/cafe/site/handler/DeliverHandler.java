package com.daniel99j.cafe.site.handler;

import com.daniel99j.cafe.User;
import com.daniel99j.cafe.UserLoader;
import com.daniel99j.cafe.ordering.Order;
import com.daniel99j.cafe.ordering.OrderItem;
import com.daniel99j.cafe.ordering.OrderManager;
import com.daniel99j.cafe.site.SiteGenerator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class DeliverHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            UUID id = UUID.fromString(exchange.getRequestURI().toString().replace("/deliver?id=", ""));
            Order o = OrderManager.orders.stream().filter((order -> order.uuid.equals(id))).findFirst().orElse(null);
            if(o == null) {
                String response = SiteGenerator.not_found();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            } else {
                String response = SiteGenerator.customPages.get("deliver.html");

                StringBuilder deliveryItems = new StringBuilder("<div id=\"_deliveryItems\" style=\"margin: 20px;\">");

                deliveryItems.append("<p>Checklist:</p>");

                for (OrderItem item : o.items) {
                    String fixedId = item.item.name.replaceAll("\\s+", "_").replaceAll("\\W", "");

                    deliveryItems.append("<div class=\"delivery-item\" id=\"_deliveryItem_").append(fixedId).append("\" style=\"display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;\">");
                    deliveryItems.append("<span style=\"min-width: 300px;\">").append(item.quantity).append("x ").append(item.item.name).append("</span>");
                    deliveryItems.append("<input type=\"checkbox\" data-deliveryitem=\"true\" id=\"_deliveryItemCheckbox_")
                            .append(fixedId).append("\">");
                    deliveryItems.append("</div>");
                }

                deliveryItems.append("</div>");

                response = response.replace("<deliveryitems></deliveryitems>", deliveryItems.toString());
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } catch (Exception e) {
            String response = "Bad Request";
            exchange.sendResponseHeaders(500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
