package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> history = new LinkedList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() < MAX_SIZE) {
            history.addLast(task);
        } else {
            history.removeFirst();
            history.addLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
