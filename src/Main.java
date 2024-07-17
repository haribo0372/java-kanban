import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.*;

public class Main {
    private static final TaskManager taskManager = new InMemoryTaskManager();

    public static void main(String[] args) {
        Epic epic1 = new Epic("Уборка", "");
        Epic epic2 = new Epic("Спорт", "");

        SubTask subTask1 = new SubTask("Помыть посуду", "Посуду помыть", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Помыть пол", "Пол помыть", TaskStatus.NEW);
        SubTask subTask3 = new SubTask("Пропылесосить", "Пропылесосить", TaskStatus.NEW);

        subTask1.setCurrentEpic(epic1);
        subTask2.setCurrentEpic(epic1);
        subTask3.setCurrentEpic(epic1);

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewSubtask(subTask3);

        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        printHistory();

        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic1.getId());
        printHistory();

        taskManager.deleteEpic(epic1.getId());
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
        System.out.println("\n\t\t\tИстория просмотров задач (" + taskManager.getHistory().size() + ')');
        taskManager.getHistory().forEach(System.out::println);
    }
}
