package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void getDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Возвращает неработоспособный объект");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager, "Возвращает неработоспособный объект");
    }
}
