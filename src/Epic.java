import java.util.ArrayList;
import java.util.Iterator;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public boolean addNewSubTask(SubTask subTask) {
        subTask.setCurrentEpic(this);
        return subTasks.add(subTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
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
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", subTasks=" + subTasks +
                '}';
    }
}
