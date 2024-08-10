package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;
    private LocalDateTime endTime = LocalDateTime.MIN;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ofNanos(0), LocalDateTime.MAX);
        subTasks = new ArrayList<>();
    }

    public void addNewSubTask(SubTask... subTasks) {
        Arrays.stream(subTasks).forEach(subTask -> {
            if (subTask == null) return;
            this.subTasks.add(subTask);
            subTask.setCurrentEpic(this);
            updateStatus();
            updateTime();
        });
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.forEach(subTask1 -> {
            if (subTask1.equals(subTask)) {
                subTask1.setName(subTask.getName());
                subTask1.setDescription(subTask.getDescription());
                subTask1.setTaskStatus(subTask.getTaskStatus());
            }
        });
        updateStatus();
        updateTime();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        updateStatus();
        updateTime();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        updateStatus();
        updateTime();
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

    private void updateTime() {
        duration = Duration.ZERO;

        subTasks.stream().filter(i -> (i.getStartTime() != null && i.getDuration() != null)).forEach(subtask -> {
            LocalDateTime currentStartTime = subtask.getStartTime();
            LocalDateTime currentEndTime = subtask.getEndTime();
            Duration currentDuration = subtask.getDuration();

            if (startTime.isAfter(currentStartTime))
                startTime = LocalDateTime.from(currentStartTime);
            if (endTime.isBefore(currentEndTime))
                endTime = LocalDateTime.from(currentEndTime);
            duration = duration.plus(currentDuration);
        });
    }

    @Override
    public LocalDateTime getEndTime() {
        return LocalDateTime.from(endTime);
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

    @Override
    public String toStringCSV() {
        return String.format("%s,EPIC,%s,%s,%s", id, name, taskStatus, description);
    }
}
