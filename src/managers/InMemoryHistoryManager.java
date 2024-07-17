package managers;

import models.Task;
import my.util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> hashMap = new HashMap<>();

    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) return;

        Node<Task> current = hashMap.get(task.getId());
        if (current != null) removeNode(current);

        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        Node<Task> temp = tail;

        while (temp != null) {
            list.add(temp.value);
            temp = temp.prev;
        }
        return list;
    }

    @Override
    public void remove(int id) {
        Node<Task> taskNode = hashMap.get(id);
        if (taskNode == null) return;

        removeNode(taskNode);
        hashMap.remove(id);
    }

    public void linkLast(Task task) {
        if (task == null) return;

        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        hashMap.put(task.getId(), newNode);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            taskList.add(current.value);
            current = current.next;
        }
        return taskList;
    }

    private void removeNode(Node<Task> node) {
        if (tail.value.equals(node.value)) {
            tail = tail.prev;
            if (tail != null) tail.next = null;
            return;
        }

        if (head.value.equals(node.value)) {
            head = head.next;
            if (head != null) head.prev = null;
            return;
        }

        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        prev.next = next;
        next.prev = prev;
    }
}
