package com.daniel99j.site;

import com.daniel99j.User;
import com.daniel99j.UserLoader;
import com.daniel99j.ordering.Order;
import com.daniel99j.ordering.OrderItem;
import com.daniel99j.ordering.OrderManager;
import com.daniel99j.ordering.OrderStatus;
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
            User user = UserLoader.loginOrNull(exchange);
            if(user == null && false) {
                String response = SiteGenerator.login();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } if(o.deliverer != user && false) {
                String response = "Invalid Credentials";
                exchange.sendResponseHeaders(401, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = SiteGenerator.customPages.get("deliver.html");

                StringBuilder deliveryItems = new StringBuilder("<div id=\"_deliveryItems\">");
                for (OrderItem item : o.items) {
                    deliveryItems.append("<label><input data-deliveryitem=\"true\" type=\"checkbox\">");
                    deliveryItems.append(item.quantity);
                    deliveryItems.append("x ");
                    deliveryItems.append(item.item.name);
                    deliveryItems.append("</label>");
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
