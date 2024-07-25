package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public void addNewSubTask(SubTask subTask) {
        subTasks.add(subTask);
        subTask.setCurrentEpic(this);
        updateStatus();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void updateSubTask(SubTask subTask) {
        for (SubTask subTask1 : subTasks) {
            if (subTask1.equals(subTask)) {
                subTask1.setName(subTask.getName());
                subTask1.setDescription(subTask.getDescription());
                subTask1.setStatus(subTask.getTaskStatus());
            }
        }
        updateStatus();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        updateStatus();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        updateStatus();
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            this.taskStatus = TaskStatus.NEW;
            return;
        }
        Iterator<SubTask> iterator = subTasks.iterator();
        TaskStatus ts = iterator.next().getTaskStatus();
        while (iterator.hasNext()) {
            if (ts != iterator.next().getTaskStatus()) {
                this.taskStatus = TaskStatus.IN_PROGRESS;
                return;
            }
        }
        this.taskStatus = ts;
    }

    @Override
    public String toString() {
        int[] arrayWithSubTasksId = subTasks.stream().mapToInt(Task::getId).toArray();
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", subTasksId=" + Arrays.toString(arrayWithSubTasksId) +
                '}';
    }
}
