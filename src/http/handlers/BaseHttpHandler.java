package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import http.handlers.util.Endpoint;
import managers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

abstract class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    protected Optional<Integer> getTaskIdFromPath(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected void writeResponse(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected abstract Endpoint getEndpoint(String requestPath, String requestMethod);
}
