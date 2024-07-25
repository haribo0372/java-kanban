package managers;

import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class FileBackedTaskManagerTest {
    static FileBackedTaskManager taskManager;
    static File testFile;

    static Task task;
    static Epic epic1;
    static Epic epic2;
    static SubTask subTask1;
    static SubTask subTask2;
    static SubTask subTask3;

    @BeforeAll
    static void managerPreparation() throws IOException {
        testFile = File.createTempFile("test", ".txt");
        taskManager = new FileBackedTaskManager(testFile);

        task = new Task("task_name_1", "task_description_1", TaskStatus.NEW);
        epic1 = new Epic("epic_name_1", "epic_description_1");
        epic2 = new Epic("epic_name_2", "epic_description_2");

        subTask1 = new SubTask("subtask_name_1", "subtask_description_1", TaskStatus.NEW);
        subTask1.setCurrentEpic(epic1);

        subTask2 = new SubTask("subtask_name_2", "subtask_description_2", TaskStatus.NEW);
        subTask2.setCurrentEpic(epic1);

        subTask3 = new SubTask("subtask_name_3", "subtask_description_3", TaskStatus.NEW);
        subTask3.setCurrentEpic(epic2);

        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask3);
    }

    @Test
    void saveTasksTest() throws IOException {
        Task[] tasks = new Task[]{task, epic1, epic2, subTask1, subTask2, subTask3};
        String[] strings = Files.readString(testFile.toPath()).split("\n");

        String message = "Задачи неправильно сохраняются в файл";
        for (int i = 1; i < strings.length; i++) {
            assertEquals(tasks[i - 1].toStringCSV(), strings[i], message);
        }
    }

    @Test
    void restoringManagerFromFileTest() {
        FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(testFile);
        String message = "Неудача при восстановлении задач из файла";

        Task restoredTask = restoredManager.getTask(task.getId());
        Epic restoredEpic1 = restoredManager.getEpic(epic1.getId());
        Epic restoredEpic2 = restoredManager.getEpic(epic2.getId());
        SubTask restoredSubtask1 = restoredManager.getSubtask(subTask1.getId());
        SubTask restoredSubtask2 = restoredManager.getSubtask(subTask2.getId());
        SubTask restoredSubtask3 = restoredManager.getSubtask(subTask3.getId());

        List<Task> rightTaskList = createSortedListWithTasks(task, epic1, epic2, subTask1, subTask2, subTask3);
        List<Task> currentTaskList =
                createSortedListWithTasks(
                        restoredTask, restoredEpic1,
                        restoredEpic2, restoredSubtask1,
                        restoredSubtask2, restoredSubtask3);

        for (int i = 0; i < rightTaskList.size(); i++) {
            assertEquals(rightTaskList.get(i), currentTaskList.get(i), message);
        }

    }

    List<Task> createSortedListWithTasks(Task... tasks) {
        List<Task> taskList = new ArrayList<>(Arrays.asList(tasks));
        taskList.sort(Comparator.comparingInt(Task::getId));
        return taskList;
    }
}
