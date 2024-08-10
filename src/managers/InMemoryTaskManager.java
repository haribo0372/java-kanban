package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int serial = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final InMemoryPrioritizedTasks prioritizedTasks = new InMemoryPrioritizedTasks();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getEpicSubtasks(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic == null) return null;

        return epic.getSubTasks();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        updateHistory(task);
        return task;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = subtasks.get(id);
        updateHistory(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        updateHistory(epic);
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        int id = serial++;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.addTask(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = serial++;
        epic.setId(id);
        epics.put(id, epic);
        prioritizedTasks.addEpic(epic);
        return id;
    }

    @Override
    public int addNewSubtask(SubTask subtask) {
        Epic currentEpic = subtask.getCurrentEpic();
        if (currentEpic == null) return -1;

        int currentEpicId = currentEpic.getId();
        if (epics.get(currentEpicId) == null) return -1;

        int id = serial++;
        subtask.setId(id);
        currentEpic.addNewSubTask(subtask);
        subtasks.put(id, subtask);
        prioritizedTasks.addSubtask(subtask);

        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.get(id) == null) return;
        tasks.put(id, task);
        prioritizedTasks.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic storageEpic = epics.get(id);
        if (storageEpic == null) return;

        storageEpic.setName(epic.getName());
        storageEpic.setDescription(epic.getDescription());
        prioritizedTasks.updateEpic(epic);
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        int id = subtask.getId();
        SubTask storageSubtask = subtasks.get(id);
        if (storageSubtask == null) return;
        Epic currentEpic = getEpic(storageSubtask.getCurrentEpic().getId());
        currentEpic.updateSubTask(subtask);
        subtasks.put(id, subtask);
        prioritizedTasks.updateSubTask(subtask);
    }

    @Override
    public void updateHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeTask(task);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeEpic(epic);
        if (epic == null) return;

        epic.getSubTasks()
                .forEach(subTask -> {
                    subtasks.remove(subTask.getId());
                    historyManager.remove(subTask.getId());
                    prioritizedTasks.removeSubtask(subTask);
                });
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subTask = subtasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeSubtask(subTask);
        if (subTask == null) return;
        int epicId = subTask.getCurrentEpic().getId();
        Epic currentEpic = getEpic(epicId);
        currentEpic.removeSubTask(subTask);
    }

    @Override
    public void deleteTasks() {
        tasks.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeTask(v);
        });
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeEpic(v);
        });
        epics.clear();
        subtasks.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeSubtask(v);
        });
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeSubtask(v);
        });
        subtasks.clear();
        epics.values().forEach(Epic::removeAllSubTasks);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.getPrioritizedTasks();
    }
}
