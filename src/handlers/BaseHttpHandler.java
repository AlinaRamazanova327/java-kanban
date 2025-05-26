package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendResponse(HttpExchange exchange, String content, int status) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendResponse(h, "Not found", 404);
    }

    protected void sendNotAcceptable(HttpExchange h) throws IOException {
        sendResponse(h, "Conflict with existing resources", 406);
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        sendResponse(h, "Bad Request", 400);
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        sendResponse(h, "Internal Server Error", 500);
    }

    protected void sendSuccessWithData(HttpExchange h, String content) throws IOException {
        sendResponse(h, content, 200);
    }

    protected void sendNoContent(HttpExchange h) throws IOException {
        h.sendResponseHeaders(204, -1);
        h.close();
    }

    protected void sendSuccessWithoutData(HttpExchange h) throws IOException {
        sendResponse(h, "Success", 201);
    }

    protected Endpoints getEndpoints(String requestPath, String requestMethod) {
        String[] parts = requestPath.split("/");
        if (parts.length < 2) return null;
        String resourceName = parts[1];
        return switch (resourceName) {
            case "tasks", "epics", "subtasks" -> switch (requestMethod) {
                case "GET" -> parts.length == 2 ? Endpoints.GET : Endpoints.GET_BY_ID;
                case "POST" -> Endpoints.POST;
                case "DELETE" -> Endpoints.DELETE;
                default -> null;
            };
            default -> null;
        };
    }

    protected int getId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }

    public abstract void handle(HttpExchange exchange) throws IOException;
}