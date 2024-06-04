package models;

public class Task extends AbstractTask{


    public Task(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
