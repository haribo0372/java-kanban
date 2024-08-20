package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.handlers.util.Endpoint;
import managers.TaskManager;
import models.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHttpHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK_BY_ID:
                handleGetSubtaskById(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
            default:
                writeResponse(exchange, "Не найдено", 404);
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskIdFromPath(exchange);
        if (taskIdOpt.isPresent()) {
            int taskId = taskIdOpt.get();
            Optional<SubTask> subtaskOpt = taskManager.getSubtask(taskId);
            if (subtaskOpt.isPresent()) {
                SubTask subTask = subtaskOpt.get();
                writeResponse(exchange, gson.toJson(subTask), 200);
            } else {
                writeResponse(exchange, String.format("Подзадачи с идентификатором %s не найдено", taskId), 404);
            }
        } else {
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) {
        try {
            List<SubTask> subTasks = taskManager.getSubtasks();
            writeResponse(exchange, gson.toJson(subTasks), 200);
        } catch (IOException e) {
            System.out.println("Не удалось выдать список подзадач");
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskIdFromPath(exchange);
        if (taskIdOpt.isPresent()) {
            int taskId = taskIdOpt.get();
            taskManager.deleteSubtask(taskId);
            writeResponse(exchange, "Подзадача успешно удалена", 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) {
        try {
            String rBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            SubTask subtask = gson.fromJson(rBody, SubTask.class);
            if (subtask.getId() == null) {
                int id = taskManager.addNewSubtask(subtask);
                if (id < 0) {
                    writeResponse(exchange, "Подзадача пересекается с уже существующими во времени", 406);
                    return;
                }
            } else {
                taskManager.updateSubtask(subtask);
            }
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (IOException e) {
            System.out.println("Что-то пошло не так\n\t" + e.getMessage());
        }
    }

    @Override
    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (!pathParts[1].equals("subtasks")) return Endpoint.UNKNOWN;

        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK_BY_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
        }
        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASK;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
