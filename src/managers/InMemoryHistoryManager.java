package managers;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    @Override
    public boolean add(Task task) {
        history.addFirst(task);
        if (history.size() > 10) history.removeLast();
        return true;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
