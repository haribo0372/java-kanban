package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.handlers.util.Endpoint;
import managers.TaskManager;
import models.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {


    public TasksHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetTaskById(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            default:
                writeResponse(exchange, "Не найдено", 404);
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskIdFromPath(exchange);
        if (taskIdOpt.isPresent()) {
            int taskId = taskIdOpt.get();
            Optional<Task> taskOpt = taskManager.getTask(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                writeResponse(exchange, gson.toJson(task), 200);
            } else {
                writeResponse(exchange, "Задачи с идентификатором %s не найдено", 404);
            }
        } else {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }

    private void handleGetTasks(HttpExchange exchange) {
        List<Task> tasks = taskManager.getTasks();
        try {
            String taskListJson = gson.toJson(tasks);
            writeResponse(exchange, taskListJson, 200);
        } catch (IOException ignored) {

        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskIdFromPath(exchange);
        if (taskIdOpt.isPresent()) {
            int taskId = taskIdOpt.get();
            taskManager.deleteTask(taskId);
            writeResponse(exchange, "Задача успешно удалена", 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }

    private void handlePostTask(HttpExchange exchange) {
        try {
            String rBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(rBody, Task.class);
            if (task.getId() == null) {
                int id = taskManager.addNewTask(task);
                if (id < 0) {
                    writeResponse(exchange, "Задача пересекается с уже существующими во времени", 406);
                    return;
                }
            } else {
                taskManager.updateTask(task);
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

        if (!pathParts[1].equals("tasks")) return Endpoint.UNKNOWN;

        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK_BY_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        }
        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }


}
