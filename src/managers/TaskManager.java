package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.*;

public class TaskManager {
    private int serial = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getEpicSubtasks(int epicId) {
        return getEpic(epicId).getSubTasks();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public int addNewTask(Task task) {
        int id = serial++;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        int id = serial++;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public int addNewSubtask(SubTask subtask) {
        Epic currentEpic = subtask.getCurrentEpic();
        if (currentEpic == null) return 0;

        int currentEpicId = currentEpic.getId();
        if (getEpic(currentEpicId) == null) return 0;

        int id = serial++;
        subtask.setId(id);
        subtasks.put(id, subtask);
        return subtask.getId();
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.get(id) == null) return;
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.get(id) == null) return;
        epics.put(id, epic);
    }

    public void updateSubtask(SubTask subtask) {
        int id = subtask.getId();
        SubTask storageSubtask = subtasks.get(id);
        if (storageSubtask == null) return;
        storageSubtask.getCurrentEpic().updateStatus();
        subtasks.put(id, subtask);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) return;

        for (SubTask subTask : epic.getSubTasks()){
            subtasks.remove(subTask.getId());
        }
        epic.removeAllSubTasks();
    }

    public void deleteSubtask(int id) {
        SubTask subTask = subtasks.remove(id);
        if (subTask == null) return;
        subTask.getCurrentEpic().removeSubTask(subTask);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()){
            epic.removeAllSubTasks();
        }
    }
}
