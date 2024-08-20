package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getTasks();

    List<SubTask> getSubtasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubtasks(int epicId);

    Optional<Task> getTask(int id);

    Optional<SubTask> getSubtask(int id);

    Optional<Epic> getEpic(int id);

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
