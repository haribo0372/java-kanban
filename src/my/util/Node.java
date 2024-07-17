package my.util;

public class Node<T> {
    public Node<T> prev;
    public T value;
    public Node<T> next;

    public Node(T value) {
        this.value = value;
    }
}
