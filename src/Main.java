import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final TaskManager taskManager = new InMemoryTaskManager();

    public static void main(String[] args) {
        Task task1 = new Task("Приготовить еду", "Сварить макароны и пожарить мясо", TaskStatus.NEW);
        Task task2 = new Task("Тренировака", "Пробежка, воркаут", TaskStatus.NEW);

        Epic epic1 = new Epic("Уборка", "На новый год приезжают гости, нужно убраться");
        Epic epic2 = new Epic("Фантазия покинула", "");

        SubTask subTask1 = new SubTask("Помыть посуду", "Посуду помыть", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Помыть пол", "Пол помыть", TaskStatus.NEW);
        SubTask subTask3 = new SubTask("some subtask", "some subtask", TaskStatus.NEW);

        subTask1.setCurrentEpic(epic1);
        subTask2.setCurrentEpic(epic1);
        subTask3.setCurrentEpic(epic2);

        // Добавляем все задачи на вооружение менеджеру
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewSubtask(subTask3);

        printAllTasks();

//        Вызывается Задача
        taskManager.getTask(task1.getId());
//        Вызывается Эпик
        taskManager.getEpic(epic1.getId());
//        Вызывается Подзадача
        taskManager.getSubtask(subTask1.getId());

        printHistory();

//        Прочитаем 10 задач, чтобы проверить ограничение истории
        IntStream.range(0, 10).forEach(i -> taskManager.getTask(1));
//        В истории должно быть 10 Task{id=1, ...}
        printHistory();
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
        System.out.println("\n\t\t\tИстория просмотров задач");
        for (Task task : taskManager.getHistory()) System.out.println(task);
    }
}
