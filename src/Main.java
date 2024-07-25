import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.*;

import java.io.File;

public class Main {
    private static final FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("example.txt"));

    public static void main(String[] args) {
        Task task = new Task("task_name_1", "task_description_1", TaskStatus.NEW);
        Epic epic = new Epic("epic_name_3", "epic_description_3");
        Epic epic2 = new Epic("epic_name_4", "epic_description_4");

        SubTask subTask = new SubTask("subtask_name_1", "subtask_description_1", TaskStatus.NEW);
        subTask.setCurrentEpic(epic);
        SubTask subTask2 = new SubTask("subtask_name_2", "subtask_description_2", TaskStatus.NEW);
        subTask2.setCurrentEpic(epic);
        SubTask subTask3 = new SubTask("subtask_name_3", "subtask_description_3", TaskStatus.NEW);
        subTask3.setCurrentEpic(epic2);

        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subTask);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask3);

//        Epic epic1 = new Epic("Уборка", "epic_1");
//        Epic epic2 = new Epic("Спорт", "epic_2");
//
//        SubTask subTask1 = new SubTask("Помыть посуду", "Посуду помыть", TaskStatus.NEW);
//        SubTask subTask2 = new SubTask("Помыть пол", "Пол помыть", TaskStatus.NEW);
//        SubTask subTask3 = new SubTask("Пропылесосить", "Пропылесосить", TaskStatus.NEW);
//
//        subTask1.setCurrentEpic(epic1);
//        subTask2.setCurrentEpic(epic1);
//        subTask3.setCurrentEpic(epic1);
//
//        taskManager.addNewEpic(epic1);
//        taskManager.addNewEpic(epic2);
//        taskManager.addNewSubtask(subTask1);
//        taskManager.addNewSubtask(subTask2);
//        taskManager.addNewSubtask(subTask3);
        printAllTasks();
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
