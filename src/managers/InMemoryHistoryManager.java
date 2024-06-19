package managers;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;

        history.addFirst(task);
        if (history.size() > 10) history.removeLast();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
