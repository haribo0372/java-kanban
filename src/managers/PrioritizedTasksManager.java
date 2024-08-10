package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;

public interface PrioritizedTasksManager {
    void addTask(Task task);

    void addSubtask(SubTask subTask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(Task task);

    void removeEpic(Epic epic);

    void removeSubtask(SubTask subTask);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    List<Task> getPrioritizedTasks();
}
