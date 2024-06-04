import managers.TaskManager;
import models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        // Создайте две задачи
        Task task1 = new Task("Приготовить еду", "Сварить макароны и пожарить мясо", TaskStatus.NEW);
        Task task2 = new Task("Тренировака", "Пробежка, воркаут", TaskStatus.NEW);

        // Создайте эпик с двумя подзадачами
        Epic epic1 = new Epic("Уборка", "На новый год приезжают гости, нужно убраться");
        SubTask subTask1 = new SubTask("Помыть посуду", "Посуду помыть", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Помыть пол", "Пол помыть", TaskStatus.NEW);
        subTask1.setCurrentEpic(epic1);
        subTask2.setCurrentEpic(epic1);

        // Создайте эпик с одной подзадачей
        Epic epic2 = new Epic("Фантазия покинула", "");
        SubTask subTask3 = new SubTask("some subtask", "some subtask", TaskStatus.NEW);
        subTask3.setCurrentEpic(epic2);

        // Добавляем все задачи на вооружение менеджеру
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewSubtask(subTask3);

        // Распечатайте списки эпиков, задач и подзадач
        System.out.println("\t\t\tСПИСОК ВСЕХ ЗАДАЧ");
        printAllTasks();

        System.out.println("\n\t\t\tПроверка механизма статусов задач у класса Task");

        // Task
        TaskStatus taskStatus1 = task1.getTaskStatus();
        TaskStatus taskStatus2 = task2.getTaskStatus();
        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        System.out.printf("Измененный статус Task1 с %s на %s\n",
                taskStatus1,
                taskManager.getTask(task1.getId()).getTaskStatus());
        System.out.printf("Измененный статус Task2 с %s на %s\n",
                taskStatus2,
                taskManager.getTask(task2.getId()).getTaskStatus());

        // Epic
        System.out.println("\n\t\t\tПроверка механизма статусов задач у класса Epic");

        taskManager.getEpicSubtasks(epic1.getId()).forEach(i -> {
            i.setStatus(TaskStatus.NEW);
            taskManager.updateSubtask(i);
        });
        System.out.println("Если у эпика все подзадачи имеют статус NEW, Epic.taskStatus = " +
                taskManager.getEpic(epic1.getId()).getTaskStatus());

        taskManager.getEpicSubtasks(epic1.getId()).forEach(i -> {
            i.setStatus(TaskStatus.DONE);
            taskManager.updateSubtask(i);
        });
        System.out.println("Если у эпика все подзадачи имеют статус DONE, Epic.taskStatus = " +
                taskManager.getEpic(epic1.getId()).getTaskStatus());

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);
        String otherCase = "В других случаях Epic.taskStatus = " +
                taskManager.getEpic(epic1.getId()).getTaskStatus();

        taskManager.deleteSubtask(subTask1.getId());
        taskManager.deleteSubtask(subTask2.getId());

        taskManager.updateEpic(epic1);
        System.out.println("Если у эпика нет подзадач, Epic.taskStatus = " +
                taskManager.getEpic(epic1.getId()).getTaskStatus());

        System.out.println(otherCase);

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        System.out.println("\n\t\t\tСПИСОК ПОСЛЕ УДАЛЕНИЯ");
        printAllTasks();

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();
    }

     static void printAllTasks() {
        List<Task> allTasks = taskManager.getTasks();
        allTasks.addAll(taskManager.getEpics());
        allTasks.addAll(taskManager.getSubtasks());
        for (Object task : allTasks) {
            System.out.println(task);
        }
    }
}
