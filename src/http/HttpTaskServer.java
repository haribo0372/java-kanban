package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import http.handlers.util.type.adapters.*;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private Gson gson;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        try {
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                    .registerTypeAdapter(Epic.class, new EpicTypeAdapter())
                    .registerTypeAdapter(SubTask.class, new SubTaskTypeAdapter())
                    .create();

            this.httpServer = HttpServer.create();
            httpServer.createContext("/tasks", new TasksHttpHandler(taskManager, this.gson));
            httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager, this.gson));
            httpServer.createContext("/epics", new EpicsHttpHandler(taskManager, this.gson));
            httpServer.createContext("/history", new HistoryHttpHandler(taskManager, this.gson));
            httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager, this.gson));
        } catch (IOException e) {
            System.out.println("Не удалось определить HTTP-сервер");
        }
    }

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }

    public void start() {
        try {
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер\n\t" + e.getMessage());
        }
    }

    public void stop() {
        httpServer.stop(1);
    }

    public Gson getGson() {
        return gson;
    }
}
