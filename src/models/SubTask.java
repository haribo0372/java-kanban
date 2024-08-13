package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Epic currentEpic;

    public SubTask(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
    }

    public SubTask(String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
        super(name, description, taskStatus, duration, startTime);
    }

    public Epic getCurrentEpic() {
        return currentEpic;
    }

    public void setCurrentEpic(Epic currentEpic) {
        this.currentEpic = currentEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public String toStringCSV() {
        String result = String.format("%s,SUBTASK,%s,%s,%s,%s,%s", id, name, taskStatus, description, duration, startTime);
        if (currentEpic == null)
            return result + ",null";

        return String.format("%s,%s", result, currentEpic.getId());
    }
}
