package managers;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.*;

public class TaskManager {
    private static int serial = 1;
    private final List<Task> taskList = new ArrayList<>();
    private final List<Epic> epicList = new ArrayList<>();
    private final List<SubTask> subTaskList = new ArrayList<>();

    public List<Task> getTasks() {
        return taskList;
    }

    public List<SubTask> getSubtasks() {
        return subTaskList;
    }

    public List<Epic> getEpics() {
        return epicList;
    }

    public List<Task> getAllTasks(){
        List<Task> all = new ArrayList<>();
        all.addAll(taskList);
        all.addAll(epicList);
        return all;
    }
    public List<SubTask> getEpicSubtasks(int epicId) {
        Optional<Epic> epic = getEpic(epicId);
        return epic.<List<SubTask>>map(Epic::getSubTasks).orElse(null);

    }

    public Optional<Task> getTask(int id) {
        return taskList.stream().filter(i -> i.getId() == id).findFirst();
    }

    public Optional<SubTask> getSubtask(int id) {
        return subTaskList.stream().filter(i -> i.getId() == id).findFirst();
    }

    public Optional<Epic> getEpic(int id) {
        return epicList.stream().filter(i -> i.getId() == id).findFirst();
    }

    public int addNewTask(Task task) {
        task.setId(serial++);
        taskList.add(task);
        return (serial - 1);
    }

    public int addNewEpic(Epic epic) {
        epic.setId(serial++);
        epicList.add(epic);
        return (serial - 1);
    }

    public int addNewSubtask(SubTask subtask) {
        subtask.setId(serial++);
        subTaskList.add(subtask);
        return (serial - 1);
    }

    public void updateTask(Task task) {
        for (Task task1 : taskList) {
            if (task1.equals(task)) {
                task1.change(task);
            }
        }
    }

    public void updateEpic(Epic epic) {
        for (Epic epic1 : epicList) {
            if (epic1.equals(epic)) {
                epic1.change(epic);
            }
        }
    }

    public void updateSubtask(SubTask subTask) {
        for (SubTask subTask1 : subTaskList) {
            if (subTask1.equals(subTask)) {
                subTask1.change(subTask);
            }
        }
    }

    public void deleteTask(int id) {
        taskList.removeIf(i -> i.getId() == id);
    }

    public void deleteEpic(int id) {
        epicList.removeIf(i -> i.getId() == id);
    }

    public void deleteSubtask(int id) {
        subTaskList.removeIf(i -> i.getId() == id);
    }

    public void deleteTasks(){
        taskList.clear();
    }

    public void deleteSubtasks(){
        subTaskList.clear();
    }

    public void deleteEpics(){
        epicList.clear();
    }
}
