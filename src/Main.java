import managers.FileBackedTaskManager;
import models.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static final FileBackedTaskManager taskManager;

    static {
        try {
            File file = File.createTempFile("example", "txt");
            taskManager = FileBackedTaskManager.loadFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String d = "PT5H";
        Task task = new Task("1", "1", TaskStatus.NEW, Duration.parse(d),
                LocalDateTime.parse("2000-01-01T01:01:01"));

        System.out.println(task.toStringCSV());
    }

    static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) System.out.println(task);

        System.out.println("Эпики:");
        for (Task task : taskManager.getEpics()) System.out.println(task);

        System.out.println("Подзадачи:");
        for (Task task : taskManager.getSubtasks()) System.out.println(task);
    }

    static void printHistory() {
        System.out.println("\n\t\t\tИстория просмотров задач (" + taskManager.getHistory().size() + ')');
        taskManager.getHistory().forEach(System.out::println);
    }
}
