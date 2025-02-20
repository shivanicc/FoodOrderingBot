public class DummyHttpServer {
    public static void start() throws IOException {
        // Cloud Run sets the PORT environment variable, default to 8080 if not set.
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "Bot is running";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        });
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("HTTP server started on port " + port);
    }
}
