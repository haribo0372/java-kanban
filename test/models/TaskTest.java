package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    Task task1;
    Task task2;

    @BeforeEach
    void beforeEach() {
        task1 = new Task("name_1", "description_1", TaskStatus.NEW);
        task2 = new Task("name_2", "description_2", TaskStatus.IN_PROGRESS);
    }

    @Test
    void equals() {
        task1.setId(1);
        task2.setId(task1.getId());
        assertEquals(task1, task2, "Задачи с одинаковым id не равны");
    }
}
