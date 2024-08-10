package managers;

import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;
    Task task;
    Epic epic;
    SubTask subTask;
    int count;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();

        count = 0;

        task = new Task("name_1", "description_1", TaskStatus.NEW);
        task.setId(++count);

        epic = new Epic("name_2", "description_2");
        epic.setId(++count);

        subTask = new SubTask("name_3", "description_3", TaskStatus.NEW);
        subTask.setId(++count);
        subTask.setCurrentEpic(epic);
    }

    @Test
    void add() {
        historyManager.add(task);
        Task savedTask = historyManager.getHistory().get(0);

        assertEquals(task.getName(), savedTask.getName(), "Имена задач не равны после добавления");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задач не равны после добавления");
        assertEquals(task.getTaskStatus(), savedTask.getTaskStatus(), "Статусы задач не равны после добавления");

        epic.addNewSubTask(subTask);

        historyManager.add(task);
        assertTrue(historyManager.getHistory().contains(task), "Не удалось добавить задачу в историю");

        historyManager.add(epic);
        assertTrue(historyManager.getHistory().contains(epic), "Не удалось добавить эпик в историю");

        historyManager.add(subTask);
        assertTrue(historyManager.getHistory().contains(subTask), "Не удалось добавить подзадачу в историю");

        List<Task> rightHistory = List.of(task, epic, subTask);
        List<Task> currentHistory = historyManager.getHistory();

        assertEquals(currentHistory, rightHistory, "История запросов высчитывается неверно");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        assertEquals(3, historyManager.getHistory().size(), "История не должга хранить дубликаты");
    }

    @Test
    void testEmbeddedLinkedList() {
        historyManager.add(task);
        assertEquals(historyManager.getHistory().getLast(), task, "Добавление в встроенный связный список" +
                " происходит неверно");

        historyManager.add(epic);
        assertEquals(historyManager.getHistory().getLast(), epic, "Добавление в встроенный связный список" +
                " происходит неверно");

        historyManager.add(subTask);
        assertEquals(historyManager.getHistory().getLast(), subTask, "Добавление в встроенный связный список" +
                " происходит неверно");


        List<Task> rightHistory = List.of(task, epic, subTask);
        List<Task> currentHistory = historyManager.getHistory();

        assertEquals(currentHistory, rightHistory, "Добавление в встроенный связный список" +
                " происходит неверно");

        historyManager.remove(epic.getId());
        rightHistory = List.of(task, subTask);
        currentHistory = historyManager.getHistory();

        assertEquals(currentHistory, rightHistory, "Удаление из встроенного связного списка" +
                " происходит неверно");
    }

    @Test
    void getHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Менеджер истории не должен возвращать null, даже если история пустая");
        assertTrue(history.isEmpty(), "История должна быть пуста");
    }
}
