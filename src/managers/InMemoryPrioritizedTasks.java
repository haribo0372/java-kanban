package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class InMemoryPrioritizedTasks implements PrioritizedTasksManager {
    private final Set<Task> tasks = new HashSet<>();
    private final Set<Epic> epics = new HashSet<>();
    private final Set<SubTask> subTasks = new HashSet<>();
    private final Comparator<Task> taskComparator = (o1, o2) -> {
        int res = o1.getStartTime().compareTo(o2.getStartTime());
        if (res == 0) return -1;
        return res;
    };
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    public static boolean tasksOverlap(Task t1, Task t2) {
        return t1.getStartTime().isBefore(t2.getEndTime()) && t2.getStartTime().isBefore(t1.getEndTime());
    }

    @Override
    public void addTask(Task task) {
        if (task.getStartTime() != null) {
            if (taskIsValidate(task)) {
                tasks.add(task);
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getStartTime() != null) {
            if (epicIsValidate(epic)) {
                epics.add(epic);
                prioritizedTasks.add(epic);
            }
        }
    }

    @Override
    public void addSubtask(SubTask subTask) {
        if (subTask.getStartTime() != null) {
            if (subtaskIsValidate(subTask)) {
                subTasks.add(subTask);
                prioritizedTasks.add(subTask);
            }
        }
    }

    private boolean taskIsValidate(Task task) {
        if (task.getStartTime() == null) return false;

        return tasks.stream().noneMatch(i -> tasksOverlap(i, task)) &&
                epics.stream().noneMatch(i -> tasksOverlap(i, task)) &&
                subTasks.stream().noneMatch(i -> tasksOverlap(i, task));
    }

    private boolean epicIsValidate(Epic epic) {
        if (epic.getStartTime() == null) return false;

        return tasks.stream().noneMatch(i -> tasksOverlap(i, epic)) &&
                epics.stream().noneMatch(i -> tasksOverlap(i, epic)) &&
                subTasks.stream().noneMatch(i -> {
                    if (!epic.getSubTasks().contains(i))
                        return tasksOverlap(i, epic);
                    return false;
                });
    }

    private boolean subtaskIsValidate(SubTask subTask) {
        if (subTask.getStartTime() == null) return false;

        AtomicBoolean currentEpicIsAvailable = new AtomicBoolean(false);
        boolean flag = epics.stream().noneMatch(i -> {
            if (!subTask.getCurrentEpic().equals(i))
                return tasksOverlap(i, subTask);
            currentEpicIsAvailable.set(true);
            return false;
        });
        return tasks.stream().noneMatch(i -> tasksOverlap(i, subTask)) &&
                subTasks.stream().noneMatch(i -> tasksOverlap(i, subTask)) &&
                flag && currentEpicIsAvailable.get();
    }

    @Override
    public void updateTask(Task task) {
        if (task.getStartTime() == null) {
            tasks.remove(task);
            prioritizedTasks.remove(task);
            return;
        }
        if (taskIsValidate(task)) {
            update(task);
        } else {
            prioritizedTasks.removeIf(t1 -> t1.equals(task));
            tasks.remove(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic.getStartTime() == null) {
            epics.remove(epic);
            epic.getSubTasks().forEach(subTasks::remove);
            prioritizedTasks.remove(epic);
            return;
        }
        if (epicIsValidate(epic)) {
            update(epic);
        } else {
            prioritizedTasks.removeIf(epic::equals);
            epics.remove(epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask.getStartTime() == null || !epics.contains(subTask.getCurrentEpic())) {
            subTasks.remove(subTask);
            prioritizedTasks.remove(subTask);
            return;
        }
        if (subtaskIsValidate(subTask)) {
            update(subTask);
        } else {
            prioritizedTasks.removeIf(subTask::equals);
            subTasks.remove(subTask);
        }
    }

    private void update(Task task) {
        if (prioritizedTasks.contains(task)) {
            prioritizedTasks.removeIf(task::equals);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void removeTask(Task task) {
        if (task.getStartTime() == null) return;

        tasks.remove(task);
        prioritizedTasks.removeIf(task::equals);
    }

    @Override
    public void removeEpic(Epic epic) {
        if (epic.getStartTime() == null) return;

        epics.remove(epic);
        prioritizedTasks.removeIf(epic::equals);
    }

    @Override
    public void removeSubtask(SubTask subTask) {
        if (subTask.getStartTime() == null) return;

        subTasks.remove(subTask);
        prioritizedTasks.removeIf(subTask::equals);
    }

    @Override
    public void clearTasks() {
        tasks.forEach(t1 -> prioritizedTasks.removeIf(t1::equals));
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.forEach(e1 -> {
            e1.getSubTasks().forEach(st1 -> prioritizedTasks.removeIf(st1::equals));
            prioritizedTasks.removeIf(e1::equals);
        });
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        subTasks.forEach(st1 -> prioritizedTasks.removeIf(st1::equals));
        subTasks.clear();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }
}
