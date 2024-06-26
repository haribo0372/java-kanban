package models;

public class SubTask extends Task {
    private Epic currentEpic;

    public SubTask(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
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
                ", currentEpicId=" + currentEpic.getId() +
                '}';
    }
}
