package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic1;
    Epic epic2;

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
}