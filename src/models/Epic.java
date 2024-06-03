package models;

import java.util.ArrayList;
import java.util.Iterator;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public void addNewSubTask(SubTask subTask) {
        subTask.setCurrentEpic(this);
        subTasks.add(subTask);
        updateStatus();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void change(Epic epic){
        super.change(epic);
        subTasks = epic.getSubTasks();
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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", subTasks=" + subTasks +
                '}';
    }
}
