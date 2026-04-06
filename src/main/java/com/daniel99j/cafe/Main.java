package com.daniel99j.cafe;

import com.daniel99j.cafe.ordering.Order;
import com.daniel99j.cafe.ordering.OrderManager;
import com.daniel99j.cafe.site.handler.*;
import com.daniel99j.cafe.site.handler.LoginHandler;
import com.daniel99j.cafe.site.*;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);  // Create a Scanner object

        UserLoader.load();
        OrderManager.load();

        new ItemsElement();
        new ItemElement();
        new CartDataElement();
        new UserIconElement();
        new SiteTitleElement();
        new UsersElement();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

//        try {
//            //keytool -genkeypair -alias myserver -keyalg RSA -keystore keystore.jks -storepass password -keypass password -dname "CN=localhost"
//            char[] password = "password".toCharArray();
//            KeyStore ks = KeyStore.getInstance("JKS");
//            ks.load(new FileInputStream("keystore.jks"), password);
//
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//            kmf.init(ks, password);
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(kmf.getKeyManagers(), null, null);
//
//            server.setHttpsConfigurator(new HttpsConfigurator(sslContext));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        SiteGenerator.load(server);

        server.createContext("/api/purchase", new PurchaseApiHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/order", new OrderApiHandler());
        server.createContext("/api/deliver", new DeliverApiHandler());
        server.createContext("/api/account", new AccountApiHandler());
        server.createContext("/api/cart", new CartApiHandler());
        server.createContext("/deliver", new DeliverHandler());
        server.createContext("/", new RedirectHandler("http://localhost:8080/store"));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");

        long time = System.currentTimeMillis();
        int second = 0;
        new Thread(() -> {
            if(input.hasNext()) {
                String command = input.nextLine();
                if(command.contains("set-password ")) {
                    User user = UserLoader.getUser(command.replace("set-password ", "").substring(0, command.replace("set-password ", "").indexOf(" ")));
                    String newPassword = command.substring(command.indexOf(user.name)+user.name.length()+1);
                    //base64 as the site uses it to sanitise inputs
                    user.setPassword(UserLoader.hashPassword(user.name, Base64.getEncoder().encodeToString(newPassword.getBytes())));
                } else if(command.contains("exit")) {
                    UserLoader.save();
                    OrderManager.save();
                    System.exit(0);
                }
            }
        }).start();


        while (true) {
            try {
                Thread.sleep(1000); //it doesnt need to run insanely fast
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            second++;

            if (second >= 30) {
                second = 0;
                OrderManager.save();
                UserLoader.save();
            }

            Iterator<Order> i = OrderManager.orders.iterator();

            while(i.hasNext()) {
                Order order = i.next();
                order.tick();
            }
        }
    }
}