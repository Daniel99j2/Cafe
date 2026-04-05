package com.daniel99j;

import com.daniel99j.site.DeliverApiHandler;
import com.daniel99j.ordering.LoginHandler;
import com.daniel99j.site.OrderApiHandler;
import com.daniel99j.site.*;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {
        UserLoader.users.add(new User("Daniel",844486432076201995L, UUID.randomUUID()));
        UserLoader.users.add(new User("Hugo",1006877160625143809L, UUID.randomUUID()));

        new ItemsElement();
        new ItemElement();
        new CartDataElement();
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
        server.createContext("/deliver", new DeliverHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}