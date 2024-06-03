import managers.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Task task1 = new Task("Приготовить еду", "Сварить макароны и пожарить мясо", TaskStatus.NEW);
        Task task2 = new Task("Тренировака", "Пробежка, воркаут", TaskStatus.NEW);

        Epic epic1 = new Epic("Уборка", "На новый год приезжают гости, нужно убраться");
        epic1.addNewSubTask(new SubTask("Помыть посуду", "Посуду помыть", TaskStatus.NEW));
        epic1.addNewSubTask(new SubTask("Помыть пол", "Пол помыть", TaskStatus.NEW));
        epic1.addNewSubTask(new SubTask("", "", TaskStatus.NEW));

        Epic epic2 = new Epic("Фантазия покинула", "");
        epic2.addNewSubTask(new SubTask("some subtask", "some subtask", TaskStatus.NEW));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        System.out.println("\t\t\tСПИСОК ЗАДАЧ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n\t\t\tСТАТУСЫ ЗДАЧ");

        task1.changeStatus(TaskStatus.DONE);
        task2.changeStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Измененный статус задачи 1 : " + task1.getTaskStatus());
        System.out.println("Измененный статус задачи 2 : " + task2.getTaskStatus());

        System.out.println("Если все подзадачи NEW, models.Epic.task.status = " + epic1.getTaskStatus());

        ArrayList<SubTask> tasksOfEpic1 = epic1.getSubTasks();

        tasksOfEpic1.forEach(i -> i.changeStatus(TaskStatus.DONE));
        System.out.println("Если все подзадачи DONE, models.Epic.task.status = " + epic1.getTaskStatus());

        tasksOfEpic1.getFirst().changeStatus(TaskStatus.NEW);
        tasksOfEpic1.getLast().changeStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Если есть подзадачи DONE, IN_PROGRESS и NEW, то models.Epic.task.status = " + epic1.getTaskStatus());

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        System.out.println("\n\t\t\tСПИСОК ПОСЛЕ УДАЛЕНИЯ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
    }
}
