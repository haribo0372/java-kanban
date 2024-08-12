package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic1;
    Epic epic2;
    SubTask[] subTasks;

    {
        SubTask subTask1 = new SubTask("", "", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("", "", TaskStatus.NEW);
        SubTask subTask3 = new SubTask("", "", TaskStatus.NEW);
        subTask1.setId(1);
        subTask2.setId(2);
        subTask3.setId(3);

        subTasks = new SubTask[]{subTask1, subTask2, subTask3};
    }

    @BeforeEach
    void beforeEach() {
        epic1 = new Epic("name_1", "description_1");
        epic2 = new Epic("name_2", "description_2");
    }

    @Test
    void equals() {
        epic1.setId(1);
        epic2.setId(epic1.getId());
        assertEquals(epic1, epic2, "Эпики с одинаковым id не равны");
    }

    @Test
    void taskStatusCalculation() {
        assertEquals(epic1.getTaskStatus(), TaskStatus.NEW, "Статус эпика без подзадач расчитывается неверно");

        Arrays.stream(subTasks).forEach(epic1::addNewSubTask);
        assertEquals(epic1.getTaskStatus(), TaskStatus.NEW, "Статус эпика с подзадачами, имеющими статус NEW, рассчитывается неверно.");

        Arrays.stream(subTasks).forEach(i -> {
            i.setTaskStatus(TaskStatus.DONE);
            epic1.updateSubTask(i);
        });
        assertEquals(epic1.getTaskStatus(), TaskStatus.DONE, "Статус эпика с подзадачами, имеющими статус DONE, рассчитывается неверно.");

        subTasks[0].setTaskStatus(TaskStatus.NEW);
        epic1.updateSubTask(subTasks[0]);
        assertEquals(epic1.getTaskStatus(), TaskStatus.IN_PROGRESS, "Статус эпика с подзадачами, имеющими статус DONE и NEW, рассчитывается неверно.");

        Arrays.stream(subTasks).forEach(i -> {
            i.setTaskStatus(TaskStatus.IN_PROGRESS);
            epic1.updateSubTask(i);
        });
        assertEquals(epic1.getTaskStatus(), TaskStatus.IN_PROGRESS, "Статус эпика с подзадачами, имеющими статус IN_PROGRESS, рассчитывается неверно.");
    }

    @Test
    void timesCalculation() {
        Epic epic = new Epic("e_1", "1");
        assertNull(epic.getStartTime());
        assertNull(epic.getDuration());

        SubTask subTask1 = new SubTask("s_1", "1", TaskStatus.NEW,
                Duration.ofDays(3),
                LocalDateTime.of(2024, 7, 4, 13, 0, 0));

        SubTask subTask2 = new SubTask("s_2", "2", TaskStatus.NEW,
                Duration.ofDays(3),
                LocalDateTime.of(2024, 7, 7, 13, 0, 0));
        SubTask subTask3 = new SubTask("s_3", "s_3", TaskStatus.NEW);

        epic.setId(1);
        subTask1.setId(2);
        subTask2.setId(3);
        subTask3.setId(4);
        epic.addNewSubTask(subTask1, subTask2, subTask3);

        assertEquals(epic.getStartTime(), subTask1.getStartTime(),
                "Неверно вычисление времени начала выполнения эпика");

        assertEquals(epic.getEndTime(), subTask2.getEndTime(),
                "Неверно вычисление времени конца выполнения эпика");

        assertEquals(epic.getDuration(), subTask1.getDuration().plus(subTask2.getDuration()),
                "Неверно вычисление продолжительности выполнения эпика");
    }
}