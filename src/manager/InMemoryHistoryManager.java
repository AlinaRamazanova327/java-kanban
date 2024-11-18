package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    private void linkLast(Node node) {
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    private void removeNode(Node node) {
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.next;
        } else if (node == tail) {
            tail = node.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class Node {

        Task task;
        Node next;
        Node prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }

    @Override
    public void add(Task task) {
        if (!history.containsKey(task.getId())) {
            Node node = new Node(task);
            linkLast(node);
            history.put(task.getId(), node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<Integer, Node> historyTask : history.entrySet()) {
            tasks.add(historyTask.getValue().task);
        }
        return tasks;
    }
}
