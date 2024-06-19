package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskTest {
    SubTask subTask1;
    SubTask subTask2;

    @BeforeEach
    void beforeEach() {
        subTask1 = new SubTask("name_1", "description_1", TaskStatus.NEW);
        subTask2 = new SubTask("name_2", "description_2", TaskStatus.IN_PROGRESS);
    }

    @Test
    void equals() {
        subTask1.setId(1);
        subTask2.setId(subTask1.getId());
        assertEquals(subTask1, subTask2, "Подзадачи с одинаковым id не равны");
    }
}
