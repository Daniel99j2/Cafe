package com.daniel99j.cafe.site;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SiteGenerator {
    public static Map<String, String> customPages = new HashMap<>();

    public static void load(HttpServer server) {
        try {
            Files.list(Paths.get("pages").toAbsolutePath()).forEach((p) -> {
                try {
                    if(!p.getFileName().toString().endsWith(".png")) {
                        GeneratedHandler handler = new GeneratedHandler(Files.readString(p));
                        if(!p.getFileName().toString().equals("deliver.html") && !p.getFileName().toString().equals("not_found.html")) server.createContext("/"+p.getFileName().toString().replace(".html", ""), handler);
                        else customPages.put(p.getFileName().toString(), new String(handler.page));

                        Path path = Path.of("generated/" + p.getFileName());
                        Files.createDirectories(path.getParent());
                        Files.deleteIfExists(path);
                        Files.createFile(path);
                        Files.write(path, handler.page);
                    } else {
                        GeneratedHandler handler = new GeneratedHandler(Files.readAllBytes(p), false);
                        server.createContext("/"+p.getFileName().toString(), handler);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class GeneratedHandler implements HttpHandler {
        public final byte[] page;

        GeneratedHandler(String page) {
            this(page.getBytes(), true);
        }

        GeneratedHandler(byte[] page, boolean fixup) {
            if(fixup) {
                String stringPage = new String(page);
                while (true) {
                    stringPage = stringPage.replace("%base%/", "http://localhost:8080/").replace("%base%", "http://localhost:8080/");
                    String old = stringPage;
                    for (ElementParser elementParser : ElementParser.elementParsers) {
                        stringPage = elementParser.parseFile(stringPage);
                    }
                    if (stringPage.equals(old)) break;
                }
                this.page = stringPage.getBytes();
            } else this.page = page;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Send HTTP 200 OK and specify response length

            exchange.sendResponseHeaders(200, page.length);

            // Write the response body
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(page);
            }
        }
    }

    public static String login() {
        return """
                <html>
                <body>
                <script src="http://localhost:8080/password.js"></script>
                <script type="text/javascript">
                    window.addEventListener("load", () => {
                       login();
                    });
                </script>
                </body>
                </html>
                """;
    }

    public static String not_found() {
        return customPages.get("not_found.html");
    }

    public static String redirect(String url) {
        return """
                <html>
                <body>
                <script type="text/javascript">
                    window.addEventListener("load", () => {
                        window.location.replace("url");
                    });
                </script>
                </body>
                </html>
                """.replace("url", url);
    }
}
