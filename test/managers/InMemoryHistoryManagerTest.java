package managers;

import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        Task task = new Task("name_1", "description_1", TaskStatus.NEW);
        historyManager.add(task);
        Task savedTask = historyManager.getHistory().get(0);

        assertEquals(task.getName(), savedTask.getName(), "Имена задач не равны после добавления");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задач не равны после добавления");
        assertEquals(task.getTaskStatus(), savedTask.getTaskStatus(), "Статусы задач не равны после добавления");

        Epic epic = new Epic("name_2", "description_2");
        SubTask subTask = new SubTask("name_3", "description_3", TaskStatus.NEW);

        assertTrue(historyManager.add(task), "Не удалось добавить задачу в историю");
        assertTrue(historyManager.add(epic), "Не удалось добавить эпик в историю");
        assertTrue(historyManager.add(subTask), "Не удалось добавить подзадачу в историю");

        List<Task> rightHistory = List.of(subTask, epic, task, task);

        assertEquals(historyManager.getHistory(), rightHistory, "История запросов высчитывается неверно");

        IntStream.range(0, 10).forEach(i -> historyManager.add(task));

        assertEquals(10, historyManager.getHistory().size(), "Не соблюдается ограничение истории в 10 элементов");
    }
}
