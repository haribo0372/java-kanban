package managers;

import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
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

        Task someTask = new Task("", "", TaskStatus.NEW);
        assertTrue(manager.add(someTask), "Объект не может добавить в себя экземпляр класса Task");

        Epic someEpic = new Epic("", "");
        assertTrue(manager.add(someEpic), "Объект не может добавить в себя экземпляр класса Epic");

        SubTask someSubtask = new SubTask("", "", TaskStatus.NEW);
        assertTrue(manager.add(someSubtask), "Объект не может добавить в себя экземпляр класса SubTask");


        assertNotNull(manager.getHistory(), "Объект содержит null в поле Managers.history");
    }
}
