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

public class InMemoryPrioritizedTasksTest {
    InMemoryPrioritizedTasks prioritizedTasks;
    static final int HOURS = 3;
    final LocalDateTime[] dateTimes = new LocalDateTime[]{
            LocalDateTime.of(2001, 1, 1, 1, 1, 1),
            LocalDateTime.of(2002, 2, 2, 2, 2, 2),
            LocalDateTime.of(2003, 3, 3, 3, 3, 3)};

    final Task[] tasks = new Task[]{
            new Task("1", "1", TaskStatus.NEW, Duration.ofHours(HOURS), dateTimes[0]),
            new Task("2", "2", TaskStatus.NEW, Duration.ofHours(HOURS), dateTimes[1]),
            new Task("3", "3", TaskStatus.NEW, Duration.ofHours(HOURS), dateTimes[2])};

    Epic epic;
    SubTask subTask1;
    SubTask subTask2;

    {
        tasks[0].setId(1);
        tasks[1].setId(2);
        tasks[2].setId(3);
    }

    @BeforeEach
    void setUp() {
        prioritizedTasks = new InMemoryPrioritizedTasks();

        epic = new Epic("e_1", "1");

        subTask1 = new SubTask("s_1", "1", TaskStatus.NEW,
                Duration.ofDays(3), dateTimes[0]);

        subTask2 = new SubTask("s_2", "2", TaskStatus.NEW,
                Duration.ofDays(3), subTask1.getEndTime());

        epic.setId(1);
        subTask1.setId(2);
        subTask2.setId(3);
        epic.addNewSubTask(subTask1, subTask2);
    }

    @Test
    void tasksOverlap() {
        String message = "Задачи неверно проверяются на пересечение во времени";

        assertFalse(InMemoryPrioritizedTasks.tasksOverlap(tasks[0], tasks[1]), message);

        tasks[2].setStartTime(tasks[1].getStartTime().plusMinutes(10));
        assertTrue(InMemoryPrioritizedTasks.tasksOverlap(tasks[1], tasks[2]), message);
        assertTrue(InMemoryPrioritizedTasks.tasksOverlap(tasks[2], tasks[1]), message);
    }

    @Test
    void addTask() {
        String message = "Задачи неверно сортируются";
        tasks[2].setStartTime(dateTimes[2]);
        Arrays.stream(tasks).forEach(i -> prioritizedTasks.addTask(i));

        Iterator<Task> taskIterator = Arrays.stream(tasks).iterator();
        prioritizedTasks.getPrioritizedTasks().forEach(i -> assertEquals(i, taskIterator.next(), message));

        prioritizedTasks.clearTasks();
        assertEquals(0, prioritizedTasks.getPrioritizedTasks().size(),
                prioritizedTasks.getPrioritizedTasks().toString());

        Task t1 = tasks[0];
        t1.setStartTime(dateTimes[0]);
        t1.setDuration(Duration.ofHours(HOURS));

        Task t2 = tasks[1];
        t2.setStartTime(dateTimes[0]);
        t2.setDuration(Duration.ofHours(HOURS));

        prioritizedTasks.addTask(t1);
        prioritizedTasks.addTask(t2);

        assertEquals(1, prioritizedTasks.getPrioritizedTasks().size(),
                "Задачи с одинаковым временным диапозоном неверно добавляются");

        tasks[1].setStartTime(tasks[0].getStartTime().minus(tasks[1].getDuration()));
        prioritizedTasks.addTask(tasks[1]);

        assertEquals(tasks[1], prioritizedTasks.getPrioritizedTasks().getFirst(), message);
        assertEquals(tasks[0], prioritizedTasks.getPrioritizedTasks().getLast(), message);
    }

    @Test
    void addEpic() {
        prioritizedTasks.addEpic(epic);
        prioritizedTasks.addSubtask(subTask1);
        assertEquals(2, prioritizedTasks.getPrioritizedTasks().size(), "Эпики неверно добавляются");

        final Epic tempEpic = new Epic("", "");

        tempEpic.addNewSubTask(new SubTask("s_2", "2", TaskStatus.NEW,
                subTask1.getDuration().plusDays(5), subTask1.getStartTime()));

        prioritizedTasks.addEpic(tempEpic);
        assertFalse(prioritizedTasks.getPrioritizedTasks().contains(tempEpic),
                "Эпики с пересечением во времени выполнения добавляются в список приорететных задач");
    }

    @Test
    void addSubTask() {
        String message = "Подзадачи неверно сохраняются в список приорететных задач";

        prioritizedTasks.addEpic(epic);
        prioritizedTasks.addSubtask(subTask1);
        prioritizedTasks.addSubtask(subTask2);
        List<Task> currPrioritizedTasks = prioritizedTasks.getPrioritizedTasks();

        assertTrue(currPrioritizedTasks.contains(epic), message);
        assertTrue(currPrioritizedTasks.contains(subTask1), message);
        assertTrue(currPrioritizedTasks.contains(subTask2), message);

        assertEquals(subTask1, currPrioritizedTasks.getFirst(), message);
        assertEquals(subTask2, currPrioritizedTasks.getLast(), message);

        subTask2.setStartTime(subTask1.getStartTime());
        subTask2.setDuration(subTask1.getDuration());
        prioritizedTasks.updateSubTask(subTask2);
        assertFalse(prioritizedTasks.getPrioritizedTasks().contains(subTask2),
                "Обновленная подзадача не удаляется");
    }
}
