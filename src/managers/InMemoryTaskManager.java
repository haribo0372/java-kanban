package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int serial = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
        Optional<Epic> epicOpt = getEpic(epicId);
        return epicOpt.map(Epic::getSubTasks).orElse(null);
    }

    @Override
    public Optional<Task> getTask(int id) {
        Optional<Task> taskOpt = Optional.ofNullable(tasks.get(id));
        taskOpt.ifPresent(this::updateHistory);
        return taskOpt;
    }

    @Override
    public Optional<SubTask> getSubtask(int id) {
        Optional<SubTask> subtaskOpt = Optional.ofNullable(subtasks.get(id));
        subtaskOpt.ifPresent(this::updateHistory);
        return subtaskOpt;
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        Optional<Epic> epicOpt = Optional.ofNullable(epics.get(id));
        epicOpt.ifPresent(this::updateHistory);
        return epicOpt;
    }

    @Override
    public int addNewTask(Task task) {
        int id = -1;
        if (taskHasNoTime(task)) {
            id = serial++;
            task.setId(id);
            tasks.put(id, task);
            return id;
        } else if (taskIsValidateInTime(task)) {
            id = serial++;
            task.setId(id);
            tasks.put(id, task);
            prioritizedTasks.add(task);
            return id;
        }
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = serial++;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int addNewSubtask(SubTask subtask) {
        Epic currentEpic = subtask.getCurrentEpic();
        if (currentEpic == null) return -1;

        int currentEpicId = currentEpic.getId();
        if (epics.get(currentEpicId) == null) return -1;

        int id = -1;
        if (taskHasNoTime(subtask)) {
            id = serial++;
            subtask.setId(id);
            currentEpic.addNewSubTask(subtask);
            subtasks.put(id, subtask);
        } else if (taskIsValidateInTime(subtask)) {
            id = serial++;
            subtask.setId(id);
            currentEpic.addNewSubTask(subtask);
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
        }

        return id;
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.get(id) == null) return;
        if (taskHasNoTime(task)) {
            prioritizedTasks.removeIf(task::equals);
            tasks.put(id, task);
            return;
        }
        if (taskIsValidateInTime(task)) {
            tasks.put(id, task);
            prioritizedTasks.removeIf(task::equals);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic storageEpic = epics.get(id);
        if (storageEpic == null) return;

        storageEpic.setName(epic.getName());
        storageEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        int id = subtask.getId();
        SubTask storageSubtask = subtasks.get(id);
        if (storageSubtask == null) return;
        Epic currentEpic = getEpic(storageSubtask.getCurrentEpic().getId()).get();
        currentEpic.updateSubTask(subtask);

        if (taskHasNoTime(subtask)) {
            subtasks.put(id, subtask);
            prioritizedTasks.removeIf(subtask::equals);
            return;
        }
        if (taskIsValidateInTime(subtask)) {
            prioritizedTasks.removeIf(subtask::equals);
            prioritizedTasks.add(subtask);
            subtasks.put(id, subtask);
        }
    }

    @Override
    public void updateHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        historyManager.remove(id);
        if (task != null) prioritizedTasks.removeIf(task::equals);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic == null) return;

        epic.getSubTasks()
                .forEach(subTask -> {
                    subtasks.remove(subTask.getId());
                    historyManager.remove(subTask.getId());
                    prioritizedTasks.removeIf(subTask::equals);
                });
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subTask = subtasks.remove(id);
        historyManager.remove(id);
        if (subTask == null) return;
        prioritizedTasks.removeIf(subTask::equals);
        int epicId = subTask.getCurrentEpic().getId();
        Optional<Epic> currentEpicOpt = getEpic(epicId);
        currentEpicOpt.ifPresent(epic -> epic.removeSubTask(subTask));
    }

    @Override
    public void deleteTasks() {
        tasks.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeIf(v::equals);
        });
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.forEach((k, v) -> {
            historyManager.remove(k);
        });
        epics.clear();
        subtasks.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeIf(v::equals);
        });
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.forEach((k, v) -> {
            historyManager.remove(k);
            prioritizedTasks.removeIf(v::equals);
        });
        subtasks.clear();
        epics.values().forEach(Epic::removeAllSubTasks);
    }

    protected boolean taskIsValidateInTime(Task task) {
        if (taskHasNoTime(task)) return false;

        return prioritizedTasks.stream().noneMatch(i -> tasksOverlap(i, task));
    }

    protected boolean taskHasNoTime(Task task) {
        return task.getStartTime() == null || task.getDuration() == null;
    }

    protected boolean tasksOverlap(Task t1, Task t2) {
        LocalDateTime startTime1 = t1.getStartTime();
        LocalDateTime startTime2 = t2.getStartTime();
        LocalDateTime endTime1 = t1.getEndTime();
        LocalDateTime endTime2 = t2.getEndTime();

        return startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }
}
