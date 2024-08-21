package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.handlers.util.Endpoint;
import managers.TaskManager;
import models.Epic;
import models.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHttpHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC_BY_ID:
                handleGetEpicById(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
            default:
                writeResponse(exchange, "Не найдено", 404);
        }
    }

    private void handleGetEpics(HttpExchange exchange) {
        try {
            List<Epic> epics = taskManager.getEpics();
            writeResponse(exchange, gson.toJson(epics), 200);
        } catch (IOException e) {
            System.out.println("Не удалось выдать список эпиков");
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getTaskIdFromPath(exchange);
        if (epicIdOpt.isPresent()) {
            int epicId = epicIdOpt.get();
            Optional<Epic> epicOpt = taskManager.getEpic(epicId);
            if (epicOpt.isPresent()) {
                Epic epic = epicOpt.get();
                writeResponse(exchange, gson.toJson(epic), 200);
            } else {
                writeResponse(exchange, String.format("Эпик с идентификатором %s не найдено", epicId), 404);
            }
        } else {
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
        }
    }

    private void handlePostEpic(HttpExchange exchange) {
        try {
            String rBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (rBody.isEmpty())
                writeResponse(exchange, "Тело запроса не должно быть пустым", 400);

            Epic epic = gson.fromJson(rBody, Epic.class);
            if (epic.getId() == null) {
                taskManager.addNewEpic(epic);
            } else {
                taskManager.updateEpic(epic);
            }
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (IOException e) {
            System.out.println("Что-то пошло не так\n\t" + e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskIdFromPath(exchange);
        if (taskIdOpt.isPresent()) {
            int taskId = taskIdOpt.get();
            taskManager.deleteEpic(taskId);
            writeResponse(exchange, "Эпик успешно удален", 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskIdFromPath(exchange);
        if (taskIdOpt.isPresent()) {
            int epicId = taskIdOpt.get();
            List<SubTask> subTasks = taskManager.getEpicSubtasks(epicId);
            if (subTasks == null)
                writeResponse(exchange, String.format("Эпика с идентификатором %s нет", epicId), 404);
            writeResponse(exchange, gson.toJson(subTasks), 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
        }
    }

    @Override
    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (!pathParts[1].equals("epics")) return Endpoint.UNKNOWN;

        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC_BY_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        }
        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPIC;
            }
        }
        if (pathParts.length == 4 && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }
}
