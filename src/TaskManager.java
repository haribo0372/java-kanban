import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TaskManager {
    private static int serial = 1;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();

    public HashMap<Integer, Task> getTaskHashMap() {
        return taskHashMap;
    }

    public void removeAllTasks() {
        taskHashMap.clear();
    }

    public Task getTask(int taskID) {
        return taskHashMap.get(taskID);
    }

    public void addTask(Task... task) {
        Arrays.stream(task).forEach(i -> taskHashMap.put(serial++, i));
    }

    public void updateTask(int taskID, Task task) {
        taskHashMap.put(taskID, task);
    }

    public void removeTask(int taskId) {
        taskHashMap.remove(taskId);
    }

    public ArrayList<SubTask> getSubTasksOfEpic(int epicId) {
        Task task = taskHashMap.get(epicId);
        if (task == null) return null;

        Epic epic = (Epic) task;
        return epic.getSubTasks();
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "taskHashMap=" + taskHashMap +
                '}';
    }
}
