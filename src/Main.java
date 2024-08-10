import managers.FileBackedTaskManager;
import managers.InMemoryPrioritizedTasks;
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
        InMemoryPrioritizedTasks prioritizedTasks = new InMemoryPrioritizedTasks();

        LocalDateTime[] dateTimes = new LocalDateTime[]{
                LocalDateTime.of(2001, 1, 1, 1, 1, 1),
                LocalDateTime.of(2002, 2, 2, 2, 2, 2),
                LocalDateTime.of(2002, 2, 2, 3, 3, 3)};

        final Epic epic = new Epic("e_1", "1");
        final SubTask subTask = new SubTask("s_1", "1", TaskStatus.NEW);

        subTask.setStartTime(dateTimes[0]);
        subTask.setDuration(Duration.ofDays(3));

        epic.setId(1);
        subTask.setId(2);

        epic.addNewSubTask(subTask);
        System.out.println(subTask.getCurrentEpic());
        prioritizedTasks.addEpic(epic);
        prioritizedTasks.addSubtask(subTask);

        System.out.println(prioritizedTasks.getPrioritizedTasks());
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
