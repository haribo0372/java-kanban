package managers;

import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    final LocalDateTime[] dateTimes = new LocalDateTime[]{
            LocalDateTime.of(2001, 1, 1, 1, 1, 1),
            LocalDateTime.of(2002, 2, 2, 2, 2, 2),
            LocalDateTime.of(2003, 3, 3, 3, 3, 3)};

    final Duration[] durations = new Duration[]{
            Duration.ofHours(1),
            Duration.ofHours(2),
            Duration.ofHours(3)};

    final Task[] tasks = new Task[]{
            new Task("1", "1", TaskStatus.NEW),
            new Task("2", "2", TaskStatus.NEW),
            new Task("3", "3", TaskStatus.NEW)};

    final Epic[] epics = new Epic[]{
            new Epic("1", "1"),
            new Epic("2", "2"),
            new Epic("3", "3")};

    final SubTask[] subTasks = new SubTask[]{
            new SubTask("SUBtask_1", "1", TaskStatus.NEW),
            new SubTask("2", "2", TaskStatus.NEW),
            new SubTask("3", "3", TaskStatus.NEW)};


    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    void addNewTask() {
        Task task = new Task("name_1", "description_1", TaskStatus.NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertFalse(tasks.isEmpty(), "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("name_1", "description_1");
        final int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubTask() {
        Epic epic = new Epic("name_1", "description_1");
        SubTask subTask = new SubTask("name_1", "description_1", TaskStatus.NEW);
        subTask.setCurrentEpic(epic);

        final int currentEpicId = taskManager.addNewEpic(epic);
        final int subTaskId = taskManager.addNewSubtask(subTask);

        final SubTask savedSubTask = taskManager.getSubtask(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");
        assertEquals(epic.getId(), savedSubTask.getCurrentEpic().getId(), "Эпики подзадач не совпадают.");


        final List<SubTask> subTasks = taskManager.getSubtasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void deleteTask() {
        List<Integer> taskIds = Arrays.stream(tasks).map(i -> taskManager.addNewTask(i)).toList();

        assertFalse(taskManager.getTasks().isEmpty(), "Задачи не добавились");

        taskIds.forEach(id -> taskManager.deleteTask(id));
        assertTrue(taskManager.getTasks().isEmpty(), "Задачи не удаляются");

        Arrays.stream(tasks).forEach(i -> taskManager.addNewTask(i));
        assertFalse(taskManager.getTasks().isEmpty(), "Задачи повторно не добавились");
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Задачи не удаляются");
    }

    @Test
    void deleteEpic() {
        Iterator<SubTask> subTaskIterator1 = Arrays.stream(subTasks).iterator();

        List<Integer> epicIds = Arrays.stream(epics).map(epic -> {
            SubTask subTask = subTaskIterator1.next();
            epic.addNewSubTask(subTask);
            int id = taskManager.addNewEpic(epic);
            taskManager.addNewSubtask(subTask);
            return id;
        }).toList();

        assertFalse(taskManager.getEpics().isEmpty(), "Эпики не добавились");

        Arrays.stream(subTasks).forEach(i -> assertNotNull(i.getCurrentEpic(), "Эпики не связываются с подзадачами"));

        epicIds.forEach(i -> taskManager.deleteEpic(i));
        assertTrue(taskManager.getEpics().isEmpty(), "Эпики не удалились");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадачи не удаляются вместе с эпиками");

        Iterator<SubTask> subTaskIterator2 = Arrays.stream(subTasks).iterator();
        Arrays.stream(epics).forEach(epic -> {
            SubTask subTask = subTaskIterator2.next();
            epic.addNewSubTask(subTask);
            taskManager.addNewEpic(epic);
            taskManager.addNewSubtask(subTask);
        });
        assertFalse(taskManager.getEpics().isEmpty(), "Эпики повторно не добавились");

        taskManager.deleteEpics();
        assertTrue(taskManager.getEpics().isEmpty(), "Эпики не удалились");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадачи не удаляются вместе с эпиками");
    }

    @Test
    void deleteSubTask() {
        Epic epic = epics[0];
        SubTask subTask = subTasks[0];
        subTask.setCurrentEpic(epic);

        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subTask);

        taskManager.deleteSubtask(subTask.getId());
        assertTrue(taskManager.getEpicSubtasks(epic.getId()).isEmpty(), "Удаленные подзадачи остаются в эпике");
    }

    @Test
    void getHistory() {
        assertNotNull(taskManager.getHistory(), "Менеджер задач не должен возвращать null в качестве истории просмотра");
        List<Integer> taskIds = Arrays.stream(tasks).map(i -> taskManager.addNewTask(i)).toList();

        taskIds.forEach(id -> taskManager.getTask(id));
        assertFalse(taskManager.getHistory().isEmpty(), "История неверно возвращается");
    }

    @Test
    void prioritizedTasksTest() {
        String message1 = "Менеджер задач не должен возвращать null в качестве списка приорететных задач";
        assertNotNull(taskManager.getPrioritizedTasks(), message1);
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), message1);

        Task task = tasks[0];
        Epic epic = epics[0];
        SubTask subTask1 = subTasks[0];
        SubTask subTask2 = subTasks[1];
        epic.addNewSubTask(subTask1, subTask2);

        taskManager.addNewTask(task);
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Задачи не имеющие времени добавляются в список приорететных задач");

        taskManager.addNewEpic(epic);
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Эпики не должны добавляться в список приорететных задач");

        taskManager.addNewSubtask(subTask1);
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Подзадачи не имеющие времени добавляются в список приорететных задач");

        String message2 = "Задачи неверно сортируются";
        task.setStartTime(dateTimes[0]);
        task.setDuration(durations[0]);
        subTask1.setStartTime(dateTimes[0]);
        subTask1.setDuration(durations[0]);

        taskManager.deleteTasks();
        taskManager.deleteSubtasks();

        taskManager.addNewTask(task);
        taskManager.addNewSubtask(subTask1);

        assertEquals(1, taskManager.getPrioritizedTasks().size(), message2);

        subTask1.setStartTime(task.getEndTime().plusYears(1));
        taskManager.addNewSubtask(subTask1);
        List<Task> tempList = taskManager.getPrioritizedTasks();

        assertEquals(2, tempList.size(), message2);
        assertEquals(task, tempList.getFirst(), message2);
        assertEquals(subTask1, tempList.getLast(), message2);

        subTask1.setStartTime(task.getStartTime());
        taskManager.updateSubtask(subTask1);
        assertEquals(2, taskManager.getPrioritizedTasks().size(), message2);
        assertEquals(task, taskManager.getPrioritizedTasks().getFirst(), message2);
    }
}

