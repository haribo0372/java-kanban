package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.handlers.util.Endpoint;
import managers.TaskManager;
import models.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class HistoryHttpHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        if (Objects.requireNonNull(endpoint) == Endpoint.GET_HISTORY) {
            handleGetPrioritized(exchange);
        } else {
            writeResponse(exchange, "Не найдено", 404);
        }
    }

    @Override
    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");
        if (path[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetPrioritized(HttpExchange exchange) {
        try {
            List<Task> tasks = taskManager.getHistory();
            writeResponse(exchange, gson.toJson(tasks), 200);
        } catch (IOException e) {
            System.out.println("Не удалось выдать историю просмотров задач");
        }
    }
}
