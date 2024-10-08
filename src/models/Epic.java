package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final List<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public void addNewSubTask(SubTask... subTasks) {
        Arrays.stream(subTasks).forEach(subTask -> {
            if (subTask == null) return;
            this.subTasks.add(subTask);
            subTask.setCurrentEpic(this);
            updateStatus();
            if (startTime == null || duration == null) {
                startTime = subTask.getStartTime();
                duration = subTask.getDuration();
                if (startTime != null && duration != null)
                    endTime = startTime.plus(duration);
            }
            updateTime();
        });
    }

    public List<SubTask> getSubTasks() {
        return List.copyOf(subTasks);
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

    public void updateTime() {
        if (subTasks.isEmpty()) {
            duration = null;
            startTime = null;
            endTime = null;
            return;
        }

        if (subTasks.stream().anyMatch(i -> (i.getStartTime() != null && i.getDuration() != null))) {
            duration = Duration.ZERO;
            startTime = LocalDateTime.MAX;
            endTime = LocalDateTime.MIN;
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
        } else {
            duration = null;
            startTime = null;
            endTime = null;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        if (endTime != null) return LocalDateTime.from(endTime);
        return null;
    }

    @Override
    public String toString() {
        int[] arrayWithSubTasksId = subTasks.stream().filter(i -> i.getId() != null).mapToInt(Task::getId).toArray();
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", subTasksId=" + Arrays.toString(arrayWithSubTasksId) +
                '}';
    }

    @Override
    public String toStringCSV() {
        return String.format("%s,EPIC,%s,%s,%s,%s,%s", id, name, taskStatus, description, duration, startTime);
    }
}
