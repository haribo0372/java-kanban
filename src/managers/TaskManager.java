package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getTasks();

    List<SubTask> getSubtasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubtasks(int epicId);

    Task getTask(int id);

    SubTask getSubtask(int id);

    Epic getEpic(int id);

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(SubTask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subtask);

    void updateHistory(Task task);

    List<Task> getPrioritizedTasks();

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();
}
