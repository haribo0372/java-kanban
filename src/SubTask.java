public class SubTask extends Task{
    private Epic currentEpic;

    public SubTask(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
    }

    public void setCurrentEpic(Epic currentEpic) {
        this.currentEpic = currentEpic;
    }

    @Override
    public void changeStatus(TaskStatus taskStatus){
        this.taskStatus = taskStatus;
        currentEpic.updateStatus();
    }
}
