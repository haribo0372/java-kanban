import java.util.ArrayList;

public class Main {
    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Task task1 = new Task("Приготовить еду", "Сварить макароны и пожарить мясо", TaskStatus.NEW);
        Task task2 = new Task("Тренировака", "Пробежка, воркаут", TaskStatus.NEW);

        Epic epic1 = new Epic("Уборка", "На новый год приезжают гости, нужно убраться");
        epic1.addNewSubTask(new SubTask("Помыть посуду", "Посуду помыть", TaskStatus.NEW));
        epic1.addNewSubTask(new SubTask("Помыть пол", "Пол помыть", TaskStatus.NEW));

        Epic epic2 = new Epic("Фантазия покинула", "");
        epic2.addNewSubTask(new SubTask("some subtask", "some subtask", TaskStatus.NEW));

        taskManager.addTask(task1, task2, epic1, epic2);

        System.out.println("\t\t\tСПИСОК ЗАДАЧ");
        for (Task task : taskManager.getTaskHashMap().values()) {
            System.out.println(task);
        }

        System.out.println("\n\t\t\tСТАТУСЫ ЗДАЧ");

        task1.changeStatus(TaskStatus.DONE);
        task2.changeStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Измененный статус задачи 1 : " + task1.getTaskStatus());
        System.out.println("Измененный статус задачи 2 : " + task2.getTaskStatus());

        System.out.println("Если все подзадачи NEW, Epic : " + epic1.getTaskStatus());
        ArrayList<SubTask> tasksOfEpic1 = epic1.getSubTasks();
        tasksOfEpic1.getFirst().changeStatus(TaskStatus.DONE);
        tasksOfEpic1.getLast().changeStatus(TaskStatus.DONE);

        System.out.println("Если все подзадачи DONE, Epic : " + epic1.getTaskStatus());

        tasksOfEpic1.getFirst().changeStatus(TaskStatus.IN_PROGRESS);
        System.out.println("В других случаях Epic : " + epic1.getTaskStatus());

        taskManager.removeTask(1);
        taskManager.removeTask(3);
        System.out.println("\n\t\t\tСПИСОК ПОСЛЕ УДАЛЕНИЯ");
        for (Task task : taskManager.getTaskHashMap().values()) {
            System.out.println(task);
        }
    }
}
