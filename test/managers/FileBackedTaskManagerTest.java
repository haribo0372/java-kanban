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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    static FileBackedTaskManager taskManager;
    static File testFile;

    static Task task1;
    static Task task2;
    static Epic epic1;
    static Epic epic2;
    static SubTask subTask1;
    static SubTask subTask2;
    static SubTask subTask3;

    @BeforeAll
    static void managerPreparation() throws IOException {
        testFile = File.createTempFile("test", ".txt");
        taskManager = new FileBackedTaskManager(testFile);

        task1 = new Task("task_name_1", "task_description_1", TaskStatus.NEW);
        task2 = new Task("task_name_2", "task_description_2", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        epic1 = new Epic("epic_name_1", "epic_description_1");
        epic2 = new Epic("epic_name_2", "epic_description_2");

        subTask1 = new SubTask("subtask_name_1", "subtask_description_1", TaskStatus.NEW,
                Duration.ofHours(3), LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        subTask1.setCurrentEpic(epic1);

        subTask2 = new SubTask("subtask_name_2", "subtask_description_2", TaskStatus.NEW);
        subTask2.setCurrentEpic(epic1);

        subTask3 = new SubTask("subtask_name_3", "subtask_description_3", TaskStatus.NEW);
        subTask3.setCurrentEpic(epic2);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subTask3);
    }

    @Test
    void saveTasksTest() throws IOException {
        Task[] tasks = new Task[]{task1, task2, epic1, epic2, subTask1, subTask2, subTask3};
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

        Task restoredTask1 = restoredManager.getTask(task1.getId()).get();
        Task restoredTask2 = restoredManager.getTask(task2.getId()).get();
        Epic restoredEpic1 = restoredManager.getEpic(epic1.getId()).get();
        Epic restoredEpic2 = restoredManager.getEpic(epic2.getId()).get();
        SubTask restoredSubtask1 = restoredManager.getSubtask(subTask1.getId()).get();
        SubTask restoredSubtask2 = restoredManager.getSubtask(subTask2.getId()).get();
        SubTask restoredSubtask3 = restoredManager.getSubtask(subTask3.getId()).get();

        assertTrue(checkEqualTasks(restoredTask1, task1), message);
        assertTrue(checkEqualTasks(restoredTask2, task2), message);
        assertTrue(checkEqualEpic(restoredEpic1, epic1), message);
        assertTrue(checkEqualEpic(restoredEpic2, epic2), message);
        assertTrue(checkEqualSubTask(restoredSubtask1, subTask1), message);
        assertTrue(checkEqualSubTask(restoredSubtask2, subTask2), message);
        assertTrue(checkEqualSubTask(restoredSubtask3, subTask3), message);
    }

    boolean checkEqualTasks(Task t1, Task t2) {
        if (!Objects.equals(t1.getId(), t2.getId())) return false;
        if (t1.getTaskStatus() != t2.getTaskStatus()) return false;
        if (!Objects.equals(t1.getName(), t2.getName())) return false;
        if (!Objects.equals(t1.getStartTime(), t2.getStartTime())) return false;
        if (!Objects.equals(t1.getDuration(), t2.getDuration())) return false;
        return Objects.equals(t1.getDescription(), t2.getDescription());
    }

    boolean checkEqualSubTask(SubTask subTask1, SubTask subTask2) {
        if (!Objects.equals(subTask1.getCurrentEpic().getId(), subTask2.getCurrentEpic().getId()))
            return false;
        return checkEqualTasks(subTask1, subTask2);
    }

    boolean checkEqualEpic(Epic epic1, Epic epic2) {
        List<SubTask> list1 = epic1.getSubTasks();
        List<SubTask> list2 = epic2.getSubTasks();

        if (list1.size() != list2.size()) return false;

        for (int i = 0; i < list1.size(); i++) {
            if (!checkEqualSubTask(list1.get(i), list2.get(i))) return false;
        }
        if (!Objects.equals(epic1.getEndTime(), epic2.getEndTime())) return false;
        return checkEqualTasks(epic1, epic2);
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            File tempFile = File.createTempFile("superTest", ".txt");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
